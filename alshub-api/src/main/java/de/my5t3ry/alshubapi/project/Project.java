package de.my5t3ry.alshubapi.project;

import de.my5t3ry.alshubapi.explorer.SetPathRequest;
import de.my5t3ry.alshubapi.user.User;
import lombok.Data;

import javax.persistence.*;

@Data
@Entity
public class Project {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Integer id;
    private String path;
    private String gitUuid;
    private String remoteGitUrl;
    @ManyToOne
    private User user;

    public Project() {
    }

    public Project(final SetPathRequest setPathRequest) {
        this.path = setPathRequest.getPath();
    }

    public void setUser(final User user) {
        this.user = user;
    }

    public User getUser() {
        return user;
    }
}
