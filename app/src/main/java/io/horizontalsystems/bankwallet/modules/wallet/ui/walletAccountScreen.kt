package io.horizontalsystems.bankwallet.modules.wallet.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.gestures.rememberScrollableState
import androidx.compose.foundation.gestures.scrollable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorMatrix
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.ui.res.stringResource
import com.google.accompanist.swiperefresh.rememberSwipeRefreshState
import io.horizontalsystems.bankwallet.R
import io.horizontalsystems.bankwallet.core.App
import io.horizontalsystems.bankwallet.core.navigateWithTermsAccepted
import io.horizontalsystems.bankwallet.core.slideFromBottom
import io.horizontalsystems.bankwallet.core.slideFromRight
import io.horizontalsystems.bankwallet.entities.ViewState
import io.horizontalsystems.bankwallet.modules.balance.*
import io.horizontalsystems.bankwallet.modules.balance.ui.BalanceItems
import io.horizontalsystems.bankwallet.modules.balance.ui.WalletBalanceItem
import io.horizontalsystems.bankwallet.modules.nft.asset.NftAssetModule
import io.horizontalsystems.bankwallet.modules.nft.holdings.NftHoldingsModule
import io.horizontalsystems.bankwallet.modules.nft.holdings.NftHoldingsViewModel
import io.horizontalsystems.bankwallet.modules.nft.holdings.nftsCollectionSection
import io.horizontalsystems.bankwallet.ui.compose.HSSwipeRefresh
import io.horizontalsystems.bankwallet.ui.compose.components.*

