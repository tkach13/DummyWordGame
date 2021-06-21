package com.adjarabet.user

import com.adjarabet.user.common.helpers.WordsValidator
import com.adjarabet.user.domain.models.LostReasonIncorrectWord
import com.adjarabet.user.domain.models.ValidationResultLost
import com.adjarabet.user.domain.models.ValidationResultOK
import org.junit.*
import java.lang.StringBuilder

class WordsValidatorTests {
    private val wordsValidator = WordsValidator()
    private val initialList = listOf("some", "\n", "domdom")
    @Before
    fun setup(){
        wordsValidator.words.addAll(initialList)
    }
    @Test
    fun  checkValidationOkTest() {
        val newWord = StringBuilder ()
        initialList.forEach {
            newWord.append(it)
            newWord.append(' ')
        }
        Assert.assertTrue(wordsValidator.doValidation("$newWord jira") is ValidationResultOK)
    }
    @Test
    fun checkValidationIncorrectWord(){
        val newWord = "some \n domdo mec"
        Assert.assertTrue((wordsValidator.doValidation(newWord) as ValidationResultLost).lostReasons is LostReasonIncorrectWord   )
    }
    @After
    fun clearData(){
        wordsValidator.words.clear()
    }


}