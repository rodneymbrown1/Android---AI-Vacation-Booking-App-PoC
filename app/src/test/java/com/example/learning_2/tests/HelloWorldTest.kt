package com.example.learning_2.tests

import junit.framework.TestCase.assertEquals
import org.junit.Test

class HelloWorldTest {

    @Test
    fun testHelloWorld() {
        val message = "Hello, World!"
        println(message)  // This will print in the test output
        assertEquals("Hello, World!", message)
    }
}
