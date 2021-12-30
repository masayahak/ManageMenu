package masaya.release.manage_menu

import android.app.Application
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.NavigationUI
import masaya.release.manage_menu.data.MyRoomDatabase

// アプリケーションクラス名「FoodMenuApplication」は、
// マニュフェストのandroid:name=".FoodMenuApplication"と合わせる必要がある
class FoodMenuApplication : Application() {
    // アプリケーション全体でデータベースインスタンスを１つだけ持つため。
    val database: MyRoomDatabase by lazy { MyRoomDatabase.getDatabase(this) }
}

class ActivityList : AppCompatActivity(R.layout.activity_list),
    FragmentList.FromActivityListToListener {

    private lateinit var navController: NavController

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val navHostFragment = supportFragmentManager
            .findFragmentById(R.id.nav_host_fragment_list) as NavHostFragment
        navController = navHostFragment.navController
        NavigationUI.setupActionBarWithNavController(this, navController)
    }

    override fun onSupportNavigateUp(): Boolean {
        return navController.navigateUp() || super.onSupportNavigateUp()
    }

    override fun toAddFoodmenu() {
        val intent = Intent(applicationContext, ActivityAdd::class.java)
        startActivity(intent)
        overridePendingTransition(R.anim.slide_in, R.anim.fade_out)
    }

    override fun toEditFoodmenu(foodId: Int) {
        val intent = Intent(applicationContext, ActivityEdit::class.java)

        val b = Bundle()
        b.putInt("foodId", foodId)
        intent.putExtras(b)

        startActivity(intent)
        overridePendingTransition(R.anim.slide_in, R.anim.fade_out)
    }
}