package ltd.matrixstudios.syndicate.storage

import com.mongodb.client.model.Filters
import com.mongodb.client.model.UpdateOptions
import com.mongodb.client.result.UpdateResult
import ltd.matrixstudios.syndicate.Syndicate
import ltd.matrixstudios.syndicate.assembly.gson.GsonAssembler
import ltd.matrixstudios.syndicate.objects.IStoreObject
import org.bson.Document
import java.util.UUID

class MongoService<K : IStoreObject>(
    private val dataType: Class<K>
)
{
    val localCache: HashMap<UUID, K> = hashMapOf()

    private val collection = Syndicate.stream.collection(dataType.simpleName)

    fun depositToCache()
    {
        GsonAssembler.listToObjects(
            collection.find().into(arrayListOf()), dataType
        ).apply {
            for (item in this)
            {
                localCache[item.id] = item
            }
        }
    }

    fun findAll() : MutableList<K>
    {
        val finalItems = mutableListOf<K>()

        for (item in collection.find())
        {
            val value = GsonAssembler.fromJson(item.toJson(), dataType)

            finalItems.add(value)
        }

        return finalItems
    }

    fun save(value: K) : UpdateResult
    {
        val json = GsonAssembler.toJson(value)

        return collection.updateOne(
            Filters.eq(
                "_id",
                value.id
            ),
            Document("\$set",
                Document.parse(json)
            ),
            UpdateOptions().upsert(true))
    }
}