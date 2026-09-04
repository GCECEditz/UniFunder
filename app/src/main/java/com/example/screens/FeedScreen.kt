package com.example.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.MainViewModel
import com.example.ui.theme.LilacAsh
import com.example.ui.theme.Malachite
import com.example.ui.theme.PineBlue
import com.example.ui.theme.VintageGrape

@Composable
fun FeedScreen(
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
                text = "FEED",
                fontFamily = GoogleSans,
                fontWeight = FontWeight.Bold,
                fontSize = 20.sp,
                color = PineBlue
            )
        }

        // Live list of feed items
        LazyColumn(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
                .padding(horizontal = 20.dp),
            verticalArrangement = Arrangement.spacedBy(15.dp)
        ) {
            items(vm.feedItems) { text ->
                FeedCard(text = text, initials = vm.userInitials)
            }
        }
    }
}

@Composable
fun FeedCard(text: String, initials: String) {
    Card(
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        border = BorderStroke(1.dp, LilacAsh),
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Circle placeholder for icon initials
            //val initials = if (text.startsWith("MATT")) "M" else "NM"
            val bgCircle = VintageGrape //if (initials == "M") Malachite else VintageGrape

            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier
                    .size(44.dp)
                    .background(bgCircle.copy(alpha = 0.15f), CircleShape)
                    .border(1.dp, bgCircle, CircleShape)
            ) {
                Text(
                    text = initials,
                    fontFamily = GoogleSans,
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp,
                    color = bgCircle
                )
            }

            Spacer(modifier = Modifier.width(15.dp))

            Text(
                text = text,
                fontFamily = GoogleSans,
                fontWeight = FontWeight.Bold,
                fontSize = 14.sp,
                color = PineBlue,
                modifier = Modifier.weight(1f)
            )
        }
    }
}
