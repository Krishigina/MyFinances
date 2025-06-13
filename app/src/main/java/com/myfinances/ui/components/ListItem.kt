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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.myfinances.R

@Composable
fun ListItem(model: ListItemModel) {
    val itemHeight = if (model.type == ItemType.TOTAL) 56.dp else 70.dp
    val backgroundColor = if (model.type == ItemType.TOTAL) {
        MaterialTheme.colorScheme.secondary
    } else {
        MaterialTheme.colorScheme.background
    }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(itemHeight)
            .background(backgroundColor)
            .clickable(enabled = model.type != ItemType.TOTAL, onClick = model.onClick)
            .padding(horizontal = 16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        model.leadingIcon?.let { icon ->
            if (model.type == ItemType.TRANSACTION) {
                Box(
                    modifier = Modifier
                        .size(24.dp)
                        .clip(CircleShape)
                        .background(MaterialTheme.colorScheme.secondary),
                    contentAlignment = Alignment.Center
                ) {
                    when (icon) {
                        is LeadingIcon.Emoji -> Text(text = icon.char, fontSize = 16.sp)
                        is LeadingIcon.Resource -> Icon(
                            painterResource(id = icon.id),
                            contentDescription = null,
                            modifier = Modifier.size(16.dp)
                        )
                    }
                }
                Spacer(modifier = Modifier.width(16.dp))
            }
        }

        Column(modifier = Modifier.weight(1f)) {
            val titleStyle = if (model.type == ItemType.TOTAL) {
                MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.Normal)
            } else {
                MaterialTheme.typography.bodyLarge
            }
            Text(text = model.title, style = titleStyle)
            model.subtitle?.let {
                Text(text = it, style = MaterialTheme.typography.bodyMedium, color = Color.Gray)
            }
        }

        model.trailingContent?.let { content ->
            Spacer(modifier = Modifier.width(16.dp))
            val trailingTextStyle = MaterialTheme.typography.bodyLarge.copy(
                fontWeight = if (model.type == ItemType.TOTAL) FontWeight.Bold else FontWeight.Normal
            )
            when (content) {
                is TrailingContent.ArrowOnly -> Icon(
                    painterResource(R.drawable.ic_list_item_arrow),
                    contentDescription = null
                )

                is TrailingContent.TextOnly -> Text(text = content.text, style = trailingTextStyle)
                is TrailingContent.TextWithArrow -> {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(text = content.text, style = trailingTextStyle)
                        Spacer(modifier = Modifier.width(8.dp))
                        Icon(
                            painterResource(R.drawable.ic_list_item_arrow),
                            contentDescription = null
                        )
                    }
                }

                is TrailingContent.Switch -> Switch(
                    checked = content.isChecked,
                    onCheckedChange = content.onToggle
                )
            }
        }
    }
}