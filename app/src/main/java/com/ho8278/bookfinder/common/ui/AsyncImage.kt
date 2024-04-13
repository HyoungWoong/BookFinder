package com.ho8278.bookfinder.common.ui

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.ho8278.bookfinder.R
import com.ho8278.bookfinder.common.theme.BookFinderTheme

@Composable
fun ImageSuccess(painter: Painter) {
    Box(
        modifier = Modifier.fillMaxSize()
    ) {
        Image(
            modifier = Modifier.fillMaxSize(),
            painter = painter,
            contentDescription = stringResource(R.string.cd_searched_image),
            contentScale = ContentScale.Crop
        )
    }
}

@Composable
fun ImageLoading() {
    Box(
        modifier = Modifier.fillMaxSize()
    ) {
        Icon(
            modifier = Modifier
                .align(Alignment.Center)
                .size(64.dp),
            painter = painterResource(id = R.drawable.baseline_pending_24),
            contentDescription = stringResource(id = R.string.cd_image_loading),
            tint = BookFinderTheme.colorScheme.onSurface
        )
    }
}

@Composable
fun ImageFallback() {
    Box(
        modifier = Modifier.fillMaxSize()
    ) {
        Icon(
            modifier = Modifier
                .align(Alignment.Center)
                .size(64.dp),
            painter = painterResource(id = R.drawable.baseline_question_mark_24),
            contentDescription = stringResource(id = R.string.cd_image_fallback),
            tint = BookFinderTheme.colorScheme.onSurface
        )
    }
}