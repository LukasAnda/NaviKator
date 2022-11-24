package com.lukasanda.navikatorSample.ui.home

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.lifecycle.ViewModel
import com.lukasanda.navikator.NavRoute
import com.lukasanda.navikator.RouteNavigator
import com.lukasanda.navikatorSample.data.DetailData
import com.lukasanda.navikatorSample.ui.detail.DetailRoute
import org.koin.androidx.compose.viewModel
import org.koin.core.parameter.ParametersDefinition
import kotlin.random.Random

@NavigationRoute
object HomeRoute : NavRoute<HomeViewModel> {
    override val route: String = "home"

    @Composable
    override fun viewModel(parameters: ParametersDefinition?) =
        viewModel<HomeViewModel>(parameters = parameters)

    @Composable
    override fun Content(viewModel: HomeViewModel) = Home(viewModel)

}

class HomeViewModel(private val routeNavigator: RouteNavigator) : ViewModel(), HomeInteractor,
    RouteNavigator by routeNavigator {

    override fun showDetail(randomId: Int) {
        routeNavigator.navigateToRoute(DetailRoute.navigate(DetailData(randomId)))
    }

}

interface HomeInteractor {
    fun showDetail(randomId: Int)
}

@Composable
fun Home(interactor: HomeInteractor) {
    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Button(
            onClick = {
                interactor.showDetail(Random.nextInt())
            }
        ) {
            Text(text = "Click to go to detail")
        }
    }
}