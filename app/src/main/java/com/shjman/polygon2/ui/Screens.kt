package com.shjman.polygon2.ui

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Segment
import androidx.compose.material.icons.filled.AddTask
import androidx.compose.material.icons.filled.AttachMoney
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Settings
import androidx.compose.ui.graphics.vector.ImageVector

sealed class Screens(var screenRoute: String) {
    data object AddTrustedUserScreen : Screens("AddTrustedUser")
    data object Categories : Screens("Categories")
    data object EditCategory : Screens("EditCategory")
    data object EditSpending : Screens("EditSpending")
    data object SharingSettings : Screens("SharingSettings")
    data object Unauthorized : Screens("Unauthorized")

    sealed class BottomNavItem(val title: String, val icon: ImageVector, screenRoute: String) : Screens(screenRoute) {
        data object Home : BottomNavItem("Home", Icons.Default.Home, "home")
        data object Spent : BottomNavItem("Spent", Icons.Filled.AttachMoney, "spent")
        data object Overview : BottomNavItem("Overview", Icons.AutoMirrored.Filled.Segment, "overview")
        data object Planned : BottomNavItem("Planned", Icons.Filled.AddTask, "planned")
        data object Setting : BottomNavItem("Setting", Icons.Default.Settings, "setting")

        companion object {
            fun values(): List<BottomNavItem> = listOf(
                Home,
                Spent,
                Overview,
                Planned,
                Setting,
            )
        }
    }
}
