package io.horizontalsystems.bankwallet.ui.compose.components

import androidx.annotation.DrawableRes
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.ui.core.px
import io.horizontalsystems.bankwallet.R
import io.horizontalsystems.bankwallet.modules.balance.BalanceViewItem
import io.horizontalsystems.bankwallet.modules.balance.newBalanceViewModel

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
    tabPadding: Dp = 15.dp,
    cornerRadius: Dp = 20.dp,
    spacing: Dp = 15.dp,
    enabledTabs: List<Boolean> = tabs.map { true } // By default, all tabs are enabled
) {
    Row(
        modifier = modifier
            .background(color = Color.Transparent, shape = RoundedCornerShape(cornerRadius))
            .padding(horizontal = spacing / 12 )
    ) {
        tabs.zip(enabledTabs).forEachIndexed { index, (title, enabled) ->
            val isSelected = selectedTabIndex == index


            Box(
                modifier = Modifier
                    .height(40.dp)
                    .weight(1f)
                    .padding(horizontal = spacing / 1)
                    .background(color = if (isSelected) selectedTabColor else if (enabled) tabColor else tabColor.copy(alpha = 1.0f), shape = RoundedCornerShape(cornerRadius))
                    .clickable(enabled) { onTabSelected(index) }
                    .padding(horizontal = tabPadding, vertical = 5.dp)
                    .align(Alignment.CenterVertically)
            ) {
                Text(
                    modifier = Modifier.align(Alignment.Center),
                    text = title,
                    maxLines = 2,
                    color = if (isSelected) selectedTextColor else if (enabled) textColor else textColor.copy(alpha = 1.0f),
                    fontWeight = FontWeight.W500,
                    fontSize = if (title.length > 7) 11.sp else 14.sp
                )
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

    Box(
        modifier = modifier
            .background(color = Color(31, 34, 42, 1), shape = CircleShape)
            .padding(0.5.dp)
            .size(100.dp)
            .clickable(onClick = {})
    ) {
        Image(
            painter = painter,
            contentDescription = "drawable_icons",
            modifier = Modifier
                .size(55.dp)
                .align(Alignment.Center),
            contentScale = ContentScale.Fit
        )
        Text(
            text = text,
            modifier = Modifier
                .padding(top = 80.dp)
                .align(Alignment.BottomCenter),
            color = Color.White,
            fontSize = if (text.length > 6) 14.5.sp else 16.sp

        )
    }
}
@Composable
fun WalletActionsRow( navController: NavController) {
    val buttonWidth = (LocalContext.current.resources.displayMetrics.widthPixels / 3).toFloat()

    Row(
        horizontalArrangement = Arrangement.End,
        modifier = Modifier.fillMaxWidth()
    ) {
        walletActionButton(
            drawableId = R.drawable.send_icon,
            text = "Send",
            modifier = Modifier.weight(1f).width(buttonWidth.dp),
                    onClick = { navController.navigate(R.id.sendXFragment) }
        )

        walletActionButton(
            drawableId = R.drawable.swap_icon,
            text = "Swap",
            modifier = Modifier.weight(1f).width(buttonWidth.dp),
                    onClick = { navController.navigate(R.id.swapFragment) }
        )

        walletActionButton(
            drawableId = R.drawable.receive_icon,
            text = "Receive",
            modifier = Modifier.weight(1f).width(buttonWidth.dp),
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



