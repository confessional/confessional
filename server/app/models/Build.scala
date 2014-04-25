package models

import java.lang.Math

object BuildTree {

  type GR  = Map[Question, Map[PossibleAnswer, Seq[Sin]]]
  type PAS = Map[PossibleAnswer, Seq[Sin]]


  def create(answers: Seq[Answer]): TreeElement = {
    val sinsCount = answers.map(_.sin).toSet.size

    val grouped = group(answers)
    val question = extractQuestion(grouped, sinsCount)

    TreeNode(question._1, toTreeNodes(answers, question._1, question._2) )
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

  def toTreeNodes(answers: Seq[Answer], q: Question, pas: PAS) = {
    pas.mapValues {
      case Nil        => TreeFail
      case Seq(sin)   => TreeLeaf(sin)
      case sins       => create(
        answers.filter { a => a.question != q && sins.contains(a.sin) }
      )
    }
  }

}