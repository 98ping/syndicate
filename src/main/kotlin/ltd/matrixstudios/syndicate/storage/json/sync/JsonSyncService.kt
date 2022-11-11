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
    private var destinationFile: String? = null

    private val type: Type = object : TypeToken<MutableList<K>>() {}.type

    private val file = File(destinationFile + (dataType.simpleName + "-configuration.json"))

    val cache: HashMap<UUID, K> = hashMapOf()

    private fun writeBlankJson()
    {
        if (!file.exists())
        {
            file.createNewFile()
        }

        Files.write(GsonAssembler.gson.toJson(cache.values), file, Charsets.UTF_8)
    }

    override fun save(value: K) {
        cache[value.id] = value

        if (!file.exists())
        {
            file.createNewFile()
        }

        Files.write(GsonAssembler.gson.toJson(cache.values), file, Charsets.UTF_8)
    }

    override fun findById(id: UUID): K? {
        if (cache.containsKey(id)) return cache[id]

        for (item in findAll())
        {
            if (item.id.toString() == id.toString())
            {
                return item
            }
        }

        return null
    }

    override fun findAll(): MutableList<K> {
        val reader = Files.newReader(file, Charsets.UTF_8)

        val items = mutableListOf<K>()

        if (reader != null) {
            val elements = GsonAssembler.gson.fromJson<MutableList<K>>(reader, type)

            items.addAll(elements)
        } else {
            writeBlankJson()
        }

        return items
    }

    override fun loadToLocalCache() {
        findAll().forEach {
            cache[it.id] = it
        }
    }

    fun defineFile(location: String) : JsonSyncService<K>
    {
        return this.apply {
            this.destinationFile = location
        }
    }

}