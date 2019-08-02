package de.my5t3ry.alshubapi.project;

import de.my5t3ry.als_parser.domain.AbletonProject.AbletonProject;
import de.my5t3ry.alshubapi.explorer.SetPathRequest;
import lombok.Data;

import javax.persistence.*;
import java.io.File;
import java.util.List;

@Data
@Entity
public class Project {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Integer id;
    private String name;
    private String path;
    private String gitUuid;
    private String remoteGitUrl;
    private String alsFile;
    @OneToOne(cascade = CascadeType.ALL)
    private AbletonProject abletonProject;
    @ManyToMany
    private List<Genre> genreList;

    public Project() {
    }

    public Project(final SetPathRequest setPathRequest) {
        this.path = setPathRequest.getPath();
        this.name = new File(setPathRequest.getPath()).getName();
    }

    public void setAlsFile(final String alsFile) {
        this.alsFile = alsFile;
    }

    public String getAlsFile() {
        return alsFile;
    }

    public void setAbletonProject(final AbletonProject abletonProject) {
        this.abletonProject = abletonProject;
    }

    public AbletonProject getAbletonProject() {
        return abletonProject;
    }
}
