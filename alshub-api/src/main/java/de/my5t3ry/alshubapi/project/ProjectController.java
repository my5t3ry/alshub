package de.my5t3ry.alshubapi.project;

import de.my5t3ry.alshubapi.explorer.SetPathRequest;
import de.my5t3ry.alshubapi.git.GitService;
import de.my5t3ry.alshubapi.user.UserController;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.security.Principal;

@Component
public class ProjectController {
    @Autowired
    private GitService gitService;
    @Autowired
    private UserController userController;
    @Autowired
    private ProjectRepository projectRepository;

    public Project addProject(final SetPathRequest setPathRequest, Principal principal) {
        Project project = new Project(setPathRequest);
        gitService.createNewRepositoryForProject(project);
        project.setUser(userController.getUser(principal));
        projectRepository.save(project);
        return project;
    }
}
