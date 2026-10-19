package com.aistudio.shoppinghospitalar.data.repository

import com.aistudio.shoppinghospitalar.data.model.*
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import java.text.NumberFormat
import java.util.Locale
import java.util.UUID

class ShoppingRepository {

    private val _currentUser = MutableStateFlow(
        Profile(
            id = "user_me",
            fullName = "Anderson Silva",
            role = "Vendedor Hospitalar",
            department = "Vendas",
            status = UserStatus.ONLINE,
            phone = "(11) 98765-4321",
            email = "anderson@shoppinghospitalar.com.br"
        )
    )
    val currentUser: StateFlow<Profile> = _currentUser.asStateFlow()

    private val _teamProfiles = MutableStateFlow(
        listOf(
            Profile("p1", "Anderson Silva", "Vendedor Hospitalar", "Vendas", UserStatus.ONLINE),
            Profile("p2", "Dra. Camila Duarte", "Diretora Técnica", "Diretoria", UserStatus.ONLINE),
            Profile("p3", "Carlos Eduardo Mendes", "Líder de Estoque", "Estoque", UserStatus.ONLINE),
            Profile("p4", "Mariana Rios", "Gerente Administrativa", "Administrativo", UserStatus.AUSENTE),
            Profile("p5", "Lucas Ferraz", "Consultor de Vendas", "Vendas", UserStatus.ONLINE),
            Profile("p6", "Beatriz Santos", "Supervisora de Licitações", "Vendas", UserStatus.OCUPADO),
            Profile("p7", "Rodrigo Nogueira", "Auxiliar de Logística", "Estoque", UserStatus.OFFLINE)
        )
    )
    val teamProfiles: StateFlow<List<Profile>> = _teamProfiles.asStateFlow()

    private val _davs = MutableStateFlow(
        listOf(
            Dav(
                id = "dav_1",
                davNumber = "DAV-2026-104",
                clientName = "Hospital Santa Clara",
                description = "Lote de bombas de infusão contínua e insumos descartáveis estéreis",
                sellerId = "user_me",
                sellerName = "Anderson Silva",
                status = DavStatus.EM_ANDAMENTO,
                category = DavCategory.GERAL,
                davValue = 48500.00,
                dueAt = "2026-10-22",
                createdAt = "2026-10-15"
            ),
            Dav(
                id = "dav_2",
                davNumber = "DAV-2026-098",
                clientName = "Prefeitura Municipal de Campinas",
                description = "Equipamentos de monitorização multiparamétrica UTI e oxímetros",
                sellerId = "p6",
                sellerName = "Beatriz Santos",
                status = DavStatus.ABERTO,
                category = DavCategory.PREFEITURA,
                davValue = 125000.00,
                dueAt = "2026-10-25",
                createdAt = "2026-10-14"
            ),
            Dav(
                id = "dav_3",
                davNumber = "DAV-2026-089",
                clientName = "Clínica Ortopédica São Lucas",
                description = "Kits cirúrgicos de artroscopia e implantes esterilizados",
                sellerId = "user_me",
                sellerName = "Anderson Silva",
                status = DavStatus.CONCLUIDO,
                category = DavCategory.RESERVADO,
                davValue = 28900.00,
                dueAt = "2026-10-18",
                createdAt = "2026-10-10"
            ),
            Dav(
                id = "dav_4",
                davNumber = "DAV-2026-074",
                clientName = "Hospital Geral Metropolitano",
                description = "Cateteres venosos centrais e aventais impermeáveis",
                sellerId = "p5",
                sellerName = "Lucas Ferraz",
                status = DavStatus.CONCLUIDO,
                category = DavCategory.GERAL,
                davValue = 64200.00,
                dueAt = "2026-10-12",
                createdAt = "2026-10-05"
            ),
            Dav(
                id = "dav_5",
                davNumber = "DAV-2026-062",
                clientName = "Centro Médico Vida e Saúde",
                description = "Desfibrilador bifásico com DEA e pás pediátricas",
                sellerId = "user_me",
                sellerName = "Anderson Silva",
                status = DavStatus.CANCELADO,
                category = DavCategory.GERAL,
                davValue = 19800.00,
                dueAt = "2026-10-08",
                createdAt = "2026-10-01"
            )
        )
    )
    val davs: StateFlow<List<Dav>> = _davs.asStateFlow()

