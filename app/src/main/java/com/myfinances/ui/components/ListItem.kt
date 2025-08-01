package com.myfinances.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import com.myfinances.R
import com.myfinances.ui.theme.LocalDimensions

/**
 * Универсальный компонент для отображения элемента списка.
 * Адаптирует свой внешний вид в зависимости от типа, определенного в [ListItemModel].
 * Может содержать иконку, заголовок, подзаголовок и различное содержимое в конце.
 */
@Composable
fun ListItem(model: ListItemModel) {
    val dimensions = LocalDimensions.current

    val isTotalType = model.type == ItemType.TOTAL
    val isSettingType = model.type == ItemType.SETTING

    val itemHeight = when {
        isTotalType -> dimensions.listItem.heightTotal
        isSettingType -> dimensions.listItem.heightTotal
        else -> dimensions.listItem.heightTransaction
    }
    val backgroundColor = when {
        isTotalType -> MaterialTheme.colorScheme.secondary
        else -> MaterialTheme.colorScheme.background
    }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(itemHeight)
            .background(backgroundColor)
            .clickable(enabled = model.onClick != null, onClick = { model.onClick?.invoke() })
            .padding(horizontal = dimensions.spacing.paddingLarge),
        verticalAlignment = Alignment.CenterVertically
    ) {
        model.leadingIcon?.let { icon ->
            val iconBackgroundColor = if (model.useWhiteIconBackground) {
                MaterialTheme.colorScheme.background
            } else {
                MaterialTheme.colorScheme.secondary
            }
            Box(
                modifier = Modifier
                    .size(dimensions.icon.medium)
                    .clip(CircleShape)
                    .background(iconBackgroundColor),
                contentAlignment = Alignment.Center
            ) {
                when (icon) {
                    is LeadingIcon.Emoji -> Text(text = icon.char, fontSize = 16.sp)
                    is LeadingIcon.Resource -> Icon(
                        painterResource(id = icon.id),
                        contentDescription = null,
                        modifier = Modifier.size(dimensions.spacing.paddingLarge)
                    )
                    is LeadingIcon.Vector -> Icon(
                        imageVector = icon.imageVector,
                        contentDescription = null,
                        modifier = Modifier.size(dimensions.icon.medium)
                    )
                }
            }
            Spacer(modifier = Modifier.width(dimensions.spacing.paddingLarge))
        }

        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = model.title,
                style = MaterialTheme.typography.bodyLarge
            )
            model.subtitle?.let {
                if (it.isNotBlank()) {
                    Text(
                        text = it,
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }

        model.trailingContent?.let { content ->
            Spacer(modifier = Modifier.width(dimensions.spacing.paddingLarge))
            Row(verticalAlignment = Alignment.CenterVertically) {
                val trailingTextStyle = MaterialTheme.typography.bodyLarge.copy(
                    fontWeight = FontWeight.Normal
                )
                when (content) {
                    is TrailingContent.ArrowOnly -> {
                        val iconRes = content.customIconRes ?: R.drawable.ic_list_item_arrow
                        Icon(
                            painter = painterResource(iconRes),
                            contentDescription = null
                        )
                    }
                    is TrailingContent.TextOnly -> {
                        Text(text = content.text, style = trailingTextStyle)
                    }
                    is TrailingContent.TextWithArrow -> {
                        Column(horizontalAlignment = Alignment.End) {
                            Text(text = content.text, style = trailingTextStyle)
                            content.secondaryText?.let {
                                Text(
                                    text = it,
                                    style = MaterialTheme.typography.bodyLarge,
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                            }
                        }
                    }
                    is TrailingContent.Switch -> {
                        val switchModifier = model.trailingContentTestTag?.let {
                            Modifier.testTag(it)
                        } ?: Modifier

                        Switch(
                            checked = content.isChecked,
                            onCheckedChange = content.onToggle,
                            modifier = switchModifier
                        )
                    }
                    is TrailingContent.Custom -> {
                        content.content()
                    }
                }

                if (model.showTrailingArrow) {
                    Spacer(modifier = Modifier.width(dimensions.spacing.paddingMedium))
                    Icon(
                        painter = painterResource(model.trailingArrowIconRes ?: R.drawable.ic_list_item_arrow),
                        contentDescription = null
                    )
                }
            }
        }
    }
}