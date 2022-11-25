package com.lukasanda.navikatorprocessor.visitor

import com.google.devtools.ksp.processing.CodeGenerator
import com.google.devtools.ksp.symbol.KSAnnotation
import com.google.devtools.ksp.symbol.KSClassDeclaration
import com.google.devtools.ksp.symbol.KSVisitorVoid
import com.squareup.kotlinpoet.ClassName
import com.squareup.kotlinpoet.FileSpec
import com.squareup.kotlinpoet.FunSpec
import com.squareup.kotlinpoet.KModifier
import com.squareup.kotlinpoet.ParameterSpec
import com.squareup.kotlinpoet.ParameterizedTypeName.Companion.parameterizedBy
import com.squareup.kotlinpoet.PropertySpec
import com.squareup.kotlinpoet.TypeSpec
import com.squareup.kotlinpoet.asTypeName
import com.squareup.kotlinpoet.ksp.KotlinPoetKspPreview
import com.squareup.kotlinpoet.ksp.toClassName
import com.squareup.kotlinpoet.ksp.writeTo
import java.util.*

class NavigationRouteVisitor(private val codeGenerator: CodeGenerator) : KSVisitorVoid() {
    @OptIn(KotlinPoetKspPreview::class)
    override fun visitClassDeclaration(viewModel: KSClassDeclaration, data: Unit) {
        super.visitClassDeclaration(viewModel, data)

        val annotation: KSAnnotation = viewModel.annotations.first {
            it.shortName.asString() == "NavigationRoute"
        }

        // Getting the 'name' argument object from the @Function.
        val route = annotation.arguments
            .first { arg -> arg.name?.asString() == "route" }.value.toString()

        val routeName = route.replaceFirstChar { if (it.isLowerCase()) it.titlecase(Locale.getDefault()) else it.toString() }.plus("Route")

        val packageName = viewModel.packageName.asString()
//        val routeName = content.simpleName.asString() + "Route"
//
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
            addImport(viewModel.packageName.asString(), viewModel.simpleName.asString())
            addType(
                TypeSpec.interfaceBuilder(routeName)
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
                            .getter(
                                FunSpec.getterBuilder()
                                    .addStatement("return %S", route)
                                    .build()
                            )
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
                            .addStatement(
                                "return navigate(${navArgs.joinToString(", ") { "%N" }})",
                                *navArgs.map { it.first.toString() }.toTypedArray()
                            )
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
                        FunSpec.builder("provideViewModel")
                            .apply {
                                navArgs.forEach {
                                    addParameter(it.first.toString(), it.second)
                                }
                            }
                            .addAnnotation(
                                ClassName(
                                    packageName = "androidx.compose.runtime",
                                    "Composable"
                                )
                            )
                            .addModifiers(KModifier.ABSTRACT)
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
                            )
                            .build()
                    )
                    .addFunction(
                        FunSpec.builder("provideViewModelInternal")
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
                                "return provideViewModel(${
                                    navArgs.mapIndexed { index, pair ->
                                        "${pair.first} = args[$index] as ${pair.second.simpleName}"
                                    }.joinToString(", ")
                                })"
                            )
                            .build()
                    )
//                    .addFunction(
//                        FunSpec.builder("Content")
//                            .addAnnotation(
//                                ClassName(
//                                    packageName = "androidx.compose.runtime",
//                                    "Composable"
//                                )
//                            )
//                            .addModifiers(KModifier.OVERRIDE)
//                            .addParameter("viewModel", viewModel.toClassName())
//                            .addStatement("return %N(viewModel)", content.simpleName.asString())
//                            .build()
//                    )
                    .build()
            )
        }.build()

        fileSpec.writeTo(codeGenerator, aggregating = true)
    }
}