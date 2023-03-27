package wordsvirtuoso

import java.io.File

fun main(args: Array<String>) {
    if (args.size != 2) {
        println("Error: Wrong number of arguments.")
        return
    }

    val allWordsFile = File(args[0])
    if (!allWordsFile.exists()) {
        println("Error: The words file ${args[0]} doesn't exist.")
        return
    }

    val candidatesFile = File(args[1])
    if (!candidatesFile.exists()) {
        println("Error: The candidate words file ${args[1]} doesn't exist.")
        return
    }

    val allWords = allWordsFile.readText().split("\n").map{ it.lowercase() }.toSet()
    var count = 0
    allWords.forEach { if (isNotValidWord(it)) count++ }
    if (count > 0) {
        println("Error: $count invalid words were found in the ${args[0]} file.")
        return
    }

    val candidates = candidatesFile.readText().split("\n").map{ it.lowercase() }.toSet()
    count = 0
    candidates.forEach { if (isNotValidWord(it)) count++ }
    if (count > 0) {
        println("Error: $count invalid words were found in the ${args[1]} file.")
        return
    }

    val difference = candidates - allWords
    if (difference.isNotEmpty()) {
        println("Error: ${difference.size} candidate words are not included in the ${args[0]} file.")
        return
    }

    play(candidates.random(), allWords)
}

fun play(secretWord: String, allWords: Set<String>) {
    println("Words Virtuoso")
    val wrongChars = mutableSetOf<Char>()
    val clues = mutableListOf<List<String>>()
    val start = System.currentTimeMillis()
    var tries = 1
    while (true) {
        println("Input a 5-letter word:")

        val input = readln().lowercase()
        println()

        if (input == "exit") {
            println("The game is over.")
            return
        }

        clues.forEach { println(it.joinToString("")) }

        if (secretWord == input) {
            val time = (System.currentTimeMillis() - start) / 1000
            input.forEach { print(greenLetter(it)) }
            println("\n")
            println("Correct!")
            println(
                if (clues.isEmpty())
                    "Amazing luck! The solution was found at once."
                else
                    "The solution was found after $tries tries in $time seconds."
            )
            return
        }

        if (isValidWord(input, allWords)) {
            val clue = getClue(input, secretWord)
            clues += clue
            println(clue.joinToString(""))
            println()
            wrongChars += getWrongChars(clue)///////////////////todo
            println(azureString(wrongChars.sorted().joinToString("").uppercase()))
        }

        tries++
        println("\n")
    }
}

fun getClue(word: String, secretWord: String): List<String> {
    val letters = mutableListOf<String>()
    for (i in 0..4) {
        letters += if (word[i] == secretWord[i]) {
            greenLetter(word[i])
        } else if (secretWord.contains(word[i])) {
            yellowLetter(word[i])
        } else {
            greyLetter(word[i])
        }
    }
    return letters
}

fun getWrongChars(clue: List<String>): Set<Char> {//////////////todo
    val chars = mutableSetOf<Char>()
    for (c in clue) {
        if (c.startsWith("\u001B[48:5:7m")) {
            chars += c[9]
        }
    }
    return chars
}

fun greenLetter(ch: Char) = "\u001B[48:5:10m${ch.uppercaseChar()}\u001B[0m"

fun yellowLetter(ch: Char) = "\u001B[48:5:11m${ch.uppercaseChar()}\u001B[0m"

fun greyLetter(ch: Char) = "\u001B[48:5:7m${ch.uppercaseChar()}\u001B[0m"

fun azureString(str: String) = "\u001B[48:5:14m$str\u001B[0m"

fun isValidWord(word: String, allWords: Set<String>): Boolean {
    if (word.length != 5) {
        println("The input isn't a 5-letter word.\n")
        return false
    } else if (!Regex("[a-z]{5}").matches(word)) {
        println("One or more letters of the input aren't valid.\n")
        return false
    } else if (hasDuplicate(word)) {
        println("The input has duplicate letters.\n")
        return false
    } else if (!allWords.contains(word)) {
        println("The input word isn't included in my words list.\n")
        return false
    }
    return true
}

fun isNotValidWord(word: String) = word.length != 5 || !Regex("[a-z]{5}").matches(word) || hasDuplicate(word)

fun hasDuplicate(s: String): Boolean {
    for (i in 0..3) {
        for (j in i + 1..4) {
            if (s[i] == s[j]) return true
        }
    }
    return false
}