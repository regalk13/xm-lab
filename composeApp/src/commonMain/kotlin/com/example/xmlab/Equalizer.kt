import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlin.math.roundToInt

data class ConsoleBand(
    val id: Int,
    val label: String,
    var gain: Float
)

enum class EqPreset(val title: String, val gains: List<Float>) {
    Flat("Flat", listOf(0f, 0f, 0f, 0f, 0f, 0f)),
    BassBoost("Bass Boosted", listOf(8f, 5f, 0f, 0f, -2f, 5f)),
    Vibrant("Vibrant", listOf(4f, -2f, -4f, 3f, 6f, 8f)),
    Vocal("Voice / Spoken", listOf(-5f, -2f, 5f, 4f, -3f, 2f))
}

@Composable
fun ConsoleEqualizerView() {
    val bands = remember {
        mutableStateListOf(
            ConsoleBand(0, "400", 0f),
            ConsoleBand(1, "1k", 0f),
            ConsoleBand(2, "2.5k", 0f),
            ConsoleBand(3, "6.3k", 0f),
            ConsoleBand(4, "16k", 0f)
        )
    }

    var clearBass by remember { mutableStateOf(0f) }

    var showMenu by remember { mutableStateOf(false) }
    var currentPresetName by remember { mutableStateOf("Flat") }

    val primaryColor = Color(0xFFDCA561)
    val bgColor = Color(0xFF252535)
    val darkPanelColor = Color(0xFF1E1E2C)

    Row(
        modifier = Modifier.
        fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            text = "Equalizer",
            color = primaryColor,
            fontWeight = FontWeight.Bold,
            fontSize = 18.sp,
            letterSpacing = 0.5.sp
        )

        Box(
            contentAlignment = Alignment.Center
        ) {
            Row(
                modifier = Modifier
                    .background(darkPanelColor, RoundedCornerShape(8.dp))
                    .border(2.dp, primaryColor.copy(alpha = 0.5f), RectangleShape)
                    .clickable { showMenu = true }
                    .padding(horizontal = 16.dp, vertical = 8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = currentPresetName,
                    color = primaryColor,
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp,
                    letterSpacing = 1.sp
                )
                // Spacer(modifier = Modifier.width(8.dp))
            }

            DropdownMenu(
                expanded = showMenu,
                onDismissRequest = { showMenu = false },
                modifier = Modifier.background(darkPanelColor)
            ) {
                EqPreset.values().forEach { preset ->
                    DropdownMenuItem(
                        text = { Text(preset.title, color = Color.White) },
                        onClick = {
                            currentPresetName = preset.title
                            preset.gains.forEachIndexed { index, gain ->
                                if (index < bands.size) {
                                    bands[index] = bands[index].copy(gain = gain)
                                }
                            }
                            if (preset.gains.size > 5) {
                                clearBass = preset.gains[5]
                            }
                            showMenu = false
                        }
                    )
                }
            }
        }

    }


    Spacer(modifier = Modifier.height(15.dp))

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .border(3.dp, primaryColor, RectangleShape)
            .background(bgColor)
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(300.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            bands.forEachIndexed { index, band ->
                VerticalFader(
                    gain = band.gain,
                    label = band.label,
                    color = primaryColor,
                    onValueChange = { newGain ->
                        currentPresetName = "Custom"
                        bands[index] = bands[index].copy(gain = newGain)
                    }
                )
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        Text(
            text = "CLEAR BASS",
            color = Color.Gray,
            fontSize = 12.sp,
            letterSpacing = 2.sp,
            fontWeight = FontWeight.Bold
        )
        Spacer(modifier = Modifier.height(2.dp))

        ClearBassFader(
            value = clearBass,
            color = primaryColor,
            onValueChange = { clearBass = it }
        )

        Spacer(modifier = Modifier.height(16.dp))
    }
}


