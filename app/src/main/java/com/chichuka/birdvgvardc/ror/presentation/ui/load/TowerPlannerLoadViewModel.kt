package com.chichuka.birdvgvardc.ror.presentation.ui.load

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.chichuka.birdvgvardc.ror.data.shar.TowerPlannerSharedPreference
import com.chichuka.birdvgvardc.ror.data.utils.TownPlannerSystemService
import com.chichuka.birdvgvardc.ror.domain.usecases.TowerPlannerGetAllUseCase
import com.chichuka.birdvgvardc.ror.presentation.app.BuildMasterApp
import com.chichuka.birdvgvardc.ror.presentation.app.BuildMasterAppsFlyerState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class TowerPlannerLoadViewModel(
    private val towerPlannerGetAllUseCase: TowerPlannerGetAllUseCase,
    private val chickenSharedPreference: TowerPlannerSharedPreference,
    private val volcanoSystemService: TownPlannerSystemService
) : ViewModel() {

    private val _chickenHomeScreenState: MutableStateFlow<ChickenHomeScreenState> =
        MutableStateFlow(ChickenHomeScreenState.ChickenLoading)
    val chickenHomeScreenState = _chickenHomeScreenState.asStateFlow()

    private var chickenGetApps = false


    init {
        viewModelScope.launch {
            when (chickenSharedPreference.chickenAppState) {
                0 -> {
                    if (volcanoSystemService.volcanoIsOnline()) {
                        BuildMasterApp.Companion.chickenConversionFlow.collect {
                            when(it) {
                                BuildMasterAppsFlyerState.BuildMasterDefault -> {}
                                BuildMasterAppsFlyerState.BuildMasterError -> {
                                    chickenSharedPreference.chickenAppState = 2
                                    _chickenHomeScreenState.value =
                                        ChickenHomeScreenState.ChickenError
                                    chickenGetApps = true
                                }
                                is BuildMasterAppsFlyerState.BuildMasterSuccess -> {
                                    if (!chickenGetApps) {
                                        chickenGetData(it.chickenData)
                                        chickenGetApps = true
                                    }
                                }
                            }
                        }
                    } else {
                        _chickenHomeScreenState.value =
                            ChickenHomeScreenState.ChickenNotInternet
                    }
                }
                1 -> {
                    if (volcanoSystemService.volcanoIsOnline()) {
                        if (BuildMasterApp.Companion.TOWNPL_FB_LI != null) {
                            _chickenHomeScreenState.value =
                                ChickenHomeScreenState.ChickenSuccess(
                                    BuildMasterApp.Companion.TOWNPL_FB_LI.toString()
                                )
                        } else if (System.currentTimeMillis() / 1000 > chickenSharedPreference.chickenExpired) {
                            Log.d(BuildMasterApp.Companion.TOWNPLANNER_MAIN_TAG, "Current time more then expired, repeat request")
                            BuildMasterApp.Companion.chickenConversionFlow.collect {
                                when(it) {
                                    BuildMasterAppsFlyerState.BuildMasterDefault -> {}
                                    BuildMasterAppsFlyerState.BuildMasterError -> {
                                        _chickenHomeScreenState.value =
                                            ChickenHomeScreenState.ChickenSuccess(
                                                chickenSharedPreference.chickenSavedUrl
                                            )
                                        chickenGetApps = true
                                    }
                                    is BuildMasterAppsFlyerState.BuildMasterSuccess -> {
                                        if (!chickenGetApps) {
                                            chickenGetData(it.chickenData)
                                            chickenGetApps = true
                                        }
                                    }
                                }
                            }
                        } else {
                            Log.d(BuildMasterApp.Companion.TOWNPLANNER_MAIN_TAG, "Current time less then expired, use saved url")
                            _chickenHomeScreenState.value =
                                ChickenHomeScreenState.ChickenSuccess(
                                    chickenSharedPreference.chickenSavedUrl
                                )
                        }
                    } else {
                        _chickenHomeScreenState.value =
                            ChickenHomeScreenState.ChickenNotInternet
                    }
                }
                2 -> {
                    _chickenHomeScreenState.value =
                        ChickenHomeScreenState.ChickenError
                }
            }
        }
    }


    private suspend fun chickenGetData(conversation: MutableMap<String, Any>?) {
        val chickenData = towerPlannerGetAllUseCase.invoke(conversation)
        if (chickenSharedPreference.chickenAppState == 0) {
            if (chickenData == null) {
                chickenSharedPreference.chickenAppState = 2
                _chickenHomeScreenState.value =
                    ChickenHomeScreenState.ChickenError
            } else {
                chickenSharedPreference.chickenAppState = 1
                chickenSharedPreference.apply {
                    chickenExpired = chickenData.chickenExpires
                    chickenSavedUrl = chickenData.chickenUrl
                }
                _chickenHomeScreenState.value =
                    ChickenHomeScreenState.ChickenSuccess(chickenData.chickenUrl)
            }
        } else  {
            if (chickenData == null) {
                _chickenHomeScreenState.value =
                    ChickenHomeScreenState.ChickenSuccess(chickenSharedPreference.chickenSavedUrl)
            } else {
                chickenSharedPreference.apply {
                    chickenExpired = chickenData.chickenExpires
                    chickenSavedUrl = chickenData.chickenUrl
                }
                _chickenHomeScreenState.value =
                    ChickenHomeScreenState.ChickenSuccess(chickenData.chickenUrl)
            }
        }
    }


    sealed class ChickenHomeScreenState {
        data object ChickenLoading : ChickenHomeScreenState()
        data object ChickenError : ChickenHomeScreenState()
        data class ChickenSuccess(val data: String) : ChickenHomeScreenState()
        data object ChickenNotInternet: ChickenHomeScreenState()
    }
}