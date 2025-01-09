package encryptdecrypt

fun main() {
    val operation = readln()
    val message = readln()
    val key = readln().toInt()
    when (operation) {
        "enc" -> encryptMessage(message, key)
        "dec" -> decryptMessage(message, key)
    }
}

private fun encryptMessage(message: String, key: Int) {
    val ciphertext = message.toCharArray()
    message.forEachIndexed { index, ch ->
        ciphertext[index] = (ch.code + key).toChar()
    }
    println(ciphertext)
}

private fun decryptMessage(ciphertext: String, key: Int) {
    val message = ciphertext.toCharArray()
    ciphertext.forEachIndexed { index, ch ->
        message[index] = (ch.code - key).toChar()
    }
    println(message)
}