package de.my5t3ry.alshubapi.project;

import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
public class ProjectChanges {
    private final List<ProjectChange> changeList = new ArrayList<ProjectChange>();

    public void addChange(ProjectChange change) {
        changeList.add(change);
    }
}
