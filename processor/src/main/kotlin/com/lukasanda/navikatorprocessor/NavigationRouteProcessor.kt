package com.lukasanda.navikatorprocessor

import com.google.devtools.ksp.processing.CodeGenerator
import com.google.devtools.ksp.processing.KSPLogger
import com.google.devtools.ksp.processing.Resolver
import com.google.devtools.ksp.processing.SymbolProcessor
import com.google.devtools.ksp.symbol.KSAnnotated
import com.google.devtools.ksp.validate
import com.lukasanda.navikatorannotation.NavigationRoute
import com.lukasanda.navikatorprocessor.validator.SymbolValidator


class NavigationRouteProcessor(
    private val logger: KSPLogger,
    codeGenerator: CodeGenerator
) : SymbolProcessor {

    private val validator = SymbolValidator(logger)

    override fun process(resolver: Resolver): List<KSAnnotated> {
        var unresolvedSymbols: List<KSAnnotated> = emptyList()
        val annotationName = NavigationRoute::class.qualifiedName

        if (annotationName != null) {
            val resolved = resolver
                .getSymbolsWithAnnotation(annotationName)
                .toList()     // 1
            val validatedSymbols = resolved.filter { it.validate() }.toList()     // 2
            validatedSymbols
                .filter {
                    validator.isValid(it)
                }
                .forEach {
                    //TODO: visit and process this symbol
                }     // 3
            unresolvedSymbols = resolved - validatedSymbols
        }
        return unresolvedSymbols
    }

}
