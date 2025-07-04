package com.myfinances.ui.screens.articles

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Divider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.myfinances.R
import com.myfinances.ui.components.ListItem
import com.myfinances.ui.components.ListItemModel

/**
 * Composable-функция экрана "Статьи".
 *
 * Отображает список категорий расходов и поле для их локального поиска.
 * Состояние экрана полностью управляется [ArticlesViewModel].
 *
 * @param viewModel ViewModel, предоставляемая Hilt.
 */
@Composable
fun ArticlesScreen(
    viewModel: ArticlesViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    Box(modifier = Modifier.fillMaxSize()) {
        when (val state = uiState) {
            is ArticlesUiState.Loading -> {
                CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
            }
            is ArticlesUiState.Success -> {
                ArticlesScreenContent(
                    query = state.query,
                    onQueryChange = viewModel::onSearchQueryChanged,
                    categoryItems = state.categoryItems
                )
            }
            is ArticlesUiState.Error -> {
                Text(
                    text = state.message,
                    modifier = Modifier
                        .align(Alignment.Center)
                        .padding(16.dp),
                    textAlign = TextAlign.Center
                )
            }
            is ArticlesUiState.NoInternet -> {
                Text(
                    text = stringResource(id = R.string.no_internet_connection),
                    modifier = Modifier
                        .align(Alignment.Center)
                        .padding(16.dp),
                    textAlign = TextAlign.Center
                )
            }
        }
    }
}

/**
 * Компонент, отвечающий за отрисовку контента экрана "Статьи" (поле поиска и список).
 *
 * @param query Текущий поисковый запрос.
 * @param onQueryChange Коллбэк для обновления запроса в ViewModel.
 * @param categoryItems Список моделей для отображения.
 */
@Composable
private fun ArticlesScreenContent(
    query: String,
    onQueryChange: (String) -> Unit,
    categoryItems: List<ListItemModel>
) {
    Column(modifier = Modifier.fillMaxSize()) {
        TextField(
            value = query,
            onValueChange = onQueryChange,
            modifier = Modifier
                .fillMaxWidth(),
            placeholder = { Text(stringResource(id = R.string.search_placeholder_text)) },
            leadingIcon = {
                Icon(
                    painter = painterResource(id = R.drawable.ic_articles_search),
                    contentDescription = stringResource(id = R.string.search)
                )
            },
            singleLine = true,
            colors = TextFieldDefaults.colors(
                focusedIndicatorColor = MaterialTheme.colorScheme.primary,
                unfocusedIndicatorColor = MaterialTheme.colorScheme.outline
            )
        )

        Divider()

        if (categoryItems.isEmpty()) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Text(
                    text = stringResource(R.string.no_articles_found),
                    textAlign = TextAlign.Center,
                    modifier = Modifier.padding(16.dp)
                )
            }
        } else {
            LazyColumn {
                items(items = categoryItems, key = { it.id }) { model ->
                    ListItem(model = model)
                    Divider()
                }
            }
        }
    }
}