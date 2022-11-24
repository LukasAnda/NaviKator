package com.lukasanda.navikatorprocessor.validator

import com.google.devtools.ksp.processing.KSPLogger
import com.google.devtools.ksp.symbol.KSAnnotated
import com.google.devtools.ksp.symbol.KSAnnotation
import com.google.devtools.ksp.symbol.KSClassDeclaration
import com.google.devtools.ksp.symbol.KSFunctionDeclaration
import com.google.devtools.ksp.symbol.KSType
import com.google.devtools.ksp.validate
import com.lukasanda.navikatorannotation.NavigationRoute

class SymbolValidator(private val logger: KSPLogger) {

    fun isValid(symbol: KSAnnotated): Boolean {
        return symbol is KSClassDeclaration
                && symbol.validate()
                && symbol.isRoute()
    }

}

private fun KSClassDeclaration.isRoute(): Boolean {
    val androidFragment = "com.lukasanda.navikator.NavRoute"
    return isSubclassOf(androidFragment)
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

private fun KSClassDeclaration.getRouteAnnotation(): KSAnnotation {
    val annotationKClass = NavigationRoute::class
    return annotations.filter {
        it.annotationType
            .resolve()
            .declaration.qualifiedName?.asString() == annotationKClass.qualifiedName
    }.first()
}