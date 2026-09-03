package com.example.screens

import android.content.Intent
import android.widget.Toast
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ExitToApp
import androidx.compose.material.icons.automirrored.filled.InsertDriveFile
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.ChatBubble
import androidx.compose.material.icons.filled.People
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.CorporateFare
import androidx.compose.material.icons.filled.AttachMoney
import androidx.compose.material.icons.filled.SentimentSatisfied
import androidx.compose.material.icons.filled.QrCode
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.AddCircle
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Mail
import androidx.compose.material.icons.filled.Person
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import androidx.compose.material.icons.filled.Group
import androidx.compose.material.icons.filled.TaskAlt
import androidx.compose.material.icons.filled.Handshake
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.net.toUri
import com.example.Screen
import com.example.MainViewModel
import com.example.ui.theme.Malachite
import com.example.ui.theme.LilacAsh
import com.example.ui.theme.VintageGrape
import com.example.ui.theme.VintageGrapeLight
import com.example.ui.theme.PineBlue
import java.util.Locale
import java.util.Random
import android.graphics.Bitmap
import androidx.compose.foundation.Image
import androidx.compose.ui.graphics.asImageBitmap
import com.google.zxing.BarcodeFormat
import com.google.zxing.MultiFormatWriter

// --- Custom Font Definition (Google Sans Style / Sans-Serif Default) ---
val GoogleSans = FontFamily.SansSerif

// --- Bottom Navigation Component ---
@Composable
fun UniFunderBottomBar(
    currentScreen: Screen,
    onNavigate: (Screen) -> Unit,
    modifier: Modifier = Modifier
) {
    Surface(
        tonalElevation = 8.dp,
        color = Color.White,
        border = BorderStroke(1.dp, LilacAsh.copy(alpha = 0.3f)),
        modifier = modifier
            .fillMaxWidth()
            .navigationBarsPadding()
    ) {
        Row(
            horizontalArrangement = Arrangement.SpaceEvenly,
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 12.dp)
        ) {
            // Profile Item
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier
                    .weight(1f)
                    .clickable { onNavigate(Screen.Profile) }
                    .padding(vertical = 4.dp)
                    .testTag("nav_profile")
            ) {
                val activeBg = if (currentScreen == Screen.Profile) VintageGrape.copy(alpha = 0.1f) else Color.Transparent
                Box(
                    contentAlignment = Alignment.Center,
                    modifier = Modifier
                        .size(width = 64.dp, height = 32.dp)
                        .clip(RoundedCornerShape(16.dp))
                        .background(activeBg)
                ) {
                    Icon(
                        imageVector = Icons.Filled.AccountCircle,
                        contentDescription = "PROFILE",
                        tint = if (currentScreen == Screen.Profile) PineBlue else LilacAsh,
                        modifier = Modifier.size(24.dp)
                    )
                }
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "PROFILE",
                    fontFamily = GoogleSans,
                    fontWeight = FontWeight.Bold,
                    fontSize = 10.sp,
                    letterSpacing = 1.5.sp,
                    color = if (currentScreen == Screen.Profile) PineBlue else LilacAsh
                )
            }

            // Home Center Button (to quickly return to main dashboard)
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier
                    .size(52.dp)
                    .clip(CircleShape)
                    .background(PineBlue)
                    .clickable { onNavigate(Screen.Home) }
                    .testTag("nav_home")
            ) {
                Icon(
                    imageVector = Icons.Filled.Home,
                    contentDescription = "HOME",
                    tint = Color.White,
                    modifier = Modifier.size(26.dp)
                )
            }

            // Feed Item
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier
                    .weight(1f)
                    .clickable { onNavigate(Screen.Feed) }
                    .padding(vertical = 4.dp)
                    .testTag("nav_feed")
            ) {
                val activeBg = if (currentScreen == Screen.Feed) VintageGrape.copy(alpha = 0.1f) else Color.Transparent
                Box(
                    contentAlignment = Alignment.Center,
                    modifier = Modifier
                        .size(width = 64.dp, height = 32.dp)
                        .clip(RoundedCornerShape(16.dp))
                        .background(activeBg)
                ) {
                    Icon(
                        imageVector = Icons.Filled.ChatBubble,
                        contentDescription = "FEED",
                        tint = if (currentScreen == Screen.Feed) PineBlue else LilacAsh,
                        modifier = Modifier.size(24.dp)
                    )
                }
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "FEED",
                    fontFamily = GoogleSans,
                    fontWeight = FontWeight.Bold,
                    fontSize = 10.sp,
                    letterSpacing = 1.5.sp,
                    color = if (currentScreen == Screen.Feed) PineBlue else LilacAsh
                )
            }
        }
    }
}

