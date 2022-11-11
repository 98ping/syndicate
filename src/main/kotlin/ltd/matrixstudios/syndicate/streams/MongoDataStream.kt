package ltd.matrixstudios.syndicate.streams

import com.mongodb.MongoClient
import com.mongodb.MongoClientURI
import com.mongodb.client.MongoCollection
import org.bson.Document

class MongoDataStream(
    var host: String,
    var database: String
) {

    val owningDatabase = useMongoClient().getDatabase(database)

    fun useMongoClient() : MongoClient
    {
        return MongoClient(MongoClientURI(host))
    }

    fun collection(
        collectionName: String
    ) : MongoCollection<Document>
    {
        return owningDatabase.getCollection(collectionName)
    }
}