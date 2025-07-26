package com.myfinances.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.sp
import com.myfinances.ui.theme.LocalDimensions

@Composable
fun EditableListItem(
    model: ListItemModel,
    value: String,
    onValueChange: (String) -> Unit,
    placeholder: String = "",
    keyboardType: KeyboardType = KeyboardType.Text,
    textAlign: TextAlign = TextAlign.Start
) {
    val dimensions = LocalDimensions.current
    val textStyle = MaterialTheme.typography.bodyLarge

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(dimensions.listItem.heightTotal)
            .background(MaterialTheme.colorScheme.background)
            .padding(horizontal = dimensions.spacing.paddingLarge),
        verticalAlignment = Alignment.CenterVertically
    ) {
        model.leadingIcon?.let { icon ->
            val iconBackgroundColor =
                if (model.useWhiteIconBackground) MaterialTheme.colorScheme.background else MaterialTheme.colorScheme.secondary
            Box(
                modifier = Modifier
                    .size(dimensions.icon.medium)
                    .clip(CircleShape)
                    .background(iconBackgroundColor),
                contentAlignment = Alignment.Center
            ) {
                when (icon) {
                    is LeadingIcon.Emoji -> Text(icon.char, fontSize = 16.sp)
                    is LeadingIcon.Resource -> Icon(
                        painter = painterResource(id = icon.id),
                        contentDescription = null,
                        modifier = Modifier.size(dimensions.spacing.paddingLarge),
                        tint = MaterialTheme.colorScheme.onSecondary
                    )

                    is LeadingIcon.Vector -> Icon(
                        imageVector = icon.imageVector,
                        contentDescription = null,
                        modifier = Modifier.size(dimensions.icon.medium)
                    )
                }
            }
            Spacer(Modifier.width(dimensions.spacing.paddingLarge))
        }

        if (model.title.isNotEmpty()) {
            Text(text = model.title, style = textStyle)
            Spacer(Modifier.width(dimensions.spacing.paddingLarge))
        }

        BasicTextField(
            value = value,
            onValueChange = onValueChange,
            modifier = Modifier.weight(1f),
            textStyle = textStyle.copy(
                textAlign = textAlign,
                color = MaterialTheme.colorScheme.onBackground
            ),
            keyboardOptions = KeyboardOptions(keyboardType = keyboardType),
            singleLine = true,
            cursorBrush = SolidColor(MaterialTheme.colorScheme.primary),
            decorationBox = { innerTextField ->
                Box(contentAlignment = if (textAlign == TextAlign.End) Alignment.CenterEnd else Alignment.CenterStart) {
                    if (value.isEmpty() && placeholder.isNotEmpty()) {
                        Text(placeholder, color = MaterialTheme.colorScheme.onSurfaceVariant, style = textStyle)
                    }
                    innerTextField()
                }
            }
        )
    }
}