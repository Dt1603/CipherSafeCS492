package com.example.computersecurity

class TEA(private val key: ByteArray) {
    companion object {
        private const val KEY_LENGTH = 16
        private const val ROUNDS = 32
        private const val DELTA = 0x9e3779b9.toInt()
    }

    init {
        require(key.size == KEY_LENGTH) { "Invalid key length" }
    }

    fun encrypt(data: String): ByteArray {
        val paddedData = if (data.length % 8 == 0) {
            data
        } else {
            val padding = 8 - (data.length % 8)
            data.padEnd(data.length + padding, ' ')
        }
        val result = ByteArray(paddedData.length)
        for (i in paddedData.indices step 8) {
            val v = IntArray(2)
            v[0] = (paddedData[i].code and 0xff shl 24) or ((paddedData[i + 1].code and 0xff) shl 16) or ((paddedData[i + 2].toInt() and 0xff) shl 8) or (paddedData[i + 3].toInt() and 0xff)
            v[1] = (paddedData[i + 4].code and 0xff shl 24) or ((paddedData[i + 5].code and 0xff) shl 16) or ((paddedData[i + 6].toInt() and 0xff) shl 8) or (paddedData[i + 7].toInt() and 0xff)
            var sum = 0
            for (j in 0 until ROUNDS) {
                sum += DELTA
                v[0] += ((v[1] shl 4) + key[0]) xor (v[1] + sum) xor ((v[1] ushr 5) + key[1])
                v[1] += ((v[0] shl 4) + key[2]) xor (v[0] + sum) xor ((v[0] ushr 5) + key[3])
            }
            result[i] = (v[0] ushr 24).toByte()
            result[i + 1] = (v[0] ushr 16).toByte()
            result[i + 2] = (v[0] ushr 8).toByte()
            result[i + 3] = v[0].toByte()
            result[i + 4] = (v[1] ushr 24).toByte()
            result[i + 5] = (v[1] ushr 16).toByte()
            result[i + 6] = (v[1] ushr 8).toByte()
            result[i + 7] = v[1].toByte()
        }
        return result
    }

    fun decrypt(data: String): ByteArray {
        return encrypt(data)
    }
}
