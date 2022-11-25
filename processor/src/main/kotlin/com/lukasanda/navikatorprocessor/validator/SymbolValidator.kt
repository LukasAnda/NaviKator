package com.lukasanda.navikatorprocessor.validator

import com.google.devtools.ksp.processing.KSPLogger
import com.google.devtools.ksp.symbol.*
import com.google.devtools.ksp.validate
import com.lukasanda.navikatorannotation.NavigationRoute

class SymbolValidator(private val logger: KSPLogger) {

    fun isValid(symbol: KSAnnotated): Boolean {
        logger.warn("Symbol is Class: ${symbol is KSClassDeclaration}")
        logger.warn("Symbol is viewModel: ${(symbol as? KSClassDeclaration)?.isViewModel()}")
        logger.warn("Symbol is function: ${symbol is KSFunctionDeclaration}")
        return (symbol is KSClassDeclaration && symbol.isViewModel() || symbol is KSFunctionDeclaration)
//                && symbol.validate()
    }

}

private fun KSClassDeclaration.isViewModel(): Boolean {
    val androidViewModel = "androidx.lifecycle.ViewModel"
    return isSubclassOf(androidViewModel)
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

internal fun KSDeclaration.getRouteAnnotation(): KSAnnotation {
    val annotationKClass = NavigationRoute::class
    return annotations.filter {
        it.annotationType
            .resolve()
            .declaration.qualifiedName?.asString() == annotationKClass.qualifiedName
    }.first()
}