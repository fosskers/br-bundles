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

  val bundles: Seq[Bundle] = Seq(lotr,jimButcher)
  val bigOrder: Seq[Item] = Seq(
    storm,fool,summer,grave,hobbit,rings,
    hobbit,hobbit,alch,dune,robin,robin
  )
  val smallOrder: Seq[Item] = Seq(hobbit,rings,dune)

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

  val smallFut: Future[Float] = bestPrice(bundles, smallOrder)
  val bigFut: Future[Float] = bestPrice(bundles, bigOrder)

  "bestPrice" should "find the lowest price for a single-bundle order" in {
    whenReady(smallFut) { price =>
      price should be (22)
    }
  }

  it should "find the lowest price for a multi-bundle order" in {
    whenReady(bigFut) { price =>
      price should be (95)
    }
  }

  it should "find the lowest price for orders with repeated bundles" in {
    val order = Seq(hobbit,rings,hobbit,rings)

    whenReady(bestPrice(bundles, order)) { price =>
      price should be (24)
    }
  }

  it should "give the default price if bundles weren't possible" in {
    val order = Seq(grave,alch,robin)

    whenReady(bestPrice(bundles, order)) { price =>
      price should be (order.map(_.price).sum)
    }
  }
}
