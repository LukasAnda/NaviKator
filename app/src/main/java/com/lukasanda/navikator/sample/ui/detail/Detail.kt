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
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.navigation.NavArgs
import com.lukasanda.navikator.sample.model.DetailData
import com.lukasanda.navikator.sample.ui.navArgs
import com.ramcosta.composedestinations.annotation.Destination
import kotlinx.coroutines.flow.MutableStateFlow
import org.koin.androidx.compose.getViewModel
import org.koin.core.parameter.parametersOf

class DetailViewModel(
    private val savedStateHandle: SavedStateHandle,
) : ViewModel(){
    val state = MutableStateFlow(savedStateHandle.navArgs() as DetailData)
}

@Composable
@Destination(navArgsDelegate = DetailData::class)
fun Detail(
    viewModel: DetailViewModel = getViewModel<DetailViewModel>()
) {
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