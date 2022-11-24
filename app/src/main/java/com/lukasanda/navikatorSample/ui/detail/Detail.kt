package com.lukasanda.navikatorSample.ui.detail

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.lifecycle.ViewModel
import com.lukasanda.navikator.NavRoute
import com.lukasanda.navikator.RouteNavigator
import com.lukasanda.navikatorSample.data.DetailData
import com.lukasanda.navikatorannotation.NavigationRoute
import kotlinx.coroutines.flow.MutableStateFlow
import org.koin.androidx.compose.viewModel
import org.koin.core.parameter.parametersOf

@NavigationRoute
object DetailRoute : NavRoute<DetailViewModel> {
    override val route: String = "Detail"

    override fun getActualArguments() = listOf(
        "detailArg" to DetailData::class
    )

    @Composable
    override fun viewModel(vararg args: Any?) = viewModel<DetailViewModel> { parametersOf(*args) }

    @Composable
    override fun Content(viewModel: DetailViewModel) = Detail(viewModel)

}

class DetailViewModel(
    private val detailData: DetailData,
    private val routeNavigator: RouteNavigator
) : ViewModel(),
    RouteNavigator by routeNavigator {

    val state = MutableStateFlow(detailData)

}

@Composable
fun Detail(viewModel: DetailViewModel) {
    val uiState by viewModel.state.collectAsState()
    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Text(
            text = "Random userID is: ${uiState.randomId}",
            color = MaterialTheme.colorScheme.onSurface
        )
    }
}