package edu.bu.taskmaster.utils

import android.app.Activity
import android.app.Dialog
import android.content.Context
import android.view.ViewGroup
import android.view.WindowManager
import edu.bu.taskmaster.R

class Controller {

    companion object {
        private var dialogspinner: Dialog? = null

        fun dialogStart(context: Context?) {
            dialogStop()
            if (context != null && !(context as Activity).isFinishing) {
                dialogspinner = Dialog(context, R.style.progress_dialog)
                dialogspinner!!.window!!.setFlags(
                    WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE,
                    WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE
                )
                dialogspinner!!.setContentView(R.layout.progress_dialog)
                dialogspinner!!.window!!.attributes.width = ViewGroup.LayoutParams.WRAP_CONTENT
                dialogspinner!!.window!!.attributes.height = ViewGroup.LayoutParams.WRAP_CONTENT
                dialogspinner!!.setCancelable(false)
                dialogspinner!!.show()
                dialogspinner!!.window!!.decorView.systemUiVisibility =
                    context.window.decorView.systemUiVisibility
                dialogspinner!!.window?.clearFlags(WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE)
            }
        }

        fun dialogStop() {
            if (dialogspinner != null) {
                if (dialogspinner!!.isShowing) {
                    dialogspinner!!.dismiss()
                }
            }
        }

    }
}