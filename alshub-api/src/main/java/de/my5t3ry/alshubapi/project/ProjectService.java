package de.my5t3ry.alshubapi.project;

import de.my5t3ry.alshubapi.explorer.SetPathRequest;
import de.my5t3ry.alshubapi.git.GitService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class ProjectService {
    @Autowired
    private GitService gitService;
    public Project addProject(final SetPathRequest setPathRequest) {
        Project  project = new Project(setPathRequest);
        gitService.createNewRepositoryForProject(project);
                                            return project;
    }
}
