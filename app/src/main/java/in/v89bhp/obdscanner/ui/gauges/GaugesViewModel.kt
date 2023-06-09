package `in`.v89bhp.obdscanner.ui.gauges


import android.app.Application
import android.content.Context
import android.util.Log
import android.view.ViewGroup
import android.widget.FrameLayout
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.AndroidViewModel
import `in`.v89bhp.obdscanner.BuildConfig.APP_NAME
import `in`.v89bhp.obdscanner.obdparameters.BaseParameter
import `in`.v89bhp.obdscanner.obdparameters.ParameterHolder.addParameter
import `in`.v89bhp.obdscanner.obdparameters.ParameterHolder.parameterList
import org.json.JSONArray
import org.json.JSONObject
import java.io.FileNotFoundException
import java.nio.charset.Charset

class GaugesViewModel(application: Application) : AndroidViewModel(application) {


    /**
     *  Load gauge(s) from previous session (if any)
     */
    fun loadGauges(context: FragmentActivity) {
        if (parameterList.isEmpty()) {
            val fip = try {
                getApplication<Application>()?.openFileInput("Parameters.json")
            } catch (ex: FileNotFoundException) {
                Log.i(APP_NAME, ex.message ?: ex.toString())
                null
            }

            fip?.use {
                val paramsJsonArray =
                    JSONObject(
                        String(
                            it.readBytes(),
                            Charset.forName("utf-8")
                        )
                    ).getJSONArray("params")
                if (paramsJsonArray.length() > 0) {
                    var i = 0
                    do {
                        val paramJSONObject = paramsJsonArray.getJSONObject(i)
                        addParameter(
                            (Class.forName(paramJSONObject.getString("className")).constructors[0].newInstance(
                                context,
                                this@GaugesViewModel,
                                paramJSONObject.getString("gaugeType")
                            ) as BaseParameter).apply {

                                maxAlertValue = paramJSONObject.getDouble("maxAlertValue").toFloat()
                                audioAlert = paramJSONObject.getBoolean("audioAlert")
                                // Remove the CENTER layout gravity used for adding new gauges:
                                gaugeFrame.layoutParams = FrameLayout.LayoutParams(
                                    ViewGroup.LayoutParams.WRAP_CONTENT,
                                    ViewGroup.LayoutParams.WRAP_CONTENT
                                )
                                gaugeFrame.x = paramJSONObject.getDouble("x").toFloat()
                                gaugeFrame.y = paramJSONObject.getDouble("y").toFloat()

                                paramJSONObject.getDouble("scaleFactor").toFloat().also {
                                    gaugeFrame.scaleX = it
                                    gaugeFrame.scaleY = it
                                }
                            }
                        )
                        i++
                    } while (i < paramsJsonArray.length())
                }
            }
        }
    }

    fun storeGauges() {
        val paramsJsonArray = JSONArray()
        for (parameter in parameterList) {
            val paramJSONObject = JSONObject().apply {
                put("className", parameter::class.java.name)
                put("gaugeType", parameter.gauge::class.java.name)
                put("maxAlertValue", parameter.maxAlertValue)
                put("audioAlert", parameter.audioAlert)

                put("x", parameter.gaugeFrame.x)
                put("y", parameter.gaugeFrame.y)

                put("scaleFactor", parameter.gaugeFrame.scaleX)

            }
            paramsJsonArray.put(paramJSONObject)
        }

        val paramsJson = JSONObject()
        paramsJson.put("params", paramsJsonArray)
        getApplication<Application>()?.openFileOutput("Parameters.json", Context.MODE_PRIVATE).use {
            it?.write(paramsJson.toString().toByteArray(Charset.forName("utf-8")))
        }
    }
}