package encryptdecrypt

fun main(args: Array<String>) {
    val parameters = parseArgs(args)

    val operation = parameters["-mode"] ?: "enc"
    val key = parameters["-key"]?.toInt() ?: 0
    val message = parameters["-data"] ?: ""

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

private fun parseArgs(args: Array<String>): MutableMap<String, String> {
    val result = mutableMapOf<String, String>()
    for (i in args.indices step 2) result[args[i]] = args[i + 1]
    return result
}