package com.example.screens

import android.widget.Toast
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ChatBubble
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.MainViewModel
import com.example.R
import com.example.Screen
import com.example.ui.theme.LilacAsh
import com.example.ui.theme.Malachite
import com.example.ui.theme.PineBlue
import com.example.ui.theme.VintageGrapeLight

@Composable
fun BudgetScreen(
    vm: MainViewModel,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    var searchQuery by remember { mutableStateOf("") }
    val filteredBudgets = vm.budgets.filter { it.name.contains(searchQuery, ignoreCase = true) }

    var showCreateBudgetDialog by remember { mutableStateOf(false) }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(Color.White)
            .testTag("budget_screen")
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
                    contentDescription = stringResource(R.string.content_desc_back),
                    tint = PineBlue
                )
            }
            Spacer(modifier = Modifier.width(10.dp))
            Text(
                text = stringResource(R.string.budget_screen_title),
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
            placeholder = { Text(stringResource(R.string.budget_screen_search), color = LilacAsh) },
            trailingIcon = {
                Icon(
                    imageVector = Icons.Filled.Search,
                    contentDescription = stringResource(R.string.content_desc_search),
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
                .testTag("budget_search_bar")
        )

        Spacer(modifier = Modifier.height(10.dp))

        // Budgets Cards List
        LazyColumn(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
                .padding(horizontal = 20.dp),
            verticalArrangement = Arrangement.spacedBy(15.dp)
        ) {
            items(filteredBudgets) { budget ->
                Card(
                    shape = RoundedCornerShape(8.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    border = BorderStroke(1.dp, LilacAsh),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = budget.name,
                                fontFamily = GoogleSans,
                                fontWeight = FontWeight.Bold,
                                fontSize = 16.sp,
                                color = PineBlue,
                                modifier = Modifier.weight(1f)
                            )

                            // Dropdown trig circle icon
                            Box {
                                IconButton(
                                    onClick = {
                                        vm.activeBudgetDropdownId = if (vm.activeBudgetDropdownId == budget.id) null else budget.id
                                    },
                                    modifier = Modifier.testTag("action_budget_${budget.id}")
                                ) {
                                    Icon(
                                        imageVector = Icons.Filled.Add,
                                        contentDescription = stringResource(R.string.content_desc_menu),
                                        tint = PineBlue
                                    )
                                }

                                val export_toast = stringResource(R.string.budget_screen_export_toast, budget.name)
                                val share_toast = stringResource(R.string.budget_screen_share_toast)
                                val rename_toast = stringResource(R.string.budget_screen_rename_toast)
                                val rename_new = stringResource(R.string.budget_screen_rename_new, budget.name)

                                DropdownMenu(
                                    expanded = vm.activeBudgetDropdownId == budget.id,
                                    onDismissRequest = { vm.activeBudgetDropdownId = null }
                                ) {
                                    DropdownMenuItem(
                                        text = { Text(stringResource(R.string.budget_screen_export)) },
                                        onClick = {
                                            vm.activeBudgetDropdownId = null
                                            Toast.makeText(context, export_toast, Toast.LENGTH_LONG).show()
                                        }
                                    )
                                    DropdownMenuItem(
                                        text = { Text(stringResource(R.string.budget_screen_share)) },
                                        onClick = {
                                            vm.activeBudgetDropdownId = null
                                            Toast.makeText(context, share_toast, Toast.LENGTH_SHORT).show()
                                        }
                                    )
                                    DropdownMenuItem(
                                        text = { Text(stringResource(R.string.budget_screen_rename)) },
                                        onClick = {
                                            vm.activeBudgetDropdownId = null
                                            vm.renameBudget(budget.id, rename_new)
                                            Toast.makeText(context, rename_toast, Toast.LENGTH_SHORT).show()
                                        }
                                    )
                                    DropdownMenuItem(
                                        text = { Text(stringResource(R.string.budget_screen_delete), color = Color.Red) },
                                        onClick = {
                                            vm.activeBudgetDropdownId = null
                                            vm.deleteBudget(budget.id)
                                        }
                                    )
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = budget.info,
                            fontFamily = GoogleSans,
                            fontSize = 12.sp,
                            color = VintageGrapeLight
                        )
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(
                            text = budget.details,
                            fontFamily = GoogleSans,
                            fontSize = 13.sp,
                            color = Color.Black
                        )
                        if (budget.items.isNotEmpty()) {
                            Spacer(modifier = Modifier.height(8.dp))
                            budget.items.forEach { item ->
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    modifier = Modifier.padding(vertical = 2.dp)
                                ) {
                                    Box(
                                        modifier = Modifier
                                            .size(6.dp)
                                            .clip(CircleShape)
                                            .background(Malachite)
                                    )
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text(
                                        text = item,
                                        fontSize = 12.sp,
                                        fontFamily = GoogleSans,
                                        color = VintageGrapeLight
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }

        // Actionable Icons at the bottom row (Ask AI & Add link / Create New)
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp, vertical = 5.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Ask AI button (bottom left)
            IconButton(
                onClick = { vm.navigateTo(Screen.AskAi) },
                modifier = Modifier
                    .size(54.dp)
                    .border(1.dp, LilacAsh, CircleShape)
                    .testTag("ai_nav_circle_button")
            ) {
                Icon(
                    imageVector = Icons.Filled.ChatBubble,
                    contentDescription = stringResource(R.string.content_desc_askAI),
                    tint = PineBlue,
                    modifier = Modifier.size(24.dp)
                )
            }

            var showCreateMenu by remember { mutableStateOf(false) }

            // Create dropdown button (bottom right)
            Box {
                IconButton(
                    onClick = { showCreateMenu = true },
                    modifier = Modifier
                        .size(54.dp)
                        .border(1.dp, LilacAsh, CircleShape)
                        .testTag("budget_add_options_button")
                ) {
                    Icon(
                        imageVector = Icons.Filled.Add,
                        contentDescription = stringResource(R.string.content_desc_addOptions),
                        tint = PineBlue,
                        modifier = Modifier.size(24.dp)
                    )
                }

                val add_link_toast = stringResource(R.string.budget_screen_add_link_toast)
                DropdownMenu(
                    expanded = showCreateMenu,
                    onDismissRequest = { showCreateMenu = false }
                ) {
                    DropdownMenuItem(
                        text = { Text(stringResource(R.string.budget_screen_create)) },
                        onClick = {
                            showCreateMenu = false
                            showCreateBudgetDialog = true
                        }
                    )
                    DropdownMenuItem(
                        text = { Text(stringResource(R.string.budget_screen_add_link)) },
                        onClick = {
                            showCreateMenu = false
                            Toast.makeText(context, add_link_toast, Toast.LENGTH_SHORT).show()
                        }
                    )
                }
            }
        }

        // Primary Action: Large central CREATE NEW BUDGET button (Malachite)
        Button(
            onClick = { showCreateBudgetDialog = true },
            colors = ButtonDefaults.buttonColors(containerColor = Malachite),
            shape = RoundedCornerShape(10.dp),
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp, vertical = 15.dp)
                .height(54.dp)
                .testTag("create_budget_button")
        ) {
            Text(
                text = stringResource(R.string.budget_screen_create_title),
                fontFamily = GoogleSans,
                fontWeight = FontWeight.Bold,
                fontSize = 16.sp,
                color = Color.White
            )
        }

        // Dialog to create a new budget
        if (showCreateBudgetDialog) {
            var inputName by remember { mutableStateOf("") }
            var inputDetails by remember { mutableStateOf("") }
            var inputItem by remember { mutableStateOf("") }
            val inputItems = remember { mutableStateListOf<String>() }

            AlertDialog(
                onDismissRequest = { showCreateBudgetDialog = false },
                title = { Text(stringResource(R.string.budget_screen_create_title), fontFamily = GoogleSans, color = PineBlue, fontWeight = FontWeight.Bold) },
                text = {
                    Column {
                        OutlinedTextField(
                            value = inputName,
                            onValueChange = { inputName = it },
                            label = { Text(stringResource(R.string.budget_screen_create_budget_name)) },
                            modifier = Modifier.fillMaxWidth()
                        )
                        Spacer(modifier = Modifier.height(10.dp))
                        OutlinedTextField(
                            value = inputDetails,
                            onValueChange = { inputDetails = it },
                            label = { Text(stringResource(R.string.budget_screen_create_budget_description)) },
                            modifier = Modifier.fillMaxWidth()
                        )
                        Spacer(modifier = Modifier.height(10.dp))

                        Text(stringResource(R.string.budget_screen_create_budget_allocation), fontWeight = FontWeight.Bold, fontSize = 12.sp)
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            OutlinedTextField(
                                value = inputItem,
                                onValueChange = { inputItem = it },
                                placeholder = { Text(stringResource(R.string.budget_screen_create_budget_placeholder)) },
                                modifier = Modifier.weight(1f)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            IconButton(onClick = {
                                if (inputItem.isNotBlank()) {
                                    inputItems.add(inputItem)
                                    inputItem = ""
                                }
                            }) {
                                Icon(Icons.Filled.Add, stringResource(R.string.content_desc_addItem), tint = PineBlue)
                            }
                        }

                        Spacer(modifier = Modifier.height(5.dp))
                        inputItems.forEach {
                            Text("• $it", fontSize = 12.sp, color = VintageGrapeLight)
                        }
                    }
                },
                confirmButton = {
                    val create_toast = stringResource(R.string.budget_screen_create_toast)
                    Button(
                        onClick = {
                            if (inputName.isNotBlank()) {
                                vm.createNewBudget(inputName, inputDetails, inputItems.toList())
                                showCreateBudgetDialog = false
                                Toast.makeText(context, create_toast, Toast.LENGTH_SHORT).show()
                            }
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = Malachite)
                    ) {
                        Text(stringResource(R.string.generic_create))
                    }
                },
                dismissButton = {
                    TextButton(onClick = { showCreateBudgetDialog = false }) {
                        Text(stringResource(R.string.generic_cancel), color = PineBlue)
                    }
                }
            )
        }
    }
}