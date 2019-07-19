package de.my5t3ry.alshubapi.project;

import lombok.Data;

@Data
public class ProjectChange {
    public ProjectChange(final ProjectChangeType type, final String path) {
        this.type = type;
        this.path = path;
    }

    private ProjectChangeType type;
    private String path;
}
