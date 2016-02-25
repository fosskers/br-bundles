import com.boldradius.bundlepricing.BundlePricing._
import com.boldradius.bundlepricing.types.{Bundle, Item}
import org.scalatest._
import org.scalatest.concurrent.ScalaFutures
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

// --- //

class BundleTests extends FlatSpec with Matchers with ScalaFutures {

  /* The contents of my bookcase */
  val dune = Item("Dune", 10)
  val storm = Item("Storm Front", 5)
  val fool = Item("Fool Moon", 5)
  val summer = Item("Summer Knight", 5)
  val grave = Item("Grave Peril", 5)
  val wind = Item("The Name of the Wind", 19.50f)
  val hobbit = Item("The Hobbit", 8)
  val rings = Item("The Lord of the Rings", 15)
  val daemon = Item("Daemon", 9)
  val robin = Item("Robin Hood", 20)
  val alch = Item("The Alchemist", 7)

  /* What deals! */
  val lotr = Bundle(Seq(hobbit,rings), 12)
  val jimButcher = Bundle(Seq(storm,fool,summer,grave), 10)
  val mixed = Bundle(Seq(hobbit,grave,summer,alch), 1)

  val bundles: Seq[Bundle] = Seq(lotr,jimButcher,mixed)

  "leftOver" should "fail on empty orders" in {
    leftOver(lotr, Seq()) should not be defined
  }

  it should "fail for bundles that can't be made" in {
    leftOver(jimButcher, Seq(storm,fool,wind,daemon)) should not be defined
  }

  it should "succeed for single bundles" in {
    val left = leftOver(lotr, Seq(hobbit,rings,daemon))

    left shouldBe defined
    left.get.length should be (1)
  }

  it should "only take away one instance of bundle items at a time" in {
    val left = leftOver(lotr, Seq(hobbit,rings,hobbit,rings))

    left shouldBe defined
    left.get.length should be (2)
  }

  "bestPrice" should "find the lowest price for a single-bundle order" in {
    val smallOrder: Seq[Item] = Seq(hobbit,rings,dune)

    whenReady(bestPrice(bundles, smallOrder)) { price =>
      price should be (lotr.price + dune.price)
    }
  }

  it should "find the lowest price for a multi-bundle order" in {
    val bigOrder: Seq[Item] = Seq(
      storm,fool,summer,grave,hobbit,rings,
      hobbit,hobbit,dune,robin,robin
    )

    whenReady(bestPrice(bundles, bigOrder)) { price =>
      price should be (lotr.price + jimButcher.price + hobbit.price * 2 +
        dune.price + robin.price * 2
      )
    }
  }

  it should "find the lowest price for orders with repeated bundles" in {
    val order = Seq(hobbit,rings,hobbit,rings)

    whenReady(bestPrice(bundles, order)) { price =>
      price should be (lotr.price * 2)
    }
  }

  it should "find the lowest price for orders whose items are in multiple bundles" in {
    val order = Seq(hobbit,storm,fool,grave,summer,alch)

    whenReady(bestPrice(bundles,order)) { price =>
      price should be (mixed.price + storm.price + fool.price)
    }
  }

  it should "give the default price if bundles weren't possible" in {
    val order = Seq(grave,alch,robin)

    whenReady(bestPrice(bundles, order)) { price =>
      price should be (order.map(_.price).sum)
    }
  }
}
