package file


case class ArtistDTO(id: String, mbid: String, trackId: String, name: String)


object ArtistDTO {


    def read(source: Array[String]): ArtistDTO = {
      ArtistDTO(source(0), source(1), source(2), source(3))

  }

}
