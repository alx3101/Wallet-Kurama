package io.horizontalsystems.bankwallet.modules.balance.ui

import android.util.Log
import android.view.View
import androidx.compose.animation.*
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.Divider
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.os.bundleOf
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import io.horizontalsystems.bankwallet.R
import io.horizontalsystems.bankwallet.core.slideFromBottom
import io.horizontalsystems.bankwallet.core.slideFromRight
import io.horizontalsystems.bankwallet.entities.CurrencyValue
import io.horizontalsystems.bankwallet.modules.amount.AmountInputType
import io.horizontalsystems.bankwallet.modules.availablebalance.AvailableBalanceModule
import io.horizontalsystems.bankwallet.modules.availablebalance.AvailableBalanceViewModel
import io.horizontalsystems.bankwallet.modules.balance.*
import io.horizontalsystems.bankwallet.modules.coin.CoinFragment
import io.horizontalsystems.bankwallet.modules.manageaccount.dialogs.BackupRequiredDialog
import io.horizontalsystems.bankwallet.modules.receive.ReceiveFragment
import io.horizontalsystems.bankwallet.modules.send.SendFragment
import io.horizontalsystems.bankwallet.modules.send.solana.SendSolanaViewModel
import io.horizontalsystems.bankwallet.modules.swap.SwapMainModule
import io.horizontalsystems.bankwallet.modules.syncerror.SyncErrorDialog
import io.horizontalsystems.bankwallet.modules.walletconnect.list.ui.DraggableCardSimple
import io.horizontalsystems.bankwallet.ui.compose.ComposeAppTheme
import io.horizontalsystems.bankwallet.ui.compose.components.*
import io.horizontalsystems.bankwallet.ui.extensions.RotatingCircleProgressView
import io.horizontalsystems.core.helpers.HudHelper
import java.lang.Math.round
import java.math.BigDecimal

@Composable
fun BalanceCard(

    viewItem: BalanceViewItem,
    viewModel: newBalanceViewModel,
    accountViewItem:  BalanceModule.BalanceAccountViewItem,
    uiState: BalanceUiState,
    totalState: TotalUIState,
    navController: NavController,

    ) {

    val percentage = viewModel.calculatePercentage(viewItem, totalState)
    if (percentage > 0 ) {
    Box(modifier = Modifier) {

        when (totalState) {
            TotalUIState.Hidden -> {

            }
            is TotalUIState.Visible -> {

                Row(modifier = Modifier) {

                    Column(
                        modifier = Modifier
                            .align(Alignment.CenterVertically)
                            .padding(10.dp)
                            .height(40.dp)
                    ) {

                        val context = LocalContext.current

                        Text(text = viewItem.coinCode,
                            style = TextStyle(color = Color.White))
                        Text(text = "${viewModel.calculatePercentage(viewItem,totalState)}%",
                            style = TextStyle(color = Color.White))

                        viewModel.calculateAndAddPercentage(viewItem, totalState)


                    }
                }
            }
        }
    }
} else if (percentage <5 ){

    Log.d("test","ci sono altri dati")
}

}
@Composable
fun WalletBalanceCard(

    viewItem: BalanceViewItem,
    viewModel: newBalanceViewModel,
    accountViewItem:  AccountViewItem,
    uiState: BalanceUiState,
    totalState: TotalUIState,
    navController: NavController,

    ) {

    val percentage = viewModel.calculatePercentage(viewItem, totalState)
    if (percentage > 0 ) {
        Box(modifier = Modifier) {

            when (totalState) {
                TotalUIState.Hidden -> {

                }
                is TotalUIState.Visible -> {

                    Row(modifier = Modifier) {

                        Column(
                            modifier = Modifier
                                .align(Alignment.CenterVertically)
                                .padding(10.dp)
                                .height(40.dp)
                        ) {

                            val context = LocalContext.current

                            Text(text = viewItem.coinCode,
                                style = TextStyle(color = Color.White))
                            Text(text = "${viewModel.calculatePercentage(viewItem,totalState)}%",
                                style = TextStyle(color = Color.White))

                            viewModel.calculateAndAddPercentage(viewItem, totalState)


                        }
                    }
                }
            }
        }
    } else if (percentage <5 ){

        Log.d("test","ci sono altri dati")
    }

}


@Composable
private fun ExpandableContent(
    viewItem: BalanceViewItem,
    navController: NavController,
    viewModel: BalanceViewModel
) {
    AnimatedVisibility(
        visible = viewItem.expanded

    ) {
        Column {
            LockedValueRow(viewItem)
            Divider(
                modifier = Modifier.padding(start = 12.dp, end = 12.dp, top = 5.dp, bottom = 6.dp),
                thickness = 1.dp,
                color = ComposeAppTheme.colors.steel10
            )
            ButtonsRow(viewItem, navController, viewModel)
        }
    }
}

