package com.careerinde.careerinde_app.resume.export;

import java.awt.Color;
import java.io.ByteArrayOutputStream;
import java.util.List;

import org.springframework.stereotype.Service;

import com.careerinde.careerinde_app.resume.optimization.OptimizedEducation;
import com.careerinde.careerinde_app.resume.optimization.OptimizedExperience;
import com.careerinde.careerinde_app.resume.optimization.OptimizedProject;
import com.careerinde.careerinde_app.resume.optimization.OptimizedResume;

import com.lowagie.text.Chunk;
import com.lowagie.text.Document;
import com.lowagie.text.DocumentException;
import com.lowagie.text.Font;
import com.lowagie.text.FontFactory;
import com.lowagie.text.ListItem;
import com.lowagie.text.PageSize;
import com.lowagie.text.Paragraph;
import com.lowagie.text.pdf.PdfWriter;

@Service
public class ResumePdfService {

    private static final Color DARK =
            new Color(15, 23, 42);

    private static final Color TEXT =
            new Color(51, 65, 85);

    private static final Color MUTED =
            new Color(100, 116, 139);

    private static final Color BLUE =
            new Color(37, 99, 235);


    // =========================================================
    // GENERATE PDF
    // =========================================================

    public byte[] generatePdf(
            OptimizedResume resume) {

        if (resume == null) {

            throw new IllegalArgumentException(
                    "Resume cannot be null."
            );
        }


        try {

            ByteArrayOutputStream output =
                    new ByteArrayOutputStream();


            Document document =
                    new Document(
                            PageSize.A4,
                            45,
                            45,
                            40,
                            40
                    );


            PdfWriter.getInstance(
                    document,
                    output
            );


            document.open();


            addHeader(
                    document,
                    resume
            );


            addProfessionalSummary(
                    document,
                    resume
            );


            addSkills(
                    document,
                    resume
            );


            addExperience(
                    document,
                    resume
            );


            addProjects(
                    document,
                    resume
            );


            addEducation(
                    document,
                    resume
            );


            addCertifications(
                    document,
                    resume
            );


            addLanguages(
                    document,
                    resume
            );


            document.close();


            return output.toByteArray();


        } catch (Exception exception) {

            throw new RuntimeException(
                    "Could not generate resume PDF.",
                    exception
            );
        }
    }


    // =========================================================
    // HEADER
    // =========================================================

    private void addHeader(
            Document document,
            OptimizedResume resume)
            throws DocumentException {


        Font nameFont =
                FontFactory.getFont(
                        FontFactory.HELVETICA_BOLD,
                        20,
                        DARK
                );


        Paragraph name =
                new Paragraph(
                        safe(
                                resume.getFullName(),
                                "Candidate"
                        ),
                        nameFont
                );


        name.setSpacingAfter(3);


        document.add(name);


        if (hasText(
                resume.getTargetRole()
        )) {

            Font roleFont =
                    FontFactory.getFont(
                            FontFactory.HELVETICA_BOLD,
                            11,
                            BLUE
                    );


            Paragraph role =
                    new Paragraph(
                            resume.getTargetRole(),
                            roleFont
                    );


            role.setSpacingAfter(7);


            document.add(role);
        }


        String contactLine =
                buildContactLine(
                        resume
                );


        if (!contactLine.isBlank()) {

            Font contactFont =
                    FontFactory.getFont(
                            FontFactory.HELVETICA,
                            8.5f,
                            MUTED
                    );


            Paragraph contact =
                    new Paragraph(
                            contactLine,
                            contactFont
                    );


            contact.setLeading(12);
            contact.setSpacingAfter(10);


            document.add(contact);
        }


        Font dividerFont =
                FontFactory.getFont(
                        FontFactory.HELVETICA,
                        6,
                        DARK
                );


        Paragraph divider =
                new Paragraph(
                        "________________________________________________________________________________",
                        dividerFont
                );


        divider.setSpacingAfter(10);


        document.add(divider);
    }


    // =========================================================
    // PROFESSIONAL SUMMARY
    // =========================================================

