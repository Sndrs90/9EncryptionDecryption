package encryptdecrypt

import java.io.File

fun main(args: Array<String>) {
    if (!areArgsRight(args)) {
        println("Error")
        return
    }

    val parameters = parseArgs(args)

    val operation = parameters["-mode"] ?: "enc"
    val key = parameters["-key"]?.toInt() ?: 0
    val message = parameters["-data"] ?: ""
    val inputFile = parameters["-in"] ?: ""
    val outputFile = parameters["-out"]

    when (operation) {
        "enc" -> when {
            parameters.containsKey("-data") -> encryptMessage(message, outputFile, key)
            parameters.containsKey("-in") -> encryptFile(inputFile, outputFile, key)
            else -> encryptMessage("", outputFile, key)
        }

        "dec" -> when {
            parameters.containsKey("-data") -> decryptMessage(message, outputFile, key)
            parameters.containsKey("-in") -> decryptFile(inputFile, outputFile, key)
            else -> decryptMessage("", outputFile, key)
        }
    }
}

private fun areArgsRight(args: Array<String>): Boolean {
    for (i in args.indices) {
        if (args[i].first() == '-') {
            try {
                require(i + 1 < args.size && args[i + 1].first() != '-'){"Error"}
            } catch (e: IllegalArgumentException) {
                return false
            }
        }
    }
    return true
}

private fun encryptMessage(message: String, outputFile: String?, key: Int) {
    val outFile = outputFile?.let { File(it) }
    outFile?.writeText("")
    val ciphertext = message.toCharArray()
    message.forEachIndexed { index, ch ->
        ciphertext[index] = (ch.code + key).toChar()

    }
    outFile?.appendText("${ciphertext.joinToString("")}\n") ?: println(ciphertext)
}

private fun encryptFile(inputFile: String, outputFile: String?, key: Int) {
    val file = File(inputFile)
    val outFile = outputFile?.let { File(it) }
    outFile?.writeText("")
    if (file.exists()) {
        file.forEachLine {
            val line = it.toCharArray()
            it.forEachIndexed { index, ch ->
                line[index] = (ch.code + key).toChar()
            }
            outFile?.appendText("${line.joinToString("")}\n") ?: println(line)
        }
    } else println("Error. File doesn't exist")
}

private fun decryptMessage(ciphertext: String, outputFile: String?, key: Int) {
    val outFile = outputFile?.let { File(it) }
    outFile?.writeText("")
    val message = ciphertext.toCharArray()
    ciphertext.forEachIndexed { index, ch ->
        message[index] = (ch.code - key).toChar()
    }
    outFile?.appendText("${message.joinToString("")}\n") ?: println(message)
}

private fun decryptFile(inputFile: String, outputFile: String?, key: Int) {
    val file = File(inputFile)
    val outFile = outputFile?.let { File(it) }
    outFile?.writeText("")
    if (file.exists()) {
        file.forEachLine {
            val line = it.toCharArray()
            it.forEachIndexed { index, ch ->
                line[index] = (ch.code - key).toChar()
            }
            outFile?.appendText("${line.joinToString("")}\n") ?: println(line)
        }
    } else println("Error. File doesn't exist")
}

private fun parseArgs(args: Array<String>): MutableMap<String, String> {
    val result = mutableMapOf<String, String>()
    for (i in args.indices step 2) result[args[i]] = args[i + 1]
    return result
}