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
import net.nemerosa.ontrack.git.model.GitLog;
import net.nemerosa.ontrack.git.support.GitRepositoryClientFactoryImpl;
import org.eclipse.jgit.api.*;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.lib.ObjectId;
import org.eclipse.jgit.lib.Ref;
import org.eclipse.jgit.lib.RepositoryCache;
import org.eclipse.jgit.lib.StoredConfig;
import org.eclipse.jgit.revplot.PlotCommitList;
import org.eclipse.jgit.revplot.PlotLane;
import org.eclipse.jgit.revplot.PlotWalk;
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
import java.time.format.DateTimeFormatter;
import java.util.*;
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
            pushChanges(newLocalRepository, "created local project repository", project);
        } catch (IOException | GitAPIException e) {
            e.printStackTrace();
        }
    }

    public void pushChanges(final Git newLocalRepository, final String commitMessage, final Project project) throws GitAPIException {
        newLocalRepository.add().addFilepattern(".").call();
        try {
            final String commitName = newLocalRepository.getRepository().getBranch();
            if (isValidSHA1(commitName)) {
                final String newBranchName = "branched_" + commitName;
                if (!branchExists(newLocalRepository, newBranchName)) {
                    Ref ref = newLocalRepository.checkout().
                            setCreateBranch(true).
                            setName(newBranchName).
                            setUpstreamMode(CreateBranchCommand.SetupUpstreamMode.TRACK).
                            setForce(true)
                            .call();
                } else {
                    Ref ref = newLocalRepository.checkout().
                            setName(newBranchName)
                            .setForce(true)
                            .call();
                }
            }
            newLocalRepository.commit()
                    .setMessage(commitMessage)
                    .setCommitter("auto", "commit")
                    .setAll(true)
                    .call();
            PushCommand pushCommand = newLocalRepository.push();

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
        Collections.reverse(commits);
        return commits;
    }

    public List<GitGraphCommit> plotGitGraph(final Project project) {
        final Git newLocalRepository = getGit(project);
        final GitRepositoryClient source = new GitRepositoryClientFactoryImpl(new File(project.getPath())).getClient(new GitRepository("source", project.getGitUuid(), GIT_REPO_URL.concat(project.getGitUuid()), "", ""));
        RevCommit youngestCommit = null;
        RevCommit oldestCommit = null;
        List<Ref> branches = null;
        try (RevWalk walk = new RevWalk(newLocalRepository.getRepository())) {
            branches = newLocalRepository.branchList().setListMode(ListBranchCommand.ListMode.ALL).call();
            for (Ref branch : branches) {
                RevCommit commit = walk.parseCommit(branch.getObjectId());
                for (RevCommit parentCommit : getCommits(newLocalRepository, branch)) {
                    parentCommit = walk.parseCommit(parentCommit.toObjectId());
                    if (youngestCommit == null || parentCommit.getAuthorIdent().getWhen().compareTo(
                            youngestCommit.getAuthorIdent().getWhen()) > 0)
                        youngestCommit = parentCommit;
                    if (oldestCommit == null || parentCommit.getAuthorIdent().getWhen().compareTo(
                            oldestCommit.getAuthorIdent().getWhen()) < 0)
                        oldestCommit = parentCommit;
                }
            }
            return convertToGitGraphCommit(source.graph(youngestCommit.getName(), oldestCommit.getName()),newLocalRepository);
        } catch (Exception e) {
            throw new ProcessingException("could not plot graph for project ['" + e.getMessage() + "']", e);
        }
    }

    private List<GitGraphCommit> convertToGitGraphCommit(final GitLog graph,Git git) {
         final List<GitGraphCommit> result = new ArrayList<GitGraphCommit>() ;
        graph.getCommits().forEach(gitCommit -> {
            result.add(GitGraphCommit.builder()
                    .author(gitCommit.getAuthor().getName())
                    .dotText("V")
                    .hash(gitCommit.getId())
                    .subject(gitCommit.getCommitTime().format( DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"))+ " - "+gitCommit.getFullMessage())
                    .build());
        });
        Ref head = git.getRepository().getAllRefs().get("HEAD");
        result.stream().filter(gitGraphCommit -> gitGraphCommit.getHash().equals(head.getObjectId().getName())).collect(Collectors.toList()).forEach(gitGraphCommit -> gitGraphCommit.setCheckedOut(true));
        return result;
    }


    public void test(Git git) {
        Ref head = null;
        try {
            PlotWalk revWalk = new PlotWalk(git.getRepository());
            ObjectId rootId = git.getRepository().resolve("refs/heads/master");
            RevCommit root = revWalk.parseCommit(rootId);
            revWalk.markStart(root);
            PlotCommitList<PlotLane> plotCommitList = new PlotCommitList<>();
            plotCommitList.source(revWalk);
            plotCommitList.fillTo(Integer.MAX_VALUE);

            System.out.println("Printing children of commit " + root);
            for (RevCommit com : revWalk) {
                System.out.println("Child: " + com);
            }

            System.out.println("Printing with next()");
            System.out.println("next: " + revWalk.next());

        } catch (IOException e) {
            e.printStackTrace();
        }
        // a RevWalk allows to walk over commits based on some filtering that is defined


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
            pushChanges(getGit(project), "Manual revision created", project);

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

    public List<GitGraphCommit> checkoutCommit(final Project project, final String commitId) {
        final Git git = getGit(project);
        try {
            git.checkout().setName(commitId).call();
            return plotGitGraph(project);
        } catch (GitAPIException e) {
            throw new ProcessingException("could not checkout project to commit ['" + commitId + "']", e);
        }
    }

    public boolean isValidSHA1(String s) {
        return s.matches("^[a-fA-F0-9]{40}$");
    }
}
