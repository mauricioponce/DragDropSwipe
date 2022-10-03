package cl.dal.dragdropswipe

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.RecyclerView
import cl.dal.dragdropswipe.databinding.ElementItemBinding
import cl.dal.dragdropswipe.databinding.FragmentFirstBinding

/**
 * A simple [Fragment] subclass as the default destination in the navigation.
 */
class FirstFragment : Fragment() {

    private var _binding: FragmentFirstBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    private lateinit var adapter: MyListAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        _binding = FragmentFirstBinding.inflate(inflater, container, false)
        return binding.root

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        adapter = MyListAdapter()
        adapter.setList(getList())

        val helper = Helper(adapter)
        ItemTouchHelper(helper).attachToRecyclerView(binding.rvListing)

        binding.rvListing.adapter = adapter

    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun getList(): List<ItemElement> =
        (1..30).map { ItemElement("text $it") }

}

data class ItemElement(val text: String)

class MyListAdapter :
    DiffUtilAdapter<ItemElement, MyListAdapter.ItemViewHolder, ElementItemBinding>(ItemElementCallback),
    ItemTouchHelperAdapter {

    fun setList(elements: List<ItemElement>) {
        submitList(elements)
    }

    override fun setBinding(
        layoutInflater: LayoutInflater,
        container: ViewGroup
    ): ElementItemBinding =
        ElementItemBinding.inflate(layoutInflater, container, false)

    override fun createViewHolder(binding: ElementItemBinding): ItemViewHolder =
        ItemViewHolder(binding)


    override fun bindViewHolder(holder: ItemViewHolder, item: ItemElement) {
        holder.bind(item)
    }

    class ItemViewHolder(private val binding: ElementItemBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(item: ItemElement) {
            binding.textView.text = item.text
        }
    }

    object ItemElementCallback : DiffUtil.ItemCallback<ItemElement>() {
        override fun areItemsTheSame(oldItem: ItemElement, newItem: ItemElement): Boolean =
            oldItem.text == newItem.text


        override fun areContentsTheSame(oldItem: ItemElement, newItem: ItemElement): Boolean =
            oldItem == newItem
    }

    override fun onItemMove(fromPosition: Int, toPosition: Int): Boolean {
        val l = currentList.toMutableList()
        l.move(fromPosition, toPosition)
        submitList(l)
        return true
    }

    override fun onItemDismiss(position: Int) {
        TODO("Not yet implemented")
    }

    private fun <E> MutableList<E>.move(fromPosition: Int, toPosition: Int) {
        val item = get(fromPosition)
        removeAt(fromPosition)
        add(toPosition, item)
    }
}


