package io.horizontalsystems.bankwallet.modules.balance.ui

import android.os.Bundle
import android.service.quickaccesswallet.WalletCard
import android.util.Log
import androidx.annotation.DrawableRes
import androidx.compose.animation.Crossfade
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.google.accompanist.swiperefresh.rememberSwipeRefreshState
import io.horizontalsystems.bankwallet.R
import io.horizontalsystems.bankwallet.core.slideFromBottom
import io.horizontalsystems.bankwallet.core.slideFromRight
import io.horizontalsystems.bankwallet.entities.CurrencyValue
import io.horizontalsystems.bankwallet.entities.ViewState
import io.horizontalsystems.bankwallet.modules.amount.AmountInputType
import io.horizontalsystems.bankwallet.modules.availablebalance.AvailableBalanceViewModel
import io.horizontalsystems.bankwallet.modules.balance.*
import io.horizontalsystems.bankwallet.modules.manageaccount.ManageAccountModule
import io.horizontalsystems.bankwallet.modules.manageaccount.ManageAccountScreen
import io.horizontalsystems.bankwallet.modules.manageaccounts.ManageAccountsModule
import io.horizontalsystems.bankwallet.modules.rateapp.RateAppModule
import io.horizontalsystems.bankwallet.modules.rateapp.RateAppViewModel
import io.horizontalsystems.bankwallet.modules.wallet.ui.walletCard
import io.horizontalsystems.bankwallet.ui.compose.ComposeAppTheme
import io.horizontalsystems.bankwallet.ui.compose.HSSwipeRefresh
import io.horizontalsystems.bankwallet.ui.compose.components.*
import io.horizontalsystems.core.helpers.HudHelper
import java.math.BigDecimal


@Composable
fun NoteWarning(
    modifier: Modifier = Modifier,
    text: String,
    onClick: (() -> Unit),
    onClose: (() -> Unit)?
) {
    Note(
        modifier = modifier.clickable(onClick = onClick),
        text = text,
        title = stringResource(id = R.string.AccountRecovery_Note),
        icon = R.drawable.ic_attention_20,
        borderColor = ComposeAppTheme.colors.jacob,
        backgroundColor = ComposeAppTheme.colors.yellow20,
        textColor = ComposeAppTheme.colors.jacob,
        iconColor = ComposeAppTheme.colors.jacob,
        onClose = onClose
    )
}

@Composable
fun NoteError(
    modifier: Modifier = Modifier,
    text: String,
    onClick: (() -> Unit)
) {
    Note(
        modifier = modifier.clickable(onClick = onClick),
        text = text,
        title = stringResource(id = R.string.AccountRecovery_Note),
        icon = R.drawable.ic_attention_20,
        borderColor = ComposeAppTheme.colors.lucian,
        backgroundColor = ComposeAppTheme.colors.red20,
        textColor = ComposeAppTheme.colors.lucian,
        iconColor = ComposeAppTheme.colors.lucian
    )
}

