package me.mrsajal.credapp

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.Icon
import androidx.compose.material.MaterialTheme
import androidx.compose.material.OutlinedButton
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Check
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.Button
import androidx.compose.material3.CardDefaults
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.zIndex
import me.mrsajal.credapp.EmiItem
import me.mrsajal.credapp.Item

@Composable
fun EMISelectionView(
    onSelectEMI: (EmiItem) -> Unit,
    itemData: Item,
    modifier: Modifier = Modifier
) {
    val emiItems = itemData.open_state?.body?.items?.filterIsInstance<EmiItem>() ?: emptyList()
    var selectedEmiItem by remember {
        mutableStateOf<EmiItem?>(
            itemData.open_state?.body?.items?.get(
                0
            )
        )
    }

    Column(
        modifier = modifier
            .fillMaxWidth()
            .fillMaxHeight()
            .padding(16.dp)
            .verticalScroll(rememberScrollState())
    ) {
        // Title and subtitle for EMI selection
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

        // EMI Plan Cards in LazyRow
        LazyRow(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(emiItems) { emiItem ->
                EMIPlanCard(
                    emiItem = emiItem,
                    isSelected = selectedEmiItem == emiItem,
                    onClick = {
                        selectedEmiItem = emiItem
                    }
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        OutlinedButton(onClick = {}) {
            itemData.open_state?.body?.footer?.let { Text(text = it, color = Color.Black) }

        }

        Button(
            onClick = {
                selectedEmiItem?.let {
                    onSelectEMI(it) // Proceed only if an EMI plan is selected
                }
            },
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp),
            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF6DA1F2)),
        ) {
            Text(text = "Proceed", color = Color.White)
        }
    }
}

@Composable
fun EMIPlanCard(
    emiItem: EmiItem,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    val colorOptions = listOf(Color(0xFF4A2C29), Color(0xFF2A2D34), Color(0xFF3B3B3B))
    val randomColor = remember { colorOptions.random() }
    Box(
        modifier = Modifier
            .padding(8.dp)
            .clickable { onClick() }
    ) {
        Card(
            modifier = Modifier
                .width(150.dp).zIndex(2f)
                .clip(RoundedCornerShape(16.dp))
                .background(Color.Red) // Use random color when selected
                .border(
                    width = if (isSelected) 2.dp else 1.dp,
                    color = if (isSelected) Color(0xFFDAB883) else Color(0xFF4F5359),
                    shape = RoundedCornerShape(16.dp)
                ), colors = CardDefaults.cardColors(
                containerColor = randomColor // Set random color as card background color
            ),
            elevation = CardDefaults.cardElevation(4.dp)
        ) {
            Column(
                modifier = Modifier
                    .padding(16.dp)
                    .fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.SpaceEvenly
            ) {
                // Circle for the tick icon
                Box(
                    modifier = Modifier
                        .size(36.dp) // Fixed size for the circle
                        .clip(RoundedCornerShape(12.dp)) // Rounded circle shape
                        .border(2.dp, Color.Gray, RoundedCornerShape(120.dp)) // Border for circle
                        .background(Color.Transparent) // Background of circle
                        .align(Alignment.CenterHorizontally),
                    contentAlignment = Alignment.Center
                ) {
                    // Show tick icon only if the card is selected
                    if (isSelected) {
                        Icon(
                            imageVector = Icons.Outlined.Check,
                            contentDescription = "Selected",
                            tint = Color.White,
                            modifier = Modifier.size(26.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(16.dp)) // Space between the circle and the content

                // Display EMI amount
                Text(
                    text = emiItem.emi ?: "",
                    color = Color.White,
                    fontSize = 16.sp,
                    fontWeight = if (isSelected) androidx.compose.ui.text.font.FontWeight.Bold else androidx.compose.ui.text.font.FontWeight.Normal
                )

                Spacer(modifier = Modifier.height(4.dp))

                // Display duration
                Text(
                    text = emiItem.duration ?: "",
                    color = Color.Gray,
                    fontSize = 12.sp
                )

                Spacer(modifier = Modifier.height(4.dp))

                // Optional "See calculations" or other subtitle
                Text(
                    text = emiItem.subtitle ?: "",
                    color = Color.Gray,
                    fontSize = 10.sp
                )
            }
        }

        // The tag that appears half outside the card
        emiItem.tag?.let {
            Box(
                modifier = Modifier
                    .offset(y = (-10).dp) // Move it half outside the top of the card
                    .align(Alignment.TopCenter) // Center it horizontally
                    .zIndex(2f)
            ) {
                Text(
                    text = it,
                    fontSize = 10.sp,
                    color = Color.White,
                    modifier = Modifier
                        .background(Color(0xFF91C788), shape = RoundedCornerShape(12.dp))
                        .padding(horizontal = 6.dp, vertical = 2.dp)
                )
            }
        }
    }
}
