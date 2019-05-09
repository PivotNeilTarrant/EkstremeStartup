package io.pivotal.extremerstartup.question

import kotlin.math.pow
import kotlin.random.Random

abstract class BinaryMathsQuestion(random: Random, maximum: Int = 20) : Question() {
    val numbers = listOf(random.nextInt(maximum), random.nextInt(maximum))
}

abstract class TernaryMathsQuestion(random: Random, maximum: Int = 20) : Question() {
    val numbers = listOf(random.nextInt(maximum), random.nextInt(maximum), random.nextInt(maximum))
}

class AdditionQuestion(random: Random) : BinaryMathsQuestion(random) {
    override val question = "what is ${numbers[0]} plus ${numbers[1]}"
    override val answer = numbers.sum()
}

class SubtractionQuestion(random: Random) : BinaryMathsQuestion(random) {
    override val question = "what is ${numbers[0]} minus ${numbers[1]}"
    override val answer = (numbers[0] - numbers[1])
}

class MultiplicationQuestion(random: Random) : BinaryMathsQuestion(random) {
    override val question = "what is ${numbers[0]} multiplied by ${numbers[1]}"
    override val answer = (numbers[0] * numbers[1])
}

class PowerQuestion(random: Random) : BinaryMathsQuestion(random, 15) {
    override val question = "what is ${numbers[0]} to the power of ${numbers[1]}"
    override val answer = numbers[0].toDouble().pow(numbers[1]).toLong()
    override val points = 20
}

class AdditionAdditionQuestion(random: Random) : TernaryMathsQuestion(random) {
    override val question = "what is ${numbers[0]} plus ${numbers[1]} plus ${numbers[2]}"
    override val answer = numbers.sum()
    override val points = 30
}

class AdditionMultiplicationQuestion(random: Random) : TernaryMathsQuestion(random) {
    override val question = "what is ${numbers[0]} plus ${numbers[1]} multiplied by ${numbers[2]}"
    override val answer = ((numbers[0] + numbers[1]) * numbers[2])
    override val points = 60
}

class MultiplicationAdditionQuestion(random: Random) : TernaryMathsQuestion(random) {
    override val question = "what is ${numbers[0]} multiplied by ${numbers[1]} plus ${numbers[2]}"
    override val answer = ((numbers[0] * numbers[1]) + numbers[2])
    override val points = 50
}