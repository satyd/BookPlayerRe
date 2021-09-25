package com.levp.bookplayer

import android.app.Application
import android.content.Context
import android.graphics.Bitmap
import android.renderscript.Allocation
import android.renderscript.Element
import android.renderscript.RenderScript
import android.renderscript.ScriptIntrinsicBlur
import android.widget.Toast
import kotlin.math.roundToInt


fun blur(context: Context?, image: Bitmap): Bitmap? {
    val BITMAP_SCALE = 0.4f;
    val BLUR_RADIUS = 17.5f;

    val width = (image.width * BITMAP_SCALE).roundToInt()
    val height = (image.height * BITMAP_SCALE).roundToInt()
    val inputBitmap = Bitmap.createScaledBitmap(image, width, height, false)
    val outputBitmap = Bitmap.createBitmap(inputBitmap)
    val rs = RenderScript.create(context)
    val theIntrinsic = ScriptIntrinsicBlur.create(rs, Element.U8_4(rs))
    val tmpIn = Allocation.createFromBitmap(rs, inputBitmap)
    val tmpOut = Allocation.createFromBitmap(rs, outputBitmap)
    theIntrinsic.setRadius(BLUR_RADIUS)
    theIntrinsic.setInput(tmpIn)
    theIntrinsic.forEach(tmpOut)
    tmpOut.copyTo(outputBitmap)
    return outputBitmap
}

fun shortToast(applicationContext : Application, text:String){
    Toast.makeText(applicationContext, text, Toast.LENGTH_SHORT).show()
}