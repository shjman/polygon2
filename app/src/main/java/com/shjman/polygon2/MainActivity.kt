package com.shjman.polygon2

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Face
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import com.shjman.polygon2.ui.theme.Polygon2Theme
import timber.log.Timber

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            val label = remember { mutableStateOf("Button") }
            val textField = remember { mutableStateOf("") }
            Polygon2Theme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colors.background
                ) {
                    Greeting("Android5")
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .fillMaxHeight(),
                        verticalArrangement = Arrangement.Center,
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        myTextField(textField)
                        buttonWithColor(label, textField)
                    }
                }
            }
        }
    }
}

@Composable
fun Greeting(name: String) {
    MaterialTheme {
        Text(text = "Hello $name!")
    }
}

@Composable
fun myTextField(textField: MutableState<String>) {
    TextField(
        value = textField.value,
        singleLine = true,
        onValueChange = { newText -> textField.value = newText },
        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
        trailingIcon = { Icon(Icons.Outlined.Face, contentDescription = "Дополнительная информация") },
        placeholder = { Text("Hello Work!") }
    )
}

@Composable
fun buttonWithColor(label: MutableState<String>, textField: MutableState<String>) {
    val name = "Button"
    val labelInt = remember { mutableStateOf(0) }
    Button(
        onClick = {
            Timber.d("button clicked textField.value = ${textField.value} ")
            //your onclick code
            labelInt.value = ++labelInt.value
            label.value = name + labelInt.value
        },
        colors = ButtonDefaults.buttonColors(backgroundColor = Color.DarkGray)
    )
    {
        Text(text = label.value, color = Color.White)
    }
}

@Preview(showBackground = true)
@Composable
fun DefaultPreview() {
    Polygon2Theme {
        Greeting("Android4")
    }
}