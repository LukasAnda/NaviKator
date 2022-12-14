package com.lukasanda.navikator.sample.ui.detail

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
import com.lukasanda.navikator.DetailRoute
import com.lukasanda.navikator.RouteNavigator
import com.lukasanda.navikator.sample.model.DetailData
import com.lukasanda.navikator.annotation.NavigationArg
import com.lukasanda.navikator.annotation.NavigationRoute
import kotlinx.coroutines.flow.MutableStateFlow
import org.koin.androidx.compose.viewModel
import org.koin.core.parameter.parametersOf

object Detail : DetailRoute {
    @Composable
    override fun provideViewModel(detailData: DetailData) = viewModel<DetailViewModel> {
        parametersOf(detailData)

    }

    @Composable override fun Content(viewModel: DetailViewModel) = Detail(viewModel = viewModel)
}

@NavigationRoute("detail", "sample")
class DetailViewModel(
    @NavigationArg private val detailData: DetailData, private val routeNavigator: RouteNavigator
) : ViewModel(), RouteNavigator by routeNavigator {

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