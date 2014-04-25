

case class Question(id: Integer, text: String)

case class Sin(id: Integer, text: String)


sealed trait PossibleAnswer

case object YES extends PossibleAnswer
case object NO extends PossibleAnswer
case object NC extends PossibleAnswer

case class Answer(question: Question, sin: Sin, answer: PossibleAnswer)

trait TreeElement

case class TreeNode(question: Question, next: Map[PossibleAnswer, TreeElement]) extends TreeElement
case class TreeLeaf(sin: Sin) extends TreeElement
case class TreeFail extends TreeElement