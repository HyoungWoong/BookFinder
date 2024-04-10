package com.ho8278.bookfinder.main

import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.commit
import androidx.lifecycle.flowWithLifecycle
import androidx.lifecycle.lifecycleScope
import com.ho8278.bookfinder.R
import com.ho8278.bookfinder.databinding.ActivityMainBinding
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    private val viewModel by viewModels<MainViewModel>()

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        lifecycleScope.launch {
            val position = viewModel.tabPosition.first()
            val selectedId = if (position == 0) R.id.menu_search else R.id.menu_favorite

            binding.bottomNav.selectedItemId = selectedId
        }

        lifecycleScope.launch {
            viewModel.tabPosition
                .flowWithLifecycle(lifecycle)
                .collect { position ->
                    val tag = if (position == 0) SearchFragment.TAG else FavoriteFragment.TAG
                    val fragment = supportFragmentManager.findFragmentByTag(tag)

                    supportFragmentManager.commit {
                        supportFragmentManager.fragments.forEach {
                            if (it.isVisible) hide(it)
                        }
                        if (fragment != null) {
                            show(fragment)
                        } else {
                            val fragment = if (position == 0) {
                                SearchFragment.newInstance()
                            } else {
                                FavoriteFragment.newInstance()
                            }
                            add(R.id.fragment, fragment, tag)
                        }
                    }
                }
        }

        binding.bottomNav.setOnItemSelectedListener {
            when (it.itemId) {
                R.id.menu_search -> {
                    viewModel.onTabSelected(0)
                }

                R.id.menu_favorite -> {
                    viewModel.onTabSelected(1)
                }
            }
            true
        }
    }
}