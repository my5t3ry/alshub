package de.my5t3ry.alshubapi.project;

import com.fasterxml.jackson.databind.ObjectMapper;
import de.my5t3ry.alshub.project.ProjectMetaData;
import de.my5t3ry.alshubapi.error.ProcessingException;
import de.my5t3ry.alshubapi.user.UserController;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.security.Principal;
import java.time.Duration;
import java.util.UUID;

@Component
public class ProjectMetaDataService {
    private final String API_URL = "https://alshub-meta-storage.mikodump.org/api/project-metadata/";
    //    private final String API_URL = "https://alshub-meta-storage.mikodump.org/api/project-metadata/";
    private HttpClient client = HttpClient.newHttpClient();
    private ObjectMapper objectMapper = new ObjectMapper();

    @Autowired
    private UserController userController;

    public ProjectMetaData createProjectMetaData(final Project project, Principal principal) {
        final ProjectMetaData projectMetaData = ProjectMetaData.builder()
                .id(UUID.randomUUID().toString())
                .ownerUserId(userController.getUser(principal).getId())
                .projectId(project.getId())
                .viewCount(0)
                .likeCount(0)
                .forkCount(0)
                .build();
        try {
            final HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(API_URL))
                    .timeout(Duration.ofMinutes(1))
                    .header("Content-Type", "application/json")
                    .POST(HttpRequest.BodyPublishers.ofString(objectMapper.writeValueAsString(projectMetaData)))
                    .build();
            final String body = HttpClient.newHttpClient().send(HttpRequest.newBuilder()
                    .uri(URI.create(API_URL))
                    .build(), HttpResponse.BodyHandlers.ofString()).body();
            return projectMetaData;
        } catch (IOException | InterruptedException e) {
            throw new ProcessingException("Could not serialize projectMeta data", e);
        }

    }

}
