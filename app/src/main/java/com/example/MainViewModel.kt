package com.example

import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.model.ChatMessage
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.UUID
import kotlin.time.Duration.Companion.seconds

class MainViewModel : ViewModel() {

    // --- Navigation Backstack ---
    private val _backStack = mutableStateListOf(Screen.SignInUp)
    val currentScreen: Screen get() = _backStack.lastOrNull() ?: Screen.SignInUp

    fun navigateTo(screen: Screen) {
        if (_backStack.lastOrNull() != screen) {
            _backStack.add(screen)
        }
    }

    fun navigateBack(): Boolean {
        if (_backStack.size > 1) {
            _backStack.removeLast()
            return true
        }
        return false
    }

    // --- Auth State ---
    var email by mutableStateOf("")
    var password by mutableStateOf("")
    var isLoggedIn by mutableStateOf(value = false)
    val isUniversityEmail: Boolean
        get() = email.endsWith(".edu.my", ignoreCase = true) || 
                (email.contains("student", ignoreCase = true) && email.contains(".edu"))

    // --- Core Statistical State ---
    var fundsRaised by mutableStateOf(350.0)
    var fundsGoal by mutableStateOf(500.0)
    var donorsReached by mutableStateOf(12)
    var proposalsCreated by mutableStateOf(3)
    var proposalsApproved by mutableStateOf(1)
    var ngosPartnered by mutableStateOf(2)

    // --- Lists of Data ---
    val ngos = listOf(
        Ngo(
            id = "mt_miriam",
            name = "Mount Miriam Cancer Hospital",
            website = "https://mountmiriam.com",
            description = "Provides compassionate, affordable cancer treatment & care services to needy patients.",
            initials = "NM",
            docTemplateUrl = "https://docs.google.com/document/d/1XyR7EovGOfuD_K3Lp6jE6mHl_Wz3AxhN",
        ),
        Ngo(
            id = "habitat",
            name = "The Habitat Foundation",
            website = "https://habitatfoundation.org.my",
            description = "Supports biodiversity conservation and natural habitat protection in Malaysia.",
            initials = "HF",
            docTemplateUrl = "https://docs.google.com/document/d/1B_L76fHe8O8mU27x2pEwF6vJgYhG_28"
        ),
        Ngo(
            id = "kawan",
            name = "Kawan NGO",
            website = "https://www.kawanngo.org",
            description = "Educational assistance, community empowerment, and food security in Malaysia.",
            initials = "KW",
            docTemplateUrl = "https://docs.google.com/document/d/1A_kO_u8LhP_V_3uN7v8C6XWnL29z_30"
        )
    )

    var selectedNgo by mutableStateOf<Ngo>(ngos.first())

    // Active dropdowns
    var activeProposalNgoId by mutableStateOf<String?>(null)
    var activeBudgetDropdownId by mutableStateOf<String?>(null)

    // Proposals State
    val proposals = mutableStateListOf<Proposal>(
        Proposal(
            id = "prop_1",
            ngoId = "mt_miriam",
            ngoName = "Mount Miriam Cancer Hospital",
            title = "TARUMT Cancer Care Awareness Drive",
            content = "This project proposal outlines a partnership between university students and Mount Miriam Cancer Hospital to conduct cancer awareness seminars and raise MYR 500 in funding to support subsidised chemotherapy treatments for B40 patients.",
            isSent = true,
            isApproved = true
        ),
        Proposal(
            id = "prop_2",
            ngoId = "habitat",
            ngoName = "The Habitat Foundation",
            title = "Eco-Volunteer Rainforest Regeneration",
            content = "Proposal for students to volunteer in conservation and build educational exhibits. Fundraising goal is set to fund soil enrichment and native seedling planting.",
            isSent = false,
            isApproved = false
        )
    )

