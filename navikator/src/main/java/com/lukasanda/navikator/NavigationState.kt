package com.lukasanda.navikator

sealed class NavigationState {

    object Idle : NavigationState()
    object CloseApp : NavigationState()

    data class NavigateToRoute(val route: String, val clearBackStack: Boolean = false) : NavigationState()
    data class NavigateToApp(val packageName: String) : NavigationState()

    object NavigateUp : NavigationState()
}