    private void addProfessionalSummary(
            Document document,
            OptimizedResume resume)
            throws DocumentException {


        if (!hasText(
                resume.getProfessionalSummary()
        )) {

            return;
        }


        addSectionTitle(
                document,
                "PROFESSIONAL SUMMARY"
        );


        Paragraph summary =
                new Paragraph(
                        resume.getProfessionalSummary(),
                        bodyFont()
                );


        summary.setLeading(13.5f);
        summary.setSpacingAfter(11);


        document.add(summary);
    }


    // =========================================================
    // SKILLS
    // =========================================================

    private void addSkills(
            Document document,
            OptimizedResume resume)
            throws DocumentException {


        if (resume.getSkills() == null ||
                resume.getSkills().isEmpty()) {

            return;
        }


        addSectionTitle(
                document,
                "CORE SKILLS"
        );


        String skillsText =
                String.join(
                        "  |  ",
                        resume.getSkills()
                );


        Paragraph skills =
                new Paragraph(
                        skillsText,
                        bodyFont()
                );


        skills.setLeading(13);
        skills.setSpacingAfter(11);


        document.add(skills);
    }


    // =========================================================
    // EXPERIENCE
    // =========================================================

    private void addExperience(
            Document document,
            OptimizedResume resume)
            throws DocumentException {


        List<OptimizedExperience> experiences =
                resume.getExperiences();


        if (experiences == null ||
                experiences.isEmpty()) {

            return;
        }


        addSectionTitle(
                document,
                "PROFESSIONAL EXPERIENCE"
        );


        for (OptimizedExperience experience
                : experiences) {


            if (experience == null) {
                continue;
            }


            String heading =
                    safe(
                            experience.getJobTitle(),
                            ""
                    );


            if (hasText(
                    experience.getCompany()
            )) {

                if (!heading.isBlank()) {
                    heading += " | ";
                }

                heading +=
                        experience.getCompany();
            }


            Font headingFont =
                    FontFactory.getFont(
                            FontFactory.HELVETICA_BOLD,
                            10,
                            DARK
                    );


            Paragraph position =
                    new Paragraph(
                            heading,
                            headingFont
                    );


            position.setSpacingBefore(2);
            position.setSpacingAfter(2);


            document.add(position);


            String meta =
                    buildExperienceMeta(
                            experience
                    );


            if (!meta.isBlank()) {

                Font metaFont =
                        FontFactory.getFont(
                                FontFactory.HELVETICA,
                                8.5f,
                                MUTED
                        );


                Paragraph metaParagraph =
                        new Paragraph(
                                meta,
                                metaFont
                        );


                metaParagraph.setSpacingAfter(3);


                document.add(
                        metaParagraph
                );
            }


            addBulletPoints(
                    document,
                    experience.getBulletPoints()
            );


            addSmallSpacing(
                    document
            );
        }
    }


    // =========================================================
    // PROJECTS
    // =========================================================

    private void addProjects(
            Document document,
            OptimizedResume resume)
            throws DocumentException {


        List<OptimizedProject> projects =
                resume.getProjects();


        if (projects == null ||
                projects.isEmpty()) {

            return;
        }


        addSectionTitle(
                document,
                "PROJECTS"
        );


        for (OptimizedProject project
                : projects) {


            if (project == null) {
                continue;
            }


            if (hasText(
                    project.getTitle()
            )) {

                Font titleFont =
                        FontFactory.getFont(
                                FontFactory.HELVETICA_BOLD,
                                10,
                                DARK
                        );


                Paragraph title =
                        new Paragraph(
                                project.getTitle(),
                                titleFont
                        );


                title.setSpacingAfter(3);


                document.add(title);
            }


            if (hasText(
                    project.getDescription()
            )) {

                Paragraph description =
                        new Paragraph(
                                project.getDescription(),
                                bodyFont()
                        );


                description.setLeading(13);
                description.setSpacingAfter(3);


                document.add(
                        description
                );
            }


            if (project.getTechnologies() != null &&
                    !project.getTechnologies().isEmpty()) {


                Font techFont =
                        FontFactory.getFont(
                                FontFactory.HELVETICA_BOLD,
                                8.5f,
                                BLUE
                        );


                Paragraph technologies =
                        new Paragraph(
                                "Technologies: "
                                        +
                                String.join(
                                        ", ",
                                        project.getTechnologies()
                                ),
                                techFont
                        );


                technologies.setSpacingAfter(3);


                document.add(
                        technologies
                );
            }


            addBulletPoints(
                    document,
                    project.getBulletPoints()
            );


            addSmallSpacing(
                    document
            );
        }
    }


