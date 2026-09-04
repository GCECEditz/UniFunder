package com.example

import android.app.Application
import android.content.Context
import android.content.Intent
import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import androidx.credentials.ClearCredentialStateRequest
import androidx.credentials.CredentialManager
import com.google.api.client.http.javanet.NetHttpTransport
import com.google.api.client.googleapis.extensions.android.gms.auth.GoogleAccountCredential
import com.google.api.client.json.gson.GsonFactory
import com.google.api.services.drive.Drive
import com.google.api.services.sheets.v4.Sheets
import com.google.api.services.sheets.v4.model.ValueRange
import com.example.model.ChatMessage
import com.example.model.GoogleSheetObject
import com.example.model.UserSheetAssociation
import io.github.jan.supabase.postgrest.postgrest
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
import com.example.model.FundraisingProgress
import org.json.JSONArray

class MainViewModel(application: Application) : AndroidViewModel(application) {

    private val prefs = application.getSharedPreferences("UniFunderPrefs", Context.MODE_PRIVATE)

    private val KEY_LOGGED_IN = "is_logged_in"
    private val KEY_EMAIL = "logged_in_email"
    private val KEY_DISPLAY_NAME = "logged_in_display_name"

    private val feedPrefs =
        application.getSharedPreferences(
            "UniFunderFeedPrefs",
            Context.MODE_PRIVATE
        )

    val feedItems = mutableStateListOf<String>()

    private fun getFeedKey(): String {

        val userId =
            loggedInEmail
                .trim()
                .lowercase()

        return if (userId.isBlank()) {
            "feed_items_guest"
        } else {
            "feed_items_$userId"
        }
    }


    private fun saveFeedItems() {

        val jsonArray =
            JSONArray()

        feedItems.forEach { message ->

            jsonArray.put(
                message
            )
        }

        feedPrefs.edit()
            .putString(
                getFeedKey(),
                jsonArray.toString()
            )
            .apply()
    }


    private fun loadFeedItems() {

        val savedFeed =
            feedPrefs.getString(
                getFeedKey(),
                null
            )

        if (savedFeed.isNullOrBlank()) {

            saveFeedItems()

            return
        }

        try {

            val jsonArray =
                JSONArray(
                    savedFeed
                )

            feedItems.clear()

            for (
            i in 0 until jsonArray.length()
            ) {

                feedItems.add(
                    jsonArray.getString(i)
                )
            }

        } catch (e: Exception) {

            Log.e(
                "FEED",
                "Failed to load saved feed",
                e
            )
        }
    }


    fun addFeedNotification(
        message: String
    ) {

        feedItems.add(
            0,
            message
        )

        saveFeedItems()
    }

    // --- Navigation Backstack ---
    private val _backStack = mutableStateListOf(Screen.SignInUp)
    val currentScreen: Screen get() = _backStack.lastOrNull() ?: Screen.SignInUp

    // --- Auth State ---
    var isLoggedIn by mutableStateOf(value = false)
    var loggedInEmail by mutableStateOf("")
    var loggedInDisplayName by mutableStateOf("")
    var authError by mutableStateOf<String?>(null)
    var googleCredential by mutableStateOf<GoogleAccountCredential?>(null)

    init {
        loadLoginState()
        loadFeedItems()
    }

    fun loadLoginState() {
        isLoggedIn = prefs.getBoolean(KEY_LOGGED_IN, false)
        loggedInEmail = prefs.getString(KEY_EMAIL, "") ?: ""
        loggedInDisplayName = prefs.getString(KEY_DISPLAY_NAME, "") ?: ""

        if (isLoggedIn) {
            _backStack.clear()
            _backStack.add(Screen.Home)
        }
    }

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

    // QR / FUNDRAISING PROGRESS

    // Total money raised by EVERY user for selected NGO
    var qrRaisedAmount by mutableStateOf(0.0)
        private set

    // Fundraising target
    var qrGoalAmount by mutableStateOf(0.0)
        private set

    // Link / information encoded inside QR
    var qrContent by mutableStateOf("")
        private set

    // Donation made by currently logged-in user
    var qrMyDonation by mutableStateOf(0.0)
        private set

    // Loading state
    var qrIsLoading by mutableStateOf(false)
        private set

