package com.example.screens

import android.content.Intent
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.AddCircle
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.net.toUri
import com.example.MainViewModel
import com.example.ui.theme.LilacAsh
import com.example.ui.theme.Malachite
import com.example.ui.theme.PineBlue
import com.example.ui.theme.VintageGrape
import com.example.ui.theme.VintageGrapeLight

@Composable
fun SelectNgoScreen(
    vm: MainViewModel,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    var searchQuery by remember { mutableStateOf("") }
    val filteredNgos = vm.ngos.filter { it.name.contains(searchQuery, ignoreCase = true) }

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
            IconButton(
                onClick = { vm.navigateBack() }
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = "Back",
                    tint = PineBlue
                )
            }
            Spacer(modifier = Modifier.width(10.dp))
            Text(
                text = "SELECT AN NGO",
                fontFamily = GoogleSans,
                fontWeight = FontWeight.Bold,
                fontSize = 20.sp,
                color = PineBlue
            )
        }

        // Search Bar
        OutlinedTextField(
            value = searchQuery,
            onValueChange = { searchQuery = it },
            placeholder = { Text("Search NGO...", color = LilacAsh) },
            leadingIcon = {
                Icon(
                    imageVector = Icons.Filled.Search,
                    contentDescription = "Search",
                    tint = PineBlue
                )
            },
            shape = RoundedCornerShape(30.dp),
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = PineBlue,
                unfocusedBorderColor = LilacAsh,
                focusedContainerColor = Color.White,
                unfocusedContainerColor = Color.White
            ),
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp, vertical = 10.dp)
        )

        Spacer(modifier = Modifier.height(10.dp))

        // NGO List
        LazyColumn(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
                .padding(horizontal = 20.dp)
        ) {
            items(filteredNgos) { ngo ->
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Profile Circle Placeholder
                    Box(
                        contentAlignment = Alignment.Center,
                        modifier = Modifier
                            .size(50.dp)
                            .border(1.dp, LilacAsh, CircleShape)
                            .background(Color.White, CircleShape)
                    ) {
                        Text(
                            text = ngo.initials,
                            fontFamily = GoogleSans,
                            fontWeight = FontWeight.Bold,
                            fontSize = 16.sp,
                            color = VintageGrape
                        )
                    }

                    Spacer(modifier = Modifier.width(15.dp))

                    // NGO Details
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = ngo.name,
                            fontFamily = GoogleSans,
                            fontWeight = FontWeight.Bold,
                            fontSize = 16.sp,
                            color = PineBlue,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                        Text(
                            text = ngo.description,
                            fontFamily = GoogleSans,
                            fontSize = 12.sp,
                            color = PineBlue.copy(alpha = 0.6f),
                            maxLines = 2,
                            overflow = TextOverflow.Ellipsis
                        )
                    }

                    // Clickable Dropdown trigger icon
                    Box {
                        IconButton(
                            onClick = {
                                vm.activeProposalNgoId = if (vm.activeProposalNgoId == ngo.id) null else ngo.id
                            }
                        ) {
                            Icon(
                                imageVector = Icons.Filled.AddCircle,
                                contentDescription = "Options",
                                tint = LilacAsh,
                                modifier = Modifier.size(30.dp)
                            )
                        }

                        // Dropdown Menu
                        DropdownMenu(
                            expanded = vm.activeProposalNgoId == ngo.id,
                            onDismissRequest = { vm.activeProposalNgoId = null }
                        ) {
                            DropdownMenuItem(
                                text = { Text("Visit website", fontFamily = GoogleSans, color = PineBlue) },
                                onClick = {
                                    vm.activeProposalNgoId = null
                                    try {
                                        val intent = Intent(Intent.ACTION_VIEW, ngo.website.toUri())
                                        context.startActivity(intent)
                                    } catch (e: Exception) {
                                        Toast.makeText(context, "No browser available", Toast.LENGTH_SHORT).show()
                                    }
                                }
                            )
                            DropdownMenuItem(
                                text = { Text("Project proposal", fontFamily = GoogleSans, color = PineBlue) },
                                onClick = {
                                    vm.activeProposalNgoId = null
                                    try {
                                        val intent = Intent(Intent.ACTION_VIEW, ngo.docTemplateUrl.toUri())
                                        context.startActivity(intent)
                                    } catch (e: Exception) {
                                        Toast.makeText(context, "No browser available", Toast.LENGTH_SHORT).show()
                                    }
                                    vm.prepareNewProposal(ngo)
                                }
                            )
                        }
                    }
                }
                HorizontalDivider(color = LilacAsh, thickness = 0.5.dp)
            }
        }

        // Primary Action Email Proposal Button at the bottom
        Button(
            onClick = {
                val proposal = vm.proposals.findLast { it.ngoId == vm.selectedNgo.id }
                if (proposal != null) {
                    val emailBody = """
                        Dear ${vm.selectedNgo.name},
                        
                        I am writing to you on behalf of UniFunder, a student-led initiative aligned with UN SDG 17: Partnerships for the Goals. We are interested in collaborating with your esteemed organisation.
                        
                        ---
                        PROPOSAL: ${proposal.title}
                        
                        ${proposal.content}
                        ---
                        
                        ${if (proposal.docsLink.isNotBlank()) "The full proposal document is available at:\n${proposal.docsLink}\n" else ""}
                        Thank you for your time and consideration.
                        
                        Best regards,
                        UniFunder Student Representative
                    """.trimIndent()

                    try {
                        val intent = Intent(Intent.ACTION_SENDTO).apply {
                            data = "mailto:".toUri()
                            putExtra(Intent.EXTRA_EMAIL, arrayOf(vm.selectedNgo.email))
                            putExtra(Intent.EXTRA_SUBJECT, "Project Proposal - ${proposal.title}")
                            putExtra(Intent.EXTRA_TEXT, emailBody)
                        }
                        context.startActivity(Intent.createChooser(intent, "Send Email"))
                        vm.sendEmailProposal(proposal)
                    } catch (e: Exception) {
                        Toast.makeText(context, "No email app available", Toast.LENGTH_SHORT).show()
                    }
                } else {
                    Toast.makeText(context, "Please create a proposal first", Toast.LENGTH_SHORT).show()
                }
            },
            colors = ButtonDefaults.buttonColors(containerColor = Malachite),
            shape = RoundedCornerShape(10.dp),
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp, vertical = 15.dp)
                .height(54.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center
            ) {
                Icon(
                    imageVector = Icons.Filled.Email,
                    contentDescription = null,
                    tint = Color.White
                )
                Spacer(modifier = Modifier.width(10.dp))
                Text(
                    text = "EMAIL PROPOSAL",
                    fontFamily = GoogleSans,
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp,
                    color = Color.White
                )
            }
        }

        // Dialog for Custom Proposal Creation/Edit
        if (vm.showEditProposalDialog) {
            var inputTitle by remember { mutableStateOf(vm.proposalTitleInput) }
            var inputContent by remember { mutableStateOf(vm.proposalContentInput) }
            var inputDocsLink by remember { mutableStateOf(vm.proposalDocsLink) }

            AlertDialog(
                onDismissRequest = { vm.showEditProposalDialog = false },
                title = {
                    Text(
                        text = "Customize Proposal",
                        fontFamily = GoogleSans,
                        fontWeight = FontWeight.Bold,
                        color = PineBlue
                    )
                },
                text = {
                    Column {
                        OutlinedTextField(
                            value = inputTitle,
                            onValueChange = { inputTitle = it },
                            label = { Text("Proposal Title") },
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = PineBlue,
                                unfocusedBorderColor = LilacAsh
                            ),
                            modifier = Modifier
                                .fillMaxWidth()
                        )
                        Spacer(modifier = Modifier.height(12.dp))
                        OutlinedTextField(
                            value = inputContent,
                            onValueChange = { inputContent = it },
                            label = { Text("Proposal Content") },
                            minLines = 4,
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = PineBlue,
                                unfocusedBorderColor = LilacAsh
                            ),
                            modifier = Modifier
                                .fillMaxWidth()
                        )
                        Spacer(modifier = Modifier.height(12.dp))
                        OutlinedTextField(
                            value = inputDocsLink,
                            onValueChange = { inputDocsLink = it },
                            placeholder = { Text("PASTE YOUR EDITED GOOGLE DOCS LINK HERE", color = LilacAsh) },
                            label = { Text("Google Docs Link") },
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Uri),
                            singleLine = true,
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = PineBlue,
                                unfocusedBorderColor = LilacAsh
                            ),
                            modifier = Modifier
                                .fillMaxWidth()
                        )
                        Spacer(modifier = Modifier.height(10.dp))
                        Text(
                            text = "Based on SDG 17 partnership parameters.",
                            fontSize = 11.sp,
                            color = VintageGrapeLight
                        )
                    }
                },
                confirmButton = {
                    Button(
                        onClick = { vm.saveProposal(inputTitle, inputContent, inputDocsLink) },
                        colors = ButtonDefaults.buttonColors(containerColor = Malachite)
                    ) {
                        Text("Save Draft")
                    }
                },
                dismissButton = {
                    TextButton(onClick = { vm.showEditProposalDialog = false }) {
                        Text("Cancel", color = PineBlue)
                    }
                }
            )
        }
    }
}
