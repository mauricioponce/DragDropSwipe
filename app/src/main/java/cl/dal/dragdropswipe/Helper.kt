package cl.dal.dragdropswipe

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.graphics.drawable.Drawable
import android.util.Log
import android.view.View
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.ItemTouchHelper.ACTION_STATE_SWIPE
import androidx.recyclerview.widget.RecyclerView


private const val dragFlags = ItemTouchHelper.UP or ItemTouchHelper.DOWN
private const val swipeFlags = ItemTouchHelper.LEFT

private const val TAG = "Helper"

class Helper(private val adapter: ItemTouchHelperAdapter, private val context: Context) : ItemTouchHelper.SimpleCallback(dragFlags, swipeFlags) {

    // we want to cache these and not allocate anything repeatedly in the onChildDraw method
    private lateinit var background: Drawable
    private lateinit var deleteIcon: Drawable
    private var itemMargin = 0
    private var initiated = false

    private fun init() {
        background = ColorDrawable(Color.RED)
        deleteIcon = ContextCompat.getDrawable(context, R.drawable.ic_delete)!!
        itemMargin = context.resources.getDimension(R.dimen.ic_clear_margin).toInt()
        initiated = true
    }

    override fun onMove(
        recyclerView: RecyclerView,
        viewHolder: RecyclerView.ViewHolder,
        target: RecyclerView.ViewHolder
    ): Boolean {
        adapter.onItemMove(
            viewHolder.adapterPosition,
            target.adapterPosition
        )

        return true
    }

    override fun onChildDraw(
        canvas: Canvas,
        recyclerView: RecyclerView,
        viewHolder: RecyclerView.ViewHolder,
        dX: Float,
        dY: Float,
        actionState: Int,
        isCurrentlyActive: Boolean
    ) {
        //Log.d(TAG, "onChildDraw: ------ $dX $isCurrentlyActive ")

        val itemView: View = viewHolder.itemView


        // not sure why, but this method get's called for viewholder that are already swiped away
        if (viewHolder.adapterPosition == -1) {
            // not interested in those
            return
        }

        if(actionState == ACTION_STATE_SWIPE) {
            if (!initiated) {
                init()
            }

            drawBackground(itemView, dX, canvas)

            drawIcon(itemView, canvas)

            if(stopDrawing(dX)) {
                return
            }
        }

        super.onChildDraw(canvas, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive)
    }

    private fun stopDrawing(dX: Float): Boolean = dX <= -200

    private fun drawIcon(itemView: View, canvas: Canvas) {
        val itemHeight: Int = itemView.bottom - itemView.top
        val intrinsicWidth = deleteIcon.intrinsicWidth
        val intrinsicHeight = deleteIcon.intrinsicHeight

        val deleteIconLeft: Int = itemView.right - itemMargin - intrinsicWidth
        val deleteRight: Int = itemView.right - itemMargin
        val deleteTop: Int = itemView.top + (itemHeight - intrinsicHeight) / 2
        val deleteBottom = deleteTop + intrinsicHeight
        deleteIcon.setBounds(deleteIconLeft, deleteTop, deleteRight, deleteBottom)

        deleteIcon.draw(canvas)
    }

    private fun drawBackground(itemView: View, dX: Float, canvas: Canvas) {
        //val right = itemView.right - itemMargin
        val right = itemView.right

        val kindOfPadding = 35
        var left = right + dX.toInt() - kindOfPadding
        if(left <= 0) {
            left = 0 + itemMargin
        }

        Log.d(TAG, "left: $left - right: $right - dX: $dX")

        background.setBounds(
            left,
            itemView.top,
            right,
            itemView.bottom
        )
        background.draw(canvas)
    }

    override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
        adapter.onItemDismiss(viewHolder.adapterPosition)
    }

    override fun isItemViewSwipeEnabled(): Boolean = true

    override fun clearView(recyclerView: RecyclerView, viewHolder: RecyclerView.ViewHolder) {
        super.clearView(recyclerView, viewHolder)
        viewHolder.itemView.setBackgroundColor(ContextCompat.getColor(viewHolder.itemView.context, R.color.white))
    }

    override fun onSelectedChanged(viewHolder: RecyclerView.ViewHolder?, actionState: Int) {
        super.onSelectedChanged(viewHolder, actionState)
        if(actionState == ItemTouchHelper.ACTION_STATE_DRAG) {
            viewHolder?.itemView?.setBackgroundColor(Color.CYAN)
        }
    }
}

interface ItemTouchHelperAdapter {
    fun onItemMove(fromPosition: Int, toPosition: Int): Boolean
    fun onItemDismiss(position: Int)
}