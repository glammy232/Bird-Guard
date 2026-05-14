package com.chichuka.birdvgvardc.ror.domain.usecases

import android.util.Log
import com.chichuka.birdvgvardc.ror.data.repo.TownPlannerRepository
import com.chichuka.birdvgvardc.ror.data.utils.BirdGuardPushToken
import com.chichuka.birdvgvardc.ror.data.utils.BirdGuardSystemService
import com.chichuka.birdvgvardc.ror.domain.model.BirdGuardEntity
import com.chichuka.birdvgvardc.ror.domain.model.TownPlannerParam
import com.chichuka.birdvgvardc.ror.presentation.app.BirdGuardApp

class BirdGuardGetAllUseCase(
    private val townPlannerRepository: TownPlannerRepository,
    private val volcanoSystemService: BirdGuardSystemService,
    private val aeroMarinePushToken: BirdGuardPushToken,
) {
    suspend operator fun invoke(conversion: MutableMap<String, Any>?) : BirdGuardEntity?{
        val params = TownPlannerParam(
            chickenLocale = volcanoSystemService.volcanoGetLocale(),
            chickenPushToken = aeroMarinePushToken.chickenGetToken(),
            chickenAfId = volcanoSystemService.volcanoGetAppsflyerId()
        )
        Log.d(BirdGuardApp.Companion.TOWNPLANNER_MAIN_TAG, "Params for request: $params")
        return townPlannerRepository.chickenGetClient(params, conversion)
    }



}