package de.my5t3ry.alshubapi.git;

import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class GitGraphCommit {

    private String author;
    private String subject;
    private String hash;
    private final List<GitGraphCommit> parents = new ArrayList<>();
    private String dotText;
    private boolean checkedOut = false;

    protected boolean canEqual(final Object other) {
        return other instanceof GitGraphCommit;
    }

}
