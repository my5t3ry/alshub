package net.nemerosa.ontrack.git.exceptions

class GitRepositoryCannotCloneException(remote: String) : GitRepositoryException("Repository for %s cloning did not succeed.", remote)
