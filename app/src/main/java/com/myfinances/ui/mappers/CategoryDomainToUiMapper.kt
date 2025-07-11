package com.myfinances.ui.mappers

import com.myfinances.domain.entity.Category
import com.myfinances.ui.components.ItemType
import com.myfinances.ui.components.LeadingIcon
import com.myfinances.ui.components.ListItemModel
import javax.inject.Inject

class CategoryDomainToUiMapper @Inject constructor() {
    fun toListItemModel(category: Category): ListItemModel {
        return ListItemModel(
            id = category.id.toString(),
            type = ItemType.TRANSACTION,
            leadingIcon = category.emoji?.let { LeadingIcon.Emoji(it) },
            title = category.name,
            trailingContent = null,
            showTrailingArrow = false
        )
    }
}