    // Error message
    var qrError by mutableStateOf<String?>(null)
        private set

    // =======================================================
    // QR SESSION DATA
    // Resets when app is completely closed/reopened.
    // =======================================================

    private val qrSessionRaisedAmounts =
        mutableMapOf<String, Double>()

    private val qrSessionMyDonations =
        mutableMapOf<String, Double>()

    private val qrSessionGoalAmounts =
        mutableMapOf<String, Double>()

    private val qrSessionContents =
        mutableMapOf<String, String>()

    private val qrLoadedNgoIds =
        mutableSetOf<String>()


    // Called when user selects another NGO from dropdown
    fun selectNgoForQr(ngo: Ngo) {

        selectedNgo = ngo

        loadFundraisingProgress(
            ngo.id
        )
    }

    // =======================================================
    // SIMULATE QR DONATION
    // =======================================================
    fun simulateQrDonation(): Double {

        // Generate RM5 - RM50
        val donationAmount =
            (5..50).random().toDouble()

        val ngoId =
            selectedNgo.id

        val userId =
            loggedInEmail.trim()

        val currentGoal =
            qrGoalAmount

        val currentQrContent =
            qrContent


        // Increase current displayed progress
        qrRaisedAmount += donationAmount

        // Increase current user's contribution
        qrMyDonation += donationAmount


        // ===================================================
        // SAVE INTO SESSION MEMORY
        // ===================================================

        qrSessionRaisedAmounts[ngoId] =
            qrRaisedAmount

        qrSessionMyDonations[ngoId] =
            qrMyDonation

        qrSessionGoalAmounts[ngoId] =
            qrGoalAmount

        qrSessionContents[ngoId] =
            qrContent

        qrLoadedNgoIds.add(
            ngoId
        )


        // Dashboard simulation
        fundsRaised += donationAmount
        donorsReached += 1


        // Feed notification
        val donationMessage =
            "RM ${
                String.format(
                    java.util.Locale.US,
                    "%.2f",
                    donationAmount
                )
            } DONATED TO ${selectedNgo.name.uppercase()}"


        addFeedNotification(
            donationMessage
        )


        // ===================================================
        // SAVE DONATION TO SUPABASE
        // ===================================================

        if (userId.isNotBlank()) {

            viewModelScope.launch {

                try {

                    // Check whether this user already has
                    // a fundraising record for this NGO
                    val userRecords =
                        withContext(Dispatchers.IO) {

                            SupabaseClient.client
                                .postgrest["FundraisingProgress"]
                                .select {

                                    filter {

                                        eq(
                                            "ngo_id",
                                            ngoId
                                        )

                                        eq(
                                            "user_id",
                                            userId
                                        )
                                    }
                                }
                                .decodeList<FundraisingProgress>()
                        }


                    if (userRecords.isNotEmpty()) {

                        // User already has a row.
                        // Add this donation to their existing amount.
                        val existingAmount =
                            userRecords.sumOf {
                                it.raised_amount
                            }

                        val newAmount =
                            existingAmount +
                                    donationAmount


                        withContext(Dispatchers.IO) {

                            SupabaseClient.client
                                .postgrest["FundraisingProgress"]
                                .update(
                                    {
                                        set(
                                            "raised_amount",
                                            newAmount
                                        )
                                    }
                                ) {

                                    filter {

                                        eq(
                                            "ngo_id",
                                            ngoId
                                        )

                                        eq(
                                            "user_id",
                                            userId
                                        )
                                    }
                                }
                        }

                    } else {

                        // No row for this user yet.
                        // Create a new one.
                        val newRecord =
                            FundraisingProgress(
                                ngo_id =
                                    ngoId,

                                raised_amount =
                                    donationAmount,

                                goal_amount =
                                    currentGoal,

                                qr_content =
                                    currentQrContent,

                                user_id =
                                    userId
                            )


                        withContext(Dispatchers.IO) {

                            SupabaseClient.client
                                .postgrest["FundraisingProgress"]
                                .insert(
                                    newRecord
                                )
                        }
                    }


                } catch (e: Exception) {

                    Log.e(
                        "QR_DONATION",
                        "Failed to save donation",
                        e
                    )

                    qrError =
                        "Donation shown, but failed to save to Supabase."
                }
            }
        }


