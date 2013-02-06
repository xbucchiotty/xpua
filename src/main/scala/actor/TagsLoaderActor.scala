package actor

import com.mongodb.casbah.Imports._
import util.{FileReader, MongoCollections}
import akka.util.Timeout
import file.Tag

case class TagsLoaderActor(db: MongoDB) extends MongoLoaderActor {

  private lazy val tags = db(MongoCollections.tags)
  implicit val timeout = Timeout(1000)

  def write() {
    FileReader("subset_unique_mbtags.txt").parse {
      tag: Tag => tags += tag
    }
  }

  protected def mongoCollectionName: String = MongoCollections.tags
}
