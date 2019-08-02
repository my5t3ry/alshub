package de.my5t3ry.alshubapi.git;

import com.jcraft.jsch.Session;
import de.my5t3ry.alshubapi.error.ProcessingException;
import de.my5t3ry.alshubapi.project.Project;
import de.my5t3ry.alshubapi.project.ProjectChange;
import de.my5t3ry.alshubapi.project.ProjectChangeType;
import de.my5t3ry.alshubapi.project.ProjectChanges;
import kong.unirest.Unirest;
import net.nemerosa.ontrack.git.GitRepository;
import net.nemerosa.ontrack.git.GitRepositoryClient;
import net.nemerosa.ontrack.git.support.GitRepositoryClientFactoryImpl;
import org.eclipse.jgit.api.*;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.lib.Constants;
import org.eclipse.jgit.lib.Ref;
import org.eclipse.jgit.lib.RepositoryCache;
import org.eclipse.jgit.lib.StoredConfig;
import org.eclipse.jgit.revwalk.RevCommit;
import org.eclipse.jgit.revwalk.RevWalk;
import org.eclipse.jgit.transport.JschConfigSessionFactory;
import org.eclipse.jgit.transport.OpenSshConfig;
import org.eclipse.jgit.transport.PushResult;
import org.eclipse.jgit.transport.SshSessionFactory;
import org.eclipse.jgit.util.FS;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;


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
//
//    public List<GitGraphCommit> getCommits(final Project project) {
//        final List<GitGraphCommit> result = new ArrayList<GitGraphCommit>();
//        try {
//            final Git git = getGit(project);
//            final Iterable<RevCommit> logs = git.log()
//                    .add(git.getRepository().resolve("refs/heads/master"))
//                    .call();
//            for (RevCommit rev : logs) {
//                result.add(new GitGraphCommit(rev.getFullMessage(), new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(rev.getAuthorIdent().getWhen()), rev.getName(), false));
//            }
//            Ref head = git.getRepository().getAllRefs().get("HEAD");
//            result.stream().filter(gitGraphCommit -> gitGraphCommit.getId().equals(head.getObjectId().getName())).collect(Collectors.toList()).forEach(gitGraphCommit -> gitGraphCommit.setCheckedOut(true));
//        } catch (IOException | GitAPIException e) {
//            throw new ProcessingException("could not resolve commit history for project ['" + project.getPath() + "']", e);
//        }
//        return result;
//    }

    private Git getGit(final Project project) {
        try {
            return Git.open(new File(project.getPath()));
        } catch (IOException e) {
            throw new ProcessingException("could not read repository for project ['" + project.getPath() + "']", e);
        }
    }

    private void createAndPushLocalRepo(final Project project) {
        try {
            File f‌ile = new File(project.getPath().concat("/README"));
            f‌ile.createNewFile();
            Git newLocalRepository = Git.init().setDirectory(f‌ile.getParentFile()).call();
            setRemoteOrigin(project, newLocalRepository);
            pushChanges("created local project repository", project);
        } catch (IOException | GitAPIException e) {
            throw new ProcessingException("could not create local repo for project ['" + e.getMessage() + "']", e);
        }
    }

    public void pushChanges(final String commitMessage, final Project project) throws GitAPIException {
        final Git git = getGit(project);
        git.add().addFilepattern(".").call();
        try {
            final String commitName = git.getRepository().getBranch();
            if (isValidSHA1(commitName)) {
                final String newBranchName = "branched_" + commitName;
                if (!branchExists(git, newBranchName)) {
                    Ref ref = git.checkout().
                            setCreateBranch(true).
                            setName(newBranchName).
                            setUpstreamMode(CreateBranchCommand.SetupUpstreamMode.TRACK).
                            setForce(true)
                            .call();
                } else {
                    Ref ref = git.checkout().
                            setName(newBranchName)
                            .setForce(true)
                            .call();
                }
            }
            git.commit()
                    .setMessage(commitMessage)
                    .setCommitter("auto", "commit")
                    .setAll(true)
                    .call();
            PushCommand pushCommand = git.push();
            final Iterable<PushResult> call = pushCommand.call();
            plotGitGraph(project);
        } catch (IOException e) {
            throw new ProcessingException("could not push changes for project ['" + e.getMessage() + "']", e);
        }
    }

    public List<RevCommit> getCommits(Git git, Ref branch) {
        final ArrayList<RevCommit> commits = new ArrayList<>();
        try (RevWalk revWalk = new RevWalk(git.getRepository())) {
            revWalk.markStart(revWalk.parseCommit(branch.getObjectId()));
            revWalk.forEach(c -> commits.add(c));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return commits;
    }

    public List<GitGraphBranch> plotGitGraph(final Project project) {
        final List<GitGraphBranch> result = new ArrayList<GitGraphBranch>();
        final Git newLocalRepository = getGit(project);
        final GitRepositoryClient source = new GitRepositoryClientFactoryImpl(new File(project.getPath())).getClient(new GitRepository("source", project.getGitUuid(), GIT_REPO_URL.concat(project.getGitUuid()), "", ""));
        source.getBranches();
        List<Ref> branches = null;
        try {
            branches = newLocalRepository.branchList().setListMode(ListBranchCommand.ListMode.ALL).call();
            for (Ref branch : branches) {
                final List<RevCommit> commits = getCommits(newLocalRepository, branch);
                List<GitGraphCommit> scrapedCommits = convertToGitGraphCommit(commits, newLocalRepository);
                result.add(GitGraphBranch.builder().hash(branch.getObjectId().getName()).commits(scrapedCommits).subject(branch.getName()).build());
            }
            Collections.sort(result);
            return result;
        } catch (Exception e) {
            throw new ProcessingException("could not plot graph for project ['" + e.getMessage() + "']", e);
        }
    }

    private List<GitGraphCommit> convertToGitGraphCommit(List<RevCommit> commits, Git git) {
        final List<GitGraphCommit> result = new ArrayList<GitGraphCommit>();
        try {
            for (RevCommit gitCommit : commits) {
                final RevCommit parseCommit;
                parseCommit = new RevWalk(git.getRepository()).parseCommit(gitCommit);
                result.add(GitGraphCommit.builder()
                        .author(parseCommit.getAuthorIdent().getName())
                        .dotText("V")
                        .hash(parseCommit.toObjectId().getName())
                        .commitTime(parseCommit.getAuthorIdent().getWhen())
                        .subject(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(parseCommit.getAuthorIdent().getWhen()) + " - " + parseCommit.getFullMessage())
                        .build());
            }
            Ref head = git.getRepository().getAllRefs().get("HEAD");
            result.stream().filter(gitGraphCommit -> gitGraphCommit.getHash().equals(head.getObjectId().getName())).collect(Collectors.toList()).forEach(gitGraphCommit -> gitGraphCommit.setCheckedOut(true));
            return result;
        } catch (IOException e) {
            throw new ProcessingException("could not plot graph for project ['" + e.getMessage() + "']", e);
        }
    }

    private boolean branchExists(Git git, String branchName) {
        try {
            ListBranchCommand listBranchCommand = git.branchList();
            listBranchCommand.setListMode(ListBranchCommand.ListMode.ALL);
            List<Ref> refs = listBranchCommand.call();
            for (Ref ref : refs) {
                if (ref.getName().contains(branchName)) {
                    return true;
                }
            }
            return false;
        } catch (GitAPIException ex) {
            throw new RuntimeException(ex);
        }
    }

    public void pushChanges(final Project project) {
        try {
            pushChanges("Manual revision created", project);
        } catch (GitAPIException e) {
            throw new ProcessingException("could not push changes for project ['" + e.getMessage() + "']", e);
        }
    }

    private void setRemoteOrigin(final Project project, final Git newLocalRepository) throws IOException, GitAPIException {
        StoredConfig config = newLocalRepository.getRepository().getConfig();
        final String remoteGitUrl = GIT_REPO_URL.concat(project.getGitUuid());
        project.setRemoteGitUrl(remoteGitUrl);
        config.setString("remote", "origin", "url", remoteGitUrl);
        config.save();
    }

    public ProjectChanges checkChanges(Project project) {
        ProjectChanges result = new ProjectChanges();
        try {
            Git git = getGit(project);
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
        } catch (GitAPIException e) {
            e.printStackTrace();
        }
        return result;
    }

    public List<GitGraphBranch> checkoutCommit(final Project project, final String commitId) {
        final Git git = getGit(project);
        try {
            if (!getGit(project).getRepository().getRef(Constants.HEAD).getObjectId().getName().equals(commitId)) {
                git.checkout().setName(commitId).call();
            }
            return plotGitGraph(project);
        } catch (GitAPIException | IOException e) {
            throw new ProcessingException("could not checkout project to commit ['" + commitId + "']", e);
        }
    }

    public boolean isValidSHA1(String s) {
        return s.matches("^[a-fA-F0-9]{40}$");
    }
}
