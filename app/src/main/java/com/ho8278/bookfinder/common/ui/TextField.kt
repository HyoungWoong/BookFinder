package com.ho8278.bookfinder.common.ui

import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.shape.CornerSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.ho8278.bookfinder.common.theme.BookFinderTheme

@Composable
fun RoundedTextField(
    text: String,
    onTextChanges: (String) -> Unit,
    cornerSize: CornerSize
) {
    val backgroundShape = RoundedCornerShape(cornerSize)
    val textFieldColor = TextFieldDefaults.colors(
        disabledContainerColor = BookFinderTheme.colorScheme.background,
        errorContainerColor = BookFinderTheme.colorScheme.background,
        focusedContainerColor = BookFinderTheme.colorScheme.background,
        unfocusedContainerColor = BookFinderTheme.colorScheme.background,
        disabledIndicatorColor = Color.Transparent,
        errorIndicatorColor = Color.Transparent,
        focusedIndicatorColor = Color.Transparent,
        unfocusedIndicatorColor = Color.Transparent,
    )
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .border(
                1.dp,
                BookFinderTheme.colorScheme.onBackground,
                backgroundShape
            )
            .clip(backgroundShape)
    ) {
        TextField(
            text,
            modifier = Modifier
                .fillMaxWidth(),
            onValueChange = {
                onTextChanges(it)
            },
            maxLines = 1,
            shape = backgroundShape,
            colors = textFieldColor
        )
    }
}