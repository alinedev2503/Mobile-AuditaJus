package com.example.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.FolderShared
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.MenuBook
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.outlined.CalendarMonth
import androidx.compose.material.icons.outlined.FolderShared
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.MenuBook
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

enum class AppNavDestination(
    val title: String,
    val selectedIcon: ImageVector,
    val unselectedIcon: ImageVector,
    val testTag: String
) {
    HOME("Home", Icons.Filled.Home, Icons.Outlined.Home, "nav_home"),
    CASES("Meus Casos", Icons.Filled.FolderShared, Icons.Outlined.FolderShared, "nav_cases"),
    GUIDES("Guias", Icons.Filled.MenuBook, Icons.Outlined.MenuBook, "nav_guides"),
    CALENDAR("Agenda", Icons.Filled.CalendarMonth, Icons.Outlined.CalendarMonth, "nav_calendar"),
    SETTINGS("Ajustes", Icons.Filled.Settings, Icons.Outlined.Settings, "nav_settings")
}

@Composable
fun AppBottomNavBar(
    currentDestination: AppNavDestination,
    onNavigate: (AppNavDestination) -> Unit,
    favoriteGuidesCount: Int = 0,
    modifier: Modifier = Modifier
) {
    NavigationBar(
        modifier = modifier,
        containerColor = MaterialTheme.colorScheme.surface,
        contentColor = MaterialTheme.colorScheme.onSurface
    ) {
        AppNavDestination.entries.forEach { destination ->
            val isSelected = destination == currentDestination
            NavigationBarItem(
                selected = isSelected,
                onClick = { onNavigate(destination) },
                icon = {
                    BadgedBox(
                        badge = {
                            if (destination == AppNavDestination.GUIDES && favoriteGuidesCount > 0) {
                                Badge(
                                    containerColor = Color(0xFFF59E0B),
                                    contentColor = Color.White
                                ) {
                                    Text(
                                        text = "$favoriteGuidesCount",
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 10.sp
                                    )
                                }
                            }
                        }
                    ) {
                        Icon(
                            imageVector = if (isSelected) destination.selectedIcon else destination.unselectedIcon,
                            contentDescription = destination.title
                        )
                    }
                },
                label = {
                    Text(
                        text = destination.title,
                        style = MaterialTheme.typography.labelMedium
                    )
                },
                colors = NavigationBarItemDefaults.colors(
                    selectedIconColor = MaterialTheme.colorScheme.primary,
                    selectedTextColor = MaterialTheme.colorScheme.primary,
                    indicatorColor = MaterialTheme.colorScheme.secondaryContainer,
                    unselectedIconColor = MaterialTheme.colorScheme.secondary,
                    unselectedTextColor = MaterialTheme.colorScheme.secondary
                ),
                modifier = Modifier.testTag(destination.testTag)
            )
        }
    }
}
