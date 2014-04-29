package models

import java.lang.Math

object Tree {

  type GR  = Map[Question, Map[PossibleAnswer, Seq[Sin]]]
  type PAS = Map[PossibleAnswer, Seq[Sin]]

  val answers   = Data.answers.right.get
  val grouped   = group(answers)
  val questions = grouped.toSeq.map { t  => (t._1.id, t._1 )  }.toMap
  val allSins   = answers.map(_.sin).toSet

  val IdYES = """(\d+)Y""".r
  val IdNO  = """(\d+)N""".r
  val IdNC  = """(\d+)C""".r

  lazy val root = {
    val q = nextQuestion(answers)
    TreeNode(encode(Nil, q), q)
  }

  def next(uuid: String, pa: PossibleAnswer): TreeElement = {
    val (prevs, current) = decode(uuid, questions)

    next(answers, grouped, prevs :+ (current, pa))
  }

  def decode(uuid: String, questions: Map[Int, Question]): (Seq[(Question, PossibleAnswer)], Question) = {
    uuid.split('+') match {
      case Array(prev, current) => {
        (prev, questions.get(current.toInt)) match {
          case ("", Some(question)) => (Nil, question)
          case (str, Some(question)) => (
            str.split('-').toSeq.map {
              case IdYES(id) => questions.get(id.toInt).map( (_, YES) )
              case IdNO(id)  => questions.get(id.toInt).map( (_, NO) )
              case IdNC(id)  => questions.get(id.toInt).map( (_, NC) )
            }.flatten, question
          )
          case _ => throw new RuntimeException("Invalid UUID")
        }
      }
      case _ => throw new RuntimeException("Invalid UUID")
    }
  }

  def encode(prev: Seq[(Question, PossibleAnswer)], current: Question): String = {
    val mkStr = prev.map {
      case (q, YES) => s"${q.id}Y"
      case (q, NO)  => s"${q.id}N"
      case (q, NC)  => s"${q.id}C"
    }.mkString("-")

    s"$mkStr+${current.id}"
  }

  def next(answers: Seq[Answer], grouped: GR, questions: Seq[(Question, PossibleAnswer)]): TreeElement = {
    val qSet    = questions.map(_._1).toSet
    val sins    = questions.flatMap { case (q, pa) =>
      for {
        pas  <- grouped.get(q)
        sins <- pas.get(pa)
      } yield sins.toSet
    }.foldLeft(allSins){ (acc, a) => acc.intersect(a) }

    sins.toSeq match {
      case Seq(sin) => TreeLeaf(sin)
      case Nil      => TreeFail
      case _        => {
        val filtered = answers.filter { a => sins.contains(a.sin) && !qSet.contains(a.question) }
        if( filtered.nonEmpty ) {
          val question = nextQuestion(filtered)
          val uuid     = encode(questions, question)
          TreeNode(uuid, question)
        } else TreeFail
      }
    }
  }

  def nextQuestion(answers: Seq[Answer]): Question = {
    val sinsCount = answers.map(_.sin).toSet.size

    val grouped = group(answers)
    val question = extractQuestion(grouped, sinsCount)

    question._1
  }

  def group(answers: Seq[Answer]): GR = {
    answers
      .groupBy(_.question)
      .mapValues { v =>
        v.groupBy(_.answer).mapValues { v =>
          v.map(_.sin)
        }
      }
  }

  def extractQuestion(questions: GR, sinsCount: Int): (Question, PAS, Int) = {
    questions
      .toSeq
      .map { case (q, pas) =>
        (q, pas, extractPA(pas, sinsCount))
      }
      .sortBy(_._3)
      .head
  }

  def extractPA(pas: Map[PossibleAnswer, Seq[Sin]], sinsCount: Int): Int = {
    pas
      .toSeq
      .map { case (pa, sins) =>
        Math.abs( sinsCount / 2 - sins.size )
      }
      .sorted
      .head
  }

}