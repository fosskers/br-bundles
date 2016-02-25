/**
  * Types.scala
  *
  * Given the simplicity of our types, we keep them all in one file
  * for easy simultaneous viewing.
  */

package com.boldradius.bundlepricing.types

/** Anything with a price associated with it */
trait Sellable {
  val price: Float
}

case class Item(name: String, price: Float) extends Sellable

case class Bundle(items: Seq[Item], price: Float) extends Sellable

object Types {
  type Catalogue = Set[Item]
}
