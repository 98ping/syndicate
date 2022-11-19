package ltd.matrixstudios.syndicate

import ltd.matrixstudios.syndicate.builders.MongoCharacteristicBuilder
import ltd.matrixstudios.syndicate.objects.IStoreObject
import ltd.matrixstudios.syndicate.streams.MongoDataStream

object Syndicate
{
    var stream = MongoCharacteristicBuilder.makeDefaultStream()
}