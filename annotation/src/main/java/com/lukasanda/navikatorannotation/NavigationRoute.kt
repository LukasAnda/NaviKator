package com.lukasanda.navikatorannotation

@Target(AnnotationTarget.CLASS)
@Retention(AnnotationRetention.SOURCE)
annotation class NavigationRoute(val route: String, val schema: String)