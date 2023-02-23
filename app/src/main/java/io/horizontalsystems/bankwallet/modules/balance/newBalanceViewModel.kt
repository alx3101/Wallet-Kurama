package io.horizontalsystems.bankwallet.modules.balance

import android.accounts.AccountManager
import android.app.Application
import android.content.Context
import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.graphics.Color
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import cash.z.ecc.android.sdk.ext.collectWith
import io.horizontalsystems.bankwallet.core.AdapterState
import io.horizontalsystems.bankwallet.core.IAccountManager
import io.horizontalsystems.bankwallet.core.ILocalStorage
import io.horizontalsystems.bankwallet.core.managers.ActiveAccountState
import io.horizontalsystems.bankwallet.core.managers.FaqManager
import io.horizontalsystems.bankwallet.entities.Account
import io.horizontalsystems.bankwallet.entities.ViewState
import io.horizontalsystems.bankwallet.entities.Wallet
import io.horizontalsystems.bankwallet.modules.manageaccounts.ManageAccountsModule
import io.horizontalsystems.core.ILanguageManager
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.reactive.asFlow
import kotlinx.coroutines.withContext
import java.net.URL
import kotlin.math.exp
import kotlin.math.log

class newBalanceViewModel ( private val accountManager: IAccountManager,
                            private val service: BalanceService,
                            private val balanceViewItemFactory: BalanceViewItemFactory,
                            private val balanceViewTypeManager: BalanceViewTypeManager,
                            private val totalBalance: TotalBalance,
                            private val localStorage: ILocalStorage,
                            private val languageManager: ILanguageManager,
                            private val faqManager: FaqManager
) : ViewModel(), ITotalBalance by totalBalance{


    var balanceScreenState by mutableStateOf<BalanceScreenState?>(null)
        private set
    private var activeAccountId: String? = null
    private var balanceViewType = balanceViewTypeManager.balanceViewTypeFlow.value
    private var viewState: ViewState = ViewState.Loading
    private var balanceViewItems = listOf<BalanceViewItem>()
    private var isRefreshing = false
    var viewItems by mutableStateOf<Pair<List<BalanceModule.BalanceAccountViewItem>, List<BalanceModule.BalanceAccountViewItem>>?>(null)
    var finish by mutableStateOf(false)
    val accountsList: List<Pair<String, Account>> = accountManager.accounts.map { account -> Pair(account.id, account) }
    val totalUIStateList = mutableListOf<TotalUIState>()
    val total = totalBalance.totalBalance



    private val setOfIds = hashSetOf<String>()

    fun addIdToIdsList(id: String) {
        setOfIds.add(id)
    }



    private val colors = listOf(
        Color(237, 110, 0, 255), // Orange
        Color(31, 27, 222, 255), // Blue
        Color(0, 102, 255, 255), // Cyan
        Color(27, 210, 222, 255), // Turquoise
        Color(255, 255, 255, 255) // Light blue/gray
    )

    fun getColorForPercent(percent: Int): Color {
        return when {
            percent > 30 -> colors[0] // orange
            percent > 19 -> colors[1] // blue
            percent > 10 -> colors[2] // cyan
            percent > 5 -> colors[3] // turquoise
            else -> colors[4] // light blue/gray
        }
    }

    var uiState by mutableStateOf(
        BalanceUiState(
            balanceViewItems = balanceViewItems,
            viewState = viewState,
            isRefreshing = isRefreshing,
            headerNote = HeaderNote.None
        )
    )
        private set

    val sortTypes = listOf(BalanceSortType.Value, BalanceSortType.Name, BalanceSortType.PercentGrowth)
    var sortType by service::sortType
    var expandedState: Boolean = false
    private var expandedWallet: Wallet? = null

    init {


        accountManager.activeAccountStateFlow.collectWith(viewModelScope) {
            handleAccount(it)
        }
        viewModelScope.launch {
            accountManager.accountsFlowable.asFlow()
                .collect { accounts ->
                    accountManager.activeAccountObservable.asFlow()
                        .collect { activeAccount ->
                            updateViewItems(activeAccount.orElse(null), accounts)
                        }
                }
        }

        viewModelScope.launch {
            accountManager.accountsFlowable.asFlow()
                .collect {
                    updateViewItems(accountManager.activeAccount, it)
                }
        }
        viewModelScope.launch {
            accountManager.activeAccountObservable.asFlow()
                .collect { activeAccount ->
                    updateViewItems(activeAccount.orElse(null), accountManager.accounts)
                }
        }
        updateViewItems(accountManager.activeAccount, accountManager.accounts)

        viewModelScope.launch {
            service.balanceItemsFlow
                .collect { items ->
                    totalBalance.setTotalServiceItems(items?.map {
                        TotalService.BalanceItem(
                            it.balanceData.total,
                            it.state !is AdapterState.Synced,
                            it.coinPrice
                        )
                    })

                    items?.let { refreshViewItems(it) }
                }
        }

        viewModelScope.launch {
            balanceViewTypeManager.balanceViewTypeFlow.collect {
                handleUpdatedBalanceViewType(it)
            }
        }
        service.start()
        totalBalance.start(viewModelScope)



    }


    fun autoSelectAccounts() {
        viewModelScope.launch {
            uiState = uiState.copy(viewState = ViewState.Loading)


            viewModelScope.launch {
            viewItems?.first?.forEachIndexed { index, accountViewItem ->
                    accountManager.setActiveAccountId(accountViewItem.accountId)
                    viewItems?.first?.getOrNull(index - 1)?.selected = false
                    accountViewItem.selected = true
                    delay(100L) // adjust delay as needed
                }
            }


            }

            uiState = uiState.copy(viewState = ViewState.Success)
        }

    private fun handleAccount(activeAccountState: ActiveAccountState) {
        when(activeAccountState) {
            ActiveAccountState.NotLoaded -> { }
            is ActiveAccountState.ActiveAccount -> {
                balanceScreenState = if (activeAccountState.account != null) {
                    BalanceScreenState.HasAccount(
                        AccountViewItem(
                            activeAccountState.account.isWatchAccount,
                            activeAccountState.account.name,
                            activeAccountState.account.id
                        )
                    )
                } else {
                    BalanceScreenState.NoAccount
                }
            }
        }
    }


    private fun updateViewItems(activeAccount: Account?, accounts: List<Account>) {
        viewItems = accounts

            .map { getViewItem(it, activeAccount) }
            .partition { !it.isWatchAccount }
    }


    private fun getViewItem(account: Account, activeAccount: Account?) =
        BalanceModule.BalanceAccountViewItem(
            accountId = account.id,
            title = account.name,
            subtitle = account.type.detailedDescription,
            selected = account == activeAccount,
            backupRequired = !account.isBackedUp,
            isWatchAccount = account.isWatchAccount,
            migrationRequired = account.nonStandard,
            migrationRecommended = account.nonRecommended
        )

    fun getAccountIds(accounts: List<BalanceModule.BalanceAccountViewItem>): List<String> {
        val accountIds = mutableListOf<String>()
        for (account in accounts) {
            accountIds.add(account.accountId)
        }
        return accountIds

    }

    fun setActiveAccounts(accounts: List<BalanceModule.BalanceAccountViewItem>) {
        val accountIds = getAccountIds(accounts)
        for (accountId in accountIds) {
            accountManager.setActiveAccountId(accountId)
        }
    }




    fun onSelect(accountViewItem: BalanceModule.BalanceAccountViewItem) {
        accountManager.setActiveAccountId(accountViewItem.accountId)
        expandedState = true


        finish = true

    }

    fun getAccountViewItem(accountId: String): BalanceModule.BalanceAccountViewItem? {
        return viewItems?.first?.find { it.accountId == accountId }
    }




    private suspend fun handleUpdatedBalanceViewType(balanceViewType: BalanceViewType) {
        this.balanceViewType = balanceViewType

        service.balanceItemsFlow.value?.let {
            refreshViewItems(it)
        }
    }
    private fun emitState() {
        val newUiState = BalanceUiState(
            balanceViewItems = balanceViewItems,
            viewState = viewState,
            isRefreshing = isRefreshing,
            headerNote = headerNote()
        )

        viewModelScope.launch {
            uiState = newUiState
        }
    }

    private fun headerNote(): HeaderNote {
        val account = service.account ?: return HeaderNote.None
        val nonRecommendedDismissed = localStorage.nonRecommendedAccountAlertDismissedAccounts.contains(account.id)

        return account.headerNote(nonRecommendedDismissed)
    }
    private suspend fun refreshViewItems(balanceItems: List<BalanceModule.BalanceItem>) {
        withContext(Dispatchers.IO) {
            viewState = ViewState.Success

            balanceViewItems = balanceItems.map { balanceItem ->
                balanceViewItemFactory.viewItem(
                    balanceItem,
                    service.baseCurrency,
                    balanceItem.wallet == expandedWallet,
                    balanceHidden,
                    service.isWatchAccount,
                    balanceViewType
                )
            }

            emitState()
        }

    }

    override fun onCleared() {
        totalBalance.stop()
        service.clear()
    }

    override fun toggleBalanceVisibility() {
        totalBalance.toggleBalanceVisibility()
        viewModelScope.launch {
            service.balanceItemsFlow.value?.let { refreshViewItems(it) }
        }
    }

    fun onItem(viewItem: BalanceViewItem) {
        viewModelScope.launch {
            expandedWallet = when {
                viewItem.wallet == expandedWallet -> null
                else -> viewItem.wallet
            }

            service.balanceItemsFlow.value?.let { refreshViewItems(it) }
        }
    }

    val pieChartData = mutableListOf<Float>()
    var numberStringed = ""
    var listData = mutableListOf<Double>()

    fun testTotalBalance() {

            val balanceInString = numberStringed
            val cleanedStringDollar = balanceInString.replace("$", "")
            val cleanedLineString = cleanedStringDollar.replace("~", "")
            val cleanedString = cleanedLineString.replace(",", ".")
            val walletBalance = cleanedString.toDouble()

          listData.add(walletBalance)



        }





    fun calculatePercentage(viewItem: BalanceViewItem, totalState: TotalUIState): Int {

        var percentage = 0
        if (totalState is TotalUIState.Visible) {
            val balanceInString = totalState.secondaryAmountStr
            val cleanedStringDollar = balanceInString.replace("$","")
            val cleanedLineString = cleanedStringDollar.replace("~","")
            val cleanedString = cleanedLineString.replace(",",".")
            val walletBalance = cleanedString.toDouble()

            val coinPriceString = viewItem.secondaryValue.value
            Log.d("coinPriceString è :", coinPriceString.toString())
            val cleanedStringCoinDollar = coinPriceString.replace("$","")
            val cleanedStringCoinComa = cleanedStringCoinDollar.replace(',', '.')
            val coinBalance = cleanedStringCoinComa.toDouble()
            percentage = (coinBalance / walletBalance  * 100).toInt()
        } else if (totalState is TotalUIState.Hidden) {
            percentage = 0
        }

        return percentage


    }


    fun calculateAndAddPercentage(viewItem: BalanceViewItem, totalState: TotalUIState) {
        if (totalState is TotalUIState.Visible) {
            val balanceInString = totalState.secondaryAmountStr
            val cleanedStringDollar = balanceInString.replace("$", "")
            val cleanedLineString = cleanedStringDollar.replace("~", "")
            val cleanedString = cleanedLineString.replace(",", ".")
            val walletBalance = cleanedString.toDouble()

            val coinPriceString = viewItem.secondaryValue.value
            val cleanedStringCoinDollar = coinPriceString.replace("$", "")
            val cleanedStringCoinComa = cleanedStringCoinDollar.replace(',', '.').toFloat()
            val coinBalance = cleanedStringCoinComa.toDouble()
            val percentage = (coinBalance / walletBalance * 100).toInt()
            if (percentage != -1) {
                pieChartData.add(percentage.toFloat())
            }
        }

    }

    fun getWalletForReceive(viewItem: BalanceViewItem) = when {
        viewItem.wallet.account.isBackedUp -> viewItem.wallet
        else -> throw BackupRequiredError(viewItem.wallet.account, viewItem.coinTitle)
    }

    fun onRefresh() {
        if (isRefreshing) {
            return
        }

        viewModelScope.launch {
            isRefreshing = true
            emitState()

            service.refresh()
            // A fake 2 seconds 'refresh'
            delay(2300)

            isRefreshing = false
            emitState()
        }
    }

    fun onCloseHeaderNote(headerNote: HeaderNote) {
        when (headerNote) {
            HeaderNote.NonRecommendedAccount -> {
                service.account?.let { account ->
                    localStorage.nonRecommendedAccountAlertDismissedAccounts += account.id
                    emitState()
                }
            }
            else -> Unit
        }
    }

    fun getFaqUrl(headerNote: HeaderNote): String {
        val baseUrl = URL(faqManager.faqListUrl)
        val faqUrl = headerNote.faqUrl(languageManager.currentLocale.language)
        return URL(baseUrl, faqUrl).toString()
    }

    fun disable(viewItem: BalanceViewItem) {
        service.disable(viewItem.wallet)
    }

    fun getSyncErrorDetails(viewItem: BalanceViewItem): SyncError = when {
        service.networkAvailable -> SyncError.Dialog(viewItem.wallet, viewItem.errorMessage)
        else -> SyncError.NetworkNotAvailable()
    }

    sealed class SyncError {
        class NetworkNotAvailable : SyncError()
        class Dialog(val wallet: Wallet, val errorMessage: String?) : SyncError()
    }

} //Fine viewModel


