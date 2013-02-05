package util

import slick.session.Database


case class DatabaseReader(databaseName: String) {

  private lazy val additionalFiles = Configuration.additionalFiles

  def database = Database.forURL("jdbc:sqlite://%s/%s".format(additionalFiles, databaseName), driver = "org.sqlite.JDBC")


}
