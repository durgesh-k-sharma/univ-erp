package edu.univ.erp.ui.instructor;

import edu.univ.erp.domain.Section;

public class SectionItem {
    private final Section section;

    public SectionItem(Section section) {
        this.section = section;
    }

    public Section getSection() {
        return section;
    }

    @Override
    public String toString() {
        return section.getCourseCode() + "-" + section.getSectionNumber() +
                " (" + section.getSemester() + " " + section.getYear() + ")";
    }
}
