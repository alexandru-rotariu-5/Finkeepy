package com.alexrotariu.finkeepy.ui.dashboard

import android.graphics.Rect
import android.view.View
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.ItemDecoration


class RecordItemDecoration(private val verticalSpaceHeight: Int) : ItemDecoration() {

    override fun getItemOffsets(outRect: Rect, view: View, parent: RecyclerView, state: RecyclerView.State) {
        // Add top margin for all items except the first one
        if (parent.getChildAdapterPosition(view) != 0) {
            outRect.top = verticalSpaceHeight
        }
    }
}
