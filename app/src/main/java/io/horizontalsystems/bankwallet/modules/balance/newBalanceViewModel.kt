package io.horizontalsystems.bankwallet.modules.balance

import android.accounts.AccountManager
import android.app.Application
import android.content.Context
import android.util.Log
import androidx.compose.runtime.State
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
import kotlin.math.ceil
import kotlin.math.exp
import kotlin.math.log

class newBalanceViewModel ( private val accountManager: IAccountManager,
                            private val service: BalanceService,
                            private val balanceViewItemFactory: BalanceViewItemFactory,
                            private val balanceViewTypeManager: BalanceViewTypeManager,
                            private val totalBalance: TotalBalance,
                            private val localStorage: ILocalStorage,
                            private val languageManager: ILanguageManager,
                            private val faqManager: FaqManager,

) : ViewModel(), ITotalBalance by totalBalance {


    var balanceScreenState by mutableStateOf<BalanceScreenState?>(null)
        private set
    private val _animationPlayed = mutableStateOf(false)
    val animationPlayed: State<Boolean> = _animationPlayed
    private var activeAccountId: String? = null
    private var balanceViewType = balanceViewTypeManager.balanceViewTypeFlow.value
    private var viewState: ViewState = ViewState.Loading
    private var balanceViewItems = listOf<BalanceViewItem>()
    private var isRefreshing = false
    var viewItems by mutableStateOf<Pair<List<BalanceModule.BalanceAccountViewItem>, List<BalanceModule.BalanceAccountViewItem>>?>(
        null
    )
    val walletBalanceDictionary = mutableMapOf<String, Double>() // Dictionary to store wallet balance with account ID
    var finish by mutableStateOf(false)
    val accountsList: List<Pair<String, Account>> =
        accountManager.accounts.map { account -> Pair(account.id, account) }
    val totalUIStateList = mutableListOf<TotalUIState>()
    val total = totalBalance.totalBalance
    val pieChartData = mutableListOf<Float>()
    var otherCoinsPecentage = 0




    var uiState by mutableStateOf(
        BalanceUiState(
            balanceViewItems = balanceViewItems,
            viewState = viewState,
            isRefreshing = isRefreshing,
            headerNote = HeaderNote.None
        )
    )
        private set

    val sortTypes =
        listOf(BalanceSortType.Value, BalanceSortType.Name, BalanceSortType.PercentGrowth)
    var sortType by service::sortType
    var expandedState: Boolean = false
    private var expandedWallet: Wallet? = null

    init {
        //AccountsViewModel
        accountManager.activeAccountStateFlow.collectWith(viewModelScope) {
            handleAccount(it)

        }

        //ManagesAccountViewModel
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


        //BalanceViewModel
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
                    pieChartData.clear()
                    calculateAllPercentages()
                }

        }

        viewModelScope.launch {

            balanceViewTypeManager.balanceViewTypeFlow.collect {
                handleUpdatedBalanceViewType(it)
                pieChartData.clear()
                calculateAndAddPercentage(balanceViewItems,totalUiState)

            }
        }
        viewModelScope.launch {


            calculateAllPercentages()


        }



        service.start()
        totalBalance.start(viewModelScope)
        otherCoinsPecentage
        Log.e("I valori dentro la map", walletBalanceDictionary.toString())
        Log.e("I valori sono della lista", pieChartData.toString())

    }


    private fun handleAccount(activeAccountState: ActiveAccountState) {
        when (activeAccountState) {
            ActiveAccountState.NotLoaded -> {}
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

    fun getWalletBalance(
        totalState: TotalUIState,
        accountId: String
    ) {
        var walletBalance = 0.0

        if (totalState is TotalUIState.Visible) {
            if (totalState.secondaryAmountStr == "---" || totalState.secondaryAmountStr.isNullOrEmpty()) {
                walletBalance = 0.0
            } else {
                val balanceInString = totalState.secondaryAmountStr
                val cleanedStringDollar = balanceInString.replace("$", "")
                val cleanedLineString = cleanedStringDollar.replace("~", "")
                val cleanedString = cleanedLineString.replace(",", ".")
                walletBalance = cleanedString.toDouble()

                val accountID = accountId
                walletBalanceDictionary[accountID] = walletBalance // Store wallet balance with account ID in dictionary
            }
        }
    }



    fun setAnimationPlayed(value: Boolean) {
        _animationPlayed.value = value
    }

    fun getColorForPercent(percent: Int, index: Int): Color {
        val colors = arrayOf(
            Color(0xFFED6E00),
            Color(0xFF1D1BDE),
            Color(0xFF0066FF),
            Color(0xFF1BD2DE),
            Color(0xFF7283BE)
        )
        return colors.getOrElse(index % colors.size) { colors.last() }
    }

    fun getProportions(percentValues: List<Float>): List<Triple<Float, Float, Color>> {
        var prevEndAngle = 105F
        return percentValues.mapIndexed { index, item ->
            val color = getColorForPercent(item.toInt(), index)
            val sweepAngle = item / 100 * 360F
            val proportion = Triple(prevEndAngle, prevEndAngle + sweepAngle, color)
            prevEndAngle += sweepAngle // update end angle for next arc
            proportion
        }
    }

    fun getData(percentValues: List<Float>): List<Pair<Float, Color>> {
        return percentValues.mapIndexed { index, item ->
            val color = getColorForPercent(item.toInt(), index)
            Pair(item, color)
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




    fun onSelect(accountViewItem: BalanceModule.BalanceAccountViewItem) {
        accountManager.setActiveAccountId(accountViewItem.accountId)
        expandedState = true
        finish = true

    }

    fun getAccountViewItem(accountId: String): BalanceModule.BalanceAccountViewItem? {
        pieChartData.clear()
        return viewItems?.first?.find { it.accountId == accountId }
        calculateAllPercentages()

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
        val nonRecommendedDismissed =
            localStorage.nonRecommendedAccountAlertDismissedAccounts.contains(account.id)

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
            service.balanceItemsFlow.value?.let {
                refreshViewItems(it)
            }
            pieChartData.clear()
            calculateAllPercentages()
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


    fun calculatePercentage(viewItem: BalanceViewItem, totalState: TotalUIState,accountId: String): Int {

        var percentage = 0
        if (totalState is TotalUIState.Visible) {
            var walletBalance = 0.0
            if (totalState.secondaryAmountStr == "---" || totalState.secondaryAmountStr.isNullOrEmpty()) {

                walletBalance = 0.0

            } else {
                val balanceInString = totalState.secondaryAmountStr
                val cleanedStringDollar = balanceInString.replace("$", "")
                val cleanedLineString = cleanedStringDollar.replace("~", "")
                val cleanedString = cleanedLineString.replace(",", ".")
                walletBalance = cleanedString.toDouble()

                walletBalanceDictionary[accountId] = walletBalance



            }

            if (viewItem.secondaryValue.value == "---" || viewItem.secondaryValue.value.isNullOrEmpty()) {

                percentage = 0
            } else {
                val coinPriceString = viewItem.secondaryValue.value
                Log.d("coinPriceString è :", coinPriceString.toString())
                val cleanedStringCoinDollar = coinPriceString.replace("$", "")
                val cleanedStringCoinComa = cleanedStringCoinDollar.replace(',', '.')
                val coinBalance = cleanedStringCoinComa.toDouble()
                percentage = ceil(coinBalance / walletBalance * 100).toInt()
            }


        }

        return percentage


    }


    fun calculateAndAddPercentageRest(
        items: List<BalanceViewItem>,
        totalState: TotalUIState
    ) {
        if (totalState is TotalUIState.Visible) {
            var percentage = 0
            if (totalState is TotalUIState.Visible) {
                var walletBalance = 0.0
                var top4CoinBalance = 0.0
                if (totalState.secondaryAmountStr == "---" || totalState.secondaryAmountStr.isNullOrEmpty()) {
                    walletBalance = 0.0
                } else {
                    val balanceInString = totalState.secondaryAmountStr
                    val cleanedStringDollar = balanceInString.replace("$", "")
                    val cleanedLineString = cleanedStringDollar.replace("~", "")
                    val cleanedString = cleanedLineString.replace(",", ".")
                    walletBalance = cleanedString.toDouble()
                }

                // calculate the total value of the coins beyond the top 4
                val totalCoinValue = items.filter { !it.secondaryValue.value.isNullOrEmpty() }
                    .sortedWith(compareByDescending { it.secondaryValue.value })
                    .drop(4)
                    .sumByDouble {
                        val coinPriceString = it.secondaryValue.value
                        val cleanedStringCoinDollar = coinPriceString.replace("$", "")
                        val cleanedStringCoinComa = cleanedStringCoinDollar.replace(',', '.').toFloat()
                        cleanedStringCoinComa.toDouble()
                    }

                // calculate the percentage of the total value of the coins beyond the top 4
                percentage = if (walletBalance == 0.0) {
                    0
                } else {
                    ceil((totalCoinValue / walletBalance) * 100).toInt()
                }

                // add the percentage to the pie chart data
                if (percentage != -1) {
                    pieChartData.add(percentage.toFloat())

                    Log.e("la percentuale rimanente è ", percentage.toString())
                    otherCoinsPecentage = percentage.toInt()
                }
            }
        }
    }


    fun calculateAndAddPercentage(
        items: List<BalanceViewItem>,
        totalState: TotalUIState
    ) {
        if (totalState is TotalUIState.Visible) {
            var percentage = 0
            if (totalState is TotalUIState.Visible) {
                var walletBalance = 0.0
                var top4CoinBalance = 0.0
                if (totalState.secondaryAmountStr == "---") {

                    walletBalance = 0.0

                } else {
                    val balanceInString = totalState.secondaryAmountStr
                    val cleanedStringDollar = balanceInString.replace("$", "")
                    val cleanedLineString = cleanedStringDollar.replace("~", "")
                    val cleanedString = cleanedLineString.replace(",", ".")
                    walletBalance = cleanedString.toDouble()

                }
                val topFourItems =
                    items.filter { !it.secondaryValue.value.isNullOrEmpty() }
                        .sortedWith(compareByDescending { it.secondaryValue.value })
                        .take(4)

                topFourItems.forEach { item ->
                    val coinPriceString = item.secondaryValue.value
                    val cleanedStringCoinDollar = coinPriceString.replace("$", "")
                    val cleanedStringCoinComa = cleanedStringCoinDollar.replace(',', '.').toFloat()
                    val coinBalance = cleanedStringCoinComa.toDouble()
                    val percentage = if (walletBalance == 0.0) {
                        0
                    } else {
                        (coinBalance / walletBalance * 100).toInt()
                    }
                    if (percentage != -1) {
                        pieChartData.add(percentage.toFloat())
                    }
                }

            }
        }
    }
    fun calculateAllPercentages() {
        calculateAndAddPercentage(balanceViewItems, totalUiState)
        calculateAndAddPercentageRest(balanceViewItems, totalUiState)

        if (balanceHidden) {
            viewModelScope.launch {
                delay(2500L)
                pieChartData.clear()
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
        pieChartData.clear()
        calculateAndAddPercentage(balanceViewItems,totalUiState)

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
