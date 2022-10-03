package cl.dal.dragdropswipe

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import androidx.viewbinding.ViewBinding

abstract class DiffUtilAdapter<T : Any, VH : RecyclerView.ViewHolder, V : ViewBinding>(
    itemCallback: DiffUtil.ItemCallback<T>,
) :
    ListAdapter<T, VH>(itemCallback) {

    private lateinit var binding: V

    abstract fun setBinding(layoutInflater: LayoutInflater, container: ViewGroup): V

    abstract fun createViewHolder(binding: V): VH

    abstract fun bindViewHolder(holder: VH, item: T)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VH {
        binding = setBinding(LayoutInflater.from(parent.context), parent)
        return createViewHolder(binding)
    }

    override fun onBindViewHolder(holder: VH, position: Int) {
        bindViewHolder(holder, getItem(position))
    }

    /**
     * Adds the specified [newElement] to the end of the current element list
     */
    fun addElement(newElement: T, submitFinishCallback: () -> Unit = {}) {
        addElement(newElement, currentList.size, submitFinishCallback)
    }

    /**
     * Inserts [newElement] into the current element list at the specified [index].
     */
    fun addElement(newElement: T, index: Int, submitFinishCallback: () -> Unit = {}) {
        addElements(mutableListOf(newElement), index, submitFinishCallback)
    }

    /**
     * Adds all of the elements of the specified [newElements] to the end of the current element list.
     */
    fun addElements(newElements: List<T>, submitFinishCallback: () -> Unit = {}) {
        addElements(newElements, currentList.size, submitFinishCallback)
    }

    /**
     * Adds all of the elements of the specified [newElements] collection to the current element list
     * at the specified [index].
     */
    fun addElements(newElements: List<T>, index: Int, submitFinishCallback: () -> Unit = {}) {
        val newList: MutableList<T> = mutableListOf()
        newList.addAll(currentList)
        newList.addAll(index, newElements)
        submitList(newList, submitFinishCallback)
    }

    /**
     * Remove the specified [elementToRemove] from the current element list
     */
    fun removeElement(elementToRemove: T, submitFinishCallback: () -> Unit = {}) {
        removeElements(mutableListOf(elementToRemove), submitFinishCallback)
    }


    /**
     * Remove all the specified [elementsToRemove] from the current element list
     */
    fun removeElements(elementsToRemove: List<T>, submitFinishCallback: () -> Unit = {}) {
        val newList: MutableList<T> = mutableListOf()
        newList.addAll(currentList)
        newList.removeAll(elementsToRemove)
        submitList(newList, submitFinishCallback)
    }

    fun isEmpty() = itemCount == 0
    fun isNotEmpty() = itemCount != 0
}
