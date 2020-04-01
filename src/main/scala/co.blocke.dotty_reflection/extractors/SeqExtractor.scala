package co.blocke.dotty_reflection
package extractors

import impl._
import impl.Clazzes._
import infos._ 
import scala.tasty.Reflection

case class SeqExtractor() extends TypeInfoExtractor[SeqLikeInfo]:

  def matches(clazz: Class[_]): Boolean = clazz <:< SeqClazz || clazz <:< SetClazz || clazz <:< ScalaArrayClazz

  def emptyInfo(clazz: Class[_]): SeqLikeInfo = 
    SeqLikeInfo(
      clazz.getName, 
      clazz, 
      clazz.getTypeParameters.toList.head.getName.asInstanceOf[TypeSymbol])

  def extractInfo(reflect: Reflection)(
    t: reflect.Type, 
    tob: List[reflect.TypeOrBounds], 
    className: String, 
    clazz: Class[_], 
    typeInspector: ScalaClassInspector): ConcreteType =

    SeqLikeInfo(
            className, 
            clazz,
            typeInspector.inspectType(reflect)(tob.head.asInstanceOf[reflect.TypeRef]))
