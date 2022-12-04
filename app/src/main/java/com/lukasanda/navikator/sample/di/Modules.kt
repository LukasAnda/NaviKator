package com.lukasanda.navikator.sample.di

import com.lukasanda.navikator.MyRouteNavigator
import com.lukasanda.navikator.RouteNavigator
import com.lukasanda.navikator.sample.model.DetailData
import com.lukasanda.navikator.sample.ui.detail.DetailViewModel
import com.lukasanda.navikator.sample.ui.home.HomeViewModel
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

val navigatorModule = module {
    single<RouteNavigator> { MyRouteNavigator() }
}

val viewModelModule = module {
    viewModel { HomeViewModel(get()) }
    viewModel { (data: DetailData) -> DetailViewModel(data, get()) }
}