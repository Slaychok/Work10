package com.example.work10.presentation.compose

import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.example.work10.R
import com.example.work10compose.presentation.viewmodel.CatViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    private val catViewModel: CatViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            CatScreen(catViewModel = catViewModel)
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CatScreen(catViewModel: CatViewModel) {
    val context = LocalContext.current
    val catImageUrl by catViewModel.catImageUrl.observeAsState()
    val saveResult by catViewModel.saveResult.observeAsState()

    // Сайд-эффект для скачивания и сохранения изображения при появлении нового URL
    LaunchedEffect(catImageUrl) {
        catImageUrl?.let { url ->
            catViewModel.downloadAndSaveImage(url)
        }
    }

    // Сайд-эффект для отображения сообщений Toast при изменении результата сохранения
    LaunchedEffect(saveResult) {
        saveResult?.let { success ->
            val message = if (success) {
                "Изображение успешно сохранено!"
            } else {
                "Ошибка при сохранении изображения"
            }
            Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
        }
    }

    // UI
    Scaffold(
        content = { paddingValues ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                if (catImageUrl != null) {
                    // Загрузка изображения с помощью Coil
                    AsyncImage(
                        model = ImageRequest.Builder(context)
                            .data(catImageUrl)
                            .placeholder(R.drawable.gear_spinner)
                            .error(R.drawable.error)
                            .build(),
                        contentDescription = "Cat Image",
                        modifier = Modifier
                            .fillMaxWidth()
                            .aspectRatio(1f)
                            .padding(bottom = 16.dp)
                    )
                }

                Button(onClick = { catViewModel.fetchCat() }) {
                    Text(text = "fetch cat")
                }
            }
        }
    )
}
