package dto

import util.Reader

case class ArtistDTO(id: String, mbid: String, trackId: String, name: String)


object ArtistDTO {

  implicit object ReaderFromFile extends Reader[ArtistDTO] {

    def read(source: Array[String]): ArtistDTO = {
      ArtistDTO(source(0), source(1), source(2), source(3))
    }

  }

}
