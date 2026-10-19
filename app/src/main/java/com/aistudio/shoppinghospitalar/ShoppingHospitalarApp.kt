package com.aistudio.shoppinghospitalar

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.aistudio.shoppinghospitalar.theme.*
import com.aistudio.shoppinghospitalar.ui.components.AppBottomNav
import com.aistudio.shoppinghospitalar.ui.components.AppTopBar
import com.aistudio.shoppinghospitalar.ui.navigation.Screen
import com.aistudio.shoppinghospitalar.ui.screens.*
import com.aistudio.shoppinghospitalar.viewmodel.ShoppingViewModel
import kotlinx.coroutines.launch

@Composable
fun ShoppingHospitalarApp(
    viewModel: ShoppingViewModel = viewModel()
) {
    val state by viewModel.uiState.collectAsState()
    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    val scope = rememberCoroutineScope()

    // Handle back button: if not on Dashboard, navigate back to Dashboard
    if (state.currentScreen != Screen.Dashboard) {
        BackHandler {
            viewModel.navigateTo(Screen.Dashboard)
        }
    }

    ModalNavigationDrawer(
        drawerState = drawerState,
        drawerContent = {
            ModalDrawerSheet(
                drawerContainerColor = PurpleDark,
                drawerContentColor = Color.White
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(24.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .size(48.dp)
                            .clip(RoundedCornerShape(12.dp))
                            .background(CoralHighlight),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.LocalHospital,
                            contentDescription = null,
                            tint = Color.White,
                            modifier = Modifier.size(28.dp)
                        )
                    }
                    Spacer(modifier = Modifier.height(12.dp))
                    Text(
                        text = "Shopping Hospitalar",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                    Text(
                        text = "Portal Corporativo • Vendas & Estoque",
                        style = MaterialTheme.typography.labelSmall,
                        color = Color.White.copy(alpha = 0.7f)
                    )
                }

                HorizontalDivider(color = Color.White.copy(alpha = 0.15f))
                Spacer(modifier = Modifier.height(10.dp))

                Screen.drawerItems.forEach { screen ->
                    val isSelected = state.currentScreen.route == screen.route
                    NavigationDrawerItem(
                        icon = { Icon(screen.icon, contentDescription = null) },
                        label = { Text(screen.title, fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal) },
                        selected = isSelected,
                        onClick = {
                            viewModel.navigateTo(screen)
                            scope.launch { drawerState.close() }
                        },
                        colors = NavigationDrawerItemDefaults.colors(
                            selectedContainerColor = CoralHighlight,
                            selectedIconColor = Color.White,
                            selectedTextColor = Color.White,
                            unselectedContainerColor = Color.Transparent,
                            unselectedIconColor = Color.White.copy(alpha = 0.8f),
                            unselectedTextColor = Color.White.copy(alpha = 0.8f)
                        ),
                        modifier = Modifier
                            .padding(NavigationDrawerItemDefaults.ItemPadding)
                            .testTag("drawer_${screen.route}")
                    )
                }
            }
        }
    ) {
        Scaffold(
            topBar = {
                AppTopBar(
                    currentScreen = state.currentScreen,
                    userStatus = state.currentUser.status,
                    onMenuClick = {
                        scope.launch {
                            if (drawerState.isClosed) drawerState.open() else drawerState.close()
                        }
                    },
                    onNotificationClick = {
                        viewModel.navigateTo(Screen.Dashboard)
                    }
                )
            },
            bottomBar = {
                AppBottomNav(
                    currentScreen = state.currentScreen,
                    onNavigate = { viewModel.navigateTo(it) }
                )
            },
            containerColor = MaterialTheme.colorScheme.background
        ) { paddingValues ->
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
            ) {
                when (state.currentScreen) {
                    Screen.Dashboard -> {
                        DashboardScreen(
                            state = state,
                            onNavigate = { viewModel.navigateTo(it) },
                            onNewDavClick = { viewModel.showNewDavDialog(true) }
                        )
                    }
                    Screen.Davs -> {
                        DavsScreen(
                            state = state,
                            onSearchChange = { viewModel.setDavSearchQuery(it) },
                            onCategoryChange = { viewModel.setDavCategoryFilter(it) },
                            onStatusChange = { id, status -> viewModel.updateDavStatus(id, status) },
                            onDeleteDav = { viewModel.deleteDav(it) },
                            onNewDavClick = { viewModel.showNewDavDialog(true) }
                        )
                    }
                    Screen.Chat -> {
                        ChatScreen(
                            state = state,
                            onSelectConversation = { viewModel.selectConversation(it) },
                            onSendMessage = { viewModel.sendMessage(it) }
                        )
                    }
                    Screen.Team -> {
                        TeamScreen(
                            state = state,
                            onStartChat = {
                                viewModel.selectConversation("c_geral")
                                viewModel.navigateTo(Screen.Chat)
                            }
                        )
                    }
                    Screen.Meetings -> {
                        MeetingsScreen(
                            state = state,
                            onNewMeetingClick = { viewModel.showNewMeetingDialog(true) },
                            onJoinMeeting = {
                                // Joined meeting action
                            }
                        )
                    }
                    Screen.Reminders -> {
                        RemindersScreen(
                            state = state,
                            onToggleReminder = { viewModel.toggleReminder(it) },
                            onNewReminderClick = { viewModel.showNewReminderDialog(true) }
                        )
                    }
                    Screen.Sales -> {
                        SalesScreen(state = state)
                    }
                    Screen.Profile -> {
                        ProfileScreen(
                            state = state,
                            onUpdateProfile = { name, status, phone ->
                                viewModel.updateProfile(name, status, phone)
                            }
                        )
                    }
                }
            }
        }
    }

    // Dialogs
    if (state.isNewDavDialogVisible) {
        NewDavDialog(
            onDismiss = { viewModel.showNewDavDialog(false) },
            onConfirm = { client, desc, value, category, dueAt ->
                viewModel.createDav(client, desc, value, category, dueAt)
            }
        )
    }

    if (state.isNewMeetingDialogVisible) {
        NewMeetingDialog(
            onDismiss = { viewModel.showNewMeetingDialog(false) },
            onConfirm = { title, startsAt, duration, location ->
                viewModel.createMeeting(title, startsAt, duration, location)
            }
        )
    }

    if (state.isNewReminderDialogVisible) {
        NewReminderDialog(
            onDismiss = { viewModel.showNewReminderDialog(false) },
            onConfirm = { title, notes, dept, dueAt ->
                viewModel.createReminder(title, notes, dept, dueAt)
            }
        )
    }
}
