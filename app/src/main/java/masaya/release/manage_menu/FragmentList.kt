package masaya.release.manage_menu

import android.annotation.SuppressLint
import android.content.Context
import android.os.Bundle
import android.view.*
import android.widget.PopupWindow
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import masaya.release.manage_menu.imageFile.ImageFiles
import masaya.release.manage_menu.data.FoodMenuViewModel
import masaya.release.manage_menu.data.*
import masaya.release.manage_menu.databinding.*


class FragmentList : Fragment(), FoodListAdapter.PopupEventListner {

    // ユーザーのリストソート状態を保持するためのviewModel
    private val listOrderViewModel: ListOrderViewModel by activityViewModels()

    // ─────────────────────────────────────────────────────
    // 追加画面、修正画面へ遷移する
    // アクティビティに移譲するためのインターフェースとリスナー
    // ─────────────────────────────────────────────────────
    interface FromActivityListToListener {
        fun toAddFoodmenu()
        fun toEditFoodmenu(foodId: Int)
    }

    private var listener : FromActivityListToListener? = null

    override fun onAttach(context: Context) {
        super.onAttach(context)
        listener = context as? FromActivityListToListener
        if (listener == null) {
            throw ClassCastException("$context must implement FromActivityListToListener")
        }
    }

    override fun onDetach() {
        super.onDetach()
        listener = null
    }

    // ROOMデータベースアクセス用ビューモデル
    private val viewModel: FoodMenuViewModel by activityViewModels {
        FoodMenuViewModelFactory(
            (activity?.application as FoodMenuApplication).database.FoodMenuDao()
        )
    }

    // ─────────────────────────────────────────────────────
    // アクションバー ： テストデータロード
    // ─────────────────────────────────────────────────────
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // アクションバーのために必要
        setHasOptionsMenu(true)

