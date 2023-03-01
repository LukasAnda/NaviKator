package com.lukasanda.navikator.sample.ui.home

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.lifecycle.ViewModel
import com.lukasanda.navikator.sample.model.DetailData
import com.lukasanda.navikator.sample.ui.NavGraphs
import com.lukasanda.navikator.sample.ui.destinations.DetailDestination
import com.ramcosta.composedestinations.annotation.Destination
import com.ramcosta.composedestinations.annotation.RootNavGraph
import com.ramcosta.composedestinations.navigation.DestinationsNavigator
import com.ramcosta.composedestinations.scope.DestinationScope
import org.koin.androidx.compose.getViewModel
import org.koin.core.parameter.parametersOf
import kotlin.random.Random

class HomeViewModel(private val navigator: DestinationsNavigator) : ViewModel(), HomeInteractor {

    override fun showDetail(randomId: Int) {
        navigator.navigate(DetailDestination(DetailData(randomId)))
    }

}

interface HomeInteractor {
    fun showDetail(randomId: Int)
}

@Destination()
@RootNavGraph(start = true)
@Composable
fun DestinationScope<*>.Home(
    interactor: HomeInteractor = getViewModel<HomeViewModel>() { parametersOf(destinationsNavigator) },
) {
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
            Text(text = "Click to go to detaill")
        }
    }
}