package de.my5t3ry.alshubapi.project;

import de.my5t3ry.alshubapi.explorer.SetPathRequest;
import lombok.Data;

@Data
public class Project {
    private final String path;
    private String gitUuid;

    public Project(final SetPathRequest setPathRequest) {
        this.path = setPathRequest.getPath();
    }
}
