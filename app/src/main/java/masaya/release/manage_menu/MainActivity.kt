package masaya.release.manage_menu

import android.app.Application
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.NavigationUI
import masaya.release.manage_menu.data.MyRoomDatabase

// アプリケーションクラス名「MenuApplication」は、
// マニュフェストのandroid:name=".MenuApplication"と合わせる必要がある
class FoodMenuApplication : Application() {
    // アプリケーション全体でデータベースインスタンスを１つだけ持つため。
    val database: MyRoomDatabase by lazy { MyRoomDatabase.getDatabase(this) }
}

class MainActivity : AppCompatActivity(R.layout.activity_main) {

    private lateinit var navController: NavController

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val navHostFragment = supportFragmentManager
            .findFragmentById(R.id.nav_host_fragment) as NavHostFragment
        navController = navHostFragment.navController
        NavigationUI.setupActionBarWithNavController(this, navController)
    }

    override fun onSupportNavigateUp(): Boolean {
        return navController.navigateUp() || super.onSupportNavigateUp()
    }
}