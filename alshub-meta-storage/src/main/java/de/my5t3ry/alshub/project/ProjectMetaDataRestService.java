package de.my5t3ry.alshub.project;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/project-metadata")
public class ProjectMetaDataRestService {
    @Autowired
    private ProjectMetaDataRepository projectMetaDataRepository;

    @PostMapping(path = "/")
    public ResponseEntity<ProjectMetaData> save(@RequestBody ProjectMetaData projectMetaData) {
        return new ResponseEntity<>(projectMetaDataRepository.save(projectMetaData), HttpStatus.OK);
    }
}
