package masaya.release.manage_menu

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.*
import android.widget.PopupMenu
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import masaya.release.manage_menu.data.FoodMenuViewModel
import masaya.release.manage_menu.data.*
import masaya.release.manage_menu.databinding.*


class FoodmenuListFragment : Fragment(), CustomAdapterListener {
    private val viewModel: FoodMenuViewModel by activityViewModels {
        FoodMenuViewModelFactory(
            (activity?.application as FoodMenuApplication).database.FoodMenuDao()
        )
    }

    private var _binding: FragmentFoodmenuListBinding? = null
    private val binding get() = _binding!!

    interface fromListActivityToListener {
        fun toAddFoodmenu()
        fun toEditFoodmenu()
    }

    private var listener : fromListActivityToListener? = null

    override fun onAttach(context: Context) {
        super.onAttach(context)
        listener = context as? fromListActivityToListener
        if (listener == null) {
            throw ClassCastException("$context must implement fromListActivityToListener")
        }
    }

    override fun onDetach() {
        super.onDetach()
        listener = null
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // メニューのために必要
        setHasOptionsMenu(true)
    }

    // アプリケーションバーのオプション用メニューを生成
    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        // 左上の←（戻るボタン）を消す
        val activity = activity as AppCompatActivity?
        activity?.supportActionBar?.setDisplayHomeAsUpEnabled(false)
    }

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
            listener?.toAddFoodmenu()
        }
    }

    // ポップアップメニューの修正クリック
    override fun onEditClicked(foodId: Int) {
        listener?.toEditFoodmenu()
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
            holder.onClick()
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

            // 画像のロード
            val loadbmp = readImgsFromFileName(item.bmpName, binding.root.context)
            binding.foodimage.setImageBitmap(loadbmp)

            binding.foodId.text = item.id.toString()
            binding.foodName.text = item.foodName
            binding.foodPrice.text = item.getFormattedPrice()

            // ︙がクリックされた時にポップアップメニューを表示する
            binding.rowMenu.setOnClickListener {
                onClick()
            }

        }

        fun onClick() {
            val food = FoodMenu(
                binding.foodId.text.toString().toInt(),
                binding.foodName.text.toString()
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