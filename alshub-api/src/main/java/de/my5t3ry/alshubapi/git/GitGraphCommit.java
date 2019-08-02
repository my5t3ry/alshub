package de.my5t3ry.alshubapi.git;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.Date;
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
    private Date commitTime;
    private boolean checkedOut = false;

    protected boolean canEqual(final Object other) {
        return other instanceof GitGraphCommit;
    }

}
