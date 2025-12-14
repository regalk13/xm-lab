package com.example.xmlab

import AmbientMode
import AmbientSoundControl
import ConsoleEqualizerView
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.layout.ModifierLocalBeyondBoundsLayout
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import org.jetbrains.compose.ui.tooling.preview.Preview

val myDevices = listOf("Sony WH-1000XM4", "WF-1000XM5", "LinkBuds S")

@Composable
@Preview
fun App() {
    val primaryColor = Color(0xFFDCA561)
    val darkPanelColor = Color(0xFF1E1E2C)

    var showMenu by remember { mutableStateOf(false) }
    var currentDeviceName by remember { mutableStateOf(myDevices.first()) }

    var ambientMode by remember { mutableStateOf(AmbientMode.AmbientSound) }
    var ambientLevel by remember { mutableStateOf(10f) }
    var focusOnVoice by remember { mutableStateOf(false) }

    MaterialTheme {
        Column(
            modifier = Modifier
                .background(Color(0xFF16161D))
                .safeContentPadding()
                .fillMaxSize()
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Spacer(modifier = Modifier.height(24.dp))
            Box(contentAlignment = Alignment.Center) {
                Row(
                    modifier = Modifier
                        .background(darkPanelColor)
                        .border(2.dp, primaryColor.copy(alpha = 0.5f), RectangleShape)
                        .clickable { showMenu = true }
                        .padding(horizontal = 16.dp, vertical = 10.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = currentDeviceName,
                        color = primaryColor,
                        fontWeight = FontWeight.Bold,
                        fontSize = 16.sp,
                        letterSpacing = 1.sp
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Icon(Icons.Default.ArrowDropDown, "Select", tint = primaryColor)
                }

                DropdownMenu(
                    expanded = showMenu,
                    onDismissRequest = { showMenu = false },
                    modifier = Modifier.background(darkPanelColor)
                ) {
                    myDevices.forEach { device ->
                        DropdownMenuItem(
                            text = { Text(device, color = Color.White) },
                            onClick = {
                                currentDeviceName = device
                                showMenu = false
                            }
                        )
                    }
                    HorizontalDivider(color = Color.Gray.copy(alpha = 0.3f))
                    DropdownMenuItem(
                        text = {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(Icons.Default.Add, null, tint = primaryColor, modifier = Modifier.size(18.dp))
                                Spacer(modifier = Modifier.width(8.dp))
                                Text("Add New Device", color = primaryColor, fontWeight = FontWeight.Bold)
                            }
                        },
                        onClick = { showMenu = false }
                    )
                }
            }

            Spacer(modifier = Modifier.height(24.dp))


            Text(
                text = "Ambient Sound Control",
                color = Color.White,
                fontWeight = FontWeight.Bold,
                fontSize = 18.sp,
                letterSpacing = 0.5.sp,
                textAlign = TextAlign.Left,
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(8.dp))

            AmbientSoundControl(
                currentMode = ambientMode,
                onModeSelected = { ambientMode = it },
                ambientLevel = ambientLevel,
                onAmbientLevelChange = { ambientLevel = it },
                focusOnVoice = focusOnVoice,
                onFocusOnVoiceChange = { focusOnVoice = it },
                primaryColor = primaryColor
            )

            Spacer(modifier = Modifier.height(24.dp))

            Text(
                text = "Noise Cancelling Optimizer",
                color = Color.White,
                fontWeight = FontWeight.Bold,
                fontSize = 18.sp,
                letterSpacing = 0.5.sp,
                textAlign = TextAlign.Left,
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(8.dp))

            ConsoleEqualizerView()
        }
    }
}