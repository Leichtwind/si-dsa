import java.math.BigInteger
import java.security.MessageDigest
import java.util.*
import kotlin.test.assertNotEquals


class DSA(private val parametersProvider: DomainParametersProvider) {

  private val random = Random()
  private val digest = MessageDigest.getInstance("SHA-256")

  // Secret Key
  private val x: BigInteger

  // Open Key
  private val y: BigInteger

  init {
    with(parametersProvider) {
      x = BigInteger(DomainParametersProvider.N, random) % q
      y = g.modPow(x, p)
    }

    assertNotEquals(BigInteger.ZERO, x)
  }

  fun createSign(message: String): Pair<BigInteger, BigInteger> {
    val hash = BigInteger(digest.digest(message.toByteArray()))

    var k: BigInteger
    var r: BigInteger
    var s: BigInteger

    with(parametersProvider) {
      do {
        do {
          do {
            k = BigInteger(DomainParametersProvider.N - 1, random)
          } while (k == BigInteger.ZERO)

          r = g.modPow(k, p) % q
        } while (r == BigInteger.ZERO)

        // ab mod p = (a mod p)(b mod p) mod p
        s = (((hash + x * r) % q) * k.modInverse(q)) % q
      } while (s == BigInteger.ZERO)
    }

    return r to s
  }

  fun verifySign(message: String, sign: Pair<BigInteger, BigInteger>): Boolean {
    with(parametersProvider) {
      assert(sign.first > BigInteger.ZERO && sign.first < q)
      assert(sign.second > BigInteger.ZERO && sign.second < q)

      val hash = BigInteger(digest.digest(message.toByteArray()))

      val w = sign.second.modInverse(q)

      val u1 = hash * w % q
      val u2 = sign.first * w % q

      // ab mod p = (a mod p)(b mod p) mod p
      val v = ((g.modPow(u1, p) % p) * (y.modPow(u2, p) % p)) % p % q

      return v == sign.first
    }
  }
}
