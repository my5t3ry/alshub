package de.my5t3ry.alshubapi.project;

import de.my5t3ry.alshub.project.ProjectMetaData;
import de.my5t3ry.alshubapi.error.ProcessingException;
import de.my5t3ry.alshubapi.error.ProcessingExceptionType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;

@RestController
@RequestMapping("/project-meta-data")
public class ProjectMetaDataRestController {
    @Autowired
    private ProjectMetaDataService projectMetaDataService;

    @GetMapping("/{projectId}")
    public ResponseEntity<ProjectMetaData> get(@PathVariable("projectId") Integer projectId) {
        try {
            return new ResponseEntity<>(projectMetaDataService.getByProject(projectId), HttpStatus.OK);
        } catch (IOException | InterruptedException e) {
            throw new ProcessingException("could not receive meta data for project", ProcessingExceptionType.MINOR);
        }
    }
}
