package com.olafros.exercise5.Service

import org.springframework.stereotype.Service
import java.lang.IllegalArgumentException

@Service
class CalculatorService {

    /**
     * Calculates the input
     */
    fun calculate(arr: List<Any>): Double {
        try {
            checkIsValidArray(arr)
        } catch (e: NumberFormatException) {
            throw IllegalArgumentException("The array doesn't have the correct format")
        }
        var array: List<Any> = arr
        while (array.size >= 3) {
            val sum = calc((array[0] as String).toDouble(), array[1] as String, (array[2] as String).toDouble())
            array = listOf(sum, *(arrayOf(array.drop(3))))
        }
        return array[0] as Double
    }

    /**
     * Checks that the input array consists of valid input.
     * Ex: [Double, String, Double, String, Double, ...]
     */
    fun checkIsValidArray(arr: List<Any>): Boolean {
        for (i in arr.indices) {
            if (i % 2 == 0) {
                if ((arr[i] as String).toDouble() !is Double)
                    return false
            } else {
                if (arr[i] !is String)
                    return false
            }
        }
        return true
    }

    /**
     * Sums two numbers with the operator
     */
    fun calc(number1: Double, sign: String, number2: Double): Double {
        return when (sign) {
            "+" -> number1 + number2;
            "-" -> number1 - number2;
            "/" -> number1 / number2;
            "*" -> number1 * number2;
            else -> 0.0;
        }
    };

}