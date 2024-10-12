package me.mrsajal.credapp

import android.view.MotionEvent
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.*
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.*
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.input.pointer.pointerInteropFilter
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlin.math.*


@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun CreditAmountSlider(
    modifier: Modifier = Modifier,
    initialAmount: Float = 150000f,
    minAmount: Float = 100000f,
    maxAmount: Float = 500000f,
    stroke: Float = 20f,
    padding: Float = 40f,
    touchStroke: Float = 60f,
) {
    var width by remember { mutableStateOf(0) }
    var height by remember { mutableStateOf(0) }
    var center by remember { mutableStateOf(Offset.Zero) }
    var radius by remember { mutableStateOf(0f) }
    var appliedAngle by remember { mutableStateOf(0f) }
    var isDragging by remember { mutableStateOf(false) }
    var currentAmount by remember { mutableStateOf(initialAmount) }

    // Range of angle: 0 to 270 (3/4 of a circle)
    val angleRange = 360f
    val gradient = Brush.sweepGradient(
        listOf(Color(0xFFF58220), Color(0xFFE87116), Color(0xFFD8550E))
    )

    // Calculate dynamic amount based on the applied angle
    fun calculateAmount(angle: Float): Float {
        val normalizedAngle = angle / angleRange
        return minAmount + (maxAmount - minAmount) * normalizedAngle
    }

    fun angle(center: Offset, offset: Offset): Float {
        val rad = atan2(center.y - offset.y, center.x - offset.x)
        val deg = Math.toDegrees(rad.toDouble()).toFloat()
        return deg.let { if (it < 0) 360f + it else it }
    }

    fun clampAngle(angle: Float): Float {
        return angle.coerceIn(0f, angleRange)
    }

    fun distance(first: Offset, second: Offset): Float {
        return sqrt((first.x - second.x).pow(2) + (first.y - second.y).pow(2))
    }

    Box(
        modifier = modifier
            .height(300.dp)
            .fillMaxWidth()
            .padding(16.dp)
            .onGloballyPositioned {
                width = it.size.width
                height = it.size.height
                center = Offset(width / 2f, height / 2f)
                radius = min(width.toFloat(), height.toFloat()) / 2f - padding - stroke / 2f
            }
    ) {
        Canvas(
            modifier = Modifier.fillMaxSize().pointerInteropFilter {
                val offset = Offset(it.x, it.y)
                when (it.action) {
                    MotionEvent.ACTION_DOWN -> {
                        val d = distance(offset, center)
                        if (d in (radius - touchStroke / 2f)..(radius + touchStroke / 2f)) {
                            isDragging = true
                            appliedAngle = clampAngle(angle(center, offset) - 180f)
                            currentAmount = calculateAmount(appliedAngle)
                        }
                    }
                    MotionEvent.ACTION_MOVE -> {
                        if (isDragging) {
                            appliedAngle = clampAngle(angle(center, offset) - 180f)
                            currentAmount = calculateAmount(appliedAngle)
                        }
                    }
                    MotionEvent.ACTION_UP -> {
                        isDragging = false
                    }
                    else -> return@pointerInteropFilter false
                }
                true
            }
        ) {
            // Draw the full background arc
            drawArc(
                color = Color(0xFFFDD7BF), // Background arc color
                startAngle = -135f,
                sweepAngle = angleRange,
                useCenter = false,
                topLeft = center - Offset(radius, radius),
                size = Size(radius * 2, radius * 2),
                style = Stroke(width = stroke)
            )

            // Draw the orange progress arc
            drawArc(
                brush = gradient,
                startAngle = -135f,
                sweepAngle = appliedAngle,
                useCenter = false,
                topLeft = center - Offset(radius, radius),
                size = Size(radius * 2, radius * 2),
                style = Stroke(width = stroke)
            )

            // Draw the draggable thumb
            drawCircle(
                color = Color.Black,
                radius = 25f, // Thumb size
                center = center + Offset(
                    radius * cos((-135 + appliedAngle) * PI / 180f).toFloat(),
                    radius * sin((-135 + appliedAngle) * PI / 180f).toFloat()
                )
            )

            // Draw the inner circle in the thumb
            drawCircle(
                color = Color(0xFFEDEDED),
                radius = 10f,
                center = center + Offset(
                    radius * cos((-135 + appliedAngle) * PI / 180f).toFloat(),
                    radius * sin((-135 + appliedAngle) * PI / 180f).toFloat()
                )
            )
        }

        // Display the credit amount text in the center of the circle
        Column(
            modifier = Modifier
                .align(Alignment.Center)
                .padding(vertical = 32.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(text = "Credit Amount", fontSize = 24.sp)
            Text(
                text = "₹${currentAmount.roundToInt()}",
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold
            )
            Text(text = "1.04% monthly", fontSize = 20.sp, color = Color(0xFF4CAF50))
        }



    }
}
