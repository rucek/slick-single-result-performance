enablePlugins(JmhPlugin)

name := "slick-single-result-performance"

version := "1.0"

scalaVersion := "2.12.1"

resolvers += Resolver.sonatypeRepo("releases")

libraryDependencies ++= Seq(
  "com.typesafe.slick" %% "slick" % "3.2.0",
  "com.h2database" % "h2" % "1.4.194",
  "com.danielasfregola" %% "random-data-generator" % "2.0",
  "org.slf4j" % "slf4j-nop" % "1.7.25"
)
