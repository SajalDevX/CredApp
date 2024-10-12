package me.mrsajal.credapp


import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import me.mrsajal.credapp.ui.theme.CredAppTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            CredAppTheme {
                Surface(modifier = Modifier.fillMaxSize()) {
                    CreditSelectionScreen()
                }
            }
        }
    }
}

@Composable
fun MainScreen() {
    var items by remember { mutableStateOf<List<Item>?>(null) }
    var loading by remember { mutableStateOf(true) }
    var hasError by remember { mutableStateOf(false) }

    // Fetch data from API
    LaunchedEffect(Unit) {
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val fetchedItems = fetchItemsFromApi() // Fetch the data from the API
                withContext(Dispatchers.Main) {
                    items = fetchedItems
                    loading = false
                    hasError = fetchedItems.isEmpty()
                }
            } catch (e: Exception) {
                e.printStackTrace()
                withContext(Dispatchers.Main) {
                    loading = false
                    hasError = true
                }
            }
        }
    }

    if (loading) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            CircularProgressIndicator()
        }
    } else if (hasError) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            Text("Error loading data", color = Color.Red, fontSize = 18.sp)
        }
    } else {
        items?.let { StackFramework(it) } // Pass the items to the stack framework
    }
}