@Composable
fun Note(
    modifier: Modifier = Modifier,
    text: String,
    title: String,
    @DrawableRes icon: Int,
    iconColor: Color,
    borderColor: Color,
    backgroundColor: Color,
    textColor: Color,
    onClose: (() -> Unit)? = null
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .border(1.dp, borderColor, RoundedCornerShape(12.dp))
            .background(backgroundColor)
            .padding(horizontal = 16.dp, vertical = 12.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Row(
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {

            Icon(
                painter = painterResource(id = icon),
                contentDescription = null,
                tint = iconColor
            )
            Text(
                modifier = Modifier.weight(1f),
                text = title,
                color = textColor,
                style = ComposeAppTheme.typography.subhead1,


            )
            onClose?.let {
                HsIconButton(
                    modifier = Modifier.size(20.dp),
                    onClick = onClose
                ) {
                    Icon(
                        painter = painterResource(id = R.drawable.ic_close),
                        tint = iconColor,
                        contentDescription = null,
                    )
                }
            }
        }
        if (text.isNotEmpty()) {
            subhead2_leah(text = text)
        }
    }
}



@Composable
fun WalletBalanceItem(
    balanceViewItems: List<BalanceViewItem>,
    viewModel: newBalanceViewModel,
    accountViewItem: AccountViewItem,
    navController: NavController,
    uiState: BalanceUiState,
    totalState: TotalUIState,
    expandedState : Boolean


) {

    val percent = remember { mutableStateOf(0) }
    val colors = listOf(
        Color(237, 110, 0, 255), // Arancione
        Color(31, 27, 222, 255), // Blu
        Color(0, 102, 255, 255), // Ciano
        Color(27, 210, 222, 255), // Ciano
        Color(255, 255, 255, 255), // Azurro/Grigio

    )

    val sortType = viewModel.sortTypes
    val accountId = accountViewItem.id


    // Use rememberSaveable to save the state of secondaryAmount for the given accountId
    var secondaryAmount: String? by rememberSaveable(accountId) { mutableStateOf(null) }


    val rateAppViewModel = viewModel<RateAppViewModel>(factory = RateAppModule.Factory())
    DisposableEffect(true) {
        rateAppViewModel.onBalancePageActive()
        onDispose {
            rateAppViewModel.onBalancePageInactive()
        }
    }

    val itemColorFigma = Color(red = 31, green = 34, blue = 42)
    val context = LocalContext.current

    Crossfade(uiState.viewState) { viewState ->
        when (viewState) {
            ViewState.Success -> {

                Box(

                    modifier = Modifier
                        .height(if (expandedState == true) 300.dp else 135.dp)
                        .clip(RoundedCornerShape(15.dp))
                        .fillMaxWidth()
                        .background(ComposeAppTheme.colors.lawrence)
                        .padding(top = 0.dp, start = 6.dp, end = 6.dp)
                        .clickable {
                            navController.navigate(
                                R.id.walletFragment,

                                )
                        }

                ) {

                    Column(
                        modifier = Modifier
                            .align(Alignment.Center)
                            .height(300.dp)
                    ) {
                        Row(
                            modifier = Modifier
                                .height(50.dp)
                                .padding(all = 1.dp)


                        ) {

                            Spacer(Modifier.weight(1f))

                            IconButton(
                                onClick = {
                                    navController.navigate(
                                        R.id.manageAccountFragment,
                                        ManageAccountModule.prepareParams(accountViewItem.id)
                                    )
                                },

                                ) {
                                Image(
                                    painter = painterResource(id = io.horizontalsystems.bankwallet.R.drawable.more_dots),
                                    contentDescription = null
                                )
                            }
                        }
                        Spacer(modifier = Modifier.height(20.dp))
                        Box(
                            modifier = Modifier
                                .height(170.dp)
                                .offset(y = -40.dp)
                                .background(Color.Transparent)
                                .padding(all = 50.dp)
                        )
                        {
                            when (totalState) {
                                TotalUIState.Hidden -> {

                                    secondaryAmount = "*****"

                                }
                                is TotalUIState.Visible -> {
                                    secondaryAmount = totalState.secondaryAmountStr

                                }
                            }

                            FullCircleChart(
                                modifier = Modifier.scale(0.8f),
                                percentValues = viewModel.pieChartData,
                                viewModel = viewModel,
                                title = accountViewItem.name,
                                balance = secondaryAmount.toString()

                            )

                        }


                        var revealedCardId by remember { mutableStateOf<Int?>(null) }

                        val listState = rememberSaveable(
                            accountId,
                            sortType,
                            saver = LazyListState.Saver
                        ) {
                            LazyListState()
                        }

                        BoxWithConstraints(
                            modifier = Modifier
                                .weight(1f)
                                .padding(end = 8.dp)
                        ) {

                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(start = 1.dp, end = 3.dp),
                                horizontalArrangement = Arrangement.SpaceAround
                            ) {

                                BoxWithConstraints(
                                    modifier = Modifier
                                        .weight(1f)
                                        .padding(end = 8.dp)
                                ) {

                                    Row(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .padding(start = 1.dp, end = 3.dp),
                                        horizontalArrangement = Arrangement.SpaceAround
                                    ) {


                                        val topFourItems = balanceViewItems
                                            .sortedWith(compareByDescending { it.secondaryValue.value })
                                            .take(4)


                                        // create balance card composables with weight
                                        topFourItems.forEachIndexed { index, item ->
                                            val color = colors[index % colors.size]
                                            WalletBalanceCard(
                                                modifier = Modifier
                                                    .padding(end = 5.dp),
                                                viewItem = item,
                                                viewModel = viewModel,
                                                accountViewItem = accountViewItem,
                                                uiState = uiState,
                                                totalState = totalState,
                                                navController = navController,
                                                color = color,

                                                )

                                        }


                                        NonFunctionalBalanceCard(
                                            modifier = Modifier,
                                            viewModel = viewModel,
                                            color = Color(0xFF7283BE),
                                            CoinCode = "Other",
                                            "100%"


                                        )


                                    }
                                }
                            }
                        }
                    }
                }
            }

            else -> {
                ViewState.Loading
            }
        }
    }
}



