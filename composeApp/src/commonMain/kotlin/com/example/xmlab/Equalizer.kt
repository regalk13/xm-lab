import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.*
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
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

data class ConsoleBand(
    val id: Int,
    val label: String,
    var gain: Float
)

@Composable
fun ConsoleEqualizerView() {
    val bands = remember {
        mutableStateListOf(
            ConsoleBand(0, "400", 2f),
            ConsoleBand(1, "1k", 4f),
            ConsoleBand(2, "2.5k", -1f),
            ConsoleBand(3, "6.3k", 3f),
            ConsoleBand(4, "16k", 5f)
        )
    }

    var clearBass by remember {
        mutableStateOf(0f)
    }

    val primaryColor = Color(0xFFDCA561)
    val bgColor = Color(0xFF252535)

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .border(3.dp, primaryColor, RectangleShape)
            .background(bgColor)
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        androidx.compose.material3.Text(
            text = "EQ",
            color = primaryColor,
            fontWeight = FontWeight.Bold,
            letterSpacing = 2.sp,
            modifier = Modifier.padding(bottom = 24.dp)
        )

        Row(
            modifier = Modifier.fillMaxWidth().height(300.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            bands.forEachIndexed { index, band ->
                VerticalFader(
                    gain = band.gain,
                    label = band.label,
                    color = primaryColor,
                    onValueChange = { newGain ->
                        bands[index] = bands[index].copy(gain = newGain)
                    }
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = "Clear Bass",
            color = Color.Gray,
            fontSize = 12.sp,
            letterSpacing = 2.sp
        )
        Spacer(modifier = Modifier.height(5.dp))

        ClearBassFader(
            value = clearBass,
            color = primaryColor,
            { clearBass = it }
        )
        Spacer(modifier = Modifier.height(30.dp))

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

    // Visual sizing
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
                    cap = androidx.compose.ui.graphics.StrokeCap.Round
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

        androidx.compose.material3.Text(
            text = label,
            style = TextStyle(color = Color.Gray, fontSize = 12.sp, fontWeight = FontWeight.Medium)
        )
        androidx.compose.material3.Text(
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
    val currentValue by rememberUpdatedState(value)

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(60.dp)
            .pointerInput(Unit) {
                var startVal = 0f
                var drag = 0f
                detectDragGestures(
                    onDragStart = {
                        startVal = currentValue
                        drag = 0f
                    },
                    onDrag = { change, dragAmount ->
                        change.consume()
                        drag += dragAmount.x

                        val percentage = drag / size.width
                        val changeVal = percentage * 20f

                        onValueChange((startVal + changeVal))
                    }
                )
            }
    ) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            val h = size.height
            val w = size.width
            val midY = h / 2
            val range = 20f

            drawLine(
                color = Color.Black.copy(alpha = 0.5f),
                start = Offset(0f, midY),
                end = Offset(w, midY),
                strokeWidth = 4.dp.toPx(),
                cap = StrokeCap.Round
            )

            val steps = 20

            for (i in 0..steps) {
                val x = (i.toFloat() / steps) * w
                val hSize = if (i == 10) 20f else 10f
                drawLine(
                    color = if (i == 10) color.copy(alpha = 0.5f) else Color.White.copy(alpha = 0.2f),
                    start = Offset(x, midY - hSize/2),
                    end = Offset(x, midY + hSize/2),
                    strokeWidth = 2f
                )
            }

            val normalized = (value + 10f) / range
            val knobX = normalized * w

            val knobS = 20.dp.toPx()

            drawCircle(
                color = Color.Black.copy(alpha = 0.5f),
                radius = knobS / 2,
                center = Offset(knobX, midY + 4f)
            )

            drawCircle(
                brush = Brush.radialGradient(
                    colors = listOf(color, color.copy(alpha = 0.8f))
                ),
                radius = knobS / 2,
                center = Offset(knobX, midY)
            )
        }
    }
}