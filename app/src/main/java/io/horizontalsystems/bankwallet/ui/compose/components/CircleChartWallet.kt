package io.horizontalsystems.bankwallet.ui.compose.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color

import androidx.compose.foundation.layout.*

import androidx.compose.ui.geometry.Offset

import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp

@Composable
fun CircleChartWallet(modifier: Modifier) {
    Canvas(modifier = Modifier.fillMaxSize()) {
        val canvasWidth = size.width
        val canvasHeight = size.height

        drawCircle(
            color = Color.Black,
            center = Offset(x = canvasWidth / 2, y = canvasHeight / 2),
            radius = size.minDimension/1.9F
        )


    }
}


@Preview(name = "Pie Chart Preview",  showBackground = true, heightDp = 100, widthDp = 100)
@Composable
fun DefaultPreview() {
    CircleChartWallet(Modifier.width(50.dp).height(50.dp))

}