package com.myfinances.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import com.myfinances.R
import com.myfinances.ui.theme.LocalDimensions

@Composable
fun SearchField(
    placeholderText: String
) {
    val dimensions = LocalDimensions.current

    Box(
        modifier = Modifier
            .fillMaxWidth()
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(dimensions.listItem.heightTotal)
                .background(MaterialTheme.colorScheme.tertiary)
                .padding(horizontal = dimensions.spacing.paddingLarge),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = placeholderText,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                style = MaterialTheme.typography.bodyLarge,
                modifier = Modifier.weight(1f)
            )
            Spacer(modifier = Modifier.width(dimensions.spacing.paddingMedium))
            Icon(
                painter = painterResource(id = R.drawable.ic_articles_search),
                contentDescription = stringResource(id = R.string.search),
                tint = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

@Preview
@Composable
fun SearchFieldPreview() {
    SearchField(placeholderText = stringResource(id = R.string.search_placeholder_text))
}