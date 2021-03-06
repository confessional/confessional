package controllers

import play.api._
import play.api.mvc._
import play.api.libs.json.Json

import models._

object Application extends Controller {

  def index = Action {
    Ok(views.html.index("Your new application is ready."))
  }

  def start = Action {
    Ok( Json.toJson(Tree.root) )
  }

  def next(uuid: String, answer: String) = Action{
    val pa = answer match {
      case "YES" => Some(YES)
      case "NO"  => Some(NO)
      case "NC"  => Some(NC)
      case _ => None
    }

    pa match {
      case Some(pa) => Ok( Json.toJson(Tree.next(uuid, pa)) )
      case _        => BadRequest("Invalid node")
    }
  }

  val q1 = Question(1, "Hello")
  val q2 = Question(2, "Hello2")
  val q3 = Question(3, "Hello3")
  val q4 = Question(4, "Hello4")
  val q5 = Question(5, "Hello")

  val s1 = Sin(1, "Sinnnnn")
  val s2 = Sin(2, "Sinnnnn")
  val s3 = Sin(3, "Sinnnnn")
  val s4 = Sin(4, "Sinnnnn")


  val answers = Seq(
    Answer(q1, s1, YES),
    Answer(q1, s2, NO),
    Answer(q1, s3, NO),
    Answer(q1, s4, NO),

    Answer(q2, s1, NO),
    Answer(q2, s2, YES),
    Answer(q2, s3, NO),
    Answer(q2, s4, NO),

    Answer(q3, s1, NO),
    Answer(q3, s2, NO),
    Answer(q3, s3, YES),
    Answer(q3, s4, NO),

    Answer(q4, s1, NO),
    Answer(q4, s2, NO),
    Answer(q4, s3, NO),
    Answer(q4, s4, YES),

    Answer(q5, s1, NO),
    Answer(q5, s2, NO),
    Answer(q5, s3, YES),
    Answer(q5, s4, YES)
  )

}
