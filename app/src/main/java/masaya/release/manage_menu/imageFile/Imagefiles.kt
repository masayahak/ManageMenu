package masaya.release.manage_menu.imageFile

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.util.Log
import android.widget.ImageView
import androidx.databinding.BindingAdapter
import masaya.release.manage_menu.R
import masaya.release.manage_menu.imageFile.ImageFiles.readSmallImgsFromFileName
import java.io.BufferedInputStream
import java.io.ByteArrayOutputStream
import java.io.FileOutputStream
import java.io.IOException


// 一覧の表示時に画像を1行ずつ読み込んで表示すると処理が重たくなる。
// DBから読み込んだ上を一覧に表示するのとは別に、
// 個別のスレッドで（コルーチンで）画像を読み込む
@BindingAdapter(value = ["setImageFile"])
fun ImageView.bindImageFile(bmpName: String?) {
    if (bmpName != null && bmpName.isNotBlank()) {
        val bmp = readSmallImgsFromFileName(this.context, bmpName)
        this.setImageBitmap(bmp)
    } else {
        this.setImageResource(R.drawable.no_image_mini)
    }
}

object ImageFiles {

    // ユーザーが選択した画像を内部ストレージに保管する
    fun saveImgsFromBmp(context: Context, bmp: Bitmap, outputFileName:String) {
        try {
            val byteArrOutputStream = ByteArrayOutputStream()
            val fileOutputStream: FileOutputStream = context.openFileOutput(outputFileName, Context.MODE_PRIVATE)
            bmp.compress(Bitmap.CompressFormat.JPEG, 100, byteArrOutputStream)
            fileOutputStream.write(byteArrOutputStream.toByteArray())
            fileOutputStream.close()
        }
        catch (e:Exception){
            e.printStackTrace()
        }
    }

    // 内部ストレージ内の画像を取得する
    fun readImgsFromFileName(context: Context, fileName:String): Bitmap? {
        try {
            val bufferedInputStream = BufferedInputStream(context.openFileInput(fileName))
            val bmp = BitmapFactory.decodeStream(bufferedInputStream)
            bufferedInputStream.close()
            return bmp
        }
        catch (e: IOException){
            e.printStackTrace()
            return null
        }
    }

    // 内部ストレージ内の画像を「一覧用に縮小して」取得する
    fun readSmallImgsFromFileName(context: Context, fileName:String): Bitmap? {
        try {

            // どれぐらい縮小するのか元画像のサイズを取得し判定する
            val bufferedInputStreamForScale = BufferedInputStream(context.openFileInput(fileName))
            val imageOptionsForScale = BitmapFactory.Options()
            imageOptionsForScale.inJustDecodeBounds  = true
            BitmapFactory.decodeStream(bufferedInputStreamForScale, null, imageOptionsForScale)
            bufferedInputStreamForScale.close()

            val originWidth = imageOptionsForScale.outWidth
            val originHeight = imageOptionsForScale.outHeight
            val originMax =
                if (originWidth>originHeight)
                    originWidth
                else originHeight
            val MAX_SIZE = 80

            // 一応Double型で正確に計算してるけど、本当はIntで十分
            val magnification : Double = (originMax.toDouble() / MAX_SIZE.toDouble())

            // 倍率magnificationに対して、2の乗数で最も近くて小さいものを探す
            // 例えば倍率が100なら欲しいものは64になる。
            var ration = 1
            while (ration < magnification) { ration = ration * 2}
            if (ration > 1) ration = ration / 2

            // 縮小率がわかったので、今度は縮小して画像を読み込む
            val bufferedInputStream = BufferedInputStream(context.openFileInput(fileName))
            val imageOptions = BitmapFactory.Options()
            imageOptions.inSampleSize = ration  // 縦・横 それぞれ ration分の1に縮小している
            imageOptions.inPreferredConfig = Bitmap.Config.RGB_565
            val bmp = BitmapFactory.decodeStream(bufferedInputStream, null, imageOptions)
            bufferedInputStream.close()

            return bmp
        }
        catch (e: IOException){
            e.printStackTrace()
            return null
        }
    }

}