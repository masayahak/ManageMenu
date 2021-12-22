package masaya.release.manage_menu

import android.os.Bundle
import android.text.Editable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import masaya.release.manage_menu.data.FoodMenu
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

        // 一覧と同様にデータベースの変更を監視しているので、変更があれば動的に詳細データを自動更新
        // データ１行をDBから取得し画面へ表示（動的監視付き）の良いサンプル
        viewModel.retrieveFood(foodID).observe(this.viewLifecycleOwner) { food ->
            bind(food)
        }
    }

    // DBのデータを画面に表示
    private fun bind(food: FoodMenu) {
        binding.apply {
            foodName.setText(food.foodName)
            foodPrice.setText(food.foodPrice.toInt().toString())
            // ボタン
            editFood.setOnClickListener { editFood() }
        }
    }

    // ───────────────────────────────────────────────────────────
    // EDIT（変更）
    // ───────────────────────────────────────────────────────────
    private fun editFood() {
        // 画面入力チェック
        val inputFoodName : Editable? = binding.foodName.text
        val inputFoodPrice : Editable? = binding.foodPrice.text
        if (!checkInputFood(inputFoodName, inputFoodPrice)){
            showErrorMessage()
            return
        }

        // DB更新
        val ret : Boolean = viewModel.updateFood(
            foodID
            , inputFoodName.toString()
            , inputFoodPrice.toString()
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
