package com.example.olenchenkovproject

import android.annotation.SuppressLint
import android.graphics.Color
import android.os.Bundle
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.widget.PopupMenu
import androidx.appcompat.widget.SearchView
import androidx.core.view.MenuHost
import androidx.core.view.MenuProvider
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Lifecycle
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.olenchenkovproject.databinding.FragmentGameListBinding

class ListFragment : Fragment(R.layout.fragment_game_list) {

    private lateinit var binding: FragmentGameListBinding
    private val viewModel: GameViewModel by activityViewModels()

    private lateinit var gameAdapter: GameAdapter
    private lateinit var categoryAdapter: CategoryAdapter

    private var currentQuery = ""
    private var currentCategoryType = "ALL"
    private var selectedCategoryName: String? = null
    private var currentSortMode = "Default"

    @SuppressLint("SetTextI18n")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = FragmentGameListBinding.bind(view)

        setupMenu()

        gameAdapter = GameAdapter(viewModel.games) { selectedGame ->
            val bundle = Bundle().apply { putSerializable("game_key", selectedGame) }
            findNavController().navigate(R.id.action_listFragment_to_detailFragment, bundle)
        }

        binding.rvGames.adapter = gameAdapter
        binding.rvGames.layoutManager = LinearLayoutManager(requireContext())

