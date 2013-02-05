package util

import com.mongodb.casbah.Imports._

case class CollectionCleaner(collection: MongoCollection){

  def clean() {
    collection.dropIndexes()
    collection.dropCollection()
    collection.drop()
  }
}

