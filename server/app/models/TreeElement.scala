package models

import play.api.libs.json._

sealed trait TreeElement

case class  TreeNode(uuid: String, question: Question) extends TreeElement

case class  TreeLeaf(sin: Sin) extends TreeElement

case object TreeFail extends TreeElement

object TreeLeaf {
  implicit lazy val writers = Json.writes[TreeLeaf]
}

object TreeNode {
  implicit lazy val writers = Json.writes[TreeNode]
}

object TreeElement {
  implicit lazy val writers: Writes[TreeElement] = new Writes[TreeElement] {
    def writes(value: TreeElement) = value match {
      case tn:TreeNode => Json.toJson(tn)(TreeNode.writers)
      case tl:TreeLeaf => Json.toJson(tl)(TreeLeaf.writers)
      case TreeFail    => new JsString("FAIL")
    }
  }
}