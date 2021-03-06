package io.pivotal.extremerstartup.question

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory
import com.fasterxml.jackson.module.kotlin.KotlinModule
import io.pivotal.extremerstartup.player.Player
import org.springframework.core.io.ClassPathResource
import kotlin.math.pow
import kotlin.math.sqrt
import kotlin.random.Random

abstract class Question(
        open val question: String = "TEST QUESTION",
        open val answer: String = "TEST ANSWER",
        open val points: Int = 10
) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as Question

        if (question != other.question) return false
        if (answer != other.answer) return false
        if (points != other.points) return false

        return true
    }

    override fun hashCode(): Int {
        var result = question.hashCode()
        result = 31 * result + answer.hashCode()
        result = 31 * result + points
        return result
    }

    override fun toString(): String {
        return "Question(question='$question', answer='$answer', points=$points)"
    }
}

class BasicQuestion(
        override val question: String,
        override val answer: String,
        override val points: Int
) : Question(
        question,
        answer,
        points
)

class WarmUpQuestion(val player: Player) : Question(
        "what is your name",
        player.name,
        1
)

abstract class BinaryMathsQuestion(random: Random, maximum: Int = 20) : Question() {
    val numbers = listOf(random.nextInt(maximum), random.nextInt(maximum))
}

abstract class TernaryMathsQuestion(random: Random, maximum: Int = 20) : Question() {
    val numbers = listOf(random.nextInt(maximum), random.nextInt(maximum), random.nextInt(maximum))
}

abstract class SelectFromListOfNumbersQuestion(
        private val random: Random,
        private val numOptions: Int,
        val filter: (Int) -> Boolean = { _: Int -> true }
) : Question() {

    val numbers: MutableList<Int>

    init {
        numbers = (0 until numOptions).map {
            var num = random.nextInt(0, 1000)
            while (!filter(num)) {
                num = random.nextInt(0, 1000)
            }
            num
        }.toMutableList()
    }

    fun setCorrectAnswer(correctAnswer: Int) {
        numbers.add(correctAnswer)
        numbers.shuffle()
    }
}

class AdditionQuestion(random: Random) : BinaryMathsQuestion(random) {
    override val question = "what is ${numbers[0]} plus ${numbers[1]}"
    override val answer = numbers.sum().toString()
}

class SubtractionQuestion(random: Random) : BinaryMathsQuestion(random) {
    override val question = "what is ${numbers[0]} minus ${numbers[1]}"
    override val answer = (numbers[0] - numbers[1]).toString()
}

class MultiplicationQuestion(random: Random) : BinaryMathsQuestion(random) {
    override val question = "what is ${numbers[0]} multiplied by ${numbers[1]}"
    override val answer = (numbers[0] * numbers[1]).toString()
}

class PowerQuestion(random: Random) : BinaryMathsQuestion(random, 15) {
    override val question = "what is ${numbers[0]} to the power of ${numbers[1]}"
    override val answer = numbers[0].toDouble().pow(numbers[1]).toLong().toString()
    override val points = 20
}

class AdditionAdditionQuestion(random: Random) : TernaryMathsQuestion(random) {
    override val question = "what is ${numbers[0]} plus ${numbers[1]} plus ${numbers[2]}"
    override val answer = numbers.sum().toString()
    override val points = 30
}

class AdditionMultiplicationQuestion(random: Random) : TernaryMathsQuestion(random) {
    override val question = "what is ${numbers[0]} plus ${numbers[1]} multiplied by ${numbers[2]}"
    override val answer = ((numbers[0] + numbers[1]) * numbers[2]).toString()
    override val points = 60
}

class MultiplicationAdditionQuestion(random: Random) : TernaryMathsQuestion(random) {
    override val question = "what is ${numbers[0]} multiplied by ${numbers[1]} plus ${numbers[2]}"
    override val answer = ((numbers[0] * numbers[1]) + numbers[2]).toString()
    override val points = 50
}

class SquareCubeQuestion(random: Random) : SelectFromListOfNumbersQuestion(random, 4, { it != 1 }) {
    override val question get() = "which of the following numbers is both a square and a cube: ${numbers.joinToString(", ")}"
    override val answer = if (random.nextBoolean()) "64" else "729"
    override val points = 30

    init {
        setCorrectAnswer(answer.toInt())
    }
}

class MaximumQuestion(random: Random) : SelectFromListOfNumbersQuestion(
        random,
        random.nextInt(3, 5)
) {
    override val question get() = "which of the following numbers is the largest: ${numbers.joinToString(", ")}"
    override val answer = numbers.max().toString()
    override val points = 40
}

class MinimumQuestion(random: Random) : SelectFromListOfNumbersQuestion(
        random,
        random.nextInt(3, 5)
) {
    override val question get() = "which of the following numbers is the smallest: ${numbers.joinToString(", ")}"
    override val answer = numbers.min().toString()
    override val points = 40
}

class PrimesQuestion(random: Random) : SelectFromListOfNumbersQuestion(
        random,
        5,
        { !it.isPrime() }
) {
    override val question get() = "which of the following numbers is prime: ${numbers.joinToString(", ")}"
    override val answer: String
    override val points = 60

    init {
        var num = random.nextInt(0, 1000)
        while (!num.isPrime()) {
            num = random.nextInt(0, 1000)
        }
        setCorrectAnswer(num)
        answer = num.toString()
    }
}

class GeneralKnowledgeQuestion(random: Random) : Question() {
    val questions: List<ExternalQuestion>
    override val question: String
    override val answer: String
    override val points = 45

