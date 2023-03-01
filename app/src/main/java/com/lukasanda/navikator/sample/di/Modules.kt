package com.lukasanda.navikator.sample.di

import com.lukasanda.navikator.sample.ui.detail.DetailViewModel
import com.lukasanda.navikator.sample.ui.home.HomeViewModel
import com.ramcosta.composedestinations.navigation.DestinationsNavigator
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

val navigatorModule = module {
//    single<RouteNavigator> { MyRouteNavigator() }
}

val viewModelModule = module {
    viewModel { (navigator: DestinationsNavigator) -> HomeViewModel(navigator) }
    viewModel { DetailViewModel(get()) }
}