package file

import _root_.util.{Writer, Reader}
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

