package com.shjman.polygon2

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.ui.graphics.vector.ImageVector

sealed class BottomNavItem(var title: String, var icon: ImageVector, var screenRoute: String) {
    object Home : BottomNavItem("Home", Icons.Default.Home, "home")
    object Spent : BottomNavItem("Spent", Icons.Filled.AttachMoney, "spent")
    object Overview : BottomNavItem("Overview", Icons.Default.Segment, "overview")
    object Setting : BottomNavItem("Setting", Icons.Default.Settings, "setting")
}