package com.adjarabet.user.common.koinModules

import com.adjarabet.user.common.helpers.WordsValidator
import org.koin.dsl.module

val validatorsModule = module {
    single {
        WordsValidator()
    }
}