package dto

import util.{MongoCollections, CollectionCleaner, Writer, Reader}
import com.mongodb.casbah.Imports._

case class Tag(value: String)

object Tag {

  implicit object ReaderFromFile extends Reader[Tag] {
    def read(source: Array[String]): Tag = {
      Tag(source(0))
    }
  }

  implicit object ToMongo extends Writer[Tag, DBObject] {
    def apply(tag: Tag): DBObject = {
      MongoDBObject("tag" -> tag.value)
    }
  }

}

case class Tags(db: MongoDB) {

  private lazy val tags = db(MongoCollections.tags)

  def load() {
    CollectionCleaner(tags).clean()
    util.FileReader("subset_unique_mbtags.txt").parse {
      tag: Tag => tags += tag
    }
    println("[OK] : tags (%s elements)".format(tags.size))
  }

}