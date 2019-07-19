package de.my5t3ry.alshubapi.user;

import de.my5t3ry.alshubapi.project.Project;
import lombok.Data;
import lombok.Getter;

import javax.persistence.*;
import java.security.Principal;
import java.util.ArrayList;
import java.util.List;

@Entity
@Data
@Getter
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Integer id;

    private String name;
    @ManyToMany
    private List<Project> projects = new ArrayList<>();

    public User() {
    }

    public User(final Principal principal) {
        this.name = principal.getName();
    }

    public void addProject(Project project) {
        this.projects.add(project);
    }

}
