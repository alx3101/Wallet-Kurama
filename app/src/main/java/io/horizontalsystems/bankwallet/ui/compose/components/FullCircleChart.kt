package io.horizontalsystems.bankwallet.ui.compose.components

import android.content.res.Resources
import android.util.Log
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.fontResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.res.ResourcesCompat
import androidx.ui.core.px
import io.horizontalsystems.bankwallet.R
import io.horizontalsystems.bankwallet.modules.balance.BalanceViewModel
import io.horizontalsystems.bankwallet.modules.balance.newBalanceViewModel
import io.horizontalsystems.core.helpers.HudHelper
import org.koin.core.component.getScopeId

@Composable
fun FullCircleChart(
    modifier: Modifier = Modifier,
    percentValues: List<Float>,
    viewModel: newBalanceViewModel,
    title: String,
    balance: String

) {
    val figmaOrange =  Color(237, 110, 0, 255)
    val sp = 21 * Resources.getSystem().displayMetrics.scaledDensity / (410/160)
    val context = LocalContext.current


    val colors = listOf(
        Color(237, 110, 0, 255), // Arancione
        Color(31, 27, 222, 255), // Blu
        Color(0, 102, 255, 255), // Ciano
        Color(27, 210, 222, 255), // Ciano
        Color(255, 255, 255, 255), // Azurro/Grigio

    )

    fun getColorForPercent(percent: Int): Color {
        return when {
            percent > 35 -> colors[0] // arancione
            percent > 30 -> colors[1] // blu
            percent > 20 -> colors[2] // ciano
            percent > 5 -> colors[3] // turchese
            else -> colors[4] // azzurro/grigio
        }
    }



    var startAngle = 0F



    val proportions = percentValues.mapIndexed { index, item ->
        val sweepAngle = item / 100 * 360F
        val color = getColorForPercent(item.toInt())
        val proportion = Triple(startAngle, sweepAngle, color)
        startAngle += sweepAngle
        proportion
    }

    val data = percentValues.mapIndexed { index, item ->
        val color = getColorForPercent(item.toInt())
        val data = Pair(item, color)
        data
    }


    Box(
        modifier = modifier
            .fillMaxSize()
            .aspectRatio(1f)
    ) {
        Canvas(
            modifier = Modifier.fillMaxWidth()
        ) {
            val strokeWidth = size.width * 0.07f // 20% of total width of chart
            val diameter = size.width - strokeWidth

            data.forEach { (percent, color) ->
                drawArc(
                    color = color,
                    startAngle = startAngle,
                    sweepAngle = percent / 100 * 360F,
                    useCenter = false,
                    topLeft = Offset(x = strokeWidth / 2f, y = strokeWidth / 2f),
                    style = Stroke(width = strokeWidth, cap = StrokeCap.Round),
                    size = Size(diameter, diameter)
                )
                startAngle += percent / 100 * 360F
            }
        }
        Column(
            modifier = Modifier
                .align(Alignment.Center)
        ) {


            Text(text = title,
                color = figmaOrange,
                style = TextStyle(
                    fontSize = 30.sp,
                    fontWeight = FontWeight.W400
                ),
                modifier = Modifier.align(Alignment.CenterHorizontally)
            )



            Text(text = "$balance",
          color = Color.White,
            style = TextStyle(
                fontSize = 25.sp,
                fontWeight = FontWeight.W500
            ))

            IconButton(
                onClick = {
                    viewModel.toggleBalanceVisibility()
                    HudHelper.vibrate(context)

                },
                modifier = Modifier
                    .size(20.dp)
                    .padding(top = 25.dp),

            ) {
                Image(
                    painter = painterResource(id = R.drawable.hide_balance),
                    contentDescription = null
                )
            }


        }
    }
}

