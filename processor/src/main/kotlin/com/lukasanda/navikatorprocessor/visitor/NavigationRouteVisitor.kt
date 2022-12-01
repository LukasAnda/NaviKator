package com.lukasanda.navikatorprocessor.visitor

import com.google.devtools.ksp.processing.CodeGenerator
import com.google.devtools.ksp.symbol.KSAnnotation
import com.google.devtools.ksp.symbol.KSClassDeclaration
import com.google.devtools.ksp.symbol.KSVisitorVoid
import com.squareup.kotlinpoet.*
import com.squareup.kotlinpoet.ParameterizedTypeName.Companion.parameterizedBy
import com.squareup.kotlinpoet.ksp.KotlinPoetKspPreview
import com.squareup.kotlinpoet.ksp.toClassName
import com.squareup.kotlinpoet.ksp.toTypeName
import com.squareup.kotlinpoet.ksp.writeTo
import java.util.*

class NavigationRouteVisitor(private val codeGenerator: CodeGenerator) : KSVisitorVoid() {
    @OptIn(KotlinPoetKspPreview::class)
    override fun visitClassDeclaration(classDeclaration: KSClassDeclaration, data: Unit) {
        super.visitClassDeclaration(classDeclaration, data)

        val annotation: KSAnnotation = classDeclaration.annotations.first {
            it.shortName.asString() == "NavigationRoute"
        }

        // Getting the 'name' argument object from the @Function.
        val route = annotation.arguments
            .first { arg -> arg.name?.asString() == "route" }.value.toString()

        val schema = annotation.arguments
            .first { arg -> arg.name?.asString() == "schema" }.value.toString()

        val routeName =
            route.replaceFirstChar { if (it.isLowerCase()) it.titlecase(Locale.getDefault()) else it.toString() }
                .plus("Route")

        val packageName = classDeclaration.packageName.asString()

        val navArgs = classDeclaration.primaryConstructor
            ?.parameters
            ?.filter { it.annotations.find { it.shortName.asString() == "NavigationArg" } != null }
            ?.map { it.name?.asString() to it.type }
            ?: emptyList()

        val fileSpec = FileSpec.builder(packageName, routeName).apply {
            addImport("androidx.compose.runtime", "Composable")
            addImport("com.lukasanda.navikator", "NavRoute")
            navArgs.forEach {
                addImport(
                    it.second.resolve().toClassName().packageName,
                    it.second.resolve().toClassName().simpleName
                )
            }
            addImport(
                classDeclaration.packageName.asString(),
                classDeclaration.simpleName.asString()
            )
            addType(
                TypeSpec.interfaceBuilder(routeName)
                    .addSuperinterface(
                        ClassName("com.lukasanda.navikator", "NavRoute")
                            .parameterizedBy(
                                ClassName(
                                    classDeclaration.packageName.asString(),
                                    classDeclaration.simpleName.asString()
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
                    .addProperty(
                        PropertySpec.builder("appUrl", String::class)
                            .addModifiers(KModifier.OVERRIDE)
                            .getter(
                                FunSpec.getterBuilder()
                                    .addStatement("return %S", schema)
                                    .build()
                            )
                            .build()
                    )
                    .addFunction(
                        FunSpec.builder("navigateSafe")
                            .returns(String::class)
                            .apply {
                                navArgs.forEach {
                                    addParameter(it.first.toString(), it.second.toTypeName())
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
                                        it.second.resolve().toClassName().simpleName
                                    )
                                }.flatten().toTypedArray())
                            )
                            .build()
                    )
                    .addFunction(
                        FunSpec.builder("provideViewModel")
                            .apply {
                                navArgs.forEach {
                                    addParameter(it.first.toString(), it.second.toTypeName())
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
                                        classDeclaration.packageName.asString(),
                                        classDeclaration.simpleName.asString()
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
                            .addAnnotation(
                                AnnotationSpec.builder(
                                    ClassName(
                                        packageName = "kotlin",
                                        "Suppress"
                                    )
                                )
                                    .addMember("%S", "UNCHECKED_CAST")
                                    .build()
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
                                        classDeclaration.packageName.asString(),
                                        classDeclaration.simpleName.asString()
                                    )
                                )
                            ).addStatement(
                                "return provideViewModel(${
                                    navArgs.mapIndexed { index, pair ->
                                        "${pair.first} = args[$index] as %T"
                                    }.joinToString(", ")
                                })", *navArgs.map { it.second.toTypeName() }.toTypedArray()
                            )
                            .build()
                    )
                    .build()
            )
        }.build()

        fileSpec.writeTo(codeGenerator, aggregating = true)
    }
}