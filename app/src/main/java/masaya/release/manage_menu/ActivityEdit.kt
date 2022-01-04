package masaya.release.manage_menu

import android.content.Context
import android.os.Bundle
import android.view.View
import android.view.inputmethod.InputMethodManager
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.NavigationUI
import masaya.release.manage_menu.data.UserInputViewModel

class ActivityEdit : AppCompatActivity(R.layout.activity_edit),
    FragmentEdit.FromActivityEditToListener {

    private val userInputViewModel: UserInputViewModel by viewModels()

    private lateinit var navController: NavController
    private lateinit var navHostFragment: NavHostFragment

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // 一覧画面からの引数を取得
        val b = intent.extras
        if (b != null) {
            // 追加か修正か
            val mode = b.getString("MODE").toString()
            userInputViewModel.setInputMode(mode)
            // 修正対象のID
            val foodId = b.getInt("foodId")
            userInputViewModel.setfoodId(foodId)
        }

        // アクションバーに戻る矢印を表示
        navHostFragment = supportFragmentManager
            .findFragmentById(R.id.nav_host_fragment_edit) as NavHostFragment
        navController = navHostFragment.navController
        NavigationUI.setupActionBarWithNavController(this, navController)

    }

    // アクションバーの戻る矢印をクリックされたとき
    override fun onSupportNavigateUp(): Boolean {

        // 現在表示しているフラグメントを判定し、
        val currentFragment = navHostFragment.childFragmentManager.fragments[0]

        when (currentFragment) {
            is FragmentEdit -> {
                //  1) 編集画面 → 終了しリスト画面へ
                toListActivity()
            }
            is FragmentImage -> {
                //  2) 画像拡大画像 → 編集画面
                val action = FragmentImageDirections.actionFragmentImageToFragmentEdit()
                navController.navigate(action)
                overridePendingTransition(R.anim.slide_in, R.anim.fade_out)
            }
        }
        return true
    }

    // 一覧へ戻る：このアクティビティは終了
    override fun toListActivity() {
        finish()
        overridePendingTransition(R.anim.slide_in, R.anim.fade_out)
    }
}

object KeyboardUtils {
    fun hideKeyboard(focusView: View) {
        val inputMethodManager: InputMethodManager =
            focusView.context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        inputMethodManager.hideSoftInputFromWindow(
            focusView.windowToken,
            InputMethodManager.HIDE_NOT_ALWAYS
        )
    }
}