    // Budgets State
    val budgets = mutableStateListOf<Budget>(
        Budget(
            id = "budget_1",
            name = "HOUSING ASSISTANCE FUND",
            info = "Active: 2024. Next Review: July 1.",
            details = "Provides temporary housing support for displaced families under SDG 11 and 17 partnerships.",
            items = listOf("Temporary Lodging Cost: $180.00", "Emergency Transport Card: $70.00", "Food Allowance: $100.00")
        ),
        Budget(
            id = "budget_2",
            name = "COMMUNITY GARDEN PROJECT",
            info = "Active: 2024. Next Review: July 1.",
            details = "Promotes sustainable local farming on university grounds. Encourages student-NGO cooperation.",
            items = listOf("Soil & Organic Fertilizer: $100.00", "Seedling Starters: $50.00", "Manual Gardening Set: $150.00", "Instructional Workshop: $50.00")
        ),
        Budget(
            id = "budget_3",
            name = "EDUCATIONAL SCHOLARSHIP FUND",
            info = "Active: 2024. Next Review: July 1.",
            details = "Supports B40 Malaysian student book purchases, material copying, and transport subsidies.",
            items = listOf("Malaysian textbook vouchers: $200.00", "University printing allowance: $50.00", "LRT student pass: $100.00")
        )
    )

    // Feed State
    val feedItems = mutableStateListOf<String>(
        "MATT DONATED $50",
        "MOUNT MIRIAM CANCER HOSPITAL PROJECT PROPOSAL APPROVED"
    )

    // Social drafts
    val socialDrafts = mutableStateListOf<SocialDraft>()

    // Ask AI Chat State

    private val _isAiLoading = MutableStateFlow(false)
    private val _chatText = MutableStateFlow<String>("")
    private val _chatMessages = MutableStateFlow<List<ChatMessage>>(emptyList()
//        ChatMessage(
//            id = "msg_init_user",
//            text = "HOW CAN I OPTIMIZE THIS PROJECT PROPOSAL?",
//            isUser = true,
//            senderName = "STEPHANIE FRANKLIN",
//            initials = "SF",
//            timestamp = "01:39"
//        ),
//        ChatMessage(
//            id = "msg_init_ai",
//            text = "CONSIDER QUANTIFYING YOUR IMPACT BY INCLUDING THE NUMBER OF FAMILIES ASSISTED.",
//            isUser = false,
//            senderName = "UniFunder AI",
//            initials = "AI",
//            timestamp = "01:40"
//        )
    )

    val aiChatMessages: StateFlow<List<ChatMessage>> = _chatMessages.asStateFlow()
    val aiChatText: StateFlow<String> = _chatText.asStateFlow()
    val isAiLoading = _isAiLoading.asStateFlow()

    // Proposal Custom Form
    var proposalTitleInput by mutableStateOf("")
    var proposalContentInput by mutableStateOf("")
    var showEditProposalDialog by mutableStateOf(false)
    var activeEditingProposal by mutableStateOf<Proposal?>(null)

    // --- Actions ---

    fun onChatTextChange(value: String) { _chatText.value = value }

    fun login() {
        if (email.isNotEmpty() && password.isNotEmpty()) {
            isLoggedIn = true
            navigateTo(Screen.Home)
        }
    }

    fun logout() {
        isLoggedIn = false
        email = ""
        password = ""
        _backStack.clear()
        _backStack.add(Screen.SignInUp)
    }

    fun prepareNewProposal(ngo: Ngo) {
        selectedNgo = ngo
        val existing = proposals.find { it.ngoId == ngo.id }
        if (existing != null) {
            proposalTitleInput = existing.title
            proposalContentInput = existing.content
            activeEditingProposal = existing
        } else {
            proposalTitleInput = "University-NGO Collaboration with ${ngo.name}"
            proposalContentInput = "This proposal establishes cooperation between university volunteers and ${ngo.name} to carry out localized fundraising drives supporting their noble societal causes in compliance with SDG 17."
            activeEditingProposal = null
        }
        showEditProposalDialog = true
    }