// --- Dynamic Logo Composable ---
@Composable
fun UniFunderLogo(modifier: Modifier = Modifier, size: Int = 80) {
    Box(
        contentAlignment = Alignment.Center,
        modifier = modifier.size(size.dp)
    ) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            val width = size.toDp().toPx()
            val height = size.toDp().toPx()

            // Drawing the heart shape held by hands represented conceptually
            val mainGradient = Brush.linearGradient(
                colors = listOf(Malachite, PineBlue, VintageGrape),
                start = Offset(0f, 0f),
                end = Offset(width, height)
            )

            // Outer ring
            drawCircle(
                brush = mainGradient,
                radius = width / 2f,
                style = Stroke(width = 6f)
            )

            // Heart outline inside
            val path = Path().apply {
                moveTo(width / 2f, height * 0.75f)
                cubicTo(width * 0.15f, height * 0.5f, width * 0.1f, height * 0.25f, width * 0.45f, height * 0.25f)
                cubicTo(width / 2f, height * 0.25f, width / 2f, height * 0.35f, width / 2f, height * 0.35f)
                cubicTo(width / 2f, height * 0.35f, width / 2f, height * 0.25f, width * 0.55f, height * 0.25f)
                cubicTo(width * 0.9f, height * 0.25f, width * 0.85f, height * 0.5f, width / 2f, height * 0.75f)
                close()
            }
            drawPath(path = path, brush = mainGradient)

            // Top Dot (representing unity / person)
            drawCircle(
                color = VintageGrape,
                radius = width * 0.08f,
                center = Offset(width / 2f, height * 0.15f)
            )
        }
    }
}

// ==========================================
// 1. HOME SCREEN
// ==========================================
@Composable
fun HomeScreen(
    vm: MainViewModel,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .background(Color.White)
            .testTag("home_screen")
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

        // Large screen title centered with extra bold & tracking
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
                    tag = "grid_proposal_button",
                    onClick = { vm.navigateTo(Screen.SelectNgo) }
                )

                // Budget
                GridItem(
                    label = "BUDGET",
                    icon = Icons.Filled.AttachMoney,
                    tag = "grid_budget_button",
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
                    tag = "grid_social_button",
                    onClick = { vm.navigateTo(Screen.SocialMedia) }
                )

                // QR Code
                GridItem(
                    label = "QR",
                    icon = Icons.Filled.QrCode,
                    tag = "grid_qr_button",
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
    tag: String,
    onClick: () -> Unit
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .clickable { onClick() }
            .testTag(tag)
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

// ==========================================
// 2. SELECT AN NGO SCREEN
// ==========================================
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
            .testTag("select_ngo_screen")
    ) {
        // Header
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 15.dp, vertical = 15.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(
                onClick = { vm.navigateBack() },
                modifier = Modifier.testTag("back_button")
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
                .testTag("ngo_search_bar")
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
                            },
                            modifier = Modifier.testTag("action_ngo_${ngo.id}")
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
                            data = android.net.Uri.parse("mailto:")
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
                .testTag("email_proposal_button")
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
                                .testTag("proposal_title_input")
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
                                .testTag("proposal_content_input")
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
                                .testTag("proposal_docs_link_input")
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

// ==========================================
// 3. PROFILE SCREEN
// ==========================================
@Composable
fun ProfileScreen(
    vm: MainViewModel,
    googleSignInClient: GoogleSignInClient,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .background(Color.White)
            .testTag("profile_screen")
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
            IconButton(onClick = { vm.logout(googleSignInClient) }, modifier = Modifier.testTag("logout_button")) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ExitToApp,
                    contentDescription = "Logout",
                    tint = VintageGrape
                )
            }
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
                text = "Team Captain",
                fontFamily = GoogleSans,
                fontWeight = FontWeight.Medium,
                fontSize = 14.sp,
                color = VintageGrapeLight
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
                    value = "$${String.format(Locale.US, "%.2f", vm.fundsRaised)}",
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

