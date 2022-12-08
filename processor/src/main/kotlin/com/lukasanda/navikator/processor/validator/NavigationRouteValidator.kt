package com.lukasanda.navikator.processor.validator

import com.google.devtools.ksp.processing.KSPLogger
import com.google.devtools.ksp.symbol.KSAnnotated
import com.google.devtools.ksp.symbol.KSAnnotation
import com.google.devtools.ksp.symbol.KSClassDeclaration
import com.google.devtools.ksp.symbol.KSDeclaration
import com.google.devtools.ksp.validate
import com.lukasanda.navikator.annotation.NavigationRoute

class NavigationRouteValidator(private val logger: KSPLogger) {

    fun isValid(symbol: KSAnnotated): Boolean {
        return symbol is KSClassDeclaration && symbol.isViewModel() && symbol.isRouteNavigator() && symbol.validate()
    }

}

private fun KSClassDeclaration.isViewModel(): Boolean {
    val androidViewModel = "androidx.lifecycle.ViewModel"
    return isSubclassOf(androidViewModel)
}

private fun KSClassDeclaration.isRouteNavigator(): Boolean {
    val navigator = "com.lukasanda.navikator.RouteNavigator"
    return isSubclassOf(navigator)
}

private fun KSClassDeclaration.isSubclassOf(
    superClassName: String, 
): Boolean {
    val superClasses = superTypes.toMutableList() 
    while (superClasses.isNotEmpty()) { 
        val current = superClasses.first()
        val declaration = current.resolve().declaration 
        when {
            declaration is KSClassDeclaration
                    && declaration.qualifiedName?.asString() == superClassName -> { 
                return true
            }
            declaration is KSClassDeclaration -> {
                superClasses.removeAt(0) 
                superClasses.addAll(0, declaration.superTypes.toList())
            }
            else -> {
                superClasses.removeAt(0) 
            }
        }
    }
    return false 
}