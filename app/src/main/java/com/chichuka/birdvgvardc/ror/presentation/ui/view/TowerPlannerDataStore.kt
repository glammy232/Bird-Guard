package com.chichuka.birdvgvardc.ror.presentation.ui.view

import android.annotation.SuppressLint
import android.widget.FrameLayout
import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow

class TowerPlannerDataStore : ViewModel(){
    val proBubbleBoPlingViList: MutableList<TownPlannerVi> = mutableListOf()
    private val _chickenIsFirstFinishPage: MutableStateFlow<Boolean> = MutableStateFlow(true)
    var chickenIsFirstCreate = true
    @SuppressLint("StaticFieldLeak")
    lateinit var chickenContainerView: FrameLayout
    @SuppressLint("StaticFieldLeak")
    lateinit var proBubbleBoPlingView: TownPlannerVi

    fun chickenSetIsFirstFinishPage() {
        _chickenIsFirstFinishPage.value = false
    }
}