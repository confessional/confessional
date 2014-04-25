package models

import play.api.libs.json._

sealed trait TreeElement

case class  TreeNode(uuid: String, question: Question, next: Map[PossibleAnswer, TreeElement]) extends TreeElement

case class  SimpleTreeNode(uuid: String, question: Question)

case class  TreeLeaf(sin: Sin) extends TreeElement

case object TreeFail extends TreeElement

object SimpleTreeNode{
  implicit lazy val writers = Json.writes[SimpleTreeNode]
}

object TreeLeaf {
  implicit lazy val writers = Json.writes[TreeLeaf]
}

object TreeNode {

  implicit lazy val mapWriter = new Writes[Map[PossibleAnswer, TreeElement]] {
    def writes(values: Map[PossibleAnswer, TreeElement]) = {
      new JsArray(
        values.toSeq.map { case (pa, te) =>
          Json.obj(
            "key"   -> Json.toJson(pa),
            "value" -> Json.toJson(te)(TreeElement.writers)
          )
        }
      )
    }
  }

  implicit lazy val writers = Json.writes[TreeNode]

  def create(question: Question, next: Map[PossibleAnswer, TreeElement]) = {
    new TreeNode(java.util.UUID.randomUUID.toString, question, next)
  }

  def find(root: TreeNode, uuid: String): Option[TreeNode] = {
    if(root.uuid != uuid ) {
      root.next.foldLeft[Option[TreeNode]](None){
        case (None, (_, tn:TreeNode)) => if( tn.uuid == uuid ) Some(tn) else find(tn, uuid)
        case (None, _)     => None
        case (Some(tn), _) => Some(tn)
      }
    } else {
      Some(root)
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

object TreeElement {
  implicit lazy val writers: Writes[TreeElement] = new Writes[TreeElement] {
    def writes(value: TreeElement) = value match {
      case tn:TreeNode => Json.toJson(tn)(TreeNode.writers)
      case tl:TreeLeaf => Json.toJson(tl)(TreeLeaf.writers)
      case TreeFail    => new JsString("FAIL")
    }
  }

  def find(root: TreeElement, uuid: String): Option[TreeNode] = {
    root match {
      case tn: TreeNode => TreeNode.find(tn, uuid)
      case _ => None
    }
  }
}