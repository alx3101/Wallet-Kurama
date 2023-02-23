package io.horizontalsystems.bankwallet.modules.balance.ui

import android.os.Bundle
import android.util.Log
import androidx.annotation.DrawableRes
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
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.google.accompanist.swiperefresh.rememberSwipeRefreshState
import io.horizontalsystems.bankwallet.R
import io.horizontalsystems.bankwallet.core.slideFromBottom
import io.horizontalsystems.bankwallet.core.slideFromRight
import io.horizontalsystems.bankwallet.entities.CurrencyValue
import io.horizontalsystems.bankwallet.modules.amount.AmountInputType
import io.horizontalsystems.bankwallet.modules.availablebalance.AvailableBalanceViewModel
import io.horizontalsystems.bankwallet.modules.balance.*
import io.horizontalsystems.bankwallet.modules.manageaccounts.ManageAccountsModule
import io.horizontalsystems.bankwallet.modules.rateapp.RateAppModule
import io.horizontalsystems.bankwallet.modules.rateapp.RateAppViewModel
import io.horizontalsystems.bankwallet.ui.compose.ComposeAppTheme
import io.horizontalsystems.bankwallet.ui.compose.HSSwipeRefresh
import io.horizontalsystems.bankwallet.ui.compose.components.*


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
                style = ComposeAppTheme.typography.subhead1
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

    Spacer(modifier = Modifier.height(10.dp))

    Box(

        modifier = Modifier
            .height(450.dp)
            .clip(RoundedCornerShape(10.dp))
            .fillMaxWidth()
            .background(itemColorFigma)
            .padding(top = 5.dp, start = 15.dp, end = 15.dp)

    ) {

        Column(
            modifier = Modifier
                .align(Alignment.Center),
        ){

                Row(
                    modifier = Modifier
                        .height(50.dp)
                        .padding(all = 1.dp)


                ) {

                    Spacer(Modifier.weight(1f))

                    IconButton(
                        onClick = {
                            navController.slideFromBottom(
                                R.id.manageAccountsFragment,
                                ManageAccountsModule.prepareParams(ManageAccountsModule.Mode.Switcher)
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
                        .height(300.dp)
                        .offset(y = -40.dp)
                        .background(Color.Transparent)
                        .padding(all = 20.dp)
                )
                {
                    when (totalState) {
                        TotalUIState.Hidden -> {

                            FullCircleChart(
                                modifier = Modifier .scale(0.9f),
                                percentValues = viewModel.pieChartData,
                                viewModel = viewModel,
                                title = accountViewItem.name,
                                balance = "*****"
                            )

                        }
                        is TotalUIState.Visible -> {

                            FullCircleChart(
                                modifier = Modifier .scale(0.9f),
                                percentValues = viewModel.pieChartData,
                                viewModel = viewModel,
                                title = accountViewItem.name,
                                balance = totalState.secondaryAmountStr

                            )

                            secondaryAmount = totalState.secondaryAmountStr
                            viewModel.numberStringed = secondaryAmount.toString()
                        }
                    }
                }


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
                    LazyRow(
                        modifier = Modifier.fillMaxSize(),
                        state = listState,

                        ) {
                        items(balanceViewItems, key = { item -> item.wallet.hashCode() }) { item ->

                            WalletBalanceCard(item,viewModel,accountViewItem,uiState,totalState,navController)


                        }
                    }
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


    val sortType = viewModel.sortTypes
    val accountId = accountViewItem.accountId
    viewModel.addIdToIdsList(accountId)

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

    Spacer(modifier = Modifier.height(10.dp))

    Box(

        modifier = Modifier
            .height(if (expandedState == true) 450.dp else 200.dp)
            .clip(RoundedCornerShape(10.dp))
            .width(350.dp)
            .background(itemColorFigma)
            .padding(top = 5.dp, start = 15.dp, end = 15.dp)
            .clickable {
                navController.navigate(
                    R.id.walletFragment,

                    )
            }

    ) {

        Column(
            modifier = Modifier
                .align(Alignment.Center)
                .height(600.dp)
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


                    }
                    Column(modifier = Modifier.align(Alignment.CenterHorizontally)) {


                        Text(
                            text = accountViewItem.title,
                            style = TextStyle(color = Color.White)
                        )

                        Text(
                            text = secondaryAmount.toString(),
                            style = TextStyle(color = Color.Green)
                        )

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
                            navController.slideFromBottom(
                                R.id.manageAccountsFragment,
                                ManageAccountsModule.prepareParams(ManageAccountsModule.Mode.Switcher)
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
                        .height(300.dp)
                        .offset(y = -40.dp)
                        .background(Color.Transparent)
                        .padding(all = 20.dp)
                )
                {
                    when (totalState) {
                        TotalUIState.Hidden -> {

                            FullCircleChart(
                                modifier = Modifier.scale(0.7f),
                                percentValues = viewModel.pieChartData,
                                viewModel = viewModel,
                                title = accountViewItem.title,
                                balance = "****"

                            )


                        }
                        is TotalUIState.Visible -> {

                            FullCircleChart(
                                modifier = Modifier.scale(0.7f),
                                percentValues = viewModel.pieChartData,
                                viewModel = viewModel,
                                title = accountViewItem.title,
                                balance = totalState.secondaryAmountStr

                            )

                            secondaryAmount = totalState.secondaryAmountStr
                            viewModel.numberStringed = secondaryAmount.toString()
                        }
                    }
                }


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
                    LazyRow(
                        modifier = Modifier.fillMaxSize(),
                        state = listState,

                        ) {
                        items(balanceViewItems, key = { item -> item.wallet.hashCode() }) { item ->

                            BalanceCard(
                                item,
                                viewModel,
                                accountViewItem,
                                uiState,
                                totalState,
                                navController
                            )


                        }
                    }
                }
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