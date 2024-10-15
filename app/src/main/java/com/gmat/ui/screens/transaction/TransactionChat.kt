package com.gmat.ui.screens.transaction

import androidx.navigation.NavController
import com.gmat.ui.components.CenterBar
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.gmat.R
import com.gmat.data.model.TransactionModel
import com.gmat.data.model.UserModel
import com.gmat.env.ChatDetails
import com.gmat.env.formatDate
import com.gmat.navigation.NavRoutes
import com.gmat.ui.events.QRScannerEvents
import com.gmat.ui.events.TransactionEvents
import com.gmat.ui.theme.DarkGreen
import com.gmat.ui.theme.DarkRed
import kotlinx.coroutines.launch

@Composable
fun TransactionChat(
    modifier: Modifier = Modifier,
    navController: NavController,
    user: UserModel?,
    chatIndex: String,
    recentUserTransactions: List<ChatDetails>? = null,
    onQRScannerEvents: (QRScannerEvents) -> Unit
) {
    val transactionUser by remember {
        mutableStateOf(recentUserTransactions?.get(chatIndex.toInt())?.userDetails)
    }

    val chats by remember {
        mutableStateOf(
            recentUserTransactions?.get(chatIndex.toInt())?.transactions?.sortedBy { it.timestamp }
        )
    }

    val listState = rememberLazyListState()

// Automatically scroll to the last item (most recent chat) with smooth animation
    LaunchedEffect(chats) {
        chats?.let {
            launch {
                listState.animateScrollToItem(
                    index = it.size - 1, // Scroll to the last item
                    scrollOffset = 0 // Adjust this if needed
                )
            }
        }
    }

    Scaffold(
        topBar = {
            CenterBar(
                onClick = { navController.navigateUp() },
                title = {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center,
                    ) {
                        Text(
                            text = transactionUser!!.name,
                            style = MaterialTheme.typography.headlineMedium,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                        Text(
                            text = "UPI ID: ${transactionUser!!.vpa}",
                            style = MaterialTheme.typography.labelMedium,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                    }
                }
            )
        },
        floatingActionButton = {
            user?.let { user ->
                if (!user.isMerchant) {
                    ExtendedFloatingActionButton(
                        onClick = {
                            onQRScannerEvents(QRScannerEvents.AddQR(transactionUser!!.qr))
                            navController.navigate(NavRoutes.AddTransactionDetails.route)
                        }
                    ) {
                        Text(
                            "Pay",
                            style = MaterialTheme.typography.bodyMedium
                        )
                    }
                }
            }
        }
    ) { innerPadding ->
        LazyColumn(
            state = listState, // Attach the list state for controlling the scroll position
            modifier = modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(bottom = 80.dp)
        ) {
            itemsIndexed(chats!!, key = { _, transaction -> transaction.txnId }) { _, transaction ->
                Row(
                    modifier = Modifier.fillMaxWidth()
                ) {
                    if (user?.isMerchant == true) {
                        // Merchant: Show card on the left side
                        TransactionCard(
                            navController = navController,
                            transaction = transaction,
                            transactionUser = transactionUser,
                            modifier = Modifier.weight(1f),
                            isMerchant = true,
                            payerUserId = ""
                        )
                        Spacer(modifier = Modifier.weight(1f)) // Push content to the right
                    } else {
                        Spacer(modifier = Modifier.weight(1f)) // Pushes the card to the right
                        TransactionCard(
                            navController = navController,
                            transaction = transaction,
                            transactionUser = transactionUser,
                            modifier = Modifier.weight(1f),
                            isMerchant = false,
                            payerUserId = user!!.userId // Card on the right side
                        )
                    }
                }
            }
        }
    }
}

// TransactionCard composable to display transaction details
@Composable
fun TransactionCard(
    transaction: TransactionModel,
    modifier: Modifier = Modifier,
    navController: NavController,
    transactionUser: UserModel?,
    isMerchant: Boolean,
    payerUserId: String
) {
    Card(
        onClick = {
            if (isMerchant) {
                navController.navigate(
                    NavRoutes.TransactionReceipt.withArgs(
                        transaction.txnId,
                        transactionUser!!.userId
                    )
                )
            } else {
                navController.navigate(
                    NavRoutes.TransactionReceipt.withArgs(
                        transaction.txnId,
                        payerUserId
                    )
                )
            }

        },
        modifier = modifier
            .padding(8.dp), // Card-specific padding
        elevation = CardDefaults.cardElevation(4.dp),
        colors = CardDefaults.cardColors(
            contentColor = MaterialTheme.colorScheme.onPrimary,
            containerColor = MaterialTheme.colorScheme.primary
        )
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 24.dp, horizontal = 14.dp) // Padding inside the card
        ) {
            Column(
                modifier = Modifier.fillMaxWidth() // Ensures column takes full width of the card
            ) {
                Text(
                    text = getTransactionStatusText(transaction.status),
                    style = MaterialTheme.typography.bodyLarge.copy(
                        fontSize = 12.sp
                    ),
                    modifier = Modifier
                        .align(Alignment.Start)
                        .padding(top = 0.dp)
                )
                Spacer(modifier = Modifier.height(10.dp))
                Text(
                    text = "₹ " + transaction.amount,
                    style = MaterialTheme.typography.bodyLarge.copy(
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold
                    )
                )
                Spacer(modifier = Modifier.height(5.dp))
                Text(
                    text = formatDate(transaction.timestamp),
                    style = MaterialTheme.typography.bodyLarge.copy(
                        fontSize = 10.sp
                    ),
                    modifier = Modifier.padding(top = 4.dp)
                )
            }

            // Status image at the top right corner
            val (statusImage, statusColor) = when (transaction.status) {
                1 -> Pair(R.drawable.success, DarkGreen) // Success: Green
                else -> Pair(R.drawable.failed, DarkRed)
            }

            Icon(
                painter = painterResource(id = statusImage),
                contentDescription = getTransactionStatusText(transaction.status),
                modifier = Modifier
                    .align(Alignment.TopEnd) // Aligns the icon to the top right of the card
                    .size(30.dp),
                tint = statusColor
            )
        }
    }
}

fun getTransactionStatusText(status: Int): String {
    return when (status) {
        1 -> "Completed"
        2 -> "Pending"
        else -> "Failed"
    }
}