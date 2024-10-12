package me.mrsajal.credapp

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun StackFramework(
    items: List<Item>,  // The items fetched from the API
    modifier: Modifier = Modifier
) {
    var expandedIndex by remember { mutableStateOf(-1) }

    val stackItems = items.take(4).takeIf { it.size >= 2 } ?: emptyList()

    Column(
        modifier = modifier.fillMaxSize(),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        stackItems.forEachIndexed { index, item ->
            StackItem(
                item = item,
                isExpanded = index == expandedIndex,
                onToggle = {
                    expandedIndex = if (expandedIndex == index) -1 else index
                }
            )
        }
    }
}
@Composable
fun StackItem(
    item: Item,           // The item representing the content
    isExpanded: Boolean,  // Whether this item is expanded
    onToggle: () -> Unit  // Callback when toggling the state
) {
    Card(
        shape = RoundedCornerShape(12.dp),
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onToggle() },  // Toggle between expanded and collapsed
        elevation = CardDefaults.cardElevation(4.dp)
    ) {
        if (isExpanded) {
            ExpandedView(item)
        } else {
            CollapsedView(item)
        }
    }
}


@Composable
fun ExpandedView(item: Item) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
            .background(Color.Gray)
    ) {
        Text(text = item.open_state?.body?.title ?: "", fontSize = 22.sp, color = Color.White)
        Text(text = item.open_state?.body?.subtitle ?: "", fontSize = 18.sp, color = Color.White)

        // Additional content for expanded view, such as a list or buttons
        item.open_state?.body?.items?.forEach { emiItem ->
            Text(text = emiItem.title ?: "", fontSize = 18.sp, color = Color.White)
            Text(text = emiItem.subtitle ?: "", fontSize = 14.sp, color = Color.White)
        }

        // Footer (e.g., button)
        item.open_state?.body?.footer?.let {
            Button(onClick = { /* Handle Action */ }) {
                Text(text = it)
            }
        }
    }
}

@Composable
fun CollapsedView(item: Item) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
            .background(Color.LightGray)
    ) {
        Text(text = item.closed_state?.body?.key1 ?: "Collapsed View", fontSize = 18.sp, color = Color.Black)
    }
}
