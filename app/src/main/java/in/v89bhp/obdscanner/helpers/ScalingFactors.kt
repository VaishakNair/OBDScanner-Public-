package `in`.v89bhp.obdscanner.helpers

import android.os.Handler
import android.util.Log
import `in`.v89bhp.obdscanner.BuildConfig


object ScalingFactors {
    const val PID_FOUR_F = "014F1"
    const val PID_FIVE_ZERO = "01501"



    var initialized = false

    var intakeManifoldAbsolutePressureScalingFactor: Float = 1f
    var mafScalingFactor = 0.01f

    var maxEquivalenceRatio: Float = 2.0f
    var equivalenceRatioScalingFactor: Float = 0.0000305f

    var maxOxygenSensorCurrent: Float = 128.0f
    var oxygenSensorCurrentScalingFactor: Float = 0.00390625f

    var maxOxygenSensorVoltage: Float = 8.0f
    var oxygenSensorVoltageScalingFactor: Float = 0.000122f


    fun processScalingFactorsResponse(response: String, handler: Handler): Boolean {
        when (ElmHelper.lastCommand) {
            PID_FOUR_F -> {
                if (response.contains("NODATA").not() && response.startsWith("7F").not()) {
                    initializeIntakeManifoldAbsolutePressureScalingFactor(response)
                    initializeEquivalenceRatioScalingFactor(response)
                    initializeOxygenSensorCurrentScalingFactor(response)
                    initializeOxygenSensorVoltageScalingFactor(response)
                    // TODO Add functions for processing other scaling factors obtained for PID '4F'
                }

                ElmHelper.send(handler, "${PID_FIVE_ZERO}\r")
                return false
            }

            PID_FIVE_ZERO -> {
                if (response.contains("NODATA").not() && response.startsWith("7F").not()) {
                    initializeMAFScalingFactor(response)
                    // TODO Add functions for processing other scaling factors obtained for PID '50'
                }


                // TODO Replace with call to another scaling factors generator PID and move 'initialized' boolean setting
                // and everything that follows it to its handler.
                initialized = true
                return true
            }
            else -> return false
        }
    }

    private fun initializeIntakeManifoldAbsolutePressureScalingFactor(response: String) {
        val value: Float = try {
            Integer.parseInt(response.substring(10), 16) * 1f
        }catch (ex: NumberFormatException) {
            Log.i(BuildConfig.APP_NAME, ex.message ?: ex.toString())
            0f
        } catch (ex: StringIndexOutOfBoundsException) {
            Log.i(BuildConfig.APP_NAME, ex.message ?: ex.toString())
            0f
        }
        if(value != 0f) {
            intakeManifoldAbsolutePressureScalingFactor = value / 255f // Not multiplied by 10 as it
            // has been compensated for by changing the 'unit' (x10) of 'Parameter0B'
        }
    }

    private fun initializeEquivalenceRatioScalingFactor(response: String) {
        val value: Float = try {
            Integer.parseInt(response.substring(4, 6), 16) * 1f
        }catch (ex: NumberFormatException) {
            Log.i(BuildConfig.APP_NAME, ex.message ?: ex.toString())
            0f
        } catch (ex: StringIndexOutOfBoundsException) {
            Log.i(BuildConfig.APP_NAME, ex.message ?: ex.toString())
            0f
        }
        if(value != 0f) {
            maxEquivalenceRatio = value
            equivalenceRatioScalingFactor = value / 65535f
        }
    }

    private fun initializeOxygenSensorCurrentScalingFactor(response: String) {
        val value: Float = try {
            Integer.parseInt(response.substring(8, 10), 16) * 1f
        }catch (ex: NumberFormatException) {
            Log.i(BuildConfig.APP_NAME, ex.message ?: ex.toString())
            0f
        } catch (ex: StringIndexOutOfBoundsException) {
            Log.i(BuildConfig.APP_NAME, ex.message ?: ex.toString())
            0f
        }
        if(value != 0f) {
            maxOxygenSensorCurrent = value
            oxygenSensorCurrentScalingFactor = value / 32768f
        }
    }

    private fun initializeOxygenSensorVoltageScalingFactor(response: String) {
        val value: Float = try {
            Integer.parseInt(response.substring(6, 8), 16) * 1f
        }catch (ex: NumberFormatException) {
            Log.i(BuildConfig.APP_NAME, ex.message ?: ex.toString())
            0f
        } catch (ex: StringIndexOutOfBoundsException) {
            Log.i(BuildConfig.APP_NAME, ex.message ?: ex.toString())
            0f
        }
        if(value != 0f) {
            maxOxygenSensorVoltage = value
            oxygenSensorVoltageScalingFactor = value / 65535f
        }
    }

    private fun initializeMAFScalingFactor(response: String) {
        val value: Float = try {
            Integer.parseInt(response.substring(4, 6), 16) * 1f
        }catch (ex: NumberFormatException) {
            Log.i(BuildConfig.APP_NAME, ex.message ?: ex.toString())
            0f
        } catch (ex: StringIndexOutOfBoundsException) {
            Log.i(BuildConfig.APP_NAME, ex.message ?: ex.toString())
            0f
        }
        if(value != 0f) {
            mafScalingFactor = (value * 10) / 65535f
        }
    }
}
