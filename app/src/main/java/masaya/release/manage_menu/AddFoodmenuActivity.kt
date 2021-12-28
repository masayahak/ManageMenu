package masaya.release.manage_menu

import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.NavigationUI
import androidx.navigation.ui.setupActionBarWithNavController
import masaya.release.manage_menu.databinding.ActivityAddFoodmenuBinding

class AddFoodmenuActivity : AppCompatActivity(R.layout.activity_add_foodmenu) {

    private lateinit var appBarConfiguration: AppBarConfiguration
    private lateinit var binding : ActivityAddFoodmenuBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // TODO 第２アクティビティーにアクションバーをうまく表示できない。
        // 表示しようとするとアクションバー以外が全く描画できなくなる


/*
        binding = ActivityAddFoodmenuBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setSupportActionBar(binding.toolbar)

        val navHostFragment = supportFragmentManager
            .findFragmentById(R.id.nav_host_fragment_add) as NavHostFragment
        val navController = navHostFragment.navController

        appBarConfiguration = AppBarConfiguration(navController.graph)
        setupActionBarWithNavController(navController, appBarConfiguration)

        NavigationUI.setupActionBarWithNavController(this, navController)
*/
        val navHostFragment = supportFragmentManager
            .findFragmentById(R.id.nav_host_fragment_add) as NavHostFragment
        val navController = navHostFragment.navController
        // NavigationUI.setupActionBarWithNavController(this, navController)
    }

    override fun onSupportNavigateUp(): Boolean {
        Log.i("TEST--------------", "onSupportNavigateUp")
        finish()
        val x = super.onNavigateUp()
        overridePendingTransition(R.anim.slide_in, R.anim.fade_out)
        return x
    }

}