// ==========================================
// 4. FEED SCREEN
// ==========================================
@Composable
fun FeedScreen(
    vm: MainViewModel,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .background(Color.White)
            .testTag("feed_screen")
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
                FeedCard(text = text)
            }
        }
    }
}

@Composable
fun FeedCard(text: String) {
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
            val initials = if (text.startsWith("MATT")) "M" else "NM"
            val bgCircle = if (initials == "M") Malachite else VintageGrape

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

// ==========================================
// 5. SIGN IN / UP SCREEN
// ==========================================
/* 
 * NOTE: For real deployment, the .edu.my domain list should be fetched from a backend allowlist 
 * rather than hardcoded, since some Malaysian universities use variants like 
 * .um.edu.my, .utar.edu.my, .taylors.edu.my, etc.
 */
@Composable
fun SignInUpScreen(
    vm: MainViewModel,
    onGoogleSignInClick: () -> Unit,
    googleSignInClient: GoogleSignInClient,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .background(Color.White)
            .padding(horizontal = 24.dp)
            .testTag("signin_screen"),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        UniFunderLogo(size = 100)

        Spacer(modifier = Modifier.height(24.dp))

        Text(
            text = "WELCOME TO UNIFUNDER",
            fontFamily = GoogleSans,
            fontSize = 14.sp,
            fontWeight = FontWeight.Medium,
            color = PineBlue,
            letterSpacing = 1.sp
        )

        Spacer(modifier = Modifier.height(40.dp))

        // LOG IN WITH GOOGLE Button
        Button(
            onClick = {
                vm.authError = null
                onGoogleSignInClick()
            },
            colors = ButtonDefaults.buttonColors(containerColor = Malachite),
            shape = RoundedCornerShape(8.dp),
            modifier = Modifier
                .fillMaxWidth()
                .height(52.dp)
                .testTag("google_login_button")
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center
            ) {
                // TODO: Replace with R.drawable.ic_google
                Icon(
                    imageVector = Icons.Default.Mail,
                    contentDescription = null,
                    tint = Color.White,
                    modifier = Modifier.size(24.dp)
                )
                Spacer(modifier = Modifier.width(10.dp))
                Text(
                    text = "LOG IN WITH GOOGLE",
                    fontFamily = GoogleSans,
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp,
                    color = Color.White
                )
            }
        }

        vm.authError?.let { error ->
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = error,
                color = Color.Red,
                fontSize = 12.sp,
                fontFamily = GoogleSans,
                maxLines = 2,
                textAlign = TextAlign.Center,
                modifier = Modifier.fillMaxWidth()
            )
        }

        Spacer(modifier = Modifier.height(12.dp))

        Text(
            text = "Only university Google Workspace accounts are allowed",
            color = PineBlue.copy(alpha = 0.6f),
            fontSize = 11.sp,
            fontFamily = GoogleSans,
            textAlign = TextAlign.Center,
            modifier = Modifier.fillMaxWidth()
        )
    }
}

