package ltd.matrixstudios.syndicate.repository.sync

import ltd.matrixstudios.syndicate.objects.IStoreObject
import java.util.HashMap
import java.util.UUID

abstract class ParentRepository<K : IStoreObject>(
    private val dataType: Class<K>
)
{
    val localCache: HashMap<UUID, K> = hashMapOf()

    abstract fun save(value: K)
    abstract fun findById(id: UUID) : K?

    abstract fun findAll() : MutableList<K>
}