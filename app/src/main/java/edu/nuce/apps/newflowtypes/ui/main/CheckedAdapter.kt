package edu.nuce.apps.newflowtypes.ui.main

import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.ViewGroup
import androidx.recyclerview.selection.ItemDetailsLookup
import androidx.recyclerview.selection.ItemKeyProvider
import androidx.recyclerview.selection.SelectionTracker
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import edu.nuce.apps.newflowtypes.databinding.ItemCheckBinding

class CheckedAdapter : ListAdapter<Checked, CheckedVH>(DiffCallback) {

    var tracker: SelectionTracker<Long>? = null

    init {
        setHasStableIds(true)
    }

    override fun getItemId(position: Int): Long = position.toLong()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CheckedVH {
        val layoutInflater = LayoutInflater.from(parent.context)
        val binding = ItemCheckBinding.inflate(layoutInflater, parent, false)
        return CheckedVH(binding)
    }

    override fun onBindViewHolder(holder: CheckedVH, position: Int) {
        getItem(position).let {
            holder.bind(
                it,
                tracker?.isSelected(
                    position.toLong()
                ) ?: false
            )
        }
    }
}

class CheckedVH(
    private val binding: ItemCheckBinding
) : RecyclerView.ViewHolder(binding.root) {

    fun bind(item: Checked, checked: Boolean = false) {
        binding.run {
            text.text = item.data
            check.isSelected = checked
        }
    }

    fun getItemDetails(): ItemDetailsLookup.ItemDetails<Long> =
        object : ItemDetailsLookup.ItemDetails<Long>() {
            override fun getPosition(): Int = adapterPosition
            override fun getSelectionKey(): Long = itemId
            override fun inSelectionHotspot(e: MotionEvent): Boolean = true
        }
}

data class Checked(
    val data: String
)

object DiffCallback : DiffUtil.ItemCallback<Checked>() {
    override fun areItemsTheSame(oldItem: Checked, newItem: Checked): Boolean {
        return oldItem.data == newItem.data
    }

    override fun areContentsTheSame(oldItem: Checked, newItem: Checked): Boolean {
        return oldItem == newItem
    }
}

class CheckedItemDetailsLookup(private val recyclerView: RecyclerView) : ItemDetailsLookup<Long>() {

    override fun getItemDetails(e: MotionEvent): ItemDetails<Long>? {
        val view = recyclerView.findChildViewUnder(e.x, e.y)
        if (view != null) {
            return (recyclerView.getChildViewHolder(view) as CheckedVH).getItemDetails()
        }
        return null
    }
}

class CheckedSinglePredicate : SelectionTracker.SelectionPredicate<Long>() {

    override fun canSetStateForKey(key: Long, nextState: Boolean): Boolean =
        !(key.toInt() == 0 && !nextState)

    override fun canSetStateAtPosition(position: Int, nextState: Boolean): Boolean = true

    override fun canSelectMultiple(): Boolean = true
}

class CheckedKeyProvider(private val recyclerView: RecyclerView) : ItemKeyProvider<Long>(
    SCOPE_MAPPED
) {

    override fun getKey(position: Int): Long? {
        return recyclerView.adapter?.getItemId(position)
    }

    override fun getPosition(key: Long): Int {
        return recyclerView.findViewHolderForItemId(key)?.layoutPosition ?: RecyclerView.NO_POSITION
    }
}