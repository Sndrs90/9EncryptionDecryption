package encryptdecrypt

const val ABC = "abcdefghijklmnopqrstuvwxyz"

fun main() {
    val message = readln()
    val key = readln().toInt()

    val ciphertext = message.toCharArray()
    message.forEachIndexed { index, ch ->
        if (ch.isLetter()) {
            val ind = ABC.indexOf(ch)
            ciphertext[index] = when {
                ind + key < ABC.length -> ABC[ind + key]
                else -> ABC[ind + key - 26]
            }
        }
    }
    println(ciphertext)
}