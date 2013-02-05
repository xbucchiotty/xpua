addSbtPlugin("com.github.mpeltonen" % "sbt-idea" % "1.1.0")

resolvers += Resolver.url(
  "http://scalasbt.artifactoryonline.com/scalasbt/sbt-plugin-releases/",
  new URL("http://scalasbt.artifactoryonline.com/scalasbt/sbt-plugin-releases/")
)(Resolver.ivyStylePatterns)

addSbtPlugin("com.github.retronym" % "sbt-onejar" % "0.8")
