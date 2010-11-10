import sbt._
class Plugins(info: ProjectInfo) extends PluginDefinition(info)
{
  val markdown = "org.clapper" % "sbt-markdown-plugin" % "0.3"
}

