package com.ho8278.bookfinder.search

import android.content.res.Configuration.UI_MODE_NIGHT_YES
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.shape.CornerSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.platform.ViewCompositionStrategy
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import coil.compose.AsyncImagePainter
import coil.compose.SubcomposeAsyncImage
import com.ho8278.bookfinder.R
import com.ho8278.bookfinder.common.ItemHolder
import com.ho8278.bookfinder.common.theme.BookFinderTheme
import dagger.hilt.android.AndroidEntryPoint
import com.ho8278.data.model.Image as ImageData

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
        return ComposeView(requireActivity()).apply {
            setViewCompositionStrategy(
                ViewCompositionStrategy.DisposeOnLifecycleDestroyed(
                    viewLifecycleOwner
                )
            )

            setContent {
                val list = viewModel.itemList.collectAsState(initial = emptyList())
                BookFinderTheme {
                    SearchScreen(
                        list.value,
                        { viewModel.onTextChanges(it) },
                        { checked, image ->
                            if (checked) viewModel.addFavorite(image)
                            else viewModel.removeFavorite(image)
                        },
                        { viewModel.loadMore() },
                    )
                }
            }
        }
    }

    @Composable
    fun SearchScreen(
        list: List<ItemHolder>,
        onTextChanges: (String) -> Unit,
        onCheckedChange: (Boolean, ImageData) -> Unit,
        onLoadMore: () -> Unit,
    ) {
        Column {
            SearchField("", onTextChanges)
            SearchedImageList(list, onCheckedChange, onLoadMore)
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
        var text by remember { mutableStateOf(initialText) }

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
                    text = it
                    onTextChanges(text)
                },
                maxLines = 1,
                shape = backgroundShape,
                colors = textFieldColor
            )
        }
    }

    @Composable
    fun SearchedImageList(
        list: List<ItemHolder>,
        onCheckedChange: (Boolean, ImageData) -> Unit,
        onLoadMore: () -> Unit,
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp)
        ) {
            LazyVerticalGrid(
                columns = GridCells.Fixed(2),
                modifier = Modifier
                    .fillMaxWidth()
                    .fillMaxHeight(),
                horizontalArrangement = Arrangement.spacedBy(10.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp),
            ) {
                items(
                    list.size,
                    { list[it].image.thumbnailUrl }
                ) { position ->
                    if (position == list.size - 1) {
                        onLoadMore()
                    }
                    val itemHolder = list[position]
                    SearchedImage(itemHolder, onCheckedChange)
                }
            }
        }
    }

    @Composable
    fun SearchedImage(
        itemHolder: ItemHolder,
        onCheckedChange: (Boolean, ImageData) -> Unit
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .height(200.dp)
                .clip(RoundedCornerShape(16.dp))
                .background(BookFinderTheme.colorScheme.surfaceContainer)
        ) {
            Box(
                modifier = Modifier.weight(1f)
            ) {
                SubcomposeAsyncImage(
                    model = itemHolder.image.thumbnailUrl,
                    contentDescription = stringResource(R.string.cd_searched_image)
                ) {
                    when (val state = painter.state) {
                        is AsyncImagePainter.State.Loading -> {
                            ImageLoading()
                        }

                        is AsyncImagePainter.State.Error -> {
                            state.result.throwable.printStackTrace()
                            ImageFallback()
                        }

                        is AsyncImagePainter.State.Success -> {
                            ImageSuccess(state.painter)
                        }

                        is AsyncImagePainter.State.Empty -> Unit
                    }
                }
            }

            val checkButtonIcon = if (itemHolder.isFavorite) {
                painterResource(id = R.drawable.baseline_favorite_24)
            } else {
                painterResource(id = R.drawable.baseline_favorite_border_24)
            }

            val favoriteDescriptionRes = if (itemHolder.isFavorite) {
                R.string.cd_remove_favorite_icon
            } else {
                R.string.cd_set_favorite_icon
            }

            Icon(
                checkButtonIcon,
                stringResource(favoriteDescriptionRes),
                modifier = Modifier
                    .size(48.dp, 48.dp)
                    .selectable(itemHolder.isFavorite) {
                        onCheckedChange(!itemHolder.isFavorite, itemHolder.image)
                    }
                    .align(Alignment.End)
                    .padding(end = 8.dp, bottom = 8.dp),
                tint = BookFinderTheme.colorScheme.onSurface
            )
        }
    }

    @Composable
    fun ImageSuccess(painter:Painter) {
        Box(
            modifier = Modifier.fillMaxSize()
        ) {
            Image(
                modifier = Modifier.fillMaxSize(),
                painter = painter,
                contentDescription = null,
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
                contentDescription = null,
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
                contentDescription = null,
                tint = BookFinderTheme.colorScheme.onSurface
            )
        }
    }

    @Preview
    @Composable
    fun PreviewImage() {
        BookFinderTheme {
            SearchedImage(
                itemHolder = ItemHolder(ImageData("https://i.stack.imgur.com/aakut.png"), false),
                onCheckedChange = { _, _ -> }
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
            SearchScreen(list = listOf(
                ItemHolder(ImageData("asdfasdf"), false),
                ItemHolder(ImageData("asdfasdf1"), false),
                ItemHolder(ImageData("asdfasdf2"), false),
                ItemHolder(ImageData("asdfasdf3"), false),
                ItemHolder(ImageData("asdfasdf4"), false),
                ItemHolder(ImageData("asdfasdf5"), false),
                ItemHolder(ImageData("asdfasdf6"), false),
                ItemHolder(ImageData("asdfasdf7"), false),
                ItemHolder(ImageData("asdfasdf8"), false),
                ItemHolder(ImageData("asdfasdf9"), false),
                ItemHolder(ImageData("asdfasdf0"), false),
                ItemHolder(ImageData("asdfasdf11"), false),
                ItemHolder(ImageData("asdfasdf12"), false),
                ItemHolder(ImageData("asdfasdf13"), false),
                ItemHolder(ImageData("asdfasdf14"), false),
                ItemHolder(ImageData("asdfasdf15"), false),
                ItemHolder(ImageData("asdfasdf17"), false),
                ItemHolder(ImageData("asdfasdf19"), false),
            ), onTextChanges = {}, onCheckedChange = { _, _ -> }, {})
        }
    }

    companion object {
        const val TAG = "search"

        @JvmStatic
        fun newInstance() = SearchFragment()
    }
}