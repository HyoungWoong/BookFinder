package com.ho8278.bookfinder.search

import android.content.res.Configuration.UI_MODE_NIGHT_YES
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.shape.CornerSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.platform.ViewCompositionStrategy
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.TextUnitType
import androidx.compose.ui.unit.dp
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil.compose.AsyncImagePainter
import coil.compose.SubcomposeAsyncImage
import com.ho8278.bookfinder.R
import com.ho8278.bookfinder.common.ToastSignal
import com.ho8278.bookfinder.common.theme.BookFinderTheme
import com.ho8278.bookfinder.common.ui.ImageFallback
import com.ho8278.bookfinder.common.ui.ImageLoading
import com.ho8278.bookfinder.common.ui.ImageSuccess
import com.ho8278.bookfinder.common.ui.RoundedTextField
import com.ho8278.bookfinder.common.ui.SingleEvent
import com.ho8278.bookfinder.common.ui.Title
import com.ho8278.data.model.Image
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
                val searchUiState = viewModel.uiState
                    .collectAsStateWithLifecycle(
                        initialValue = SearchUiState.Empty("")
                    )

                SingleEvent(
                    eventSource = viewModel.signals,
                    lifecycle
                ) {
                    when (it) {
                        is ToastSignal -> {
                            Toast.makeText(requireActivity(), it.message, Toast.LENGTH_SHORT).show()
                        }
                    }
                }

                BookFinderTheme {
                    SearchScreen(
                        searchUiState.value,
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
        searchUiState: SearchUiState,
        onTextChanges: (String) -> Unit,
        onCheckedChange: (Boolean, ImageData) -> Unit,
        onLoadMore: () -> Unit,
    ) {
        Column {
            Title(stringResource(id = R.string.fragment_search))
            SearchField(searchUiState.searchText, onTextChanges)

            when (searchUiState) {
                is SearchUiState.Undefined -> {
                    Box(modifier = Modifier.fillMaxSize())
                }

                is SearchUiState.Success -> {
                    SearchedImageList(
                        searchUiState.searchedList,
                        searchUiState.isEnd,
                        onCheckedChange,
                        onLoadMore
                    )
                }

                is SearchUiState.Loading -> {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                    ) {
                        CircularProgressIndicator(
                            modifier = Modifier
                                .size(64.dp, 64.dp)
                                .align(Alignment.Center)
                        )
                    }
                }

                is SearchUiState.Empty -> {
                    EmptySearchResult()
                }
            }
        }
    }

    @Composable
    fun EmptySearchResult() {
        Box(
            modifier = Modifier.fillMaxSize()
        ) {
            Text(
                text = stringResource(id = R.string.search_empty),
                fontSize = TextUnit(20f, TextUnitType.Sp),
                color = BookFinderTheme.colorScheme.onSurface,
                modifier = Modifier.align(Alignment.Center)
            )
        }
    }

    @Composable
    fun SearchField(text: String, onTextChanges: (String) -> Unit) {
        Box(
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
        ) {
            RoundedTextField(
                text = text,
                onTextChanges = onTextChanges,
                cornerSize = CornerSize(4.dp)
            )
        }
    }

    @Composable
    fun SearchedImageList(
        list: List<SearchItemHolder>,
        isEnd: Boolean,
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
                contentPadding = PaddingValues(bottom = 8.dp),
                modifier = Modifier
                    .fillMaxSize(),
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

                if (!isEnd) {
                    item(span = { GridItemSpan(2) }) {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(56.dp)
                        ) {
                            CircularProgressIndicator(
                                modifier = Modifier.align(Alignment.Center)
                            )
                        }
                    }
                }
            }
        }
    }

    @Composable
    fun SearchedImage(
        searchItemHolder: SearchItemHolder,
        onCheckedChange: (Boolean, Image) -> Unit
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
                    model = searchItemHolder.image.thumbnailUrl,
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

            val checkButtonIcon = if (searchItemHolder.isFavorite) {
                painterResource(id = R.drawable.baseline_favorite_24)
            } else {
                painterResource(id = R.drawable.baseline_favorite_border_24)
            }

            val favoriteDescriptionRes = if (searchItemHolder.isFavorite) {
                R.string.cd_remove_favorite_icon
            } else {
                R.string.cd_set_favorite_icon
            }

            Icon(
                checkButtonIcon,
                stringResource(favoriteDescriptionRes),
                modifier = Modifier
                    .size(48.dp, 48.dp)
                    .selectable(searchItemHolder.isFavorite) {
                        onCheckedChange(!searchItemHolder.isFavorite, searchItemHolder.image)
                    }
                    .align(Alignment.End)
                    .padding(end = 8.dp, bottom = 8.dp),
                tint = BookFinderTheme.colorScheme.onSurface
            )
        }
    }

    @Preview
    @Composable
    fun PreviewImage() {
        BookFinderTheme {
            SearchedImage(
                searchItemHolder = SearchItemHolder(ImageData("https://i.stack.imgur.com/aakut.png"), false),
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
            SearchScreen(
//                SearchUiState(
//                    "",
//                    isEnd = true,
//                    isLoading = true,
//                    searchedList = listOf(
//                        SearchItemHolder(ImageData("asdfasdf"), false),
//                        SearchItemHolder(ImageData("asdfasdf1"), false),
//                        SearchItemHolder(ImageData("asdfasdf2"), false),
//                        SearchItemHolder(ImageData("asdfasdf3"), false),
//                        SearchItemHolder(ImageData("asdfasdf4"), false),
//                    )
//                ),
                SearchUiState.Empty(""),
                onTextChanges = {}, onCheckedChange = { _, _ -> }, onLoadMore = {})
        }
    }

    @Preview
    @Composable
    fun PreviewAlertDialog() {
        BookFinderTheme {
        }
    }

    companion object {
        const val TAG = "search"

        @JvmStatic
        fun newInstance() = SearchFragment()
    }
}