package com.jarkendar.totabs

import android.app.Activity
import android.app.Dialog
import android.os.Environment
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.ListView
import android.widget.TextView
import java.io.File
import java.io.FileFilter
import java.util.*


/**
 * This is solution from Roger Keays, source: https://rogerkeays.com/simple-android-file-chooser, translate to kotlin by me
 */
public class FileChooser(private val activity: Activity) {

    private var list: ListView = ListView(activity)
    private var dialog: Dialog = Dialog(activity)
    private var currentPath: File? = null

    private var extension: String? = null
    private var fileListener: FileSelectedListener? = null

    init {
        list.onItemClickListener = AdapterView.OnItemClickListener { parent, view, which, id ->
            val fileChosen = list.getItemAtPosition(which) as String
            val chosenFile = getChosenFile(fileChosen)
            if (chosenFile.isDirectory) {
                refresh(chosenFile)
            } else {
                fileListener?.fileSelected(chosenFile)
                dialog.dismiss()
            }
        }
        dialog.setContentView(list)
        dialog.window.setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.MATCH_PARENT)
        refresh(Environment.getExternalStorageDirectory())
    }

    public fun setExtension(extension: String) {
        this.extension = extension.toLowerCase()
    }

    public fun setFileListener(fileListener: FileSelectedListener): FileChooser {
        this.fileListener = fileListener
        return this
    }

    public fun showDialog() {
        dialog.show()
    }

    /**
     * Sort, filter and display the files for the given path.
     */
    private fun refresh(path: File) {
        currentPath = path
        if (path.exists()) {
            val dirs: Array<File> = path.listFiles(FileFilter {
                return@FileFilter (it.isDirectory && it.canRead())
            })
            val files: Array<File> = path.listFiles(FileFilter {
                if (!it.isDirectory) {
                    if (!it.canRead()) {
                        return@FileFilter false
                    } else if (extension == null) {
                        return@FileFilter true
                    } else {
                        return@FileFilter it.name.toLowerCase().endsWith(extension!!)
                    }
                } else {
                    return@FileFilter false
                }
            })

            var i = 0
            val fileList: Array<String>
            if (path.parentFile == null) {
                fileList = Array(dirs.size + files.size) { "" }
            } else {
                fileList = Array(dirs.size + files.size + 1) { "" }
                fileList[i++] = PARENT_DIR
            }
            Arrays.sort(dirs)
            Arrays.sort(files)
            for (dir in dirs) {
                fileList[i++] = dir.name
            }
            for (file in files) {
                fileList[i++] = file.name
            }

            dialog.setTitle(currentPath!!.path)
            list.adapter = object : ArrayAdapter<String>(activity, android.R.layout.simple_list_item_1, fileList) {
                override fun getView(pos: Int, view: View?, parent: ViewGroup): View {
                    var convertView = view
                    convertView = super.getView(pos, convertView, parent)
                    (convertView as TextView).setSingleLine(true)
                    return convertView
                }
            }
        }
    }

    /**
     * Convert a relative filename into an actual File object.
     */
    private fun getChosenFile(fileChosen: String): File {
        return if (fileChosen == PARENT_DIR) {
            currentPath!!.parentFile
        } else {
            File(currentPath, fileChosen)
        }
    }


    // file selection event handling
    public interface FileSelectedListener {
        fun fileSelected(file: File)
    }

    companion object {
        private val PARENT_DIR: String = ".."
    }
}