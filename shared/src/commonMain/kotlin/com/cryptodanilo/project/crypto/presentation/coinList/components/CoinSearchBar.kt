package com.cryptodanilo.project.crypto.presentation.coinList.components

import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.focusProperties
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.tooling.preview.Preview
import com.cryptodanilo.project.ui.theme.CryptoTrackerTheme
import com.cryptodanilo.project.ui.theme.CryptoTrackerThemeProvider
import cryptotrackerdanilo.shared.generated.resources.Res
import cryptotrackerdanilo.shared.generated.resources.clear_search
import cryptotrackerdanilo.shared.generated.resources.search_assets
import kotlinx.coroutines.delay
import org.jetbrains.compose.resources.stringResource
import kotlin.time.Duration.Companion.milliseconds

@Composable
fun CoinSearchBar(
    query: String,
    onQueryChange: (String) -> Unit,
    modifier: Modifier = Modifier,
    isFocusable: Boolean = true,
) {
    val focusManager = LocalFocusManager.current
    val interactionSource = remember { MutableInteractionSource() }
    val colors =
        OutlinedTextFieldDefaults.colors(
            unfocusedBorderColor = CryptoTrackerTheme.colors.outline,
            focusedBorderColor = CryptoTrackerTheme.colors.primary,
        )
    // The field otherwise grants itself focus asynchronously right after the screen is shown,
    // which also opens the keyboard. Keeping it non-focusable for that brief window means there's
    // nothing to grant focus to in the first place, so the field only focuses (and only opens the
    // keyboard) from a deliberate user click, matching the previous OutlinedTextField behavior.
    var canAcceptFocus by remember { mutableStateOf(false) }
    LaunchedEffect(Unit) {
        delay(300.milliseconds)
        canAcceptFocus = true
    }
    BasicTextField(
        value = query,
        onValueChange = onQueryChange,
        modifier =
            modifier
                .fillMaxWidth()
                .height(CryptoTrackerTheme.sizing.searchBarHeight)
                .padding(horizontal = CryptoTrackerTheme.spacing.small, vertical = CryptoTrackerTheme.spacing.small)
                .focusProperties { canFocus = isFocusable && canAcceptFocus },
        textStyle = CryptoTrackerTheme.typography.bodyMedium.copy(color = CryptoTrackerTheme.colors.onSurface),
        cursorBrush = SolidColor(CryptoTrackerTheme.colors.primary),
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
        singleLine = true,
        interactionSource = interactionSource,
        decorationBox = { innerTextField ->
            OutlinedTextFieldDefaults.DecorationBox(
                value = query,
                innerTextField = innerTextField,
                enabled = true,
                singleLine = true,
                visualTransformation = VisualTransformation.None,
                interactionSource = interactionSource,
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
                colors = colors,
                contentPadding =
                    PaddingValues(
                        horizontal = CryptoTrackerTheme.spacing.medium,
                    ),
                container = {
                    OutlinedTextFieldDefaults.Container(
                        enabled = true,
                        isError = false,
                        interactionSource = interactionSource,
                        colors = colors,
                        shape = CircleShape,
                    )
                },
            )
        },
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
