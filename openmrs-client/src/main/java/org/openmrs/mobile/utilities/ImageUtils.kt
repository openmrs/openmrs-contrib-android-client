package org.openmrs.mobile.utilities

import android.content.Context
import android.content.Intent
import android.content.res.ColorStateList
import android.content.res.Resources
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Matrix
import android.widget.ImageView
import androidx.core.content.ContextCompat
import androidx.core.widget.ImageViewCompat
import org.openmrs.mobile.activities.patientdashboard.details.PatientPhotoActivity
import java.io.ByteArrayOutputStream

object ImageUtils{
        fun decodeBitmapFromResource(res: Resources?, resId: Int, reqWidth: Int, reqHeight: Int): Bitmap { // First decode with inJustDecodeBounds=true to check dimensions
            val options = BitmapFactory.Options()
            options.inJustDecodeBounds = true
            BitmapFactory.decodeResource(res, resId, options)
            // Calculate inSampleSize
            options.inSampleSize = calculateInSampleSize(options, reqWidth, reqHeight)
            // Decode bitmap with inSampleSize set
            options.inJustDecodeBounds = false
            return BitmapFactory.decodeResource(res, resId, options)
        }

        fun calculateInSampleSize(
                options: BitmapFactory.Options, reqWidth: Int, reqHeight: Int): Int { // Raw height and width of image
            val height = options.outHeight
            val width = options.outWidth
            var inSampleSize = 1
            if (height > reqHeight || width > reqWidth) { // Calculate ratios of height and width to requested height and width
                val heightRatio = Math.round(height.toFloat() / reqHeight.toFloat())
                val widthRatio = Math.round(width.toFloat() / reqWidth.toFloat())
                // Choose the smallest ratio as inSampleSize value, this will guarantee
// a final image with both dimensions larger than or equal to the
// requested height and width.
                inSampleSize = if (heightRatio < widthRatio) heightRatio else widthRatio
            }
            return inSampleSize
        }

        fun resizePhoto(photo: Bitmap): Bitmap {
            val HEIGHT = 500.0
            val WIDTH = 500.0
            val height = photo.height
            val width = photo.width
            val aspectRatio = Math.min(HEIGHT / height, WIDTH / width)
            return if (0 < aspectRatio && aspectRatio < 1) {
                Bitmap.createScaledBitmap(photo, (aspectRatio * width).toInt(), (aspectRatio * height).toInt(), true)
            } else photo
        }

        fun rotateImage(source: Bitmap, angle: Float): Bitmap {
            val matrix = Matrix()
            matrix.postRotate(angle)
            return Bitmap.createBitmap(source, 0, 0, source.width, source.height, matrix, true)
        }

        fun showPatientPhoto(context: Context, photo: Bitmap, patientName: String?) {
            val intent = Intent(context, PatientPhotoActivity::class.java)
            val byteArrayOutputStream = ByteArrayOutputStream()
            photo.compress(Bitmap.CompressFormat.PNG, 0, byteArrayOutputStream)
            intent.putExtra("photo", byteArrayOutputStream.toByteArray())
            intent.putExtra("name", patientName)
            context.startActivity(intent)
        }

        fun changeImageViewTint(context: Context?, imageView: ImageView?, color: Int) {
            ImageViewCompat.setImageTintList(imageView!!, ColorStateList.valueOf(ContextCompat.getColor(context!!, color)))
        }

    }
