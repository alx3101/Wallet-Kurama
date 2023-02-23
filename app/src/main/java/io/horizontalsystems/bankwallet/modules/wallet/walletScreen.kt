package io.horizontalsystems.bankwallet.modules.wallet

import androidx.compose.runtime.Composable
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import io.horizontalsystems.bankwallet.modules.availablebalance.AvailableBalanceModule
import io.horizontalsystems.bankwallet.modules.availablebalance.AvailableBalanceViewModel
import io.horizontalsystems.bankwallet.modules.balance.BalanceModule
import io.horizontalsystems.bankwallet.modules.balance.BalanceScreenState
import io.horizontalsystems.bankwallet.modules.balance.BalanceViewModel
import io.horizontalsystems.bankwallet.modules.balance.newBalanceViewModel
import io.horizontalsystems.bankwallet.modules.balance.ui.BalanceForAccount
import io.horizontalsystems.bankwallet.modules.balance.ui.BalanceNoAccount
import io.horizontalsystems.bankwallet.modules.wallet.ui.walletAccountScreen
import io.horizontalsystems.bankwallet.modules.wallet.walletFragment
import io.horizontalsystems.bankwallet.ui.compose.ComposeAppTheme


@Composable

fun walletScreen(navController: NavController) {
    ComposeAppTheme {
        val viewModel = viewModel<newBalanceViewModel>(factory = BalanceModule.Factory())

        when (val tmpAccount = viewModel.balanceScreenState) {
            BalanceScreenState.NoAccount -> BalanceNoAccount(navController)
            is BalanceScreenState.HasAccount -> walletAccountScreen(navController,tmpAccount.accountViewItem)
            else -> {}
        }
    }
}