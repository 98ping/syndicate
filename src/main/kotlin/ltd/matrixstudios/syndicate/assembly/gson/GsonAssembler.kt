package ltd.matrixstudios.syndicate.assembly.gson

import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.google.gson.LongSerializationPolicy
import ltd.matrixstudios.syndicate.objects.IStoreObject
import org.bson.Document

object GsonAssembler
{
    var gson: Gson = GsonBuilder()
        .serializeNulls()
        .setLongSerializationPolicy(LongSerializationPolicy.STRING)
        .create()

    fun useAndRebuild(
        body: (Gson) -> Unit
    ) : GsonBuilder
    {
        body.invoke(gson)
        return gson.newBuilder()
    }

    fun <T: IStoreObject> toJson(
        value: T
    ) : String
    {
        return gson.toJson(value)
    }

    fun <T: IStoreObject> fromJson(
        value: String,
        type: Class<T>
    ) : T
    {
        return gson.fromJson(value, type)
    }

    fun <T : IStoreObject> listToObjects(
        entries: List<Document>,
        clazz: Class<T>
    ) : MutableList<T>
    {
        return mutableListOf<T>().apply {
            for (entry in entries)
            {
                this.add(fromJson(entry.toJson(), clazz))
            }
        }
    }
}