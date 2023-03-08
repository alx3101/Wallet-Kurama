package io.horizontalsystems.bankwallet.ui.compose.components

import androidx.annotation.DrawableRes
import androidx.compose.Compose
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Icon
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.ui.core.px
import io.horizontalsystems.bankwallet.R
import io.horizontalsystems.bankwallet.modules.balance.BalanceViewItem
import io.horizontalsystems.bankwallet.modules.balance.newBalanceViewModel
import io.horizontalsystems.bankwallet.ui.compose.ComposeAppTheme

@Composable

fun KuramaTabRow(
    tabs: List<String>,
    selectedTabIndex: Int,
    onTabSelected: (Int) -> Unit,
    modifier: Modifier = Modifier,
    tabColor: Color = Color.Gray,
    selectedTabColor: Color = Color.Black,
    textColor: Color = Color.White,
    selectedTextColor: Color = Color.White,
    tabPadding: Dp = 1.dp,
    cornerRadius: Dp = 20.dp,
    spacing: Dp = 0.dp,
    enabledTabs: List<Boolean> = tabs.map { true } // By default, all tabs are enabled
) {

    val configuration = LocalConfiguration.current
    val screenWidth = configuration.screenWidthDp.dp

    val fontSize = with(LocalDensity.current) {
        if (screenWidth > 600.dp) {
            18.sp // Font size for larger screens
        } else {
            10.9.sp // Default font size
        }
    }


    Row(
        modifier = modifier
            .background(color = Color.Transparent, shape = RoundedCornerShape(cornerRadius))
            .padding(horizontal = spacing / 10)
    ) {
        tabs.zip(enabledTabs).forEachIndexed { index, (title, enabled) ->
            val isSelected = selectedTabIndex == index


            if (index == 2) { // Add an icon to the third tab
                Box(
                    modifier = Modifier
                        .heightIn(min = 40.dp)
                        .weight(1f)
                        .padding(horizontal = spacing / 3)
                        .background(
                            color = if (isSelected) selectedTabColor else if (enabled) ComposeAppTheme.colors.lawrence else tabColor.copy(
                                alpha = 1.0f
                            ),
                            shape = RoundedCornerShape(cornerRadius)
                        )
                        .clickable(enabled) { onTabSelected(index) }
                        .align(Alignment.CenterVertically)
                ) {
                    Row(
                        modifier = Modifier.align(Alignment.Center),
                        horizontalArrangement = Arrangement.SpaceAround
                    ) {
                        Text(
                            modifier = Modifier ,
                            text = title,
                            maxLines = 1,
                            color = if (isSelected) selectedTextColor else if (enabled) ComposeAppTheme.colors.text else textColor.copy(alpha = 1.0f),
                            fontWeight = FontWeight.W400,
                            fontSize = fontSize,
                            textAlign = TextAlign.Center
                        )
                        Spacer(modifier = Modifier.width(10.dp))
                        Image(
                            painter = painterResource(id = R.drawable.expand_wallet),
                            contentDescription = null,
                            modifier = Modifier
                                .size(10.dp)
                                .align(Alignment.CenterVertically)
                                .padding(top = 3.dp)
                        )
                    }
                }
            } else { // Add a regular tab
                Box(
                    modifier = Modifier
                        .height(40.dp)
                        .weight(1f)
                        .padding(horizontal = spacing / 3)
                        .background(
                            color = if (isSelected) selectedTabColor else if (enabled) ComposeAppTheme.colors.lawrence else tabColor.copy(
                                alpha = 1.0f
                            ), shape = RoundedCornerShape(cornerRadius)
                        )
                        .clickable(enabled) { onTabSelected(index) }
                        .padding(horizontal = tabPadding, vertical = 4.dp)
                        .align(Alignment.CenterVertically)
                ) {
                    Text(
                        modifier = Modifier.align(Alignment.Center),
                        text = title,
                        maxLines = 2,
                        color = if (isSelected) selectedTextColor else if (enabled) ComposeAppTheme.colors.text else textColor.copy(alpha = 1.0f),
                        fontWeight = FontWeight.W400,
                        fontSize = fontSize
                    )
                }
            }
        }
    }
}

@Composable
fun walletActionButton(
    drawableId: Int,
    text: String,
    modifier: Modifier = Modifier,
    onClick: () -> Unit,
) {
    val painter: Painter = painterResource(id = drawableId)
    val boxSize = 30.dp // adjust as needed

    BoxWithConstraints(modifier = modifier) {
        Box(
            modifier = Modifier
                .align(Alignment.TopCenter)
                .background(color = ComposeAppTheme.colors.lawrence, shape = CircleShape)
                .padding(10.dp)
                .size(boxSize)
                .clickable(onClick = onClick)
        ) {
            Image(
                painter = painter,
                contentDescription = "drawable_icons",
                modifier = Modifier
                    .size(boxSize / 1.0f)
                    .align(Alignment.Center),
                contentScale = ContentScale.Fit
            )
        }

        Box(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(top = 50.dp)
        ) {
            Text(
                text = text,
                color = ComposeAppTheme.colors.text,
                fontWeight = FontWeight.W400,
                fontSize = if (text.length > 6) 14.5.sp else 14.sp
            )
        }
    }
}



    @Composable
fun WalletActionsRow(navController: NavController) {
    val buttonWidth = (LocalContext.current.resources.displayMetrics.widthPixels / 3).toFloat()

    Row(
        horizontalArrangement = Arrangement.End,
        modifier = Modifier.fillMaxWidth()
    ) {
        walletActionButton(
            drawableId = R.drawable.send_kurama_icon,
            text = "Send",
            modifier = Modifier
                .weight(1f)
                .width(buttonWidth.dp)
                .align(Alignment.CenterVertically),
            onClick = { navController.navigate(R.id.sendXFragment) }
        )

        walletActionButton(
            drawableId = R.drawable.swap_kurama_icon,
            text = "Swap",
            modifier = Modifier
                .weight(1f)
                .width(buttonWidth.dp)
                .align(Alignment.CenterVertically),

            onClick = { navController.navigate(R.id.swapFragment) }
        )

        walletActionButton(
            drawableId = R.drawable.receive_kurama_icon,
            text = "Receive",
            modifier = Modifier
                .weight(1f)
                .width(buttonWidth.dp)
                .align(Alignment.CenterVertically),

            onClick = { navController.navigate(R.id.receiveFragment) }
        )
    }
}

/*
@Preview
@Composable
fun CircleButtonWithImageAndTextPreview() {
    CircleButtonWithImageAndText(
        imageVector = ImageVector.vectorResource(id = R.drawable.send_kurama_icon),
        text = "Send"
    )

}


 */



