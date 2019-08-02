package de.my5t3ry.alshub.project;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/project-metadata")
public class ProjectMetaDataRestService {
    @Autowired
    private ProjectMetaDataRepository projectMetaDataRepository;

    @GetMapping("/by-project-id/{projectId}")
    public ResponseEntity<ProjectMetaData> get(@PathVariable("projectId") Integer projectId) {
        return new ResponseEntity<>(projectMetaDataRepository.findByProjectId(projectId), HttpStatus.OK);
    }

    @PostMapping(path = "/")
    public ResponseEntity<ProjectMetaData> save(@RequestBody ProjectMetaData projectMetaData) {
        return new ResponseEntity<>(projectMetaDataRepository.save(projectMetaData), HttpStatus.OK);
    }

    @PostMapping(path = "/update")
    public ResponseEntity<ProjectMetaData> update(@RequestBody ProjectMetaData projectMetaData) {
        final ProjectMetaData byProjectId = projectMetaDataRepository.findByProjectId(projectMetaData.getProjectId());
        if (byProjectId != null) {
            projectMetaDataRepository.delete(byProjectId);
        }
        return new ResponseEntity<>(projectMetaDataRepository.save(projectMetaData), HttpStatus.OK);
    }
}
