package com.adjarabet.user.presentation

import com.adjarabet.user.common.helpers.DisposableValue
import com.adjarabet.user.domain.models.ValidationResult
import com.adjarabet.user.domain.models.ValidationResultLost
import com.adjarabet.user.domain.models.ValidationResultOK

data class MainViewState(
        val error:DisposableValue<Throwable>? = null,
        val validationResultOK:DisposableValue<ValidationResultOK>? = null,
        val wordReceived:DisposableValue<String>? = null,
        val gameLost:DisposableValue<ValidationResultLost>? = null,
        val gameWon:DisposableValue<Boolean>? = null
)
