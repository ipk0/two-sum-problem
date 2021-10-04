enablePlugins(PackPlugin)

name := "2sum-problem"
version := "0.1"
scalaVersion := "2.12.12"

lazy val Versions = new {
  val akka = "2.5.26"
  val akkaHttp = "10.0.15"
  val spray = "1.3.6"
  val scalaLogging = "3.9.2"
  val logback = "1.1.3"
  val scalatest = "3.0.9"
}

libraryDependencies ++= Seq(
  "com.typesafe.akka" %% "akka-actor" % Versions.akka,
  "com.typesafe.akka" %% "akka-stream" % Versions.akka,
  "com.typesafe.akka" %% "akka-http" % Versions.akkaHttp,
  "io.spray" %%  "spray-json" % Versions.spray,
  "com.typesafe.akka" %% "akka-http-spray-json" % Versions.akkaHttp,
  "com.typesafe.scala-logging" %% "scala-logging" % Versions.scalaLogging,
  "ch.qos.logback" % "logback-classic" % Versions.logback,
  "com.typesafe.akka" %% "akka-stream-testkit" % Versions.akka,
  "com.typesafe.akka" %% "akka-http-testkit" % Versions.akkaHttp,
  "org.scalatest" %% "scalatest" % Versions.scalatest % Test
)
