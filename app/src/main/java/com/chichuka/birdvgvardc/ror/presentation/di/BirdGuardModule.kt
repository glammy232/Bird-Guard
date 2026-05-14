package com.chichuka.birdvgvardc.ror.presentation.di

import com.chichuka.birdvgvardc.ror.data.repo.TownPlannerRepository
import com.chichuka.birdvgvardc.ror.data.shar.BirdGuardSharedPreference
import com.chichuka.birdvgvardc.ror.data.utils.BirdGuardPushToken
import com.chichuka.birdvgvardc.ror.data.utils.BirdGuardSystemService
import com.chichuka.birdvgvardc.ror.domain.usecases.BirdGuardGetAllUseCase
import com.chichuka.birdvgvardc.ror.presentation.pushhandler.BirdGuardPushHandler
import com.chichuka.birdvgvardc.ror.presentation.ui.load.BirdGuardLoadViewModel
import com.chichuka.birdvgvardc.ror.presentation.ui.view.BirdGuardViFun
import org.koin.core.module.dsl.viewModel
import org.koin.dsl.module

val townplannerModule = module {
    factory {
        BirdGuardPushHandler()
    }
    single {
        TownPlannerRepository()
    }
    single {
        BirdGuardSharedPreference(get())
    }
    factory {
        BirdGuardPushToken()
    }
    factory {
        BirdGuardSystemService(get())
    }
    factory {
        BirdGuardGetAllUseCase(
            get(), get(), get()
        )
    }
    factory {
        BirdGuardViFun(get())
    }
    viewModel {
        BirdGuardLoadViewModel(
            get(),
            get(),
            get()
        )
    }
}