package com.example.androidpractice.ui.moviedetail

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.constraintlayout.compose.Dimension
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.AsyncImage
import com.example.androidpractice.domain.model.Movie
import com.example.androidpractice.ui.UiState

@Composable
fun MovieDetailScreen(
    onBack: () -> Unit,
    viewModel: MovieDetailViewModel = viewModel()
) {
    val state by viewModel.state.collectAsState()
    val isInFavorites by viewModel.isInFavorites.collectAsState()

    when (val s = state) {
        is UiState.Loading -> {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator()
            }
        }

        is UiState.Error -> {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(text = s.message, color = Color.Red)
                    Spacer(modifier = Modifier.height(8.dp))
                    Button(onClick = { viewModel.retry() }) {
                        Text("Повторить")
                    }
                }
            }
        }

        is UiState.Success -> MovieDetailContent(
            movie = s.data,
            isInFavorites = isInFavorites,
            onBack = onBack,
            onToggleFavorite = { viewModel.toggleFavorite() }
        )
    }
}

@Composable
private fun MovieDetailContent(
    movie: Movie,
    isInFavorites: Boolean,
    onBack: () -> Unit,
    onToggleFavorite: () -> Unit
) {
    ConstraintLayout(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
    ) {
        val (poster, backButton, favoriteButton, title, year, genre, director, ratingLabel, description) = createRefs()

        // Постер
        AsyncImage(
            model = movie.posterUrl,
            contentDescription = "Постер",
            modifier = Modifier
                .fillMaxWidth()
                .height(300.dp)
                .constrainAs(poster) {
                    top.linkTo(parent.top)
                    start.linkTo(parent.start)
                    end.linkTo(parent.end)
                },
            contentScale = ContentScale.Crop
        )

        // Кнопка назад
        IconButton(
            onClick = onBack,
            modifier = Modifier
                .statusBarsPadding()
                .constrainAs(backButton) {
                    top.linkTo(parent.top, margin = 16.dp)
                    start.linkTo(parent.start, margin = 8.dp)
                }
        ) {
            Icon(
                imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                contentDescription = "Назад",
                tint = Color.White
            )
        }

        // Кнопка избранного
        IconButton(
            onClick = onToggleFavorite,
            modifier = Modifier
                .statusBarsPadding()
                .constrainAs(favoriteButton) {
                    top.linkTo(parent.top, margin = 16.dp)
                    end.linkTo(parent.end, margin = 8.dp)
                }
        ) {
            Icon(
                imageVector = if (isInFavorites) Icons.Default.Favorite else Icons.Default.FavoriteBorder,
                contentDescription = if (isInFavorites) "Удалить из избранного" else "Добавить в избранное",
                tint = if (isInFavorites) Color.Red else Color.White
            )
        }

        // Название
        Text(
            text = movie.title,
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Bold,
            modifier = Modifier
                .padding(horizontal = 16.dp)
                .constrainAs(title) {
                    top.linkTo(poster.bottom, margin = 16.dp)
                    start.linkTo(parent.start)
                    end.linkTo(parent.end)
                    width = Dimension.fillToConstraints
                }
        )

        // Год и страны
        Text(
            text = "${movie.year ?: "—"} · ${movie.countries}",
            style = MaterialTheme.typography.bodyMedium,
            color = Color.Gray,
            modifier = Modifier
                .padding(horizontal = 16.dp)
                .constrainAs(year) {
                    top.linkTo(title.bottom, margin = 8.dp)
                    start.linkTo(parent.start)
                }
        )

        // Жанр
        Text(
            text = movie.genres,
            style = MaterialTheme.typography.bodyMedium,
            color = Color.Gray,
            modifier = Modifier
                .padding(horizontal = 16.dp)
                .constrainAs(genre) {
                    top.linkTo(year.bottom, margin = 4.dp)
                    start.linkTo(parent.start)
                }
        )

        // Режиссёр
        Text(
            text = if (movie.director != null) "Режиссёр: ${movie.director}" else "",
            style = MaterialTheme.typography.bodyMedium,
            modifier = Modifier
                .padding(horizontal = 16.dp)
                .constrainAs(director) {
                    top.linkTo(genre.bottom, margin = 4.dp)
                    start.linkTo(parent.start)
                }
        )

        // Рейтинг
        Text(
            text = "⭐ ${movie.rating ?: "—"} / 10",
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold,
            modifier = Modifier
                .padding(horizontal = 16.dp)
                .constrainAs(ratingLabel) {
                    top.linkTo(director.bottom, margin = 12.dp)
                    start.linkTo(parent.start)
                }
        )

        // Описание
        Text(
            text = movie.description ?: "Описание отсутствует",
            style = MaterialTheme.typography.bodyLarge,
            modifier = Modifier
                .padding(horizontal = 16.dp)
                .padding(bottom = 32.dp)
                .constrainAs(description) {
                    top.linkTo(ratingLabel.bottom, margin = 16.dp)
                    start.linkTo(parent.start)
                    end.linkTo(parent.end)
                    width = Dimension.fillToConstraints
                }
        )
    }
}