data class AccountViewItem(val isWatchAccount: Boolean, val name: String = "", val id: String)

sealed class BalanceScreenState() {
    class HasAccount(val accountViewItem: AccountViewItem) : BalanceScreenState()
    class HasNotAccount(val accountViewItem: AccountViewItem) : BalanceScreenState()
    object NoAccount : BalanceScreenState()
}

class BackupRequiredError(val account: Account, val coinTitle: String) : Error("Backup Required")

data class BalanceUiState(
    val balanceViewItems: List<BalanceViewItem>,
    val viewState: ViewState,
    val isRefreshing: Boolean,
    val headerNote: HeaderNote
)


sealed class TotalUIState {
    data class Visible(
        val primaryAmountStr: String,
        val secondaryAmountStr: String,
        val dimmed: Boolean
    ) : TotalUIState()

    object Hidden : TotalUIState()

}

enum class HeaderNote {
    None,
    NonStandardAccount,
    NonRecommendedAccount
}



fun HeaderNote.faqUrl(language: String) = when (this) {
    HeaderNote.NonStandardAccount -> "faq/$language/management/migration_required.md"
    HeaderNote.NonRecommendedAccount -> "faq/$language/management/migration_recommended.md"
    HeaderNote.None -> null
}

fun Account.headerNote(nonRecommendedDismissed: Boolean): HeaderNote = when {
    nonStandard -> HeaderNote.NonStandardAccount
    nonRecommended -> if (nonRecommendedDismissed) HeaderNote.None else HeaderNote.NonRecommendedAccount
    else -> HeaderNote.None
}