    // =========================================================
    // EDUCATION
    // =========================================================

    private void addEducation(
            Document document,
            OptimizedResume resume)
            throws DocumentException {


        List<OptimizedEducation> education =
                resume.getEducation();


        if (education == null ||
                education.isEmpty()) {

            return;
        }


        addSectionTitle(
                document,
                "EDUCATION"
        );


        for (OptimizedEducation item
                : education) {


            if (item == null) {
                continue;
            }


            String degree =
                    buildEducationDegree(
                            item
                    );


            Font degreeFont =
                    FontFactory.getFont(
                            FontFactory.HELVETICA_BOLD,
                            10,
                            DARK
                    );


            Paragraph degreeParagraph =
                    new Paragraph(
                            degree,
                            degreeFont
                    );


            degreeParagraph.setSpacingAfter(2);


            document.add(
                    degreeParagraph
            );


            String institution =
                    buildEducationMeta(
                            item
                    );


            if (!institution.isBlank()) {

                Font metaFont =
                        FontFactory.getFont(
                                FontFactory.HELVETICA,
                                8.5f,
                                MUTED
                        );


                Paragraph institutionParagraph =
                        new Paragraph(
                                institution,
                                metaFont
                        );


                institutionParagraph.setSpacingAfter(2);


                document.add(
                        institutionParagraph
                );
            }


            if (hasText(
                    item.getGrade()
            )) {

                Paragraph grade =
                        new Paragraph(
                                "Grade: "
                                        +
                                item.getGrade(),
                                bodyFont()
                        );


                grade.setSpacingAfter(4);


                document.add(
                        grade
                );
            }


            addSmallSpacing(
                    document
            );
        }
    }


    // =========================================================
    // CERTIFICATIONS
    // =========================================================

    private void addCertifications(
            Document document,
            OptimizedResume resume)
            throws DocumentException {


        if (resume.getCertifications() == null ||
                resume.getCertifications().isEmpty()) {

            return;
        }


        addSectionTitle(
                document,
                "CERTIFICATIONS"
        );


        addBulletPoints(
                document,
                resume.getCertifications()
        );


        addSmallSpacing(
                document
        );
    }


    // =========================================================
    // LANGUAGES
    // =========================================================

    private void addLanguages(
            Document document,
            OptimizedResume resume)
            throws DocumentException {


        if (resume.getLanguages() == null ||
                resume.getLanguages().isEmpty()) {

            return;
        }


        addSectionTitle(
                document,
                "LANGUAGES"
        );


        String languages =
                String.join(
                        "  |  ",
                        resume.getLanguages()
                );


        Paragraph paragraph =
                new Paragraph(
                        languages,
                        bodyFont()
                );


        paragraph.setLeading(13);


        document.add(
                paragraph
        );
    }


    // =========================================================
    // SECTION TITLE
    // =========================================================

    private void addSectionTitle(
            Document document,
            String title)
            throws DocumentException {


        Font font =
                FontFactory.getFont(
                        FontFactory.HELVETICA_BOLD,
                        10,
                        DARK
                );


        Paragraph paragraph =
                new Paragraph(
                        title,
                        font
                );


        paragraph.setSpacingBefore(4);
        paragraph.setSpacingAfter(6);


        document.add(
                paragraph
        );
    }


    // =========================================================
    // BULLETS
    // =========================================================

