package com.aistudio.shoppinghospitalar.data.model

enum class DavStatus(val label: String) {
    ABERTO("Aberto"),
    EM_ANDAMENTO("Em andamento"),
    CONCLUIDO("Concluído"),
    CANCELADO("Desistiu")
}

enum class DavCategory(val label: String) {
    GERAL("Geral"),
    PREFEITURA("Prefeitura"),
    RESERVADO("Reservado")
}

data class Dav(
    val id: String,
    val davNumber: String,
    val clientName: String,
    val description: String,
    val sellerId: String,
    val sellerName: String,
    val status: DavStatus = DavStatus.ABERTO,
    val category: DavCategory = DavCategory.GERAL,
    val davValue: Double = 0.0,
    val dueAt: String,
    val createdAt: String,
    val attachments: List<String> = emptyList()
)

enum class UserStatus(val label: String) {
    ONLINE("Online"),
    AUSENTE("Ausente"),
    OCUPADO("Ocupado"),
    OFFLINE("Offline")
}

data class Profile(
    val id: String,
    val fullName: String,
    val role: String,
    val department: String,
    val status: UserStatus = UserStatus.ONLINE,
    val phone: String = "",
    val email: String = "",
    val avatarUrl: String? = null,
    val active: Boolean = true,
    val approved: Boolean = true
)

data class Announcement(
    val id: String,
    val title: String,
    val body: String,
    val important: Boolean = false,
    val authorName: String = "Diretoria",
    val createdAt: String,
    val mediaUrls: List<String> = emptyList()
)

data class Meeting(
    val id: String,
    val title: String,
    val startsAt: String,
    val durationMinutes: Int = 30,
    val location: String = "Online / Sala de Reunião",
    val status: String = "agendada"
)

enum class ReminderDepartment(val label: String) {
    ESTOQUE("Agenda do Estoque"),
    ADMINISTRATIVO("Agenda Administrativa")
}

data class Reminder(
    val id: String,
    val title: String,
    val notes: String = "",
    val department: ReminderDepartment,
    val dueAt: String,
    val completed: Boolean = false
)

data class ChatMessage(
    val id: String,
    val conversationId: String,
    val senderId: String,
    val senderName: String,
    val body: String,
    val timestamp: String,
    val isMe: Boolean = false,
    val isFavorite: Boolean = false
)

data class Conversation(
    val id: String,
    val name: String,
    val lastMessage: String,
    val unreadCount: Int = 0,
    val isGroup: Boolean = false,
    val updatedAt: String
)

data class SalesSummary(
    val totalCount: Int,
    val completedCount: Int,
    val openCount: Int,
    val inProgressCount: Int,
    val cancelledCount: Int,
    val totalRevenue: Double,
    val conversionRate: Double
)