@Composable
private fun ButtonsRow(viewItem: BalanceViewItem, navController: NavController, viewModel: BalanceViewModel) {
    val onClickReceive = {
        try {
            navController.slideFromBottom(
                R.id.receiveFragment,
                bundleOf(ReceiveFragment.WALLET_KEY to viewModel.getWalletForReceive(viewItem))
            )
        } catch (e: BackupRequiredError) {
            navController.slideFromBottom(
                R.id.backupRequiredDialog,
                BackupRequiredDialog.prepareParams(e.account, e.coinTitle)
            )
        }
    }

    Row(
        modifier = Modifier.padding(start = 16.dp, top = 4.dp, end = 16.dp, bottom = 12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        if (viewItem.isWatchAccount) {
            ButtonPrimaryDefault(
                modifier = Modifier.weight(1f),
                title = stringResource(R.string.Balance_Address),
                onClick = onClickReceive,
            )
        } else {
            ButtonPrimaryYellow(
                modifier = Modifier.weight(1f),
                title = stringResource(R.string.Balance_Send),
                onClick = {
                    navController.slideFromBottom(
                        R.id.sendXFragment,
                        SendFragment.prepareParams(viewItem.wallet)
                    )
                },
                enabled = viewItem.sendEnabled
            )
            Spacer(modifier = Modifier.width(8.dp))
            if (viewItem.swapVisible) {
                ButtonPrimaryCircle(
                    icon = R.drawable.ic_arrow_down_left_24,
                    onClick = onClickReceive,
                )
                Spacer(modifier = Modifier.width(8.dp))
                ButtonPrimaryCircle(
                    icon = R.drawable.ic_swap_24,
                    onClick = {
                        navController.slideFromBottom(
                            R.id.swapFragment,
                            SwapMainModule.prepareParams(viewItem.wallet.token)
                        )
                    },
                    enabled = viewItem.swapEnabled
                )
            } else {
                ButtonPrimaryDefault(
                    modifier = Modifier.weight(1f),
                    title = stringResource(R.string.Balance_Receive),
                    onClick = onClickReceive,
                )
            }
        }
        Spacer(modifier = Modifier.width(8.dp))
        ButtonPrimaryCircle(
            icon = R.drawable.ic_chart_24,
            onClick = {
                val coinUid = viewItem.wallet.coin.uid
                val arguments = CoinFragment.prepareParams(coinUid)

                navController.slideFromRight(R.id.coinFragment, arguments)
            },
        )
    }
}

@Composable
private fun LockedValueRow(viewItem: BalanceViewItem) {
    AnimatedVisibility(
        visible = viewItem.coinValueLocked.visible,
        enter = expandVertically() + fadeIn(),
        exit = shrinkVertically() + fadeOut()
    ) {
        Column {
            Divider(
                modifier = Modifier.padding(start = 12.dp, end = 12.dp, top = 5.dp, bottom = 6.dp),
                thickness = 1.dp,
                color = ComposeAppTheme.colors.steel10
            )
            Row(
                modifier = Modifier
                    .height(25.dp)
                    .padding(horizontal = 16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Image(
                    painter = painterResource(R.drawable.ic_lock_16),
                    contentDescription = "lock icon"
                )
                Text(
                    modifier = Modifier.padding(start = 6.dp),
                    text = viewItem.coinValueLocked.value,
                    color = if (viewItem.coinValueLocked.dimmed) ComposeAppTheme.colors.grey50 else ComposeAppTheme.colors.grey,
                    style = ComposeAppTheme.typography.subhead2,
                    maxLines = 1,
                )
                Spacer(modifier = Modifier.weight(1f))
                Text(
                    text = viewItem.fiatValueLocked.value,
                    color = if (viewItem.fiatValueLocked.dimmed) ComposeAppTheme.colors.yellow50 else ComposeAppTheme.colors.jacob,
                    style = ComposeAppTheme.typography.subhead2,
                    maxLines = 1,
                )
            }
        }
    }
}

@Composable
private fun WalletIcon(viewItem: BalanceViewItem, viewModel: BalanceViewModel, navController: NavController) {
    Box(
        modifier = Modifier
            .width(15.dp)
            .height(15.dp),
        contentAlignment = Alignment.BottomCenter
    ) {
        if (!viewItem.mainNet) {
            Image(
                modifier = Modifier.align(Alignment.BottomCenter),
                painter = painterResource(R.drawable.testnet),
                contentDescription = "Testnet"
            )
        }
        viewItem.syncingProgress.progress?.let { progress ->
            AndroidView(
                modifier = Modifier
                    .size(52.dp),
                factory = { context ->
                    RotatingCircleProgressView(context)
                },
                update = { view ->
                    val color = when (viewItem.syncingProgress.dimmed) {
                        true -> R.color.grey_50
                        false -> R.color.grey
                    }
                    view.setProgressColored(progress, view.context.getColor(color))
                }
            )
        }
        if (viewItem.failedIconVisible) {
            val view = LocalView.current
            Image(
                modifier = Modifier
                    .size(32.dp)
                    .clickable {
                        onSyncErrorClicked(viewItem, viewModel, navController, view)
                    },
                painter = painterResource(id = R.drawable.ic_attention_24),
                contentDescription = "coin icon",
                colorFilter = ColorFilter.tint(ComposeAppTheme.colors.lucian)
            )
        } else {
            CoinImage(
                iconUrl = viewItem.coinIconUrl,
                placeholder = viewItem.coinIconPlaceholder,
                modifier = Modifier
                    .size(32.dp)
            )
        }
    }
}

@Composable
private fun WalletCircle(viewItem: BalanceViewItem, viewModel: newBalanceViewModel, navController: NavController) {

}

private fun onSyncErrorClicked(viewItem: BalanceViewItem, viewModel: BalanceViewModel, navController: NavController, view: View) {
    when (val syncErrorDetails = viewModel.getSyncErrorDetails(viewItem)) {
        is BalanceViewModel.SyncError.Dialog -> {
            val wallet = syncErrorDetails.wallet
            val errorMessage = syncErrorDetails.errorMessage

            navController.slideFromBottom(
                R.id.syncErrorDialog,
                SyncErrorDialog.prepareParams(wallet, errorMessage)
            )
        }
        is BalanceViewModel.SyncError.NetworkNotAvailable -> {
            HudHelper.showErrorMessage(view, R.string.Hud_Text_NoInternet)
        }
    }
}


