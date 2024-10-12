package me.mrsajal.credapp


import androidx.cardview.widget.CardView
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.MaterialTheme
import androidx.compose.material.ModalBottomSheetLayout
import androidx.compose.material.ModalBottomSheetValue
import androidx.compose.material.Text
import androidx.compose.material.TextButton
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.outlined.Check
import androidx.compose.material.rememberModalBottomSheetState
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
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.launch
import kotlin.math.roundToInt


@Composable
fun CreditSelectionScreen() {
    var cardStack by remember { mutableStateOf(listOf<Int>()) }

    var currentCreditAmount by remember { mutableStateOf(150000) }
    var selectedEMI by remember { mutableStateOf("") }
    var selectedBankAccount by remember { mutableStateOf("") }

    var showEMIModal by remember { mutableStateOf(false) }
    var showBankModal by remember { mutableStateOf(false) }

    val cardHeights = remember { mutableStateListOf<Int>() }

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

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF1D1E2C))
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            verticalArrangement = Arrangement.Top,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Display cards based on cardStack
            cardStack.forEachIndexed { index, cardIndex ->
                var cardHeight by remember { mutableStateOf(0) }
                when (cardIndex) {
                    0 -> {
                        CardView(
                            title = "Credit Amount: ₹$currentCreditAmount",
                            onArrowClick = { showModalForCard(-1) }, // Updated here
                            modifier = Modifier
                                .onGloballyPositioned { layoutCoordinates ->
                                    cardHeight = layoutCoordinates.size.height
                                }
                        )
                    }
                    1 -> {
                        CardView(
                            title = "EMI: $selectedEMI",
                            onArrowClick = { showModalForCard(0) },
                            modifier = Modifier
                                .onGloballyPositioned { layoutCoordinates ->
                                    cardHeight = layoutCoordinates.size.height
                                }
                        )
                    }
                    2 -> {
                        CardView(
                            title = "Bank: $selectedBankAccount",
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

                )
            }
        }

        // Overlay modals
        val density = LocalDensity.current

        if (showEMIModal) {
            val offsetYPx = cardHeights.take(1).sum()-20
            val offsetY = with(density) { offsetYPx.toDp() }
            EMISelectionView(
                onSelectEMI = { emi ->
                    selectedEMI = emi
                    cardStack = cardStack.plus(1)
                    showEMIModal = false
                    showBankModal = true
                },
                modifier = Modifier
                    .offset(y = offsetY+20.dp)
                    .fillMaxSize()
                    .background(Color.White)
            )
        }

        if (showBankModal) {
            val offsetYPx = cardHeights.take(2).sum()
            val offsetY = with(density) { offsetYPx.toDp() }
            BankSelectionView(
                onBankSelected = { bank ->
                    selectedBankAccount = bank
                    cardStack = cardStack.plus(2)
                    showBankModal = false
                },
                modifier = Modifier
                    .offset(y = offsetY+20.dp)
                    .fillMaxSize()
                    .background(Color.White)
            )
        }
    }
}
@Composable
fun CreditAmountView(creditAmount: Int, onNext: () -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
    ) {
        Text(text = "Credit Amount", style = MaterialTheme.typography.h6, color = Color.White)
        // Circular Slider
        // (Assuming CreditAmountSlider is a custom Composable you've defined)
        CreditAmountSlider(
            modifier = Modifier.padding(16.dp),
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
            Text("Next", color = Color.White)
        }
    }
}

@Composable
fun EMISelectionView(
    onSelectEMI: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(16.dp)
    ) {
        Text(text = "How do you wish to repay?", style = MaterialTheme.typography.h6, color = Color.Black)
        Text(text = "Choose one of the recommended plans", color = Color.Gray)

        Spacer(modifier = Modifier.height(16.dp))

        // EMI options (mocked)
        val emiPlans = listOf("₹4,247 /mo for 12 months", "₹5,580 /mo for 9 months", "₹8,270 /mo for 6 months")
        emiPlans.forEach { emi ->
            Button(
                onClick = {
                    onSelectEMI(emi)
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(emi)
            }
            Spacer(modifier = Modifier.height(8.dp))
        }
    }
}

@Composable
fun BankSelectionView(
    onBankSelected: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(16.dp)
    ) {
        Text(text = "Where should we send the money?", style = MaterialTheme.typography.h6, color = Color.Black)
        Text(text = "Select your bank account", color = Color.Gray)

        Spacer(modifier = Modifier.height(16.dp))

        // Bank options (mocked)
        val bankAccounts = listOf("HDFC BANK", "SBI", "PNB")
        bankAccounts.forEach { bank ->
            Button(
                onClick = {
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(bank)
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
            Text(text = title, style = MaterialTheme.typography.body1)
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
