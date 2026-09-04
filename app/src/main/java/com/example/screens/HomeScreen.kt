package com.example.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AttachMoney
import androidx.compose.material.icons.filled.CorporateFare
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.People
import androidx.compose.material.icons.filled.QrCode
import androidx.compose.material.icons.filled.SentimentSatisfied
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.MainViewModel
import com.example.Screen
import com.example.ui.theme.LilacAsh
import com.example.ui.theme.PineBlue
import com.example.ui.theme.VintageGrape

@Composable
fun HomeScreen(
    vm: MainViewModel,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .background(Color.White)
    ) {
        // Header
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp, vertical = 24.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                // Mockup Custom Logo container
                Box(
                    contentAlignment = Alignment.Center,
                    modifier = Modifier
                        .size(40.dp)
                        .background(VintageGrape, RoundedCornerShape(12.dp))
                ) {
                    Icon(
                        imageVector = Icons.Filled.People,
                        contentDescription = null,
                        tint = Color.White,
                        modifier = Modifier.size(24.dp)
                    )
                }
                Spacer(modifier = Modifier.width(12.dp))
                Text(
                    text = "UniFunder",
                    fontFamily = GoogleSans,
                    fontWeight = FontWeight.Bold,
                    fontSize = 20.sp,
                    color = VintageGrape,
                    letterSpacing = (-0.5).sp
                )
            }

            var showInfo by remember { mutableStateOf(false) }
            IconButton(
                onClick = { showInfo = true },
                modifier = Modifier
                    .size(40.dp)
                    .border(1.dp, LilacAsh, CircleShape)
            ) {
                Icon(
                    imageVector = Icons.Filled.Info,
                    contentDescription = "Info",
                    tint = LilacAsh
                )
            }

            if (showInfo) {
                AlertDialog(
                    onDismissRequest = { showInfo = false },
                    title = { Text("About UniFunder", fontFamily = GoogleSans, color = PineBlue, fontWeight = FontWeight.Bold) },
                    text = {
                        Text(
                            "UniFunder is a student-led fundraising automation platform built to align with United Nations SDG 17: Partnerships for the Goals.\n\nUniversities across Malaysia can streamline raising funds and establishing partnerships with NGOs transparently.",
                            fontFamily = GoogleSans,
                            fontSize = 14.sp
                        )
                    },
                    confirmButton = {
                        TextButton(onClick = { showInfo = false }) {
                            Text("OK", color = PineBlue)
                        }
                    }
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Title
        Text(
            text = "HOME",
            fontFamily = GoogleSans,
            fontWeight = FontWeight.ExtraBold,
            fontSize = 36.sp,
            color = PineBlue,
            textAlign = TextAlign.Center,
            letterSpacing = 1.5.sp,
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 10.dp)
        )

        Spacer(modifier = Modifier.height(32.dp))

        // Action Grid (2x2 circular items)
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Row(
                modifier = Modifier.padding(bottom = 36.dp),
                horizontalArrangement = Arrangement.spacedBy(48.dp)
            ) {
                // Proposal
                GridItem(
                    label = "PROPOSAL",
                    icon = Icons.Filled.CorporateFare,
                    onClick = { vm.navigateTo(Screen.SelectNgo) }
                )

                // Budget
                GridItem(
                    label = "BUDGET",
                    icon = Icons.Filled.AttachMoney,
                    onClick = { vm.navigateTo(Screen.Budget) }
                )
            }

            Row(
                horizontalArrangement = Arrangement.spacedBy(48.dp)
            ) {
                // Social
                GridItem(
                    label = "SOCIAL",
                    icon = Icons.Filled.SentimentSatisfied,
                    onClick = { vm.navigateTo(Screen.SocialMedia) }
                )

                // QR Code
                GridItem(
                    label = "QR",
                    icon = Icons.Filled.QrCode,
                    onClick = { vm.navigateTo(Screen.QrCode) }
                )
            }
        }
    }
}

@Composable
fun GridItem(
    label: String,
    icon: ImageVector,
    onClick: () -> Unit
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .clickable { onClick() }
    ) {
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier
                .size(96.dp)
                .border(2.dp, LilacAsh, CircleShape)
                .background(Color.White, CircleShape)
        ) {
            Icon(
                imageVector = icon,
                contentDescription = label,
                tint = VintageGrape,
                modifier = Modifier.size(44.dp)
            )
        }
        Spacer(modifier = Modifier.height(10.dp))
        Text(
            text = label,
            fontFamily = GoogleSans,
            fontWeight = FontWeight.Bold,
            fontSize = 14.sp,
            color = PineBlue,
            letterSpacing = 1.sp
        )
    }
}
