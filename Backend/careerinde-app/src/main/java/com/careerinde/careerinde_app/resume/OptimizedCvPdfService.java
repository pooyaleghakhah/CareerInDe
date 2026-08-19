package com.careerinde.careerinde_app.resume;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.common.PDRectangle;
import org.apache.pdfbox.pdmodel.font.PDFont;
import org.apache.pdfbox.pdmodel.font.Standard14Fonts;
import org.apache.pdfbox.pdmodel.font.PDType1Font;
import org.springframework.stereotype.Service;

@Service
public class OptimizedCvPdfService {

    private static final float MARGIN = 50;
    private static final float FONT_SIZE = 10.5f;
    private static final float LINE_HEIGHT = 15f;

    private static final float TITLE_FONT_SIZE = 18f;
    private static final float SECTION_FONT_SIZE = 13f;

    private static final float MAX_WIDTH =
            PDRectangle.A4.getWidth() - (MARGIN * 2);


    /**
     * Generates a professional PDF from the
     * AI-optimized CV text.
     */
    public byte[] generatePdf(String optimizedCv)
            throws IOException {

        if (optimizedCv == null ||
                optimizedCv.isBlank()) {

            throw new IllegalArgumentException(
                    "Optimized CV cannot be empty."
            );
        }


        try (PDDocument document = new PDDocument();
             ByteArrayOutputStream output =
                     new ByteArrayOutputStream()) {


            PDFont normalFont =
                    new PDType1Font(
                            Standard14Fonts.FontName.HELVETICA
                    );


            PDFont boldFont =
                    new PDType1Font(
                            Standard14Fonts.FontName.HELVETICA_BOLD
                    );


            PdfWriter writer =
                    new PdfWriter(
                            document,
                            normalFont,
                            boldFont
                    );


            writer.writeCv(optimizedCv);


            document.save(output);


            return output.toByteArray();
        }
    }


    /**
     * Internal helper responsible for
     * writing text and creating new pages.
     */
    private static class PdfWriter {

        private final PDDocument document;
        private final PDFont normalFont;
        private final PDFont boldFont;

        private PDPage page;
        private PDPageContentStream stream;

        private float y;


        PdfWriter(
                PDDocument document,
                PDFont normalFont,
                PDFont boldFont)
                throws IOException {

            this.document = document;
            this.normalFont = normalFont;
            this.boldFont = boldFont;

            createNewPage();
        }


        /**
         * Processes the optimized CV line by line.
         */
        void writeCv(String cv)
                throws IOException {

            String normalized =
                    normalizeText(cv);


            String[] lines =
                    normalized.split("\\R");


            boolean firstContentWritten = false;


            for (String originalLine : lines) {

                String line =
                        originalLine.trim();


                if (line.isBlank()) {

                    y -= 7;
                    continue;
                }


                // Remove Markdown bold markers
                line = line.replace("**", "");


                /*
                 * AI section headings:
                 *
                 * PROFESSIONAL SUMMARY
                 * CORE SKILLS
                 * PROFESSIONAL EXPERIENCE
                 * EDUCATION
                 * PROJECTS
                 * TECHNICAL SKILLS
                 * LANGUAGES
                 * CERTIFICATIONS
                 */

                if (isSectionHeading(line)) {

                    if (firstContentWritten) {
                        y -= 8;
                    }


                    writeWrappedText(
                            line,
                            boldFont,
                            SECTION_FONT_SIZE,
                            18
                    );


                    y -= 4;

                    firstContentWritten = true;

                    continue;
                }


                // Bullet point
                if (line.startsWith("- ")) {

                    String bulletText =
                            line.substring(2).trim();


                    writeBullet(bulletText);

                    firstContentWritten = true;

                    continue;
                }


                // Normal content
                writeWrappedText(
                        line,
                        normalFont,
                        FONT_SIZE,
                        LINE_HEIGHT
                );


                firstContentWritten = true;
            }


            closeStream();
        }


        /**
         * Detect main CV section headings.
         */
        private boolean isSectionHeading(
                String text) {

            String value =
                    text.trim().toUpperCase();


            return value.equals("PROFESSIONAL SUMMARY")
                    || value.equals("CORE SKILLS")
                    || value.equals("PROFESSIONAL EXPERIENCE")
                    || value.equals("WORK EXPERIENCE")
                    || value.equals("EDUCATION")
                    || value.equals("PROJECTS")
                    || value.equals("TECHNICAL SKILLS")
                    || value.equals("SKILLS")
                    || value.equals("LANGUAGES")
                    || value.equals("CERTIFICATIONS")
                    || value.equals("CERTIFICATES");
        }


