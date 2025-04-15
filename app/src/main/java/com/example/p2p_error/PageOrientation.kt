package com.example.p2p_error

import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.p2p_error.pages.RecieveScreen
import com.example.p2p_error.pages.SendScreen

@Composable
fun PageOrientation(){
    var navController = rememberNavController()

    NavHost(navController = navController, startDestination = "a" ){
        composable("a") { SendScreen(navController = navController) }
        composable("b") { RecieveScreen(navController = navController) }
    }

}