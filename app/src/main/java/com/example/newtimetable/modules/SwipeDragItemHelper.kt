package com.example.newtimetable.modules

import android.content.Context
import android.graphics.Canvas
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.RecyclerView
import com.example.newtimetable.R
import com.example.newtimetable.interfaces.ItemTouchHelperLestener
import it.xabaras.android.recyclerview.swipedecorator.RecyclerViewSwipeDecorator

class SwipeDragItemHelper(var itemTouchHelperListener: ItemTouchHelperLestener, val context: Context) :
    ItemTouchHelper.Callback() {

    override fun isLongPressDragEnabled(): Boolean {
        return false
    }

    override fun isItemViewSwipeEnabled(): Boolean {
        return true
    }

    override fun getMovementFlags(
        recyclerView: RecyclerView,
        viewHolder: RecyclerView.ViewHolder
    ): Int {
        val swipeFlag = ItemTouchHelper.START or ItemTouchHelper.END
        return makeMovementFlags(0, swipeFlag)
    }

    override fun onMove(
        recyclerView: RecyclerView,
        viewHolder: RecyclerView.ViewHolder,
        target: RecyclerView.ViewHolder
    ): Boolean {
        return false
    }

    override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
        itemTouchHelperListener.onItemDismixx(viewHolder.adapterPosition)
    }

    override fun onChildDraw(
        c: Canvas,
        recyclerView: RecyclerView,
        viewHolder: RecyclerView.ViewHolder,
        dX: Float,
        dY: Float,
        actionState: Int,
        isCurrentlyActive: Boolean
    ) {
        RecyclerViewSwipeDecorator.Builder(c, recyclerView, viewHolder,dX, dY, actionState, isCurrentlyActive)
            .addBackgroundColor(ContextCompat.getColor(context, R.color.colorRed))
            .addActionIcon(R.drawable.ic_delete)
            .addSwipeLeftLabel("Удалить")
            .addSwipeRightLabel("Удалить")
            .setSwipeLeftLabelColor(ContextCompat.getColor(context, R.color.colorWhite))
            .setSwipeRightLabelColor(ContextCompat.getColor(context, R.color.colorWhite))
            .create()
            .decorate()
        super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive)
    }
}