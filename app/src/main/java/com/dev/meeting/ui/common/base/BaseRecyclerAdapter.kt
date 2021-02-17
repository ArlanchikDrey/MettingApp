
package com.dev.meeting.ui.common.base

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import androidx.recyclerview.widget.RecyclerView
import com.dev.meeting.BR

/**
 * A generic RecyclerView adapter that uses Data Binding & DiffUtil.
 *
 * @param <T> Type of the items in the list
 * @param <V> The type of the ViewDataBinding</V></T>
 */

abstract class BaseRecyclerAdapter<T>: RecyclerView.Adapter<BaseRecyclerAdapter<T>.BaseViewHolder<T>>() {

	override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) =
		BaseViewHolder<T>(
			DataBindingUtil.inflate(
				LayoutInflater.from(parent.context),
				viewType,
				parent,
				false
			)
		)

	override fun onBindViewHolder(holder: BaseViewHolder<T>, position: Int) =
		holder.bind(getItem(position))

	override fun getItemViewType(position: Int) = getLayoutIdForItem(position)

	abstract fun getItem(position: Int): T
	abstract fun getLayoutIdForItem(position: Int): Int

	private var mClickListener: ((T, Int) -> Unit)? = null
	// allows clicks events to be caught
	open fun setOnItemClickListener(listener: (T, Int) -> Unit) {
		mClickListener = listener
	}

	override fun onFailedToRecycleView(holder: BaseViewHolder<T>): Boolean { return true }

	inner class BaseViewHolder<T>(private val binding: ViewDataBinding):
			RecyclerView.ViewHolder(binding.root){

		init {
			mClickListener?.let { mClickListener ->
				itemView.setOnClickListener {
					mClickListener.invoke(getItem(adapterPosition), adapterPosition)
				}
			}
		}

		fun bind(item: T) {
			binding.setVariable(BR.bindItem, item)
			binding.executePendingBindings()
		}
	}
}