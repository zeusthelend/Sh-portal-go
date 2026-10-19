package com.aistudio.shoppinghospitalar.ui.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Chat
import androidx.compose.material.icons.filled.*
import androidx.compose.ui.graphics.vector.ImageVector

sealed class Screen(
    val route: String,
    val title: String,
    val icon: ImageVector
) {
    object Dashboard : Screen("dashboard", "Início", Icons.Default.Home)
    object Davs : Screen("davs", "DAVs", Icons.Default.Assignment)
    object Chat : Screen("chat", "Chat", Icons.AutoMirrored.Filled.Chat)
    object Team : Screen("team", "Equipe", Icons.Default.People)
    object Meetings : Screen("meetings", "Reuniões", Icons.Default.CalendarMonth)
    object Reminders : Screen("reminders", "Agendas", Icons.Default.Checklist)
    object Sales : Screen("sales", "Vendas", Icons.Default.TrendingUp)
    object Profile : Screen("profile", "Perfil", Icons.Default.Person)

    companion object {
        val bottomNavItems = listOf(
            Dashboard,
            Davs,
            Chat,
            Reminders,
            Profile
        )

        val drawerItems = listOf(
            Dashboard,
            Davs,
            Chat,
            Team,
            Meetings,
            Reminders,
            Sales,
            Profile
        )
    }
}
