package co.blocke.dotty_reflection
package info

import impl._
import scala.tasty.Reflection

/** Arity 1 Collections, e.g. List, Set, Seq */
case class SeqLikeInfo protected[dotty_reflection](
  name: String,
  _elementType: RType
) extends RType with CollectionRType:

  val fullName = name + "[" + _elementType.fullName + "]"
  lazy val infoClass: Class[_] = Class.forName(name)
      
  override def resolveTypeParams( paramMap: Map[TypeSymbol, RType] ): RType = 
    _elementType match {
      case ts: TypeSymbolInfo if paramMap.contains(ts.name.asInstanceOf[TypeSymbol]) => this.copy(_elementType = paramMap(ts.name.asInstanceOf[TypeSymbol]))
      case pt: impl.PrimitiveType => this
      case other => this.copy(_elementType = other.resolveTypeParams(paramMap))
    }

  lazy val elementType: RType = _elementType match {
    case e: SelfRefRType => e.resolve
    case e => e
  }


/** Arity 2 Collections, Map flavors, basiclly */
case class MapLikeInfo protected[dotty_reflection](
  name: String,
  _elementType: RType,
  _elementType2: RType
) extends RType with CollectionRType:

  val fullName = name + "[" + _elementType.fullName + "," + _elementType2.fullName + "]"
  lazy val infoClass: Class[_] = Class.forName(name)

  override def toType(reflect: Reflection): reflect.Type = 
    import reflect.{_, given _}
    AppliedType(Type(infoClass), List(elementType.toType(reflect), elementType2.toType(reflect)))

  override def findPaths(findSyms: Map[TypeSymbol,Path], referenceTrait: Option[TraitInfo] = None): (Map[TypeSymbol, Path], Map[TypeSymbol, Path]) = 
    val (stage1Found, stage1Unfound) = elementType match {
      case ts: TypeSymbolInfo if findSyms.contains(ts.name.asInstanceOf[TypeSymbol]) =>
        val sym = ts.name.asInstanceOf[TypeSymbol]
        (Map( ts.name.asInstanceOf[TypeSymbol] -> findSyms(sym).push(MapKeyPathElement()) ), findSyms - sym)
      case other => 
        other.findPaths(findSyms.map( (k,v) => k -> v.push(MapKeyPathElement()) ))
    }
    val (stage2Found, stage2Unfound) = elementType2 match {
      case ts: TypeSymbolInfo if stage1Unfound.contains(ts.name.asInstanceOf[TypeSymbol]) =>
        val sym = ts.name.asInstanceOf[TypeSymbol]
        (Map( ts.name.asInstanceOf[TypeSymbol] -> stage1Unfound(sym).push(MapValuePathElement()) ), findSyms - sym)
      case other => 
        other.findPaths(stage1Unfound.map( (k,v) => k -> v.push(MapValuePathElement()) ))
    }
    (stage1Found ++ stage2Found, stage2Unfound)
    

  override def resolveTypeParams( paramMap: Map[TypeSymbol, RType] ): RType = 
    var needsCopy = false
    val eKey = _elementType match {
      case ts: TypeSymbolInfo if paramMap.contains(ts.name.asInstanceOf[TypeSymbol]) => 
        needsCopy = true
        paramMap(ts.name.asInstanceOf[TypeSymbol])
      case pt: impl.PrimitiveType => 
        _elementType
      case other => 
        needsCopy = true
        other.resolveTypeParams(paramMap)
    }
    val eVal = _elementType2 match {
      case ts: TypeSymbolInfo if paramMap.contains(ts.name.asInstanceOf[TypeSymbol]) => 
        needsCopy = true
        paramMap(ts.name.asInstanceOf[TypeSymbol])
      case pt: impl.PrimitiveType => 
        _elementType2
      case other => 
        needsCopy = true
        other.resolveTypeParams(paramMap)
    }
    if needsCopy then
      this.copy(_elementType = eKey, _elementType2 = eVal)
    else
      this

  lazy val elementType: RType = _elementType match {
    case e: SelfRefRType => e.resolve
    case e => e
  }
  lazy val elementType2: RType = _elementType2 match {
    case e: SelfRefRType => e.resolve
    case e => e
  }

  override def show(tab: Int = 0, seenBefore: List[String] = Nil, supressIndent: Boolean = false, modified: Boolean = false): String = 
    val newTab = {if supressIndent then tab else tab+1}
    {if(!supressIndent) tabs(tab) else ""} + this.getClass.getSimpleName 
    + s"($name):\n"
    + elementType.show(newTab)
    + elementType2.show(newTab)


