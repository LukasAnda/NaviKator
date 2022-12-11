package com.lukasanda.navikator

import androidx.annotation.VisibleForTesting
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

interface RouteNavigator {
    fun onNavigated(state: NavigationState)
    fun navigateUp()
    fun navigateToRoute(route: String, clearBackstack: Boolean = false)
    fun openApp(packageName: String)
    fun closeApp()

    val navigationState: StateFlow<NavigationState>
}

class MyRouteNavigator : RouteNavigator {

    override val navigationState: MutableStateFlow<NavigationState> =
        MutableStateFlow(NavigationState.Idle)

    override fun onNavigated(state: NavigationState) {
        navigationState.compareAndSet(state, NavigationState.Idle)
    }


    override fun navigateUp() = navigate(NavigationState.NavigateUp)

    override fun navigateToRoute(route: String, clearBackstack: Boolean) =
        navigate(NavigationState.NavigateToRoute(route, clearBackstack))

    override fun openApp(packageName: String) = navigate(NavigationState.NavigateToApp(packageName))

    override fun closeApp() = navigate(NavigationState.CloseApp)

    @VisibleForTesting
    fun navigate(state: NavigationState) {
        navigationState.value = state
    }
}