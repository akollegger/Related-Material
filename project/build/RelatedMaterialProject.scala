import sbt._
import Process._
import sbt.FileUtilities._

class RelatedMaterialProject(info: ProjectInfo)
  extends DefaultProject(info) 
  with BookPlugin
{
    val bookSourcePath = "src" / "main" / "book"
    val textSourcePath = bookSourcePath / "text"
    val allTextPaths = textSourcePath ** "*.md"

    lazy val pandocToHtml = fileTask(htmlOutputPath from allTextPaths)
    {
        allTextPaths.get.foreach(textPath =>
          pandoc(textPath)
        )
        None
    } 

    lazy val pandocToPdf = fileTask(htmlOutputPath from allTextPaths)
    {
        allTextPaths.get.foreach(textPath =>
          pandoc(textPath)
        )
        None
    } 

}

