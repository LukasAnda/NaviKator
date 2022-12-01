package com.lukasanda.navikator.processor

import com.google.devtools.ksp.processing.CodeGenerator
import com.google.devtools.ksp.processing.KSPLogger
import com.google.devtools.ksp.processing.Resolver
import com.google.devtools.ksp.processing.SymbolProcessor
import com.google.devtools.ksp.symbol.KSAnnotated
import com.google.devtools.ksp.validate
import com.lukasanda.navikator.annotation.NavigationRoute
import com.lukasanda.navikator.processor.validator.NavigationRouteValidator
import com.lukasanda.navikator.processor.visitor.NavigationRouteVisitor


class NavigationRouteProcessor(
    private val logger: KSPLogger,
    codeGenerator: CodeGenerator
) : SymbolProcessor {

    private val navigationRouteValidator = NavigationRouteValidator(logger)
    private val navigationRouteVisitor = NavigationRouteVisitor(codeGenerator)

    override fun process(resolver: Resolver): List<KSAnnotated> {
        val unresolvedSymbols: List<KSAnnotated>

        val resolvedRoutes = resolver
            .getSymbolsWithAnnotation(NavigationRoute::class.qualifiedName.toString(), true)
            .toList()

        val validatedRoutes = resolvedRoutes
            .filter { it.validate() && navigationRouteValidator.isValid(it) }
            .toList()

        validatedRoutes.forEach { it.accept(navigationRouteVisitor, Unit) }

        unresolvedSymbols = resolvedRoutes - validatedRoutes.toSet()

        return unresolvedSymbols
    }

}
