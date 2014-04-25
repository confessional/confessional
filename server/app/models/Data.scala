package models

import parsers._
import models._

object Data {

  def answers: Either[String,Seq[Answer]] = {
    val sins         = SinsParser.parsePath("../sins_complete.txt")
    val questions    = QuestionParser.parsePath("../questions.txt")
    val associations = AssociationsParser.parsePath("../associations.txt")

    for {
      s <- sins.right
      q <- questions.right
      a <- associations.right
    } yield {
      val mapSins      = s.map { s => (s.id, s) }.toMap
      val mapQuestions = q.map { q => (q.id, q) }.toMap

      var answers = a.flatMap { a =>
        val sin = mapSins.get(a.idSin)

        val associated = a.idQuestions.map { idQ =>
          val question = mapQuestions.get(idQ)

          for {
            s <- sin
            q <- question
          } yield Answer(q, s, YES)
        }.flatten

        val notAssociated: Seq[Answer] = sin.map { s =>
          q.filter { q => !a.idQuestions.contains(q.id) }
           .map { q => Answer(q, s, NO) }
        }.getOrElse(Nil)

        associated ++ notAssociated
      }

      play.Logger.debug("Parse done")

      answers
    }
  }
}