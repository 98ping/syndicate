package ltd.matrixstudios.syndicate.storage.mongo.sync

import com.mongodb.client.model.Filters
import com.mongodb.client.model.UpdateOptions
import ltd.matrixstudios.syndicate.Syndicate
import ltd.matrixstudios.syndicate.assembly.gson.GsonAssembler
import ltd.matrixstudios.syndicate.objects.IStoreObject
import ltd.matrixstudios.syndicate.repository.sync.ParentRepository
import org.bson.Document
import java.util.UUID

class MongoService<K : IStoreObject>(
    private val dataType: Class<K>
) : ParentRepository<K>(dataType)
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

    override fun findAll(): MutableList<K> {
        val finalItems = mutableListOf<K>()

        for (item in collection.find())
        {
            val value = GsonAssembler.fromJson(item.toJson(), dataType)

            finalItems.add(value)
        }

        return finalItems
    }

    override fun withAllWithTarget(targetField: String, targetValue: Any): MutableList<K> {
        val finalItems = mutableListOf<K>()

        for (item in collection.find(Filters.eq(targetField, targetValue)))
        {
            val value = GsonAssembler.fromJson(item.toJson(), dataType)

            finalItems.add(value)
        }

        return finalItems
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

    override fun findById(id: UUID): K {
        val document = collection.find(Filters.eq("_id", id.toString()))

        return GsonAssembler.fromJson(document.toString(), dataType)
    }

}