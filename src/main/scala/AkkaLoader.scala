import actor.{Go, Worker}
import akka.actor.{Props, ActorSystem}
import com.mongodb.casbah.Imports._
import util.Configuration


object AkkaLoader extends App {
  val system = ActorSystem("LoadingSystem")

  lazy val mongoClient = MongoClient(Configuration.mongohost)
  lazy val db = mongoClient("sixtheam")

  val worker = system.actorOf(Props(new Worker(db)), "mainWorker")
  worker ! Go
}