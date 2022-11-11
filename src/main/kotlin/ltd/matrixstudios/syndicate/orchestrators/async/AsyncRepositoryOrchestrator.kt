package ltd.matrixstudios.syndicate.orchestrators.async

import ltd.matrixstudios.syndicate.objects.IStoreObject
import ltd.matrixstudios.syndicate.repository.async.AsyncParentRepository
import ltd.matrixstudios.syndicate.repository.sync.ParentRepository
import ltd.matrixstudios.syndicate.storage.mongo.async.AsyncMongoService
import ltd.matrixstudios.syndicate.storage.mongo.sync.MongoService
import ltd.matrixstudios.syndicate.types.async.AsyncStoreType
import ltd.matrixstudios.syndicate.types.sync.SyncStoreType
import java.lang.IllegalArgumentException

object AsyncRepositoryOrchestrator
{
    val asyncRepositoryCache: HashMap<Class<*>, AsyncParentRepository<*>> = hashMapOf()

    fun <T : IStoreObject> createAsyncRepository(
        classFor: Class<T>,
        type: AsyncStoreType
    ) : AsyncParentRepository<T>
    {
        var repositoryToCreate: AsyncParentRepository<T>? = null

        when (type)
        {
            AsyncStoreType.ASYNC_MONGO ->
            {
                repositoryToCreate = AsyncMongoService<T>(classFor)
            }
        }

        asyncRepositoryCache[classFor] = repositoryToCreate

        return repositoryToCreate
    }

    @Suppress("UNCHECKED_CAST")
    fun <T : IStoreObject> findAsyncNotNull(
        classFor: Class<T>
    ) : AsyncParentRepository<T>
    {
        if (!asyncRepositoryCache.containsKey(classFor))
        {
            throw IllegalArgumentException("No async service could be located for this class!")
        }

        return asyncRepositoryCache[classFor]!! as AsyncParentRepository<T>
    }
}