package com.example.xmlab

import AmbientMode
import AmbientSoundControl
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.Speed
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import org.jetbrains.compose.ui.tooling.preview.Preview

object Kanagawa {
  val SumiInk0 = Color(0xFF16161D)
  val SumiInk1 = Color(0xFF1F1F28)
  val SumiInk2 = Color(0xFF2A2A37)
  val SumiInk4 = Color(0xFF54546D)
  val FujiWhite = Color(0xFFDCD7BA)
  val FujiGray = Color(0xFF727169)
  val AutumnYellow = Color(0xFFDCA561)
}

val myDevices = listOf("Sony WH-1000XM4", "WF-1000XM5", "LinkBuds S")

@Composable
@Preview
fun App() {
  val bands = remember {
    mutableStateListOf(
        ConsoleBand(0, "400", 0f),
        ConsoleBand(1, "1k", 0f),
        ConsoleBand(2, "2.5k", 0f),
        ConsoleBand(3, "6.3k", 0f),
        ConsoleBand(4, "16k", 0f))
  }

  var currentScreen by remember { mutableStateOf("home") }

  MaterialTheme {
    if (currentScreen == "home") {
      HomeScreen(bands = bands, onNavigateToEq = { currentScreen = "eq" })
    } else {
      Column(modifier = Modifier.fillMaxSize().background(Kanagawa.SumiInk0)) {
        Spacer(modifier = Modifier.height(15.dp))
        Box(
            modifier = Modifier.fillMaxWidth().padding(16.dp),
            contentAlignment = Alignment.CenterStart) {
              IconButton(onClick = { currentScreen = "home" }) {
                Icon(Icons.Default.ArrowBack, "Back", tint = Kanagawa.FujiWhite)
              }
              Text(
                  "Equalizer",
                  color = Kanagawa.FujiWhite,
                  fontSize = 20.sp,
                  fontWeight = FontWeight.Bold,
                  modifier = Modifier.align(Alignment.Center))
            }

        ConsoleEqualizerView(bands = bands)
      }
    }
  }
}

@Composable
fun HomeScreen(bands: List<ConsoleBand>, onNavigateToEq: () -> Unit) {
  var showMenu by remember { mutableStateOf(false) }
  var currentDeviceName by remember { mutableStateOf(myDevices.first()) }
  var ambientMode by remember { mutableStateOf(AmbientMode.AmbientSound) }
  var ambientLevel by remember { mutableStateOf(10f) }
  var focusOnVoice by remember { mutableStateOf(false) }

  var audioUpsampling by remember { mutableStateOf(false) }
  var touchSensor by remember { mutableStateOf(true) }
  var pauseWhenTakenOff by remember { mutableStateOf(true) }
  var notifications by remember { mutableStateOf(true) }

  Column(
      modifier =
          Modifier.background(Kanagawa.SumiInk0)
              .fillMaxSize()
              .padding(24.dp)
              .verticalScroll(rememberScrollState()),
      horizontalAlignment = Alignment.CenterHorizontally,
  ) {
    Spacer(modifier = Modifier.height(15.dp))

    DeviceSelector(
        currentDevice = currentDeviceName,
        isMenuExpanded = showMenu,
        onExpandChange = { showMenu = it },
        onDeviceSelected = { currentDeviceName = it })

    Spacer(modifier = Modifier.height(15.dp))

    SectionTitle("Ambient Sound Control")
    Spacer(modifier = Modifier.height(8.dp))

    AmbientSoundControl(
        currentMode = ambientMode,
        onModeSelected = { ambientMode = it },
        ambientLevel = ambientLevel,
        onAmbientLevelChange = { ambientLevel = it },
        focusOnVoice = focusOnVoice,
        onFocusOnVoiceChange = { focusOnVoice = it },
        primaryColor = Kanagawa.AutumnYellow)

    SectionDivider()

    SectionTitle("Noise Canceling Optimizer")

    SettingsItem(Icons.Outlined.Speed, "Atmospheric pressure", "0.90atm", {})
    SettingsItem(
        Icons.Filled.Stars, "Optimize", "Click to start the noise cancelling optimizer.", {})
    SettingsItem(
        Icons.Default.RecordVoiceOver,
        "Speak to Chat",
        "Pauses audio when you speak.",
        {},
        showChevron = true)

    SectionDivider()

    SectionTitle("Audio Source")

    SettingsItemToggle(
        Icons.Default.Extension, "Audio Upsampling", "DSEE Extreme", audioUpsampling) {
          audioUpsampling = it
        }
    SettingsItem(
        Icons.Default.Notifications,
        "Calls and Notifications",
        "Manage voice guidance and ringtones.",
        {},
        showChevron = true)

    SectionDivider()

    SectionTitle("Equalizer")

    Box(
        modifier =
            Modifier.fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 8.dp)
                .background(Kanagawa.SumiInk2, RoundedCornerShape(12.dp))
                .border(1.dp, Kanagawa.SumiInk4, RoundedCornerShape(12.dp))
                .clickable { onNavigateToEq() }
                .padding(16.dp)) {
          Column {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically) {
                  Column {
                    Text(
                        "Equalizer",
                        color = Kanagawa.FujiWhite,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold)
                    Text("Custom 1", color = Kanagawa.FujiGray, fontSize = 12.sp)
                  }
                  Icon(Icons.Default.ChevronRight, null, tint = Kanagawa.FujiGray)
                }

            Spacer(modifier = Modifier.height(16.dp))

            MiniEqWave(
                bands = bands,
                color = Kanagawa.AutumnYellow,
                modifier = Modifier.fillMaxWidth().height(50.dp))
          }
        }

    SectionDivider()

    SectionTitle("System")

    SettingsItemToggle(Icons.Default.TouchApp, "Touch sensor control", isChecked = touchSensor) {
      touchSensor = it
    }
    SettingsItemToggle(
        Icons.Default.Pause,
        "Pause when taken off",
        "Pauses audio when headphones are removed.",
        pauseWhenTakenOff) {
          pauseWhenTakenOff = it
        }
    SettingsItem(
        Icons.Default.PowerSettingsNew,
        "Automatic Power Off",
        "When taken off",
        {},
        showChevron = true)
    SettingsItemToggle(
        Icons.Default.Notifications, "Notifications & Voice Guide", isChecked = notifications) {
          notifications = it
        }
    SettingsItem(Icons.Default.Build, "Developer settings", onClick = {}, showChevron = true)
    SettingsItem(Icons.Default.CompareArrows, "Connection", onClick = {}, showChevron = true)
    SettingsItem(Icons.Default.BatteryFull, "Battery", onClick = {}, showChevron = true)

    Spacer(modifier = Modifier.height(40.dp))
  }
}

