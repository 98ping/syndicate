package ltd.matrixstudios.syndicate.storage.mongo.async

import com.mongodb.client.model.Filters
import com.mongodb.client.model.UpdateOptions
import ltd.matrixstudios.syndicate.Syndicate
import ltd.matrixstudios.syndicate.assembly.gson.GsonAssembler
import ltd.matrixstudios.syndicate.objects.IStoreObject
import ltd.matrixstudios.syndicate.repository.async.AsyncParentRepository
import ltd.matrixstudios.syndicate.repository.sync.ParentRepository
import org.bson.Document
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono
import java.util.UUID

class AsyncMongoService<K : IStoreObject>(
    private val dataType: Class<K>
) : AsyncParentRepository<K>(dataType)
{
    private val collection = Syndicate.stream.collection(dataType.simpleName)

    override fun loadToLocalCache() {
        GsonAssembler.listToObjects(
            collection.find().into(arrayListOf()), dataType
        ).apply {
            for (item in this)
            {
                localCache[item.id] = item
            }
        }
    }

    override fun findAll(): Flux<K> {
        val finalItems = mutableListOf<K>()

        for (item in collection.find())
        {
            val value = GsonAssembler.fromJson(item.toJson(), dataType)

            finalItems.add(value)
        }

        return Flux.fromIterable(finalItems)
    }

    override fun withAllWithTarget(targetField: String, targetValue: Any): Flux<K> {
        val finalItems = mutableListOf<K>()

        for (item in collection.find(Filters.eq(targetField, targetValue)))
        {
            val value = GsonAssembler.fromJson(item.toJson(), dataType)

            finalItems.add(value)
        }

        return Flux.fromIterable(finalItems)
    }

    override fun save(value: K) {
        val json = GsonAssembler.toJson(value)

        collection.updateOne(
            Filters.eq(
                "_id",
                value.id
            ),
            Document("\$set",
                Document.parse(json)
            ),
            UpdateOptions().upsert(true))
    }

    override fun findById(id: UUID): Mono<K?> {
        val document = collection.find(Filters.eq("_id", id.toString()))

        val item = GsonAssembler.fromJson(document.toString(), dataType)

        return Mono.just(item)
    }
}