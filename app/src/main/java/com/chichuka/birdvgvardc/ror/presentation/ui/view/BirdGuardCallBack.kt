package com.chichuka.birdvgvardc.ror.presentation.ui.view


import android.webkit.PermissionRequest

interface BirdGuardCallBack {
    fun chickenHandleCreateWebWindowRequest(proBubbleBoPlingVi: BirdGuardVi)

    fun chickenOnPermissionRequest(todoSphereRequest: PermissionRequest?)

    fun chickenOnFirstPageFinished()
}