package com.aistudio.shoppinghospitalar.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.aistudio.shoppinghospitalar.data.model.*
import com.aistudio.shoppinghospitalar.data.repository.ShoppingRepository
import com.aistudio.shoppinghospitalar.ui.navigation.Screen
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

data class ShoppingUiState(
    val currentUser: Profile,
    val team: List<Profile>,
    val davs: List<Dav>,
    val filteredDavs: List<Dav>,
    val announcements: List<Announcement>,
    val meetings: List<Meeting>,
    val reminders: List<Reminder>,
    val conversations: List<Conversation>,
    val currentMessages: List<ChatMessage>,
    val currentScreen: Screen = Screen.Dashboard,
    val davSearchQuery: String = "",
    val selectedDavCategory: String = "TODOS",
    val isNewDavDialogVisible: Boolean = false,
    val isNewReminderDialogVisible: Boolean = false,
    val isNewMeetingDialogVisible: Boolean = false,
    val activeConversationId: String = "c_geral",
    val salesSummary: SalesSummary
)

class ShoppingViewModel(
    private val repository: ShoppingRepository = ShoppingRepository.instance
) : ViewModel() {

    private val _currentScreen = MutableStateFlow<Screen>(Screen.Dashboard)
    private val _davSearchQuery = MutableStateFlow("")
    private val _selectedDavCategory = MutableStateFlow("TODOS")
    private val _isNewDavDialogVisible = MutableStateFlow(false)
    private val _isNewReminderDialogVisible = MutableStateFlow(false)
    private val _isNewMeetingDialogVisible = MutableStateFlow(false)
    private val _activeConversationId = MutableStateFlow("c_geral")

    val uiState: StateFlow<ShoppingUiState> = combine(
        repository.currentUser,
        repository.teamProfiles,
        repository.davs,
        repository.announcements,
        repository.meetings
    ) { user, team, davs, announcements, meetings ->
        Tuple5(user, team, davs, announcements, meetings)
    }.combine(
        combine(
            repository.reminders,
            repository.conversations,
            repository.chatMessages,
            _currentScreen,
            _davSearchQuery
        ) { reminders, conversations, messages, screen, query ->
            Tuple5(reminders, conversations, messages, screen, query)
        }
    ) { (user, team, davs, announcements, meetings), (reminders, conversations, messages, screen, query) ->
        val cat = _selectedDavCategory.value
        val filtered = davs.filter { dav ->
            val matchesCategory = when (cat) {
                "GERAL" -> dav.category == DavCategory.GERAL
                "PREFEITURA" -> dav.category == DavCategory.PREFEITURA
                "RESERVADO" -> dav.category == DavCategory.RESERVADO
                else -> true
            }
            val matchesQuery = query.isBlank() ||
                dav.clientName.contains(query, ignoreCase = true) ||
                dav.davNumber.contains(query, ignoreCase = true) ||
                dav.sellerName.contains(query, ignoreCase = true)

            matchesCategory && matchesQuery
        }

        val completed = davs.filter { it.status == DavStatus.CONCLUIDO }
        val open = davs.filter { it.status == DavStatus.ABERTO }
        val inProgress = davs.filter { it.status == DavStatus.EM_ANDAMENTO }
        val cancelled = davs.filter { it.status == DavStatus.CANCELADO }
        val totalRevenue = completed.sumOf { it.davValue }
        val totalNonCancelled = (davs.size - cancelled.size).coerceAtLeast(1)
        val conversion = (completed.size.toDouble() / totalNonCancelled.toDouble()) * 100.0

        val salesSummary = SalesSummary(
            totalCount = davs.size,
            completedCount = completed.size,
            openCount = open.size,
            inProgressCount = inProgress.size,
            cancelledCount = cancelled.size,
            totalRevenue = totalRevenue,
            conversionRate = conversion
        )

        val convId = _activeConversationId.value
        val convMessages = messages.filter { it.conversationId == convId }

        ShoppingUiState(
            currentUser = user,
            team = team,
            davs = davs,
            filteredDavs = filtered,
            announcements = announcements,
            meetings = meetings,
            reminders = reminders,
            conversations = conversations,
            currentMessages = convMessages,
            currentScreen = screen,
            davSearchQuery = query,
            selectedDavCategory = cat,
            isNewDavDialogVisible = _isNewDavDialogVisible.value,
            isNewReminderDialogVisible = _isNewReminderDialogVisible.value,
            isNewMeetingDialogVisible = _isNewMeetingDialogVisible.value,
            activeConversationId = convId,
            salesSummary = salesSummary
        )
    }.stateIn(
        viewModelScope,
        SharingStarted.WhileSubscribed(5000),
        ShoppingUiState(
            currentUser = Profile("me", "Anderson Silva", "Vendedor", "Vendas"),
            team = emptyList(),
            davs = emptyList(),
            filteredDavs = emptyList(),
            announcements = emptyList(),
            meetings = emptyList(),
            reminders = emptyList(),
            conversations = emptyList(),
            currentMessages = emptyList(),
            salesSummary = SalesSummary(0, 0, 0, 0, 0, 0.0, 0.0)
        )
    )

    fun navigateTo(screen: Screen) {
        _currentScreen.value = screen
    }

    fun setDavSearchQuery(query: String) {
        _davSearchQuery.value = query
    }

    fun setDavCategoryFilter(category: String) {
        _selectedDavCategory.value = category
    }

    fun showNewDavDialog(show: Boolean) {
        _isNewDavDialogVisible.value = show
    }

    fun showNewReminderDialog(show: Boolean) {
        _isNewReminderDialogVisible.value = show
    }

    fun showNewMeetingDialog(show: Boolean) {
        _isNewMeetingDialogVisible.value = show
    }

    fun selectConversation(id: String) {
        _activeConversationId.value = id
    }

    fun createDav(
        client: String,
        description: String,
        value: Double,
        category: DavCategory,
        dueAt: String
    ) {
        repository.addDav(client, description, value, category, dueAt)
        _isNewDavDialogVisible.value = false
    }

    fun updateDavStatus(id: String, status: DavStatus) {
        repository.updateDavStatus(id, status)
    }

    fun deleteDav(id: String) {
        repository.deleteDav(id)
    }

    fun toggleReminder(id: String) {
        repository.toggleReminder(id)
    }

    fun createReminder(title: String, notes: String, dept: ReminderDepartment, dueAt: String) {
        repository.addReminder(title, notes, dept, dueAt)
        _isNewReminderDialogVisible.value = false
    }

    fun createMeeting(title: String, startsAt: String, duration: Int, location: String) {
        repository.addMeeting(title, startsAt, duration, location)
        _isNewMeetingDialogVisible.value = false
    }

    fun sendMessage(text: String) {
        repository.sendChatMessage(_activeConversationId.value, text)
    }

    fun updateProfile(name: String, status: UserStatus, phone: String) {
        repository.updateCurrentUser(name, status, phone)
    }
}

private data class Tuple5<A, B, C, D, E>(
    val a: A, val b: B, val c: C, val d: D, val e: E
)
