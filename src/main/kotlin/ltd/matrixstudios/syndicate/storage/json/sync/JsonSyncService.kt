package ltd.matrixstudios.syndicate.storage.json.sync

import com.google.common.io.Files
import com.google.common.primitives.Chars
import com.google.gson.reflect.TypeToken
import ltd.matrixstudios.syndicate.assembly.gson.GsonAssembler
import ltd.matrixstudios.syndicate.objects.IStoreObject
import ltd.matrixstudios.syndicate.repository.sync.ParentRepository
import java.io.File
import java.lang.reflect.Type
import java.util.*
import kotlin.collections.HashMap

class JsonSyncService<K : IStoreObject>(
    private val dataType: Class<K>
) : ParentRepository<K>(dataType)
{
    private var destinationFile: File? = null

    private val type: Type = object : TypeToken<MutableList<K>>() {}.type

    private val file = File(destinationFile, dataType.simpleName + "-configuration.json")

    private fun writeBlankJson()
    {
        if (!file.exists())
        {
            file.createNewFile()
        }

        Files.write(GsonAssembler.gson.toJson(localCache.values), file, Charsets.UTF_8)
    }

    override fun save(
        value: K
    )
    {
        localCache[value.id] = value

        if (!file.exists())
        {
            file.createNewFile()
        }

        Files.write(GsonAssembler.gson.toJson(localCache.values), file, Charsets.UTF_8)
    }

    override fun findById(
        id: UUID
    ): K?
    {
        if (localCache.containsKey(id)) return localCache[id]

        for (item in findAll())
        {
            if (item.id.toString() == id.toString())
            {
                return item
            }
        }

        return null
    }

    override fun findAll(): MutableList<K>
    {
        val reader = Files.newReader(file, Charsets.UTF_8)

        val items = mutableListOf<K>()

        if (reader != null)
        {
            val elements = GsonAssembler.gson.fromJson<MutableList<K>>(reader, type)

            items.addAll(elements)
        } else {
            writeBlankJson()
        }

        return items
    }

    fun defineFile(
        location: File
    ) : JsonSyncService<K>
    {
        return this.apply {
            this.destinationFile = location
        }
    }

}