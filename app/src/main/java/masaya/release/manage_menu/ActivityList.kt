package masaya.release.manage_menu

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle

class ActivityList : AppCompatActivity(R.layout.activity_list),
    FragmentList.FromActivityListToListener {

    // 追加モードで入力画面へ
    override fun toAddFoodmenu() {
        val intent = Intent(applicationContext, ActivityEdit::class.java)

        val b = Bundle()
        b.putString("MODE", "ADD")
        b.putInt("foodId", -1)
        intent.putExtras(b)

        startActivity(intent)
        overridePendingTransition(R.anim.slide_in, R.anim.fade_out)
    }

    // 修正モードで入力画面へ
    override fun toEditFoodmenu(foodId: Int) {
        val intent = Intent(applicationContext, ActivityEdit::class.java)

        val b = Bundle()
        b.putString("MODE", "EDIT")
        b.putInt("foodId", foodId)
        intent.putExtras(b)

        startActivity(intent)
        overridePendingTransition(R.anim.slide_in, R.anim.fade_out)
    }
}