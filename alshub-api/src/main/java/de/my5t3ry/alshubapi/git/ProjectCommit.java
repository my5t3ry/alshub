package de.my5t3ry.alshubapi.git;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class ProjectCommit {
    private final String msg;
    private final String time;
    private final String id;
}