    private val _announcements = MutableStateFlow(
        listOf(
            Announcement(
                id = "a1",
                title = "Novo protocolo de expedição de insumos cirúrgicos",
                body = "A partir desta segunda-feira, todas as saídas de materiais de classe III e IV exigirão dupla conferência no sistema do estoque.",
                important = true,
                authorName = "Carlos Eduardo Mendes",
                createdAt = "Hoje, 09:30"
            ),
            Announcement(
                id = "a2",
                title = "Metas do Trimestre e Reconhecimento da Equipe de Vendas",
                body = "Parabéns a toda a equipe hospitalar! Atingimos 114% da meta de fechamento de DAVs neste ciclo de compras governamentais.",
                important = false,
                authorName = "Dra. Camila Duarte",
                createdAt = "Ontem, 16:45"
            ),
            Announcement(
                id = "a3",
                title = "Manutenção preventiva dos servidores internos",
                body = "A equipe de TI efetuará atualização de segurança no sábado entre 22h e 00h. O portal mobile permanecerá em leitura assíncrona.",
                important = false,
                authorName = "Mariana Rios",
                createdAt = "16 Out 2026"
            )
        )
    )
    val announcements: StateFlow<List<Announcement>> = _announcements.asStateFlow()

    private val _meetings = MutableStateFlow(
        listOf(
            Meeting(
                id = "m1",
                title = "Alinhamento Semanal de Vendas e DAVs",
                startsAt = "Hoje às 14:00",
                durationMinutes = 45,
                location = "Sala de Reuniões 02 / Google Meet"
            ),
            Meeting(
                id = "m2",
                title = "Comitê de Suprimentos e Logística de Entrega",
                startsAt = "Amanhã às 10:00",
                durationMinutes = 60,
                location = "Auditório Central Shopping Hospitalar"
            ),
            Meeting(
                id = "m3",
                title = "Revisão de Licitações Hospitalares SP/RJ",
                startsAt = "Quinta-feira às 15:30",
                durationMinutes = 30,
                location = "Online"
            )
        )
    )
    val meetings: StateFlow<List<Meeting>> = _meetings.asStateFlow()

    private val _reminders = MutableStateFlow(
        listOf(
            Reminder(
                id = "r1",
                title = "Conferência de lote de cateteres esterilizados",
                notes = "Validação física com registro de lote e lote Anvisa",
                department = ReminderDepartment.ESTOQUE,
                dueAt = "Hoje até 17h",
                completed = false
            ),
            Reminder(
                id = "r2",
                title = "Envio de relatórios de saída para auditoria",
                notes = "Consolidar DAVs de prefeituras entregues na semana",
                department = ReminderDepartment.ADMINISTRATIVO,
                dueAt = "Amanhã 12h",
                completed = true
            ),
            Reminder(
                id = "r3",
                title = "Reposicionar estoque de luvas de procedimento nitrílicas",
                notes = "Corredor 4, prateleiras B1 a B4",
                department = ReminderDepartment.ESTOQUE,
                dueAt = "21 Outubro",
                completed = false
            ),
            Reminder(
                id = "r4",
                title = "Emissão de notas fiscais de faturamento direto",
                notes = "Hospital Metropolitano e Santa Clara",
                department = ReminderDepartment.ADMINISTRATIVO,
                dueAt = "22 Outubro",
                completed = false
            )
        )
    )
    val reminders: StateFlow<List<Reminder>> = _reminders.asStateFlow()

