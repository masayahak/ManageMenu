package masaya.release.manage_menu.ImageFiles

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

}