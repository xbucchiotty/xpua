package util

object Configuration {

  lazy val additionalFiles = sys.props.get("additionalFiles").getOrElse("/Users/xbucchiotty/Documents/Travail/Scala/XPUA/xpua/AdditionalFiles")
  lazy val mongohost = sys.props.get("mongo.host").getOrElse("localhost")
}
