import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

enum class AmbientMode(val title: String, val icon: ImageVector) {
  NoiseCanceling("Noise Canceling", Icons.Default.Headset),
  WindReduction("Wind Noise Reduction", Icons.Default.Air),
  AmbientSound("Ambient Sound", Icons.Default.SurroundSound),
  Off("Off", Icons.Default.PowerSettingsNew)
}

@Composable
fun AmbientSoundControl(
    currentMode: AmbientMode,
    onModeSelected: (AmbientMode) -> Unit,
    ambientLevel: Float,
    onAmbientLevelChange: (Float) -> Unit,
    focusOnVoice: Boolean,
    onFocusOnVoiceChange: (Boolean) -> Unit,
    primaryColor: Color
) {
  val unselectedColor = Color.Gray.copy(alpha = 0.5f)

  val isAmbientActive = currentMode == AmbientMode.AmbientSound

  Column(modifier = Modifier.fillMaxWidth()) {
    Box(
        modifier = Modifier.fillMaxWidth().padding(vertical = 20.dp),
        contentAlignment = Alignment.Center) {
          // Box(
          //     modifier = Modifier
          //        .fillMaxWidth(0.85f)
          //        .height(2.dp)
          //        .background(unselectedColor)
          // )

          Row(
              modifier = Modifier.fillMaxWidth(),
              horizontalArrangement = Arrangement.SpaceBetween,
              verticalAlignment = Alignment.CenterVertically) {
                AmbientMode.values().forEach { mode ->
                  AmbientModeButton(
                      mode = mode,
                      isSelected = currentMode == mode,
                      primaryColor = primaryColor,
                      unselectedColor = unselectedColor,
                      onClick = { onModeSelected(mode) })
                }
              }
        }

    Spacer(modifier = Modifier.height(16.dp))

    Column(
        modifier =
            Modifier.fillMaxWidth()
                .alpha(if (isAmbientActive) 1f else 0.3f)
                .background(Color(0xFF1E1E2C))
                .padding(16.dp)) {
          Text(
              text = "Ambient Sound Level",
              color = Color.White,
              fontSize = 14.sp,
              fontWeight = FontWeight.Bold,
              modifier = Modifier.padding(bottom = 8.dp))

          Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(
                imageVector = Icons.Filled.VolumeUp,
                contentDescription = null,
                tint = unselectedColor,
                modifier = Modifier.size(20.dp))
            Spacer(modifier = Modifier.width(12.dp))

            Slider(
                value = ambientLevel,
                onValueChange = onAmbientLevelChange,
                valueRange = 1f..20f,
                steps = 19,
                enabled = isAmbientActive,
                colors =
                    SliderDefaults.colors(
                        thumbColor = primaryColor,
                        activeTrackColor = primaryColor,
                        inactiveTrackColor = unselectedColor,
                        disabledThumbColor = primaryColor.copy(alpha = 0.5f),
                        disabledActiveTrackColor = primaryColor.copy(alpha = 0.5f)),
                modifier = Modifier.weight(1f))

            Spacer(modifier = Modifier.width(12.dp))
            Text(
                text = "${ambientLevel.toInt()}",
                color = primaryColor,
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.width(20.dp))
          }

          Spacer(modifier = Modifier.height(16.dp))

          Row(
              modifier = Modifier.fillMaxWidth(),
              verticalAlignment = Alignment.CenterVertically,
              horizontalArrangement = Arrangement.SpaceBetween) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                  Icon(
                      imageVector = Icons.Default.RecordVoiceOver,
                      contentDescription = null,
                      tint = unselectedColor,
                      modifier = Modifier.size(20.dp))
                  Spacer(modifier = Modifier.width(12.dp))
                  Text(
                      text = "Focus on Voice",
                      color = Color.White,
                      fontSize = 14.sp,
                      fontWeight = FontWeight.Medium)
                }

                Switch(
                    checked = focusOnVoice,
                    onCheckedChange = onFocusOnVoiceChange,
                    enabled = isAmbientActive,
                    colors =
                        SwitchDefaults.colors(
                            checkedThumbColor = primaryColor,
                            checkedTrackColor = primaryColor.copy(alpha = 0.5f),
                            uncheckedThumbColor = Color.Gray,
                            uncheckedTrackColor = Color.DarkGray,
                            disabledCheckedThumbColor = primaryColor.copy(alpha = 0.5f),
                            disabledCheckedTrackColor = primaryColor.copy(alpha = 0.2f)))
              }
        }
  }
}

@Composable
fun AmbientModeButton(
    mode: AmbientMode,
    isSelected: Boolean,
    primaryColor: Color,
    unselectedColor: Color,
    onClick: () -> Unit
) {
  val circleColor = if (isSelected) primaryColor else Color(0xFF1E1E2C)

  Column(
      horizontalAlignment = Alignment.CenterHorizontally,
      modifier = Modifier.width(80.dp).clickable { onClick() }) {
        Box(
            modifier =
                Modifier.size(50.dp).clip(CircleShape).background(circleColor).clickable {
                  onClick()
                },
            contentAlignment = Alignment.Center) {
              if (!isSelected) {
                Box(
                    modifier =
                        Modifier.matchParentSize()
                            .background(Color.Transparent)
                            .padding(2.dp)
                            .clip(CircleShape)
                            .background(Color(0xFF252535)))
              }

              Icon(
                  imageVector = mode.icon,
                  contentDescription = mode.title,
                  tint = if (isSelected) Color.Black else unselectedColor,
                  modifier = Modifier.size(24.dp))
            }

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = mode.title,
            color = if (isSelected) primaryColor else unselectedColor,
            fontSize = 10.sp,
            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
            textAlign = TextAlign.Center,
            lineHeight = 12.sp,
            minLines = 2,
            maxLines = 2,
            modifier = Modifier.fillMaxWidth())
      }
}
