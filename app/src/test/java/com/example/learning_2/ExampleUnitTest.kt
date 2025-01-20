package com.example.learning_2

import org.junit.Test

import org.junit.Assert.*

fun hello(): String {
    return "helloworld"
}
/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * See [testing documentation](http://d.android.com/tools/testing).
 */
class ExampleUnitTest {
    @Test
    fun addition_isCorrect() {
        println(hello())
    }
}