@Composable
fun walletAccountScreen(
    navController: NavController,
    accountViewItem: AccountViewItem,
) {
    val account = App.accountManager.activeAccount ?: return

    val viewModel = viewModel<newBalanceViewModel>(factory = BalanceModule.Factory())
    val uiState = viewModel.uiState
    val balanceViewItems = uiState.balanceViewItems
    val totalState = viewModel.totalUiState
    val sortType = BalanceSortType
    val accountId = accountViewItem.id

    //NFT
    val viewModelNFT = viewModel<NftHoldingsViewModel>(factory = NftHoldingsModule.Factory(account))

    val collections = viewModelNFT.viewItems
    val viewState = viewModelNFT.viewState

    var selectedTabIndex by remember { mutableStateOf(0) }
    val enabledTabs = listOf(!collections.isEmpty(), false, !collections.isEmpty())




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
            modifier = Modifier

                .fillMaxSize()
                .background(color = Color.Black),
            state = listState,
            contentPadding = PaddingValues(top = 8.dp, bottom = 18.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            item {
                Row() {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(
                            imageVector = Icons.Filled.ArrowBack,
                            contentDescription = "Back button",
                            tint = Color.White
                        )
                    }
                }

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 50.dp, start = 25.dp, end = 25.dp)
                ) {

                   WalletActionsRow(navController)

/*
                    walletButton(
                        modifier = Modifier
                            .weight(1f)
                            .padding(all = 10.dp),
                        imageVector = ImageVector.vectorResource(id = R.drawable.receive_icon),
                        buttonText = "Import",
                        onClick = {
                            navController.navigateWithTermsAccepted {
                                navController.slideFromRight(R.id.restoreMnemonicFragment)
                            }
                        },
                        backgroundColor = Color.Transparent,
                        fontColor = Color.White
                    )

 */
                }



                Column(
                    modifier = Modifier
                        .padding(10.dp)
                ) {
                    WalletBalanceItem(
                        balanceViewItems,
                        viewModel,
                        accountViewItem,
                        navController,
                        uiState,
                        totalState,
                        expandedState = true
                    )
                    val tabs = listOf("Crypto", "NFT", "All Chains")

                    val selected = Color(red = 82, green = 89, blue = 106)
                    val tab = Color(red = 31, green = 34, blue = 42)

                    Row(
                        modifier = Modifier.fillMaxWidth().padding(top = 13.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(modifier = Modifier.weight(1f)) {
                            KuramaTabRow(
                                tabs = listOf("Crypto", "NFT", "All Chains"),
                                selectedTabIndex = selectedTabIndex,
                                onTabSelected = { index ->
                                    if (index == 2) {
                                        navController.navigate(R.id.coinFragment)
                                    } else {
                                        selectedTabIndex = index
                                    }
                                },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 16.dp),
                                tabColor = tab,
                                selectedTabColor = selected,
                                textColor = Color.White,
                                selectedTextColor = Color.White,
                                tabPadding = 24.dp,
                                cornerRadius = 25.dp,
                                spacing = 8.dp,
                                enabledTabs = listOf(true, collections.isNotEmpty(), true)
                            )
                        }
                        ButtonSecondaryCircle(
                            icon = R.drawable.ic_manage_2,
                            onClick = {
                                navController.slideFromRight(R.id.manageWalletsFragment)
                            }
                        )
                    }

                }
                }

            if (selectedTabIndex == 0) {
                items(balanceViewItems, key = { item -> item.wallet.hashCode() }) { item ->
                    if (item.isWatchAccount) {
                        walletCard(item, viewModel, navController)
                    } else {
                        walletCard(
                            viewItem = item,
                            viewModel = viewModel,
                            navController = navController
                        )
                    }
                }
            } else if (selectedTabIndex == 1) {

                            collections.forEach { collection ->
                                nftsCollectionSection(collection, viewModelNFT) { asset ->
                                    navController.slideFromBottom(
                                        R.id.nftAssetFragment,
                                        NftAssetModule.prepareParams(
                                            asset.collectionUid,
                                            asset.nftUid
                                        )
                                    )
                                }
                            }
                        }

                    }
                }
            }




    @Composable
    fun Wallets(
        balanceViewItems: List<BalanceViewItem>,
        viewModel: newBalanceViewModel,
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
                modifier = Modifier
                    .padding(20.dp)
                    .fillMaxSize(),
                state = listState,
                contentPadding = PaddingValues(top = 8.dp, bottom = 18.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                item {
                    Row() {
                        IconButton(onClick = { /*TODO*/ }) {
                            Icon(
                                imageVector = Icons.Filled.ArrowBack,
                                contentDescription = "Back button",
                                tint = Color.White
                            )
                        }
                    }

                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 50.dp, start = 25.dp, end = 25.dp)
                    ) {
                        walletButton(
                            modifier = Modifier
                                .weight(1f)
                                .padding(all = 10.dp),
                            imageVector = ImageVector.vectorResource(id = R.drawable.send_icon),
                            buttonText = "Create",
                            onClick = {
                                navController.navigateWithTermsAccepted {
                                    navController.slideFromRight(R.id.createAccountFragment)
                                }
                            },
                            backgroundColor = Color.Transparent,
                            fontColor = Color.White
                        )

                        walletButton(
                            modifier = Modifier
                                .weight(1f)
                                .padding(all = 10.dp),
                            imageVector = ImageVector.vectorResource(id = R.drawable.swap_icon),
                            buttonText = "Import",
                            onClick = {
                                navController.navigateWithTermsAccepted {
                                    navController.slideFromRight(R.id.restoreMnemonicFragment)
                                }
                            },
                            backgroundColor = Color.Transparent,
                            fontColor = Color.White
                        )

                        walletButton(
                            modifier = Modifier
                                .weight(1f)
                                .padding(all = 10.dp),
                            imageVector = ImageVector.vectorResource(id = R.drawable.receive_icon),
                            buttonText = "Import",
                            onClick = {
                                navController.navigateWithTermsAccepted {
                                    navController.slideFromRight(R.id.restoreMnemonicFragment)
                                }
                            },
                            backgroundColor = Color.Transparent,
                            fontColor = Color.White
                        )
                    }


                }
                items(balanceViewItems, key = { item -> item.wallet.hashCode() }) { item ->
                    if (item.isWatchAccount) {
                        walletCard(item, viewModel, navController)
                    } else {
                        walletCard(
                            viewItem = item,
                            viewModel = viewModel,
                            navController = navController
                        )
                    }
                }
            }
        }
    }


    @Composable
    fun WalletsBozza(
        balanceViewItems: List<BalanceViewItem>,
        viewModel: newBalanceViewModel,
        navController: NavController,
        accountId: String,
        sortType: BalanceSortType,
        uiState: BalanceUiState
    ) {

        var revealedCardId by remember { mutableStateOf<Int?>(null) }

        val listState = rememberLazyListState()

        /* HSSwipeRefresh(
        state = rememberSwipeRefreshState(uiState.isRefreshing),
        onRefresh = {
            viewModel.onRefresh()
        }
    ) {*/
        LazyColumn(
            modifier = Modifier
                .fillMaxSize(),
            state = listState,
            contentPadding = PaddingValues(top = 8.dp, bottom = 18.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            item {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(20.dp)
                ) {
                    walletButton(
                        modifier = Modifier
                            .weight(1f)
                            .padding(all = 10.dp),
                        imageVector = ImageVector.vectorResource(id = R.drawable.send_icon),
                        buttonText = "Create",
                        onClick = {
                            navController.navigateWithTermsAccepted {
                                navController.slideFromRight(R.id.createAccountFragment)
                            }
                        },
                        backgroundColor = Color.Transparent,
                        fontColor = Color.White
                    )
                }
            }
            items(balanceViewItems, key = { item -> item.wallet.hashCode() }) { item ->
                if (item.isWatchAccount) {
                    walletCard(item, viewModel, navController)
                } else {
                    walletCard(
                        viewItem = item,
                        viewModel = viewModel,
                        navController = navController
                    )
                }
            }
        }
    }






