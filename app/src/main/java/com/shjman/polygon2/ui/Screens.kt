package com.shjman.polygon2.ui

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.ui.graphics.vector.ImageVector

sealed class Screens(var screenRoute: String) {
    object EditSpending : Screens("editSpending")
    object Categories : Screens("Categories")

    open class BottomNavItem(var title: String, var icon: ImageVector, screenRoute: String) : Screens(screenRoute) {
        object Home : BottomNavItem("Home", Icons.Default.Home, "home")
        object Spent : BottomNavItem("Spent", Icons.Filled.AttachMoney, "spent")
        object Overview : BottomNavItem("Overview", Icons.Default.Segment, "overview")
        object Setting : BottomNavItem("Setting", Icons.Default.Settings, "setting")
    }
}