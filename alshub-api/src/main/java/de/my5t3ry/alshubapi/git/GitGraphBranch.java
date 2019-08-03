package de.my5t3ry.alshubapi.git;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class GitGraphBranch implements Comparable<GitGraphBranch> {

    private String name;
    private String hash;
    private List<GitGraphCommit> children = new ArrayList<>();

    protected boolean canEqual(final Object other) {
        return other instanceof GitGraphBranch;
    }

    @Override
    public int compareTo(@NotNull final GitGraphBranch o) {
        return getLowestCommitDate().compareTo(o.getLowestCommitDate());
    }

    private Date getLowestCommitDate() {
        return children.stream().map(GitGraphCommit::getCommitTime).min(Date::compareTo).get();
    }
}
