package com.lukasanda.navikatorSample.di

import com.lukasanda.navikator.MyRouteNavigator
import com.lukasanda.navikator.RouteNavigator
import com.lukasanda.navikatorSample.data.DetailData
import com.lukasanda.navikatorSample.ui.detail.DetailViewModel
import com.lukasanda.navikatorSample.ui.home.HomeViewModel
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

val navigatorModule = module {
    single<RouteNavigator> { MyRouteNavigator() }
}

val viewModelModule = module {
    viewModel { HomeViewModel(get()) }
    viewModel { (data: DetailData) -> DetailViewModel(data, get()) }
}