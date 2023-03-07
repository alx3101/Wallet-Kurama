package io.horizontalsystems.bankwallet.modules.wallet.ui

import androidx.compose.animation.Crossfade
import androidx.compose.foundation.Image
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
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.os.bundleOf
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
import io.horizontalsystems.bankwallet.modules.manageaccounts.ManageAccountsModule
import io.horizontalsystems.bankwallet.modules.nft.asset.NftAssetModule
import io.horizontalsystems.bankwallet.modules.nft.holdings.NftCollectionViewItem
import io.horizontalsystems.bankwallet.modules.nft.holdings.NftHoldingsModule
import io.horizontalsystems.bankwallet.modules.nft.holdings.NftHoldingsViewModel
import io.horizontalsystems.bankwallet.modules.nft.holdings.nftsCollectionSection
import io.horizontalsystems.bankwallet.modules.restoreaccount.resoreprivatekey.RestorePrivateKeyModule
import io.horizontalsystems.bankwallet.modules.restoreaccount.resoreprivatekey.RestorePrivateKeyViewModel
import io.horizontalsystems.bankwallet.modules.restoreaccount.restore.RestoreViewModel
import io.horizontalsystems.bankwallet.modules.restoreaccount.restoreblockchains.RestoreBlockchainsFragment
import io.horizontalsystems.bankwallet.ui.compose.ComposeAppTheme
import io.horizontalsystems.bankwallet.ui.compose.HSSwipeRefresh
import io.horizontalsystems.bankwallet.ui.compose.OrangeK
import io.horizontalsystems.bankwallet.ui.compose.components.*

@Composable
fun walletAccountScreen(
    navController: NavController,
    accountViewItem: AccountViewItem,
) {
    val account = App.accountManager.activeAccount ?: return
    val restoreViewModel = viewModel<RestorePrivateKeyViewModel>(factory = RestorePrivateKeyModule.Factory())
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
                .background(ComposeAppTheme.colors.claude),
            state = listState,
            contentPadding = PaddingValues(top = 10.dp, bottom = 18.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            item {
                Row(modifier = Modifier.padding(top = 40.dp).height(30.dp)) {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(
                            imageVector = Icons.Filled.ArrowBack,
                            contentDescription = "Back button",
                            tint = Color.Gray
                        )
                    }
                }

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 0.dp, start = 25.dp, end = 25.dp)
                ) {

                    WalletActionsRow(navController)

                }


                Column(
                    modifier = Modifier
                        .padding(15.dp)
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


                    val selected = Color(red = 82, green = 89, blue = 106)
                    val tab = Color(red = 31, green = 34, blue = 42)

                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 0.dp),
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
                                    .padding(vertical = 14.dp),
                                tabColor = tab,
                                selectedTabColor = selected,
                                textColor = Color.White,
                                selectedTextColor = Color.White,
                                tabPadding = 24.dp,
                                cornerRadius = 25.dp,
                                spacing = 9.dp,
                                enabledTabs = listOf(true,true , true)
                            )
                        }
                        ButtonSecondaryCircle(
                            icon = R.drawable.ic_manage_2,
                            onClick = {
                                navController.slideFromRight(R.id.manageWalletsFragment)
                            }
                        )
                    }

                    walletAccountScreenContent(
                        accountViewItem,
                        viewModel = viewModel,
                        NFTViewModel = viewModelNFT,
                        collections,
                        selectedTabIndex = selectedTabIndex,
                        navController = navController,
                        balanceViewItems = balanceViewItems,
                        modifier = Modifier.fillMaxWidth(),
                    )
                }


            }
        }
    }
}



@Composable
fun walletAccountScreenContent(accountViewItem: AccountViewItem,
                               viewModel: newBalanceViewModel,
                               NFTViewModel: NftHoldingsViewModel,
                               collectionViewItem: List<NftCollectionViewItem>,
                               selectedTabIndex: Int, navController: NavController,
                               balanceViewItems: List<BalanceViewItem>,
                               modifier: Modifier) {

    //NFT
    val collections = NFTViewModel.viewItems
    val viewState = NFTViewModel.viewState
    val uiState = viewModel.uiState
    when (selectedTabIndex) {

        0 -> {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(color = Color.Transparent)
                            .padding(top = 8.dp, bottom = 18.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {

                        balanceViewItems.forEach { item ->
                            item.wallet.hashCode()
                            walletCard(
                                modifier = Modifier.fillMaxWidth(),
                                viewItem = item,
                                viewModel = viewModel,
                                navController = navController
                            )
                        }
                    }
            }

        1 -> {
            if (collections.isEmpty()) {

                Box(modifier = Modifier.padding(50.dp).fillMaxSize()) {


                    Image(
                        painter = painterResource(id = R.drawable.ic_image_empty),
                        contentDescription = null,
                        modifier = Modifier.align(Alignment.TopCenter)
                    )

                    Text(
                        text = "You don't have any NFTs in your wallet",
                        color = Color.Gray,
                        style = TextStyle(
                            fontSize = 15.sp,
                            fontWeight = FontWeight.W400
                        ),
                            modifier = Modifier.align(Alignment.BottomCenter)
                                .padding(top = 60.dp)
                        )
                }
            } else {



                Text(text = "Ci sono NFT",
                    color = OrangeK,
                    style = TextStyle(
                        fontSize = 30.sp,
                        fontWeight = FontWeight.W400
                    ),

                    )

             /*   LazyColumn(modifier = Modifier.fillMaxSize()) {

                    collectionViewItem.forEach { collection ->
                        nftsCollectionSection(collection, NFTViewModel) { asset ->
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

              */
            }
        }
        2 -> {

            navController.slideFromRight(
               R.id.restoreSelectCoinsFragment
            )
        }
    }


    }










