package com.example.olenchenkovproject

import android.os.Bundle
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import androidx.core.view.MenuHost
import androidx.core.view.MenuProvider
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Lifecycle
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.olenchenkovproject.databinding.FragmentFavoritesBinding
import com.google.android.material.tabs.TabLayout

class FavoritesFragment : Fragment(R.layout.fragment_favorites) {

    private lateinit var binding: FragmentFavoritesBinding
    private val viewModel: GameViewModel by activityViewModels()
    private lateinit var adapter: GameAdapter

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = FragmentFavoritesBinding.bind(view)

        val menuHost: MenuHost = requireActivity()
        menuHost.addMenuProvider(object : MenuProvider {
            override fun onCreateMenu(menu: Menu, menuInflater: MenuInflater) {
                menuInflater.inflate(R.menu.favorites_menu, menu)
            }

            override fun onMenuItemSelected(menuItem: MenuItem): Boolean {
                return when (menuItem.itemId) {
                    R.id.action_stats -> {
                        val statsSheet = StatsBottomSheet()
                        statsSheet.show(parentFragmentManager, "StatsBottomSheet")
                        true
                    }
                    R.id.action_smart_filter -> {
                        val filterSheet = SmartFilterBottomSheet()
                        filterSheet.show(parentFragmentManager, "SmartFilterSheet")
                        true
                    }
                    else -> false
                }
            }
        }, viewLifecycleOwner, Lifecycle.State.RESUMED)


        adapter = GameAdapter(emptyList()) { selectedGame ->
            val bundle = Bundle().apply { putSerializable("game_key", selectedGame) }
            findNavController().navigate(R.id.action_favoritesFragment_to_detailFragment, bundle)
        }
        binding.rvFavorites.adapter = adapter
        binding.rvFavorites.layoutManager = LinearLayoutManager(requireContext())

        fun updateList() {
            val allEntities = viewModel.favoriteGamesIds.value ?: emptyList()

            adapter.updateReviewsData(allEntities)

            val currentMode = viewModel.currentTabMode
            adapter.isReviewMode = (currentMode == "REVIEWS")

            var filteredEntities = when (currentMode) {
                "ALL" -> allEntities
                "FAVORITE" -> allEntities.filter { it.isFavorite }
                "COLLECTION" -> allEntities.filter { it.status == "COLLECTION" }
                "WISHLIST" -> allEntities.filter { it.status == "WISHLIST" }
                "REVIEWS" -> allEntities.filter { it.userRating > 0 || it.userComment.isNotEmpty() || it.playCount > 0 }
                else -> allEntities
            }

            val ids = filteredEntities.map { it.gameId }
            var gamesToShow = viewModel.games.filter { it.id in ids }

            val smartFilter = viewModel.smartFilter.value
            if (smartFilter != null) {
                if (smartFilter.playerCount > 0) {
                    val p = smartFilter.playerCount
                    gamesToShow = gamesToShow.filter { p >= it.minPlayers && p <= it.maxPlayers }
                }
                if (smartFilter.maxTime > 0) {
                    val t = smartFilter.maxTime
                    gamesToShow = gamesToShow.filter { it.minTime <= t }
                }
            }

            if (gamesToShow.isEmpty()) {
                binding.tvEmptyState.visibility = View.VISIBLE
                binding.rvFavorites.visibility = View.GONE

                if (smartFilter != null && (smartFilter.playerCount > 0 || smartFilter.maxTime > 0)) {
                    binding.tvEmptyState.text = "Нічого не знайдено для цих умов 🧐\nСпробуйте змінити фільтр."
                } else {
                    binding.tvEmptyState.text = "Список порожній :("
                }
            } else {
                binding.tvEmptyState.visibility = View.GONE
                binding.rvFavorites.visibility = View.VISIBLE
                adapter.updateData(gamesToShow)
            }
        }

        viewModel.favoriteGamesIds.observe(viewLifecycleOwner) { updateList() }
        viewModel.smartFilter.observe(viewLifecycleOwner) { updateList() }

        binding.tabLayout.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
            override fun onTabSelected(tab: TabLayout.Tab?) {
                when (tab?.position) {
                    0 -> viewModel.currentTabMode = "ALL"
                    1 -> viewModel.currentTabMode = "FAVORITE"
                    2 -> viewModel.currentTabMode = "COLLECTION"
                    3 -> viewModel.currentTabMode = "WISHLIST"
                    4 -> viewModel.currentTabMode = "REVIEWS"
                }
                updateList()
            }
            override fun onTabUnselected(tab: TabLayout.Tab?) {}
            override fun onTabReselected(tab: TabLayout.Tab?) {}
        })

    }
}