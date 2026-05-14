package com.chichuka.birdvgvardc.ror.domain.usecases

import android.util.Log
import com.chichuka.birdvgvardc.ror.data.repo.TownPlannerRepository
import com.chichuka.birdvgvardc.ror.data.utils.TowerPlannerPushToken
import com.chichuka.birdvgvardc.ror.data.utils.TownPlannerSystemService
import com.chichuka.birdvgvardc.ror.domain.model.TowerPlannerEntity
import com.chichuka.birdvgvardc.ror.domain.model.TownPlannerParam
import com.chichuka.birdvgvardc.ror.presentation.app.BuildMasterApp

class TowerPlannerGetAllUseCase(
    private val townPlannerRepository: TownPlannerRepository,
    private val volcanoSystemService: TownPlannerSystemService,
    private val aeroMarinePushToken: TowerPlannerPushToken,
) {
    suspend operator fun invoke(conversion: MutableMap<String, Any>?) : TowerPlannerEntity?{
        val params = TownPlannerParam(
            chickenLocale = volcanoSystemService.volcanoGetLocale(),
            chickenPushToken = aeroMarinePushToken.chickenGetToken(),
            chickenAfId = volcanoSystemService.volcanoGetAppsflyerId()
        )
        Log.d(BuildMasterApp.Companion.TOWNPLANNER_MAIN_TAG, "Params for request: $params")
        return townPlannerRepository.chickenGetClient(params, conversion)
    }



}