        return donationAmount
    }

    private fun setDefaultQrProgress(ngoId: String) {

        when (ngoId) {

            "mt_miriam" -> {
                qrRaisedAmount = 350.0
                qrGoalAmount = 500.0
                qrContent = "https://mountmiriam.com"
            }

            "habitat" -> {
                qrRaisedAmount = 225.0
                qrGoalAmount = 500.0
                qrContent = "https://habitatfoundation.org.my"
            }

            "kawan" -> {
                qrRaisedAmount = 150.0
                qrGoalAmount = 500.0
                qrContent = "https://www.ywampenang.org"
            }

            "st_nicholas" -> {
                qrRaisedAmount = 400.0
                qrGoalAmount = 500.0
                qrContent = "https://www.stnicholashome.org.my"
            }

            "wcc" -> {
                qrRaisedAmount = 275.0
                qrGoalAmount = 500.0
                qrContent = "https://www.wccpenang.org"
            }

            else -> {
                qrRaisedAmount = 0.0
                qrGoalAmount = 500.0
                qrContent = selectedNgo.website
            }
        }
    }


    // Read FundraisingProgress table from Supabase
    // =======================================================
// LOAD FUNDRAISING PROGRESS
// =======================================================
    fun loadFundraisingProgress(
        ngoId: String = selectedNgo.id
    ) {

        // ===================================================
        // CHECK SESSION FIRST
        // ===================================================

        if (qrLoadedNgoIds.contains(ngoId)) {

            qrRaisedAmount =
                qrSessionRaisedAmounts[ngoId]
                    ?: 0.0

            qrMyDonation =
                qrSessionMyDonations[ngoId]
                    ?: 0.0

            qrGoalAmount =
                qrSessionGoalAmounts[ngoId]
                    ?: 500.0

            qrContent =
                qrSessionContents[ngoId]
                    ?: selectedNgo.website

            qrError = null

            return
        }


        // ===================================================
        // FIRST TIME OPENING THIS NGO
        // LOAD SUPABASE / DEFAULT DATA
        // ===================================================

        viewModelScope.launch {

            qrIsLoading = true
            qrError = null

            try {

                val records =
                    withContext(Dispatchers.IO) {

                        SupabaseClient.client
                            .postgrest["FundraisingProgress"]
                            .select {

                                filter {

                                    eq(
                                        "ngo_id",
                                        ngoId
                                    )
                                }
                            }
                            .decodeList<FundraisingProgress>()
                    }


                // ===================================================
                // NO SUPABASE DATA
                // USE DEFAULT DEMO VALUE
                // ===================================================

                if (records.isEmpty()) {

                    setDefaultQrProgress(
                        ngoId
                    )

                    qrMyDonation = 0.0

                    qrError = null
                }

                // ===================================================
                // SUPABASE DATA FOUND
                // ===================================================

                else {

                    qrRaisedAmount =
                        records.sumOf {
                            it.raised_amount
                        }


                    qrGoalAmount =
                        records
                            .firstOrNull {
                                it.goal_amount > 0.0
                            }
                            ?.goal_amount
                            ?: records.first()
                                .goal_amount


                    qrContent =
                        records
                            .firstOrNull {
                                it.qr_content
                                    .isNotBlank()
                            }
                            ?.qr_content
                            ?: selectedNgo.website


                    val currentUserId =
                        loggedInEmail.trim()


                    qrMyDonation =

                        if (
                            currentUserId.isBlank()
                        ) {

                            0.0

                        } else {

                            records
                                .filter {

                                    it.user_id.equals(
                                        currentUserId,
                                        ignoreCase = true
                                    )
                                }
                                .sumOf {

                                    it.raised_amount

                                }
                        }
                }


                // ===================================================
                // SAVE INITIAL RESULT INTO SESSION
                // ===================================================

                qrSessionRaisedAmounts[ngoId] =
                    qrRaisedAmount

                qrSessionMyDonations[ngoId] =
                    qrMyDonation

                qrSessionGoalAmounts[ngoId] =
                    qrGoalAmount

                qrSessionContents[ngoId] =
                    qrContent

                qrLoadedNgoIds.add(
                    ngoId
                )

            }

            catch (e: Exception) {

                Log.e(
                    "MainViewModel",
                    "Failed to load fundraising progress",
                    e
                )


                // Use default demo values
                setDefaultQrProgress(
                    ngoId
                )

                qrMyDonation = 0.0


                // ===================================================
                // SAVE DEFAULT INTO SESSION TOO
                // ===================================================

                qrSessionRaisedAmounts[ngoId] =
                    qrRaisedAmount

                qrSessionMyDonations[ngoId] =
                    qrMyDonation

                qrSessionGoalAmounts[ngoId] =
                    qrGoalAmount

                qrSessionContents[ngoId] =
                    qrContent

                qrLoadedNgoIds.add(
                    ngoId
                )


                qrError =
                    "Demo data displayed - Supabase unavailable."
            }

            finally {

                qrIsLoading = false
            }
        }
    }

    // Active dropdowns
    var activeProposalNgoId by mutableStateOf<String?>(null)

    // Proposals State
    val proposals = mutableStateListOf<Proposal>(
//        Proposal(
//            id = "prop_1",
//            ngoId = "mt_miriam",
//            ngoName = "Mount Miriam Cancer Hospital",
//            title = "TARUMT Cancer Care Awareness Drive",
//            content = "This project proposal outlines a partnership between university students and Mount Miriam Cancer Hospital to conduct cancer awareness seminars and raise MYR 500 in funding to support subsidised chemotherapy treatments for B40 patients.",
//            docsLink = "",
//            isSent = true,
//            isApproved = true
//        ),
//        Proposal(
//            id = "prop_2",
//            ngoId = "habitat",
//            ngoName = "The Habitat Foundation",
//            title = "Eco-Volunteer Rainforest Regeneration",
//            content = "Proposal for students to volunteer in conservation and build educational exhibits. Fundraising goal is set to fund soil enrichment and native seedling planting.",
//            docsLink = "",
//            isSent = false,
//            isApproved = false
//        )
    )

    // Budgets State

    // Social drafts
    val socialDrafts = mutableStateListOf<SocialDraft>()

    // Ask AI Chat State ---------------------------------------------------------------------------
    private val _isAiLoading = MutableStateFlow(false)
    private val _chatText = MutableStateFlow<String>("")
    private val _chatMessages = MutableStateFlow<List<ChatMessage>>(emptyList()    )

    val aiChatMessages: StateFlow<List<ChatMessage>> = _chatMessages.asStateFlow()
    val aiChatText: StateFlow<String> = _chatText.asStateFlow()
    val isAiLoading = _isAiLoading.asStateFlow()

    fun onChatTextChange(value: String) { _chatText.value = value }

    // Budget Screen States  -----------------------------------------------------------------------

    private val _activeBudgetDropdownId = MutableStateFlow<String?>(null)

    private val _budgets = MutableStateFlow<List<Budget>>(emptyList())

    private val _selected_budget = MutableStateFlow<Budget?>(null)
    private val _budget_search_query = MutableStateFlow<String>("")
    private val _budget_name = MutableStateFlow<String>("")
    private val _budget_description = MutableStateFlow<String>("")
    private val _sheets_link = MutableStateFlow<String>("")

    private val _create_budget_alert_isActive = MutableStateFlow<Boolean>(false)
    private val _rename_budget_alert_isActive = MutableStateFlow<Boolean>(false)
    private val _delete_budget_alert_isActive = MutableStateFlow<Boolean>(false)


    var selectedBudget = _selected_budget.asStateFlow()
    var activeBudgetDropdownId = _activeBudgetDropdownId.asStateFlow()
    var budgets = _budgets.asStateFlow()
    var budget_search_query = _budget_search_query.asStateFlow()
    var budget_name = _budget_name.asStateFlow()
    var budget_description = _budget_description.asStateFlow()
    var sheets_link = _sheets_link.asStateFlow()
    var create_budget_alert_isActive = _create_budget_alert_isActive.asStateFlow()
    var rename_budget_alert_isActive = _rename_budget_alert_isActive.asStateFlow()
    var delete_budget_alert_isActive = _delete_budget_alert_isActive.asStateFlow()

    private val _authIntent = MutableStateFlow<Intent?>(null)
    val authIntent = _authIntent.asStateFlow()
    fun onAuthIntentHandled() { _authIntent.value = null }

    fun onActiveBudgetDropdownIdChange(value: String?) { _activeBudgetDropdownId.value = value }
    fun onBudgetSearchQueryChange(value: String) { _budget_search_query.value = value }
    fun onBudgetNameChange(value: String) { _budget_name.value = value }
    fun onBudgetDescriptionChange(value: String) { _budget_description.value = value }
    fun onCreateBudgetAlertIsActiveChange(value: Boolean) { _create_budget_alert_isActive.value = value }
    fun onRenameBudgetAlertIsActiveChange(value: Boolean) { _rename_budget_alert_isActive.value = value }
    fun onDeleteBudgetAlertIsActiveChange(value: Boolean) { _delete_budget_alert_isActive.value = value }
    fun onSelectedBudgetChange(value: Budget?) { _selected_budget.value = value }
    fun onSheetLinkChange(value: String) { _sheets_link.value = value}

    // Proposal Custom Form
    var proposalTitleInput by mutableStateOf("")
    var proposalContentInput by mutableStateOf("")
    var proposalDocsLink by mutableStateOf("")
    var showEditProposalDialog by mutableStateOf(false)
    var activeEditingProposal by mutableStateOf<Proposal?>(null)

    // --- Actions ---

    fun navigateTo(screen: Screen) {
        if (_backStack.lastOrNull() != screen) {
            _backStack.add(screen)
        }
    }

    fun navigateBack(): Boolean {
        if (currentScreen == Screen.AskAi) {
            resetAiPromptIfPending()
        }
        if (_backStack.size > 1) {
            _backStack.removeAt(_backStack.lastIndex)
            return true
        }
        return false
    }


    fun handleGoogleSignIn(email: String) {
        Log.d("MainViewModel", "Handling Google Sign-In for: $email")
        authError = null
        if (email.endsWith(".edu.my", ignoreCase = true)) {
            Log.d("MainViewModel", "Email suffix check passed")
            loggedInEmail = email
            isLoggedIn = true

            prefs.edit()
                .putBoolean(KEY_LOGGED_IN, true)
                .putString(KEY_EMAIL, email)
                .putString(KEY_DISPLAY_NAME, loggedInDisplayName)
                .apply()

            loadFeedItems()

            navigateTo(Screen.Home)
        } else {
            authError = "Access denied. Please sign in with a university Google Workspace account (*.edu.my)."
        }
    }

    fun logout(context: Context) {
        viewModelScope.launch {
            try {
                val credentialManager = CredentialManager.create(context)
                credentialManager.clearCredentialState(ClearCredentialStateRequest())
            } catch (e: Exception) {
                Log.e("MainViewModel", "SignOut failed", e)
            }
        }
        isLoggedIn = false
        loggedInEmail = ""
        loggedInDisplayName = ""
        authError = null
        proposalDocsLink = ""

        prefs.edit().clear().apply()

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
        addFeedNotification(
            "PROPOSAL SENT TO ${proposal.ngoName.uppercase()}: ${proposal.title.uppercase()}"
        )
        
        // Dynamic approval simulation for prototype fun!
        viewModelScope.launch {
            // Simulate NGO review
            kotlinx.coroutines.delay(3.seconds)
            val updatedIdx = proposals.indexOfFirst { it.id == proposal.id }
            if (updatedIdx != -1) {
                proposals[updatedIdx] = proposals[updatedIdx].copy(isApproved = true)
                addFeedNotification(
                    "${proposal.ngoName.uppercase()} PROJECT PROPOSAL APPROVED"
                )
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
        addFeedNotification(
            "NEW BUDGET CREATED: ${name.uppercase()}"
        )
    }

    suspend fun deleteBudget(id: String) {
        val b = _budgets.value.find { it.id == id }
        val userEmail = loggedInEmail
        if (b != null && userEmail.isNotBlank()) {
            // If it's a Supabase record (numeric ID), delete both association and sheet
            val longId = id.toLongOrNull()
            if (longId != null) {
                // 1. Delete the link between user and sheet
                deleteAssociationBySheetId(longId, userEmail)
                // 2. Delete the actual sheet record
                deleteGoogleSheet(longId)
            }
            
            _budgets.update { budgets -> budgets - b}
            addFeedNotification(
                "DELETED BUDGET: ${b.name}"
            )
        }
    }

    suspend fun renameBudget(id: String, newName: String, newDetails: String) {
        val idx = _budgets.value.indexOfFirst { it.id == id }
        if (idx != -1) {
            val old = _budgets.value[idx]
            
            // If it's a Supabase record (numeric ID), update remote
            val longId = id.toLongOrNull()
            if (longId != null) {
                updateGoogleSheet(
                    GoogleSheetObject(id = longId, name = newName, description = newDetails)
                )
            }

            _budgets.update {it.toMutableList().apply{ this[idx] = old.copy(name = newName.uppercase(), details = newDetails) }}
            addFeedNotification(
                "RENAMED BUDGET: ${old.name} TO ${newName.uppercase()}"
            )
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
                    NetHttpTransport(),
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

    private var _isPendingAiFromBudget = false

    fun askAiAboutBudget(budget: Budget) {
        val fileId = extractFileIdFromUrl(budget.sheetLink)
        if (fileId == null) {
            onChatTextChange("Hello UniFunder AI! I'd like feedback on my budget: ${budget.name}. Unfortunately, I couldn't extract the data from the link: ${budget.sheetLink}")
            navigateTo(Screen.AskAi)
            return
        }

        val credential = googleCredential
        if (credential == null) {
            onChatTextChange("Hello UniFunder AI! Please provide feedback on my budget '${budget.name}' details: ${budget.details}")
            navigateTo(Screen.AskAi)
            return
        }

        _isAiLoading.value = true
        _isPendingAiFromBudget = true
        navigateTo(Screen.AskAi)

        viewModelScope.launch(Dispatchers.IO) {
            try {
                val sheetsService = Sheets.Builder(
                    NetHttpTransport(),
                    GsonFactory.getDefaultInstance(),
                    credential
                ).setApplicationName("UniFunder").build()

                // Fetch first few rows/cols to get an idea of the budget
                val response: ValueRange = sheetsService.spreadsheets().values()
                    .get(fileId, "A1:J30")
                    .execute()

                val values = response.getValues()
                val sheetData = if (values.isNullOrEmpty()) {
                    "No data found in sheet."
                } else {
                    values.joinToString("\n") { row -> row.joinToString(" | ") }
                }

                withContext(Dispatchers.Main) {
                    onChatTextChange(
                        "Hello UniFunder AI! Here is my budget data from '${budget.name}' (${budget.details}). Please analyze it and give me feedback:\n\n$sheetData"
                    )
                    _isAiLoading.value = false
                }
            } catch (e: Exception) {
                Log.e("MainViewModel", "Sheets API error", e)
                
                if (e is com.google.api.client.googleapis.extensions.android.gms.auth.UserRecoverableAuthIOException) {
                    _authIntent.value = e.intent
                    _isAiLoading.value = false
                    return@launch
                }

                val errorMessage = when {
                    e is com.google.api.client.googleapis.json.GoogleJsonResponseException -> {
                        "Google API Error: ${e.details?.message ?: e.statusCode}"
                    }
                    else -> e.localizedMessage ?: e.message ?: e.javaClass.simpleName
                }
                withContext(Dispatchers.Main) {
                    onChatTextChange(
                        "Hello UniFunder AI! I'm linking my budget '${budget.name}' but had trouble reading the sheet directly ($errorMessage). Details: ${budget.details}. Link: ${budget.sheetLink}"
                    )
                    _isAiLoading.value = false
                }
            }
        }
    }

    fun resetAiPromptIfPending() {
        if (_isPendingAiFromBudget) {
            onChatTextChange("")
            _isAiLoading.value = false
            _isPendingAiFromBudget = false
        }
    }

    // AI Chat Operations
    fun sendChatMessage() {
        if (_chatText.value.isBlank()) return
        _isPendingAiFromBudget = false // User sent it, so it's no longer pending
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
        addFeedNotification(
            "SHARED A POST ON ${platform.uppercase()}"
        )
        
        // Simulate a new donor from social media!
        fundsRaised += 50.0
        donorsReached += 1
    }

    // --- Supabase CRUD Operations ---

    // GoogleSheetObject CRUD
    suspend fun insertGoogleSheet(sheet: GoogleSheetObject): GoogleSheetObject? = withContext(Dispatchers.IO) {
        try {
            val response = SupabaseClient.client.postgrest["GoogleSheetObject"].insert(sheet) {
                select() // Required to get the inserted record back
            }
            response.decodeSingle<GoogleSheetObject>()
        } catch (e: Exception) {
            Log.e("MainViewModel", "Supabase insert error", e)
            null
        }
    }

    suspend fun updateGoogleSheet(sheet: GoogleSheetObject): Boolean = withContext(Dispatchers.IO) {
        try {
            SupabaseClient.client.postgrest["GoogleSheetObject"].update(sheet) {
                filter {
                    eq("id", sheet.id ?: 0L)
                }
            }
            true
        } catch (e: Exception) {
            Log.e("MainViewModel", "Supabase update error", e)
            false
        }
    }

    suspend fun deleteGoogleSheet(id: Long): Boolean = withContext(Dispatchers.IO) {
        try {
            SupabaseClient.client.postgrest["GoogleSheetObject"].delete {
                filter {
                    eq("id", id)
                }
            }
            true
        } catch (e: Exception) {
            Log.e("MainViewModel", "Supabase delete error", e)
            false
        }
    }

    // UserSheetAssociation CRUD
    suspend fun associateUserWithSheet(association: UserSheetAssociation): Boolean = withContext(Dispatchers.IO) {
        try {
            SupabaseClient.client.postgrest["UserSheetAssociation"].insert(association)
            true
        } catch (e: Exception) {
            Log.e("MainViewModel", "Supabase association error", e)
            false
        }
    }

    suspend fun deleteAssociationBySheetId(sheetId: Long, userId: String): Boolean = withContext(Dispatchers.IO) {
        try {
            SupabaseClient.client.postgrest["UserSheetAssociation"].delete {
                filter {
                    eq("sheet_id", sheetId)
                    eq("user_id", userId)
                }
            }
            true
        } catch (e: Exception) {
            Log.e("MainViewModel", "Supabase delete association by sheet error", e)
            false
        }
    }

    /**
     * High-level operation to create a new sheet record and associate it with the current user.
     */
    suspend fun createAndAssociateSheet(name: String, description: String, sheetUrl: String): Boolean {
        val userEmail = loggedInEmail
        if (userEmail.isBlank()) return false

        val newSheet = insertGoogleSheet(
            GoogleSheetObject(name = name, description = description, sheetUrl = sheetUrl)
        )
        val sheetId = newSheet?.id ?: return false

        return associateUserWithSheet(
            UserSheetAssociation(userId = userEmail, sheetId = sheetId)
        )
    }

    /**
     * Fetches all budget sheets associated with the current user's email
     * and updates the local _budgets state flow.
     */
    suspend fun fetchUserBudgetsFromSupabase(): Boolean = withContext(Dispatchers.IO) {
        val userEmail = loggedInEmail
        if (userEmail.isBlank()) return@withContext false

        try {
            // Fetch associations with the joined sheet data
            val associations = SupabaseClient.client.postgrest["UserSheetAssociation"]
                .select(columns = io.github.jan.supabase.postgrest.query.Columns.raw("*, GoogleSheetObject(*)")) {
                    filter {
                        eq("user_id", userEmail)
                    }
                }.decodeList<UserSheetAssociation>()

            // Map database records to our UI Budget models
            val fetchedBudgets = associations.mapNotNull { assoc ->
                assoc.sheet?.let { s ->
                    Budget(
                        id = s.id.toString(),
                        name = s.name,
                        info = "Created: ${s.createdAt?.substringBefore("T") ?: ""}",
                        details = s.description ?: "",
                        sheetLink = s.sheetUrl
                    )
                }
            }

            _budgets.value = fetchedBudgets
            true
        } catch (e: Exception) {
            Log.e("MainViewModel", "Error fetching user budgets", e)
            false
        }
    }
}