        /**
         * Writes one bullet point.
         */
        private void writeBullet(
                String text)
                throws IOException {

            ensureSpace(LINE_HEIGHT);


            float bulletIndent = 12;
            float textIndent = 22;


            stream.beginText();

            stream.setFont(
                    normalFont,
                    FONT_SIZE
            );

            stream.newLineAtOffset(
                    MARGIN + bulletIndent,
                    y
            );

            stream.showText("•");

            stream.endText();


            writeWrappedTextAtX(
                    text,
                    normalFont,
                    FONT_SIZE,
                    LINE_HEIGHT,
                    MARGIN + textIndent,
                    MAX_WIDTH - textIndent
            );
        }


        /**
         * Write wrapped text using
         * the standard left margin.
         */
        private void writeWrappedText(
                String text,
                PDFont font,
                float fontSize,
                float lineHeight)
                throws IOException {

            writeWrappedTextAtX(
                    text,
                    font,
                    fontSize,
                    lineHeight,
                    MARGIN,
                    MAX_WIDTH
            );
        }


        /**
         * Word wrapping implementation.
         */
        private void writeWrappedTextAtX(
                String text,
                PDFont font,
                float fontSize,
                float lineHeight,
                float x,
                float maxWidth)
                throws IOException {


            if (text == null ||
                    text.isBlank()) {

                return;
            }


            String[] words =
                    text.split("\\s+");


            StringBuilder currentLine =
                    new StringBuilder();


            for (String word : words) {

                String candidate;


                if (currentLine.length() == 0) {

                    candidate = word;

                } else {

                    candidate =
                            currentLine
                                    + " "
                                    + word;
                }


                float width =
                        getTextWidth(
                                candidate,
                                font,
                                fontSize
                        );


                if (width <= maxWidth) {

                    currentLine =
                            new StringBuilder(
                                    candidate
                            );

                } else {

                    if (currentLine.length() > 0) {

                        writeSingleLine(
                                currentLine.toString(),
                                font,
                                fontSize,
                                lineHeight,
                                x
                        );
                    }


                    currentLine =
                            new StringBuilder(word);
                }
            }


            if (currentLine.length() > 0) {

                writeSingleLine(
                        currentLine.toString(),
                        font,
                        fontSize,
                        lineHeight,
                        x
                );
            }
        }


        /**
         * Write one physical line.
         */
        private void writeSingleLine(
                String text,
                PDFont font,
                float fontSize,
                float lineHeight,
                float x)
                throws IOException {


            ensureSpace(lineHeight);


            String safeText =
                    sanitizeForPdf(text);


            stream.beginText();

            stream.setFont(
                    font,
                    fontSize
            );

            stream.newLineAtOffset(
                    x,
                    y
            );

            stream.showText(
                    safeText
            );

            stream.endText();


            y -= lineHeight;
        }


        /**
         * Calculate text width.
         */
        private float getTextWidth(
                String text,
                PDFont font,
                float fontSize)
                throws IOException {

            String safe =
                    sanitizeForPdf(text);


            return font.getStringWidth(safe)
                    / 1000f
                    * fontSize;
        }


        /**
         * Create a new A4 page.
         */
        private void createNewPage()
                throws IOException {

            closeStream();


            page =
                    new PDPage(
                            PDRectangle.A4
                    );


            document.addPage(page);


            stream =
                    new PDPageContentStream(
                            document,
                            page
                    );


            y =
                    PDRectangle.A4.getHeight()
                            - MARGIN;
        }


        /**
         * Create another page when
         * the current page is full.
         */
        private void ensureSpace(
                float requiredHeight)
                throws IOException {

            if (y - requiredHeight
                    < MARGIN) {

                createNewPage();
            }
        }


        /**
         * Close current PDF content stream.
         */
        private void closeStream()
                throws IOException {

            if (stream != null) {

                stream.close();

                stream = null;
            }
        }


        /**
         * Normalize characters commonly
         * returned by AI.
         */
        private String normalizeText(
                String text) {

            return text
                    .replace("\u2011", "-")
                    .replace("\u2012", "-")
                    .replace("\u2013", "-")
                    .replace("\u2014", "-")
                    .replace("\u2212", "-")
                    .replace("\u2018", "'")
                    .replace("\u2019", "'")
                    .replace("\u201C", "\"")
                    .replace("\u201D", "\"")
                    .replace("\u00A0", " ");
        }


        /**
         * Helvetica does not support every
         * Unicode character.
         *
         * Replace unsupported characters
         * to avoid PDF generation errors.
         */
        private String sanitizeForPdf(
                String text) {

            if (text == null) {
                return "";
            }


            return text
                    .replace("✓", "+")
                    .replace("✦", "*")
                    .replace("•", "-")
                    .replace("\u202F", " ")
                    .replace("\u00A0", " ");
        }
    }
}