package net.nemerosa.ontrack.git.exceptions

import org.eclipse.jgit.api.errors.GitAPIException

class GitRepositoryAPIException(remote: String, ex: GitAPIException) : GitRepositoryException(ex, "Git API exception on %s", remote)
