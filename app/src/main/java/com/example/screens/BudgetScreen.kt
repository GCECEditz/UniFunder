package com.example.screens

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.util.Patterns
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
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.Budget
import com.example.MainViewModel
import com.example.R
import com.example.Screen
import com.example.ui.theme.LilacAsh
import com.example.ui.theme.Malachite
import com.example.ui.theme.PineBlue
import com.example.ui.theme.VintageGrapeLight
import kotlinx.coroutines.flow.compose

@Composable
fun BudgetScreen(
    vm: MainViewModel,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current

    val searchQuery by vm.budget_search_query.collectAsStateWithLifecycle()

    val budgets by vm.budgets.collectAsStateWithLifecycle()
    val filteredBudgets = budgets.filter { it.name.contains(searchQuery, ignoreCase = true) }

    val showCreateBudgetDialog by vm.create_budget_alert_isActive.collectAsStateWithLifecycle()
    val showRenameBudgetDialog by vm.rename_budget_alert_isActive.collectAsStateWithLifecycle()
    val showDeleteBudgetDialog by vm.delete_budget_alert_isActive.collectAsStateWithLifecycle()

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
            onValueChange = vm::onBudgetSearchQueryChange,
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
                BudgetRowItem(vm, modifier, budget)
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
                        text = { Text(stringResource(R.string.budget_screen_add_link)) },
                        onClick = {
                            showCreateMenu = false
                            vm.onCreateBudgetAlertIsActiveChange(true)
                            //Toast.makeText(context, add_link_toast, Toast.LENGTH_SHORT).show()
                        }
                    )
                }
            }
        }

        // Dialog to create a new budget
        if (showCreateBudgetDialog) {
            val inputName by vm.budget_name.collectAsStateWithLifecycle()
            val inputDetails by vm.budget_description.collectAsStateWithLifecycle()
            val docLink by vm.sheets_link.collectAsStateWithLifecycle()

            AlertDialog(
                onDismissRequest = {
                    reset_create_vars(vm)
                },
                title = { Text(stringResource(R.string.budget_screen_create_title), fontFamily = GoogleSans, color = PineBlue, fontWeight = FontWeight.Bold) },
                text = {
                    Column {
                        OutlinedTextField(
                            value = inputName,
                            onValueChange = vm::onBudgetNameChange,
                            label = { Text(stringResource(R.string.budget_screen_create_budget_name)) },
                            modifier = Modifier.fillMaxWidth()
                        )
                        Spacer(modifier = Modifier.height(10.dp))
                        OutlinedTextField(
                            value = inputDetails,
                            onValueChange = vm::onBudgetDescriptionChange,
                            label = { Text(stringResource(R.string.budget_screen_create_budget_description)) },
                            modifier = Modifier.fillMaxWidth()
                        )
                        Spacer(modifier = Modifier.height(10.dp))

                        OutlinedTextField(
                            value = docLink,
                            onValueChange = vm::onSheetLinkChange,
                            label = { Text(stringResource(R.string.budget_screen_link)) },
                            modifier = Modifier.fillMaxWidth()
                        )

//                        Text(stringResource(R.string.budget_screen_create_budget_allocation), fontWeight = FontWeight.Bold, fontSize = 12.sp)
//                        Row(verticalAlignment = Alignment.CenterVertically) {
//                            OutlinedTextField(
//                                value = inputItem,
//                                onValueChange = { inputItem = it },
//                                placeholder = { Text(stringResource(R.string.budget_screen_create_budget_placeholder)) },
//                                modifier = Modifier.weight(1f)
//                            )
//                            Spacer(modifier = Modifier.width(8.dp))
//                            IconButton(onClick = {
//                                if (inputItem.isNotBlank()) {
//                                    inputItems.add(inputItem)
//                                    inputItem = ""
//                                }
//                            }) {
//                                Icon(Icons.Filled.Add, stringResource(R.string.content_desc_addItem), tint = PineBlue)
//                            }
//                        }
//
//                        Spacer(modifier = Modifier.height(5.dp))
//                        inputItems.forEach {
//                            Text("• $it", fontSize = 12.sp, color = VintageGrapeLight)
//                        }
                    }
                },
                confirmButton = {
                    val create_toast = stringResource(R.string.budget_screen_create_toast)
                    var isVerifying by remember { mutableStateOf(false) }

                    Button(
                        onClick = {
                            isVerifying = true
                            vm.verifySheetOwnership(docLink) { success, errorMessage ->
                                isVerifying = false
                                if (success) {
                                    vm.createNewBudget(inputName, inputDetails, docLink)
                                    reset_create_vars(vm)
                                    Toast.makeText(context, create_toast, Toast.LENGTH_SHORT).show()
                                } else {
                                    Toast.makeText(context, errorMessage ?: "Ownership verification failed", Toast.LENGTH_LONG).show()
                                }
                            }
                        },
                        enabled = inputName.isNotBlank() && isValidUrl(docLink) && !isVerifying,
                        colors = ButtonDefaults.buttonColors(containerColor = Malachite)
                    ) {
                        Text(if (isVerifying) stringResource(R.string.generic_verifying) else stringResource(R.string.generic_create))
                    }
                },
                dismissButton = {
                    TextButton(onClick = {
                        reset_create_vars(vm)
                    }) {
                        Text(stringResource(R.string.generic_cancel), color = PineBlue)
                    }
                }
            )
        }

        // Dialog to rename budget
        if (showRenameBudgetDialog){
            val selectedBudget by vm.selectedBudget.collectAsStateWithLifecycle()

            selectedBudget?.let{ selectedBudget ->
                val renameName by vm.budget_name.collectAsStateWithLifecycle()
                val renameDetails by vm.budget_description.collectAsStateWithLifecycle()

                AlertDialog(
                    onDismissRequest = {
                        reset_rename_vars(vm)
                    },
                    title = { Text(stringResource(R.string.budget_screen_rename_title), fontFamily = GoogleSans, color = PineBlue, fontWeight = FontWeight.Bold) },
                    text = {
                        Column {
                            OutlinedTextField(
                                value = renameName,
                                onValueChange = vm::onBudgetNameChange,
                                label = { Text(stringResource(R.string.budget_screen_create_budget_name)) },
                                modifier = Modifier.fillMaxWidth()
                            )
                            Spacer(modifier = Modifier.height(10.dp))
                            OutlinedTextField(
                                value = renameDetails,
                                onValueChange = vm::onBudgetDescriptionChange,
                                label = { Text(stringResource(R.string.budget_screen_create_budget_description)) },
                                modifier = Modifier.fillMaxWidth()
                            )
                            Spacer(modifier = Modifier.height(10.dp))

                            Text(stringResource(R.string.budget_screen_create_budget_allocation), fontWeight = FontWeight.Bold, fontSize = 12.sp)
                        }
                    },
                    confirmButton = {
                        val rename_toast = stringResource(R.string.budget_screen_rename_toast)
                        Button(
                            onClick = {
                                if (renameName.isNotBlank()) {
                                    vm.renameBudget(selectedBudget.id, renameName, renameDetails)
                                    reset_rename_vars(vm)
                                    Toast.makeText(context, rename_toast, Toast.LENGTH_SHORT).show()
                                }
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = Malachite)
                        ) {
                            Text(stringResource(R.string.generic_rename))
                        }
                    },
                    dismissButton = {
                        TextButton(onClick = {
                            reset_rename_vars(vm)
                        }) {
                            Text(stringResource(R.string.generic_cancel), color = PineBlue)
                        }
                    }
                )
            }
        }

        // Dialog to confirm deletion
        if (showDeleteBudgetDialog) {
            val selectedBudget by vm.selectedBudget.collectAsStateWithLifecycle()
            selectedBudget?.let { budget ->
                AlertDialog(
                    onDismissRequest = {
                        reset_delete_vars(vm)
                    },
                    title = { Text(stringResource(R.string.budget_screen_delete_title), fontFamily = GoogleSans, color = PineBlue, fontWeight = FontWeight.Bold) },
                    text = { Text(stringResource(R.string.budget_screen_delete_msg)) },
                    confirmButton = {
                        Button(
                            onClick = {
                                vm.deleteBudget(budget.id)
                                reset_delete_vars(vm)
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = Color.Red)
                        ) {
                            Text(stringResource(R.string.generic_delete), color = Color.White)
                        }
                    },
                    dismissButton = {
                        TextButton(onClick = {
                            reset_delete_vars(vm)
                        }) {
                            Text(stringResource(R.string.generic_cancel), color = PineBlue)
                        }
                    }
                )
            }
        }
    }
}