/** Scala Array */
case class ArrayInfo protected[dotty_reflection](
  name: String,
  _elementType: RType
) extends RType with CollectionRType:

  val fullName = name + "[" + _elementType.fullName + "]"
  lazy val infoClass: Class[_] = Class.forName(name)
      
  override def resolveTypeParams( paramMap: Map[TypeSymbol, RType] ): RType = 
    _elementType match {
      case ts: TypeSymbolInfo if paramMap.contains(ts.name.asInstanceOf[TypeSymbol]) => this.copy(_elementType = paramMap(ts.name.asInstanceOf[TypeSymbol]))
      case pt: impl.PrimitiveType => this
      case other => this.copy(_elementType = other.resolveTypeParams(paramMap))
    }

  lazy val elementType: RType = _elementType match {
    case e: SelfRefRType => e.resolve
    case e => e
  }

  override def show(tab: Int = 0, seenBefore: List[String] = Nil, supressIndent: Boolean = false, modified: Boolean = false): String = 
    val newTab = {if supressIndent then tab else tab+1}
    {if(!supressIndent) tabs(tab) else ""} + s"array of " + elementType.show(newTab,name :: seenBefore,true)


/** Java Set dirivative */
case class JavaSetInfo protected[dotty_reflection](
  name: String,
  _elementType: RType
) extends RType with CollectionRType:

  val fullName = name + "[" + _elementType.fullName + "]"
  lazy val infoClass: Class[_] = Class.forName(name)
      
  override def resolveTypeParams( paramMap: Map[TypeSymbol, RType] ): RType = 
    _elementType match {
      case ts: TypeSymbolInfo if paramMap.contains(ts.name.asInstanceOf[TypeSymbol]) => this.copy(_elementType = paramMap(ts.name.asInstanceOf[TypeSymbol]))
      case pt: impl.PrimitiveType => this
      case other => this.copy(_elementType = other.resolveTypeParams(paramMap))
    }

  lazy val elementType: RType = _elementType match {
    case e: SelfRefRType => e.resolve
    case e => e
  }


/** Java List dirivative */
case class JavaListInfo protected[dotty_reflection](
  name: String,
  _elementType: RType
) extends RType with CollectionRType:

  val fullName = name + "[" + _elementType.fullName + "]"
  lazy val infoClass: Class[_] = Class.forName(name)
      
  override def resolveTypeParams( paramMap: Map[TypeSymbol, RType] ): RType = 
    _elementType match {
      case ts: TypeSymbolInfo if paramMap.contains(ts.name.asInstanceOf[TypeSymbol]) => this.copy(_elementType = paramMap(ts.name.asInstanceOf[TypeSymbol]))
      case pt: impl.PrimitiveType => this
      case other => this.copy(_elementType = other.resolveTypeParams(paramMap))
    }

  lazy val elementType: RType = _elementType match {
    case e: SelfRefRType => e.resolve
    case e => e
  }


/** Java Array */
case class JavaArrayInfo protected[dotty_reflection](
  name: String,
  _elementType: RType
) extends RType with CollectionRType:
 
  val fullName = name + "[" + _elementType.fullName + "]"
  lazy val infoClass: Class[_] = Class.forName(name)
      
  override def resolveTypeParams( paramMap: Map[TypeSymbol, RType] ): RType = 
    _elementType match {
      case ts: TypeSymbolInfo if paramMap.contains(ts.name.asInstanceOf[TypeSymbol]) => this.copy(_elementType = paramMap(ts.name.asInstanceOf[TypeSymbol]))
      case pt: impl.PrimitiveType => this
      case other => this.copy(_elementType = other.resolveTypeParams(paramMap))
    }

  lazy val elementType: RType = _elementType match {
    case e: SelfRefRType => e.resolve
    case e => e
  }

  override def show(tab: Int = 0, seenBefore: List[String] = Nil, supressIndent: Boolean = false, modified: Boolean = false): String = 
    val newTab = {if supressIndent then tab else tab+1}
    {if(!supressIndent) tabs(tab) else ""} + s"array of " + elementType.show(newTab,name :: seenBefore,true)


/** Java Queue dirivative */
case class JavaQueueInfo protected[dotty_reflection](
  name: String,
  _elementType: RType
) extends RType with CollectionRType:

  val fullName = name + "[" + _elementType.fullName + "]"
  lazy val infoClass: Class[_] = Class.forName(name)
      
  override def resolveTypeParams( paramMap: Map[TypeSymbol, RType] ): RType = 
    _elementType match {
      case ts: TypeSymbolInfo if paramMap.contains(ts.name.asInstanceOf[TypeSymbol]) => this.copy(_elementType = paramMap(ts.name.asInstanceOf[TypeSymbol]))
      case pt: impl.PrimitiveType => this
      case other => this.copy(_elementType = other.resolveTypeParams(paramMap))
    }

  lazy val elementType: RType = _elementType match {
    case e: SelfRefRType => e.resolve
    case e => e
  }


