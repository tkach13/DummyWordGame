package com.adjarabet.user.common.koinModules

import com.adjarabet.user.presentation.MainViewModel
import org.koin.android.viewmodel.dsl.viewModel
import org.koin.dsl.module

val viewModelsModule = module {
    viewModel {
        MainViewModel(get())
    }
}