fun reset_rename_vars(vm: MainViewModel){
    vm.onRenameBudgetAlertIsActiveChange(false)
    vm.onActiveBudgetDropdownIdChange(null)
    vm.onBudgetDescriptionChange("")
    vm.onBudgetNameChange("")
    vm.onSelectedBudgetChange(null)
}

fun reset_delete_vars(vm: MainViewModel){
    vm.onDeleteBudgetAlertIsActiveChange(false)
    vm.onActiveBudgetDropdownIdChange(null)
    vm.onSelectedBudgetChange(null)
}

fun reset_create_vars(vm: MainViewModel){
    vm.onCreateBudgetAlertIsActiveChange(false)
    vm.onBudgetDescriptionChange("")
    vm.onBudgetNameChange("")
    vm.onSheetLinkChange("")
}

fun isValidUrl(url: String): Boolean {
    return url.isNotBlank() && Patterns.WEB_URL.matcher(url).matches()
}

@Composable
fun BudgetRowItem(
    vm: MainViewModel,
    modifier: Modifier = Modifier,
    budget: Budget
){
    val context = LocalContext.current

    val activeBudgetDropdownId by vm.activeBudgetDropdownId.collectAsStateWithLifecycle()
    Card(
        onClick = {
            try {
                Toast.makeText(context, "Opening in Google Sheets. Ensure you are logged into the same account.", Toast.LENGTH_LONG).show()
                val intent = Intent(Intent.ACTION_VIEW, Uri.parse(budget.sheetLink))
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                context.startActivity(intent)
            } catch (e: Exception) {
                Toast.makeText(context, "Unable to open link", Toast.LENGTH_SHORT).show()
            }
        },
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
                            vm.onSelectedBudgetChange(null)
                            vm.onActiveBudgetDropdownIdChange(if (activeBudgetDropdownId == budget.id) null else budget.id)
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

                    DropdownMenu(
                        expanded = activeBudgetDropdownId == budget.id,
                        onDismissRequest = {
                            vm.onActiveBudgetDropdownIdChange(null)
                            vm.onSelectedBudgetChange(null)
                        }
                    ) {
                        DropdownMenuItem(
                            text = { Text(stringResource(R.string.budget_screen_export)) },
                            onClick = {
                                vm.onActiveBudgetDropdownIdChange(null)
                                Toast.makeText(context, export_toast, Toast.LENGTH_LONG).show()
                            }
                        )
                        DropdownMenuItem(
                            text = { Text(stringResource(R.string.budget_screen_rename)) },
                            onClick = {
                                vm.onActiveBudgetDropdownIdChange(null)
                                vm.onRenameBudgetAlertIsActiveChange(true)
                                vm.onSelectedBudgetChange(budget)
                                vm.onBudgetNameChange(budget.name)
                                vm.onBudgetDescriptionChange(budget.details)
                            }
                        )
                        DropdownMenuItem(
                            text = { Text(stringResource(R.string.budget_screen_copy_link)) },
                            onClick = {
                                vm.onActiveBudgetDropdownIdChange(null)
                                val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                                val clip = ClipData.newPlainText("Budget Sheet Link", budget.sheetLink)
                                clipboard.setPrimaryClip(clip)
                                Toast.makeText(context, "Link copied to clipboard", Toast.LENGTH_SHORT).show()
                            }
                        )
                        DropdownMenuItem(
                            text = { Text(stringResource(R.string.budget_screen_delete), color = Color.Red) },
                            onClick = {
                                vm.onActiveBudgetDropdownIdChange(null)
                                vm.onSelectedBudgetChange(budget)
                                vm.onDeleteBudgetAlertIsActiveChange(true)
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
            Spacer(modifier = Modifier.height(6.dp))
            Text(
                text = "Link: ${budget.sheetLink}",
                fontFamily = GoogleSans,
                fontSize = 12.sp,
                color = Malachite
            )
        }
    }
}