package me.mrsajal.credapp


import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Slider
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp


@Composable
fun CreditSelectionScreen() {
    var cardStack by remember { mutableStateOf(listOf<Int>()) }

    var currentCreditAmount by remember { mutableStateOf(0) }
    var selectedEMI by remember { mutableStateOf<EmiItem?>(null) }
    var selectedBankAccount by remember { mutableStateOf("") }

    var showEMIModal by remember { mutableStateOf(false) }
    var showBankModal by remember { mutableStateOf(false) }

    val cardHeights = remember { mutableStateListOf<Int>() }

    var items by remember { mutableStateOf<List<Item>>(emptyList()) }
    var isLoading by remember { mutableStateOf(true) }
    var errorMessage by remember { mutableStateOf<String?>(null) }

    LaunchedEffect(Unit) {
        try {
            items = fetchItemsFromApi()
            isLoading = false
        } catch (e: Exception) {
            errorMessage = "Error fetching data"
            isLoading = false
        }
    }

    if (isLoading) {
        // Show loading indicator
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            CircularProgressIndicator()
        }
    } else if (errorMessage != null) {
        // Show error message
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            Text(text = errorMessage!!)
        }
    } else if (items.size >= 3) {
        // Proceed with UI after data is loaded
        BoxWithConstraints(
            modifier = Modifier
                .fillMaxSize()
                .background(Color(0xFF1D1E2C))
        ) {
            val density = LocalDensity.current
            val screenHeightPx = constraints.maxHeight.toFloat()
            val screenHeightDp = with(density) { screenHeightPx.toDp() }

            // Helper function to show modals or reset to CreditAmountView
            fun showModalForCard(index: Int) {
                cardStack = cardStack.take(index + 1) // Keep cards up to the clicked one
                when (index) {
                    -1 -> {
                        // Reset to CreditAmountView
                        cardStack = listOf()
                        showEMIModal = false
                        showBankModal = false
                    }
                    0 -> {
                        // Reopen EMI modal
                        showEMIModal = true
                        showBankModal = false
                    }
                    1 -> {
                        // Reopen Bank Selection modal
                        showEMIModal = false
                        showBankModal = true
                    }
                }
            }

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp)
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.Top,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Display cards based on cardStack
                cardStack.forEachIndexed { index, cardIndex ->
                    var cardHeight by remember { mutableStateOf(0) }
                    when (cardIndex) {
                        0 -> {
                            CardView(
                                title = items[0].open_state?.body?.card?.header ?: "Credit Amount",
                                value = "₹$currentCreditAmount",
                                onArrowClick = { showModalForCard(-1) },
                                modifier = Modifier
                                    .onGloballyPositioned { layoutCoordinates ->
                                        cardHeight = layoutCoordinates.size.height
                                    }
                            )
                        }
                        1 -> {
                            CardView(
                                title = selectedEMI?.title ?: "EMI",
                                value = selectedEMI?.subtitle ?: "",
                                onArrowClick = { showModalForCard(0) },
                                modifier = Modifier
                                    .onGloballyPositioned { layoutCoordinates ->
                                        cardHeight = layoutCoordinates.size.height
                                    }
                            )
                        }
                        2 -> {
                            CardView(
                                title = "Bank Account",
                                value = selectedBankAccount,
                                onArrowClick = { showModalForCard(1) },
                                modifier = Modifier
                                    .onGloballyPositioned { layoutCoordinates ->
                                        cardHeight = layoutCoordinates.size.height
                                    }
                            )
                        }
                    }

                    // Update cardHeights list
                    if (cardHeights.size > index) {
                        cardHeights[index] = cardHeight
                    } else {
                        cardHeights.add(cardHeight)
                    }
                }

                // If no cards, show the Credit Amount View
                if (cardStack.isEmpty()) {
                    CreditAmountView(
                        creditAmount = currentCreditAmount,
                        onNext = {
                            cardStack = cardStack.plus(0)
                            showEMIModal = true
                        },
                        onCreditAmountChanged = { amount ->
                            currentCreditAmount = amount
                        },
                        item = items[0]
                    )
                }
            }

            if (showEMIModal) {
                val offsetYPx = cardHeights.take(1).sum().toFloat()
                val offsetY = with(density) { offsetYPx.toDp() }
                val modalHeight = screenHeightDp - offsetY

                EMISelectionView(
                    onSelectEMI = { emi ->
                        selectedEMI = emi
                        cardStack = cardStack.plus(1)
                        showEMIModal = false
                        showBankModal = true
                    },
                    itemData = items[1],
                    modifier = Modifier
                        .offset(y = offsetY+25.dp)
                        .fillMaxWidth()
                        .height(modalHeight)
                        .background(Color.White)
                )
            }

            if (showBankModal) {
                val offsetYPx = cardHeights.take(2).sum().toFloat()
                val offsetY = with(density) { offsetYPx.toDp() }
                val modalHeight = screenHeightDp - offsetY

                BankSelectionView(
                    onBankSelected = { bank ->
                        selectedBankAccount = bank
                        cardStack = cardStack.plus(2)
                        showBankModal = false
                    },
                    itemData = items[2],
                    modifier = Modifier
                        .offset(y = offsetY+25.dp)
                        .fillMaxWidth()
                        .height(modalHeight)
                        .background(Color.White)
                )
            }
        }
    } else {
        // Show error or loading indicator if items are not enough
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            Text(text = "Not enough data available.")
        }
    }
}
@Composable
fun CreditAmountView(
    creditAmount: Int,
    onNext: () -> Unit,
    onCreditAmountChanged: (Int) -> Unit,
    item: Item?
) {
    Column(
        modifier = Modifier
            .fillMaxHeight()
            .fillMaxWidth()
            .padding(16.dp)
    ) {
        val cardData = item?.open_state?.body?.card

        Text(
            text = cardData?.header ?: "Credit Amount",
            style = MaterialTheme.typography.h6,
            color = Color.White
        )
        Text(
            text = cardData?.description ?: "",
            style = MaterialTheme.typography.body2,
            color = Color.White
        )
        Spacer(modifier = Modifier.height(16.dp))

        Slider(
            value = creditAmount.toFloat(),
            onValueChange = { value ->
                onCreditAmountChanged(value.toInt())
            },
            valueRange = (cardData?.min_range?.toFloat() ?: 0f)..(cardData?.max_range?.toFloat()
                ?: 500000f),
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = "₹$creditAmount",
            style = MaterialTheme.typography.h4,
            color = Color.White,
            fontWeight = FontWeight.Bold
        )

        Spacer(modifier = Modifier.height(16.dp))

        Button(
            onClick = onNext,
            modifier = Modifier.fillMaxWidth(),
            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF6200EA))
        ) {
            Text(
                text = item?.cta_text ?: "Next",
                color = Color.White
            )
        }
    }
}
@Composable
fun EMISelectionView(
    onSelectEMI: (EmiItem) -> Unit,
    itemData: Item,
    modifier: Modifier = Modifier
) {
    val emiItems = itemData.open_state?.body?.items?.filterIsInstance<EmiItem>() ?: emptyList()

    Column(
        modifier = modifier
            .fillMaxWidth()
            .fillMaxHeight()
            .padding(16.dp)
            .verticalScroll(rememberScrollState())
    ) {
        Text(
            text = itemData.open_state?.body?.title ?: "How do you wish to repay?",
            style = MaterialTheme.typography.h6,
            color = Color.Black
        )
        Text(
            text = itemData.open_state?.body?.subtitle ?: "Choose one of the recommended plans",
            color = Color.Gray
        )

        Spacer(modifier = Modifier.height(16.dp))

        emiItems.forEach { emiItem ->
            Button(
                onClick = {
                    onSelectEMI(emiItem)
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("${emiItem.emi} for ${emiItem.duration}")
            }
            Spacer(modifier = Modifier.height(8.dp))
        }
    }
}

// Similarly update BankSelectionView

@Composable
fun BankSelectionView(
    onBankSelected: (String) -> Unit,
    itemData: Item,
    modifier: Modifier = Modifier
) {
    val bankItems = itemData.open_state?.body?.items ?: emptyList()

    Column(
        modifier = modifier
            .fillMaxWidth()
            .fillMaxHeight()
            .padding(16.dp)
            .verticalScroll(rememberScrollState())
    ) {
        Text(
            text = itemData.open_state?.body?.title ?: "Where should we send the money?",
            style = MaterialTheme.typography.h6,
            color = Color.Black
        )
        Text(
            text = itemData.open_state?.body?.subtitle ?: "Select your bank account",
            color = Color.Gray
        )

        Spacer(modifier = Modifier.height(16.dp))

        bankItems.forEach { bankItem ->
            val bankName = bankItem.title ?: ""
            Button(
                onClick = {
                    onBankSelected(bankName)
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(bankName)
            }
            Spacer(modifier = Modifier.height(8.dp))
        }

        Spacer(modifier = Modifier.height(16.dp))

        Button(
            onClick = { /* Do nothing for the last modal */ },
            modifier = Modifier.fillMaxWidth(),
            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF6200EA))
        ) {
            Text("Done", color = Color.White)
        }
    }
}

@Composable
fun CardView(
    title: String,
    value: String,
    onArrowClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column {
                Text(text = title, style = MaterialTheme.typography.body1)
                Text(text = value, style = MaterialTheme.typography.subtitle1)
            }
            IconButton(onClick = onArrowClick) {
                Icon(
                    imageVector = Icons.Default.KeyboardArrowDown,
                    contentDescription = "Modify",
                    tint = Color.Gray
                )
            }
        }
    }
}
