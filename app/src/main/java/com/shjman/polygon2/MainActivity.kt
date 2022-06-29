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
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.compose.viewModel
import com.shjman.polygon2.ui.theme.Polygon2Theme
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import timber.log.Timber

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            Polygon2Theme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colors.background
                ) {
                    Greeting("My dear friend")
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .fillMaxHeight(),
                        verticalArrangement = Arrangement.Center,
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        InputAmountSpendScreen()
                    }
                }
            }
        }
    }
}

class SpentViewModel : ViewModel() {
    private val _amountSpent: MutableLiveData<Int> = MutableLiveData<Int>(0)
    val amountSpent: LiveData<Int> = _amountSpent

    fun onAmountSpentChanged(amountSpent: Int) {
        _amountSpent.value = amountSpent
    }

    fun onSaveButtonClicked() {
        viewModelScope.launch {
            delay(3000)
            Timber.d("save this amount  == ${amountSpent.value}")
            _amountSpent.value = 0
        }
    }
}

@Composable
fun InputAmountSpendScreen(spentViewModel: SpentViewModel = viewModel()) {
    val amountSpent: Int by spentViewModel.amountSpent.observeAsState(0)
    TextField(amountSpent = amountSpent, amountSpentChanged = { spentViewModel.onAmountSpentChanged(it.toIntOrNull() ?: 0) })
    SaveButton { spentViewModel.onSaveButtonClicked() }
}


@Composable
fun TextField(amountSpent: Int, amountSpentChanged: (String) -> Unit) {
    TextField(
        value = if (amountSpent == 0) "" else amountSpent.toString(),
        singleLine = true,
        onValueChange = amountSpentChanged,
        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
        trailingIcon = { Icon(Icons.Outlined.Face, contentDescription = "trailing icon") },
        placeholder = { Text("enter the amount spent") }
    )
}

@Composable
fun SaveButton(onSaveAmountClicked: () -> Unit) {
    Button(
        onClick = onSaveAmountClicked,
        colors = ButtonDefaults.buttonColors(backgroundColor = Color.DarkGray)
    ) {
        Text(text = "save this amount", color = Color.White)
    }
}

@Preview(showBackground = true)
@Composable
fun DefaultPreview() {
    Polygon2Theme {
        Greeting("Android4")
    }
}

@Composable
fun Greeting(name: String) {
    MaterialTheme {
        Text(text = "Hello $name!")
    }
}