    private void addBulletPoints(
            Document document,
            List<String> bulletPoints)
            throws DocumentException {


        if (bulletPoints == null ||
                bulletPoints.isEmpty()) {

            return;
        }


        com.lowagie.text.List list =
                new com.lowagie.text.List(
                        false,
                        10
                );


        /*
         * OpenPDF expects Chunk here,
         * not Phrase.
         */
        list.setListSymbol(
                new Chunk(
                        "• ",
                        bodyFont()
                )
        );


        for (String bullet : bulletPoints) {


            if (!hasText(bullet)) {
                continue;
            }


            ListItem item =
                    new ListItem(
                            bullet.trim(),
                            bodyFont()
                    );


            item.setLeading(13);


            list.add(item);
        }


        document.add(list);
    }


    // =========================================================
    // CONTACT LINE
    // =========================================================

    private String buildContactLine(
            OptimizedResume resume) {


        StringBuilder builder =
                new StringBuilder();


        appendInline(
                builder,
                resume.getLocation()
        );


        appendInline(
                builder,
                resume.getPhone()
        );


        appendInline(
                builder,
                resume.getEmail()
        );


        appendInline(
                builder,
                resume.getLinkedin()
        );


        appendInline(
                builder,
                resume.getGithub()
        );


        return builder.toString();
    }


    // =========================================================
    // EXPERIENCE META
    // =========================================================

    private String buildExperienceMeta(
            OptimizedExperience experience) {


        StringBuilder builder =
                new StringBuilder();


        appendInline(
                builder,
                experience.getLocation()
        );


        String dateRange =
                buildDateRange(
                        experience.getStartDate(),
                        experience.getEndDate()
                );


        appendInline(
                builder,
                dateRange
        );


        return builder.toString();
    }


    // =========================================================
    // EDUCATION
    // =========================================================

    private String buildEducationDegree(
            OptimizedEducation education) {


        String degree =
                safe(
                        education.getDegree(),
                        ""
                );


        String field =
                safe(
                        education.getFieldOfStudy(),
                        ""
                );


        if (!degree.isBlank() &&
                !field.isBlank()) {

            return degree
                    + " - "
                    + field;
        }


        return !degree.isBlank()
                ? degree
                : field;
    }


    private String buildEducationMeta(
            OptimizedEducation education) {


        StringBuilder builder =
                new StringBuilder();


        appendInline(
                builder,
                education.getInstitution()
        );


        appendInline(
                builder,
                education.getLocation()
        );


        appendInline(
                builder,
                buildDateRange(
                        education.getStartDate(),
                        education.getEndDate()
                )
        );


        return builder.toString();
    }


    // =========================================================
    // DATE
    // =========================================================

    private String buildDateRange(
            String startDate,
            String endDate) {


        boolean hasStart =
                hasText(startDate);


        boolean hasEnd =
                hasText(endDate);


        if (hasStart && hasEnd) {

            return startDate.trim()
                    + " - "
                    + endDate.trim();
        }


        if (hasStart) {
            return startDate.trim();
        }


        if (hasEnd) {
            return endDate.trim();
        }


        return "";
    }


    // =========================================================
    // INLINE VALUES
    // =========================================================

    private void appendInline(
            StringBuilder builder,
            String value) {


        if (!hasText(value)) {
            return;
        }


        if (!builder.isEmpty()) {

            builder.append(
                    "  |  "
            );
        }


        builder.append(
                value.trim()
        );
    }


    // =========================================================
    // SPACING
    // =========================================================

    private void addSmallSpacing(
            Document document)
            throws DocumentException {


        Paragraph spacing =
                new Paragraph(" ");


        spacing.setLeading(4);


        document.add(
                spacing
        );
    }


    // =========================================================
    // FONT
    // =========================================================

    private Font bodyFont() {

        return FontFactory.getFont(
                FontFactory.HELVETICA,
                9,
                TEXT
        );
    }


    // =========================================================
    // HELPERS
    // =========================================================

    private boolean hasText(
            String value) {

        return value != null &&
                !value.isBlank();
    }


    private String safe(
            String value,
            String fallback) {

        if (value == null ||
                value.isBlank()) {

            return fallback;
        }


        return value.trim();
    }
}