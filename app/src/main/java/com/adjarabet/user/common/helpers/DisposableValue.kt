package com.adjarabet.user.common.helpers

class DisposableValue<T>(private val value: T) {

    private var isAlreadyUsed: Boolean = false

    fun getValue(): T? {
        return if (isAlreadyUsed) {
            null
        } else {
            isAlreadyUsed = true
            value
        }
    }
}