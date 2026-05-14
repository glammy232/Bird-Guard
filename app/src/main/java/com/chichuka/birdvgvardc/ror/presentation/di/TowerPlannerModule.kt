package com.chichuka.birdvgvardc.ror.presentation.di

import com.chichuka.birdvgvardc.ror.data.repo.TownPlannerRepository
import com.chichuka.birdvgvardc.ror.data.shar.TowerPlannerSharedPreference
import com.chichuka.birdvgvardc.ror.data.utils.TowerPlannerPushToken
import com.chichuka.birdvgvardc.ror.data.utils.TownPlannerSystemService
import com.chichuka.birdvgvardc.ror.domain.usecases.TowerPlannerGetAllUseCase
import com.chichuka.birdvgvardc.ror.presentation.pushhandler.TowerPlannerPushHandler
import com.chichuka.birdvgvardc.ror.presentation.ui.load.TowerPlannerLoadViewModel
import com.chichuka.birdvgvardc.ror.presentation.ui.view.TownPlannerViFun
import org.koin.core.module.dsl.viewModel
import org.koin.dsl.module

val townplannerModule = module {
    factory {
        TowerPlannerPushHandler()
    }
    single {
        TownPlannerRepository()
    }
    single {
        TowerPlannerSharedPreference(get())
    }
    factory {
        TowerPlannerPushToken()
    }
    factory {
        TownPlannerSystemService(get())
    }
    factory {
        TowerPlannerGetAllUseCase(
            get(), get(), get()
        )
    }
    factory {
        TownPlannerViFun(get())
    }
    viewModel {
        TowerPlannerLoadViewModel(
            get(),
            get(),
            get()
        )
    }
}