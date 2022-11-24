package com.lukasanda.navikatorprocessor.generator

import com.google.devtools.ksp.processing.CodeGenerator
import com.google.devtools.ksp.symbol.KSClassDeclaration
import com.google.devtools.ksp.symbol.KSFunctionDeclaration
import com.squareup.kotlinpoet.*
import com.squareup.kotlinpoet.ParameterizedTypeName.Companion.parameterizedBy
import com.squareup.kotlinpoet.ksp.KotlinPoetKspPreview
import com.squareup.kotlinpoet.ksp.toClassName
import com.squareup.kotlinpoet.ksp.writeTo

@OptIn(KotlinPoetKspPreview::class)
class NavigationRouteGenerator(private val codeGenerator: CodeGenerator) {
    fun generate(viewModel: KSClassDeclaration, content: KSFunctionDeclaration, route: String) {

        val packageName = viewModel.packageName.asString()
        val routeName = content.simpleName.asString() + "Route"

        val navArgs = viewModel.primaryConstructor
            ?.parameters
            ?.filter { it.annotations.find { it.shortName.asString() == "NavigationArg" } != null }
            ?.map { it.name?.asString() to it.type.resolve().toClassName() }
            ?: emptyList()

        val fileSpec = FileSpec.builder(packageName, routeName).apply {
            addImport("androidx.compose.runtime", "Composable")
            addImport("com.lukasanda.navikator", "NavRoute")
            navArgs.forEach {
                addImport(it.second.packageName, it.second.simpleName)
            }
            addImport("org.koin.androidx.compose", "viewModel")
            addImport("org.koin.core.parameter", "parametersOf")
            addImport(viewModel.packageName.asString(), viewModel.simpleName.asString())
            addImport(content.packageName.asString(), content.simpleName.asString())
            addType(
                TypeSpec.objectBuilder(routeName)
                    .addSuperinterface(
                        ClassName("com.lukasanda.navikator", "NavRoute")
                            .parameterizedBy(
                                ClassName(
                                    viewModel.packageName.asString(),
                                    viewModel.simpleName.asString()
                                )
                            )
                    )
                    .addProperty(
                        PropertySpec.builder("route", String::class)
                            .addModifiers(KModifier.OVERRIDE)
                            .initializer("%S", route)
                            .build()
                    )
                    .addFunction(
                        FunSpec.builder("navigateSafe")
                            .returns(String::class)
                            .apply {
                                navArgs.forEach {
                                    addParameter(it.first.toString(), it.second)
                                }
                            }
                            .addStatement("return navigate(${navArgs.joinToString(", ") { "%N" }})", *navArgs.map { it.first.toString() }.toTypedArray())
                            .build()
                    )
                    .addFunction(
                        FunSpec.builder("getActualArguments")
                            .addModifiers(KModifier.OVERRIDE)
                            .returns(
                                ClassName("kotlin.collections", "List")
                                    .parameterizedBy(
                                        ClassName("kotlin", "Pair")
                                            .parameterizedBy(
                                                ClassName("kotlin", "String"),
                                                ClassName("kotlin.reflect", "KClassifier")
                                            )
                                    )
                            )
                            .addStatement(
                                "return listOf(${
                                    navArgs.joinToString(", ") {
                                        "%S to %N::class"
                                    }
                                })", *(navArgs.map {
                                    listOf(
                                        it.first.toString(),
                                        it.second.simpleName
                                    )
                                }.flatten().toTypedArray())
                            )
                            .build()
                    )
                    .addFunction(
                        FunSpec.builder("viewModel")
                            .addAnnotation(
                                ClassName(
                                    packageName = "androidx.compose.runtime",
                                    "Composable"
                                )
                            )
                            .addModifiers(KModifier.OVERRIDE)
                            .addParameter(
                                ParameterSpec.builder(
                                    "args",
                                    Any::class.asTypeName().copy(nullable = true)
                                ).addModifiers(KModifier.VARARG)
                                    .build()
                            )
                            .returns(
                                ClassName(
                                    packageName = "kotlin",
                                    "Lazy"
                                ).parameterizedBy(
                                    ClassName(
                                        viewModel.packageName.asString(),
                                        viewModel.simpleName.asString()
                                    )
                                )
                            ).addStatement(
                                "return viewModel<%N> { parametersOf(*args) }",
                                viewModel.simpleName.asString()
                            )
                            .build()
                    )
                    .addFunction(
                        FunSpec.builder("Content")
                            .addAnnotation(
                                ClassName(
                                    packageName = "androidx.compose.runtime",
                                    "Composable"
                                )
                            )
                            .addModifiers(KModifier.OVERRIDE)
                            .addParameter("viewModel", viewModel.toClassName())
                            .addStatement("return %N(viewModel)", content.simpleName.asString())
                            .build()
                    ).build()
            )
        }.build()

        fileSpec.writeTo(codeGenerator, aggregating = false)
    }
}