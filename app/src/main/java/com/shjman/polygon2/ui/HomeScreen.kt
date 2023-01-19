package com.shjman.polygon2.ui

import androidx.compose.foundation.layout.*
import androidx.compose.material.Button
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color

@Composable
fun HomeScreen(
    login: () -> Unit,
    logout: () -> Unit,
    onClickGoNext: () -> Unit,
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .wrapContentSize(Alignment.Center)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.Center
        ) {
            Text(
                text = "home screen",
                color = Color.Green,
            )
        }
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .fillMaxHeight(),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Button(
                onClick = login,
                colors = ButtonDefaults.buttonColors(backgroundColor = Color.White)
            ) {
                Text(text = "login", color = Color.Black)
            }
            Button(
                onClick = logout,
                colors = ButtonDefaults.buttonColors(backgroundColor = Color.White)
            ) {
                Text(text = "logout", color = Color.Black)
            }
            Button(
                onClick = onClickGoNext,
                colors = ButtonDefaults.buttonColors(backgroundColor = Color.White)
            ) {
                Text(text = "go spent screen", color = Color.Black)
            }
        }
    }
}
