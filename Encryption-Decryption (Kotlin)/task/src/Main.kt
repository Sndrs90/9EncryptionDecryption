package encryptdecrypt

fun main() {
    val message = "we found a treasure!"
    var ciphertext = message.toCharArray()
    message.forEachIndexed { index, ch ->
        if (ch.isLetter()) {
            val newLetter = (219 - ch.code).toChar()
            ciphertext[index] = newLetter
        }
    }
    println(ciphertext)
}