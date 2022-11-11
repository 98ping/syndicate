package ltd.matrixstudios.syndicate.repository.async

import ltd.matrixstudios.syndicate.objects.IStoreObject
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono
import java.util.HashMap
import java.util.UUID
import java.util.concurrent.CompletableFuture

abstract class AsyncParentRepository<K : IStoreObject>(
    private val dataType: Class<K>
)
{
    val localCache: HashMap<UUID, K> = hashMapOf()

    abstract fun save(value: K)
    abstract fun findById(id: UUID) : Mono<K?>

    abstract fun findAll() : Flux<K>

    abstract fun loadToLocalCache()
}