// ==========================================
// 6. SOCIAL MEDIA SCREEN
// ==========================================
@Composable
fun SocialMediaScreen(
    vm: MainViewModel,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    var showPostDialog by remember { mutableStateOf(false) }
    var selectedPlatform by remember { mutableStateOf("") }
    var postText by remember { mutableStateOf("") }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(Color.White)
            .testTag("social_media_screen")
    ) {
        // Header
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 15.dp, vertical = 15.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "BACK",
                fontFamily = GoogleSans,
                fontWeight = FontWeight.Bold,
                fontSize = 14.sp,
                color = PineBlue,
                modifier = Modifier
                    .clickable { vm.navigateBack() }
                    .padding(8.dp)
            )
            Spacer(modifier = Modifier.weight(1f))
            Text(
                text = "SOCIAL MEDIA",
                fontFamily = GoogleSans,
                fontWeight = FontWeight.Bold,
                fontSize = 20.sp,
                color = PineBlue
            )
            Spacer(modifier = Modifier.weight(1f))
        }

        HorizontalDivider(color = PineBlue, thickness = 2.dp, modifier = Modifier.padding(horizontal = 20.dp))

        Spacer(modifier = Modifier.height(20.dp))

        // Platform Cards List
        val platforms = listOf("INSTAGRAM", "FACEBOOK", "REDNOTE (小紅書)", "TIKTOK")

        LazyColumn(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
                .padding(horizontal = 20.dp),
            verticalArrangement = Arrangement.spacedBy(15.dp)
        ) {
            items(platforms) { platform ->
                Card(
                    shape = RoundedCornerShape(8.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    border = BorderStroke(1.dp, LilacAsh),
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable {
                            selectedPlatform = platform
                            postText = "Support ${vm.selectedNgo.name} in our TARUMT partnership! We are raising funds for cancer care under SDG 17. Donate today!"
                            showPostDialog = true
                        }
                        .testTag("platform_$platform")
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .size(36.dp)
                                .border(2.dp, PineBlue, CircleShape)
                        ) // circular placeholder
                        Spacer(modifier = Modifier.width(15.dp))
                        Text(
                            text = platform,
                            fontFamily = GoogleSans,
                            fontWeight = FontWeight.Bold,
                            fontSize = 16.sp,
                            color = PineBlue
                        )
                    }
                }
            }
        }

        // Empty Bottom Area suggested by wireframe placeholder
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(100.dp)
                .padding(horizontal = 20.dp, vertical = 10.dp)
                .border(1.dp, LilacAsh, RoundedCornerShape(8.dp))
                .background(Color.White),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = "Create templates or drafts for social media reels & posts above.",
                fontFamily = GoogleSans,
                fontSize = 12.sp,
                color = LilacAsh,
                textAlign = TextAlign.Center
            )
        }

        Spacer(modifier = Modifier.height(15.dp))

        // Dialog for entering Social Media post
        if (showPostDialog) {
            AlertDialog(
                onDismissRequest = { showPostDialog = false },
                title = { Text("Publish to $selectedPlatform", fontFamily = GoogleSans, color = PineBlue, fontWeight = FontWeight.Bold) },
                text = {
                    Column {
                        OutlinedTextField(
                            value = postText,
                            onValueChange = { postText = it },
                            label = { Text("Post Body") },
                            modifier = Modifier.fillMaxWidth()
                        )
                        Spacer(modifier = Modifier.height(10.dp))
                        Text("This post will be directly visible to your followers.", fontSize = 12.sp, color = LilacAsh)
                    }
                },
                confirmButton = {
                    Button(
                        onClick = {
                            vm.createSocialPost(selectedPlatform, postText)
                            showPostDialog = false
                            Toast.makeText(context, "Post successfully published to $selectedPlatform!", Toast.LENGTH_SHORT).show()
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = Malachite)
                    ) {
                        Text("POST NOW")
                    }
                },
                dismissButton = {
                    TextButton(onClick = { showPostDialog = false }) {
                        Text("Cancel", color = PineBlue)
                    }
                }
            )
        }
    }
}

// ==========================================
// 7. ASK AI SCREEN (No Back, No bottom nav)
// ==========================================

// ==========================================
// 8. BUDGET SCREEN
// ==========================================

// ==========================================
// 9. QR CODE SCREEN (Track progress)
// ==========================================

// ======================================================
// GENERATE REAL QR CODE
// ======================================================

private fun generateQrBitmap(
    content: String,
    size: Int = 700
): Bitmap? {

    if (content.isBlank()) {
        return null
    }

    return try {

        val bitMatrix =
            MultiFormatWriter().encode(
                content,
                BarcodeFormat.QR_CODE,
                size,
                size
            )

        Bitmap.createBitmap(
            size,
            size,
            Bitmap.Config.RGB_565
        ).apply {

            for (x in 0 until size) {

                for (y in 0 until size) {

                    setPixel(
                        x,
                        y,
                        if (bitMatrix[x, y]) {
                            android.graphics.Color.BLACK
                        } else {
                            android.graphics.Color.WHITE
                        }
                    )
                }
            }
        }

    } catch (e: Exception) {
        null
    }
}

