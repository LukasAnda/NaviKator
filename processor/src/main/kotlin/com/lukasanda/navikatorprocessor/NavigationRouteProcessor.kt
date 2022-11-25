package com.lukasanda.navikatorprocessor

import com.google.devtools.ksp.KspExperimental
import com.google.devtools.ksp.processing.CodeGenerator
import com.google.devtools.ksp.processing.KSPLogger
import com.google.devtools.ksp.processing.Resolver
import com.google.devtools.ksp.processing.SymbolProcessor
import com.google.devtools.ksp.symbol.KSAnnotated
import com.google.devtools.ksp.symbol.KSClassDeclaration
import com.google.devtools.ksp.symbol.KSDeclaration
import com.google.devtools.ksp.symbol.KSFunctionDeclaration
import com.google.devtools.ksp.validate
import com.lukasanda.navikatorannotation.NavigationRoute
import com.lukasanda.navikatorprocessor.generator.NavigationRouteGenerator
import com.lukasanda.navikatorprocessor.validator.SymbolValidator
import com.lukasanda.navikatorprocessor.validator.getRouteAnnotation


class NavigationRouteProcessor(
    private val logger: KSPLogger,
    codeGenerator: CodeGenerator
) : SymbolProcessor {

    private val validator = SymbolValidator(logger)
    private val generator = NavigationRouteGenerator(codeGenerator, logger)

    @OptIn(KspExperimental::class)
    override fun process(resolver: Resolver): List<KSAnnotated> {
        var unresolvedSymbols: List<KSAnnotated> = emptyList()
        val annotationName = NavigationRoute::class.qualifiedName

        if (annotationName != null) {
            logger.info("Declared route process started")
            val resolved = resolver
                .getSymbolsWithAnnotation(annotationName, true)
                .toList()
            logger.warn("Resolved size: ${resolved.size}")
            val validatedSymbols = resolved.filter { it.validate() }.toList()
            logger.warn("Validated size: ${validatedSymbols.size}")
            resolved
                .filter {
                    validator.isValid(it)
                }
                .groupBy {
                    val annotation = (it as? KSDeclaration)?.getRouteAnnotation()
                    annotation?.arguments?.first()?.value.toString()
                }.filter {
                    if (it.value.size != 2) {
                        logger.error("Navigation route ${it.key} needs to be declared for both ViewModel and content function, size is ${it.value.size}")
                        return@filter false
                    }

                    if (it.value.find { it is KSClassDeclaration } == null) {
                        logger.error("Could not find any ViewModel required by NavigationRoute(${it.key})")
                        return@filter false
                    }

                    if (it.value.find { it is KSFunctionDeclaration } == null) {
                        logger.error("Could not find any Content function required by NavigationRoute(${it.key})")
                        return@filter false
                    }

                    it.value.size == 2
                }
                .forEach {
                    logger.info("Declared route found: ${it.key}")
                    generator.generate(
                        it.value.find { it is KSClassDeclaration } as KSClassDeclaration,
                        it.value.find { it is KSFunctionDeclaration } as KSFunctionDeclaration,
                        it.key
                    )
                }
            unresolvedSymbols = resolved - validatedSymbols.toSet()
        }
        return unresolvedSymbols
    }

}
