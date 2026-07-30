package com.cryptodanilo.project.core.presentation.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.Dp
import com.cryptodanilo.project.ui.theme.CryptoTrackerTheme
import cryptotrackerdanilo.shared.generated.resources.Res
import cryptotrackerdanilo.shared.generated.resources.add_to_favorites
import cryptotrackerdanilo.shared.generated.resources.remove_from_favorites
import org.jetbrains.compose.resources.stringResource

// Shared by the list row and the detail header — full-opacity accent-tinted when favorited,
// dimmed otherwise. Deliberately always the same (filled) glyph rather than switching to
// Icons.Outlined.Star for the unfavorited state: in this project's material-icons-core
// dependency, Outlined.Star renders as a solid shape indistinguishable from Filled.Star (no
// hollow center) — confirmed by rendering it at high zoom — so opacity is the only reliable way
// to tell the two states apart here, not icon shape.
//
// The clickable touch box (touchSize) is deliberately a fixed size, separate from the icon's
// own visual size (iconSize) — call sites must give this a non-weighted slot in their layout
// (not inside a Modifier.weight() row shared with other flexible content), otherwise the touch
// box can still get squeezed/clipped by neighboring content and end up inconsistently sized
// row to row.
@Composable
fun FavoriteStar(
    isFavorite: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    iconSize: Dp = CryptoTrackerTheme.sizing.iconMedium,
    touchSize: Dp = CryptoTrackerTheme.sizing.iconLarge,
) {
    Box(
        modifier = modifier.size(touchSize).clickable(onClick = onClick),
        contentAlignment = Alignment.Center,
    ) {
        Icon(
            imageVector = Icons.Filled.Star,
            contentDescription =
                stringResource(
                    if (isFavorite) Res.string.remove_from_favorites else Res.string.add_to_favorites,
                ),
            tint =
                if (isFavorite) {
                    CryptoTrackerTheme.colors.primary
                } else {
                    CryptoTrackerTheme.colors.onSurfaceVariant.copy(alpha = 0.35f)
                },
            modifier = Modifier.size(iconSize),
        )
    }
}