// ======================================================
// QR SCREEN
// ======================================================
@Composable
fun QrCodeScreen(
    vm: MainViewModel,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    // Controls NGO dropdown
    var dropdownExpanded by remember {
        mutableStateOf(false)
    }
    /*
     * Every time:
     *
     * selected NGO changes
     * OR
     * logged-in user changes
     *
     * reload Supabase progress.
     */
    LaunchedEffect(
        vm.selectedNgo.id,
        vm.loggedInEmail
    ) {

        vm.loadFundraisingProgress(
            vm.selectedNgo.id
        )
    }

    // ==================================================
    // CALCULATE PROGRESS
    // ==================================================

    val progressPercent =

        if (vm.qrGoalAmount > 0.0) {
            (
                    vm.qrRaisedAmount /
                            vm.qrGoalAmount
                    )
                .toFloat()
                .coerceIn(
                    0f,
                    1f
                )
        } else {
            0f
        }

    // ==================================================
    // GENERATE QR FROM SUPABASE qr_content
    // ==================================================

    val qrBitmap = remember(
        vm.qrContent
    ) {
        generateQrBitmap(
            vm.qrContent
        )
    }

    // ==================================================
    // MAIN SCREEN
    // ==================================================

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .background(Color.White)
            .testTag("qr_code_screen"),
        horizontalAlignment =
            Alignment.CenterHorizontally,
        contentPadding =
            PaddingValues(
                bottom = 32.dp
            )
    ) {

        // ==================================================
        // HEADER
        // ==================================================

        item {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(
                        horizontal = 15.dp,
                        vertical = 15.dp
                    ),
                verticalAlignment =
                    Alignment.CenterVertically
            ) {
                IconButton(
                    onClick = {
                        vm.navigateBack()
                    }
                ) {
                    Icon(
                        imageVector =
                            Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription =
                            "Back",
                        tint =
                            PineBlue
                    )
                }

                Spacer(
                    modifier =
                        Modifier.width(10.dp)
                )

                Text(
                    text =
                        "TRACK PROGRESS & QR",

                    fontFamily =
                        GoogleSans,

                    fontWeight =
                        FontWeight.Bold,

                    fontSize =
                        20.sp,

                    color =
                        PineBlue
                )
            }
        }


        // ==================================================
        // NGO DROPDOWN
        // ==================================================

        item {
            Text(
                text =
                    "Choose an NGO to support",

                fontFamily =
                    GoogleSans,

                fontWeight =
                    FontWeight.Bold,

                fontSize =
                    15.sp,

                color =
                    VintageGrape,

                modifier =
                    Modifier
                        .fillMaxWidth()
                        .padding(
                            horizontal = 30.dp
                        )
            )

            Spacer(
                modifier =
                    Modifier.height(8.dp)
            )

            Box(

                modifier =
                    Modifier
                        .fillMaxWidth()
                        .padding(
                            horizontal = 30.dp
                        )

            ) {

                // Dropdown button
                OutlinedButton(

                    onClick = {

                        dropdownExpanded =
                            true

                    },

                    modifier =
                        Modifier.fillMaxWidth(),

                    shape =
                        RoundedCornerShape(
                            12.dp
                        ),

                    border =
                        BorderStroke(
                            1.dp,
                            PineBlue
                        )

                ) {
                    Text(

                        text =
                            vm.selectedNgo.name,

                        color =
                            PineBlue,

                        fontWeight =
                            FontWeight.Bold,

                        modifier =
                            Modifier.weight(1f),

                        textAlign =
                            TextAlign.Start

                    )
                    Text(
                        text = "▼",
                        color = PineBlue
                    )

                }


                // Actual dropdown
                DropdownMenu(
                    expanded =
                        dropdownExpanded,
                    onDismissRequest = {
                        dropdownExpanded =
                            false
                    },
                    modifier =
                        Modifier
                            .fillMaxWidth(
                                0.85f
                            )
                            .background(
                                Color.White
                            )
                ) {
                    vm.ngos.forEach { ngo ->
                        DropdownMenuItem(
                            text = {

                                Text(

                                    text =
                                        ngo.name,

                                    fontWeight =

                                        if (
                                            ngo.id ==
                                            vm.selectedNgo.id
                                        ) {

                                            FontWeight.Bold

                                        } else {

                                            FontWeight.Normal

                                        }
                                )
                            },
                            onClick = {
                                dropdownExpanded =
                                    false
                                /*
                                 * Change NGO
                                 * +
                                 * load Supabase data
                                 */
                                vm.selectNgoForQr(
                                    ngo
                                )

                            }
                        )
                    }
                }
            }
            Spacer(
                modifier =
                    Modifier.height(24.dp)
            )

            // ==================================================
            // SELECTED NGO NAME
            // ==================================================

            Text(
                text =
                    vm.selectedNgo.name,

                fontFamily =
                    GoogleSans,

                fontWeight =
                    FontWeight.Bold,

                fontSize =
                    24.sp,

                color =
                    PineBlue,

                textAlign =
                    TextAlign.Center,

                modifier =
                    Modifier
                        .fillMaxWidth()
                        .padding(
                            horizontal = 24.dp
                        )
            )
            Text(
                text =
                    "SDG 17: Partnerships for the Goals",

                fontFamily =
                    GoogleSans,

                fontWeight =
                    FontWeight.Medium,

                fontSize =
                    14.sp,

                color =
                    VintageGrapeLight,

                textAlign =
                    TextAlign.Center,

                modifier =
                    Modifier.fillMaxWidth()
            )
            Spacer(
                modifier =
                    Modifier.height(20.dp)
            )


            // ==================================================
            // NGO INITIAL LOGO
            // ==================================================

            Box(
                contentAlignment =
                    Alignment.Center,
                modifier =
                    Modifier
                        .size(80.dp)
                        .clip(CircleShape)
                        .background(
                            LilacAsh.copy(
                                alpha = 0.2f
                            )
                        )
                        .border(
                            1.dp,
                            LilacAsh,
                            CircleShape
                        )
            ) {
                Text(
                    text =
                        vm.selectedNgo.initials,

                    fontWeight =
                        FontWeight.Bold,

                    fontSize =
                        26.sp,

                    color =
                        VintageGrape
                )
            }
            Spacer(
                modifier =
                    Modifier.height(20.dp)
            )

        }


        // ==================================================
        // QR CODE
        // ==================================================

        item {
            Card(
                modifier =
                    Modifier.size(
                        230.dp
                    ),
                shape =
                    RoundedCornerShape(
                        16.dp
                    ),
                colors =
                    CardDefaults.cardColors(
                        containerColor =
                            Color.White
                    ),
                border =
                    BorderStroke(
                        2.dp,
                        PineBlue
                    )
            ) {
                Box(
                    modifier =
                        Modifier
                            .fillMaxSize()
                            .padding(
                                14.dp
                            ),
                    contentAlignment =
                        Alignment.Center
                ) {
                    when {
                        // Loading Supabase
                        vm.qrIsLoading -> {

                            CircularProgressIndicator(
                                color =
                                    PineBlue
                            )
                        }
                        // QR available
                        qrBitmap != null -> {

                            Image(
                                bitmap = qrBitmap.asImageBitmap(),

                                contentDescription =
                                    "Donation QR for ${vm.selectedNgo.name}",

                                modifier = Modifier
                                    .fillMaxSize()
                                    .clickable {

                                        // Simulate successful payment
                                        val donatedAmount =
                                            vm.simulateQrDonation()

                                        // Show notification to user
                                        Toast.makeText(
                                            context,
                                            "RM ${
                                                String.format(
                                                    Locale.US,
                                                    "%.2f",
                                                    donatedAmount
                                                )
                                            } has been donated to ${vm.selectedNgo.name}!",
                                            Toast.LENGTH_LONG
                                        ).show()
                                    }
                            )
                        }
                        // Error
                        else -> {

                            Text(

                                text =
                                    "QR not available",

                                color =
                                    VintageGrape,

                                textAlign =
                                    TextAlign.Center
                            )
                        }
                    }
                }
            }
            Spacer(
                modifier =
                    Modifier.height(10.dp)
            )
            Text(
                text =
                    "Scan to support ${vm.selectedNgo.name}",

                color =
                    VintageGrapeLight,

                fontSize =
                    13.sp,

                textAlign =
                    TextAlign.Center,

                modifier =
                    Modifier.padding(
                        horizontal = 24.dp
                    )
            )
            Spacer(
                modifier =
                    Modifier.height(28.dp)
            )
        }

        // ==================================================
        // PROGRESS BAR
        // ==================================================

        item {
            Column(
                modifier =
                    Modifier
                        .fillMaxWidth()
                        .padding(
                            horizontal = 30.dp
                        ),
                horizontalAlignment =
                    Alignment.CenterHorizontally
            ) {
                Text(
                    text =
                        "DONATION PROGRESS",

                    fontFamily =
                        GoogleSans,

                    fontWeight =
                        FontWeight.Bold,

                    fontSize =
                        16.sp,

                    color =
                        PineBlue
                )
                Spacer(
                    modifier =
                        Modifier.height(12.dp)
                )

                // ==================================================
                // PROGRESS BAR
                // ==================================================

                LinearProgressIndicator(
                    progress = {
                        progressPercent
                    },
                    color =
                        Malachite,
                    trackColor =
                        LilacAsh.copy(
                            alpha = 0.3f
                        ),
                    modifier =
                        Modifier
                            .fillMaxWidth()
                            .height(
                                16.dp
                            )
                            .clip(
                                RoundedCornerShape(
                                    8.dp
                                )
                            )
                )
                Spacer(
                    modifier =
                        Modifier.height(10.dp)
                )
                Text(
                    text =
                        "${(progressPercent * 100).toInt()}% RAISED",

                    fontFamily =
                        GoogleSans,

                    fontWeight =
                        FontWeight.Bold,

                    fontSize =
                        18.sp,

                    color =
                        PineBlue
                )

                // ==================================================
                // TOTAL NGO PROGRESS
                // ==================================================

                Text(
                    text =
                        "RM ${
                            String.format(
                                Locale.US,
                                "%.2f",
                                vm.qrRaisedAmount
                            )
                        } / RM ${
                            String.format(
                                Locale.US,
                                "%.2f",
                                vm.qrGoalAmount
                            )
                        } Goal",
                    fontFamily =
                        GoogleSans,
                    fontWeight =
                        FontWeight.Medium,
                    fontSize =
                        14.sp,
                    color =
                        VintageGrape
                )
                Spacer(
                    modifier =
                        Modifier.height(10.dp)
                )

                // ==================================================
                // CURRENT USER'S DONATION
                // ==================================================

                Text(
                    text =
                        "Your contribution: RM ${
                            String.format(
                                Locale.US,
                                "%.2f",
                                vm.qrMyDonation
                            )
                        }",
                    fontFamily =
                        GoogleSans,
                    fontWeight =
                        FontWeight.Bold,
                    fontSize =
                        14.sp,
                    color =
                        Malachite
                )
                Spacer(
                    modifier =
                        Modifier.height(14.dp)
                )

                // ==================================================
                // REFRESH BUTTON
                // ==================================================

                OutlinedButton(
                    onClick = {
                        vm.loadFundraisingProgress(
                            vm.selectedNgo.id
                        )
                    },
                    enabled =
                        !vm.qrIsLoading
                ) {
                    Text(
                        if (vm.qrIsLoading) {
                            "Loading..."
                        } else {
                            "Refresh Progress"
                        }
                    )
                }

                // ==================================================
                // ERROR
                // ==================================================

                vm.qrError?.let { message ->
                    Spacer(
                        modifier =
                            Modifier.height(
                                10.dp
                            )
                    )
                    Text(
                        text =
                            message,
                        color =
                            MaterialTheme
                                .colorScheme
                                .error,
                        fontSize =
                            12.sp,
                        textAlign =
                            TextAlign.Center
                    )
                }

                Spacer(
                    modifier =
                        Modifier.height(24.dp)
                )

                Text(
                    text =
                        "Thank you for supporting our partnership.",
                    fontFamily =
                        GoogleSans,
                    fontWeight =
                        FontWeight.Bold,
                    fontSize =
                        14.sp,
                    color =
                        VintageGrapeLight,
                    textAlign =
                        TextAlign.Center
                )
            }
        }
    }
}
