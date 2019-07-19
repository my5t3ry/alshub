package de.my5t3ry.alshubapi.project;

import de.my5t3ry.als_parser.AbletonFileParser;
import de.my5t3ry.als_parser.domain.AbletonProject.AbletonProject;
import de.my5t3ry.alshubapi.explorer.SetPathRequest;
import de.my5t3ry.alshubapi.git.GitService;
import de.my5t3ry.alshubapi.user.User;
import de.my5t3ry.alshubapi.user.UserController;
import de.my5t3ry.alshubapi.user.UserRepository;
import org.apache.commons.io.FileUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.File;
import java.security.Principal;
import java.util.ArrayList;

@Component
public class ProjectService {
    @Autowired
    private GitService gitService;
    @Autowired
    private UserController userController;
    @Autowired
    private ProjectRepository projectRepository;
    @Autowired
    private UserRepository userRepository;

    public Project addProject(final SetPathRequest setPathRequest, Principal principal) {
        Project project = new Project(setPathRequest);
        gitService.createNewRepositoryForProject(project);
        project.setAlsFile(findAlsFile(project).getAbsolutePath());
        createStats(project);
        projectRepository.save(project);
        final User user = userController.getUser(principal);
        user.addProject(project);
        userRepository.save(user);
        return project;
    }

    private void createStats(final Project project) {
        final AbletonFileParser alsParser = new AbletonFileParser();
        final AbletonProject result = alsParser.parse(new File(project.getAlsFile()));
        project.setAbletonProject(result);
        System.out.println(result.getTotalTracks());
    }

    private File findAlsFile(final Project project) {
        final ArrayList<File> files = new ArrayList<>(FileUtils.listFiles(new File(project.getPath()), new String[]{"als"}, false));
        if (files.size() > 0) {
            return files.get(0);
        }
        throw new IllegalStateException("Could not find als file for project");
    }
}
