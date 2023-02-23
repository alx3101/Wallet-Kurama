package io.horizontalsystems.bankwallet.modules.market

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.platform.ViewCompositionStrategy
import androidx.compose.ui.res.stringResource
import androidx.core.os.bundleOf
import androidx.fragment.app.viewModels
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.google.accompanist.pager.ExperimentalPagerApi
import com.google.accompanist.pager.HorizontalPager
import com.google.accompanist.pager.rememberPagerState
import io.horizontalsystems.bankwallet.R
import io.horizontalsystems.bankwallet.core.BaseFragment
import io.horizontalsystems.bankwallet.core.slideFromRight
import io.horizontalsystems.bankwallet.modules.market.favorites.MarketFavoritesScreen
import io.horizontalsystems.bankwallet.modules.market.overview.MarketOverviewScreen
import io.horizontalsystems.bankwallet.modules.market.posts.MarketPostsScreen
import io.horizontalsystems.bankwallet.modules.market.topcoins.MarketTopCoinsFragment
import io.horizontalsystems.bankwallet.modules.market.topcoins.MarketTopCoinsModule
import io.horizontalsystems.bankwallet.modules.market.topcoins.MarketTopCoinsViewModel
import io.horizontalsystems.bankwallet.ui.compose.ComposeAppTheme
import io.horizontalsystems.bankwallet.ui.compose.TranslatableString
import io.horizontalsystems.bankwallet.ui.compose.components.AppBar
import io.horizontalsystems.bankwallet.ui.compose.components.MenuItem
import io.horizontalsystems.bankwallet.ui.compose.components.TabItem
import io.horizontalsystems.bankwallet.ui.compose.components.Tabs
import io.horizontalsystems.core.findNavController


class MarketFragment : BaseFragment() {

    private val sortingField by lazy {
        arguments?.getParcelable<SortingField>(sortingFieldKey)
    }
    private val topMarket by lazy {
        arguments?.getParcelable<TopMarket>(topMarketKey)
    }
    private val marketField by lazy {
        arguments?.getParcelable<MarketField>(marketFieldKey)
    }


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return ComposeView(requireContext()).apply {
            setViewCompositionStrategy(
                ViewCompositionStrategy.DisposeOnLifecycleDestroyed(viewLifecycleOwner)
            )
            setContent {
                ComposeAppTheme {
                    MarketFragmentScreen(findNavController(),viewModel)
                }
            }
        }
    }

    val viewModel by viewModels<MarketTopCoinsViewModel> {
        MarketTopCoinsModule.Factory(topMarket, sortingField, marketField)
    }
    companion object {
        private const val sortingFieldKey = "sorting_field"
        private const val topMarketKey = "top_market"
        private const val marketFieldKey = "market_field"

        fun prepareParams(
            sortingField: SortingField,
            topMarket: TopMarket,
            marketField: MarketField
        ): Bundle {
            return bundleOf(
                sortingFieldKey to sortingField,
                topMarketKey to topMarket,
                marketFieldKey to marketField
            )
        }
    }
}

@OptIn(ExperimentalPagerApi::class)
@Composable
fun MarketFragmentScreen(navController: NavController, topCoinViewModel: MarketTopCoinsViewModel) {
    val marketViewModel = viewModel<MarketViewModel>(factory = MarketModule.Factory())
    val tabs = marketViewModel.tabs
    val selectedTab = marketViewModel.selectedTab

    val pagerState = rememberPagerState(initialPage = selectedTab.ordinal)

    Column(modifier = Modifier.background(color = ComposeAppTheme.colors.tyler)) {
        AppBar(
            title = TranslatableString.ResString(R.string.Market_Title),
            menuItems = listOf(
                MenuItem(
                    title = TranslatableString.ResString(R.string.Market_Search),
                    icon = R.drawable.ic_search_discovery_24,
                    onClick = {
                        navController.slideFromRight(R.id.marketSearchFragment)
                    }
                )
            )
        )

        MarketOverviewScreen(navController = navController,topCoinViewModel)
        /*
            LaunchedEffect(key1 = selectedTab, block = {
                pagerState.scrollToPage(selectedTab.ordinal)
            })
            val tabItems = tabs.map {
                TabItem(stringResource(id = it.titleResId), it == selectedTab, it)
            }
            Tabs(tabItems, onClick = {
                marketViewModel.onSelect(it)
            })




            HorizontalPager(
                count = tabs.size,
                state = pagerState,
                userScrollEnabled = false
            ) { page ->
                when (tabs[page]) {
                    MarketModule.Tab.Overview -> MarketOverviewScreen(navController)
                    MarketModule.Tab.Posts -> MarketPostsScreen()
                    MarketModule.Tab.Watchlist -> MarketFavoritesScreen(navController)
                }
            }

             */
    }
}