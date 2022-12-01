package com.lukasanda.navikatorSample.ui.dashboard

import androidx.lifecycle.ViewModel
import com.lukasanda.navikator.RouteNavigator
import com.lukasanda.navikatorannotation.NavigationArg
import com.lukasanda.navikatorannotation.NavigationRoute

@NavigationRoute("dashboard", "sample")
class DashboardViewModel(
    @NavigationArg val tabs: List<String>,
    private val routeNavigator: RouteNavigator
) : ViewModel(), RouteNavigator by routeNavigator {

}