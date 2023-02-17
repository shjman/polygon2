package com.shjman.polygon2.ui.categories

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.Button
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.Card
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.shjman.polygon2.data.Category
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import org.koin.androidx.compose.koinViewModel

@Composable
fun CategoriesScreen(
    categoriesState: MutableState<List<Category>?> = remember { mutableStateOf(null) },
    navigateToEditCategory: () -> Unit,
) {
    val viewModel = koinViewModel< CategoriesViewModel>()
    val scope: CoroutineScope = rememberCoroutineScope()
    val onAddNewCategoryClicked: () -> Unit = { viewModel.addNewCategoryClicked() }

    LaunchedEffect(Unit) {
        viewModel.loadCategories()
        viewModel.categories
            .onEach { categoriesState.value = it }
            .launchIn(scope)
        viewModel.onAddNewCategoryClicked
            .onEach { navigateToEditCategory() }
            .launchIn(scope)
    }
    Column(
        modifier = Modifier.fillMaxSize()
    ) {
        LazyColumn(
            modifier = Modifier.weight(1f),
        ) {
            val categories = categoriesState.value
            when {
                categories == null -> {
                    item {
                        Text(
                            modifier = Modifier.fillMaxWidth(),
                            textAlign = TextAlign.Center,
                            text = "is loading",
                        )
                    }
                }
                categories.isEmpty() -> {
                    item {
                        Text(
                            modifier = Modifier.fillMaxWidth(),
                            textAlign = TextAlign.Center,
                            text = "category list is empty",
                        )
                    }
                }
                else -> {
                    categories.onEach {
                        item(key = it.id) {
                            CategoryView(
                                category = it,
                            )
                        }
                    }
                }
            }
        }
        AddNewCategoryButton(
            addNewCategoryClicked = onAddNewCategoryClicked,
        )
    }
}

@Composable
fun AddNewCategoryButton(
    addNewCategoryClicked: () -> Unit,
) {
    Button(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp),
        onClick = addNewCategoryClicked,
        colors = ButtonDefaults.buttonColors(backgroundColor = Color.Black),
    ) {
        Text(
            text = "add new category",
            color = Color.White,
        )
    }
}

@Composable
fun CategoryView(
    category: Category,
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(4.dp),
        elevation = 4.dp,
    ) {
        Row()
        {
            Text(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(12.dp),
                text = category.name,
            )
        }
    }
}
