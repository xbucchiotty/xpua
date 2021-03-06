name := "sixtheam-feed"

version := "1.0"

scalaVersion := "2.10.0"

resolvers += "Typesafe repository" at "http://repo.typesafe.com/typesafe/releases/"

libraryDependencies += "org.mongodb" %% "casbah" % "2.5.0"

libraryDependencies += "org.xerial" % "sqlite-jdbc" % "3.7.2"

libraryDependencies += "com.typesafe.slick" %% "slick" % "1.0.0-RC2"

libraryDependencies +=
  "com.typesafe.akka" %% "akka-actor" % "2.1.0"

fork in run := true

javaOptions in run += "-Xmx1G -XX:MaxPermSize=128m"

seq(com.github.retronym.SbtOneJar.oneJarSettings: _*)

libraryDependencies += "commons-lang" % "commons-lang" % "2.6"
