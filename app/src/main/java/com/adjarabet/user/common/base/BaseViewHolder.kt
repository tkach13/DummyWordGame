package com.adjarabet.user.common.base

import androidx.recyclerview.widget.RecyclerView
import androidx.viewbinding.ViewBinding
import com.adjarabet.user.domain.models.GameHistoryItemDataModel

abstract class BaseViewHolder(binding: ViewBinding):RecyclerView.ViewHolder(binding.root) {
   abstract fun bind(itemDataModel:GameHistoryItemDataModel)
}