    init {
        questions = loadGeneralKnowledgeFromFile()
        val num = random.nextInt(questions.size)
        question = questions[num].question
        answer = questions[num].answer
    }
}

class FibonacciQuestion(random: Random) : Question() {
    val index = random.nextInt(1, 92)
    override val question = "what is the ${index.ordinal()} number in the Fibonacci sequence"
    override val answer = nthFibonacciNumber(index).toString()
    override val points = 50
}

class SquareQuestion(random: Random) : Question() {
    val num = random.nextInt(1, 92)
    override val question = "what is ${num} squared"
    override val answer = num.times(num).toString()
    override val points = 10
}
//
//class TranslateToGerman(random: Random): Question() {
//    val index = random.nextInt(0, 20)
//    override val question = "what is the German translation of ${index}"
//    override val answer = when(index) {
//        1 -> "eins"
//        2 -> "siebzehn" // 17
//        3 -> "zwanzig" // 20
//        3 -> "fünfundsechzig" //65
//
//        else -> "null"
//    }
//}

fun Int.ordinal() =
        "$this${when (this.toString().last()) {
            '1' -> "st"
            '2' -> "st"
            '3' -> "rd"
            else -> "th"
        }}"

class AnagramQuestion(random: Random) : Question() {
    override val question: String
    override val answer: String
    override val points = 45

    init {
        val anagrams = loadAnagramsFromFile()
        val num = random.nextInt(anagrams.size)
        val anagram = anagrams[num]
        val answers = anagram.incorrect.plus(anagram.correct).shuffled()
        question = "which of the following is an anagram of ${anagram.anagram}: ${answers.joinToString(", ")}"
        answer = anagram.correct
    }
}


class EnglishScrabbleQuestion(random: Random) : Question() {
    private val words = listOf("banana", "september", "cloud", "zoo", "ruby", "buzzword")
    override val question: String
    override val answer: String
    override val points = 80

    init {
        val num = random.nextInt(words.size)
        val word = words[num]
        question = "what is the English scrabble score of ${word}"
        answer = word.map { it.englishScrabbleScore() }.sum().toString()
    }
}

class GermanScrabbleQuestion(random: Random) : Question() {
    private val words = listOf("zeit", "für", "brot", "immer", "punkt", "genau")
    override val question: String
    override val answer: String
    override val points = 80

    init {
        val num = random.nextInt(words.size)
        val word = words[num]
        question = "what is the German scrabble score of ${word}"
        answer = word.map { it.germanScrabbleScore() }.sum().toString()
    }
}

fun Char.englishScrabbleScore(): Int {
    return when (this) {
        'e', 'a', 'i', 'o', 'n', 'r', 't', 'l', 's', 'u' -> 1
        'd', 'g' -> 2
        'b', 'c', 'm', 'p' -> 3
        'f', 'h', 'v', 'w', 'y' -> 4
        'k' -> 5
        'j', 'x' -> 8
        'q', 'z' -> 10
        else -> 0
    }
}

fun Char.germanScrabbleScore(): Int {
    return when (this) {
        'd', 'a', 'i', 'r', 't', 'u', 's', 'n', 'e' -> 1
        'g', 'l', 'o', 'h' -> 2
        'w', 'z', 'b', 'm' -> 3
        'p', 'c', 'f', 'k' -> 4
        'ä', 'j', 'ü', 'v' -> 5
        'ö', 'x' -> 8
        'q', 'y' -> 10
        else -> 0
    }
}

fun Int.primeFactors(): List<Int> {
    val max = sqrt(this.toDouble()).toInt()

    return when {
        this < 3 -> listOf(this)
        else -> listOf(listOf(1, 2), (3..max step 2).toList())
                .flatten()
                .filter { this.rem(it) == 0 }
    }
}

fun Int.isPrime(): Boolean {
    return when {
        this == 0 -> false
        this == 1 -> false
        else -> this.primeFactors().size == 1
    }
}

val fibs = mutableMapOf<Int, Long>()

fun nthFibonacciNumber(n: Int): Long {
    return when (n) {
        1 -> 1L
        2 -> 1L
        else -> fibs.getOrPut(n - 1) { nthFibonacciNumber(n - 1) } + fibs.getOrPut(n - 2) { nthFibonacciNumber(n - 2) }
    }
//    return (((1 + sqrt(5.0)).pow(n) - (1 - sqrt(5.0)).pow(n)) / (sqrt(5.0) * 2.0.pow(n))).toLong()
}


fun loadGeneralKnowledgeFromFile(): List<ExternalQuestion> {
    val mapper = ObjectMapper(YAMLFactory()) // Enable YAML parsing
    mapper.registerModule(KotlinModule()) // Enable Kotlin support

    val ref = mapper.typeFactory.constructCollectionType(MutableList::class.java, ExternalQuestion::class.java)

    return ClassPathResource("questions/general-knowledge.yaml").file.bufferedReader().use { mapper.readValue<List<ExternalQuestion>>(it, ref) }
}

fun loadAnagramsFromFile(): List<Anagram> {
    val mapper = ObjectMapper(YAMLFactory()) // Enable YAML parsing
    mapper.registerModule(KotlinModule()) // Enable Kotlin support

    val ref = mapper.typeFactory.constructCollectionType(MutableList::class.java, Anagram::class.java)

    return ClassPathResource("questions/anagrams.yaml").file.bufferedReader().use { mapper.readValue<List<Anagram>>(it, ref) }
}


data class ExternalQuestion(
        val question: String,
        val answer: String
)

data class Anagram(
        val anagram: String,
        val correct: String,
        val incorrect: List<String>
)