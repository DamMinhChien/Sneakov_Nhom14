package com.firebase.sneakov.ui.compose

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.firebase.sneakov.ui.theme.SneakovTheme
import compose.icons.FontAwesomeIcons
import compose.icons.fontawesomeicons.Solid
import compose.icons.fontawesomeicons.solid.Search
import compose.icons.fontawesomeicons.solid.Times

@Composable
fun SearchBar(
    query: String,
    placeholder: String = "Bạn đang cần đôi giày nào...",
    onQueryChange: (String) -> Unit,
    onSearch: () -> Unit,
    modifier: Modifier = Modifier // 👈 thêm dòng này
) {
    OutlinedTextField(
        value = query,
        onValueChange = onQueryChange,
        placeholder = { Text(placeholder) },
        leadingIcon = {
            Icon(
                imageVector = FontAwesomeIcons.Solid.Search,
                contentDescription = "Search",
                modifier = Modifier.size(18.dp)
            )
        },
        trailingIcon = {
            if (query.isNotEmpty()) {
                Icon(
                    imageVector = FontAwesomeIcons.Solid.Times,
                    contentDescription = "Clear",
                    modifier = Modifier
                        .size(18.dp)
                        .clickable { onQueryChange("") }
                )
            }
        },
        singleLine = true,
        shape = RoundedCornerShape(12.dp),
        modifier = modifier // 👈 dùng modifier truyền vào đây
            .fillMaxWidth()
            .padding(horizontal = 16.dp)
            .height(56.dp)
            .shadow(
                elevation = 6.dp,
                shape = RoundedCornerShape(12.dp),
                ambientColor = Color.Black.copy(alpha = 0.25f),
                spotColor = Color.Black.copy(alpha = 0.25f)
            ),
        colors = OutlinedTextFieldDefaults.colors(
            focusedBorderColor = MaterialTheme.colorScheme.primary,
            unfocusedBorderColor = MaterialTheme.colorScheme.outline,
            focusedContainerColor = MaterialTheme.colorScheme.surface,
            unfocusedContainerColor = MaterialTheme.colorScheme.surface
        ),
        keyboardActions = KeyboardActions(onSearch = {
            if (query.isNotBlank()) {
                onSearch()
            }
        }),
        keyboardOptions = KeyboardOptions.Default.copy(imeAction = ImeAction.Search)
    )
}

@Composable
@Preview(showSystemUi = true, showBackground = true)
fun SearchPreView() {
    SneakovTheme {
        SearchBar(
            query = "",
            placeholder = "",
            onQueryChange = {},
            onSearch = {}
        )
    }
}