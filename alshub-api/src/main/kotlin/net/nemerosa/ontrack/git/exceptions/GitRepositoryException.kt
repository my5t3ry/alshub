package net.nemerosa.ontrack.git.exceptions

import net.nemerosa.ontrack.common.BaseException

abstract class GitRepositoryException : BaseException {

    constructor(pattern: String, vararg parameters: Any) : super(pattern, *parameters) {}

    constructor(ex: Exception, pattern: String, vararg parameters: Any) : super(ex, pattern, *parameters) {}
}
