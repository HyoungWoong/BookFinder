package com.ho8278.bookfinder.search

import android.content.res.Configuration.UI_MODE_NIGHT_YES
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.shape.CornerSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material3.Icon
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.platform.ViewCompositionStrategy
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import coil.compose.AsyncImage
import com.ho8278.bookfinder.R
import com.ho8278.bookfinder.common.ItemHolder
import com.ho8278.bookfinder.common.theme.BookFinderTheme
import com.ho8278.data.model.Image
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
            SearchedImageList(list) {}
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

    @Composable
    fun SearchedImageList(
        list: List<ItemHolder>,
        onCheckedChange: (Boolean) -> Unit,
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .fillMaxHeight()
                .padding(horizontal = 8.dp)
        ) {
            LazyVerticalGrid(
                columns = GridCells.Fixed(2),
                modifier = Modifier
                    .fillMaxWidth()
                    .fillMaxHeight()
            ) {
                items(
                    list,
                    { it.image.thumbnailUrl }
                ) {
                    SearchedImage(it, onCheckedChange)
                }
            }
        }
    }

    @Composable
    fun SearchedImage(
        itemHolder: ItemHolder,
        onCheckedChange: (Boolean) -> Unit
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(108.dp)
                .clip(RoundedCornerShape(16.dp))
                .background(BookFinderTheme.colorScheme.surfaceContainer)
        ) {
            AsyncImage(
                model = itemHolder.image,
                contentDescription = null,
                modifier = Modifier
                    .fillMaxWidth()
                    .fillMaxHeight(),
                placeholder = painterResource(id = R.drawable.baseline_pending_24),
                fallback = painterResource(id = R.drawable.baseline_question_mark_24),
                contentScale = ContentScale.Fit
            )

            val checkButtonIcon = if (itemHolder.isFavorite) {
                Icons.Default.Favorite
            } else {
                Icons.Default.FavoriteBorder
            }
            Icon(
                checkButtonIcon,
                null,
                modifier = Modifier
                    .size(48.dp, 48.dp)
                    .selectable(itemHolder.isFavorite) {
                        onCheckedChange(!itemHolder.isFavorite)
                    }
                    .align(Alignment.BottomEnd)
                    .padding(end = 8.dp, bottom = 8.dp)
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

    @Preview(
        showBackground = true,
        uiMode = UI_MODE_NIGHT_YES
    )
    @Composable
    fun PreviewSearchedImage() {
        BookFinderTheme {
            SearchedImage(
                ItemHolder(
                    Image("https://developer.android.com/static/develop/ui/compose/images/lists-photogrid.png"),
                    false
                ), {}
            )
        }
    }

    companion object {
        const val TAG = "search"

        @JvmStatic
        fun newInstance() = SearchFragment()
    }
}