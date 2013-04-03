package actor

import akka.actor.Actor
import util.Configuration
import com.mongodb.casbah.Imports._


trait MongoCollectionActor extends Actor {

  private lazy val db = Configuration.db

  protected lazy val coll = db(this.name)

  protected val name: String

  def indexCollection()


  def receive = {
    case Write(objects: List[MongoDBObject]) => {
      objects.map(obj => coll += obj)
      sender tell(Done(s"$name"), self)
    }

    case FindOne(query: MongoDBObject) => {
      val result = coll.findOne(query)
      sender tell(result, self)
    }

    case Find(query: MongoDBObject) => {
      sender tell(coll.find(query), self)
    }
  }


  override def preStart() {
    super.preStart()
    println(s"Preparing collection $name")

    coll.drop()
    coll.dropIndexes()

    indexCollection()
  }

  override def postStop() {
    super.postStop()
    println(s"Collection $name:. ${coll.count()} elements")
  }
}