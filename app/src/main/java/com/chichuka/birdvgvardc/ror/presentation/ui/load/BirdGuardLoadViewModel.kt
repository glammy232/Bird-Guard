package com.chichuka.birdvgvardc.ror.presentation.ui.load

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.chichuka.birdvgvardc.ror.data.shar.BirdGuardSharedPreference
import com.chichuka.birdvgvardc.ror.data.utils.BirdGuardSystemService
import com.chichuka.birdvgvardc.ror.domain.usecases.BirdGuardGetAllUseCase
import com.chichuka.birdvgvardc.ror.presentation.app.BirdGuardApp
import com.chichuka.birdvgvardc.ror.presentation.app.BirdGuardAppsFlyerState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class BirdGuardLoadViewModel(
    private val birdGuardGetAllUseCase: BirdGuardGetAllUseCase,
    private val chickenSharedPreference: BirdGuardSharedPreference,
    private val volcanoSystemService: BirdGuardSystemService
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
                        BirdGuardApp.Companion.chickenConversionFlow.collect {
                            when(it) {
                                BirdGuardAppsFlyerState.BirdGuardDefault -> {}
                                BirdGuardAppsFlyerState.BirdGuardError -> {
                                    chickenSharedPreference.chickenAppState = 2
                                    _chickenHomeScreenState.value =
                                        ChickenHomeScreenState.ChickenError
                                    chickenGetApps = true
                                }
                                is BirdGuardAppsFlyerState.BirdGuardSuccess -> {
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
                        if (BirdGuardApp.Companion.TOWNPL_FB_LI != null) {
                            _chickenHomeScreenState.value =
                                ChickenHomeScreenState.ChickenSuccess(
                                    BirdGuardApp.Companion.TOWNPL_FB_LI.toString()
                                )
                        } else if (System.currentTimeMillis() / 1000 > chickenSharedPreference.chickenExpired) {
                            Log.d(BirdGuardApp.Companion.TOWNPLANNER_MAIN_TAG, "Current time more then expired, repeat request")
                            BirdGuardApp.Companion.chickenConversionFlow.collect {
                                when(it) {
                                    BirdGuardAppsFlyerState.BirdGuardDefault -> {}
                                    BirdGuardAppsFlyerState.BirdGuardError -> {
                                        _chickenHomeScreenState.value =
                                            ChickenHomeScreenState.ChickenSuccess(
                                                chickenSharedPreference.chickenSavedUrl
                                            )
                                        chickenGetApps = true
                                    }
                                    is BirdGuardAppsFlyerState.BirdGuardSuccess -> {
                                        if (!chickenGetApps) {
                                            chickenGetData(it.chickenData)
                                            chickenGetApps = true
                                        }
                                    }
                                }
                            }
                        } else {
                            Log.d(BirdGuardApp.Companion.TOWNPLANNER_MAIN_TAG, "Current time less then expired, use saved url")
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
        val chickenData = birdGuardGetAllUseCase.invoke(conversation)
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