        // リスト並び替え 初期値の設定
        listOrderViewModel.setLastOrderKey("foodName")
        listOrderViewModel.setLastOrderAsce(true)
        listOrderViewModel.setFoodNameAsce(true)
        listOrderViewModel.setFoodPriceAsce(true)
    }

    // アプリケーションバーのオプション用メニューを生成
    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        menu.clear()
        inflater.inflate(R.menu.menu_options_menu_list, menu)
        super.onCreateOptionsMenu(menu, inflater)
    }

    // テストデータロード
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        showLoadTestDataDialog()
        return true
    }

    private fun showLoadTestDataDialog() {
        MaterialAlertDialogBuilder(requireContext(), R.style.Body_ThemeOverlay_MaterialComponents_MaterialAlertDialog)
            .setMessage(getString(R.string.load_question))
            .setCancelable(false)
            .setNegativeButton(getString(R.string.no)) { _, _ -> }
            .setPositiveButton(getString(R.string.do_load)) { _, _ -> TestData.loadTestData(requireActivity(), viewModel)}
            .show()
    }

    // ─────────────────────────────────────────────────────
    // リスト表示
    // ─────────────────────────────────────────────────────

    private var _binding: FragmentListBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentListBinding.inflate(inflater, container, false)
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

                // 保存されたキーと順序で並べ替え
                val sortKey = listOrderViewModel.lastOrderKey.value
                val ascending = listOrderViewModel.lastOrderAsce.value as Boolean
                adapter.sortFoodList(sortKey!!, ascending)
            }
        }

        // ───────────────────────────────────────────────────────────────────────
        // ボタン類
        // ───────────────────────────────────────────────────────────────────────

        // メニュー名 並べ替え
        binding.foodNameOrder.setOnClickListener { sortByFoodName(adapter) }

        // 価格 並べ替え
        binding.foodPriceOrder.setOnClickListener { sortByFoodPrice(adapter) }

        // データ追加画面へ遷移する「＋」ボタンクリック
        binding.floatingActionButton.setOnClickListener {
            listener?.toAddFoodmenu()
        }
    }


    // ───────────────────────────────────────────────────────────────────────
    // 並べ替え
    // ───────────────────────────────────────────────────────────────────────

    // メニュー名 並べ替え
    private fun sortByFoodName(adapter : FoodListAdapter) {

        val drDown = ContextCompat.getDrawable(requireActivity(), R.drawable.ic_arrow_down)
        val drUp = ContextCompat.getDrawable(requireActivity(), R.drawable.ic_arrow_up)

        var ascending = listOrderViewModel.foodNameAsce.value as Boolean
        ascending = !ascending

        // 並べ替え
        adapter.sortFoodList("foodName", ascending)

        listOrderViewModel.setLastOrderKey("foodName")
        listOrderViewModel.setLastOrderAsce(ascending)
        listOrderViewModel.setFoodNameAsce(ascending)

        // アイコンの矢印向き変更
        if (ascending) {
            binding.foodNameOrder.setImageDrawable(drDown)
        } else {
            binding.foodNameOrder.setImageDrawable(drUp)
        }
    }

    // 価格 並べ替え
    private fun sortByFoodPrice(adapter: FoodListAdapter) {

        val drDown = ContextCompat.getDrawable(requireActivity(), R.drawable.ic_arrow_down)
        val drUp = ContextCompat.getDrawable(requireActivity(), R.drawable.ic_arrow_up)

        var ascending = listOrderViewModel.foodPriceAsce.value as Boolean
        ascending = !ascending

        adapter.sortFoodList("foodPrice", ascending)

        // 並べ替え
        listOrderViewModel.setLastOrderKey("foodPrice")
        listOrderViewModel.setLastOrderAsce(ascending)
        listOrderViewModel.setFoodPriceAsce(ascending)

        // アイコンの矢印向き変更
        if (ascending) {
            binding.foodPriceOrder.setImageDrawable(drDown)
        } else {
            binding.foodPriceOrder.setImageDrawable(drUp)
        }
    }

    // ───────────────────────────────────────────────────────────
    // ポップアップメニュー
    // ───────────────────────────────────────────────────────────

    // ポップアップメニューの修正クリック
    override fun onEditClicked(foodId: Int) {
        listener?.toEditFoodmenu(foodId)
    }

    // ポップアップメニューの削除クリック
    override fun onDeleteClicked(foodId: Int) {
        val food = FoodMenu(foodId)
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

// ───────────────────────────────────────────────────────────
// リスト表示用のアダプタ
// 引数：_listener リストをクリックし「修正」、「削除」を選択できるが
//      選択時の処理は呼び出し元のフラグメントで処理したい。
// ───────────────────────────────────────────────────────────
class FoodListAdapter(_listener: PopupEventListner) : RecyclerView.Adapter<FoodListAdapter.FoodViewHolder>() {

    interface PopupEventListner {
        fun onEditClicked(foodId: Int)
        fun onDeleteClicked(foodId: Int)
    }

    private val listener : PopupEventListner = _listener
    private var foodList: List<FoodMenu?>? = mutableListOf()

    override fun onBindViewHolder(holder: FoodViewHolder, position: Int) {
        val current : FoodMenu = getItem(position)
        // １行分のデータcurrentを画面へセットする
        holder.bind(current)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewtype: Int): FoodViewHolder {
        val view = RowFoodmenuListBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return FoodViewHolder(view, listener)
    }

    class FoodViewHolder(
        private val binding: RowFoodmenuListBinding,
        private val listener : PopupEventListner)
        : RecyclerView.ViewHolder(binding.root) {

        // １行分のデータcurrentを画面へセットする
        fun bind(item: FoodMenu) {

            binding.foodId.text = item.id.toString()
            binding.foodName.text = item.getShortFoodName()
            binding.foodPrice.text = item.getFormattedPrice()

            // 画像のロード（縮小している）
            val loadSmallBmp =
                ImageFiles.readSmallImgsFromFileName(binding.root.context, item.bmpName)
            binding.foodimage.setImageBitmap(loadSmallBmp)

            // ︙がクリックされた時にポップアップメニューを表示する
            binding.rowMenu.setOnClickListener {
                onClick()
            }
        }

        // ポップアップメニュー表示（修正／削除用）
        private fun onClick() {
            val foodId  = binding.foodId.text.toString().toInt()
            showPopupWindow(foodId)
        }

        // ポップアップメニュー表示
        private fun showPopupWindow(foodId: Int) {

            val popview = LayoutInflater.from(binding.root.context).inflate(R.layout.popup_on_list, null)

            val popupWindow = PopupWindow(binding.root.context)

            // 表示したポップアップ上で 修正クリック時のイベント
            popview.findViewById<TextView>(R.id.textEdit).setOnClickListener{
                listener.onEditClicked(foodId)
                if (popupWindow.isShowing) {
                    popupWindow.dismiss()
                }
            }
            // 表示したポップアップ上で 削除クリック時のイベント
            popview.findViewById<TextView>(R.id.textDelete).setOnClickListener{
                listener.onDeleteClicked(foodId)
                if (popupWindow.isShowing) {
                    popupWindow.dismiss()
                }
            }

            popupWindow.contentView = popview
            popupWindow.isOutsideTouchable = true
            popupWindow.isFocusable = true

            // 高さを直接指定しないと、リストの下部で呼び出された時にはみ出してしまう。、
            // 本来、必要とされる高さを取得しその値をセットすべきと考えるけど
            // その方法がよくわからない。今回は固定値埋め込みで良しとする。
            popupWindow.height = 225

            // ポップアップ表示
            popupWindow.showAsDropDown(binding.rowMenu)

        }
    }

    @SuppressLint("NotifyDataSetChanged")
    fun setFoodList(foodList: List<FoodMenu?>?) {

        this.foodList = foodList
        //これ大事。ないと、データ追加後に画面が更新されません。
        notifyDataSetChanged()
    }

    @SuppressLint("NotifyDataSetChanged")
    fun sortFoodList(Key : String, ascending : Boolean) {

        val fl = when (Key) {
            "foodName" ->
                if (ascending) {
                    foodList?.sortedBy { it?.foodName }
                } else {
                    foodList?.sortedBy { it?.foodName }?.reversed()
                }
            else ->
                if (ascending) {
                    foodList?.sortedWith(compareBy({ it?.foodPrice }, { it?.foodName }))
                } else {
                    foodList?.sortedWith(compareBy({ it?.foodPrice }, { it?.foodName }))?.reversed()
                }
        }
        this.foodList = fl

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