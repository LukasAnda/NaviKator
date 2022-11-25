package com.lukasanda.navikatorSample.ui.home

import android.util.Log
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.lifecycle.ViewModel
import com.lukasanda.navikator.RouteNavigator
import com.lukasanda.navikatorSample.model.DetailData
import com.lukasanda.navikatorSample.ui.detail.Detail
import com.lukasanda.navikatorannotation.NavigationRoute
import org.koin.androidx.compose.viewModel
import kotlin.random.Random

object Home : HomeRoute {
    @Composable
    override fun provideViewModel(): Lazy<HomeViewModel> = viewModel()

}

@NavigationRoute("home")
class HomeViewModel(private val routeNavigator: RouteNavigator) : ViewModel(), HomeInteractor,
    RouteNavigator by routeNavigator {

    override fun showDetail(randomId: Int) {
        routeNavigator.navigateToRoute(Detail.navigateSafe(DetailData(randomId)))
    }

}

interface HomeInteractor {
    fun showDetail(randomId: Int)
}

@Composable
@NavigationRoute("home")
fun Home(interactor: HomeInteractor) {
    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Button(
            onClick = {
                Log.d("TAG", "")
                interactor.showDetail(Random.nextInt())
            }
        ) {
            Text(text = "Click to go to detailld")
        }
    }
}