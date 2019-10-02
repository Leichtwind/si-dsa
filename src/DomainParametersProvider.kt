import java.math.BigInteger
import java.util.*

class DomainParametersProvider {

  private val random = Random()

  val q: BigInteger = BigInteger.probablePrime(N, random)
  val p = computeP()
  val g = computeG()

  private fun computeG(): BigInteger {
    var probableG: BigInteger

    var h = BigInteger.ONE
    do {
      probableG = (++h).modPow((p - BigInteger.ONE) / q, p)
    } while (probableG == BigInteger.ONE)

    return probableG
  }

  private fun computeP(): BigInteger {
    val max = BigInteger.valueOf(2).pow(L)
    val min = BigInteger.valueOf(2)

    var probableP = (min.pow(L - 1) / q + BigInteger.ONE) * q + BigInteger.ONE

    // 100 is the default value is for probablePrime
    while (!probableP.isProbablePrime(100) && probableP < max) {
      probableP += q
    }

    assert(probableP < max)

    return probableP
  }

  companion object {
    const val L = 3072
    const val N = 256
  }
}