@Composable
fun BalanceItems(
    balanceViewItems: List<BalanceViewItem>,
    viewModel: newBalanceViewModel,
    accountViewItem: BalanceModule.BalanceAccountViewItem,
    navController: NavController,
    uiState: BalanceUiState,
    totalState: TotalUIState,
    expandedState : Boolean


) {

    val percent = remember { mutableStateOf(0) }
    val colors = listOf(
        Color(237, 110, 0, 255), // Arancione
        Color(31, 27, 222, 255), // Blu
        Color(0, 102, 255, 255), // Ciano
        Color(27, 210, 222, 255), // Ciano
       // Color(255, 255, 255, 255), // Azurro/Grigio

    )

    val sortType = viewModel.sortTypes
    val accountId = accountViewItem.accountId


    // Use rememberSaveable to save the state of secondaryAmount for the given accountId
    var secondaryAmount: String? by rememberSaveable(accountId) { mutableStateOf(null) }


    val rateAppViewModel = viewModel<RateAppViewModel>(factory = RateAppModule.Factory())
    DisposableEffect(true) {
        rateAppViewModel.onBalancePageActive()
        onDispose {
            rateAppViewModel.onBalancePageInactive()
        }
    }
    val orangeKurama =  Color(237, 110, 0, 255)// Arancione
    val itemColorFigma = Color(red = 31, green = 34, blue = 42)
    val context = LocalContext.current

    Spacer(modifier = Modifier.height(11.dp))

    Crossfade(uiState.viewState) { viewState ->
        when (viewState) {
            ViewState.Success -> {

                Box(
                    modifier = Modifier
                        .height(if (expandedState == true) 300.dp else 135.dp)
                        .clip(RoundedCornerShape(15.dp))
                        .fillMaxWidth()
                        .background(ComposeAppTheme.colors.lawrence)
                        .padding(top = 0.dp, start = 6.dp, end = 6.dp)
                        .clickable(
                            onClick = {
                                if (expandedState == true) {
                                    navController.navigate(
                                        R.id.walletFragment,
                                    )
                                }
                            },
                        )
                ) {

                    Column(
                        modifier = Modifier
                            .align(Alignment.Center)
                            .height(300.dp)
                    ) {
                        if (expandedState == false) {
                            Log.e("L'id è: ", accountId)
                            Column {

                                Row(
                                    modifier = Modifier
                                        .height(50.dp)
                                        .padding(all = 1.dp)


                                ) {
                                    IconButton(
                                        onClick = {
                                            viewModel.onSelect(accountViewItem)
                                            Log.d("L'ID DELL WALLET è", accountId)

                                        },
                                        modifier = Modifier.rotate(if (expandedState == false) 0f else 180f)

                                    ) {
                                        Image(
                                            painter = painterResource(id = R.drawable.expand_wallet),
                                            contentDescription = null
                                        )
                                    }

                                    Spacer(Modifier.weight(1f))

                                    IconButton(
                                        onClick = {
                                            navController.navigate(
                                                R.id.manageAccountFragment,
                                                ManageAccountModule.prepareParams(accountViewItem.accountId)
                                            )
                                        },
                                    ) {
                                        Image(
                                            painter = painterResource(id = R.drawable.more_dots),
                                            contentDescription = null
                                        )
                                    }


                                }
                                Column(
                                    modifier = Modifier
                                        .align(Alignment.CenterHorizontally)
                                        .offset(y = -30.dp)
                                ) {


                                    Text(
                                        text = accountViewItem.title,
                                        color = ComposeAppTheme.colors.orangeK,
                                        style = TextStyle(
                                            fontSize = 25.sp,
                                            fontWeight = FontWeight.W400
                                        ),
                                        modifier = Modifier
                                            .align(Alignment.CenterHorizontally)
                                            .padding(bottom = 10.dp)
                                    )

                                    Text(
                                        text = "",
                                        color = ComposeAppTheme.colors.text,
                                        style = TextStyle(
                                            fontSize = 25.sp,
                                            fontWeight = FontWeight.W400
                                        ),
                                        modifier = Modifier.align(Alignment.CenterHorizontally)
                                    )

                                    IconButton(
                                        onClick = {
                                            viewModel.toggleBalanceVisibility()
                                            HudHelper.vibrate(context)

                                        },
                                        modifier = Modifier
                                            .align(Alignment.CenterHorizontally)
                                            .size(50.dp)
                                            .padding(top = 10.dp),

                                        ) {
                                        Image(
                                            painter = painterResource(id = R.drawable.new_hide),
                                            contentDescription = null
                                        )
                                    }


                                }
                            }
                        } else {

                            Row(
                                modifier = Modifier
                                    .height(50.dp)
                                    .padding(all = 1.dp)


                            ) {
                                IconButton(
                                    onClick = {
                                        expandedState == false
                                    },
                                    modifier = Modifier.rotate(if (expandedState == true) 180f else 0f)

                                ) {
                                    Image(
                                        painter = painterResource(id = io.horizontalsystems.bankwallet.R.drawable.expand_wallet),
                                        contentDescription = null
                                    )
                                }

                                Spacer(Modifier.weight(1f))

                                IconButton(
                                    onClick = {
                                        navController.navigate(
                                            R.id.manageAccountFragment,
                                            ManageAccountModule.prepareParams(accountViewItem.accountId)
                                        )
                                    },

                                    ) {
                                    Image(
                                        painter = painterResource(id = io.horizontalsystems.bankwallet.R.drawable.more_dots),
                                        contentDescription = null
                                    )
                                }
                            }
                            Spacer(modifier = Modifier.height(20.dp))
                            Box(
                                modifier = Modifier
                                    .height(170.dp)
                                    .offset(y = -40.dp)
                                    .background(Color.Transparent)
                                    .padding(all = 50.dp)
                            )
                            {
                                when (totalState) {
                                    TotalUIState.Hidden -> {

                                        secondaryAmount = "*****"

                                    }
                                    is TotalUIState.Visible -> {
                                        secondaryAmount = totalState.secondaryAmountStr

                                    }
                                }

                                FullCircleChart(
                                    modifier = Modifier.scale(0.8f),
                                    percentValues = viewModel.pieChartData,
                                    viewModel = viewModel,
                                    title = accountViewItem.title,
                                    balance = secondaryAmount.toString()

                                )

                            }


                            var revealedCardId by remember { mutableStateOf<Int?>(null) }

                            val listState = rememberSaveable(
                                accountId,
                                sortType,
                                saver = LazyListState.Saver
                            ) {
                                LazyListState()
                            }
                            BoxWithConstraints(
                                modifier = Modifier
                                    .weight(1f)
                                    .padding(end = 8.dp)
                            ) {

                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(start = 1.dp, end = 3.dp),
                                    horizontalArrangement = Arrangement.SpaceAround
                                ) {

                                    BoxWithConstraints(
                                        modifier = Modifier
                                            .weight(1f)
                                            .padding(end = 8.dp)
                                    ) {

                                        Row(
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .padding(start = 1.dp, end = 3.dp),
                                            horizontalArrangement = Arrangement.SpaceAround
                                        ) {


                                            val topFourItems = balanceViewItems
                                                .sortedWith(compareByDescending { it.secondaryValue.value })
                                                .take(4)


                                            // create balance card composables with weight
                                            topFourItems.forEachIndexed { index, item ->
                                                val color = colors[index % colors.size]
                                                BalanceCard(
                                                    modifier = Modifier
                                                        .padding(end = 5.dp),
                                                    viewItem = item,
                                                    viewModel = viewModel,
                                                    accountViewItem = accountViewItem,
                                                    uiState = uiState,
                                                    totalState = totalState,
                                                    navController = navController,
                                                    color = color,

                                                    )

                                            }


                                            NonFunctionalBalanceCard(
                                                modifier = Modifier,
                                                viewModel = viewModel,
                                                color = Color(0xFF7283BE),
                                                CoinCode = "Other",
                                                "100%"


                                            )


                                        }
                                    }


                                }
                            }
                        }
                    }
                }




            }
            else -> {
                ViewState.Loading
            }
        }
    }
}





