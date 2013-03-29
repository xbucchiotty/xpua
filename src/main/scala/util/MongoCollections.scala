package util


trait MongoCollection {
  def name(): String
}

object MongoCollections {

  val tags = new MongoCollection {
    def name() = "tags"
  }

  val terms = new MongoCollection {
    def name() = "terms"
  }

  val songs = new MongoCollection {
    def name() = "songs"
  }

  val similaritites = new MongoCollection {
    def name() = "similaritites"
  }

  val locations = new MongoCollection {
    def name() = "locations"
  }

  val years = new MongoCollection {
    def name() = "years"
  }

  val tracks = new MongoCollection {
    def name() = "tracks"
  }

  val artists = new MongoCollection {
    def name() = "artists"
  }
}
