package masaya.release.manage_menu

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.PopupMenu
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import masaya.release.manage_menu.data.FoodMenuViewModel
import masaya.release.manage_menu.data.*
import masaya.release.manage_menu.databinding.FragmentFoodmenuListBinding
import masaya.release.manage_menu.databinding.RowFoodmenuListBinding


class FoodmenuListFragment : Fragment(), CustomAdapterListener {
    private val viewModel: FoodMenuViewModel by activityViewModels {
        FoodMenuViewModelFactory(
            (activity?.application as FoodMenuApplication).database.FoodMenuDao()
        )
    }

    private var _binding: FragmentFoodmenuListBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentFoodmenuListBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val adapter = FoodListAdapter(this)

        binding.recyclerView.layoutManager = LinearLayoutManager(this.context)
        binding.recyclerView.adapter = adapter

        // リサイクルビューの行毎に区切り線を追加
        val decorator = DividerItemDecoration(activity, DividerItemDecoration.VERTICAL)
        binding.recyclerView.addItemDecoration(decorator)

        // データベースの変更を監視し、変更があれば動的にリストを自動更新
        viewModel.allItems().observe(this.viewLifecycleOwner) { items ->
            items.let {
                adapter.setFoodList(it)
            }
        }

        // データ追加画面へ遷移する「＋」ボタンクリック
        binding.floatingActionButton.setOnClickListener {
            val action = FoodmenuListFragmentDirections.actionFoodmenuListFragmentToFoodmenuAddFragment()
            this.findNavController().navigate(action)
        }
    }

    // ポップアップメニューの修正クリック
    override fun onEditClicked(foodId: Int) {
        val action = FoodmenuListFragmentDirections.actionFoodmenuListFragmentToFoodmenuEditFragment(foodId)
        this.findNavController().navigate(action)
    }

    // ポップアップメニューの削除クリック
    override fun onDeleteClicked(food: FoodMenu) {
        showConfirmationDialog(food)
    }

    // ───────────────────────────────────────────────────────────
    // DELETE（削除）
    // ───────────────────────────────────────────────────────────
    private fun showConfirmationDialog(food: FoodMenu) {
        MaterialAlertDialogBuilder(requireContext(), R.style.Body_ThemeOverlay_MaterialComponents_MaterialAlertDialog)
            .setMessage(getString(R.string.delete_question))
            .setCancelable(false)
            .setNegativeButton(getString(R.string.no)) { _, _ -> }
            .setPositiveButton(getString(R.string.yes)) { _, _ -> deleteFood(food)}
            .show()
    }

    private fun deleteFood(food: FoodMenu) {
        viewModel.deleteFood(food)
    }
}

interface CustomAdapterListener {
    fun onEditClicked(foodId: Int)
    fun onDeleteClicked(food: FoodMenu)
}

class FoodListAdapter(_listener: CustomAdapterListener) : RecyclerView.Adapter<FoodListAdapter.FoodViewHolder>() {

    private var foodList: List<FoodMenu?>? = null
    private val listener : CustomAdapterListener = _listener

    override fun onBindViewHolder(holder: FoodViewHolder, position: Int) {
        val current : FoodMenu = getItem(position)
        holder.bind(current)
        // 行がロングクリックされた時にポップアップメニューを表示する
        holder.itemView.setOnLongClickListener {
            holder.onLongClick()
            return@setOnLongClickListener true
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewtype: Int): FoodViewHolder {
        val view = RowFoodmenuListBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return FoodViewHolder(view, listener)
    }

    class FoodViewHolder(private val binding: RowFoodmenuListBinding, private val listener : CustomAdapterListener) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(item: FoodMenu) {
            binding.foodId.text = item.id.toString()
            binding.foodName.text = item.foodName
            binding.foodPrice.text = item.getFormattedPrice()
            binding.foodPriceNumeric.text = item.foodPrice.toString()
        }

        fun onLongClick() {
            val food = FoodMenu(
                binding.foodId.text.toString().toInt(),
                binding.foodName.text.toString(),
                binding.foodPriceNumeric.text.toString().toDouble()
            )
            showPopupMenu(food)
        }

        private fun showPopupMenu(food : FoodMenu) {
            val popup = PopupMenu(binding.root.context, binding.root, Gravity.END)
            popup.inflate(R.menu.menu_popup_menu_list)
            popup.setOnMenuItemClickListener { item ->
                when (item.itemId) {
                    R.id.menuListContextEdit -> {
                        listener.onEditClicked(food.id)
                        true
                    }
                    R.id.menuListContextDelete -> {
                        listener.onDeleteClicked(food)
                        true
                    }
                    else -> {
                        false
                    }
                }
            }
            popup.show()
        }
    }

    @SuppressLint("NotifyDataSetChanged")
    fun setFoodList(foodList: List<FoodMenu?>?) {
        this.foodList = foodList
        //これ大事。ないと、データ追加後に画面が更新されません。
        notifyDataSetChanged()
    }

    private fun getItem(position: Int): FoodMenu {
        return foodList!![position]!!
    }

    override fun getItemCount(): Int {
        return foodList?.size ?: 0
    }

}