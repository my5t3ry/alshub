package de.my5t3ry.alshubapi.project;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import de.my5t3ry.alshub.project.ProjectMetaData;
import de.my5t3ry.alshubapi.error.ProcessingException;
import de.my5t3ry.alshubapi.user.UserController;
import kong.unirest.Unirest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.security.Principal;
import java.util.HashMap;
import java.util.UUID;

@Component
@Scope("singleton")
public class ProjectMetaDataService {
    private final String API_URL = "https://alshub-meta-storage.mikodump.org/api/project-metadata/";
    //    private final String API_URL = "https://alshub-meta-storage.mikodump.org/api/project-metadata/";
    private ObjectMapper objectMapper = new ObjectMapper();
    private HashMap<Integer, ProjectMetaData> cache = new HashMap<>();

    @Autowired
    private UserController userController;

    public ProjectMetaDataService() {
        objectMapper.configure(
                DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        ;
    }

    public ProjectMetaData getByProject(final Project project) throws IOException, InterruptedException {
        return getQueryProjectMetaData(project.getId());
    }

    public ProjectMetaData getByProject(final Integer projectId) throws IOException, InterruptedException {
        if (cache.containsKey(projectId)) {
            return cache.get(projectId);
        }
        return getQueryProjectMetaData(projectId);
    }

    public ProjectMetaData createProjectMetaData(final Project project, Principal principal) {
        final ProjectMetaData projectMetaData = ProjectMetaData.builder()
                .id(UUID.randomUUID().toString())
                .ownerUserId(userController.getUser(principal).getId())
                .projectId(project.getId())
                .name(project.getName())
                .viewCount(0)
                .likeCount(0)
                .forkCount(0)
                .build();
        try {
            final ProjectMetaData result = putQueryProjectMetaData(projectMetaData);
            cache.put(project.getId(), result);
            return result;
        } catch (IOException | InterruptedException e) {
            throw new ProcessingException("Could not serialize projectMeta data", e);
        }
    }

    private ProjectMetaData getQueryProjectMetaData(Integer projectId) throws IOException, InterruptedException {
        final String resultJson =        Unirest.get(API_URL + "/by-project-id/" + projectId) .asString().getBody();

        return objectMapper.readValue(resultJson, ProjectMetaData.class);
    }

    private ProjectMetaData putQueryProjectMetaData(ProjectMetaData projectMetaData) throws IOException, InterruptedException {
        final String body =  Unirest.get(API_URL) .asString().getBody();
        return objectMapper.readValue(body, ProjectMetaData.class);
    }
}
