/**
  * BundlePricing.scala
  * 
  * Functions for optimizating the final price of a collection
  * of `Sellable` items.
  */

package com.boldradius.bundlepricing

import com.boldradius.bundlepricing.types.{Bundle, Item}
import scala.concurrent.{ExecutionContext, Future}

// --- //

object BundlePricing {

  def bestPrice(
    bundles: Seq[Bundle],
    order: Seq[Item]
  )(implicit ec: ExecutionContext): Future[Float] = {

    /* All unique bundles to consider building.
     * 
     * I've yet to test if `.toSet.toSeq` is faster overall
     * than `.distinct`, but it would be by the Math:
     *   toSet.toSeq: O(nlogn + n)
     *   distinct: O(n^2)
     */
    val uniques: Set[Bundle] = order.distinct.map(i =>
      bundles.filter(_.items.contains(i))
    ).flatten.toSet

    bundle(uniques, order)
  }

  def bundle(
    bundles: Set[Bundle],
    order: Seq[Item]
  )(implicit ec: ExecutionContext): Future[Float] = {
    Future.sequence(bundles.map({ b =>

      /* If we can form a bundle, we recurse. Otherwise we
       * assume that the sum of the individual item prices is best.
       * We recurse on the full `bundles` set, as we assume that
       * more than one instance of the same bundle could appear in
       * an order.
       */
      leftOver(b,order) match {
        case None => Future.successful(order.map(_.price).sum)
        case Some(rest) => bundle(bundles,rest).map(_ + b.price)
      }

    })).map(_.reduceLeft((acc,p) => if (p < acc) p else acc))
  }

  /* If the bundle can be made with the given order, what `Item`s would
   * be left over?
   */
  def leftOver(bundle: Bundle, order: Seq[Item]): Option[Seq[Item]] = {

    /* Some clever `Seq` tricks. If a `Bundle` would be made empty by
     * removing the contents of the order from it, then the Bundle is
     * possible with this order. 
     */
    bundle.items.diff(order) match {
      case Seq() => Some(order.diff(bundle.items))
      case _ => None
    }
  }
}

