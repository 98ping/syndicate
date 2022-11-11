package ltd.matrixstudios.syndicate.orchestrators

import ltd.matrixstudios.syndicate.objects.IStoreObject
import ltd.matrixstudios.syndicate.storage.MongoService
import java.lang.IllegalArgumentException

object RepositoryOrchestrator
{
    val cache: HashMap<Class<*>, MongoService<*>> = hashMapOf()

    fun <T : IStoreObject> createService(
        classFor: Class<T>,
        sync: Boolean
    ) : MongoService<T>
    {
        return MongoService(classFor).apply {
            cache[classFor] = this
        }
    }

    @Suppress("UNCHECKED_CAST")
    fun <T : IStoreObject> findNotNull(
        classFor: Class<T>
    ) : MongoService<T>
    {
       if (!cache.containsKey(classFor))
       {
           throw IllegalArgumentException("No service could be located for this class!")
       }

        return cache[classFor]!! as MongoService<T>
    }
}