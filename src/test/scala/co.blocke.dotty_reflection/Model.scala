package co.blocke.dotty_reflection

import co.blocke.reflect._

// Basic Tasty class
case class Person(name: String, age: Int, other: Int | Boolean)

// Match / dependent types
type Elem[X] = X match {
  case String => Char
  case Array[t] => t
  case Iterable[t] => t
}
case class Definitely( id: Elem[List[Int]], stuff: Elem[String] )

case class ScalaPrimitives(
  a: Boolean,
  b: Byte,
  c: Char,
  d: Double,
  e: Float,
  f: Int,
  g: Long,
  h: Short,
  i: String,
  j: Any
)

// Mixin tests
trait SJCapture {
  var captured: java.util.HashMap[String, _] =
    new java.util.HashMap[String, Any]()
}
class SJCaptureJava extends SJCapture

case class WithMix(id:String) extends SJCapture

// Object/field Annotations
@ClassAnno(name="Foom")
case class WithAnnotation(@FieldAnno(idx=5) id: String)

// Opaque type aliases
opaque type EMP_ID = Int
case class Employee(eId: EMP_ID, age: Int)

// Value classes
case class IdUser(id: Int) extends AnyVal  // value class
case class Employee2(eId: IdUser, age: Int)

// Parameterized classes
case class WithParam[T,U](one:T, two:U)

// Opaque type is union
opaque type GEN_ID = Int | String
case class OpaqueUnion(id: GEN_ID)

// With default value
case class WithDefault(a: Int, b: String = "wow")

// Either
case class BothSides(a: scala.util.Either[Int,String])
case class BothSidesWithOption(a: scala.util.Either[Int, Option[String]])
case class BothSidesWithUnion(a: scala.util.Either[Int, String|Boolean])

// Options
case class NormalOption(a: Option[Int])
case class NestedOption(a: Option[Option[Int]])
case class ParamOption[T](a: Option[T])
case class UnionHavingOption(a: Boolean | Option[Int], b: Boolean | java.util.Optional[Int])
case class OptionHavingUnion(a: Option[Boolean|String])

// Plain class
class PlainGood(val a: Int, val b: String)
class PlainBad(val a: Int, b: String)

// Collections - immutable
case class Coll1(a: List[String])
case class Coll2(a: scala.collection.immutable.HashSet[String])
case class Coll3(a: Map[String,Float])
case class Coll4(a: scala.collection.immutable.ListMap[String,Boolean])
// Collections - mutable
case class Coll1m(a: scala.collection.mutable.ListBuffer[String])
case class Coll2m(a: scala.collection.mutable.HashSet[String])
case class Coll3m(a: scala.collection.mutable.Map[String,Float])
case class Coll4m(a: scala.collection.mutable.ListMap[String,Boolean])
case class NestedColl(a: Map[String, List[Option[Int]]])

// Tuple
case class TupleTurtle[Z]( t: (Int, Z, List[String], NormalOption))

// Scala 2.x style Enumeration
object WeekDay extends Enumeration {
  type WeekDay = Value
  val Monday = Value(1)
  val Tuesday = Value(2)
  val Wednesday = Value(3)
  val Thursday = Value(4)
  val Friday = Value(5)
  val Saturday = Value(6)
  val Sunday = Value(-3)
}
import WeekDay._

// Scala 3 Enum
enum Month {
  case Jan, Feb, Mar
}

case class Birthday(m: Month, d: WeekDay)

case class TryMe(maybe: scala.util.Try[Boolean])

// Sealed trait w/case classes and objects
sealed trait Vehicle
case class Truck(numberOfWheels: Int) extends Vehicle
case class Car(numberOfWheels: Int, color: String) extends Vehicle
case class Plane(numberOfEngines: Int) extends Vehicle
case class VehicleHolder(v: Vehicle)

sealed trait Flavor
case object Vanilla extends Flavor
case object Chocolate extends Flavor
case object Bourbon extends Flavor
case class FlavorHolder(f: Flavor)

// Type substitution models
//-------------------------
// 0-level
case class DuoTypes[Q,U](a: Q, b: U)

// 1st level type substitution
case class DuoHolder( a: DuoTypes[Int,Float] )

// 2nd and 3rd level type substitution - option
case class OptHolder( a: Option[DuoTypes[String,Boolean]])
case class OptHolder2( a: Option[Option[DuoTypes[String,Boolean]]])

// 2nd and 3rd level type substitution - either
case class EitherHolder( a: Either[DuoTypes[Int,Float], Option[DuoTypes[String,Boolean]]])

// Alias type substitution
opaque type mystery = DuoTypes[Byte,Short]
case class AliasTypeSub(a: mystery) 

// 1st and 2nd level substitution in class
case class DuoClass( a: DuoTypes[Int,DuoTypes[Byte,Short]] )

// List and Map substitution
case class ListMapSub( a: List[DuoTypes[Int,Byte]], b: Map[String, DuoTypes[Float,Short]])

// Try substitution
case class TryHolder( a: scala.util.Try[DuoTypes[String,Int]] )

// Trait type substitution
trait TypeShell[X] { val x: X }
case class TypeShellHolder(a: TypeShell[Int])

// Union type substitution
case class UnionHolder(a: Int | TypeShell[String])

// Trait type substitution
trait ParamThing[X]{ val id: X }
// case class ParamItem[Y](id:Y) extends ParamThing[Y]

// Intersection type substitution
trait Stackable[T]
trait Floatable[U]

// Non-parameterized Intersection Types
trait InterA
trait InterB
trait InterC
case class IntersectionHolder( a: InterA & InterB & InterC )

// Type member subsitution
trait Body
case class FancyBody(message: String) extends Body

case class Envelope[T <: Body, U](id: String, body: T) {
  type Giraffe = T
  type Foo = Int
}

case class WithScalaArray(list: Array[Char])