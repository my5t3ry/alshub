package de.my5t3ry.alshubapi.project;

import de.my5t3ry.alshubapi.git.GitGraphCommit;
import de.my5t3ry.alshubapi.git.GitService;
import de.my5t3ry.alshubapi.response_entity.ResponseEntityFactory;
import de.my5t3ry.alshubapi.response_entity.ResponseMessageType;
import de.my5t3ry.alshubapi.user.UserController;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;

@RestController
@RequestMapping("/project")
public class ProjectRestController {
    @Autowired
    private ProjectRepository projectRepository;
    @Autowired
    private UserController userController;
    @Autowired
    private ProjectService projectService;
    @Autowired
    private GitService gitService;

    @GetMapping("/my-projects")
    public ResponseEntity<List<Project>> get(Principal principal) {
        return new ResponseEntity<>((userController.getUser(principal)).getProjects(), HttpStatus.OK);
    }

    @GetMapping("/{projectId}")
    public ResponseEntity<Project> get(@PathVariable("projectId") Integer projectId) {
        return new ResponseEntity<>(projectRepository.findById(projectId).get(), HttpStatus.OK);
    }

    @GetMapping("/get-changes/{projectId}")
    public ResponseEntity<ProjectChanges> getChanges(@PathVariable("projectId") Integer projectId) {
        return ResponseEntityFactory.build(
                gitService.checkChanges(projectRepository.findById(projectId).get()),
                HttpStatus.OK);
    }

    @GetMapping("/get-commit-history/{projectId}")
    public ResponseEntity<List<GitGraphCommit>> getCommitHistory(@PathVariable("projectId") Integer projectId) {
        return ResponseEntityFactory.build(gitService.plotGitGraph(projectRepository.findById(projectId).get()),
                HttpStatus.OK);
    }

    @GetMapping("/restore-commit/{projectId}/{commitId}")
    public ResponseEntity<List<GitGraphCommit>> getCommitHistory(@PathVariable("projectId") Integer projectId, @PathVariable("commitId") String commitId) {
        return ResponseEntityFactory.build("Revision restored",
                ResponseMessageType.INFO,
                gitService.checkoutCommit(projectRepository.findById(projectId).get(), commitId),
                HttpStatus.OK);
    }

    @PostMapping(path = "/edit-project")
    public ResponseEntity<Project> editProject(@RequestBody Project project, Principal principal) {
        projectService.updateProject(project, principal);
        return ResponseEntityFactory.build("Project saved",
                ResponseMessageType.INFO,
                projectRepository.save(project),
                HttpStatus.OK);
    }

    @GetMapping("/push-changes/{projectId}")
    public ResponseEntity<List<GitGraphCommit>> pushChanges(@PathVariable("projectId") Integer projectId) {
        gitService.pushChanges(projectRepository.findById(projectId).get());
        return ResponseEntityFactory.build(gitService.plotGitGraph(projectRepository.findById(projectId).get()),
                HttpStatus.OK);
    }
}
