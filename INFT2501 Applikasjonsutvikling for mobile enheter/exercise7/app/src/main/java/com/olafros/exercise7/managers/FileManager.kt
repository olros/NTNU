package com.olafros.exercise7.managers

import android.content.Context
import java.io.*

/**
 * Just contains basic code snippets relevant for reading from/to different files
 */
class FileManager(private val context: Context) {

    private val filename: String = "movies.txt"

    private var dir: File = context.filesDir
    private var file: File = File(dir, filename)

    private var externalDir: File? = context.getExternalFilesDir(null)
    private var externalFile = File(externalDir, filename)


    fun write(str: String) {
        PrintWriter(file).use { writer ->
            writer.println(str)
        }
    }

    fun readLine(): String? {
        BufferedReader(FileReader(file)).use { reader ->
            return reader.readLine()
        }
    }

    /**
     * Open file: *res/raw/id.txt*
     *
     * @param fileId R.raw.filename
     */
    fun readFileFromResFolder(fileId: Int): String {
        val content = StringBuffer("")
        try {
            val inputStream: InputStream = context.resources.openRawResource(fileId)
            BufferedReader(InputStreamReader(inputStream)).use { reader ->
                var line = reader.readLine()
                while (line != null) {
                    content.append(line)
                    content.append("\n")
                    line = reader.readLine()
                }

            }
        } catch (e: IOException) {
            e.printStackTrace()
        }
        return content.toString()
    }
}
