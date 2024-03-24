package com.tejasdev.bunkbuddy

//import org.junit.jupiter.api.Test
import org.junit.runner.RunWith
import org.junit.runners.JUnit4
import org.junit.Assert.*
import org.junit.Test

@RunWith(JUnit4::class)
class TestClass1 {

    @Test
    fun credential_isCorrect(){
        assertEquals(
            RegistrationUtil.checkIfCredentialsValid(
                "tejas",
                "passwordd",
                "passwordd"
            ),
            true
        )
    }
}