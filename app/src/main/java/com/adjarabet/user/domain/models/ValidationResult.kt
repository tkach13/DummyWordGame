package com.adjarabet.user.domain.models

sealed class ValidationResult

sealed class LostReasons()
object LostReasonTwoWordsAdded:LostReasons()
object LostReasonNoWordAdded:LostReasons()
data class LostReasonWordRepeated(val repeatedWord:String):LostReasons()
data class LostReasonIncorrectWord(val incorrectWord:Map<String,String>):LostReasons()

data class ValidationResultOK(val word:String):ValidationResult()

data class  ValidationResultLost(val lostReasons: LostReasons):ValidationResult()

