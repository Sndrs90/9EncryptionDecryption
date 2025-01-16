package encryptdecrypt

import java.io.File

//Константа алфавита для алгоритма shift
const val ABC = "abcdefghijklmnopqrstuvwxyz"

fun main(args: Array<String>) {
    //Проверяем корректность args. Если нет, завершаем программу
    if (!areArgsRight(args)) return

    //Парсим строку args в map parameters: key - название аргумента, value - значение аргумента
    val parameters = parseArgs(args)

    //Блок переменных со значениями аргументов, в том числе по умолчанию, если аргумент не указан
    val operation = parameters["-mode"] ?: "enc"
    val key = parameters["-key"]?.toInt() ?: 0
    val message = parameters["-data"] ?: ""
    val inputFile = parameters["-in"] ?: ""
    val outputFile = parameters["-out"]
    val algorithm = parameters["-alg"] ?: "shift"

    when {
        //Если аргумент -data выполняем шифрование/дешифрование из командной строки
        parameters.containsKey("-data") -> encryptDecryptMessage(operation, message, outputFile, key, algorithm)
        //Если аргумент -in выполняем шифрование/дешифрование из файла
        parameters.containsKey("-in") -> encryptDecryptFile(operation, inputFile, outputFile, key, algorithm)
        //Если не указаны ни -data, ни -in выполняем операцию с пустой строкой
        else -> encryptDecryptMessage(operation,"", outputFile, key, algorithm)
    }
}

//Ф-ция проверки корректности args из ф-ции main()
private fun areArgsRight(args: Array<String>): Boolean {
    //Цикл прохода по всем элементам args
    for (i in args.indices) {
        //Если элемент args начинается с '-' (ключевой), то
        if (args[i].first() == '-') {
            try {
                //Проверяем, что следующий элемент существует и он не ключевой (не начинается с '-')
                require(i + 1 < args.size && args[i + 1].first() != '-'){"Error. The args aren't right"}
            //Иначе ловим исключение
            } catch (e: IllegalArgumentException) {
                println(e.message)
                return false
            }
        }
    }
    return true
}

//Ф-ция парсинга строки args в map
private fun parseArgs(args: Array<String>): MutableMap<String, String> {
    val result = mutableMapOf<String, String>()
    //Присваиваем каждому аргументу его значение
    for (i in args.indices step 2) result[args[i]] = args[i + 1]
    return result
}

/* Ф-ция шифрования/дешифрования сообщения из командной строки
 * operation: enc - шифрование, dec - дешифрование
 * inputText: исходный текст для шифрования/дешифрования
 * outputFile: файл результата, может быть null, тогда вывод в командную строку
 * key: ключ для шифрования
 * algorithm: shift - сдвиг по алфавиту, unicode - сдвиг по таблице unicode
 */
private fun encryptDecryptMessage(
    operation: String, inputText: String, outputFile: String?, key: Int, algorithm: String
) {
    //Для работы с выходным файлом если он требуется
    val outFile = outputFile?.let { File(it) }
    //Перезапись файла пустой строкой перед новым запуском
    outFile?.writeText("")
    //Запускаем ф-цию шифрования/дешифрования
    val outputText = encryptDecryptLine(operation, inputText, key, algorithm)
    //Вывод результата в файл или командную строку
    outFile?.appendText("${outputText.joinToString("")}\n") ?: println(outputText)
}

/* Ф-ция шифрования/дешифрования сообщения из командной строки
 * operation: enc - шифрование, dec - дешифрование
 * inputFile: исходный файл для шифрования/дешифрования
 * outputFile: файл результата, может быть null, тогда вывод в командную строку
 * key: ключ для шифрования
 * algorithm: shift - сдвиг по алфавиту, unicode - сдвиг по таблице unicode
 */
private fun encryptDecryptFile(
    operation: String, inputFile: String, outputFile: String?, key: Int, algorithm: String
) {
    //Входной файл
    val file = File(inputFile)
    //Для работы с выходным файлом если он требуется
    val outFile = outputFile?.let { File(it) }
    //Перезапись файла пустой строкой перед новым запуском
    outFile?.writeText("")
    //Проверка, что входной файл существует
    if (file.exists()) {
        //Для каждой строки исходного файла
        file.forEachLine {
            //Выполняем шифрование/дешифрование
            val line = encryptDecryptLine(operation, it, key, algorithm)
            //Вывод результата в файл или командную строку
            outFile?.appendText("${line.joinToString("")}\n") ?: println(line)
        }
    //Если исходного файла не существует
    } else println("Error. File doesn't exist")
}

//Ф-ция шифрования/дешифрования строки
private fun encryptDecryptLine(
    operation: String, inputText: String, key: Int, algorithm: String
): CharArray {
    //Переменная результата
    val outputText = inputText.toCharArray()
    //Выбор алгоритма
    when (algorithm) {
        //Алгоритм shift (сдвиг по алфавиту)
        "shift" -> {
            //Для каждого символа исходного текста
            inputText.forEachIndexed { index, ch ->
                //Если символ буква
                if (ch.isLetter()) {
                    //Находим порядковый индекс буквы в алфавите. Заглавные буквы перед этим конвертируем в строчные
                    val ind = ABC.indexOf(ch.lowercaseChar())
                    //Выбор типа операции
                    when (operation) {
                        //Шифрование
                        "enc" -> {
                            //Зашифрованный символ равен
                            outputText[index] = when {
                                //Проверяем что индекс буквы в алфавите + ключ не выходит за 'z'
                                //Если символ был изначально заглавным, то снова делаем таким зашифрованный
                                ind + key < ABC.length -> if (ch.isUpperCase()) ABC[ind + key].uppercaseChar() else ABC[ind + key]
                                //Если индекс + ключ выходит за 'z', то снова начинаем отсчет с 'a'
                                else -> if (ch.isUpperCase()) ABC[ind + key - 26].uppercaseChar() else ABC[ind + key - 26]
                            }
                        }

                        //Дешифрование
                        "dec" -> {
                            //Дешифрованный символ равен
                            outputText[index] = when {
                                //Проверяем что индекс буквы в алфавите - ключ не выходит за 'a'
                                //Если символ был изначально заглавным, то делаем таким дешифрованный
                                ind - key >= 0 -> if (ch.isUpperCase()) ABC[ind - key].uppercaseChar() else ABC[ind - key]
                                //Если индекс - ключ выходит за 'a', то начинаем обратный отсчет с 'z'
                                else -> if (ch.isUpperCase()) ABC[ind - key + 26].uppercaseChar() else ABC[ind - key + 26]
                            }
                        }
                    }
                }
            }
        }

        //Алгоритм unicode (сдвиг по таблице unicode)
        "unicode" -> {
            //Для каждого символа исходного текста
            inputText.forEachIndexed { index, ch ->
                //Зашифрованный символ равен
                outputText[index] = when (operation) {
                    //Если шифрование, то прибавляем к unicode коду ключ
                    "enc" -> (ch.code + key).toChar()
                    //Если дешифрование, то вычитаем из unicode кода ключ
                    else -> (ch.code - key).toChar()
                }
            }
        }
    }
    //Возврат результата
    return outputText
}