package ltd.matrixstudios.syndicate.orchestrators.sync

import ltd.matrixstudios.syndicate.objects.IStoreObject
import ltd.matrixstudios.syndicate.repository.async.AsyncParentRepository
import ltd.matrixstudios.syndicate.repository.sync.ParentRepository
import ltd.matrixstudios.syndicate.storage.json.sync.JsonSyncService
import ltd.matrixstudios.syndicate.storage.mongo.sync.MongoService
import ltd.matrixstudios.syndicate.types.sync.SyncStoreType
import java.lang.IllegalArgumentException

object SyncRepositoryOrchestrator
{
    val syncRepositoryCache: HashMap<Class<*>, ParentRepository<*>> = hashMapOf()

    val asyncRepositoryCache: HashMap<Class<*>, AsyncParentRepository<*>> = hashMapOf()

    fun <T : IStoreObject> createSyncRepository(
        classFor: Class<T>,
        type: SyncStoreType
    ) : ParentRepository<T>
    {
        var repositoryToCreate: ParentRepository<T>? = null

        repositoryToCreate = when (type) {
            SyncStoreType.MONGO_SYNC -> {
                MongoService<T>(classFor)
            }

            SyncStoreType.JSON_SYNC -> {
                JsonSyncService<T>(classFor)
            }
        }

        return repositoryToCreate.apply {
            if (this != null)
            {
                syncRepositoryCache[classFor] = this
            }
        }
    }

    @Suppress("UNCHECKED_CAST")
    fun <T : IStoreObject> findSyncNotNull(
        classFor: Class<T>
    ) : ParentRepository<T>
    {
       if (!syncRepositoryCache.containsKey(classFor))
       {
           throw IllegalArgumentException("No sync service could be located for this class!")
       }

        return syncRepositoryCache[classFor]!! as ParentRepository<T>
    }
}