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
import com.lukasanda.navikator.RouteNavigator
import com.lukasanda.navikatorSample.model.DetailData
import com.lukasanda.navikatorannotation.NavigationArg
import com.lukasanda.navikatorannotation.NavigationRoute
import kotlinx.coroutines.flow.MutableStateFlow

@NavigationRoute("detail")
class DetailViewModel(
    @NavigationArg private val detailData: DetailData,
    private val routeNavigator: RouteNavigator
) : ViewModel(),
    RouteNavigator by routeNavigator {

    val state = MutableStateFlow(detailData)

}

@Composable
@NavigationRoute("detail")
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