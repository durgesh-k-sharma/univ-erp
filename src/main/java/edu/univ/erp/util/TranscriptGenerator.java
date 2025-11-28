package edu.univ.erp.util;

import com.lowagie.text.*;
import com.lowagie.text.Font;
import com.lowagie.text.pdf.PdfPCell;
import com.lowagie.text.pdf.PdfPTable;
import com.lowagie.text.pdf.PdfWriter;
import edu.univ.erp.domain.Grade;
import edu.univ.erp.domain.Student;

import java.awt.*;
import java.io.FileOutputStream;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Utility to generate PDF transcripts
 */
public class TranscriptGenerator {

    public static void generateTranscript(Student student, List<Grade> grades, String filePath) throws Exception {
        Document document = new Document();
        PdfWriter.getInstance(document, new FileOutputStream(filePath));

        document.open();

        // Fonts
        Font titleFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 18, Color.BLACK);
        Font headerFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 12, Color.BLACK);
        Font normalFont = FontFactory.getFont(FontFactory.HELVETICA, 10, Color.BLACK);

        // Header
        Paragraph title = new Paragraph("University ERP - Official Transcript", titleFont);
        title.setAlignment(Element.ALIGN_CENTER);
        document.add(title);
        document.add(Chunk.NEWLINE);

        // Student Info
        PdfPTable infoTable = new PdfPTable(2);
        infoTable.setWidthPercentage(100);
        infoTable.setSpacingAfter(20);

        addInfoRow(infoTable, "Student Name:", student.getFullName(), headerFont, normalFont);
        addInfoRow(infoTable, "Roll Number:", student.getRollNo(), headerFont, normalFont);
        addInfoRow(infoTable, "Program:", student.getProgram(), headerFont, normalFont);
        addInfoRow(infoTable, "Year:", String.valueOf(student.getYear()), headerFont, normalFont);
        addInfoRow(infoTable, "Date Generated:",
                LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")), headerFont, normalFont);

        document.add(infoTable);

        // Group grades by enrollment (course)
        Map<Integer, List<Grade>> gradesByEnrollment = grades.stream()
                .collect(Collectors.groupingBy(Grade::getEnrollmentId));

        // Grades Table
        PdfPTable table = new PdfPTable(new float[] { 2, 4, 1, 2, 1 });
        table.setWidthPercentage(100);
        table.setHeaderRows(1);

        // Table Headers
        addTableHeader(table, "Course Code", headerFont);
        addTableHeader(table, "Course Title", headerFont);
        addTableHeader(table, "Credits", headerFont);
        addTableHeader(table, "Semester", headerFont);
        addTableHeader(table, "Grade", headerFont);

        // Calculate and display final grade for each course
        int totalCredits = 0;
        BigDecimal totalGradePoints = BigDecimal.ZERO;

        for (Map.Entry<Integer, List<Grade>> entry : gradesByEnrollment.entrySet()) {
            List<Grade> courseGrades = entry.getValue();
            if (courseGrades.isEmpty())
                continue;

            // Get course info from first grade
            Grade firstGrade = courseGrades.get(0);

            // Calculate weighted average
            BigDecimal weightedSum = BigDecimal.ZERO;
            for (Grade grade : courseGrades) {
                weightedSum = weightedSum.add(grade.getWeightedScore());
            }

            // Convert to letter grade
            String letterGrade = calculateLetterGrade(weightedSum);

            // Get credits (using placeholder method for now)
            int credits = firstGrade.getCredits();

            // Add to table
            table.addCell(
                    new Phrase(firstGrade.getCourseCode() != null ? firstGrade.getCourseCode() : "N/A", normalFont));
            table.addCell(
                    new Phrase(firstGrade.getCourseTitle() != null ? firstGrade.getCourseTitle() : "N/A", normalFont));
            table.addCell(new Phrase(String.valueOf(credits), normalFont));
            table.addCell(new Phrase(firstGrade.getSemester() != null ? firstGrade.getSemester() : "N/A", normalFont));
            table.addCell(
                    new Phrase(letterGrade + " (" + weightedSum.setScale(2, RoundingMode.HALF_UP) + "%)", normalFont));

            // Calculate GPA contribution
            totalCredits += credits;
            BigDecimal gradePoint = getGradePoint(letterGrade);
            totalGradePoints = totalGradePoints.add(gradePoint.multiply(new BigDecimal(credits)));
        }

        document.add(table);

        // GPA Calculation
        if (totalCredits > 0) {
            BigDecimal gpa = totalGradePoints.divide(new BigDecimal(totalCredits), 2, RoundingMode.HALF_UP);

            Paragraph gpaInfo = new Paragraph();
            gpaInfo.setSpacingBefore(20);
            gpaInfo.add(new Phrase("Total Credits: " + totalCredits + "    ", headerFont));
            gpaInfo.add(new Phrase("GPA: " + gpa + " / 4.0", headerFont));
            document.add(gpaInfo);
        }

        // Footer
        Paragraph footer = new Paragraph("This is a computer-generated document.",
                FontFactory.getFont(FontFactory.HELVETICA_OBLIQUE, 8, Color.GRAY));
        footer.setAlignment(Element.ALIGN_CENTER);
        footer.setSpacingBefore(30);
        document.add(footer);

        document.close();
    }

    private static String calculateLetterGrade(BigDecimal percentage) {
        double score = percentage.doubleValue();
        if (score >= 90)
            return "A";
        if (score >= 80)
            return "B";
        if (score >= 70)
            return "C";
        if (score >= 60)
            return "D";
        return "F";
    }

    private static BigDecimal getGradePoint(String letterGrade) {
        switch (letterGrade) {
            case "A":
                return new BigDecimal("4.0");
            case "B":
                return new BigDecimal("3.0");
            case "C":
                return new BigDecimal("2.0");
            case "D":
                return new BigDecimal("1.0");
            default:
                return BigDecimal.ZERO;
        }
    }

    private static void addInfoRow(PdfPTable table, String label, String value, Font labelFont, Font valueFont) {
        PdfPCell labelCell = new PdfPCell(new Phrase(label, labelFont));
        labelCell.setBorder(com.lowagie.text.Rectangle.NO_BORDER);
        table.addCell(labelCell);

        PdfPCell valueCell = new PdfPCell(new Phrase(value, valueFont));
        valueCell.setBorder(com.lowagie.text.Rectangle.NO_BORDER);
        table.addCell(valueCell);
    }

    private static void addTableHeader(PdfPTable table, String header, Font font) {
        PdfPCell cell = new PdfPCell(new Phrase(header, font));
        cell.setHorizontalAlignment(Element.ALIGN_CENTER);
        cell.setBackgroundColor(Color.LIGHT_GRAY);
        cell.setPadding(5);
        table.addCell(cell);
    }
}
