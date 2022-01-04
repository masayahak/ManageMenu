package masaya.release.manage_menu

import android.app.Application
import masaya.release.manage_menu.data.MyRoomDatabase

// アプリケーションクラス名「FoodMenuApplication」は、
// マニュフェストのandroid:name=".FoodMenuApplication"と合わせる必要がある
class FoodMenuApplication : Application() {
    // アプリケーション全体でデータベースインスタンスを１つだけ持つため。
    val database: MyRoomDatabase by lazy { MyRoomDatabase.getDatabase(this) }
}
