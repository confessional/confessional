package models

case class Question(id: Integer, text: String)

case class Sin(id: Integer, text: String)


sealed trait PossibleAnswer

case object YES extends PossibleAnswer
case object NO extends PossibleAnswer
case object NC extends PossibleAnswer

case class Answer(question: Question, sin: Sin, answer: PossibleAnswer)

sealed trait TreeElement

case class  TreeNode(uuid: String, question: Question, next: Map[PossibleAnswer, TreeElement]) extends TreeElement
case class  TreeLeaf(sin: Sin) extends TreeElement
case object TreeFail extends TreeElement

object TreeNode {
  def apply(question: Question, next: Map[PossibleAnswer, TreeElement]) = {
    new TreeNode(java.util.UUID.randomUUID.toString, question, next)
  }

  def find(root: TreeNode, uuid: String): Option[TreeNode] = {
    root.next.foldLeft[Option[TreeNode]](None){
      case (None, (_, tn:TreeNode)) => if( tn.uuid == uuid ) Some(tn) else find(tn, uuid)
      case (None, _)     => None
      case (Some(tn), _) => Some(tn)
    }
  }

  def next(root: TreeNode, uuid: String, pa: PossibleAnswer) = {
    val te = for {
      tn <- find(root, uuid)
      te <- tn.next.get(pa)
    } yield te

    te.getOrElse(TreeFail)
  }
}