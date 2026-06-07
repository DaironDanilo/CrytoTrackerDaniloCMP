package com.cryptodanilo.project.crypto.presentation.coinList.components

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.cryptodanilo.project.ui.theme.CryptoTrackerTheme
import com.cryptodanilo.project.ui.theme.CryptoTrackerThemeProvider
import cryptotrackerdanilo.shared.generated.resources.Res
import cryptotrackerdanilo.shared.generated.resources.search_assets
import org.jetbrains.compose.resources.stringResource

@Composable
fun CoinSearchBar(
    query: String,
    onQueryChange: (String) -> Unit,
    modifier: Modifier = Modifier,
) {
    OutlinedTextField(
        value = query,
        onValueChange = onQueryChange,
        modifier =
            modifier
                .fillMaxWidth()
                .padding(horizontal = CryptoTrackerTheme.spacing.small, vertical = CryptoTrackerTheme.spacing.small),
        placeholder = {
            Text(
                text = stringResource(Res.string.search_assets),
                style = CryptoTrackerTheme.typography.bodyMedium,
                color = CryptoTrackerTheme.colors.onSurfaceVariant,
            )
        },
        trailingIcon = {
            Icon(
                imageVector = Icons.Default.Search,
                contentDescription = null,
                tint = CryptoTrackerTheme.colors.onSurfaceVariant,
            )
        },
        textStyle = CryptoTrackerTheme.typography.bodyMedium,
        colors =
            OutlinedTextFieldDefaults.colors(
                unfocusedBorderColor = CryptoTrackerTheme.colors.outline,
                focusedBorderColor = CryptoTrackerTheme.colors.primary,
            ),
        singleLine = true,
    )
}

@Preview(showBackground = true, backgroundColor = 0xFF1C1B1FL, name = "Dark")
@Composable
private fun CoinSearchBarPreview() {
    CryptoTrackerThemeProvider(darkTheme = true) {
        CoinSearchBar(query = "", onQueryChange = {})
    }
}