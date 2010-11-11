import sbt._
import Process._
import FileUtilities._
import java.io.File

import scala.util.matching.Regex

trait BookPlugin extends DefaultProject
{
  val PathParser = new Regex("""^(.*/)?(.*)\.(.*)$""")

  def pandocOutputPath = outputPath / "pandoc"

    val bookOutputPath = outputPath / "book"
    val htmlOutputPath = bookOutputPath / "html" 
    val pdfOutputPath = bookOutputPath / "pdf" 
  

  def pandoc(src:Path):Unit =
  {
    createDirectory(pandocOutputPath, log)

    val PathParser(path, file, ext) = src.toString

        "markdown2pdf -o " + Path.fromString(pandocOutputPath, file + ".pdf") + " " + src.toString  !
  }

  def toHtml():Unit =
  {
              // pandoc -o target/scala_2.8.0/hello.html src/main/book/text/hello.md
              //textFile.asFile #> "pandoc --smart --to=html" #> new File(htmlOutputPath.asFile, file + ".html") ! log
  }

}

