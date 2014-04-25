package parsers

import scala.util.parsing.input._
import scala.util.parsing.combinator._
import scala.util.matching._
import java.math._
import org.joda.time._
import org.joda.time.format._
import play._

import models._

trait Parser[T] extends RegexParsers{

    override def skipWhitespace = false

    val separator: String = " "

    val encoding: io.Codec = io.Codec("UTF-8")

    def EOF: Parser[String] = """\Z""".r

    def EOL: Parser[String] = (("\r"?) ~> "\n") | EOF

    def ID: Parser[String] = """\d+""".r

    def TEXT: Parser[String] = """.+""".r

    def line: Parser[T]

    def nonConcordance = sys.error("Non concordance entre le nombre d'élement de la ligne et la fonction de mapping")

    def parser: Parser[Seq[T]] = (line*) <~ (whiteSpace *)

    def parse( reader: Reader[Char] ): Either[String,Seq[T]] = {
        parser( reader ) match {
            case Success(sequence, in) if in.atEnd => Right(sequence)
            case Success(_, in) => Left("Oops, non parsé totalement -> " + in.pos.toString)
            case NoSuccess(message, _) => Left(message)
        }
    }

    def parse( data: String ): Either[String,Seq[T]] = {
        parse( new CharSequenceReader(data) )
    }

    def parsePath( path: String ): Either[String,Seq[T]] = {
        parse(io.Source.fromFile( path )(encoding).mkString)
    }

    def parseResource( path: String ): Either[String,Seq[T]] = {
        parse(io.Source.fromInputStream(Play.application().resourceAsStream(path))(encoding).mkString)
    }
}

object SinsParser extends Parser[Sin] {

    def line: Parser[Sin] = ID ~ TEXT <~ EOL ^^ { case id ~ text => Sin(id.toInt, text) }

}

object QuestionParser extends Parser[Question] {

    def line: Parser[Question] = ID ~ TEXT <~ EOL ^^ { case id ~ text => Question(id.toInt, text) }

}

case class Associations(idSin: Int, idQuestions: List[Int])

object AssociationsParser extends Parser[Associations] {

    def line: Parser[Associations] = ID ~ rep(separator ~> ID) <~ EOL ^^ { case idSin ~ idQuestions =>
        Associations(idSin.toInt, idQuestions.map(_.toInt))
    }

}