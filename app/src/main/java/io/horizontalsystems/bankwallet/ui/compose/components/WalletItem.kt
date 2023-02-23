package io.horizontalsystems.bankwallet.ui.compose.components

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.IconButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import io.horizontalsystems.bankwallet.R
import io.horizontalsystems.bankwallet.core.slideFromRight



@Composable
fun WalletItem( navController: NavController) {

    val expandedState = remember { mutableStateOf(false) }



    val itemColorFigma = Color(red = 31, green = 34, blue = 42)

    Box(
        modifier = Modifier
            .height(if (expandedState.value) 450.dp else 200.dp)
            .clip(RoundedCornerShape(25.dp))
            .width(350.dp)
            .background(itemColorFigma)
            .padding(top = 5.dp, start = 15.dp, end = 15.dp)
    )
    {
        Column(
            modifier = Modifier
                .height(600.dp)
        ) {
            if (!expandedState.value) {

                Column() {

                    Row(
                        modifier = Modifier
                            .height(50.dp)
                            .padding(all = 1.dp)


                    ) {
                        IconButton(
                            onClick = {
                                expandedState.value = !expandedState.value
                            },
                            modifier = Modifier.rotate(if (expandedState.value) 180f else 0f)

                        ) {
                            Image(
                                painter = painterResource(id = io.horizontalsystems.bankwallet.R.drawable.expand_wallet),
                                contentDescription = null
                            )
                        }

                        Spacer(Modifier.weight(1f))


                    }
                    Column(modifier = Modifier.align(Alignment.CenterHorizontally)) {

                        title3_jacob(
                            modifier = Modifier
                                .padding(bottom = 12.dp),
                            textAlign = TextAlign.Start,
                            overflow = TextOverflow.Ellipsis,
                            text = "Wallet1",
                        )

                        title3_green50(text = "$ 100.532.403,45")
                    }
                }
            } else {
                Row(
                    modifier = Modifier
                        .height(50.dp)
                        .padding(all = 1.dp)


                ) {
                    IconButton(
                        onClick = {
                            expandedState.value = !expandedState.value
                        },
                        modifier = Modifier.rotate(if (expandedState.value) 180f else 0f)

                    ) {
                        Image(
                            painter = painterResource(id = io.horizontalsystems.bankwallet.R.drawable.expand_wallet),
                            contentDescription = null
                        )
                    }

                    Spacer(Modifier.weight(1f))

                    IconButton(
                        onClick = {

                                navController.slideFromRight(R.id.manageAccountsFragment)

                        },

                        ) {
                        Image(
                            painter = painterResource(id = io.horizontalsystems.bankwallet.R.drawable.more_dots),
                            contentDescription = null
                        )
                    }
                }
                Box(
                    modifier = Modifier
                        .height(300.dp)
                        .offset(y = -40.dp)
                        .background(Color.Transparent)
                        .padding(all = 20.dp)
                )
                {

                }
                Row(
                    modifier = Modifier
                        .wrapContentSize()
                        .fillMaxWidth()
                        .padding(all = 10.dp)

                ) {

                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(200.dp)
                    ) {
                        CoinDataForWallet(modifier = Modifier
                            .weight(1f)
                            .height(50.dp))
                        CoinDataForWallet(modifier = Modifier
                            .weight(1f)
                            .height(50.dp))
                        CoinDataForWallet(modifier = Modifier
                            .weight(1f)
                            .height(50.dp))
                        CoinDataForWallet(modifier = Modifier
                            .weight(1f)
                            .height(50.dp))
                        CoinDataForWallet(modifier = Modifier
                            .weight(1f)
                            .height(50.dp))


                    }
                }
            }

        }
    }
}