@Composable
fun MiniEqWave(bands: List<ConsoleBand>, color: Color, modifier: Modifier = Modifier) {
  Canvas(modifier = modifier) {
    val width = size.width
    val height = size.height
    val midY = height / 2
    val path = Path()
    val stepX = width / (bands.size - 1)
    fun getY(gain: Float): Float {
      val normalized = (gain + 15f) / 30f
      return height - (normalized * height)
    }
    bands.forEachIndexed { index, band ->
      val x = index * stepX
      val y = getY(band.gain)
      if (index == 0) path.moveTo(x, y)
      else {
        val prevX = (index - 1) * stepX
        val prevY = getY(bands[index - 1].gain)
        val controlX1 = prevX + (stepX / 2)
        val controlX2 = x - (stepX / 2)
        path.cubicTo(controlX1, prevY, controlX2, y, x, y)
      }
    }
    drawPath(path, color, style = Stroke(width = 2.dp.toPx(), cap = StrokeCap.Round))
    drawLine(
        color = color.copy(alpha = 0.2f),
        start = Offset(0f, midY),
        end = Offset(width, midY),
        strokeWidth = 1.dp.toPx())
  }
}

@Composable
fun SectionTitle(text: String) {
  Text(
      text = text,
      color = Kanagawa.FujiWhite,
      fontWeight = FontWeight.Bold,
      fontSize = 18.sp,
      letterSpacing = 0.5.sp,
      textAlign = TextAlign.Start,
      modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 8.dp))
}

@Composable
fun SectionDivider() {
  Column {
    Spacer(modifier = Modifier.height(16.dp))
    HorizontalDivider(
        color = Kanagawa.SumiInk4,
        thickness = 1.dp,
        modifier = Modifier.padding(horizontal = 16.dp))
    Spacer(modifier = Modifier.height(16.dp))
  }
}

