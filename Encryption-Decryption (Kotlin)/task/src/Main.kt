package encryptdecrypt

import java.io.File

const val ABC = "abcdefghijklmnopqrstuvwxyz"

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
    val algorithm = parameters["-alg"] ?: "shift"

    when (operation) {
        "enc" -> when {
            parameters.containsKey("-data") -> encryptMessage(message, outputFile, key, algorithm)
            parameters.containsKey("-in") -> encryptFile(inputFile, outputFile, key, algorithm)
            else -> encryptMessage("", outputFile, key, algorithm)
        }

        "dec" -> when {
            parameters.containsKey("-data") -> decryptMessage(message, outputFile, key, algorithm)
            parameters.containsKey("-in") -> decryptFile(inputFile, outputFile, key, algorithm)
            else -> decryptMessage("", outputFile, key, algorithm)
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

private fun encryptMessage(message: String, outputFile: String?, key: Int, algorithm: String) {
    val outFile = outputFile?.let { File(it) }
    outFile?.writeText("")
    val ciphertext = message.toCharArray()
    when (algorithm) {
        "shift" -> {
            message.forEachIndexed { index, ch ->
                if (ch.isLetter()) {
                    val ind = ABC.indexOf(ch)
                    ciphertext[index] = when {
                        ind + key < ABC.length -> ABC[ind + key]
                        else -> ABC[ind + key - 26]
                    }
                }
            }
        }
        "unicode" -> {
            message.forEachIndexed { index, ch ->
                ciphertext[index] = (ch.code + key).toChar()
            }
        }
    }
    outFile?.appendText("${ciphertext.joinToString("")}\n") ?: println(ciphertext)
}

private fun encryptFile(inputFile: String, outputFile: String?, key: Int, algorithm: String) {
    val file = File(inputFile)
    val outFile = outputFile?.let { File(it) }
    outFile?.writeText("")
    if (file.exists()) {
        file.forEachLine {
            val line = it.toCharArray()
            when (algorithm) {
                "shift" -> {
                    it.forEachIndexed { index, ch ->
                        if (ch.isLetter()) {
                            val ind = ABC.indexOf(ch)
                            line[index] = when {
                                ind + key < ABC.length -> ABC[ind + key]
                                else -> ABC[ind + key - 26]
                            }
                        }
                    }
                }
                "unicode" -> {
                    it.forEachIndexed { index, ch ->
                        line[index] = (ch.code + key).toChar()
                    }
                }
            }
            outFile?.appendText("${line.joinToString("")}\n") ?: println(line)
        }
    } else println("Error. File doesn't exist")
}

private fun decryptMessage(ciphertext: String, outputFile: String?, key: Int, algorithm: String) {
    val outFile = outputFile?.let { File(it) }
    outFile?.writeText("")
    val message = ciphertext.toCharArray()
    when (algorithm) {
        "shift" -> {
            ciphertext.forEachIndexed { index, ch ->
                if (ch.isLetter()) {
                    val ind = ABC.indexOf(ch)
                    message[index] = when {
                        ind - key >= 0 -> ABC[ind - key]
                        else -> ABC[ind - key + 26]
                    }
                }
            }
        }
        "unicode" -> {
            ciphertext.forEachIndexed { index, ch ->
                message[index] = (ch.code - key).toChar()
            }
        }
    }
    outFile?.appendText("${message.joinToString("")}\n") ?: println(message)
}

private fun decryptFile(inputFile: String, outputFile: String?, key: Int, algorithm: String) {
    val file = File(inputFile)
    val outFile = outputFile?.let { File(it) }
    outFile?.writeText("")
    if (file.exists()) {
        file.forEachLine {
            val line = it.toCharArray()
            when (algorithm) {
                "shift" -> {
                    it.forEachIndexed { index, ch ->
                        if (ch.isLetter()) {
                            val ind = ABC.indexOf(ch)
                            line[index] = when {
                                ind - key >= 0 -> ABC[ind - key]
                                else -> ABC[ind - key + 26]
                            }
                        }
                    }
                }
                "unicode" -> {
                    it.forEachIndexed { index, ch ->
                        line[index] = (ch.code - key).toChar()
                    }
                }
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