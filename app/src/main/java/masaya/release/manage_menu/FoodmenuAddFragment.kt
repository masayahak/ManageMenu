package masaya.release.manage_menu

import android.content.Context
import android.os.Bundle
import android.text.Editable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import masaya.release.manage_menu.data.FoodMenuDao
import masaya.release.manage_menu.data.FoodMenuViewModel
import masaya.release.manage_menu.data.FoodMenuViewModelFactory
import masaya.release.manage_menu.databinding.FragmentFoodmenuAddBinding

class FoodmenuAddFragment : Fragment() {

    private val viewModel: FoodMenuViewModel by activityViewModels {
        FoodMenuViewModelFactory(
            (activity?.application as FoodMenuApplication).database.FoodMenuDao()
        )
    }

    private var _binding: FragmentFoodmenuAddBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentFoodmenuAddBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // 追加ボタン
        binding.saveAction.setOnClickListener {addNewFood() }
    }

    // ───────────────────────────────────────────────────────────
    // add（追加）
    // ───────────────────────────────────────────────────────────
    private fun addNewFood() {
        // 画面入力チェック
        val inputFoodName : Editable? = binding.foodName.text
        val inputFoodPrice : Editable? = binding.foodPrice.text
        if (!checkInputFood(inputFoodName, inputFoodPrice)){
            showErrorMessage()
            return
        }

        // DB更新
        val ret : Boolean = viewModel.addNewFood(
            inputFoodName.toString(),
            inputFoodPrice.toString()
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

// 画面から入力された内容をチェック
// 修正時もこの関数を利用しているのでパブリック
fun checkInputFood(inputFoodName : Editable?, inputFoodPrice : Editable?) : Boolean{
    if (inputFoodName.isNullOrEmpty()){
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

