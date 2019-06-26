package com.dthompson.timetap


import android.Manifest
import android.app.AlertDialog
import android.bluetooth.BluetoothAdapter
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.support.v4.app.ActivityCompat
import android.support.v4.app.Fragment
import android.support.v4.content.ContextCompat
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.Toast
import com.np.lekotlin.blemodule.*
import kotlinx.android.synthetic.main.fragment_device.*


/**
 * A simple [Fragment] subclass.
 *
 */
class DeviceFragment : Fragment(), OnDeviceScanListener, View.OnClickListener{
    override fun onScanCompleted(deviceDataList: BleDeviceData) {

        //Initiate a dialog Fragment from here and ask the user to select his device
        // If the application already know the Mac address, we can simply call connect device
        Log.d("On scan complete", deviceDataList.mDeviceAddress);
        Repository.mDeviceAddress = deviceDataList.mDeviceAddress
        BLEConnectionManager.connect(deviceDataList.mDeviceAddress)

    }

    private val REQUEST_LOCATION_PERMISSION = 2018
    private val TAG = "DeviceFragment"
    private val REQUEST_ENABLE_BT = 1000

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view =  inflater.inflate(R.layout.fragment_device, container, false)
        checkLocationPermission()
        val btn = view.findViewById<Button>(R.id.btn_scan)
        btn.setOnClickListener {
            Log.d("Scan Button OnListener","Heyo")
            scanDevice(false)
        }
        return view
    }
    /**
     * Check the Location Permission before calling the BLE API's
     */
    private fun checkLocationPermission() {
        Log.d("Checking permission", "Checking permission")
        if (isAboveMarshmallow()) {
            when {
                isLocationPermissionEnabled() -> initBLEModule()
                ActivityCompat.shouldShowRequestPermissionRationale(activity!!,
                    Manifest.permission.ACCESS_COARSE_LOCATION) -> displayRationale()
                else -> requestLocationPermission()
            }
        } else {
            initBLEModule()
        }
    }

    /**
     * Request Location API
     * If the request go to Android system and the System will throw a dialog message
     * user can accept or decline the permission from there
     */
    private fun requestLocationPermission() {
        ActivityCompat.requestPermissions(activity!!,
            arrayOf(Manifest.permission.ACCESS_COARSE_LOCATION),
            REQUEST_LOCATION_PERMISSION)
    }

    /**
     * If the user decline the Permission request and tick the never ask again message
     * Then the application can't proceed further steps
     * In such situation- App need to prompt the user to do the change form Settings Manually
     */
    private fun displayRationale() {
        AlertDialog.Builder(activity!!)
            .setMessage("Location Permission Disabled!")
            .setPositiveButton("Okay"
            ) { _, _ -> requestLocationPermission() }
            .setNegativeButton("Cancel"
            ) { _, _ -> }
            .show()
    }

    /**
     * If the user either accept or reject the Permission- The requested App will get a callback
     * Form the call back we can filter the user response with the help of request key
     * If the user accept the same- We can proceed further steps
     */
    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>,
                                            grantResults: IntArray) {
        when (requestCode) {
            REQUEST_LOCATION_PERMISSION -> {
                if (permissions.size != 1 || grantResults.size != 1) {
                    throw RuntimeException("Error on requesting location permission.")
                }
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    initBLEModule()
                } else {
                    Toast.makeText(activity!!, "Location permission not granted",
                        Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    /**
     * Check with the system- If the permission already enabled or not
     */
    private fun isLocationPermissionEnabled(): Boolean {
        return ContextCompat.checkSelfPermission(activity!!,
            Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED
    }

    /**
     * The location permission is incorporated in Marshmallow and Above
     */
    private fun isAboveMarshmallow(): Boolean {
        return Build.VERSION.SDK_INT >= Build.VERSION_CODES.M
    }


    /**
     *After receive the Location Permission, the Application need to initialize the
     * BLE Module and BLE Service
     */
    private fun initBLEModule() {
        // BLE initialization
        Log.d("InitBLE","InitBLE")
        if (!BLEDeviceManager.init(activity!!)) {
            Toast.makeText(activity!!, "BLE NOT SUPPORTED", Toast.LENGTH_SHORT).show()
            return
        }
        registerServiceReceiver()
        BLEDeviceManager.setListener(this)

        if (!BLEDeviceManager.isEnabled()) {
            val enableBtIntent = Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE)
            startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT)
        }

        BLEConnectionManager.initBLEService(activity!!)
        Log.d("InitBLE","Got Connection Manager")

    }


    /**
     * Register GATT update receiver
     */
    private fun registerServiceReceiver() {
        activity!!.registerReceiver(mGattUpdateReceiver, makeGattUpdateIntentFilter())
    }



    private val mGattUpdateReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            val action = intent.action
            when {
                BLEConstants.ACTION_GATT_CONNECTED.equals(action) -> {
                    Log.i(TAG, "ACTION_GATT_CONNECTED ")
                    BLEConnectionManager.findBLEGattService(activity!!)
                }
                BLEConstants.ACTION_GATT_DISCONNECTED.equals(action) -> {
                    Log.i(TAG, "ACTION_GATT_DISCONNECTED ")
                }
                BLEConstants.ACTION_GATT_SERVICES_DISCOVERED.equals(action) -> {
                    Log.i(TAG, "ACTION_GATT_SERVICES_DISCOVERED ")
                    try {
                        Thread.sleep(500)
                    } catch (e: InterruptedException) {
                        e.printStackTrace()
                    }
                    BLEConnectionManager.findBLEGattService(activity!!)
                }
                BLEConstants.ACTION_DATA_AVAILABLE.equals(action) -> {
                    val data = intent.getStringExtra(BLEConstants.EXTRA_DATA)
                    val dataStringArray = data.split(" ")
                    Log.i(TAG, "ACTION_DATA_AVAILABLE $data")
                    Log.d("Button press!", dataStringArray.toString())
                    val button = dataStringArray[5].toInt() - 48;
                    Log.d("Button press!", button.toString())
                    if (button < 5) {
                        Log.d("Button press!", "Button $button pressed on bluetooth device!")
                    }
                    Repository.LogEvent(Repository.getActivitiesNames().value!![button])

                }
                BLEConstants.ACTION_DATA_WRITTEN.equals(action) -> {
                    val data = intent.getStringExtra(BLEConstants.EXTRA_DATA)
                    Log.i(TAG, "ACTION_DATA_WRITTEN ")
                }
            }
        }
    }

    /**
     * Intent filter for Handling BLEService broadcast.
     */
    private fun makeGattUpdateIntentFilter(): IntentFilter {
        val intentFilter = IntentFilter()
        intentFilter.addAction(BLEConstants.ACTION_GATT_CONNECTED)
        intentFilter.addAction(BLEConstants.ACTION_GATT_DISCONNECTED)
        intentFilter.addAction(BLEConstants.ACTION_GATT_SERVICES_DISCOVERED)
        intentFilter.addAction(BLEConstants.ACTION_DATA_AVAILABLE)
        intentFilter.addAction(BLEConstants.ACTION_DATA_WRITTEN)

        return intentFilter
    }

    /**
     * Unregister GATT update receiver
     */
    private fun unRegisterServiceReceiver() {
        try {
            activity!!.unregisterReceiver(mGattUpdateReceiver)
        } catch (e: Exception) {
            //May get an exception while user denies the permission and user exists the app
            Log.e(TAG, e.message)
        }

    }

    override fun onDestroy() {
        super.onDestroy()
        BLEConnectionManager.disconnect()
        unRegisterServiceReceiver()
    }

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.btn_scan -> {
                scanDevice(false)
            }
        }
    }



    /**
     * Scan the BLE device if the device address is null
     * else the app will try to connect with device with existing device address.
     */
    private fun scanDevice(isContinuesScan: Boolean) {
        if (!Repository.mDeviceAddress.isNullOrEmpty()) {
            connectDevice()
        } else {
            BLEDeviceManager.scanBLEDevice(isContinuesScan)
        }
    }

    /**
     * Connect the application with BLE device with selected device address.
     */
    private fun connectDevice() {
        Handler().postDelayed({
            BLEConnectionManager.initBLEService(activity!!)
            if (BLEConnectionManager.connect(Repository.mDeviceAddress)) {
                Toast.makeText(activity!!, "DEVICE CONNECTED", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(activity!!, "DEVICE CONNECTION FAILED", Toast.LENGTH_SHORT).show()
            }
        }, 100)
    }

}
