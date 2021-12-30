package masaya.release.manage_menu

import android.content.Context
import android.content.Intent
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Bundle
import android.text.Editable
import android.view.*
import android.view.inputmethod.InputMethodManager
import android.widget.Spinner
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import androidx.navigation.fragment.findNavController
import masaya.release.manage_menu.data.*
import masaya.release.manage_menu.databinding.FragmentEditBinding

class FragmentEdit : Fragment() {

    private var foodID : Int = -1

    // ROOMデータベース更新用のviewModel
    private val viewModel: FoodMenuViewModel by activityViewModels {
        FoodMenuViewModelFactory(
            (activity?.application as FoodMenuApplication).database.FoodMenuDao()
        )
    }

    // ユーザーの画面入力状態を保持するためのviewModel
    private val userInputViewModel: UserInputViewModel by activityViewModels()

    private var _binding: FragmentEditBinding? = null
    private val binding get() = _binding!!

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // メニューのために必要
        setHasOptionsMenu(true)
    }

    // アプリケーションバーのオプション用メニューを生成
    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        // 左上の←（戻るボタン）を表示
        val activity = activity as AppCompatActivity?
        activity?.supportActionBar?.setDisplayHomeAsUpEnabled(true)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentEditBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.lifecycleOwner = viewLifecycleOwner
        binding.userInputViewModel = userInputViewModel

        // 修正対象のID
        foodID = userInputViewModel.foodId.value!!

        // スピナー（ドロップダウンボックス）の初期設定
        val spinner: Spinner = binding.foodTypeSpinner
        setFoodtypeSpinner(requireActivity(), spinner)

        // 入力途中の情報があれば、再設定し入力を補助する
        reloadUserInput()

        // 画像拡大ボタン
        binding.floatingImageSpreadButton.setOnClickListener {showExtendedImage() }

        // 画像選択ボタン
        binding.floatingImageButton.setOnClickListener {
            val intent = Intent(Intent.ACTION_OPEN_DOCUMENT).apply {
                addCategory(Intent.CATEGORY_OPENABLE)
                type = "image/*"
            }
            launcher.launch(intent)
        }

        // 修正ボタン
        binding.editFood.setOnClickListener { editFood() }

        // 一覧と同様にデータベースの変更を監視しているので、変更があれば動的に詳細データを自動更新
        // データ１行をDBから取得し画面へ表示（動的監視付き）の良いサンプル
        viewModel.retrieveFood(foodID).observe(this.viewLifecycleOwner) { food ->
            bind(food)
        }
    }

    // ───────────────────────────────────────────────────────────
    // 画像処理
    // ───────────────────────────────────────────────────────────

    // 画像選択 ---------------------------------------------------
    private val launcher = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode != AppCompatActivity.RESULT_OK) {
            return@registerForActivityResult
        } else {
            try {
                result.data?.data?.also { uri : Uri ->
                    val inputStream = activity?.contentResolver?.openInputStream(uri)
                    val bmp = BitmapFactory.decodeStream(inputStream)

                    // 取得した画像を内部ストレージへ書き込む
                    val fileName = binding.foodImageBmpName.text.toString()
                    saveImgsFromBmp(bmp, fileName, requireActivity())

                    // 内部ストレージへ書き込んだファイルから改めて画像取得
                    val loadbmp = readImgsFromFileName(fileName, requireActivity())
                    binding.foodimage.setImageBitmap(loadbmp)
                }
            } catch (e: Exception) {
                Toast.makeText(activity, "エラーが発生しました", Toast.LENGTH_LONG).show()
            }
        }
    }

    // 画像拡大 ---------------------------------------------------------
    private fun showExtendedImage() {
        // フラグメントを遷移する前に、画面の入力状態を保持する
        savetUserInput()

        val bmpName : String = binding.foodImageBmpName.text.toString()
        if (bmpName=="") { return }
        val action = FragmentEditDirections.actionFragmentEditToFragmentImage(bmpName)
        this.findNavController().navigate(action)
    }

    // ───────────────────────────────────────────────────────────
    // 入力途中の情報があれば、再設定し入力を補助する
    // ───────────────────────────────────────────────────────────
    private fun reloadUserInput(){

        // メニュー名
        val foodName = binding.userInputViewModel?.foodName?.value
        if (foodName != null && foodName != "") {
            binding.foodName.setText(foodName)
        }

        // 価格
        val price = binding.userInputViewModel?.price?.value
        if (price != null && price != "") {
            binding.foodPrice.setText(price)
        }

        // 画像ファイル名
        val bmpName = binding.userInputViewModel?.bmpName?.value
        if (bmpName != null && bmpName != "") {
            binding.foodImageBmpName.text = bmpName

            // 画像ロード
            val loadbmp = readImgsFromFileName(bmpName, requireActivity())
            binding.foodimage.setImageBitmap(loadbmp)
        }

        // 販売開始日
        val startDate = binding.userInputViewModel?.startDate?.value
        if (startDate != null && startDate != "") {
            binding.foodStartDate.setText(startDate)
        }

        // カテゴリー
        val foodType = binding.userInputViewModel?.foodType?.value
        if (foodType != null && foodType != "") {
            setSpinnerSelection(binding.foodTypeSpinner, foodType)
        }

        // 冬季限定
        val winterOnly = binding.userInputViewModel?.winterOnly?.value
        if (winterOnly != null) {
            binding.winterOnly.isChecked = winterOnly
        }

    }

    // 画面の入力状態を保持する
    private fun savetUserInput() {
        binding.userInputViewModel?.setFoodName(binding.foodName.text.toString())
        binding.userInputViewModel?.setPrice(binding.foodPrice.text.toString())
        binding.userInputViewModel?.setBmpName(binding.foodImageBmpName.text.toString())
        binding.userInputViewModel?.setStartDate(binding.foodStartDate.text.toString())
        binding.userInputViewModel?.setFoodType(binding.foodTypeSpinner.selectedItem as String)
        binding.userInputViewModel?.setWinterOnly(binding.winterOnly.isChecked)
    }


    // ───────────────────────────────────────────────────────────
    // DBのデータを画面に表示
    // ───────────────────────────────────────────────────────────
    private fun bind(food: FoodMenu) {
        binding.foodName.setText(food.foodName)
        binding.foodPrice.setText(food.foodPrice.toInt().toString())
        setSpinnerSelection(binding.foodTypeSpinner, food.foodType)
        binding.winterOnly.isChecked = food.WinterOnly
        binding.foodStartDate.setText(food.getFormattedStartDate())

        // DBの画像ファイル名から画像取得
        binding.foodImageBmpName.text = food.bmpName
        val loadbmp = readImgsFromFileName(food.bmpName, requireActivity())
        binding.foodimage.setImageBitmap(loadbmp)

    }

    // ───────────────────────────────────────────────────────────
    // メニュー一覧（ActivityList）へ戻るためのリスナー
    // ───────────────────────────────────────────────────────────
    interface FromActivityEditToListener {
        fun toListActivity()
    }

    private var listener : FromActivityEditToListener? = null

    override fun onAttach(context: Context) {
        super.onAttach(context)
        listener = context as? FromActivityEditToListener
        if (listener == null) {
            throw ClassCastException("$context must implement FromActivityEditToListener")
        }
    }

    override fun onDetach() {
        super.onDetach()
        listener = null
    }

    // ───────────────────────────────────────────────────────────
    // EDIT（変更）
    // ───────────────────────────────────────────────────────────
    private fun editFood() {
        // 画面入力チェック
        val inputFoodName : Editable? = binding.foodName.text
        val inputFoodStartDate : Editable? = binding.foodStartDate.text
        val inputFoodPrice : Editable? = binding.foodPrice.text
        if (!checkInputFood(inputFoodName, inputFoodStartDate, inputFoodPrice)){
            showErrorMessage()
            return
        }

        // DB更新
        val ret : Boolean = viewModel.updateFood(
            foodID,
            binding.foodName.text.toString(),
            binding.foodPrice.text.toString(),
            binding.foodImageBmpName.text.toString(),
            binding.foodStartDate.text.toString(),
            binding.foodTypeSpinner.selectedItem as String,
            binding.winterOnly.isChecked
        )
        if (!ret) {
            showErrorMessage()
            return
        }

        //一覧へ戻る
        listener?.toListActivity()
    }

    private fun showErrorMessage() {
        MaterialAlertDialogBuilder(requireContext(), R.style.Body_ThemeOverlay_MaterialComponents_MaterialAlertDialog)
            .setMessage(getString(R.string.input_error))
            .setCancelable(false)
            .setNegativeButton("OK") { _, _ -> }
            .show()
    }

    override fun onDestroyView() {
        super.onDestroyView()

        // Hide keyboard.
        val inputMethodManager = requireActivity().getSystemService(Context.INPUT_METHOD_SERVICE) as
                InputMethodManager
        inputMethodManager.hideSoftInputFromWindow(requireActivity().currentFocus?.windowToken, 0)
        _binding = null
    }
}