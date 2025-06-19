package com.myfinances.ui.screens.articles

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Divider
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import com.myfinances.R
import com.myfinances.data.MockData
import com.myfinances.domain.entity.Category
import com.myfinances.ui.components.ItemType
import com.myfinances.ui.components.LeadingIcon
import com.myfinances.ui.components.ListItem
import com.myfinances.ui.components.ListItemModel
import com.myfinances.ui.components.SearchField

@Composable
fun ArticlesScreenContent(
    categories: List<Category>
) {
    val listItems = categories.map { category ->
        category.toListItemModel()
    }

    Column(modifier = Modifier.fillMaxSize()) {
        SearchField(placeholderText = stringResource(id = R.string.search_placeholder_text))
        Divider()
        LazyColumn {
            items(
                items = listItems,
                key = { it.id }
            ) { model ->
                ListItem(model = model)
                Divider()
            }
        }
    }
}

private fun Category.toListItemModel(): ListItemModel {
    return ListItemModel(
        id = this.id.toString(),
        type = ItemType.TRANSACTION,
        leadingIcon = this.emoji?.let { LeadingIcon.Emoji(it) },
        title = this.name,
        trailingContent = null,
        showTrailingArrow = false
    )
}

@Composable
fun ArticlesScreen() {
    val expenseCategories = MockData.categories.filter { !it.isIncome }
    ArticlesScreenContent(categories = expenseCategories)
}

@Preview(showBackground = true)
@Composable
fun ArticlesScreenPreview() {
    ArticlesScreen()
}