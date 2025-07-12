package com.myfinances.ui.mappers

import com.myfinances.domain.entity.Category
import com.myfinances.ui.model.ArticleItemUiModel

class CategoryDomainToUiMapper {
    fun mapToUiModel(category: Category): ArticleItemUiModel {
        return ArticleItemUiModel(
            id = category.id.toString(),
            title = category.name,
            emoji = category.emoji ?: "❓"
        )
    }
}