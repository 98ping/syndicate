package ltd.matrixstudios.syndicate.builders

import com.mongodb.client.MongoCollection
import ltd.matrixstudios.syndicate.streams.MongoDataStream
import org.bson.Document

class MongoCharacteristicBuilder
{
    private var host: String = "mongodb://localhost:27017"
    private var database: String = "Syndicate"

    companion object
    {
        @JvmStatic
        fun uri(
            uri: String
        ) : MongoCharacteristicBuilder
        {
            return MongoCharacteristicBuilder().also {
                it.host = uri
            }
        }

        @JvmStatic
        fun makeDefaultStream() : MongoDataStream
        {
            return MongoDataStream(
                "mongodb://localhost:27017",
                "Syndicate"
            )
        }
    }


    fun withDatabase(
        database: String
    ) : MongoCharacteristicBuilder
    {
        return this.also {
            this.database = database
        }
    }

    fun returnMongoStream() : MongoDataStream
    {
        return MongoDataStream(host, database)
    }
}