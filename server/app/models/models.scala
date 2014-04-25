package models

import play.api.libs.json._

case class Question(id: Int, text: String)

object Question {
  implicit val readers = Json.reads[Question]
  implicit val writers = Json.writes[Question]
}

case class Sin(id: Int, text: String)

object Sin {
  implicit val readers = Json.reads[Sin]
  implicit val writers = Json.writes[Sin]
}

case class Answer(question: Question, sin: Sin, answer: PossibleAnswer)