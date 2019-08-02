package net.nemerosa.ontrack.git.exceptions

import java.io.IOException

class GitRepositoryInitException(e: IOException) : GitRepositoryException(e, "Cannot initialise Git repository.")
