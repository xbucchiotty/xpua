package model

import util.{Splitter, Reader}

case class ArtistDTO(id: String, hash: String, trackId: String, name: String)


object ArtistDTO {

  implicit object ReaderFromFile extends Reader[ArtistDTO] {

    def read(source: Array[String]): ArtistDTO = {
      ArtistDTO(source(0), source(1), source(2), source(3))
    }

  }

}
