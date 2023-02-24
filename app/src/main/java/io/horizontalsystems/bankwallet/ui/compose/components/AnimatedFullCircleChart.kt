package io.horizontalsystems.bankwallet.ui.compose.components

import android.content.res.Resources
import android.util.Log
import androidx.compose.animation.Animatable
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.TweenSpec
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
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
fun AnimatedFullCircleChart(
    modifier: Modifier = Modifier,
    percentValues: List<Float>,
    viewModel: newBalanceViewModel,
    title: String,
    balance: String

) {
   val aspectRation = remember { androidx.compose.animation.core.Animatable(1f) }
    val animationSpec = remember { TweenSpec<Float>( durationMillis = 1000) }

    LaunchedEffect(key1 = null) {

        aspectRation.animateTo(
            targetValue = 1f,
            animationSpec = animationSpec
        )
    }

    FullCircleChart(modifier = Modifier.aspectRatio(aspectRation.value) ,
        percentValues = percentValues,
        viewModel = viewModel ,
        title = title ,
        balance = balance )

}

