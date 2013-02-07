package file

import com.mongodb.casbah.Imports._


object Years {
  def byTrackId(trackId: String): MongoDBObject = {
    MongoDBObject("trackId" -> trackId)
  }
}