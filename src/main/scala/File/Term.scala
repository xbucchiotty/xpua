package file

import util.{Writer, Reader}
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


