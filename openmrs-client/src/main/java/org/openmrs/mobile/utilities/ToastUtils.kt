package org.openmrs.mobile.utilities


import android.content.Context
import android.graphics.Bitmap
import android.view.LayoutInflater
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import org.openmrs.mobile.R
import org.openmrs.mobile.application.OpenMRS
import org.openmrs.mobile.utilities.ImageUtils.decodeBitmapFromResource
import java.util.*


object ToastUtil {
    private val logger = OpenMRS.getInstance().openMRSLogger
    private val toastQueue: MutableList<ToastThread> = ArrayList()
    private var isAppVisible = true

    @JvmStatic
    fun setAppVisible(appVisible: Boolean) {
        isAppVisible = appVisible
    }

    @JvmStatic
    fun notifyLong(message: String) {
        showToast(OpenMRS.getInstance(), ToastType.NOTICE, message, Toast.LENGTH_LONG)
    }

    @JvmStatic
    fun notify(message: String) {
        showToast(OpenMRS.getInstance(), ToastType.NOTICE, message, Toast.LENGTH_SHORT)
    }

    @JvmStatic
    fun success(message: String) {
        showToast(OpenMRS.getInstance(), ToastType.SUCCESS, message, Toast.LENGTH_SHORT)
    }

    @JvmStatic
    fun error(message: String) {
        showToast(OpenMRS.getInstance(), ToastType.ERROR, message, Toast.LENGTH_SHORT)
    }

    @JvmStatic
    fun showShortToast(context: Context, type: ToastType, textId: Int) {
        showToast(context, type, context.resources.getString(textId), Toast.LENGTH_SHORT)
    }

    @JvmStatic
    fun showLongToast(context: Context, type: ToastType, textId: Int) {
        showToast(context, type, context.resources.getString(textId), Toast.LENGTH_LONG)
    }

    @JvmStatic
    fun showShortToast(context: Context, type: ToastType, text: String) {
        showToast(context, type, text, Toast.LENGTH_SHORT)
    }

    @JvmStatic
    fun showLongToast(context: Context, type: ToastType, text: String) {
        showToast(context, type, text, Toast.LENGTH_LONG)
    }

    @JvmStatic
    private fun showToast(context: Context, type: ToastType,
                          text: String, duration: Int) {
        if (!isAppVisible) return
        val inflater = context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        val toastRoot = inflater.inflate(R.layout.toast, null)
        val bitmap: Bitmap
        val toastImage = toastRoot.findViewById<ImageView>(R.id.toastImage)
        val toastText = toastRoot.findViewById<TextView>(R.id.toastText)
        toastText.text = text
        bitmap = decodeBitmapFromResource(
                context.resources,
                getImageResId(type),
                toastImage.layoutParams.width,
                toastImage.layoutParams.height)
        toastImage.setImageBitmap(bitmap)
        logger.d("Decode bitmap: $bitmap")
        val toast = Toast(context)
        toast.view = toastRoot
        toast.duration = duration
        toast.show()
        val thread = ToastThread(duration)
        if (toastQueue.size == 0) {
            thread.start()
        }
        toastQueue.add(thread)
    }

    @JvmStatic
    private fun getImageResId(type: ToastType): Int {
        var toastTypeImageId = 0
        when (type) {
            ToastType.ERROR -> toastTypeImageId = R.drawable.toast_error
            ToastType.NOTICE -> toastTypeImageId = R.drawable.toast_notice
            ToastType.SUCCESS -> toastTypeImageId = R.drawable.toast_success
            ToastType.WARNING -> toastTypeImageId = R.drawable.toast_warning
            else -> {
                //do nothing
            }
        }
        return toastTypeImageId
    }

    enum class ToastType {
        ERROR, NOTICE, SUCCESS, WARNING
    }

    private class ToastThread(private val mDuration: Int) : Thread() {
        override fun run() {
            try {
                if (mDuration == Toast.LENGTH_SHORT) {
                    sleep(2000)
                } else {
                    sleep(3500)
                }
                toastQueue.removeAt(0)
                if (toastQueue.size > 0) {
                    toastQueue[0].run()
                }
            } catch (e: Exception) {
                logger.e(e.toString())
            }
        }

    }
}
