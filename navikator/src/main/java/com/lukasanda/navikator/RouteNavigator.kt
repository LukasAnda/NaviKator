package com.lukasanda.navikator

import androidx.annotation.VisibleForTesting
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

interface RouteNavigator {
    fun onNavigated(state: NavigationState)
    fun navigateUp()
    fun navigateToRoute(route: String)
    fun openApp(packageName: String)
    fun closeApp()

    val navigationState: StateFlow<NavigationState>
}

class MyRouteNavigator : RouteNavigator {

    /**
     * Note that I'm using a single state here, not a list of states. As a result, if you quickly
     * update the state multiple times, the view will only receive and handle the latest state,
     * which is fine for my use case.
     */
    override val navigationState: MutableStateFlow<NavigationState> =
        MutableStateFlow(NavigationState.Idle)

    override fun onNavigated(state: NavigationState) {
        // clear navigation state, if state is the current state:
        navigationState.compareAndSet(state, NavigationState.Idle)
    }


    override fun navigateUp() = navigate(NavigationState.NavigateUp)

    override fun navigateToRoute(route: String) = navigate(NavigationState.NavigateToRoute(route))

    override fun openApp(packageName: String) = navigate(NavigationState.NavigateToApp(packageName))

    override fun closeApp() = navigate(NavigationState.CloseApp)

    @VisibleForTesting
    fun navigate(state: NavigationState) {
        navigationState.value = state
    }
}