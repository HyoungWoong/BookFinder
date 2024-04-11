package com.ho8278.bookfinder.search

import android.content.res.Configuration.UI_MODE_NIGHT_YES
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.CornerSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.platform.ViewCompositionStrategy
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.ho8278.bookfinder.common.ItemHolder
import com.ho8278.bookfinder.common.theme.BookFinderTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class SearchFragment : Fragment() {

    private val viewModel by viewModels<SearchViewModel>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        viewModel.init()
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return ComposeView(requireContext()).apply {
            setViewCompositionStrategy(
                ViewCompositionStrategy.DisposeOnLifecycleDestroyed(
                    viewLifecycleOwner
                )
            )

            setContent {
                val list = viewModel.itemList.collectAsState(initial = emptyList())
                BookFinderTheme {
                    SearchScreen(list.value)
                }
            }
        }
    }

    @Composable
    fun SearchScreen(list: List<ItemHolder>) {

        Row {
            SearchField("") {}
        }
    }

    @Composable
    fun SearchField(initialText: String, onTextChanges: (String) -> Unit) {
        Box(
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
        ) {
            RoundedTextField(
                initialText = initialText,
                onTextChanges = onTextChanges,
                cornerSize = CornerSize(4.dp)
            )
        }
    }

    @Composable
    fun RoundedTextField(
        initialText: String,
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
                initialText,
                modifier = Modifier
                    .fillMaxWidth(),
                onValueChange = onTextChanges,
                maxLines = 1,
                shape = backgroundShape,
                colors = textFieldColor
            )
        }
    }

    @Preview(
        showBackground = true,
        uiMode = UI_MODE_NIGHT_YES
    )
    @Composable
    fun PreviewSearchField() {
        BookFinderTheme {
            SearchScreen(emptyList())
        }

    }

    companion object {
        const val TAG = "search"

        @JvmStatic
        fun newInstance() = SearchFragment()
    }
}