@Composable
fun SettingsItem(
    icon: ImageVector,
    title: String,
    subtitle: String? = null,
    onClick: () -> Unit,
    showChevron: Boolean = false
) {
  Row(
      modifier =
          Modifier.fillMaxWidth()
              .clickable { onClick() }
              .padding(vertical = 12.dp, horizontal = 16.dp),
      verticalAlignment = Alignment.CenterVertically) {
        Icon(icon, null, tint = Kanagawa.FujiGray, modifier = Modifier.size(24.dp))
        Spacer(modifier = Modifier.width(16.dp))
        Column(modifier = Modifier.weight(1f)) {
          Text(
              title, color = Kanagawa.FujiWhite, fontSize = 15.sp, fontWeight = FontWeight.SemiBold)
          if (subtitle != null) {
            Text(
                subtitle,
                color = Kanagawa.FujiGray,
                fontSize = 13.sp,
                lineHeight = 16.sp,
                fontWeight = FontWeight.Normal)
          }
        }
        if (showChevron) {
          Icon(Icons.Default.ChevronRight, null, tint = Kanagawa.SumiInk4)
        }
      }
}

@Composable
fun SettingsItemToggle(
    icon: ImageVector,
    title: String,
    subtitle: String? = null,
    isChecked: Boolean,
    onCheckedChange: (Boolean) -> Unit
) {
  Row(
      modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp, horizontal = 16.dp),
      verticalAlignment = Alignment.CenterVertically) {
        Icon(icon, null, tint = Kanagawa.FujiGray, modifier = Modifier.size(24.dp))
        Spacer(modifier = Modifier.width(16.dp))
        Column(modifier = Modifier.weight(1f)) {
          Text(
              title, color = Kanagawa.FujiWhite, fontSize = 15.sp, fontWeight = FontWeight.SemiBold)
          if (subtitle != null) {
            Text(
                subtitle,
                color = Kanagawa.FujiGray,
                fontSize = 13.sp,
                fontWeight = FontWeight.Normal)
          }
        }
        Switch(
            checked = isChecked,
            onCheckedChange = onCheckedChange,
            colors =
                SwitchDefaults.colors(
                    checkedThumbColor = Kanagawa.AutumnYellow,
                    checkedTrackColor = Kanagawa.AutumnYellow.copy(alpha = 0.5f),
                    uncheckedThumbColor = Kanagawa.FujiGray,
                    uncheckedTrackColor = Kanagawa.SumiInk2,
                    uncheckedBorderColor = Kanagawa.SumiInk4))
      }
}

@Composable
fun DeviceSelector(
    currentDevice: String,
    isMenuExpanded: Boolean,
    onExpandChange: (Boolean) -> Unit,
    onDeviceSelected: (String) -> Unit
) {
  Box(contentAlignment = Alignment.Center) {
    Row(
        modifier =
            Modifier.background(Kanagawa.SumiInk2)
                .border(2.dp, Kanagawa.AutumnYellow.copy(alpha = 0.5f))
                .clickable { onExpandChange(true) }
                .padding(horizontal = 16.dp, vertical = 10.dp),
        verticalAlignment = Alignment.CenterVertically) {
          Text(
              currentDevice,
              color = Kanagawa.AutumnYellow,
              fontWeight = FontWeight.Bold,
              fontSize = 16.sp,
              letterSpacing = 1.sp)
          Spacer(modifier = Modifier.width(8.dp))
          Icon(Icons.Default.ArrowDropDown, "Select", tint = Kanagawa.AutumnYellow)
        }

    DropdownMenu(
        expanded = isMenuExpanded,
        onDismissRequest = { onExpandChange(false) },
        modifier = Modifier.background(Kanagawa.SumiInk2).border(1.dp, Kanagawa.SumiInk4)) {
          myDevices.forEach { device ->
            DropdownMenuItem(
                text = { Text(device, color = Kanagawa.FujiWhite) },
                onClick = {
                  onDeviceSelected(device)
                  onExpandChange(false)
                })
          }
          HorizontalDivider(color = Kanagawa.SumiInk4)
          DropdownMenuItem(
              text = {
                Row(verticalAlignment = Alignment.CenterVertically) {
                  Icon(
                      Icons.Default.Add,
                      null,
                      tint = Kanagawa.AutumnYellow,
                      modifier = Modifier.size(18.dp))
                  Spacer(modifier = Modifier.width(8.dp))
                  Text(
                      "Add New Device", color = Kanagawa.AutumnYellow, fontWeight = FontWeight.Bold)
                }
              },
              onClick = { onExpandChange(false) })
        }
  }
}