@Composable
fun VerticalFader(
    gain: Float,
    label: String,
    color: Color,
    onValueChange: (Float) -> Unit
) {
    val currentGain by rememberUpdatedState(gain)

    val maxGain = 15f
    val minGain = -15f
    val range = maxGain - minGain

    val knobHeight = 30.dp
    val knobWidth = 20.dp

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.width(50.dp).fillMaxHeight()
    ) {
        Box(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
                .pointerInput(Unit) {
                    var startDragGain = 0f
                    var dragAccumulatorY = 0f

                    detectDragGestures(
                        onDragStart = {
                            startDragGain = currentGain
                            dragAccumulatorY = 0f
                        },
                        onDrag = { change, dragAmount ->
                            change.consume()
                            dragAccumulatorY += dragAmount.y

                            val percentageMoved = dragAccumulatorY / size.height
                            val dbChange = percentageMoved * range * -1
                            val newValue = (startDragGain + dbChange).coerceIn(minGain, maxGain)
                            onValueChange(newValue)
                        }
                    )
                }
        ) {
            Canvas(modifier = Modifier.fillMaxSize()) {
                val w = size.width
                val h = size.height
                val centerX = w / 2

                drawLine(
                    color = Color.Black.copy(alpha = 0.5f),
                    start = Offset(centerX, 0f),
                    end = Offset(centerX, h),
                    strokeWidth = 4.dp.toPx(),
                    cap = StrokeCap.Round
                )

                val steps = 10
                for (i in 0..steps) {
                    val y = (i.toFloat() / steps) * h
                    val isZero = i == 5
                    val lineWidth = if (isZero) 20f else 10f
                    val lineColor = if (isZero) color.copy(alpha = 0.5f) else Color.White.copy(alpha = 0.1f)

                    drawLine(
                        color = lineColor,
                        start = Offset(centerX - (lineWidth/2), y),
                        end = Offset(centerX + (lineWidth/2), y),
                        strokeWidth = 2f
                    )
                }

                val normalizedGain = 1f - ((gain - minGain) / range)
                val capYCenter = normalizedGain * h
                val capWidthPx = knobWidth.toPx()
                val capHeightPx = knobHeight.toPx()

                drawRoundRect(
                    color = Color.Black.copy(alpha = 0.6f),
                    topLeft = Offset(centerX - capWidthPx/2, capYCenter - capHeightPx/2 + 5f),
                    size = Size(capWidthPx, capHeightPx),
                    cornerRadius = CornerRadius(4.dp.toPx())
                )

                drawRoundRect(
                    brush = Brush.horizontalGradient(
                        colors = listOf(
                            color.copy(alpha = 0.8f),
                            color,
                            color.copy(alpha = 0.8f)
                        )
                    ),
                    topLeft = Offset(centerX - capWidthPx/2, capYCenter - capHeightPx/2),
                    size = Size(capWidthPx, capHeightPx),
                    cornerRadius = CornerRadius(4.dp.toPx())
                )

                drawLine(
                    color = Color.White.copy(alpha = 0.8f),
                    start = Offset(centerX - capWidthPx/2 + 4f, capYCenter),
                    end = Offset(centerX + capWidthPx/2 - 4f, capYCenter),
                    strokeWidth = 2f
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = label,
            style = TextStyle(color = Color.Gray, fontSize = 12.sp, fontWeight = FontWeight.Medium)
        )
        Text(
            text = "${gain.toInt()}dB",
            style = TextStyle(color = color, fontSize = 10.sp)
        )
    }
}

@Composable
fun ClearBassFader(
    value: Float,
    color: Color,
    onValueChange: (Float) -> Unit
) {
    val haptics = LocalHapticFeedback.current
    val currentValue by rememberUpdatedState(value)

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(60.dp)
            .pointerInput(Unit) {
                var startVal = 0f
                var dragAccumulator = 0f
                var lastHapticValue = 0f

                detectDragGestures(
                    onDragStart = {
                        startVal = currentValue
                        dragAccumulator = 0f
                        lastHapticValue = currentValue
                    },
                    onDrag = { change, dragAmount ->
                        change.consume()
                        dragAccumulator += dragAmount.x

                        val percentage = dragAccumulator / size.width
                        val rawChange = percentage * 20f

                        val exactValue = startVal + rawChange
                        val snappedValue = exactValue.roundToInt().toFloat().coerceIn(-10f, 10f)

                        if (snappedValue != lastHapticValue) {
                            haptics.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                            lastHapticValue = snappedValue
                            onValueChange(snappedValue)
                        }
                    }
                )
            }
    ) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            val h = size.height
            val w = size.width
            val midY = h / 2

            val padding = 12.dp.toPx()
            val trackWidth = w - (padding * 2)

            drawLine(
                color = Color.Black.copy(alpha = 0.5f),
                start = Offset(padding, midY),
                end = Offset(w - padding, midY),
                strokeWidth = 4.dp.toPx(),
                cap = StrokeCap.Round
            )

            val steps = 20
            for (i in 0..steps) {
                val x = padding + (i.toFloat() / steps) * trackWidth
                val isCenter = i == 10
                val hSize = if (isCenter) 20f else 10f
                val tickColor = if (isCenter) color.copy(alpha = 0.5f) else Color.White.copy(alpha = 0.2f)

                drawLine(
                    color = tickColor,
                    start = Offset(x, midY - hSize/2),
                    end = Offset(x, midY + hSize/2),
                    strokeWidth = 2f
                )
            }

            val range = 20f
            val normalized = (value + 10f) / range
            val knobX = padding + (normalized * trackWidth)
            val knobSize = 24.dp.toPx()

            drawCircle(
                color = Color.Black.copy(alpha = 0.5f),
                radius = knobSize / 2,
                center = Offset(knobX, midY + 4f)
            )

            drawCircle(
                brush = Brush.radialGradient(
                    colors = listOf(color, color.copy(alpha = 0.8f))
                ),
                radius = knobSize / 2,
                center = Offset(knobX, midY)
            )

            drawCircle(
                color = Color.White,
                radius = 3.dp.toPx(),
                center = Offset(knobX, midY)
            )
        }
    }
}