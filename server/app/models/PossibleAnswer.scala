package models

import play.api.libs.json._

sealed trait PossibleAnswer

case object YES extends PossibleAnswer
case object NO extends PossibleAnswer
case object NC extends PossibleAnswer

object PossibleAnswer {
  implicit lazy val writers = new Writes[PossibleAnswer] {
    def writes(value: PossibleAnswer) = value match {
      case YES => new JsString("YES")
      case NO  => new JsString("NO")
      case NC  => new JsString("NC")
    }
  }
}