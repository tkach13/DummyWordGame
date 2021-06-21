package com.adjarabet.user.presentation.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.adjarabet.user.R
import com.adjarabet.user.common.base.BaseViewHolder
import com.adjarabet.user.databinding.LayoutGameHistoryBotItemBinding
import com.adjarabet.user.databinding.LayoutGameHistoryUserItemBinding
import com.adjarabet.user.domain.models.GameHistoryItemDataModel
import com.adjarabet.user.domain.models.PlayerType

class GameHistoryAdapter() : RecyclerView.Adapter<BaseViewHolder>() {

    private val currentList = mutableListOf<GameHistoryItemDataModel>()


     fun diffUtilsUpdate(newList: List<GameHistoryItemDataModel>, detectMoves: Boolean) {
        val diffResult = DiffUtil.calculateDiff(
                object : DiffUtil.Callback() {
                    override fun getOldListSize(): Int {
                        return currentList.size
                    }

                    override fun getNewListSize(): Int {
                        return newList.size
                    }

                    override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
                        return currentList[oldItemPosition].playerType == newList[newItemPosition].playerType
                    }

                    override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
                        return currentList[oldItemPosition].currentWord == newList[newItemPosition].currentWord
                    }

                },
                detectMoves
        )
        diffResult.dispatchUpdatesTo(this)
        currentList.apply {
            clear()
            addAll(newList)
        }
    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BaseViewHolder {
        return when (PlayerType.values()[viewType]) {
            PlayerType.BOT -> GameHistoryBotViewHolder(LayoutGameHistoryBotItemBinding
                    .inflate(LayoutInflater.from(parent.context), parent, false))
            PlayerType.USER -> GameHistoryUserViewHolder(LayoutGameHistoryUserItemBinding
                    .inflate(LayoutInflater.from(parent.context), parent, false))
        }
    }

    override fun onBindViewHolder(holder: BaseViewHolder, position: Int) {
        holder.bind(currentList[position])
    }

    override fun getItemCount(): Int {
       return currentList.size
    }

    override fun getItemViewType(position: Int): Int {
        return currentList[position].playerType.ordinal
    }

    inner class GameHistoryBotViewHolder(private val viewBinding: LayoutGameHistoryBotItemBinding)
        : BaseViewHolder(viewBinding) {
        override fun bind(itemDataModel: GameHistoryItemDataModel) {
            viewBinding.tvInput.text = itemDataModel.currentWord
            viewBinding.ivPlayerType.setImageDrawable(ContextCompat.getDrawable(viewBinding.root.context, R.drawable.ic_robot))
        }
    }

    inner class GameHistoryUserViewHolder(private val viewBinding: LayoutGameHistoryUserItemBinding)
        : BaseViewHolder(viewBinding) {
        override fun bind(itemDataModel: GameHistoryItemDataModel) {
            viewBinding.tvInput.text = itemDataModel.currentWord
            viewBinding.ivPlayerType.setImageDrawable(ContextCompat.getDrawable(viewBinding.root.context, R.drawable.ic_user))
        }
    }

}
