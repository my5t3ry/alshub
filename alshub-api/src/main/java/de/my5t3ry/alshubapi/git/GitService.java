package de.my5t3ry.alshubapi.git;

import com.jcraft.jsch.Session;
import de.my5t3ry.alshubapi.error.ProcessingException;
import de.my5t3ry.alshubapi.project.Project;
import de.my5t3ry.alshubapi.project.ProjectChange;
import de.my5t3ry.alshubapi.project.ProjectChangeType;
import de.my5t3ry.alshubapi.project.ProjectChanges;
import kong.unirest.Unirest;
import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.PushCommand;
import org.eclipse.jgit.api.Status;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.internal.storage.file.FileRepository;
import org.eclipse.jgit.lib.Repository;
import org.eclipse.jgit.lib.RepositoryCache;
import org.eclipse.jgit.lib.StoredConfig;
import org.eclipse.jgit.revwalk.RevCommit;
import org.eclipse.jgit.transport.JschConfigSessionFactory;
import org.eclipse.jgit.transport.OpenSshConfig;
import org.eclipse.jgit.transport.SshSessionFactory;
import org.eclipse.jgit.util.FS;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Set;


@Component
public class GitService {

    private final String API_URL = "https://alshub-git.mikodump.org/api/git/";
    private final String GIT_REPO_URL = "git@mikodump.org:/home/git/repos/";

    public GitService() {
        SshSessionFactory.setInstance(new JschConfigSessionFactory() {
            public void configure(OpenSshConfig.Host hc, Session session) {
                session.setConfig("StrictHostKeyChecking", "no");
            }
        });
    }

    public void createNewRepositoryForProject(final Project project) {
        if (!RepositoryCache.FileKey.isGitRepository(new File(project.getPath()), FS.DETECTED)) {
            final String uuid = Unirest.get(API_URL).asString().getBody();
            project.setGitUuid(uuid);
            this.createAndPushLocalRepo(project);
        }
    }

    public List<ProjectCommit> getCommits(final Project project) {
        final List<ProjectCommit> result = new ArrayList<ProjectCommit>();
        Repository repository = null;
        try {
            repository = new FileRepository(new File(project.getPath() + "/.git"));
            final Iterable<RevCommit> logs = new Git(repository).log()
                    .add(repository.resolve("refs/heads/master"))
                    .call();
            for (RevCommit rev : logs) {
                result.add(new ProjectCommit(rev.getFullMessage(), new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date(rev.getCommitTime())), rev.getName()));
            }
        } catch (IOException | GitAPIException e) {
            throw new ProcessingException("could not resolve commit history for project ['" + project.getPath() + "']", e);
        }
        return result;
    }

    private void createAndPushLocalRepo(final Project project) {
        try {
            File f‌ile = new File(project.getPath().concat("/README"));
            f‌ile.createNewFile();
            Git newLocalRepository = Git.init().setDirectory(f‌ile.getParentFile()).call();
            setRemoteOrigin(project, newLocalRepository);
            pushChanges(newLocalRepository, "Initialized project repo");
        } catch (IOException | GitAPIException e) {
            e.printStackTrace();
        }
    }

    public void pushChanges(final Git newLocalRepository, final String commitMessage) throws GitAPIException {
        newLocalRepository.add().addFilepattern(".").call();
        newLocalRepository.commit()
                .setMessage(commitMessage)
                .setCommitter("auto", "commit")
                .call();
        PushCommand pushCommand = newLocalRepository.push();
        pushCommand.call();
    }

    public void pushChanges(final Project project) {
        try {
            pushChanges(Git.open(new File(project.getPath())), "Manual revision created");
        } catch (IOException | GitAPIException e) {
            e.printStackTrace();
        }
    }

    private void setRemoteOrigin(final Project project, final Git newLocalRepository) throws IOException {
        StoredConfig config = newLocalRepository.getRepository().getConfig();
        final String remoteGitUrl = GIT_REPO_URL.concat(project.getGitUuid());
        project.setRemoteGitUrl(remoteGitUrl);
        config.setString("remote", "origin", "url", remoteGitUrl);
        config.save();
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
