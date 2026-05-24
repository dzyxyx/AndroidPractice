package com.example.androidpractice.ui.filter

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.MenuAnchorType
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel

private val GENRES = listOf(
    "" to "Все жанры",
    "аниме" to "Аниме",
    "биография" to "Биография",
    "боевик" to "Боевик",
    "вестерн" to "Вестерн",
    "документальный" to "Документальный",
    "драма" to "Драма",
    "история" to "История",
    "комедия" to "Комедия",
    "криминал" to "Криминал",
    "мелодрама" to "Мелодрама",
    "мультфильм" to "Мультфильм",
    "музыка" to "Музыка",
    "приключения" to "Приключения",
    "семейный" to "Семейный",
    "спорт" to "Спорт",
    "триллер" to "Триллер",
    "ужасы" to "Ужасы",
    "фантастика" to "Фантастика",
    "фэнтези" to "Фэнтези",
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FilterScreen(
    onApply: () -> Unit,
    viewModel: FilterViewModel = viewModel()
) {
    val settings by viewModel.settings.collectAsState()
    var genreExpanded by remember { mutableStateOf(false) }

    val selectedGenreLabel = GENRES.find { it.first == settings.genre }?.second ?: "Все жанры"

    Column(
        modifier = Modifier
            .fillMaxSize()
            .statusBarsPadding()
            .padding(16.dp)
    ) {
        Text(
            text = "Фильтры",
            style = MaterialTheme.typography.headlineSmall
        )

        Spacer(modifier = Modifier.height(24.dp))

        // Жанр — выпадающий список
        ExposedDropdownMenuBox(
            expanded = genreExpanded,
            onExpandedChange = { genreExpanded = it },
            modifier = Modifier.fillMaxWidth()
        ) {
            OutlinedTextField(
                value = selectedGenreLabel,
                onValueChange = {},
                readOnly = true,
                label = { Text("Жанр") },
                trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = genreExpanded) },
                modifier = Modifier
                    .fillMaxWidth()
                    .menuAnchor(MenuAnchorType.PrimaryNotEditable)
            )
            ExposedDropdownMenu(
                expanded = genreExpanded,
                onDismissRequest = { genreExpanded = false }
            ) {
                GENRES.forEach { (value, label) ->
                    DropdownMenuItem(
                        text = { Text(label) },
                        onClick = {
                            viewModel.setGenre(value)
                            genreExpanded = false
                        }
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Минимальный рейтинг
        OutlinedTextField(
            value = if (settings.minRating == 0.0) "" else settings.minRating.toString(),
            onValueChange = { value ->
                viewModel.setMinRating(value.toDoubleOrNull() ?: 0.0)
            },
            label = { Text("Минимальный рейтинг (0–10)") },
            singleLine = true,
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Минимальный год
        OutlinedTextField(
            value = if (settings.minYear == 0) "" else settings.minYear.toString(),
            onValueChange = { value ->
                viewModel.setMinYear(value.toIntOrNull() ?: 0)
            },
            label = { Text("Год выпуска от") },
            singleLine = true,
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(32.dp))

        Button(
            onClick = { viewModel.saveAndApply(onApply) },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Применить")
        }

        Spacer(modifier = Modifier.height(8.dp))

        OutlinedButton(
            onClick = { viewModel.reset(onApply) },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Сбросить фильтры")
        }
    }
}
