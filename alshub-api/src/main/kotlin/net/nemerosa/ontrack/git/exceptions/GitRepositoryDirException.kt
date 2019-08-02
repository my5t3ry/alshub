package net.nemerosa.ontrack.git.exceptions

import java.io.File
import java.io.IOException

class GitRepositoryDirException(dir: File, ex: IOException) : GitRepositoryException(ex, "Cannot prepare repository directory at %s", dir.path)
