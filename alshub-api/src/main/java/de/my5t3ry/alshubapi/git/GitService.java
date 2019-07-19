package de.my5t3ry.alshubapi.git;

import com.jcraft.jsch.Session;
import de.my5t3ry.alshubapi.project.Project;
import de.my5t3ry.alshubapi.project.ProjectChange;
import de.my5t3ry.alshubapi.project.ProjectChangeType;
import de.my5t3ry.alshubapi.project.ProjectChanges;
import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.PushCommand;
import org.eclipse.jgit.api.Status;
import org.eclipse.jgit.api.errors.GitAPIException;
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
import java.util.Set;


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
        } catch (IOException | GitAPIException e) {
            e.printStackTrace();
        }
    }

    public ProjectChanges checkChanges(Project project) {
        ProjectChanges result = new ProjectChanges();
        try {
            Git git = null;
            git = Git.open(new File(project.getPath()));
            Status status = git.status().call();
            Set<String> conflicting = status.getConflicting();
            for (String conflict : conflicting) {
                result.addChange(new ProjectChange(ProjectChangeType.CONFLICT, conflict));
            }
            Set<String> added = status.getAdded();
            for (String add : added) {
                result.addChange(new ProjectChange(ProjectChangeType.ADDED, add));
            }
            Set<String> changed = status.getChanged();
            for (String change : changed) {
                result.addChange(new ProjectChange(ProjectChangeType.CHANGE, change));
            }
            Set<String> missing = status.getMissing();
            for (String miss : missing) {
                result.addChange(new ProjectChange(ProjectChangeType.MISSING, miss));
            }
            Set<String> modified = status.getModified();
            for (String modify : modified) {
                result.addChange(new ProjectChange(ProjectChangeType.MODIFICATION, modify));
            }
            Set<String> removed = status.getRemoved();
            for (String remove : removed) {
                result.addChange(new ProjectChange(ProjectChangeType.REMOVED, remove));
            }
            Set<String> uncommittedChanges = status.getUncommittedChanges();
            for (String uncommitted : uncommittedChanges) {
                result.addChange(new ProjectChange(ProjectChangeType.UNCOMMITED, uncommitted));
            }
            Set<String> untracked = status.getUntracked();
            for (String untrack : untracked) {
                result.addChange(new ProjectChange(ProjectChangeType.UNTRACKED, untrack));
            }
            Set<String> untrackedFolders = status.getUntrackedFolders();
            for (String untrack : untrackedFolders) {
                result.addChange(new ProjectChange(ProjectChangeType.UNTRACKED, untrack));
            }
        } catch (IOException e) {
            e.printStackTrace();
        } catch (GitAPIException e) {
            e.printStackTrace();
        }
        return result;

    }
}
