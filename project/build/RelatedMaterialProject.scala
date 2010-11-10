import sbt._
import sbt.FileUtilities._
import scala.util.matching.Regex
import org.clapper.sbtplugins.MarkdownPlugin

class RelatedMaterialProject(info: ProjectInfo)
extends DefaultProject(info) with MarkdownPlugin
{
    override def cleanLibAction = super.cleanAction dependsOn(markdownCleanLibAction)
    override def updateAction = super.updateAction dependsOn(markdownUpdateAction)

    val bookSourcePath = "src" / "main" / "book"
    val textSourcePath = bookSourcePath / "text"
    val allTextFiles = textSourcePath ** "*.md"

    val Pathname = new Regex("""^(.*/)?(.*)\.(.*)$""")

    val bookOutputPath = outputPath / "book"
    val htmlOutputPath = bookOutputPath / "html" 

    lazy val pagesAsHtml = fileTask(htmlOutputPath from allTextFiles)
    {
      log.info("producing multiple html in " + htmlOutputPath + " from each of " + allTextFiles)

        createDirectory(htmlOutputPath, log)

        allTextFiles.get.foreach(textFile =>
          textFile.toString match {
            case Pathname(path, file, ext) => markdown(textFile, Path.fromString( htmlOutputPath , file + ".html"), log)
          }
        )
        None
    } describedAs "generates html for each markdown file, using Showdown and MarkdownPlugin"

    lazy val pandocAsHtml = fileTask(htmlOutputPath from allTextFiles)
    {
      log.info("using pandoc to produce html from each of " + allTextFiles)

        createDirectory(htmlOutputPath, log)

    } describedAs "uses pandoc to produce html"


}

