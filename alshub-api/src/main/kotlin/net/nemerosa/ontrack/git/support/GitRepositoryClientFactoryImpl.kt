package net.nemerosa.ontrack.git.support

import net.nemerosa.ontrack.git.GitRepository
import net.nemerosa.ontrack.git.GitRepositoryClient
import net.nemerosa.ontrack.git.GitRepositoryClientFactory
import net.nemerosa.ontrack.git.exceptions.GitRepositoryDirException
import org.apache.commons.io.FileUtils
import java.io.File
import java.io.IOException
import java.util.concurrent.locks.ReentrantLock



class GitRepositoryClientFactoryImpl(private val root: File) : GitRepositoryClientFactory {

    private val lock = ReentrantLock()

    override fun getClient(repository: GitRepository): GitRepositoryClient {
        val remote = repository.remote
        lock.lock()
        try {
            // Gets any existing repository in the cache
            return createAndRegisterRepositoryClient(repository)
        } finally {
            lock.unlock()
        }
    }

    public fun createAndRegisterRepositoryClient(repository: GitRepository): GitRepositoryClient {
        val client = createRepositoryClient(repository)
        return client
    }

    protected fun createRepositoryClient(repository: GitRepository): GitRepositoryClient {
        // ID for this repository
        val repositoryId = repository.id
        // Directory for this repository
        val repositoryDir = File(root, repositoryId)
        // Makes sure the directory is ready
        try {
            FileUtils.forceMkdir(repositoryDir)
        } catch (ex: IOException) {
            throw GitRepositoryDirException(repositoryDir, ex)
        }

        // Creates the client
        return GitRepositoryClientImpl(repositoryDir, repository)
    }

}
