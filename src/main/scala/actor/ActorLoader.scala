package actor

import akka.actor.Actor
import akka.pattern.{ask, pipe}
import akka.util.Timeout
import com.mongodb.casbah.Imports._
import util.{Configuration, MongoCollections}
import base._
import concurrent.Future
import scala.slick.driver.SQLiteDriver.simple._
import com.mongodb.casbah.Imports
import file.ArtistDTO


class ActorLoader extends Actor {

  implicit val timeout = Timeout(15000)

  import context.dispatcher

  var counter = 0

  private lazy val mongoClient = MongoClient(Configuration.mongohost)
  private lazy val db = mongoClient("sixtheam")

  private val fileReader = context.actorFor("akka://LoadingSystem/user/fileReader")
  private val fileTransformer = context.actorFor("akka://LoadingSystem/user/fileTransformer")
  private val collectionCleaner = context.actorFor("akka://LoadingSystem/user/collectionCleaner")
  private val writer = context.actorFor("akka://LoadingSystem/user/mongoWriter")
  private val databaseReader = context.actorFor("akka://LoadingSystem/user/databaseReader")
  private val progressListener = context.actorFor("akka://LoadingSystem/user/progressListener")

  def receive = {
    case Go => {
      val artists = for {
        artists <- fetchArtist()
        cleanStatus <- cleanCollection() if cleanStatus == CollectionCleaned
      } yield (artists)

      artists.map {
        artists => {
          progressListener ! StartListener(artists.size)
          artists.map {
            self ! LoadArtist(_)
          }
        }
      }
    }

    case LoadArtist(artist) => {
      val artistDetails = for {
        songs <- readSongsOf(artist)
        similarities <- extractArtistSimilarities(artist)
        tags <- extractTagsAssociatedWithArtist(artist)
        terms <- extractTermsAssociatedWithArtist(artist)
      }
      yield {
        val artistDetailBuilder = MongoDBObject.newBuilder

        artistDetailBuilder += "songs" -> songs
        artistDetailBuilder += "similarities" -> similarities
        artistDetailBuilder += "mbtags" -> tags
        artistDetailBuilder += "terms" -> terms
        artistDetailBuilder.result()
      }

      val artistForMongo = artistDetails.map {
        artistDetails => {
          val artistForMongo = artist.toMongo() ++ artistDetails
          Write(List(artistForMongo), db, MongoCollections.artists)
        }
      }

      artistForMongo.pipeTo(writer)(sender = progressListener)
    }
  }


  /*val locationFromTemp = Locations.byArtistName(artist.name)


  locationFromTemp.map(location => {
    location -= ("artistName")
    artistDetailBuilder += "location" -> location
  })*/


  def cleanCollection(): Future[Message] = {
    ask(collectionCleaner, CleanCollection(db, MongoCollections.artists)).mapTo[Message]
  }

  def fetchArtist(): Future[List[ArtistDTO]] = {
    for {
      artistsRead <- readArtistFile
      artistsDto <- transformArtists(artistsRead)
    } yield (artistsDto.objects)
  }

  def transformArtists(fileLoaded: FileLoaded): Future[Transformed[ArtistDTO]] = {
    ask(fileTransformer, Transform(fileLoaded.objects, ArtistDTO(_))).mapTo[Transformed[ArtistDTO]]
  }

  def readArtistFile: Future[FileLoaded] = {
    ask(fileReader, LoadFile("subset_unique_artists.txt")).mapTo[FileLoaded]
  }

  def readSongsOf(artist: ArtistDTO): Future[Imports.MongoDBList] = {
    def selectSongsOfArtist(artist: ArtistDTO): (Session) => MongoDBList = {
      implicit session => {
        val songs: List[Song] = (for (song <- Songs if song.artistId === artist.id) yield (song)).list()

        songs.foldLeft(MongoDBList.newBuilder)((songs, song) => {

          val songForMongo = Song.toMongo(song)
          songForMongo -= "artistId"
          songForMongo -= "artistMbid"
          songForMongo -= "artistName"

          songs += songForMongo
        }).result()
      }
    }

    ask(databaseReader, Extract("subset_track_metadata.db", selectSongsOfArtist(artist)))
      .mapTo[Extracted[MongoDBList]]
      .map(_.data)
  }

  def extractArtistSimilarities(artist: ArtistDTO): Future[Imports.MongoDBList] = {
    def selectArtistSimilarities(target: ArtistDTO): (Session) => MongoDBList = {
      implicit session => {
        val similars: List[String] = (for (artist <- ArtistSimilarities if artist.target === target.id) yield (artist.similar)).list()

        similars.foldLeft(MongoDBList.newBuilder)((similarity, similar) => {
          similarity += MongoDBObject("id" -> similar)
        }).result()
      }
    }

    ask(databaseReader, Extract("subset_artist_similarity.db", selectArtistSimilarities(artist)))
      .mapTo[Extracted[Imports.MongoDBList]]
      .map(_.data)
  }

  def extractTagsAssociatedWithArtist(artist: ArtistDTO): Future[Imports.MongoDBList] = {
    def selectTagsAssociatedWithArtist(artist: ArtistDTO): (Session) => MongoDBList = {
      implicit session => {
        val tags: List[String] = (for (tagByArtist <- TagsByArtist if tagByArtist.artistId === artist.id) yield (tagByArtist.tag)).list()

        tags.foldLeft(MongoDBList.newBuilder)((tags, tag) => {
          tags += MongoDBObject("mbtag" -> tag)
          tags
        }).result()
      }
    }

    ask(databaseReader, Extract("subset_artist_term.db", selectTagsAssociatedWithArtist(artist)))
      .mapTo[Extracted[Imports.MongoDBList]]
      .map(_.data)

  }

  def extractTermsAssociatedWithArtist(artist: ArtistDTO): Future[Imports.MongoDBList] = {

    def selectTermsAssociatedWithArtist(artist: ArtistDTO): (Session) => MongoDBList = {
      implicit session => {
        val terms: List[String] = (for (termByArtist <- TermsByArtist if termByArtist.artistId === artist.id) yield (termByArtist.term)).list()

        terms.foldLeft(MongoDBList.newBuilder)((terms, term) => {
          terms += MongoDBObject("term" -> term)
          terms
        }).result()
      }
    }

    ask(databaseReader, Extract("subset_artist_term.db", selectTermsAssociatedWithArtist(artist)))
      .mapTo[Extracted[Imports.MongoDBList]]
      .map(_.data)
  }
}