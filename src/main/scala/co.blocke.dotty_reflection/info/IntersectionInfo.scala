package co.blocke.dotty_reflection
package info

import scala.tasty.Reflection

case class IntersectionInfo protected[dotty_reflection](
  name: String,
  _leftType: RType,
  _rightType: RType
  ) extends RType with LeftRightRType:

    val fullName: String = name + "[" + _leftType.fullName + "," + _rightType.fullName + "]"

    lazy val infoClass: Class[_] = impl.Clazzes.AnyClazz

    lazy val leftType: RType = _leftType match {
      case e: SelfRefRType => e.resolve
      case e => e
    }
    lazy val rightType: RType = _rightType match {
      case e: SelfRefRType => e.resolve
      case e => e
    }

    override def toType(reflect: Reflection): reflect.Type = 
      import reflect.{_, given _}
      AndType(leftType.toType(reflect), rightType.toType(reflect))  

    def _copy( left: RType, right: RType ) = this.copy(_leftType = left, _rightType = right)
