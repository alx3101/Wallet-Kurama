package io.horizontalsystems.bankwallet.ui.compose.components

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color

import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import io.horizontalsystems.bankwallet.ui.compose.ComposeAppTheme


@Composable
fun createWalletButton(
    modifier: Modifier = Modifier,
    imageVector: ImageVector,
    buttonText: String,
    onClick: () -> Unit,
    enable: Boolean = true,
    backgroundColor: Color,
    fontColor: Color,
) {

    val itemColorFigma = Color(red = 31, green = 34, blue = 42)

    Button(
        onClick = onClick,
        modifier = modifier

            .height(72.dp)
            .shadow(0.dp),
        elevation = ButtonDefaults.elevation(
            defaultElevation = 0.dp,
            pressedElevation = 0.dp,
            hoveredElevation = 0.dp,
            focusedElevation = 0.dp
        ),
        shape = RoundedCornerShape(15.dp),
        contentPadding = PaddingValues(12.dp),
        colors = ButtonDefaults.buttonColors(
            backgroundColor = ComposeAppTheme.colors.lawrence
        ),
    ) {
        Column (
            modifier = Modifier
                .align(Alignment.CenterVertically)

        ) {
            Column (
                modifier = Modifier
                    .fillMaxWidth()
                    .align(Alignment.CenterHorizontally)
            ) {
                Spacer(modifier = Modifier.width(150.dp))
                Icon(
                    imageVector = imageVector,
                    modifier = Modifier
                        .size(24.dp)
                        .align(Alignment.CenterHorizontally),
                    contentDescription = "drawable_icons",
                    tint = Color.Unspecified
                )
            }
            Spacer(modifier = Modifier.weight(1f))

            Text(
                modifier = Modifier.align(Alignment.CenterHorizontally),
                text = buttonText,
                color = ComposeAppTheme.colors.text,
                textAlign = TextAlign.Center,
                fontSize = 15.sp,
                fontWeight = FontWeight.W400

                )
        }
    }
}

@Composable
fun walletButton(
    modifier: Modifier = Modifier,
    imageVector: ImageVector,
    buttonText: String,
    onClick: () -> Unit,
    enable: Boolean = true,
    backgroundColor: Color,
    fontColor: Color,
) {

    val itemColorFigma = Color(red = 31, green = 34, blue = 42)

    Button(
        onClick = onClick,
        modifier = modifier
            .fillMaxWidth()
            .height(100.dp)
            .shadow(0.dp),
        elevation = ButtonDefaults.elevation(
            defaultElevation = 0.dp,
            pressedElevation = 0.dp,
            hoveredElevation = 0.dp,
            focusedElevation = 0.dp
        ),
        shape = RoundedCornerShape(100.dp),
        contentPadding = PaddingValues(15.dp),
        colors = ButtonDefaults.buttonColors(
            backgroundColor = Color.Transparent
        ),
    ) {
        Column (
            modifier = Modifier
                .align(Alignment.CenterVertically)

        ) {
            Column (
                modifier = Modifier
                    .fillMaxWidth()
                    .align(Alignment.CenterHorizontally)
            ) {
                Spacer(modifier = Modifier.width(150.dp))
                Icon(
                    imageVector = imageVector,
                    modifier = Modifier
                        .size(400.dp)
                        .align(Alignment.CenterHorizontally),
                    contentDescription = "drawable_icons",
                    tint = Color.Unspecified
                )
            }
            Spacer(modifier = Modifier.weight(1f))

            Text(
                modifier = Modifier.align(Alignment.CenterHorizontally),
                text = buttonText,
                color = ComposeAppTheme.colors.text,
                textAlign = TextAlign.Center,
                fontSize = 16.sp,
                fontWeight = FontWeight.W400

            )
        }
    }
}

