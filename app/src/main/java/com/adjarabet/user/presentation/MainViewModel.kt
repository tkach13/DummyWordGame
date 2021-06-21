package com.adjarabet.user.presentation

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.adjarabet.common.UserBotCommonLogicConstants.BOT_LOSING_ERR_MESSAGE
import com.adjarabet.user.common.extensions.getLastWord
import com.adjarabet.user.common.helpers.DisposableValue
import com.adjarabet.user.common.helpers.WordsValidator
import com.adjarabet.user.domain.models.ValidationResultLost
import com.adjarabet.user.domain.models.ValidationResultOK

class MainViewModel(private val wordsValidator: WordsValidator):ViewModel() {
    private val mainViewState = MainViewState()
    private val liveViewState =  MutableLiveData<MainViewState> ()
    fun getLiveViewState():LiveData<MainViewState>{
        return liveViewState
    }

    fun sendWordIntent(word: String) {
        val validationResult = wordsValidator.doValidation(word)
        if (validationResult is ValidationResultOK){
           wordsValidator.words.add(word.getLastWord())
            sendState(mainViewState.copy(validationResultOK = DisposableValue(validationResult)))
        } else if (validationResult is ValidationResultLost){
            wordsValidator.words.clear()
            sendState(mainViewState.copy(gameLost = DisposableValue(validationResult)))
        }
    }
    private fun sendState(viewState: MainViewState){
        liveViewState.postValue(viewState)
    }

    fun onWordReceivedIntent(word: String) {
        wordsValidator.words.add(word.getLastWord())
        if (word == BOT_LOSING_ERR_MESSAGE ){
            wordsValidator.words.clear()
            sendState(mainViewState.copy(gameWon = DisposableValue(true)))
        } else {
            sendState(mainViewState.copy(wordReceived = DisposableValue(word) ))
        }
    }

}