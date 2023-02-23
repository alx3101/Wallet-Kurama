package io.horizontalsystems.bankwallet.modules.balance.ui

import androidx.compose.runtime.Composable
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import io.horizontalsystems.bankwallet.modules.availablebalance.AvailableBalanceModule
import io.horizontalsystems.bankwallet.modules.availablebalance.AvailableBalanceViewModel
import io.horizontalsystems.bankwallet.modules.balance.*
import io.horizontalsystems.bankwallet.modules.manageaccounts.ManageAccountsModule
import io.horizontalsystems.bankwallet.ui.compose.ComposeAppTheme

@Composable


fun BalanceScreen(navController: NavController ) {
    ComposeAppTheme {
        BalanceForAccount(
            navController = navController
        )
    }
}