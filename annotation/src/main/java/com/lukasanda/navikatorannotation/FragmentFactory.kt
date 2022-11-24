package com.lukasanda.navikatorannotation

import kotlin.annotation.AnnotationRetention.SOURCE
import kotlin.reflect.KClass

@Target(AnnotationTarget.CLASS)
@Retention(SOURCE)
annotation class NavigationRoute