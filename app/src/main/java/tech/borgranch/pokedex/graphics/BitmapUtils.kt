package tech.borgranch.pokedex.graphics

import android.content.Context
import android.graphics.Bitmap
import java.io.File
import java.io.FileOutputStream
import java.util.UUID

object BitmapUtils {

    fun saveBitmap(bitmap: Bitmap, context: Context): String {
        val file = File(
            context.filesDir,
            "pokedex_${UUID.randomUUID().toString().replace("-","").lowercase()}.png"
        )
        val out = FileOutputStream(file)
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, out)
        out.flush()
        out.close()
        return file.absolutePath
    }
}
