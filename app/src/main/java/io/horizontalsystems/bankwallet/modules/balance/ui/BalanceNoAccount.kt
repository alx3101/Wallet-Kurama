package io.horizontalsystems.bankwallet.modules.balance.ui

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.IconButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import io.horizontalsystems.bankwallet.R
import io.horizontalsystems.bankwallet.core.navigateWithTermsAccepted
import io.horizontalsystems.bankwallet.core.slideFromRight
import io.horizontalsystems.bankwallet.modules.balance.AccountViewItem
import io.horizontalsystems.bankwallet.modules.balance.BalanceModule
import io.horizontalsystems.bankwallet.modules.balance.BalanceViewModel
import io.horizontalsystems.bankwallet.ui.compose.components.*

@Composable
fun BalanceNoAccount(navController: NavController) {

    val itemColorFigma = Color(red = 31, green = 34, blue = 42)


    Column ( modifier = Modifier
        .background(Color.Black)
        .verticalScroll(rememberScrollState())) {


        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 50.dp, start = 25.dp, end = 25.dp)
        ) {



            createWalletButton(
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

            createWalletButton(
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


            /*

                      ButtonPrimaryYellow(
                          modifier = Modifier
                              .weight(1f)
                              .padding(horizontal = 48.dp),
                          title = stringResource(R.string.Button_Create),
                          onClick = {
                              navController.navigateWithTermsAccepted {
                                  navController.slideFromRight(R.id.createAccountFragment)
                              }
                          }
                      )


                      Spacer(modifier = Modifier.height(16.dp))
                      ButtonPrimaryDefault(
                          modifier = Modifier
                              .fillMaxWidth()
                              .padding(horizontal = 48.dp),
                          title = stringResource(R.string.Button_Restore),
                          onClick = {
                              navController.navigateWithTermsAccepted {
                                  navController.slideFromRight(R.id.restoreMnemonicFragment)
                              }
                          }
                      )
                  */
        }

        Column( modifier = Modifier.align(Alignment.CenterHorizontally)) {
        


        }


    }
}



