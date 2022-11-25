package com.lukasanda.navikatorannotation

@Target(AnnotationTarget.CLASS, AnnotationTarget.FUNCTION)
@Retention(AnnotationRetention.SOURCE)
annotation class NavigationRoute(val route: String)