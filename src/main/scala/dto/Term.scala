package dto

import util.{MongoCollections, CollectionCleaner, Writer, Reader}
import com.mongodb.casbah.Imports._

case class Term(value: String)

object Term {

  implicit object ReaderFromFile extends Reader[Term] {
    def read(source: Array[String]): Term = {
      Term(source(0))
    }
  }

  implicit object ToMongo extends Writer[Term, DBObject] {
    def apply(term: Term): DBObject = {
      MongoDBObject("term" -> term.value)
    }
  }

}

case class Terms(db: MongoDB) {

  private lazy val terms = db(MongoCollections.terms)

  def load() {
    CollectionCleaner(terms).clean()
    util.FileReader("subset_unique_terms.txt").parse {
      term: Term => terms += term
    }
    println("[OK] : terms (%s elements)".format(terms.size))
  }

}
