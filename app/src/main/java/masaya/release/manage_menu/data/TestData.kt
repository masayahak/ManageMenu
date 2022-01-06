package masaya.release.manage_menu.data

import android.app.Activity
import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import masaya.release.manage_menu.ImageFiles.ImageFiles
import masaya.release.manage_menu.R

object TestData : Activity() {

    fun loadTestData(context: Context,  viewModel: FoodMenuViewModel) {

        // 現在のデータをすべて削除
        viewModel.deleteAll()

        viewModel.addNewFood (
            "スコーン",
            "180",
            "testimage_01.jpg",
            "2022/01/01",
            "洋食",
            false
        )

        viewModel.addNewFood (
            "ガトーショコラ",
            "760",
            "testimage_02.jpg",
            "2022/01/02",
            "洋食",
            false
        )

        viewModel.addNewFood (
            "冬瓜汁セット",
            "780",
            "testimage_03.jpg",
            "2022/01/03",
            "和食",
            false
        )

        viewModel.addNewFood (
            "豚バラ軟骨定食",
            "860",
            "testimage_04.jpg",
            "2022/01/04",
            "和食",
            true
        )

        viewModel.addNewFood (
            "アジの開き定食",
            "680",
            "testimage_05.jpg",
            "2022/01/05",
            "和食",
            false
        )

        viewModel.addNewFood (
            "鶏モツ煮定食",
            "690",
            "testimage_06.jpg",
            "2022/01/06",
            "和食",
            false
        )

        viewModel.addNewFood (
            "ステーキ定食",
            "1240",
            "testimage_07.jpg",
            "2022/01/07",
            "洋食",
            false
        )

        viewModel.addNewFood (
            "牡蠣のソテー定食",
            "1080",
            "testimage_08.jpg",
            "2022/01/08",
            "洋食",
            true
        )

        viewModel.addNewFood (
            "気まぐれ朝食セット",
            "680",
            "testimage_09.jpg",
            "2022/01/09",
            "洋食",
            false
        )

        viewModel.addNewFood (
            "酸辣湯豆腐セット",
            "680",
            "testimage_10.jpg",
            "2022/01/10",
            "中華",
            true
        )

        viewModel.addNewFood (
            "鰯の梅煮定食",
            "780",
            "testimage_11.jpg",
            "2022/01/11",
            "和食",
            false
        )

        viewModel.addNewFood (
            "麻婆豆腐定食",
            "780",
            "testimage_12.jpg",
            "2022/01/12",
            "中華",
            false
        )

        viewModel.addNewFood (
            "レバニラ定食",
            "780",
            "testimage_13.jpg",
            "2022/01/13",
            "中華",
            false
        )

        viewModel.addNewFood (
            "さばの味噌煮定食",
            "720",
            "testimage_14.jpg",
            "2022/01/14",
            "和食",
            false
        )

        viewModel.addNewFood (
            "さんま定食",
            "790",
            "testimage_15.jpg",
            "2022/01/15",
            "和食",
            false
        )

        // 画像ファイルの生成
        val bmp1 : Bitmap = BitmapFactory.decodeResource(context.resources, R.drawable.testimage_01)
        ImageFiles.saveImgsFromBmp(context, bmp1, "testimage_01.jpg")

        val bmp2 : Bitmap = BitmapFactory.decodeResource(context.resources, R.drawable.testimage_02)
        ImageFiles.saveImgsFromBmp(context, bmp2, "testimage_02.jpg")

        val bmp3 : Bitmap = BitmapFactory.decodeResource(context.resources, R.drawable.testimage_03)
        ImageFiles.saveImgsFromBmp(context, bmp3, "testimage_03.jpg")

        val bmp4 : Bitmap = BitmapFactory.decodeResource(context.resources, R.drawable.testimage_04)
        ImageFiles.saveImgsFromBmp(context, bmp4, "testimage_04.jpg")

        val bmp5 : Bitmap = BitmapFactory.decodeResource(context.resources, R.drawable.testimage_05)
        ImageFiles.saveImgsFromBmp(context, bmp5, "testimage_05.jpg")

        val bmp6 : Bitmap = BitmapFactory.decodeResource(context.resources, R.drawable.testimage_06)
        ImageFiles.saveImgsFromBmp(context, bmp6, "testimage_06.jpg")

        val bmp7 : Bitmap = BitmapFactory.decodeResource(context.resources, R.drawable.testimage_07)
        ImageFiles.saveImgsFromBmp(context, bmp7, "testimage_07.jpg")

        val bmp8 : Bitmap = BitmapFactory.decodeResource(context.resources, R.drawable.testimage_08)
        ImageFiles.saveImgsFromBmp(context, bmp8, "testimage_08.jpg")

        val bmp9 : Bitmap = BitmapFactory.decodeResource(context.resources, R.drawable.testimage_09)
        ImageFiles.saveImgsFromBmp(context, bmp9, "testimage_09.jpg")

        val bmp10 : Bitmap = BitmapFactory.decodeResource(context.resources, R.drawable.testimage_10)
        ImageFiles.saveImgsFromBmp(context, bmp10, "testimage_10.jpg")

        val bmp11 : Bitmap = BitmapFactory.decodeResource(context.resources, R.drawable.testimage_11)
        ImageFiles.saveImgsFromBmp(context, bmp11, "testimage_11.jpg")

        val bmp12 : Bitmap = BitmapFactory.decodeResource(context.resources, R.drawable.testimage_12)
        ImageFiles.saveImgsFromBmp(context, bmp12, "testimage_12.jpg")

        val bmp13 : Bitmap = BitmapFactory.decodeResource(context.resources, R.drawable.testimage_13)
        ImageFiles.saveImgsFromBmp(context, bmp13, "testimage_13.jpg")

        val bmp14 : Bitmap = BitmapFactory.decodeResource(context.resources, R.drawable.testimage_14)
        ImageFiles.saveImgsFromBmp(context, bmp14, "testimage_14.jpg")

        val bmp15 : Bitmap = BitmapFactory.decodeResource(context.resources, R.drawable.testimage_15)
        ImageFiles.saveImgsFromBmp(context, bmp15, "testimage_15.jpg")
    }




}