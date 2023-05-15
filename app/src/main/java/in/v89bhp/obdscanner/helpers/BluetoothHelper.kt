package `in`.v89bhp.obdscanner.helpers

import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothDevice
import android.bluetooth.BluetoothManager
import android.bluetooth.BluetoothSocket
import android.content.Context
import android.os.Handler
import android.util.Log
import androidx.core.content.ContextCompat.getSystemService
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import `in`.v89bhp.obdscanner.R
import `in`.v89bhp.obdscanner.enums.HandlerMessageCodes
import java.io.IOException
import java.util.UUID


object BluetoothHelper {
    private const val TAG = "BtHelper"
    const val UID = "00001101-0000-1000-8000-00805F9B34FB"
    val bluetoothAdapter: BluetoothAdapter? by lazy {
        (getSystemService(
            applicationContext,
            BluetoothManager::class.java
        ) as BluetoothManager).adapter
    }

    private var mHandler: Handler? = null

    private var connectThread: ConnectThread? = null

    var socket: BluetoothSocket? = null

    lateinit var applicationContext: Context

    private val _connecting = MutableLiveData(false)

    /**
     *  Is observed by ***BluetoothFragment*** to
     *  show/ hide the 'Connecting...' progress bar.
     */
    val connecting: LiveData<Boolean>
        get() = _connecting

    /**
     * Close opened streams (if any) and turn off Bluetooth
     */
    fun turnOff() {
        socket?.close()
        // Future versions of Android won't support disabling of Bluetooth. So commented out:
//        try {
//            bluetoothAdapter?.disable()
//        } catch (ex: SecurityException) {
//            Log.e(TAG, "Bluetooth permission(s) not granted", ex)
//        }
        _connecting.value = false
    }

    fun queryPairedDevices(): List<BluetoothDevice>? =
        try {
            bluetoothAdapter?.bondedDevices?.toList()
        } catch (ex: SecurityException) {
            // TODO Find usages and add logic to handle this exception:
            Log.e(TAG, "Bluetooth permission(s) not granted", ex)
            emptyList()
        }



    fun connect(mHandler: Handler?, bluetoothDevice: BluetoothDevice?) {
        if (bluetoothDevice != null) {
            this.mHandler = mHandler

            if (connectThread == null || connectThread!!.isAlive.not()) {
                connectThread = ConnectThread(bluetoothDevice).apply { start() }
            }
        }
    }

    fun close() {
        if (connectThread != null && connectThread!!.isAlive) {
            connectThread!!.close()
        }
    }

    private class ConnectThread(val bluetoothDevice: BluetoothDevice) : Thread() {
        override fun run() {
            _connecting.postValue(true)
            // Close any existing connection:
            socket?.let {
                it.close()
            }

            // Bombarding the adapter with connection requests will only result in failures.
            // Wait for 3 seconds before attempting a new connection.
            try {
                Thread.sleep(3000)
            } catch (ex: InterruptedException) {
                Log.i(TAG, "Bluetooth connect thread interrupted.")
            }

            try {
                socket =
                    bluetoothDevice.createInsecureRfcommSocketToServiceRecord(UUID.fromString(UID))
                // Always cancel discovery because it will slow down a connection
                bluetoothAdapter?.cancelDiscovery()
                socket?.let {

                    try {
                        Log.i(TAG, "Trying to connect to ${bluetoothDevice.name}")
                        it.connect()
                        connected(bluetoothDevice.name)
                        _connecting.postValue(false)
                        Log.i(TAG, "Connected to ${bluetoothDevice.name}")
                    } catch (ex: IOException) {
                        Log.i(TAG, "it.connect() threw IOException.")
                        try {
                            socket?.close()
                        } catch (ex: IOException) {
                            Log.e(TAG, "Unable to close() socket during connection failure", ex)
                        }
                        _connecting.postValue(false)
                        connectionFailed(applicationContext.getString(
                            R.string.bluetooth_connect_failed,
                            bluetoothDevice.name, "BluetoothSocket.connect() threw IOException"
                        ))
                    }
                }
            } catch (ex: IOException) {
                Log.e(TAG, "Unable to create bluetooth socket.", ex)
                connectionFailed(applicationContext.getString(
                    R.string.bluetooth_connect_failed,
                    bluetoothDevice.name, "Unable to create bluetooth socket."
                ))
                _connecting.postValue(false)
            } catch (ex: SecurityException) {
                Log.e(TAG, "Bluetooth permission(s) not granted", ex)
                connectionFailed(applicationContext.getString(
                    R.string.bluetooth_connect_failed,
                    bluetoothDevice.name, "Bluetooth permission(s) not granted."
                ))
                _connecting.postValue(false)
            }
        }

        fun close() {
            socket?.close()
            _connecting.value = false
        }

        private fun connected(deviceName: String) {
            // Send a success message back to the view model
            Log.i(TAG, "Connected")
            val msg = mHandler?.obtainMessage(
                HandlerMessageCodes.MESSAGE_SNACKBAR.ordinal,
                1,
                -1,
                applicationContext.getString(R.string.bluetooth_connected, deviceName)
            )

            mHandler?.sendMessage(msg!!)
        }

        private fun connectionFailed(errorMessage: String) {
            // Send a failure message back to the view model
            Log.i(TAG, "Connection failed!")
            val msg = mHandler?.obtainMessage(
                HandlerMessageCodes.MESSAGE_SNACKBAR.ordinal,
                0,
                -1,
                errorMessage
            )

            mHandler?.sendMessage(msg!!)
        }
    }
}