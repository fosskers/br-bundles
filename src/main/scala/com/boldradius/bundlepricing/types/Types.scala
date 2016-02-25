/**
  * Types.scala
  *
  * Given the simplicity of our types, we keep them all in one file
  * for easy simultaneous viewing.
  */

package com.boldradius.bundlepricing.types

case class Item(name: String, price: Float)

case class Bundle(items: Seq[Item], price: Float)
