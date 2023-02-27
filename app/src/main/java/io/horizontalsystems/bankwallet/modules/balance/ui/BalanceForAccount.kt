package io.horizontalsystems.bankwallet.modules.balance.ui

import android.accounts.Account
import androidx.compose.animation.Crossfade
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.vectorResource
import com.google.accompanist.swiperefresh.rememberSwipeRefreshState
import io.horizontalsystems.bankwallet.R
import io.horizontalsystems.bankwallet.core.App.Companion.accountManager
import io.horizontalsystems.bankwallet.core.navigateWithTermsAccepted
import io.horizontalsystems.bankwallet.core.slideFromRight
import io.horizontalsystems.bankwallet.entities.ViewState
import io.horizontalsystems.bankwallet.modules.availablebalance.AvailableBalanceViewModel
import io.horizontalsystems.bankwallet.modules.balance.*
import io.horizontalsystems.bankwallet.ui.compose.HSSwipeRefresh
import io.horizontalsystems.bankwallet.ui.compose.components.createWalletButton

@Composable
fun BalanceForAccount(navController: NavController) {


    val viewModel = viewModel<newBalanceViewModel>(factory = BalanceModule.Factory())
    val viewItems = viewModel.viewItems
    val itemColorFigma = Color(red = 31, green = 34, blue = 42)


        val uiState = viewModel.uiState
        val balanceViewItems = uiState.balanceViewItems
        HSSwipeRefresh(
            state = rememberSwipeRefreshState(uiState.isRefreshing),
            onRefresh = {
                viewModel.onRefresh()

            }
        ) {

            LazyColumn(modifier = Modifier.padding(top = 48.dp)) {
                item {
                    Row(
                        horizontalArrangement = Arrangement.End,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 40.dp  )

                    ) {


                        createWalletButton(
                            modifier = Modifier
                                .weight(1f)
                                .padding(all = 1.dp),

                            imageVector = ImageVector.vectorResource(id = R.drawable.create_wallet_icon),
                            buttonText = "Create",
                            onClick = {
                                navController.navigateWithTermsAccepted {
                                    navController.slideFromRight(R.id.createAccountFragment)
                                }
                            },
                            backgroundColor = itemColorFigma,
                            fontColor = Color.Black
                        )

                        Spacer(modifier = Modifier.width(19.dp))

                        createWalletButton(
                            modifier = Modifier
                                .weight(1f),
                            imageVector = ImageVector.vectorResource(id = R.drawable.create_wallet_icon),
                            buttonText = "Import",
                            onClick = {
                                navController.navigateWithTermsAccepted {
                                    navController.slideFromRight(R.id.restoreMnemonicFragment)
                                }
                            },
                            backgroundColor = itemColorFigma,
                            fontColor = Color.Black
                        )
                    }


                    Spacer(modifier = Modifier.height(10.dp))

                    viewItems?.let { (regularAccounts, watchAccounts) ->
                        if (regularAccounts.isNotEmpty()) {
                            BalanceAccountsSection(regularAccounts, viewModel, navController)
                            Spacer(modifier = Modifier.height(32.dp))
                        }

                        if (watchAccounts.isNotEmpty()) {
                            BalanceAccountsSection(watchAccounts, viewModel, navController)
                            Spacer(modifier = Modifier.height(32.dp))
                        }
                    }

                    /* Column(modifier = Modifier.align(Alignment.CenterHorizontally)) {
            BalanceItems(
                balanceViewItems,
                viewModel,
                accountViewItem,
                navController,
                uiState,
                viewModel.totalUiState,
                expandedState = true
            )
        }
        */

                }
            }
        }
    }


@Composable
private fun BalanceAccountsSection(
    accounts: List<BalanceModule.BalanceAccountViewItem>,
    viewModel: newBalanceViewModel,
    navController: NavController,
) {
    val uiState = viewModel.uiState
    val totalState = viewModel.totalUiState


    Crossfade(uiState.viewState) { viewState ->
        when (viewState) {
            ViewState.Success -> {
                Column ( modifier = Modifier.padding(start = 15.dp, end = 15.dp)  ) {
                    for (accountViewItem in accounts) {
                        val expandedState = accountViewItem.selected


                        BalanceItems(
                            balanceViewItems = uiState.balanceViewItems,
                            viewModel = viewModel,
                            accountViewItem = accountViewItem,
                            navController = navController,
                            uiState = uiState,
                            totalState = totalState,
                            expandedState = accountViewItem.selected ,

                        )
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
@SuppressLint("UnrememberedMutableState")
@Composable
fun BalanceForAccount(navController: NavController, accountViewItem: AccountViewItem) {
    val viewModel = viewModel<BalanceViewModel>(factory = BalanceModule.Factory())
    val itemColorFigma = Color(red = 31, green = 34, blue = 42)


    Column(
        modifier = Modifier
            .background(Color.Black)
            .verticalScroll(rememberScrollState())

    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 50.dp, start = 25.dp, end = 25.dp)
        ) {


            CreateWallet(
                modifier = Modifier
                    .weight(1f)
                    .padding(all = 10.dp),
                imageVector = ImageVector.vectorResource(id = R.drawable.create_wallet_icon),
                buttonText = "Create",
                onClick = {
                    navController.navigateWithTermsAccepted {
                        navController.slideFromRight(R.id.createAccountFragment)
                    }
                },
                backgroundColor = itemColorFigma,
                fontColor = Color.Black
            )

            CreateWallet(
                modifier = Modifier
                    .weight(1f)
                    .padding(all = 10.dp),
                imageVector = ImageVector.vectorResource(id = R.drawable.create_wallet_icon),
                buttonText = "Import",
                onClick = {
                    navController.navigateWithTermsAccepted {
                        navController.slideFromRight(R.id.restoreMnemonicFragment)
                    }
                },
                backgroundColor = itemColorFigma,
                fontColor = Color.Black
            )
        }


        val uiState = viewModel.uiState
       val AccountManager: IAccountManager
       val accounts: List<Account> = accountManager.accounts



        Crossfade(uiState.viewState) { viewState ->
            when (viewState) {
                ViewState.Success -> {
                    val balanceViewItems = uiState.balanceViewItems

                    if (balanceViewItems.isNotEmpty()) {

                        accounts.forEach {
                            BalanceItems(
                                balanceViewItems,
                                viewModel,
                                accountViewItem,
                                navController,
                                uiState,
                                viewModel.totalUiState
                            )
                        }
                    } else {
                        BalanceItemsEmpty(navController, accountViewItem)
                    }
                }
                ViewState.Loading,
                is ViewState.Error -> {}
            }
        }
    }
}

 */