    private val _conversations = MutableStateFlow(
        listOf(
            Conversation("c_geral", "Equipe Geral Shopping Hospitalar", "Carlos: Atualizamos o lote do estoque", unreadCount = 2, isGroup = true, updatedAt = "11:20"),
            Conversation("c_vendas", "Time de Vendas & Licitações", "Beatriz: Proposta enviada para Campinas!", unreadCount = 0, isGroup = true, updatedAt = "10:45"),
            Conversation("c_carlos", "Carlos Mendes (Estoque)", "Anderson, o pedido do Santa Clara já foi separado.", unreadCount = 1, isGroup = false, updatedAt = "09:15"),
            Conversation("c_camila", "Dra. Camila Duarte", "Bom dia Anderson! Excelente resultado na reunião.", unreadCount = 0, isGroup = false, updatedAt = "Ontem")
        )
    )
    val conversations: StateFlow<List<Conversation>> = _conversations.asStateFlow()

    private val _chatMessages = MutableStateFlow(
        listOf(
            ChatMessage("msg_1", "c_geral", "p3", "Carlos Mendes", "Equipe, os insumos da remessa especial acabaram de dar entrada no armazém central.", "10:15", isMe = false),
            ChatMessage("msg_2", "c_geral", "p2", "Dra. Camila Duarte", "Ótimo Carlos! Podem dar vazão aos DAVs prioritários de UTI.", "10:30", isMe = false),
            ChatMessage("msg_3", "c_geral", "user_me", "Anderson Silva", "Perfeito! Já notifiquei o Hospital Santa Clara para liberação da entrega.", "10:42", isMe = true),
            ChatMessage("msg_4", "c_geral", "p3", "Carlos Mendes", "Atualizamos o lote do estoque no sistema agora.", "11:20", isMe = false)
        )
    )
    val chatMessages: StateFlow<List<ChatMessage>> = _chatMessages.asStateFlow()

    fun addDav(
        clientName: String,
        description: String,
        value: Double,
        category: DavCategory,
        dueAt: String
    ) {
        val nextNumber = "DAV-2026-${(110..999).random()}"
        val newDav = Dav(
            id = UUID.randomUUID().toString(),
            davNumber = nextNumber,
            clientName = clientName,
            description = description,
            sellerId = _currentUser.value.id,
            sellerName = _currentUser.value.fullName,
            status = DavStatus.ABERTO,
            category = category,
            davValue = value,
            dueAt = dueAt,
            createdAt = "Hoje"
        )
        _davs.value = listOf(newDav) + _davs.value
    }

    fun updateDavStatus(id: String, newStatus: DavStatus) {
        _davs.value = _davs.value.map {
            if (it.id == id) it.copy(status = newStatus) else it
        }
    }

    fun deleteDav(id: String) {
        _davs.value = _davs.value.filterNot { it.id == id }
    }

    fun toggleReminder(id: String) {
        _reminders.value = _reminders.value.map {
            if (it.id == id) it.copy(completed = !it.completed) else it
        }
    }

    fun addReminder(title: String, notes: String, dept: ReminderDepartment, dueAt: String) {
        val newReminder = Reminder(
            id = UUID.randomUUID().toString(),
            title = title,
            notes = notes,
            department = dept,
            dueAt = dueAt,
            completed = false
        )
        _reminders.value = listOf(newReminder) + _reminders.value
    }

    fun addMeeting(title: String, startsAt: String, duration: Int, location: String) {
        val newMeeting = Meeting(
            id = UUID.randomUUID().toString(),
            title = title,
            startsAt = startsAt,
            durationMinutes = duration,
            location = location
        )
        _meetings.value = _meetings.value + newMeeting
    }

    fun sendChatMessage(conversationId: String, text: String) {
        if (text.isBlank()) return
        val newMsg = ChatMessage(
            id = UUID.randomUUID().toString(),
            conversationId = conversationId,
            senderId = _currentUser.value.id,
            senderName = _currentUser.value.fullName,
            body = text.trim(),
            timestamp = "Agora",
            isMe = true
        )
        _chatMessages.value = _chatMessages.value + newMsg
    }

    fun updateCurrentUser(name: String, status: UserStatus, phone: String) {
        _currentUser.value = _currentUser.value.copy(
            fullName = name,
            status = status,
            phone = phone
        )
    }

    companion object {
        val instance = ShoppingRepository()

        fun formatCurrency(value: Double): String {
            val format = NumberFormat.getCurrencyInstance(Locale("pt", "BR"))
            return format.format(value)
        }
    }
}
