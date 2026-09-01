package com.example

import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.api.client.extensions.android.http.AndroidHttp
import com.google.api.client.googleapis.extensions.android.gms.auth.GoogleAccountCredential
import com.google.api.client.json.gson.GsonFactory
import com.google.api.services.drive.Drive
import com.google.api.services.drive.DriveScopes
import com.example.model.ChatMessage
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
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
            _backStack.removeAt(_backStack.lastIndex)
            return true
        }
        return false
    }

    // --- Auth State ---
    var isLoggedIn by mutableStateOf(value = false)
    var loggedInEmail by mutableStateOf("")
    var loggedInDisplayName by mutableStateOf("")
    var authError by mutableStateOf<String?>(null)
    var googleAccount by mutableStateOf<GoogleSignInAccount?>(null)
    var googleCredential by mutableStateOf<GoogleAccountCredential?>(null)

    val userInitials: String
        get() = if (loggedInDisplayName.isNotBlank()) {
            val parts = loggedInDisplayName.trim().split("\\s+".toRegex())
            if (parts.size >= 2) {
                (parts.first().take(1) + parts.last().take(1)).uppercase()
            } else {
                parts.first().take(1).uppercase()
            }
        } else {
            "UF"
        }

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
            email = "fundraising@mountmiriam.com",
            description = "A not-for-profit cancer treatment centre providing compassionate, affordable oncology care and subsidised treatment to needy patients regardless of background.",
            initials = "MM",
            docTemplateUrl = "https://docs.google.com/document/d/1LeLGtoNpPN26ljF8cv7iRGjnhNJTW8iXs2NI4sFI6mU/copy"
        ),
        Ngo(
            id = "habitat",
            name = "The Habitat Foundation",
            website = "https://habitatfoundation.org.my",
            email = "info@habitatfoundation.org.my",
            description = "A Penang-based conservation organisation supporting biodiversity research, environmental education, and natural habitat protection in Malaysia's tropical rainforests.",
            initials = "HF",
            docTemplateUrl = "https://docs.google.com/document/d/1iBrvuj032V2WNTEzO1pwCqlmucCoigVSo7k4ZfPdVuA/copy"
        ),
        Ngo(
            id = "kawan",
            name = "Kawan",
            website = "https://www.ywampenang.org",
            email = "contact@kawanngo.org",
            description = "A centre to help needy people living in the city of George Town. It is run by an interdenominational Christian organisation called YWAM Malaysia. The acronym stands for Youth With A Mission.",
            initials = "KW",
            docTemplateUrl = "https://docs.google.com/document/d/17_Tc1j1k_rXmmewOeUSfPwMx6zqaRSZhRob4mTcjsEk/copy"
        ),
        Ngo(
            id = "st_nicholas",
            name = "St. Nicholas Home, Penang",
            website = "https://www.stnicholashome.org.my",
            email = "info@stnicholashome.org.my",
            description = "Established in 1926, one of Penang's oldest NGOs providing education, vocational training, and residential care for the blind and visually impaired, empowering them to live independent and dignified lives.",
            initials = "SN",
            docTemplateUrl = "https://docs.google.com/document/d/1KX2d1gN6C25kSn_jUz5xodyyQ2-zBrC0L6wFR_V2IMc/copy"
        ),
        Ngo(
            id = "wcc",
            name = "Women's Centre for Change (WCC) Penang",
            website = "https://www.wccpenang.org",
            email = "support@wccpenang.org",
            description = "A non-profit organisation established in 1985 promoting gender equality and providing crisis support, counselling, shelter, and legal aid to women and children facing domestic violence and abuse in Penang.",
            initials = "WC",
            docTemplateUrl = "https://docs.google.com/document/d/1eAp7xxN1fpHmVg-VQbFai5AwFCfZ_sc93ciIf6Vh534/copy"
        )
    )

    var selectedNgo by mutableStateOf<Ngo>(ngos.first())

    // Active dropdowns
    var activeProposalNgoId by mutableStateOf<String?>(null)

    // Proposals State
    val proposals = mutableStateListOf<Proposal>(
        Proposal(
            id = "prop_1",
            ngoId = "mt_miriam",
            ngoName = "Mount Miriam Cancer Hospital",
            title = "TARUMT Cancer Care Awareness Drive",
            content = "This project proposal outlines a partnership between university students and Mount Miriam Cancer Hospital to conduct cancer awareness seminars and raise MYR 500 in funding to support subsidised chemotherapy treatments for B40 patients.",
            docsLink = "",
            isSent = true,
            isApproved = true
        ),
        Proposal(
            id = "prop_2",
            ngoId = "habitat",
            ngoName = "The Habitat Foundation",
            title = "Eco-Volunteer Rainforest Regeneration",
            content = "Proposal for students to volunteer in conservation and build educational exhibits. Fundraising goal is set to fund soil enrichment and native seedling planting.",
            docsLink = "",
            isSent = false,
            isApproved = false
        )
    )

    // Budgets State


    // Feed State
    val feedItems = mutableStateListOf<String>(
        "MATT DONATED $50",
        "MOUNT MIRIAM CANCER HOSPITAL PROJECT PROPOSAL APPROVED"
    )

    // Social drafts
    val socialDrafts = mutableStateListOf<SocialDraft>()

    // Ask AI Chat State ---------------------------------------------------------------------------
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

    fun onChatTextChange(value: String) { _chatText.value = value }

    // Budget Screen States  -----------------------------------------------------------------------

    private val _activeBudgetDropdownId = MutableStateFlow<String?>(null)

    private val _budgets = MutableStateFlow<List<Budget>>(listOf(
        Budget(
        id = "budget_1",
        name = "HOUSING ASSISTANCE FUND",
        info = "Active: 2024. Next Review: July 1.",
        details = "Provides temporary housing support for displaced families under SDG 11 and 17 partnerships.",
        sheetLink = "https://docs.google.com/spreadsheets/d/1BxiMVs0XRA5nFMdKvBdBZjgmUUq1P1OE/edit"
        ),
        Budget(
            id = "budget_2",
            name = "COMMUNITY GARDEN PROJECT",
            info = "Active: 2024. Next Review: July 1.",
            details = "Promotes sustainable local farming on university grounds. Encourages student-NGO cooperation.",
            sheetLink = "https://docs.google.com/spreadsheets/d/1BxiMVs0XRA5nFMdKvBdBZjgmUUq1P1OE/edit"
        ),
        Budget(
            id = "budget_3",
            name = "EDUCATIONAL SCHOLARSHIP FUND",
            info = "Active: 2024. Next Review: July 1.",
            details = "Supports B40 Malaysian student book purchases, material copying, and transport subsidies.",
            sheetLink = "https://docs.google.com/spreadsheets/d/1BxiMVs0XRA5nFMdKvBdBZjgmUUq1P1OE/edit"
        )))

    private val _selected_budget = MutableStateFlow<Budget?>(null)
    private val _budget_search_query = MutableStateFlow<String>("")
    private val _budget_name = MutableStateFlow<String>("")
    private val _budget_description = MutableStateFlow<String>("")
    private val _sheets_link = MutableStateFlow<String>("")

    private val _create_budget_alert_isActive = MutableStateFlow<Boolean>(false)
    private val _rename_budget_alert_isActive = MutableStateFlow<Boolean>(false)


    var selectedBudget = _selected_budget.asStateFlow()
    var activeBudgetDropdownId = _activeBudgetDropdownId.asStateFlow()
    var budgets = _budgets.asStateFlow()
    var budget_search_query = _budget_search_query.asStateFlow()
    var budget_name = _budget_name.asStateFlow()
    var budget_description = _budget_description.asStateFlow()
    var sheets_link = _sheets_link.asStateFlow()
    var create_budget_alert_isActive = _create_budget_alert_isActive.asStateFlow()
    var rename_budget_alert_isActive = _rename_budget_alert_isActive.asStateFlow()

    fun onActiveBudgetDropdownIdChange(value: String?) { _activeBudgetDropdownId.value = value }
    fun onBudgetSearchQueryChange(value: String) { _budget_search_query.value = value }
    fun onBudgetNameChange(value: String) { _budget_name.value = value }
    fun onBudgetDescriptionChange(value: String) { _budget_description.value = value }
    fun onCreateBudgetAlertIsActiveChange(value: Boolean) { _create_budget_alert_isActive.value = value }
    fun onRenameBudgetAlertIsActiveChange(value: Boolean) { _rename_budget_alert_isActive.value = value }
    fun onSelectedBudgetChange(value: Budget?) { _selected_budget.value = value }
    fun onSheetLinkChange(value: String) { _sheets_link.value = value}

    // Proposal Custom Form
    var proposalTitleInput by mutableStateOf("")
    var proposalContentInput by mutableStateOf("")
    var proposalDocsLink by mutableStateOf("")
    var showEditProposalDialog by mutableStateOf(false)
    var activeEditingProposal by mutableStateOf<Proposal?>(null)

    // --- Actions ---


    fun handleGoogleSignIn(email: String, account: GoogleSignInAccount? = null) {
        authError = null
        if (email.endsWith(".edu.my", ignoreCase = true)) {
            loggedInEmail = email
            googleAccount = account
            isLoggedIn = true
            navigateTo(Screen.Home)
        } else {
            authError = "Access denied. Please sign in with a university Google Workspace account (*.edu.my)."
        }
    }

    fun logout(googleSignInClient: GoogleSignInClient? = null) {
        try {
            googleSignInClient?.signOut()
        } catch (e: Exception) {
            Log.e("MainViewModel", "Google SignOut failed", e)
        }
        isLoggedIn = false
        loggedInEmail = ""
        loggedInDisplayName = ""
        authError = null
        proposalDocsLink = ""
        _backStack.clear()
        _backStack.add(Screen.SignInUp)
    }

    fun prepareNewProposal(ngo: Ngo) {
        selectedNgo = ngo
        proposalDocsLink = ""
        val existing = proposals.find { it.ngoId == ngo.id }
        if (existing != null) {
            proposalTitleInput = existing.title
            proposalContentInput = existing.content
            proposalDocsLink = existing.docsLink
            activeEditingProposal = existing
        } else {
            proposalTitleInput = "University-NGO Collaboration with ${ngo.name}"
            proposalContentInput = "This proposal establishes cooperation between university volunteers and ${ngo.name} to carry out localized fundraising drives supporting their noble societal causes in compliance with SDG 17."
            activeEditingProposal = null
        }
        showEditProposalDialog = true
    }

    fun saveProposal(title: String, content: String, docsLink: String) {
        val editing = activeEditingProposal
        if (editing != null) {
            // Update existing
            val idx = proposals.indexOfFirst { it.id == editing.id }
            if (idx != -1) {
                proposals[idx] = editing.copy(title = title, content = content, docsLink = docsLink)
            }
        } else {
            // Create new
            val newProp = Proposal(
                id = "prop_${UUID.randomUUID()}",
                ngoId = selectedNgo.id,
                ngoName = selectedNgo.name,
                title = title,
                content = content,
                docsLink = docsLink,
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
    fun createNewBudget(name: String, details: String, sheetLink: String) {
        val newBudget = Budget(
            id = "budget_${UUID.randomUUID()}",
            name = name.uppercase(),
            info = "", //info = "Active: 2026. Next Review: July 1.",
            details = details,
            sheetLink = sheetLink
        )
        _budgets.update { budgets -> budgets + newBudget }
        //budgets.add(newBudget)
        feedItems.add(0, "NEW BUDGET CREATED: ${name.uppercase()}")
    }

    fun deleteBudget(id: String) {
        val b = _budgets.value.find { it.id == id }
        if (b != null) {
            _budgets.update { budgets -> budgets - b}
            //budgets.remove(b)
            feedItems.add(0, "DELETED BUDGET: ${b.name}")
        }
    }

    fun renameBudget(id: String, newName: String, newDetails: String) {
        val idx = _budgets.value.indexOfFirst { it.id == id }
        if (idx != -1) {
            val old = _budgets.value[idx]
            _budgets.update {it.toMutableList().apply{ this[idx] = old.copy(name = newName.uppercase(), details = newDetails) }}
            //_budgets.value[idx] = old.copy(name = newName.uppercase())
            feedItems.add(0, "RENAMED BUDGET: ${old.name} TO ${newName.uppercase()}")
        }
    }

    fun verifySheetOwnership(url: String, onResult: (Boolean, String?) -> Unit) {
        val fileId = extractFileIdFromUrl(url)
        if (fileId == null) {
            onResult(false, "Invalid Google Sheets URL format.")
            return
        }

        val credential = googleCredential
        if (credential == null) {
            onResult(false, "Google Drive permission not granted.")
            return
        }

        viewModelScope.launch(Dispatchers.IO) {
            try {
                val driveService = Drive.Builder(
                    AndroidHttp.newCompatibleTransport(),
                    GsonFactory.getDefaultInstance(),
                    credential
                ).setApplicationName("UniFunder").build()

                val file = driveService.files().get(fileId)
                    .setFields("owners(emailAddress)")
                    .execute()

                val ownerEmail = file.owners?.firstOrNull()?.emailAddress
                val isOwner = ownerEmail?.trim()?.equals(loggedInEmail.trim(), ignoreCase = true) == true

                withContext(Dispatchers.Main) {
                    if (isOwner) {
                        onResult(true, null)
                    } else {
                        onResult(false, "Ownership mismatch. Logged in as: $loggedInEmail. Owner: $ownerEmail")
                    }
                }
            } catch (e: Exception) {
                Log.e("MainViewModel", "Drive API error", e)
                val message = when {
                    e.message?.contains("403") == true -> "403 Forbidden: Ensure Google Drive API is enabled in Cloud Console and the permission checkbox was checked during sign-in."
                    e.message?.contains("404") == true -> "404 Not Found: The file ID does not exist or you don't have access."
                    else -> "Error verifying ownership: ${e.localizedMessage}"
                }
                withContext(Dispatchers.Main) {
                    onResult(false, message)
                }
            }
        }
    }

    private fun extractFileIdFromUrl(url: String): String? {
        val pattern = "/spreadsheets/d/([a-zA-Z0-9-_]+)".toRegex()
        return pattern.find(url)?.groupValues?.get(1)
    }

    // AI Chat Operations
    fun sendChatMessage() {
        if (_chatText.value.isBlank()) return
        val promptText = _chatText.value.trim()

        val userMsg = ChatMessage(
            id = "msg_${UUID.randomUUID()}",
            text = promptText,
            isUser = true,
            senderName = if (loggedInDisplayName.isBlank()) "UniFunder User" else loggedInDisplayName,
            initials = userInitials,
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
