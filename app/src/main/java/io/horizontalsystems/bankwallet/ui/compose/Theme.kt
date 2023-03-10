package io.horizontalsystems.bankwallet.ui.compose

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.compositionLocalOf
import androidx.compose.runtime.remember
import androidx.compose.ui.graphics.Color


//Kurama Palette
/*
val KuramalightPalette = Colors(
    jacob = YellowL,
    remus = GreenL,
    lucian = RedL,
    tyler = Light,
    bran = Dark,
    leah = SteelDark,
    claude = Light,
    lawrence = Color.White,
    jeremy = SteelLight,
    laguna = Color.White,
    raina = Color.Black,
)

val KuramadarkPalette = Colors(
    jacob = YellowD,
    remus = GreenD,
    lucian = RedD,
    tyler = Dark,
    bran = LightGrey,
    leah = SteelLight,
    claude = Dark,
    lawrence = SteelDark,
    jeremy = Steel20,
    laguna = Steel20,
    raina = Color.White,
)


 */


//Old Palette
val lightPalette = Colors(
    jacob = YellowL,
    remus = GreenL,
    lucian = RedL,
    tyler = Light,
    bran = Dark,
    leah = SteelDark,
    claude = Color.White,
    lawrence = Color.White,
    jeremy = SteelLight,
    laguna = LagunaL,
    raina = White50,
    text = Color.Black
)



val darkPalette = Colors(
    jacob = YellowD,
    remus = GreenD,
    lucian = RedD,
    tyler = Dark,
    bran = LightGrey,
    leah = SteelLight,
    claude = Dark,
    lawrence = SteelDark,
    jeremy = Steel20,
    laguna = LagunaD,
    raina = Steel10,
    text = Color.White
)

@Composable
fun ComposeAppTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable() () -> Unit
) {

    val colors = if (darkTheme) {
        darkPalette
    } else {
        lightPalette
    }

    //custom styles
    ProvideLocalAssets(colors = colors, typography = Typography()) {
        //material styles
        MaterialTheme(
            content = content
        )
    }

}

object ComposeAppTheme {
    val colors: Colors
        @Composable
        get() = LocalColors.current

    val typography: Typography
        @Composable
        get() = LocalTypography.current
}

@Composable
fun ProvideLocalAssets(
    colors: Colors,
    typography: Typography,
    content: @Composable () -> Unit
) {

    val colorPalette = remember {
        // Explicitly creating a new object here so we don't mutate the initial [colors]
        // provided, and overwrite the values set in it.
        colors.copy()
    }
    colorPalette.update(colors)
    CompositionLocalProvider(
        LocalColors provides colorPalette,
        LocalTypography provides typography,
        content = content
    )
}

val LocalColors = compositionLocalOf<Colors> {
    error("No Colors provided")
}
