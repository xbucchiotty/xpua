package actor

import com.mongodb.casbah.Imports._
import util.MongoCollections
import akka.util.Timeout
import file.Term

case class TermsLoaderActor(db: MongoDB) extends MongoLoaderActor {

  private lazy val terms = db(MongoCollections.terms)
  implicit val timeout = Timeout(1000)

  def write() {
    util.FileReader("subset_unique_terms.txt").parse {
      term: Term => terms += term
    }
  }

  protected def mongoCollectionName: String = MongoCollections.terms
}
