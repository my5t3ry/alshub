package de.my5t3ry.alshub.git;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.filefilter.HiddenFileFilter;
import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.File;
import java.io.IOException;
import java.nio.file.FileSystem;
import java.nio.file.Files;
import java.nio.file.attribute.UserPrincipal;
import java.nio.file.attribute.UserPrincipalLookupService;
import java.util.UUID;

@RestController
@RequestMapping("/git")
public class GitRestController {

    private final String REPO_PATH = "/home/git/repos/";

    @GetMapping()
    public ResponseEntity<String> get() {
        UUID uuid = UUID.randomUUID();
        File localPath = null;
        try {
            localPath = new File(REPO_PATH, uuid.toString());
            localPath.mkdir();
            Git git = Git.init().setDirectory(localPath).setBare(true).call();
            FileSystem fileSystem = localPath.toPath().getFileSystem();
            UserPrincipalLookupService service = fileSystem.getUserPrincipalLookupService();
            UserPrincipal userPrincipal = service.lookupPrincipalByName("git");
            FileUtils.listFilesAndDirs(localPath, HiddenFileFilter.VISIBLE, HiddenFileFilter.VISIBLE).forEach(file -> {
                try {
                    Files.setOwner(file.toPath(), userPrincipal);
                } catch (IOException e) {
                    throw new IllegalStateException("Could not create repo", e);
                }
            });
            return new ResponseEntity<>(uuid.toString(), HttpStatus.OK);
        } catch (GitAPIException e) {
            throw new IllegalStateException("Could not create repo", e);
        } catch (IOException e) {
            throw new IllegalStateException("Could not create repo", e);
        }
    }
}