        binding.searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean = false
            override fun onQueryTextChange(newText: String?): Boolean {
                currentQuery = newText ?: ""
                applyFilters()
                return true
            }
        })

        binding.btnTabAll.setOnClickListener {
            viewModel.listStateTab = "ALL"
            viewModel.listStateCategory = null
            restoreUI()
        }
        binding.btnTabGenre.setOnClickListener {
            viewModel.listStateTab = "GENRE"
            viewModel.listStateCategory = null
            restoreUI()
        }
        binding.btnTabTheme.setOnClickListener {
            viewModel.listStateTab = "THEME"
            viewModel.listStateCategory = null
            restoreUI()
        }
        binding.btnTabSeries.setOnClickListener {
            viewModel.listStateTab = "SERIES"
            viewModel.listStateCategory = null
            restoreUI()
        }

        // 3. СОРТУВАННЯ
        binding.tvSortLabel.setOnClickListener { showSortMenu(it) }

        // 4. ФІЛЬТРИ
        binding.btnFilters.setOnClickListener {
            val sheet = FilterBottomSheet(viewModel.listStateTab)
            sheet.show(parentFragmentManager, "FilterSheet")
        }

        viewModel.filters.observe(viewLifecycleOwner) {
            applyFilters()
        }

        viewModel.favoriteGamesIds.observe(viewLifecycleOwner) { entities ->
            applyFilters()
            val ratingsMap = entities
                .filter { it.userRating > 0 }
                .associate { it.gameId to it.userRating }

        }

        restoreUI()
    }

    private fun restoreUI() {
        currentCategoryType = viewModel.listStateTab
        selectedCategoryName = viewModel.listStateCategory

        updateTabButtons(currentCategoryType)

        if (currentCategoryType == "ALL") {
            showGamesList()
            binding.tvCurrentCategory.visibility = View.GONE
        } else {
            if (selectedCategoryName != null) {
                showGamesList()
                binding.tvCurrentCategory.visibility = View.VISIBLE
                binding.tvCurrentCategory.text = "${getCategoryPrefix(currentCategoryType)}: $selectedCategoryName"
            } else {
                showCategoriesGrid(currentCategoryType)
            }
        }
        applyFilters()
    }

    private fun showCategoriesGrid(type: String) {
        binding.rvGames.visibility = View.GONE
        binding.rvCategories.visibility = View.VISIBLE
        binding.llControls.visibility = View.GONE
        binding.tvCurrentCategory.visibility = View.GONE

        val list = when (type) {
            "GENRE" -> viewModel.allGenres
            "THEME" -> viewModel.allThemes
            "SERIES" -> viewModel.allSeries
            else -> emptyList()
        }

        categoryAdapter = CategoryAdapter(list) { selectedName ->
            viewModel.listStateCategory = selectedName
            restoreUI()
        }
        binding.rvCategories.adapter = categoryAdapter
        binding.rvCategories.layoutManager = GridLayoutManager(requireContext(), 2)
    }

    private fun showGamesList() {
        binding.rvGames.visibility = View.VISIBLE
        binding.rvCategories.visibility = View.GONE
        binding.llControls.visibility = View.VISIBLE
    }

    private fun getCategoryPrefix(tab: String): String = when(tab) {
        "GENRE" -> "Жанр"
        "THEME" -> "Тематика"
        "SERIES" -> "Серія"
        else -> ""
    }

    private fun updateTabButtons(activeTab: String) {
        val defaultColor = Color.WHITE
        val activeColor = Color.parseColor("#E0E0E0") // Світло-сірий для активної
        val defaultText = Color.BLACK

        fun tintBtn(btn: com.google.android.material.button.MaterialButton, isActive: Boolean) {
            btn.setBackgroundColor(if (isActive) activeColor else defaultColor)
            btn.setTextColor(defaultText)
        }



        binding.btnTabAll.setBackgroundColor(if (activeTab == "ALL") activeColor else defaultColor)
        binding.btnTabGenre.setBackgroundColor(if (activeTab == "GENRE") activeColor else defaultColor)
        binding.btnTabTheme.setBackgroundColor(if (activeTab == "THEME") activeColor else defaultColor)
        binding.btnTabSeries.setBackgroundColor(if (activeTab == "SERIES") activeColor else defaultColor)

        binding.btnTabAll.setTextColor(defaultText)
        binding.btnTabGenre.setTextColor(defaultText)
        binding.btnTabTheme.setTextColor(defaultText)
        binding.btnTabSeries.setTextColor(defaultText)
    }

    private fun applyFilters() {
        var list = viewModel.games

        if (selectedCategoryName != null) {
            list = list.filter { game ->
                when (currentCategoryType) {
                    "GENRE" -> game.genres.contains(selectedCategoryName)
                    "THEME" -> game.themes.contains(selectedCategoryName)
                    "SERIES" -> game.series == selectedCategoryName
                    else -> true
                }
            }
        }

        if (currentQuery.isNotEmpty()) {
            list = list.filter { it.title.contains(currentQuery, true) }
        }

        val f = viewModel.filters.value ?: FilterState()

        if (f.language != "Всі") list = list.filter { if(f.language=="Мовонезалежна") it.textDependency=="Немає" else it.language == f.language }
        if (f.playerCount != "Всі") {
            val p = f.playerCount.replace("+","").toIntOrNull() ?: 0
            list = list.filter { p >= it.minPlayers && (f.playerCount.contains("+") || p <= it.maxPlayers) }
        }
        if (f.complexity != "Всі") {
            list = list.filter {
                when (f.complexity) {
                    "Легкі (1-2)" -> it.complexity <= 2.0
                    "Середні (2-3.5)" -> it.complexity > 2.0 && it.complexity <= 3.5
                    "Складні (3.5+)" -> it.complexity > 3.5
                    else -> true
                }
            }
        }
        if (f.publisher != "Всі") list = list.filter { it.publisher == f.publisher }
        if (f.mechanic != "Всі") list = list.filter { it.mechanics.contains(f.mechanic) }

        if (currentCategoryType != "GENRE" && f.genre != "Всі") list = list.filter { it.genres.contains(f.genre) }
        if (currentCategoryType != "THEME" && f.theme != "Всі") list = list.filter { it.themes.contains(f.theme) }

        list = when (currentSortMode) {
            "NameAsc" -> list.sortedBy { it.title }
            "NameDesc" -> list.sortedByDescending { it.title }
            "PriceAsc" -> list.sortedBy { it.price }
            "PriceDesc" -> list.sortedByDescending { it.price }
            "YearDesc" -> list.sortedByDescending { it.year }
            "RatingDesc" -> list.sortedByDescending { it.rating } // Найкращі
            "RatingAsc" -> list.sortedBy { it.rating } // Найгірші
            else -> list
        }

        gameAdapter.updateData(list)
    }

    private fun showSortMenu(view: View) {
        val popup = PopupMenu(requireContext(), view)
        popup.menu.add(0, 0, 0, "Замовчуванням")
        popup.menu.add(0, 1, 1, "За назвою (А-Я)")
        popup.menu.add(0, 6, 2, "За назвою (Я-А)")
        popup.menu.add(0, 2, 3, "Ціна: дешеві")
        popup.menu.add(0, 3, 4, "Ціна: дорогі")
        popup.menu.add(0, 4, 5, "Новинки")
        // НОВІ ПУНКТИ
        popup.menu.add(0, 5, 6, "Рейтинг: Найкращі")
        popup.menu.add(0, 7, 7, "Рейтинг: Найгірші")

        popup.setOnMenuItemClickListener {
            currentSortMode = when(it.itemId) {
                1 -> "NameAsc"
                6 -> "NameDesc"
                2 -> "PriceAsc"
                3 -> "PriceDesc"
                4 -> "YearDesc"
                5 -> "RatingDesc"
                7 -> "RatingAsc"
                else -> "Default"
            }
            binding.tvSortLabel.text = "Сортування: ${it.title} 🔽"
            applyFilters()
            true
        }
        popup.show()
    }

    private fun setupMenu() {
        val menuHost: MenuHost = requireActivity()
        menuHost.addMenuProvider(object : MenuProvider {
            override fun onCreateMenu(menu: Menu, menuInflater: MenuInflater) {
                menuInflater.inflate(R.menu.main_menu, menu)
            }
            override fun onMenuItemSelected(menuItem: MenuItem): Boolean {
                return when (menuItem.itemId) {
                    R.id.action_tools -> {
                        findNavController().navigate(R.id.action_listFragment_to_toolsFragment)
                        true
                    }
                    R.id.action_favorites -> {
                        findNavController().navigate(R.id.action_listFragment_to_favoritesFragment)
                        true
                    }
                    else -> false
                }
            }
        }, viewLifecycleOwner, Lifecycle.State.RESUMED)
    }
}