/*
@Composable
fun Wallets(
    balanceViewItems: List<BalanceViewItem>,
    viewModel: BalanceViewModel,
    navController: NavController,
    accountId: String,
    sortType: BalanceSortType,
    uiState: BalanceUiState
) {
    var revealedCardId by remember { mutableStateOf<Int?>(null) }

    val listState = rememberSaveable(
        accountId,
        sortType,
        saver = LazyListState.Saver
    ) {
        LazyListState()
    }

    HSSwipeRefresh(
        state = rememberSwipeRefreshState(uiState.isRefreshing),
        onRefresh = {
            viewModel.onRefresh()
        }
    ) {
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            state = listState,
            contentPadding = PaddingValues(top = 8.dp, bottom = 18.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(balanceViewItems, key = { item -> item.wallet.hashCode() }) { item ->
                if (item.isWatchAccount) {
                    BalanceCard(item, viewModel, navController)
                } else {
                    BalanceCardSwipable(
                        viewItem = item,
                        viewModel = viewModel,
                        navController = navController,
                        revealed = revealedCardId == item.wallet.hashCode(),
                        onReveal = { walletHashCode ->
                            if (revealedCardId != walletHashCode) {
                                revealedCardId = walletHashCode
                            }
                        },
                        onConceal = {
                            revealedCardId = null
                        },
                    )
                }
            }
        }
    }
}*/