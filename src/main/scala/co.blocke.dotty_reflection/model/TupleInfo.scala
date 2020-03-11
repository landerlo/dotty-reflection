package co.blocke.dotty_reflection
package model

case class TupleInfo(
  name: String,
  infoClass: Class[_],
  tupleTypes: List[ALL_TYPE]
) extends ConcreteType:
  val typeParameters = Nil
  override def sewTypeParams(actualTypeMap: Map[TypeSymbol, ALL_TYPE]): ConcreteType = 
    this.copy(tupleTypes = tupleTypes.map(_ match {
      case ts: TypeSymbol if actualTypeMap.contains(ts) => actualTypeMap(ts)
      case ts: TypeSymbol => this
      case c: ConcreteType => c.sewTypeParams(actualTypeMap)
  }))