package `in`.v89bhp.obdscanner.ui.about

import android.graphics.Bitmap
import android.graphics.Canvas

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.core.content.res.ResourcesCompat
import `in`.v89bhp.obdscanner.R

@Composable
fun About(modifier: Modifier = Modifier) {

    val launcherIconBitmap = ResourcesCompat.getDrawable(
        LocalContext.current.resources,
        R.mipmap.ic_launcher, LocalContext.current.theme
    )?.let { drawable ->
        val bitmap = Bitmap.createBitmap(
            drawable.intrinsicWidth, drawable.intrinsicHeight,
            Bitmap.Config.ARGB_8888
        )
        val canvas = Canvas(bitmap)
        drawable.setBounds(0, 0, canvas.width, canvas.height)
        drawable.draw(canvas)
        bitmap
    }

    Column(modifier = modifier.fillMaxSize(),
    horizontalAlignment = Alignment.CenterHorizontally) {
        Image(bitmap = launcherIconBitmap!!.asImageBitmap(), contentDescription = "89 bhp",
        modifier = Modifier.size(110.dp))

    }
}