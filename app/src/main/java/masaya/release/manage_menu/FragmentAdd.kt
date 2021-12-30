package masaya.release.manage_menu

import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Bundle
import android.text.Editable
import android.view.*
import android.view.inputmethod.InputMethodManager
import android.widget.*
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import masaya.release.manage_menu.data.FoodMenuViewModel
import masaya.release.manage_menu.data.FoodMenuViewModelFactory
import java.text.ParseException
import java.lang.IllegalArgumentException
import java.text.SimpleDateFormat
import android.widget.Spinner
import java.io.BufferedInputStream
import java.io.ByteArrayOutputStream
import java.io.FileOutputStream
import java.io.IOException
import java.util.*
import android.widget.Toast
import masaya.release.manage_menu.data.UserInputViewModel
import masaya.release.manage_menu.databinding.FragmentAddBinding


class FragmentAdd : Fragment() {

    // ROOMデータベース更新用のviewModel
    private val viewModel: FoodMenuViewModel by activityViewModels {
        FoodMenuViewModelFactory(
            (activity?.application as FoodMenuApplication).database.FoodMenuDao()
        )
    }

    // ユーザーの画面入力状態を保持するためのviewModel
    private val userInputViewModel: UserInputViewModel by activityViewModels()

    private var _binding: FragmentAddBinding? = null
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
        container?.removeAllViews()
        _binding = FragmentAddBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.lifecycleOwner = viewLifecycleOwner
        binding.userInputViewModel = userInputViewModel

        // スピナー（ドロップダウンボックス）の初期設定
        val spinner: Spinner = binding.foodTypeSpinner
        setFoodtypeSpinner(requireActivity(), spinner)

        // 入力途中の情報があれば、再設定し入力を補助する
        reloadUserInput()

        // 画像拡大ボタン
        binding.floatingImageSpreadButton.setOnClickListener {showExtendedImage() }

        // 画像選択ボタン
        binding.floatingImageEditButton.setOnClickListener {
            val intent = Intent(Intent.ACTION_OPEN_DOCUMENT).apply {
                addCategory(Intent.CATEGORY_OPENABLE)
                type = "image/*"
            }
            launcher.launch(intent)
        }

        // 追加ボタン
        binding.saveAction.setOnClickListener {addNewFood() }
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
                    val fileName = "${UUID.randomUUID()}.jpg"
                    binding.foodImageBmpName.text = fileName
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
        val action = FragmentAddDirections.actionFragmentAddToFragmentImage(bmpName)
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
    // メニュー一覧（ActivityList）へ戻るためのリスナー
    // ───────────────────────────────────────────────────────────
    interface FromActivityAddToListener {
        fun toListActivity()
    }
    private var listener : FromActivityAddToListener? = null

    override fun onAttach(context: Context) {
        super.onAttach(context)
        listener = context as? FromActivityAddToListener
        if (listener == null) {
            throw ClassCastException("$context must implement FromActivityAddToListener")
        }
    }
    override fun onDetach() {
        super.onDetach()
        listener = null
    }

    // ───────────────────────────────────────────────────────────
    // add（追加）
    // ───────────────────────────────────────────────────────────
    private fun addNewFood() {
        // 画面入力チェック
        val inputFoodName : Editable? = binding.foodName.text
        val inputFoodStartDate : Editable? = binding.foodStartDate.text
        val inputFoodPrice : Editable? = binding.foodPrice.text
        if (!checkInputFood(inputFoodName, inputFoodStartDate, inputFoodPrice)){
            showErrorMessage()
            return
        }

        // DB更新
        val ret : Boolean = viewModel.addNewFood(
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
            .setTitle("")
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

// ユーザーが選択した画像を内部ストレージに保管する
fun saveImgsFromBmp(bmp: Bitmap, outputFileName:String, context: Context) {
    try {
        val byteArrOutputStream = ByteArrayOutputStream()
        val fileOutputStream: FileOutputStream = context.openFileOutput(outputFileName,Context.MODE_PRIVATE)
        bmp.compress(Bitmap.CompressFormat.JPEG, 100, byteArrOutputStream)
        fileOutputStream.write(byteArrOutputStream.toByteArray())
        fileOutputStream.close()
    }
    catch (e:Exception){
        e.printStackTrace()
    }
}

// 内部ストレージ内の画像を取得する
fun readImgsFromFileName(fileName:String, context: Context): Bitmap? {
    return try {
        val bufferedInputStream = BufferedInputStream(context.openFileInput(fileName))
        BitmapFactory.decodeStream(bufferedInputStream)
    }
    catch (e: IOException){
        e.printStackTrace()
        null
    }
}

fun String.isDate(pattern: String = "yyyy/MM/dd"): Boolean {
    val sdFormat = try {
        SimpleDateFormat(pattern)
    } catch (e: IllegalArgumentException) {
        return false
    }
    sdFormat.let {
        try {
            it.parse(this)
        } catch (e: ParseException){
            return false
        }
    }
    return true
}

fun String.toDateorNull(pattern: String = "yyyy/MM/dd"): Date? {
    val sdFormat = try {
        SimpleDateFormat(pattern)
    } catch (e: IllegalArgumentException) {
        return null
    }
    val date = sdFormat.let {
        try {
            it.parse(this)
        } catch (e: ParseException){
            return null
        }
    }
    return date
}

// メニューのカテゴリー用スピナーの初期設定
fun setFoodtypeSpinner(content : Context, spinner: Spinner) {
    ArrayAdapter.createFromResource(
        content,
        R.array.foodType_array,
        android.R.layout.simple_spinner_item
    ).also { adapter ->
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinner.adapter = adapter
    }
}


// スピナー（ドロップダウンボックス）へ指定された値をセットする
fun setSpinnerSelection(spinner: Spinner, item: String) {
    val adapter = spinner.adapter
    var index = 0
    for (i in 0 until adapter.count) {
        if (adapter.getItem(i) == item) {
            index = i
            break
        }
    }
    spinner.setSelection(index)
}

// 画面から入力された内容をチェック
// 修正時もこの関数を利用しているのでパブリック
fun checkInputFood(inputFoodName : Editable?, inputFoodStartDate : Editable?, inputFoodPrice : Editable?) : Boolean{
    if (inputFoodName.isNullOrEmpty()){
        return false
    }
    if (inputFoodStartDate.isNullOrEmpty()){
        return false
    }
    if(!inputFoodStartDate.toString().isDate()) {
        return false
    }
    if (inputFoodPrice.isNullOrEmpty()){
        return false
    }
    val checkInt = inputFoodPrice.toString().toIntOrNull()
    if (checkInt == null){
        return false
    }
    return true
}