package masaya.release.manage_menu.imageFile

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import java.io.BufferedInputStream
import java.io.ByteArrayOutputStream
import java.io.FileOutputStream
import java.io.IOException

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
        return try {
            val bufferedInputStream = BufferedInputStream(context.openFileInput(fileName))
            BitmapFactory.decodeStream(bufferedInputStream)
        }
        catch (e: IOException){
            e.printStackTrace()
            null
        }
    }

    // 内部ストレージ内の画像を「一覧用に縮小して」取得する
    fun readSmallImgsFromFileName(context: Context, fileName:String): Bitmap? {
        return try {
            val bufferedInputStream = BufferedInputStream(context.openFileInput(fileName))

            // TODO 仮で固定値で縮小しているが、画像サイズに合わせて縮小率を決定する
            val imageOptions = BitmapFactory.Options()
            imageOptions.inSampleSize = 16  // 縦・横 それぞれN分の1に縮小している
            imageOptions.inPreferredConfig = Bitmap.Config.RGB_565
            BitmapFactory.decodeStream(bufferedInputStream, null, imageOptions)
        }
        catch (e: IOException){
            e.printStackTrace()
            null
        }
    }

}