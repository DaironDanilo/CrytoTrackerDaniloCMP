package com.cryptodanilo.project.crypto.presentation.coinList.components

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.focusProperties
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.tooling.preview.Preview
import com.cryptodanilo.project.ui.theme.CryptoTrackerTheme
import com.cryptodanilo.project.ui.theme.CryptoTrackerThemeProvider
import cryptotrackerdanilo.shared.generated.resources.Res
import cryptotrackerdanilo.shared.generated.resources.clear_search
import cryptotrackerdanilo.shared.generated.resources.search_assets
import org.jetbrains.compose.resources.stringResource

@Composable
fun CoinSearchBar(
    query: String,
    onQueryChange: (String) -> Unit,
    modifier: Modifier = Modifier,
    isFocusable: Boolean = true,
) {
    val focusManager = LocalFocusManager.current
    OutlinedTextField(
        value = query,
        onValueChange = onQueryChange,
        modifier =
            modifier
                .fillMaxWidth()
                .focusProperties { canFocus = isFocusable }
                .padding(horizontal = CryptoTrackerTheme.spacing.small, vertical = CryptoTrackerTheme.spacing.small),
        placeholder = {
            Text(
                text = stringResource(Res.string.search_assets),
                style = CryptoTrackerTheme.typography.bodyMedium,
                color = CryptoTrackerTheme.colors.onSurfaceVariant,
            )
        },
        leadingIcon = {
            Icon(
                imageVector = Icons.Default.Search,
                contentDescription = null,
                tint = CryptoTrackerTheme.colors.onSurfaceVariant,
            )
        },
        trailingIcon = {
            if (query.isNotEmpty()) {
                IconButton(onClick = {
                    onQueryChange("")
                }) {
                    Icon(
                        imageVector = Icons.Default.Close,
                        contentDescription = stringResource(Res.string.clear_search),
                        tint = CryptoTrackerTheme.colors.onSurfaceVariant,
                    )
                }
            }
        },
        keyboardOptions =
            KeyboardOptions(
                imeAction = ImeAction.Search,
            ),
        keyboardActions =
            KeyboardActions(
                onSearch = {
                    focusManager.clearFocus()
                },
            ),
        shape = CircleShape,
        textStyle = CryptoTrackerTheme.typography.bodyMedium,
        colors =
            OutlinedTextFieldDefaults.colors(
                unfocusedBorderColor = CryptoTrackerTheme.colors.outline,
                focusedBorderColor = CryptoTrackerTheme.colors.primary,
            ),
        singleLine = true,
    )
}

@Preview(showBackground = true, backgroundColor = 0xFF1C1B1FL, name = "Dark - Empty")
@Composable
private fun CoinSearchBarEmptyPreview() {
    CryptoTrackerThemeProvider(darkTheme = true) {
        CoinSearchBar(query = "", onQueryChange = {})
    }
}

@Preview(showBackground = true, backgroundColor = 0xFF1C1B1FL, name = "Dark - With text")
@Composable
private fun CoinSearchBarWithTextPreview() {
    CryptoTrackerThemeProvider(darkTheme = true) {
        CoinSearchBar(query = "bitcoin", onQueryChange = {})
    }
}
