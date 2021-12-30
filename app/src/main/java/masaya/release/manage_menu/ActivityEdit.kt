package masaya.release.manage_menu

import android.os.Bundle
import android.util.Log
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.NavigationUI
import masaya.release.manage_menu.data.UserInputViewModel

class ActivityEdit : AppCompatActivity(R.layout.activity_edit),
    FragmentEdit.FromActivityEditToListener {

    private val userInputViewModel: UserInputViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // 修正対象のメニューのIDを引数で取得
        userInputViewModel.setfoodId(-1)
        val b = intent.extras
        if (b != null) {
            val foodId = b.getInt("foodId")
            userInputViewModel.setfoodId(foodId)
        }

        val navHostFragment = supportFragmentManager
            .findFragmentById(R.id.nav_host_fragment_edit) as NavHostFragment
        val navController = navHostFragment.navController
        NavigationUI.setupActionBarWithNavController(this, navController)
    }

    override fun onSupportNavigateUp(): Boolean {

        // 現在表示しているフラグメントを判定し、
        //  1) 編集画面 → 終了しリスト画面へ
        //  2) 拡大画像 → 編集画面
        val navHostFragment = supportFragmentManager
            .findFragmentById(R.id.nav_host_fragment_edit) as NavHostFragment
        val currentFragment = navHostFragment.childFragmentManager.fragments[0]
        val navController = navHostFragment.navController

        when (currentFragment) {
            is FragmentEdit -> {
                toListActivity()
            }
            is FragmentImage -> {
                val action = FragmentImageDirections.actionFragmentImageToFragmentEdit()
                navController.navigate(action)
                overridePendingTransition(R.anim.slide_in, R.anim.fade_out)
            }
            else -> {
                Log.i("TEST--------------", "ELSE   $currentFragment")
            }
        }

        return true
    }

    override fun toListActivity() {
        finish()
        overridePendingTransition(R.anim.slide_in, R.anim.fade_out)
    }

}