    fun saveProposal(title: String, content: String) {
        val editing = activeEditingProposal
        if (editing != null) {
            // Update existing
            val idx = proposals.indexOfFirst { it.id == editing.id }
            if (idx != -1) {
                proposals[idx] = editing.copy(title = title, content = content)
            }
        } else {
            // Create new
            val newProp = Proposal(
                id = "prop_${UUID.randomUUID()}",
                ngoId = selectedNgo.id,
                ngoName = selectedNgo.name,
                title = title,
                content = content,
                isSent = false,
                isApproved = false
            )
            proposals.add(newProp)
            proposalsCreated++
        }
        showEditProposalDialog = false
        activeEditingProposal = null
    }

    fun sendEmailProposal(proposal: Proposal) {
        // Send email
        val idx = proposals.indexOfFirst { it.id == proposal.id }
        if (idx != -1) {
            proposals[idx] = proposals[idx].copy(isSent = true)
        }
        // Add notification to Feed
        feedItems.add(0, "PROPOSAL SENT TO ${proposal.ngoName.uppercase()}: ${proposal.title.uppercase()}")
        
        // Dynamic approval simulation for prototype fun!
        viewModelScope.launch {
            // Simulate NGO review
            kotlinx.coroutines.delay(3.seconds)
            val updatedIdx = proposals.indexOfFirst { it.id == proposal.id }
            if (updatedIdx != -1) {
                proposals[updatedIdx] = proposals[updatedIdx].copy(isApproved = true)
                feedItems.add(0, "${proposal.ngoName.uppercase()} PROJECT PROPOSAL APPROVED")
                proposalsApproved++
                ngosPartnered++
                // Increment statistics
                fundsRaised += 15.0
                donorsReached += 1
            }
        }
    }

    // Budget Operations
    fun createNewBudget(name: String, details: String, items: List<String>) {
        val newBudget = Budget(
            id = "budget_${UUID.randomUUID()}",
            name = name.uppercase(),
            info = "Active: 2026. Next Review: July 1.",
            details = details,
            items = items
        )
        budgets.add(newBudget)
        feedItems.add(0, "NEW BUDGET CREATED: ${name.uppercase()}")
    }

    fun deleteBudget(id: String) {
        val b = budgets.find { it.id == id }
        if (b != null) {
            budgets.remove(b)
            feedItems.add(0, "DELETED BUDGET: ${b.name}")
        }
    }

    fun renameBudget(id: String, newName: String) {
        val idx = budgets.indexOfFirst { it.id == id }
        if (idx != -1) {
            val old = budgets[idx]
            budgets[idx] = old.copy(name = newName.uppercase())
            feedItems.add(0, "RENAMED BUDGET: ${old.name} TO ${newName.uppercase()}")
        }
    }

    // AI Chat Operations
    fun sendChatMessage() {
        if (_chatText.value.isBlank()) return
        val promptText = _chatText.value.trim()

        val userMsg = ChatMessage(
            id = "msg_${UUID.randomUUID()}",
            text = promptText,
            isUser = true,
            senderName = "STEPHANIE FRANKLIN",
            initials = "SF",
            timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("MMM-dd HH:mm"))
        )
        _chatMessages.update { chat_msgs -> chat_msgs + userMsg }
        _chatText.value = ""
        _isAiLoading.value = true

        viewModelScope.launch() {
            try {
                val aiResponse = GeminiService.getResponse(_chatMessages.value, promptText)

                val aiMsg = ChatMessage(
                    id = "msg_${UUID.randomUUID()}",
                    text = aiResponse,
                    isUser = false,
                    senderName = "UniFunder AI",
                    initials = "AI",
                    timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("MMM-dd HH:mm"))
                )
                _chatMessages.update { chat_msgs -> chat_msgs + aiMsg }
            } catch (e: Exception) {
                Log.e("MainViewModel", "Gemini API call failed", e)
            } finally {
                _isAiLoading.value = false
            }
        }

    }

    // Social Media post simulation
    fun createSocialPost(platform: String, text: String) {
        socialDrafts.add(SocialDraft(platform = platform, text = text, isPosted = true))
        feedItems.add(0, "SHARED A POST ON ${platform.uppercase()}")
        
        // Simulate a new donor from social media!
        fundsRaised += 50.0
        donorsReached += 1
    }
}
