package com.adjarabet.user.common.helpers

import com.adjarabet.user.common.extensions.getTrimmedList
import com.adjarabet.user.domain.models.*

class WordsValidator() {
    val words = mutableListOf<String>()
    fun doValidation(word: String): ValidationResult {
        val wordAdded = word.split(' ').last().trim()
        val wordsRepeated = word.split(' ').getTrimmedList().apply {
            removeLast()
        }
        val completeWordList = word.split(' ').getTrimmedList()
        val incorrectWordMap = mutableMapOf<String, String>()
        return when {
            checkForTwoWords(completeWordList) -> {
                ValidationResultLost(LostReasonTwoWordsAdded)
            }
            checkForNoWordAdded(completeWordList) -> {
                ValidationResultLost(LostReasonNoWordAdded)
            }
            checkForRepetition(wordAdded) -> {
                ValidationResultLost(LostReasonWordRepeated(wordAdded))
            }
            checkForIncorrectWord(wordsRepeated,incorrectWordMap).isNotEmpty() -> {
                ValidationResultLost(LostReasonIncorrectWord(incorrectWordMap))
            }
            else -> {
                ValidationResultOK(word)
            }
        }
    }

    private fun checkForNoWordAdded(wordsRepeated: List<String>): Boolean {
        return wordsRepeated.size - words.size < 1
    }

    private fun checkForIncorrectWord(wordsRepeated: List<String>, incorrectWordMap: MutableMap<String, String>)
    : Map<String, String> {
        if (words.isNotEmpty()) {
            wordsRepeated.forEachIndexed { index, s ->
                    if (s != words[index]) {
                    incorrectWordMap.put(s, words[index])
                }
            }
        }

        return incorrectWordMap

    }

    private fun checkForRepetition(wordAdded: String): Boolean {
        return words.contains(wordAdded)
    }

    private fun checkForTwoWords(completeWordList: List<String>): Boolean {
        return (completeWordList.size - words.size) > 1
    }
}

