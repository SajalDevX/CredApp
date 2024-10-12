package me.mrsajal.credapp


import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.outlined.Check
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun CreditSelectionScreen() {
    var items by remember { mutableStateOf<List<Item?>>(emptyList()) }
    var loading by remember { mutableStateOf(true) }
    var error by remember { mutableStateOf(false) }
    var currentCreditAmount by remember { mutableStateOf(150000) } // Starting value
    var selectedAmount = 320
    // Fetch items from API
    LaunchedEffect(Unit) {
        try {
            val apiItems = fetchItemsFromApi()
            items = apiItems
            loading = false
        } catch (e: Exception) {
            error = true
            loading = false
        }
    }

    if (loading) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            CircularProgressIndicator()
        }
    } else if (error || items.isEmpty()) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            Text(text = "Error fetching data", color = Color.Red)
        }
    } else {

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
                .background(Color(0xFF1D1E2C)),
            verticalArrangement = Arrangement.Top,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            HeaderSection(
                title = items[0]?.open_state?.body?.title.orEmpty(),
                subtitle = items[0]?.open_state?.body?.subtitle.orEmpty()
            )

            Card(
                modifier = Modifier.background(color = Color.Gray).fillMaxHeight()
            ) {
                items[0]?.open_state?.body?.card?.let { card ->
                    CreditAmountSlider(
                        modifier = Modifier.padding(16.dp),
                    )
                }
                Button(modifier = Modifier
                    .height(48.dp)
                    .fillMaxWidth(), onClick = {}) {
                    Text("${items[0]?.cta_text}")
                }
                FooterText(text = items[0]?.open_state?.body?.footer.orEmpty())
            }
        }
    }
}


@Composable
fun HeaderSection(title: String, subtitle: String) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        IconButton(onClick = { /* Close action */ }) {
            Icon(Icons.Default.Close, contentDescription = "Close", tint = Color.White)
        }
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(
                text = title,
                fontSize = 20.sp,
                color = Color.White,
                modifier = Modifier.padding(bottom = 8.dp)
            )
            Text(
                text = subtitle,
                fontSize = 14.sp,
                color = Color.Gray
            )
        }
        IconButton(onClick = { /* Help action */ }) {
            Icon(Icons.Outlined.Check, contentDescription = "Help", tint = Color.White)
        }
    }
}


@Composable
fun FooterText(text: String) {
    Text(
        text = text,
        fontSize = 12.sp,
        color = Color.Gray,
        modifier = Modifier.padding(vertical = 16.dp),
        textAlign = TextAlign.Center
    )
}

