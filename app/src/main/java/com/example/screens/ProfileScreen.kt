package com.example.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ExitToApp
import androidx.compose.material.icons.automirrored.filled.InsertDriveFile
import androidx.compose.material.icons.filled.AttachMoney
import androidx.compose.material.icons.filled.Group
import androidx.compose.material.icons.filled.Handshake
import androidx.compose.material.icons.filled.TaskAlt
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.MainViewModel
import com.example.ui.theme.*
import java.util.Locale

@Composable
fun ProfileScreen(
    vm: MainViewModel,
    modifier: Modifier = Modifier
) {
    val context = androidx.compose.ui.platform.LocalContext.current
    var showLogoutDialog by remember { mutableStateOf(false) }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(Color.White)
    ) {
        // Header
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 15.dp, vertical = 15.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = { vm.navigateBack() }) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = "Back",
                    tint = PineBlue
                )
            }
            Spacer(modifier = Modifier.width(10.dp))
            Text(
                text = "PROFILE",
                fontFamily = GoogleSans,
                fontWeight = FontWeight.Bold,
                fontSize = 20.sp,
                color = PineBlue
            )
            Spacer(modifier = Modifier.weight(1f))
            IconButton(onClick = { showLogoutDialog = true }) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ExitToApp,
                    contentDescription = "Logout",
                    tint = VintageGrape
                )
            }
        }

        if (showLogoutDialog) {
            AlertDialog(
                onDismissRequest = { showLogoutDialog = false },
                title = {
                    Text(
                        text = "Log Out",
                        color = PineBlue,
                        fontWeight = FontWeight.Bold,
                        fontFamily = GoogleSans
                    )
                },
                text = {
                    Text(
                        text = "Are you sure you want to log out?",
                        color = PineBlue.copy(alpha = 0.6f),
                        fontFamily = GoogleSans
                    )
                },
                confirmButton = {
                    Button(
                        onClick = {
                            vm.logout(context)
                            showLogoutDialog = false
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = Malachite),
                        shape = RoundedCornerShape(10.dp)
                    ) {
                        Text(
                            text = "Log Out",
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                    }
                },
                dismissButton = {
                    TextButton(onClick = { showLogoutDialog = false }) {
                        Text(
                            text = "Cancel",
                            color = PineBlue
                        )
                    }
                }
            )
        }

        Spacer(modifier = Modifier.height(10.dp))

        // Profile silhouette photo
        Column(
            modifier = Modifier.fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier
                    .size(110.dp)
                    .clip(CircleShape)
                    .border(3.dp, LilacAsh, CircleShape)
                    .background(LilacAsh.copy(alpha = 0.3f), CircleShape)
            ) {
                Text(
                    text = vm.userInitials,
                    fontFamily = GoogleSans,
                    fontWeight = FontWeight.Bold,
                    fontSize = 40.sp,
                    color = VintageGrape
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            // User details
            Text(
                text = if (vm.loggedInDisplayName.isBlank()) "UNIFUNDER USER" else vm.loggedInDisplayName.uppercase(),
                fontFamily = GoogleSans,
                fontWeight = FontWeight.Bold,
                fontSize = 22.sp,
                color = PineBlue
            )
            Text(
                text = vm.loggedInEmail.ifEmpty { "gcecgeography@gmail.com" },
                fontFamily = GoogleSans,
                fontSize = 13.sp,
                color = LilacAsh
            )
            if (vm.isLoggedIn && vm.loggedInEmail.endsWith(".edu.my", ignoreCase = true)) {
                Spacer(modifier = Modifier.height(4.dp))
                Box(
                    modifier = Modifier
                        .background(Malachite.copy(alpha = 0.15f), RoundedCornerShape(12.dp))
                        .padding(horizontal = 10.dp, vertical = 3.dp)
                ) {
                    Text(
                        text = "🎓 Verified University Account",
                        fontFamily = GoogleSans,
                        fontWeight = FontWeight.Bold,
                        fontSize = 11.sp,
                        color = Malachite
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(25.dp))

        // Stat cards (white cards on light Ash background / stacked)
        LazyColumn(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
                .padding(horizontal = 20.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            item {
                StatCard(
                    title = "TOTAL FUNDS RAISED",
                    value = "RM${String.format(Locale.US, "%.2f", vm.fundsRaised)}",
                    icon = Icons.Filled.AttachMoney
                )
            }
            item {
                StatCard(
                    title = "TOTAL DONORS REACHED",
                    value = vm.donorsReached.toString(),
                    icon = Icons.Filled.Group
                )
            }
            item {
                StatCard(
                    title = "PROPOSALS CREATED",
                    value = vm.proposalsCreated.toString(),
                    icon = Icons.AutoMirrored.Filled.InsertDriveFile
                )
            }
            item {
                StatCard(
                    title = "PROPOSALS APPROVED",
                    value = "${vm.proposalsApproved}",
                    icon = Icons.Filled.TaskAlt
                )
            }
            item {
                StatCard(
                    title = "NGO'S PARTNERED",
                    value = "${vm.ngosPartnered}",
                    icon = Icons.Filled.Handshake
                )
            }
        }
    }
}

@Composable
fun StatCard(
    title: String,
    value: String,
    icon: ImageVector
) {
    Card(
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        border = BorderStroke(1.dp, LilacAsh),
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 14.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column {
                Text(
                    text = title,
                    fontFamily = GoogleSans,
                    fontWeight = FontWeight.Bold,
                    fontSize = 11.sp,
                    color = VintageGrapeLight
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = value,
                    fontFamily = GoogleSans,
                    fontWeight = FontWeight.Bold,
                    fontSize = 18.sp,
                    color = PineBlue
                )
            }

            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier
                    .size(40.dp)
                    .background(PineBlue.copy(alpha = 0.1f), CircleShape)
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = PineBlue,
                    modifier = Modifier.size(20.dp)
                )
            }
        }
    }
}
