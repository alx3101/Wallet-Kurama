package io.horizontalsystems.bankwallet.ui.compose.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.*
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp

@Composable
fun CoinDataForWallet(modifier: Modifier = Modifier) {
    Box(modifier = modifier) {
        Row(modifier = Modifier) {
            Canvas(modifier = Modifier.height(50.dp).width(50.dp)) {
                val canvasWidth = size.width
                val canvasHeight = size.height
                val circleRadius = size.minDimension/3  // Adjust the circle radius to fit the size of the Canvas

                drawCircle(
                    color = Color.Black,
                    center = Offset(x = canvasWidth / 2, y = canvasHeight / 2),
                    radius = circleRadius
                )
            }
            Column(
                modifier = Modifier
                    .align(Alignment.CenterVertically)
                    .padding(10.dp)
                    .height(40.dp)
            ) {
                Text(text = "Coin")
                Spacer(Modifier.weight(1f))
                Text(text = "%")
            }
        }
    }
}

@Preview
@Composable
fun preview1()
{
    CoinDataForWallet()

}