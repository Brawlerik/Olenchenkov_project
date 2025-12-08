package com.example.olenchenkovproject

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.olenchenkovproject.databinding.ItemTurnPlayerBinding
import java.util.Collections

class TurnAdapter(
    private val onStartDrag: (RecyclerView.ViewHolder) -> Unit
) : RecyclerView.Adapter<TurnAdapter.TurnViewHolder>() {

    private val players = mutableListOf<String>()

    inner class TurnViewHolder(val binding: ItemTurnPlayerBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TurnViewHolder {
        val binding = ItemTurnPlayerBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return TurnViewHolder(binding)
    }

    @SuppressLint("ClickableViewAccessibility")
    override fun onBindViewHolder(holder: TurnViewHolder, position: Int) {
        val name = players[position]
        holder.binding.tvTurnName.text = name

        holder.binding.btnDeletePlayer.setOnClickListener {
            removeItem(holder.bindingAdapterPosition)
        }

        holder.binding.ivDragHandle.setOnTouchListener { _, event ->
            if (event.action == MotionEvent.ACTION_DOWN) {
                onStartDrag(holder)
            }
            false
        }
    }

    override fun getItemCount(): Int = players.size

    fun addPlayer(name: String) {
        players.add(name)
        notifyItemInserted(players.size - 1)
    }

    fun moveItem(fromPosition: Int, toPosition: Int) {
        if (fromPosition < toPosition) {
            for (i in fromPosition until toPosition) {
                Collections.swap(players, i, i + 1)
            }
        } else {
            for (i in fromPosition downTo toPosition + 1) {
                Collections.swap(players, i, i - 1)
            }
        }
        notifyItemMoved(fromPosition, toPosition)
    }

    private fun removeItem(position: Int) {
        if (position in players.indices) {
            players.removeAt(position)
            notifyItemRemoved(position)
        }
    }

    fun nextTurnRight() {
        if (players.size > 1) {
            val first = players.removeAt(0)
            players.add(first)
            notifyItemMoved(0, players.size - 1)
        }
    }

    fun nextTurnLeft() {
        if (players.size > 1) {
            val last = players.removeAt(players.size - 1)
            players.add(0, last)
            notifyItemMoved(players.size - 1, 0)
        }
    }

    fun clear() {
        players.clear()
        notifyDataSetChanged()
    }
}