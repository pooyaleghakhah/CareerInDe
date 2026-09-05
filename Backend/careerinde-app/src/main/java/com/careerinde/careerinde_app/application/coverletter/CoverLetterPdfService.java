package com.careerinde.careerinde_app.application.coverletter;

import com.lowagie.text.Document;
import com.lowagie.text.Element;
import com.lowagie.text.Font;
import com.lowagie.text.FontFactory;
import com.lowagie.text.PageSize;
import com.lowagie.text.Paragraph;
import com.lowagie.text.pdf.PdfWriter;

import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;


@Service
public class CoverLetterPdfService {


    public byte[] generatePdf(CoverLetter coverLetter) {

        if (coverLetter == null) {
            throw new IllegalArgumentException(
                    "Cover letter cannot be null."
            );
        }


        ByteArrayOutputStream outputStream =
                new ByteArrayOutputStream();


        Document document =
                new Document(
                        PageSize.A4,
                        56,
                        56,
                        55,
                        55
                );


        try {

            PdfWriter.getInstance(
                    document,
                    outputStream
            );


            document.open();


            // =================================================
            // FONTS
            // =================================================

            Font nameFont =
                    FontFactory.getFont(
                            FontFactory.HELVETICA_BOLD,
                            15
                    );


            Font metaFont =
                    FontFactory.getFont(
                            FontFactory.HELVETICA,
                            9
                    );


            Font subjectFont =
                    FontFactory.getFont(
                            FontFactory.HELVETICA_BOLD,
                            11
                    );


            Font bodyFont =
                    FontFactory.getFont(
                            FontFactory.HELVETICA,
                            10.5f
                    );


            Font closingFont =
                    FontFactory.getFont(
                            FontFactory.HELVETICA,
                            10.5f
                    );


            Font candidateFont =
                    FontFactory.getFont(
                            FontFactory.HELVETICA_BOLD,
                            10.5f
                    );


            // =================================================
            // CANDIDATE NAME
            // =================================================

            if (hasText(coverLetter.getCandidateName())) {

                Paragraph candidateName =
                        new Paragraph(
                                coverLetter.getCandidateName(),
                                nameFont
                        );


                candidateName.setSpacingAfter(6);

                document.add(candidateName);

            }


            // =================================================
            // TARGET POSITION / COMPANY
            // =================================================

            if (
                    hasText(coverLetter.getJobTitle()) ||
                    hasText(coverLetter.getCompanyName())
            ) {

                StringBuilder meta =
                        new StringBuilder();


                if (hasText(coverLetter.getJobTitle())) {

                    meta.append(
                            coverLetter.getJobTitle()
                    );

                }


                if (
                        hasText(coverLetter.getJobTitle()) &&
                        hasText(coverLetter.getCompanyName())
                ) {

                    meta.append(" | ");

                }


                if (hasText(coverLetter.getCompanyName())) {

                    meta.append(
                            coverLetter.getCompanyName()
                    );

                }


                Paragraph jobInfo =
                        new Paragraph(
                                meta.toString(),
                                metaFont
                        );


                jobInfo.setSpacingAfter(30);

                document.add(jobInfo);

            } else {

                addVerticalSpace(
                        document,
                        20
                );

            }


            // =================================================
            // SUBJECT
            // =================================================

            if (hasText(coverLetter.getSubject())) {

                Paragraph subject =
                        new Paragraph(
                                coverLetter.getSubject(),
                                subjectFont
                        );


                subject.setSpacingAfter(20);

                document.add(subject);

            }


            // =================================================
            // GREETING
            // =================================================

            if (hasText(coverLetter.getGreeting())) {

                Paragraph greeting =
                        new Paragraph(
                                coverLetter.getGreeting(),
                                bodyFont
                        );


                greeting.setSpacingAfter(15);

                document.add(greeting);

            }


            // =================================================
            // BODY
            // =================================================

            if (hasText(coverLetter.getBody())) {

                String normalizedBody =
                        coverLetter
                                .getBody()
                                .replace("\r\n", "\n")
                                .replace("\r", "\n");


                String[] paragraphs =
                        normalizedBody.split("\\n\\s*\\n");


                for (String text : paragraphs) {

                    if (
                            text == null ||
                            text.isBlank()
                    ) {
                        continue;
                    }


                    Paragraph bodyParagraph =
                            new Paragraph(
                                    text.trim(),
                                    bodyFont
                            );


                    bodyParagraph.setAlignment(
                            Element.ALIGN_LEFT
                    );


                    bodyParagraph.setLeading(
                            15f
                    );


                    bodyParagraph.setSpacingAfter(
                            11
                    );


                    document.add(
                            bodyParagraph
                    );

                }

            }


            // =================================================
            // CLOSING
            // =================================================

            if (hasText(coverLetter.getClosing())) {

                Paragraph closing =
                        new Paragraph(
                                coverLetter.getClosing(),
                                closingFont
                        );


                closing.setSpacingBefore(14);

                closing.setSpacingAfter(8);

                document.add(closing);

            }


            // =================================================
            // SIGNATURE NAME
            // =================================================

            if (hasText(coverLetter.getCandidateName())) {

                Paragraph signature =
                        new Paragraph(
                                coverLetter.getCandidateName(),
                                candidateFont
                        );


                document.add(signature);

            }


            document.close();


            return outputStream.toByteArray();


        } catch (Exception e) {

            if (document.isOpen()) {
                document.close();
            }


            throw new RuntimeException(
                    "Could not generate cover letter PDF.",
                    e
            );

        }

    }


    // =========================================================
    // HELPER
    // =========================================================

    private boolean hasText(String value) {

        return value != null &&
                !value.isBlank();

    }


    private void addVerticalSpace(
            Document document,
            float spacing
    ) throws Exception {

        Paragraph spacer =
                new Paragraph(" ");


        spacer.setSpacingAfter(spacing);

        document.add(spacer);

    }

}