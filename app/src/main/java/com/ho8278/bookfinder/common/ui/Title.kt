package com.ho8278.bookfinder.common.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.ho8278.bookfinder.common.theme.BookFinderTheme

@Composable
fun Title(title: String) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(56.dp)
            .background(BookFinderTheme.colorScheme.background)
    ) {
        Text(
            text = title,
            modifier = Modifier
                .align(Alignment.CenterStart)
                .padding(start = 16.dp),
            color = BookFinderTheme.colorScheme.onSurface,
            fontWeight = FontWeight.Bold
        )
    }
}