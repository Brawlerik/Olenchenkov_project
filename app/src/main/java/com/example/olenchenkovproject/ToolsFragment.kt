package com.example.olenchenkovproject

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.olenchenkovproject.databinding.FragmentToolsBinding
import kotlin.random.Random

class ToolsFragment : Fragment(R.layout.fragment_tools) {

    private lateinit var binding: FragmentToolsBinding

    private lateinit var itemTouchHelper: ItemTouchHelper

    private val turnAdapter = TurnAdapter { viewHolder ->
        itemTouchHelper.startDrag(viewHolder)
    }

    @SuppressLint("SetTextI18n")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = FragmentToolsBinding.bind(view)

        binding.btnRoll.setOnClickListener { rollDice() }

        binding.btnWhoFirst.setOnClickListener {
            val names = getNames(binding.etPlayerNames.text.toString())
            if (names.isNotEmpty()) {
                val winner = names.random()
                binding.tvRandomResult.text = "👑 Першим ходить:\n\n$winner"
            }
        }

        binding.btnMakeTeams.setOnClickListener {
            val names = getNames(binding.etPlayerNames.text.toString())
            val teamsCount = binding.etTeamCount.text.toString().toIntOrNull() ?: 2

            if (names.size < 2) {
                showToast("Треба мінімум 2 гравці!")
                return@setOnClickListener
            }
            if (teamsCount < 2) {
                showToast("Треба мінімум 2 команди!")
                return@setOnClickListener
            }
            if (teamsCount > names.size) {
                showToast("Гравців менше, ніж команд!")
                return@setOnClickListener
            }

            val shuffled = names.shuffled()
            val teams = MutableList(teamsCount) { mutableListOf<String>() }
            for ((index, player) in shuffled.withIndex()) {
                teams[index % teamsCount].add(player)
            }

            val resultBuilder = StringBuilder()
            for ((index, teamMembers) in teams.withIndex()) {
                resultBuilder.append("🟢 Команда ${index + 1}:\n${teamMembers.joinToString(", ")}\n\n")
            }
            binding.tvRandomResult.text = resultBuilder.toString().trim()
        }

        binding.btnCreateScoreTable.setOnClickListener { createScoreTable() }
        binding.btnClearScores.setOnClickListener {
            binding.llScoreContainer.removeAllViews()
            binding.btnClearScores.visibility = View.GONE
            binding.etScoreNames.text?.clear()
        }

        setupTurnTracker()
    }

    private fun setupTurnTracker() {
        binding.rvTurnOrder.layoutManager = LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
        binding.rvTurnOrder.adapter = turnAdapter

        binding.btnAddTurnPlayer.setOnClickListener {
            val name = binding.etTurnName.text.toString()
            if (name.isNotBlank()) {
                turnAdapter.addPlayer(name)
                binding.etTurnName.text?.clear()
            }
        }

        binding.btnTurnLeft.setOnClickListener { turnAdapter.nextTurnLeft() }
        binding.btnTurnRight.setOnClickListener { turnAdapter.nextTurnRight() }
        binding.btnClearTurn.setOnClickListener { turnAdapter.clear() }

        val callback = object : ItemTouchHelper.SimpleCallback(
            ItemTouchHelper.LEFT or ItemTouchHelper.RIGHT,
            0
        ) {
            override fun onMove(
                recyclerView: RecyclerView,
                viewHolder: RecyclerView.ViewHolder,
                target: RecyclerView.ViewHolder
            ): Boolean {
                turnAdapter.moveItem(viewHolder.bindingAdapterPosition, target.bindingAdapterPosition)
                return true
            }

            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {}

            override fun onSelectedChanged(viewHolder: RecyclerView.ViewHolder?, actionState: Int) {
                super.onSelectedChanged(viewHolder, actionState)
                if (actionState == ItemTouchHelper.ACTION_STATE_DRAG) {
                    viewHolder?.itemView?.alpha = 0.5f
                }
            }

            override fun clearView(recyclerView: RecyclerView, viewHolder: RecyclerView.ViewHolder) {
                super.clearView(recyclerView, viewHolder)
                viewHolder.itemView.alpha = 1.0f
            }
        }

        itemTouchHelper = ItemTouchHelper(callback)
        itemTouchHelper.attachToRecyclerView(binding.rvTurnOrder)
    }

    private fun getNames(text: String): List<String> {
        if (text.isBlank()) {
            showToast("Введіть імена!")
            return emptyList()
        }
        return text.split(",", "\n").map { it.trim() }.filter { it.isNotEmpty() }
    }

    private fun showToast(message: String) {
        context?.let { Toast.makeText(it, message, Toast.LENGTH_SHORT).show() }
    }

    @SuppressLint("SetTextI18n")
    private fun createScoreTable() {
        val names = getNames(binding.etScoreNames.text.toString())
        if (names.isEmpty()) return

        binding.llScoreContainer.removeAllViews()

        for (name in names) {
            val playerView = layoutInflater.inflate(R.layout.item_score_player, binding.llScoreContainer, false)
            val tvName = playerView.findViewById<TextView>(R.id.tvPlayerName)
            val etScore = playerView.findViewById<EditText>(R.id.etScore)
            val btnMinus = playerView.findViewById<Button>(R.id.btnMinus)
            val btnPlus = playerView.findViewById<Button>(R.id.btnPlus)

            tvName.text = name
            btnMinus.setOnClickListener {
                val current = etScore.text.toString().toIntOrNull() ?: 0
                etScore.setText((current - 1).toString())
            }
            btnPlus.setOnClickListener {
                val current = etScore.text.toString().toIntOrNull() ?: 0
                etScore.setText((current + 1).toString())
            }
            binding.llScoreContainer.addView(playerView)
        }
        binding.btnClearScores.visibility = View.VISIBLE
    }

    @SuppressLint("SetTextI18n")
    private fun rollDice() {
        val countStr = binding.etDiceCount.text.toString()
        val sidesStr = binding.etDiceSides.text.toString()
        if (countStr.isEmpty() || sidesStr.isEmpty()) {
            showToast("Введіть числа!")
            return
        }
        val count = countStr.toIntOrNull() ?: 1
        val sides = sidesStr.toIntOrNull() ?: 6
        if (count > 50) { showToast("Забагато"); return }

        var sum = 0
        val results = ArrayList<Int>()
        repeat(count) {
            val roll = Random.nextInt(1, sides + 1)
            sum += roll
            results.add(roll)
        }
        binding.tvDiceResult.text = "Сума: $sum"
        binding.tvDiceHistory.text = results.joinToString(" + ")
    }
}