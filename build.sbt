import ScalaxbKeys._

lazy val commonSettings = Seq(
  organization  := "com.mapr",
  scalaVersion  := "2.11.7"
)

lazy val sparkVersion = "1.3.1"

lazy val root = (project in file(".")).
  settings(commonSettings: _*).
  settings(
    name := "Spark-XML-Ingest",
    libraryDependencies ++= Seq(
      // "provided" means that these libraries won't be included in the jar when built.
      // The systems will be expected to have the spark libs on the classpath
      "org.slf4j" % "slf4j-api" % "1.7.7",
      "org.apache.spark" %% "spark-core" % sparkVersion % "provided",
      "org.apache.spark" %% "spark-streaming" % sparkVersion % "provided",
      "org.apache.spark" %% "spark-sql" % sparkVersion % "provided",
      // Use 1 % after the group specification to denote that you don't care
      // about getting the scala-version specific package
      "org.apache.mahout" % "mahout-examples" % "0.10.0",
      "org.json" % "json" % "20151123"
      )
  ).
  settings(scalaxbSettings: _*).
  settings(net.virtualvoid.sbt.graph.Plugin.graphSettings: _*).
  settings(ivyScala := ivyScala.value map { _.copy(overrideScalaVersion = true) })