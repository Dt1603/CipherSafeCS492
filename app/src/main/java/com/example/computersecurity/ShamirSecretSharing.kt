package com.example.computersecurity
import java.math.BigInteger
import java.security.SecureRandom
import java.util.ArrayList

    class ShamirSecretSharing {
        private val random = SecureRandom()


        // Generate a random polynomial of degree n-1 with secret s
        private fun generatePolynomial(n: Int, s: BigInteger): Array<BigInteger> {
            val poly = arrayOfNulls<BigInteger>(n)
            poly[0] = s
            for (i in 1 until n) {
                poly[i] = BigInteger(256, random)
            }
            return poly.requireNoNulls()
        }

        // Evaluate the polynomial at x
        private fun evaluatePolynomial(poly: Array<BigInteger>, x: BigInteger): BigInteger {
            var result = BigInteger.ZERO
            for (i in poly.indices.reversed()) {
                result = result.multiply(x).add(poly[i])
            }
            return result
        }

        // Generate n shares of the secret s, with k required to reconstruct
        fun generateShares(n: Int, k: Int, s: BigInteger): List<Share> {
            require(k <= n) { "k must be less than or equal to n" }
            val poly = generatePolynomial(k, s)
            val shares: MutableList<Share> = ArrayList()
            for (i in 1..n) {
                val x = BigInteger.valueOf(i.toLong())
                val y = evaluatePolynomial(poly, x)
                shares.add(Share(x, y))
            }
            return shares
        }

        // Reconstruct the secret from k shares
        fun reconstructSecret(shares: List<Share>): BigInteger {
            val k = shares.size
            val x = arrayOfNulls<BigInteger>(k)
            val y = arrayOfNulls<BigInteger>(k)
            for (i in 0 until k) {
                x[i] = shares[i].x
                y[i] = shares[i].y
            }
            var result = BigInteger.ZERO
            for (i in 0 until k) {
                var numerator = BigInteger.ONE
                var denominator = BigInteger.ONE
                for (j in 0 until k) {
                    if (i != j) {
                        numerator = numerator.multiply(x[j]!!.negate())
                        denominator = denominator.multiply(x[i]!!.subtract(x[j]!!)).mod(BigInteger.valueOf(256))
                    }
                }
                val term = y[i]?.multiply(numerator)?.multiply(denominator.modInverse(BigInteger.valueOf(256)))
                result = result.add(term).mod(BigInteger.valueOf(256))
            }
            return result
        }

        data class Share(val x: BigInteger, val y: BigInteger)
    }


    fun main() {
        val sss = ShamirSecretSharing()
        val n = 5 // number of shares to generate
        val k = 3 // number of shares required to reconstruct
        val secret = BigInteger("12345678901234567890")

// Generate shares
        val shares = sss.generateShares(n, k, secret)
        println("Generated ${shares.size} shares:")
        for (share in shares) {
            println(" ${share.x} : ${share.y}")

        }
    }



