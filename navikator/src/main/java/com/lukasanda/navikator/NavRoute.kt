package com.lukasanda.navikator

import android.content.Intent
import android.net.Uri
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.navigation.NamedNavArgument
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.NavType.Companion.BoolType
import androidx.navigation.NavType.Companion.FloatType
import androidx.navigation.NavType.Companion.IntType
import androidx.navigation.NavType.Companion.LongType
import androidx.navigation.NavType.Companion.StringType
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import androidx.navigation.navDeepLink
import kotlinx.serialization.json.Json
import kotlinx.serialization.serializer
import org.koin.core.parameter.ParametersDefinition
import org.koin.core.parameter.parametersOf
import java.net.URLDecoder
import java.net.URLEncoder
import kotlin.reflect.KClassifier
import kotlin.reflect.full.createType

/**
 * A route the app can navigate to.
 */

private const val APP_URL = "example"

interface NavRoute<T : RouteNavigator> {

    val route: String

    /**
     * Returns the screen's content.
     */
    @Composable
    fun Content(viewModel: T)

    /**
     * Returns the screen's ViewModel. Needs to be overridden so that Hilt can generate code for the factory for the ViewModel class.
     */
    @Composable
    fun viewModel(parameters: ParametersDefinition?): Lazy<T>

    /**
     * Override when this page uses arguments.
     *
     * We do it here and not in the [NavigationComponent to keep it centralized]
     */
    fun getActualArguments(): List<Pair<String, KClassifier>> = listOf()

    private fun getNavArgs(): List<NamedNavArgument> {
        return getActualArguments().map {
            val (name, classifier) = it
            val type = when (classifier) {
                String::class -> StringType
                Int::class -> IntType
                Long::class -> LongType
                Float::class -> FloatType
                Boolean::class -> BoolType
                else -> StringType
            }
            navArgument(name) {
                this.type = type
            }
        }
    }

    fun createRoute() = buildString {
        append(route)
        getNavArgs().forEach {
            append("/")
            append("{${it.name}}")
        }
    }

    fun createDeepLinks() = listOf(
        navDeepLink {
            uriPattern = buildString {
                append("$APP_URL://$route")
                getNavArgs().forEachIndexed { index, argument ->
                    if (index == 0) {
                        append("?")
                    } else {
                        append("&")
                    }
                    append("${argument.name}={${argument.name}}")
                }
            }
        }
    )

    fun navigate(vararg args: Any? = emptyArray()): String {
        require(args.size == getNavArgs().size) {
            "Supplied arguments: ${args.joinToString(" ")} do not match required arguments: ${
                getNavArgs().joinToString(
                    " "
                )
            }"
        }

        return buildString {
            append(route)
            args.zip(getActualArguments()).forEach { (arg, actualArg) ->
                append("/")
                val value = when (arg) {
                    is String -> URLEncoder.encode(arg, "utf-8")
                    is Int -> arg.toString()
                    is Long -> arg.toString()
                    is Float -> arg.toString()
                    is Double -> arg.toString()
                    is Boolean -> arg.toString()
                    null -> arg.toString()
                    else -> URLEncoder.encode(
                        Json.encodeToString(serializer(actualArg.second.createType()), arg), "utf-8"
                    )
                }
                append(value)
            }
        }
    }


    /**
     * Generates the composable for this route.
     */
    fun composable(
        builder: NavGraphBuilder,
        navHostController: NavHostController
    ) {
        builder.composable(createRoute(), getNavArgs(), createDeepLinks()) { entry ->
            val bundle = entry.arguments

            val args = getActualArguments().map { (name, classifier) ->
                when (classifier) {
                    String::class -> bundle?.getString(name)?.let { URLDecoder.decode(it, "utf-8") }
                    Int::class -> bundle?.getInt(name)
                    Long::class -> bundle?.getLong(name)
                    Float::class -> bundle?.getFloat(name)
                    Boolean::class -> bundle?.getBoolean(name)
                    else -> bundle?.getString(name)
                        ?.let { URLDecoder.decode(it, "utf-8") }
                        ?.let {
                            Json.decodeFromString(serializer(classifier.createType()), it)
                        }
                }
            }.toTypedArray().let { parametersOf(*it) }

            val viewModel by viewModel(args.let { { it } })
            val viewStateAsState by viewModel.navigationState.collectAsState()

            LaunchedEffect(viewStateAsState) {
                updateNavigationState(navHostController, viewStateAsState, viewModel::onNavigated)
            }

            Content(viewModel = viewModel)
        }
    }

    /**
     * Navigates to viewState.
     */
    private fun updateNavigationState(
        navHostController: NavHostController,
        navigationState: NavigationState,
        onNavigated: (navState: NavigationState) -> Unit,
    ) {
        when (navigationState) {
            is NavigationState.NavigateToRoute -> {
                navHostController.navigate(navigationState.route) {
                    if (navigationState.clearBackStack) {
                        popUpTo(0)
                    }
                    onNavigated(navigationState)
                }
            }
            is NavigationState.NavigateUp -> {
                navHostController.navigateUp()
                onNavigated(navigationState)
            }
            is NavigationState.Idle -> {
            }
            is NavigationState.NavigateToApp -> {
                with(navHostController.context) {
                    var intent =
                        packageManager.getLaunchIntentForPackage(navigationState.packageName)

                    if (intent == null) {
                        intent = try {
                            Intent(
                                Intent.ACTION_VIEW,
                                Uri.parse("market://details?id=${navigationState.packageName}")
                            )
                        } catch (e: Exception) {
                            Intent(
                                Intent.ACTION_VIEW,
                                Uri.parse("https://play.google.com/store/apps/details?id=${navigationState.packageName}")
                            )
                        }
                        startActivity(intent)
                    } else {
                        startActivity(intent)
                    }
                }
                onNavigated(navigationState)
            }
            NavigationState.CloseApp -> {
                System.exit(0)
            }
        }
    }
}