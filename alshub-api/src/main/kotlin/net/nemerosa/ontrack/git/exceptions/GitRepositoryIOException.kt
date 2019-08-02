package net.nemerosa.ontrack.git.exceptions

import java.io.IOException

class GitRepositoryIOException(remote: String, ex: IOException) : GitRepositoryException(ex, "Git IO exception on %s", remote)
