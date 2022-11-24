package com.lukasanda.navikatorannotation

import kotlin.annotation.AnnotationRetention.SOURCE
import kotlin.reflect.KFunction

@Target(AnnotationTarget.CLASS, AnnotationTarget.FUNCTION)
@Retention(SOURCE)
annotation class NavigationRoute(val route: String)