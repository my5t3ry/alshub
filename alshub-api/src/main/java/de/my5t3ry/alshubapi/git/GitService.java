package de.my5t3ry.alshubapi.git;

import com.jcraft.jsch.Session;
import de.my5t3ry.alshubapi.project.Project;
import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.PushCommand;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.api.errors.InvalidRemoteException;
import org.eclipse.jgit.api.errors.TransportException;
import org.eclipse.jgit.lib.StoredConfig;
import org.eclipse.jgit.transport.JschConfigSessionFactory;
import org.eclipse.jgit.transport.OpenSshConfig;
import org.eclipse.jgit.transport.SshSessionFactory;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;


@Component
public class GitService {

    private final String API_URL = "https://alshub-git.mikodump.org/api/git/";
    private final HttpRequest request = HttpRequest.newBuilder()
            .uri(URI.create(API_URL))
            .build();
    private final HttpClient client = HttpClient.newHttpClient();
    private final String GIT_REPO_URL = "git@mikodump.org:/home/git/repos/";

    public GitService() {
        SshSessionFactory.setInstance(new JschConfigSessionFactory() {
            public void configure(OpenSshConfig.Host hc, Session session) {
                session.setConfig("StrictHostKeyChecking", "no");
            }
        });
    }

    public void createNewRepositoryForProject(final Project project) {
        try {
            final String uuid = client.send(request, HttpResponse.BodyHandlers.ofString()).body();
            project.setGitUuid(uuid);
            this.createAndPushLocalRepo(project);
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }
    }

    private void createAndPushLocalRepo(final Project project) {
        try {
            File f‌ile = new File(project.getPath().concat("/README"));
            f‌ile.createNewFile();
            Git newLocalRepository = Git.init().setDirectory(f‌ile.getParentFile()).call();
            newLocalRepository.add().addFilepattern(".").call();
            StoredConfig config = newLocalRepository.getRepository().getConfig();
            final String remoteGitUrl = GIT_REPO_URL.concat(project.getGitUuid());
            project.setRemoteGitUrl(remoteGitUrl);
            config.setString("remote", "origin", "url", remoteGitUrl);
            config.save();
            newLocalRepository.commit()
                    .setMessage("Initial commit")
                    .setCommitter("auto", "commit")
                    .call();
            PushCommand pushCommand = newLocalRepository.push();
            pushCommand.call();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (InvalidRemoteException e) {
            e.printStackTrace();
        } catch (TransportException e) {
            e.printStackTrace();
        } catch (GitAPIException e) {
            e.printStackTrace();
        }
    }
}
