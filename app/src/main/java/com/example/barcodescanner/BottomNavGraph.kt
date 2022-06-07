package com.example.barcodescanner

import androidx.compose.runtime.Composable
import androidx.navigation.NavHost
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.example.barcodescanner.screens.HomeScreen
import com.example.barcodescanner.screens.SearchScreen

@Composable
fun BottomNavGraph(navController: NavHostController) {

    NavHost(
        navController = navController,
        startDestination = BottomBarScreen.Home.route
    ){
        composable(route=BottomBarScreen.Home.route){
            HomeScreen()
        }
        composable(route=BottomBarScreen.Search.route){
            SearchScreen()
        }
        composable(route=BottomBarScreen.Settings.route){
            SearchScreen()
        }
    }
}