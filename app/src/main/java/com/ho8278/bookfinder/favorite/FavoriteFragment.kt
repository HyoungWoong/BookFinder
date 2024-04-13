package com.ho8278.bookfinder.favorite

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Checkbox
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.platform.ViewCompositionStrategy
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
import com.ho8278.bookfinder.common.theme.BookFinderTheme
import com.ho8278.bookfinder.common.ui.ImageFallback
import com.ho8278.bookfinder.common.ui.ImageLoading
import com.ho8278.bookfinder.common.ui.ImageSuccess
import com.ho8278.bookfinder.common.ui.Title
import com.ho8278.data.model.Image
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class FavoriteFragment : Fragment() {

    private val viewModel by viewModels<FavoriteViewModel>()

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
                BookFinderTheme {
                    val uiState = viewModel.uiState.collectAsStateWithLifecycle(
                        initialValue = FavoriteUiState(emptyList(), false),
                    )

                    FavoriteScreen(
                        list = uiState.value.list,
                        isInEditMode = uiState.value.isInEditMode,
                        onEditModeSelect = {
                            if (uiState.value.isInEditMode) {
                                viewModel.setEditMode(false)
                            } else {
                                viewModel.setEditMode(true)
                            }
                        },
                        onSelectChanged = { image, selected ->
                            if (selected) viewModel.selectImage(image)
                            else viewModel.unSelectImage(image)
                        },
                        onEditConfirm = viewModel::confirm
                    )
                }
            }
        }
    }

    override fun onHiddenChanged(hidden: Boolean) {
        super.onHiddenChanged(hidden)
        if (hidden) viewModel.setEditMode(false)
    }

    @Composable
    fun FavoriteScreen(
        list: List<FavoriteItemHolder>,
        isInEditMode: Boolean,
        onEditModeSelect: () -> Unit,
        onSelectChanged: (Image, Boolean) -> Unit,
        onEditConfirm: () -> Unit,
    ) {
        Column {
            Title(stringResource(id = R.string.fragment_favorite)) {
                Box(
                    modifier = Modifier.fillMaxHeight()
                ) {
                    val menuTet = if (isInEditMode) {
                        stringResource(id = R.string.favorite_remove_cancel)
                    } else {
                        stringResource(id = R.string.favorite_remove_on)
                    }
                    Text(
                        text = menuTet,
                        modifier = Modifier
                            .align(Alignment.Center)
                            .clickable { onEditModeSelect() }
                            .padding(end = 16.dp),
                        fontSize = TextUnit(12f, TextUnitType.Sp)
                    )
                }
            }

            FavoriteList(
                list = list,
                isEditMode = isInEditMode,
                onSelectChanged,
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
            )

            if (isInEditMode) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(BookFinderTheme.colorScheme.tertiary)
                        .padding(vertical = 16.dp)
                ) {
                    Text(
                        text = stringResource(id = R.string.confirm),
                        color = BookFinderTheme.colorScheme.onTertiary,
                        modifier = Modifier
                            .align(Alignment.Center)
                            .clickable { onEditConfirm() }
                    )
                }
            }
        }
    }

    @Composable
    fun FavoriteList(
        list: List<FavoriteItemHolder>,
        isEditMode: Boolean,
        onSelectChanged: (Image, Boolean) -> Unit,
        modifier: Modifier = Modifier,
    ) {
        LazyVerticalGrid(
            columns = GridCells.Fixed(2),
            contentPadding = PaddingValues(bottom = 8.dp, top = 8.dp, start = 16.dp, end = 16.dp),
            modifier = modifier,
            horizontalArrangement = Arrangement.spacedBy(10.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp),
        ) {
            items(
                list.size,
                { list[it].image.thumbnailUrl }
            ) { position ->
                FavoriteImage(
                    itemHolder = list[position],
                    isEditMode = isEditMode,
                    onSelectChanged
                )
            }
        }
    }

    @Composable
    fun FavoriteImage(
        itemHolder: FavoriteItemHolder,
        isEditMode: Boolean,
        onSelectChanged: (Image, Boolean) -> Unit,
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

                if (isEditMode) {
                    Checkbox(
                        checked = itemHolder.selected,
                        modifier = Modifier.align(Alignment.BottomEnd),
                        onCheckedChange = {
                            onSelectChanged(itemHolder.image, it)
                        }
                    )
                }
            }
        }
    }

    @Preview
    @Composable
    fun PreviewScreen() {
        BookFinderTheme {
            FavoriteScreen(
                listOf(
                    FavoriteItemHolder(Image("asdfasdf"), false),
                    FavoriteItemHolder(Image("asdfasdf1"), false),
                    FavoriteItemHolder(Image("asdfasdf2"), false),
                    FavoriteItemHolder(Image("asdfasdf3"), false),
                    FavoriteItemHolder(Image("asdfasdf4"), false),
                    FavoriteItemHolder(Image("asdfasdf5"), false),
                    FavoriteItemHolder(Image("asdfasdf6"), false),
                ),
                true,
                {},
                { _, _ -> },
                {},
            )
        }
    }

    companion object {
        const val TAG = "favorite"

        @JvmStatic
        fun newInstance() = FavoriteFragment()
    }
}