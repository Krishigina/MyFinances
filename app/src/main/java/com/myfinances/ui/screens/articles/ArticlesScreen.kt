package com.myfinances.ui.screens.articles

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Divider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.myfinances.R
import com.myfinances.domain.entity.Category
import com.myfinances.ui.components.ListItem
import com.myfinances.ui.components.ListItemModel
import com.myfinances.ui.components.SearchField
import com.myfinances.ui.mappers.toListItemModel

/**
 * Экран для отображения списка "статей" (категорий расходов).
 * Управляется [ArticlesViewModel] для получения данных и обработки состояний.
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
                ArticlesScreenContent(categoryItems = state.categoryItems)
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
 * Компонент, отвечающий за отрисовку контента экрана "Статьи"
 * при успешной загрузке данных.
 *
 * @param categoryItems Готовый к отображению список моделей UI.
 */
@Composable
private fun ArticlesScreenContent(
    categoryItems: List<ListItemModel>
) {
    Column(modifier = Modifier.fillMaxSize()) {
        SearchField(placeholderText = stringResource(id = R.string.search_placeholder_text))
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
                items(
                    items = categoryItems,
                    key = { it.id }
                ) { model ->
                    ListItem(model = model)
                    Divider()
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun ArticlesScreenPreview() {
    val previewCategories = listOf(
        Category(1, "Продукты", "🛒", false),
        Category(2, "Транспорт", "🚗", false)
    )
    val previewItems = previewCategories.map { it.toListItemModel() }
    ArticlesScreenContent(categoryItems = previewItems)
}