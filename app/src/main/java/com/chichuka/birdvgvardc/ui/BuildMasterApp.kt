package com.chichuka.birdvgvardc.ui

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.compose.rememberNavController
import com.chichuka.birdvgvardc.navigation.NavigationGraph

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun TownPlannerApp() {
    val navController = rememberNavController()
    MaterialTheme() {
        Column(modifier = Modifier.fillMaxSize()) {
            NavigationGraph(

            )

        }
    }
}