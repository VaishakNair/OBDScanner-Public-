package `in`.v89bhp.obdscanner.ui.about

import android.graphics.Bitmap
import android.graphics.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.core.content.res.ResourcesCompat
import `in`.v89bhp.obdscanner.BuildConfig
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

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(8.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Image(
            bitmap = launcherIconBitmap!!.asImageBitmap(), contentDescription = "89 bhp",
            modifier = Modifier.size(110.dp)
        )

        Text(
            text = stringResource(R.string.app_name),
            style = MaterialTheme.typography.headlineSmall
        )

        Text(
            text = stringResource(R.string.version_name, BuildConfig.VERSION_NAME),
            style = MaterialTheme.typography.bodyLarge
        )

        Text(
            text = stringResource(R.string.connect_with_me_message),
            style = MaterialTheme.typography.bodyLarge,
            textAlign = TextAlign.Center
        )



        Text(
            text = buildAnnotatedString {
                val text = stringResource(R.string.contact_message)
                append(text)

            },
            style = MaterialTheme.typography.bodyLarge,
            textAlign = TextAlign.Center,
            textDecoration = TextDecoration.Underline
        )


    }
}