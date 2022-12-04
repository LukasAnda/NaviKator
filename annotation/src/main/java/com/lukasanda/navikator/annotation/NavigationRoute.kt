package com.lukasanda.navikator.annotation

@Target(AnnotationTarget.CLASS)
@Retention(AnnotationRetention.SOURCE)
annotation class NavigationRoute(val route: String, val schema: String)