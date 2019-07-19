package de.my5t3ry.alshubapi.project;

import de.my5t3ry.alshubapi.user.UserController;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.security.Principal;
import java.util.List;

@RestController
@RequestMapping("/project")
public class ProjectRestController {
    @Autowired
    private ProjectRepository projectRepository;
    @Autowired
    private UserController userController;

    @GetMapping("/my-projects")
    public ResponseEntity<List<Project>> get(Principal principal) {
        projectRepository.findByUser(userController.getUser(principal));
        return new ResponseEntity<>(projectRepository.findByUser(userController.getUser(principal)), HttpStatus.OK);
    }

    @GetMapping("/{projectId}")
    public ResponseEntity<Project> get(@PathVariable("projectId") Integer projectId) {
        return new ResponseEntity<>(projectRepository.findById(projectId).get(), HttpStatus.OK);
    }
}
