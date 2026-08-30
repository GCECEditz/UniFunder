package com.example.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ChatMessage
import com.example.MainViewModel
import com.example.R
import com.example.ui.theme.LilacAsh
import com.example.ui.theme.PineBlue
import com.example.ui.theme.VintageGrape

@Composable
fun AskAiScreen(
    vm: MainViewModel,
    modifier: Modifier = Modifier
) {
    var chatText by remember { mutableStateOf("") }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(Color.White)
            .testTag("ask_ai_screen")
    ) {
        // Header (Distinct, no back, no status bar)
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 15.dp),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = stringResource(R.string.ai_screen_title),
                fontFamily = GoogleSans,
                fontWeight = FontWeight.Bold,
                fontSize = 24.sp,
                color = PineBlue
            )
        }

        HorizontalDivider(color = LilacAsh, thickness = 1.dp)

        // Chat Message Feed
        LazyColumn(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
                .padding(horizontal = 16.dp, vertical = 10.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            items(vm.chatMessages) { msg ->
                ChatBubble(msg = msg)
            }
            if (vm.isAiLoading) {
                item {
                    Row(
                        modifier = Modifier.fillMaxWidth().padding(8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        CircularProgressIndicator(color = PineBlue, modifier = Modifier.size(24.dp))
                        Spacer(modifier = Modifier.width(10.dp))
                        Text(stringResource(R.string.ai_screen_gemini_answering), fontSize = 13.sp, color = LilacAsh)
                    }
                }
            }
        }

        // Bottom Input Bar
        Surface(
            tonalElevation = 4.dp,
            color = Color.White,
            border = BorderStroke(1.dp, LilacAsh)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 10.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                OutlinedTextField(
                    value = chatText,
                    onValueChange = { chatText = it },
                    placeholder = { Text(stringResource(R.string.ai_screen_prompt_examples), color = LilacAsh) },
                    shape = RoundedCornerShape(24.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = PineBlue,
                        unfocusedBorderColor = LilacAsh
                    ),
                    modifier = Modifier
                        .weight(1f)
                        .testTag("ai_input_text")
                )

                Spacer(modifier = Modifier.width(10.dp))

                // Small Pine Blue magnifying glass acts as Send
                Box(
                    contentAlignment = Alignment.Center,
                    modifier = Modifier
                        .size(48.dp)
                        .clip(CircleShape)
                        .background(PineBlue)
                        .clickable {
                            if (chatText.isNotBlank()) {
                                vm.sendChatMessage(chatText)
                                chatText = ""
                            }
                        }
                        .testTag("ai_send_button")
                ) {
                    Icon(
                        imageVector = Icons.Filled.Search,
                        contentDescription = stringResource(R.string.content_desc_send),
                        tint = Color.White,
                        modifier = Modifier.size(24.dp)
                    )
                }
            }
        }
    }
}

@Composable
fun ChatBubble(msg: ChatMessage) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = if (msg.isUser) Arrangement.End else Arrangement.Start,
        verticalAlignment = Alignment.Top
    ) {
        if (!msg.isUser) {
            // AI Logo Icon circle on left
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier
                    .size(36.dp)
                    .clip(CircleShape)
                    .background(PineBlue)
            ) {
                Text(
                    text = stringResource(R.string.ai_screen_profile_name),
                    fontWeight = FontWeight.Bold,
                    fontSize = 13.sp,
                    color = Color.White
                )
            }
            Spacer(modifier = Modifier.width(8.dp))
        }

        Card(
            shape = RoundedCornerShape(12.dp),
            border = BorderStroke(1.dp, LilacAsh),
            colors = CardDefaults.cardColors(
                containerColor = if (msg.isUser) LilacAsh else Color.White
            ),
            modifier = Modifier.widthIn(max = 280.dp)
        ) {
            Column(modifier = Modifier.padding(12.dp)) {
                Text(
                    text = msg.senderName,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    color = if (msg.isUser) Color.Black else PineBlue
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = msg.text,
                    fontSize = 14.sp,
                    color = if (msg.isUser) Color.Black else PineBlue
                )
            }
        }

        if (msg.isUser) {
            Spacer(modifier = Modifier.width(8.dp))
            // User Photo on right
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier
                    .size(36.dp)
                    .clip(CircleShape)
                    .background(VintageGrape)
            ) {
                Text(
                    text = "SF",
                    fontWeight = FontWeight.Bold,
                    fontSize = 13.sp,
                    color = Color.White
                )
            }
        }
    }
}
