name := """auth-microservice"""

version := "1.0-SNAPSHOT"

lazy val root = (project in file(".")).enablePlugins(PlayScala)

resolvers += Resolver.sonatypeRepo("snapshots")
resolvers += "typesave" at "http://repo.typesafe.com/typesafe/releases"

scalaVersion := "2.12.4"

libraryDependencies ++= {

  val slickVersion = "3.0.0"

  Seq(
    ws,
    guice,
    "com.jason-goodwin"       %% "authentikat-jwt"            % "0.4.5",
    "com.github.t3hnar"       %% "scala-bcrypt"               % "3.0",
    "com.typesafe.play"       %% "play-slick"                 % slickVersion,
    "com.typesafe.play"       %% "play-slick-evolutions"      % slickVersion,
    "com.h2database"           % "h2"                         % "1.4.196",
    "org.postgresql"           % "postgresql"                 % "42.2.1",
    "net.debasishg"            % "redisclient_2.11"           % "3.5",
    "org.scalatestplus.play"  %% "scalatestplus-play"         % "3.1.2"           % Test
  )
}

