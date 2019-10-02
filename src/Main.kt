fun main() {
  val dsa = DSA(DomainParametersProvider())

  val message = readLine() ?: ""

  val sign = dsa.createSign(message)
  val result = dsa.verifySign(message, sign)

  println(result)
}
