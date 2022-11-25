package com.lukasanda.navikatorprocessor

import com.google.devtools.ksp.processing.SymbolProcessor
import com.google.devtools.ksp.processing.SymbolProcessorEnvironment
import com.google.devtools.ksp.processing.SymbolProcessorProvider

class NavigationRouteProcessorProvider : SymbolProcessorProvider {
    override fun create(environment: SymbolProcessorEnvironment): SymbolProcessor {
        return NavigationRouteProcessor(
            logger = environment.logger,
            codeGenerator = environment.codeGenerator
        )
    }
}