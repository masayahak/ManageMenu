package masaya.release.manage_menu

import android.content.Intent
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Bundle
import android.text.Editable
import android.util.Log
import android.view.*
import android.widget.ArrayAdapter
import android.widget.Spinner
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import masaya.release.manage_menu.data.FoodMenu
import masaya.release.manage_menu.data.getFormattedStartDate
import masaya.release.manage_menu.data.FoodMenuViewModel
import masaya.release.manage_menu.databinding.FragmentFoodmenuEditBinding
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import masaya.release.manage_menu.data.FoodMenuViewModelFactory

class FoodmenuEditFragment : Fragment() {

    private val navigationArgs: FoodmenuEditFragmentArgs by navArgs()
    private var foodID : Int = 0

    private val viewModel: FoodMenuViewModel by activityViewModels {
        FoodMenuViewModelFactory(
            (activity?.application as FoodMenuApplication).database.FoodMenuDao()
        )
    }

    private var _binding: FragmentFoodmenuEditBinding? = null
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
        _binding = FragmentFoodmenuEditBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // 修正対象のID
        foodID = navigationArgs.foodId

        // スピナー（ドロップダウンボックス）
        val spinner: Spinner = binding.foodTypeSpinner
        ArrayAdapter.createFromResource(
            requireActivity(),
            R.array.foodType_array,
            android.R.layout.simple_spinner_item
        ).also { adapter ->
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            spinner.adapter = adapter
        }

        // 画像選択
        binding.floatingImageButton.setOnClickListener {
            val intent = Intent(Intent.ACTION_OPEN_DOCUMENT).apply {
                addCategory(Intent.CATEGORY_OPENABLE)
                type = "image/*"
            }
            launcher.launch(intent)
        }

        // 一覧と同様にデータベースの変更を監視しているので、変更があれば動的に詳細データを自動更新
        // データ１行をDBから取得し画面へ表示（動的監視付き）の良いサンプル
        viewModel.retrieveFood(foodID).observe(this.viewLifecycleOwner) { food ->
            bind(food)
        }
    }

    // 画像選択 -------------------------------------------------------------------------
    private val launcher = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        Log.d("registerForActivity", result.toString())

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

    // DBのデータを画面に表示
    private fun bind(food: FoodMenu) {
        binding.foodName.setText(food.foodName)
        binding.foodPrice.setText(food.foodPrice.toInt().toString())
        setSpinnerSelection(binding.foodTypeSpinner, food.foodType)
        binding.winterOnly.isChecked = food.WinterOnly
        binding.foodStartDate.setText(food.getFormattedStartDate())

        // 内部ストレージへ書き込んだファイルから改めて画像取得
        binding.foodImageBmpName.setText(food.bmpName)
        val loadbmp = readImgsFromFileName(food.bmpName, requireActivity())
        binding.foodimage.setImageBitmap(loadbmp)

        // ボタン
        binding.editFood.setOnClickListener { editFood() }
    }

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
        findNavController().navigateUp()
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
        _binding = null
    }
}