/** Java Stack dirivative */
case class JavaStackInfo protected[dotty_reflection](
  name: String,
  _elementType: RType
) extends RType with CollectionRType:

  val fullName = name + "[" + _elementType.fullName + "]"
  lazy val infoClass: Class[_] = Class.forName(name)
      
  override def resolveTypeParams( paramMap: Map[TypeSymbol, RType] ): RType = 
    _elementType match {
      case ts: TypeSymbolInfo if paramMap.contains(ts.name.asInstanceOf[TypeSymbol]) => this.copy(_elementType = paramMap(ts.name.asInstanceOf[TypeSymbol]))
      case pt: impl.PrimitiveType => this
      case other => this.copy(_elementType = other.resolveTypeParams(paramMap))
    }

  lazy val elementType: RType = _elementType match {
    case e: SelfRefRType => e.resolve
    case e => e
  }


/** Java Map dirivative */
case class JavaMapInfo protected[dotty_reflection](
  name: String,
  _elementType: RType,
  _elementType2: RType
) extends RType with CollectionRType:

  val fullName = name + "[" + _elementType.fullName + "," + _elementType2.fullName + "]"
  lazy val infoClass: Class[_] = Class.forName(name)
  
  lazy val elementType: RType = _elementType match {
    case e: SelfRefRType => e.resolve
    case e => e
  }
  lazy val elementType2: RType = _elementType2 match {
    case e: SelfRefRType => e.resolve
    case e => e
  }

  override def toType(reflect: Reflection): reflect.Type = 
    import reflect.{_, given _}
    AppliedType(Type(infoClass), List(elementType.toType(reflect), elementType2.toType(reflect)))

  override def findPaths(findSyms: Map[TypeSymbol,Path], referenceTrait: Option[TraitInfo] = None): (Map[TypeSymbol, Path], Map[TypeSymbol, Path]) = 
    val (stage1Found, stage1Unfound) = elementType match {
      case ts: TypeSymbolInfo if findSyms.contains(ts.name.asInstanceOf[TypeSymbol]) =>
        val sym = ts.name.asInstanceOf[TypeSymbol]
        (Map( ts.name.asInstanceOf[TypeSymbol] -> findSyms(sym).push(MapKeyPathElement()) ), findSyms - sym)
      case other => 
        other.findPaths(findSyms.map( (k,v) => k -> v.push(MapKeyPathElement()) ))
    }
    val (stage2Found, stage2Unfound) = elementType2 match {
      case ts: TypeSymbolInfo if stage1Unfound.contains(ts.name.asInstanceOf[TypeSymbol]) =>
        val sym = ts.name.asInstanceOf[TypeSymbol]
        (Map( ts.name.asInstanceOf[TypeSymbol] -> stage1Unfound(sym).push(MapValuePathElement()) ), findSyms - sym)
      case other => 
        other.findPaths(stage1Unfound.map( (k,v) => k -> v.push(MapValuePathElement()) ))
    }
    (stage1Found ++ stage2Found, stage2Unfound)

  override def resolveTypeParams( paramMap: Map[TypeSymbol, RType] ): RType = 
    var needsCopy = false
    val eKey = _elementType match {
      case ts: TypeSymbolInfo if paramMap.contains(ts.name.asInstanceOf[TypeSymbol]) => 
        needsCopy = true
        paramMap(ts.name.asInstanceOf[TypeSymbol])
      case pt: impl.PrimitiveType => 
        _elementType
      case other => 
        needsCopy = true
        other.resolveTypeParams(paramMap)
    }
    val eVal = _elementType2 match {
      case ts: TypeSymbolInfo if paramMap.contains(ts.name.asInstanceOf[TypeSymbol]) => 
        needsCopy = true
        paramMap(ts.name.asInstanceOf[TypeSymbol])
      case pt: impl.PrimitiveType => 
        _elementType2
      case other => 
        needsCopy = true
        other.resolveTypeParams(paramMap)
    }
    if needsCopy then
      this.copy(_elementType = eKey, _elementType2 = eVal)
    else
      this

  override def show(tab: Int = 0, seenBefore: List[String] = Nil, supressIndent: Boolean = false, modified: Boolean = false): String = 
    val newTab = {if supressIndent then tab else tab+1}
    {if(!supressIndent) tabs(tab) else ""} + this.getClass.getSimpleName 
    + s"($name):\n"
    + elementType.show(newTab,name :: seenBefore)
    + elementType2.show(newTab,name :: seenBefore)
