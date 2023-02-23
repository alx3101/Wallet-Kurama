package io.horizontalsystems.bankwallet.modules.balance

import android.app.Application
import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import io.horizontalsystems.bankwallet.core.AdapterState
import io.horizontalsystems.bankwallet.core.App
import io.horizontalsystems.bankwallet.core.BalanceData
import io.horizontalsystems.bankwallet.core.managers.FaqManager
import io.horizontalsystems.bankwallet.entities.Wallet
import io.horizontalsystems.marketkit.models.CoinPrice

object BalanceModule {

    class AccountsFactory : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            val balanceService = BalanceService(
                BalanceActiveWalletRepository(App.walletManager, App.evmSyncSourceManager),
                BalanceXRateRepository(App.currencyManager, App.marketKit),
                BalanceAdapterRepository(App.adapterManager, BalanceCache(App.appDatabase.enabledWalletsCacheDao())),
                App.localStorage,
                App.connectivityManager,
                BalanceSorter(),
                App.accountManager,
                BalanceAllWalletsRepository(App.walletManager,App.evmSyncSourceManager)
            )

            val totalService = TotalService(
                App.currencyManager,
                App.marketKit,
                App.baseTokenManager,
                App.balanceHiddenManager
            )
            return newBalanceViewModel(
                App.accountManager,
                balanceService,
                BalanceViewItemFactory(),
                App.balanceViewTypeManager,
                TotalBalance(totalService, App.balanceViewTypeManager, App.balanceHiddenManager),
                App.localStorage,
                App.languageManager,
                FaqManager,


                ) as T
        }
    }

    class Factory : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            val balanceService = BalanceService(
                BalanceActiveWalletRepository(App.walletManager, App.evmSyncSourceManager),
                BalanceXRateRepository(App.currencyManager, App.marketKit),
                BalanceAdapterRepository(App.adapterManager, BalanceCache(App.appDatabase.enabledWalletsCacheDao())),
                App.localStorage,
                App.connectivityManager,
                BalanceSorter(),
                App.accountManager,
                BalanceAllWalletsRepository(App.walletManager,App.evmSyncSourceManager)
            )

            val totalService = TotalService(
                App.currencyManager,
                App.marketKit,
                App.baseTokenManager,
                App.balanceHiddenManager
            )
            return newBalanceViewModel(
                App.accountManager,
                balanceService,
                BalanceViewItemFactory(),
                App.balanceViewTypeManager,
                TotalBalance(totalService, App.balanceViewTypeManager, App.balanceHiddenManager),
                App.localStorage,
                App.languageManager,
                FaqManager,


                ) as T
        }
    }


    data class BalanceAccountViewItem(
        val accountId: String,
        val title: String,
        val subtitle: String,
        var selected: Boolean,
        val backupRequired: Boolean,
        val isWatchAccount: Boolean,
        val migrationRequired: Boolean,
        val migrationRecommended: Boolean,
    )

    data class BalanceActionViewItem(
        @DrawableRes val icon: Int,
        @StringRes val title: Int,
        val callback: () -> Unit
    )

    data class BalanceItem(
        val wallet: Wallet,
        val mainNet: Boolean,
        val balanceData: BalanceData,
        val state: AdapterState,
        val coinPrice: CoinPrice? = null
    ) {
        val fiatValue get() = coinPrice?.value?.let { balanceData.available.times(it) }
        val balanceFiatTotal get() = coinPrice?.value?.let { balanceData.total.times(it) }
    }
}