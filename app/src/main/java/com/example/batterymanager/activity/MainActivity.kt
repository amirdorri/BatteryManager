package com.example.batterymanager.activity

import android.annotation.SuppressLint
import android.content.*
import android.graphics.Color
import android.os.BatteryManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Gravity
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.core.content.ContextCompat
import com.example.batterymanager.R
import com.example.batterymanager.utils.BatteryUsage
import com.example.batterymanager.databinding.ActivityMainBinding
import com.example.batterymanager.helper.SpManager
import com.example.batterymanager.model.BatteryModel
import com.example.batterymanager.service.BatteryAlarmService
import kotlin.math.roundToInt
import kotlin.collections.groupBy as groupBy1

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    @SuppressLint("SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        initDrawer()
        serviceConfig()


        registerReceiver(BatteryInfoReceiver, IntentFilter(Intent.ACTION_BATTERY_CHANGED))




    }

    private fun initDrawer(){

        binding.imgMenu.setOnClickListener {
            binding.drawer.openDrawer(Gravity.RIGHT)
        }

        binding.incDrawer.txtAppUsage.setOnClickListener {
            startActivity(Intent(this@MainActivity, UsageBatteryActivity::class.java))
            binding.drawer.closeDrawer(Gravity.RIGHT)
        }




    }

    @SuppressLint("SetTextI18n")
    private fun serviceConfig(){

        if (SpManager.isServiceOn(this@MainActivity)==true){
            binding.incDrawer.serviceSwitchTxt.text = "service is on"
            binding.incDrawer.serviceSwitch.isChecked = true
            startService()


        }else{
            binding.incDrawer.serviceSwitchTxt.text = "service is off"
            binding.incDrawer.serviceSwitch.isChecked = false
            stopService()
        }

        binding.incDrawer.serviceSwitch.setOnCheckedChangeListener{switch, isCheck->
            SpManager.setServiceState(this@MainActivity, isCheck)


            if (isCheck){
                startService()
                binding.incDrawer.serviceSwitchTxt.text = "service is on"
                Toast.makeText(applicationContext, "service is turned on", Toast.LENGTH_SHORT).show()
            }else{
                stopService()
                binding.incDrawer.serviceSwitchTxt.text = "service is off"
                Toast.makeText(applicationContext, "service is turned off", Toast.LENGTH_SHORT).show()
            }
        }

    }




    private fun startService(){
        val serviceIntent = Intent(this, BatteryAlarmService::class.java)
        ContextCompat.startForegroundService(this, serviceIntent)
    }

    private fun stopService(){
        val serviceIntent = Intent(this, BatteryAlarmService::class.java)
        stopService(serviceIntent)
    }


    private var BatteryInfoReceiver: BroadcastReceiver = object : BroadcastReceiver() {

        @SuppressLint("SetTextI18n")
        override fun onReceive(context: Context, intent: Intent) {
            var batteryLevel = intent.getIntExtra(BatteryManager.EXTRA_LEVEL, 0)

            if (intent.getIntExtra(BatteryManager.EXTRA_PLUGGED, 0) == 0) {
                binding.txtPlug.text = "unplug"
            } else {
                binding.txtPlug.text = "plug-in"
            }



            binding.txtTemp.text =
                (intent.getIntExtra(BatteryManager.EXTRA_TEMPERATURE, 0) / 10).toString() + " °C"
            binding.txtVoltage.text =
                (intent.getIntExtra(BatteryManager.EXTRA_VOLTAGE, 0) / 1000).toString() + " volt"
            binding.txtTechnology.text = intent.getStringExtra(BatteryManager.EXTRA_TECHNOLOGY)

            binding.circularProgressBar.progressMax = 100f
            binding.circularProgressBar.setProgressWithAnimation(batteryLevel.toFloat())
            binding.txtCharge.text = batteryLevel.toString() + "%"

            val health = intent.getIntExtra(BatteryManager.EXTRA_HEALTH, 0)
            when (health) {
                BatteryManager.BATTERY_HEALTH_DEAD -> {
                    binding.txtHealth.text = "your battery is completely dead, please change it! "
                    binding.txtHealth.setTextColor(Color.parseColor("000000"))
                    binding.imgHealth.setImageResource(R.drawable.death)
                }
                BatteryManager.BATTERY_HEALTH_GOOD -> {
                    binding.txtHealth.text = "your battery is good, please take care of it! "
                    binding.txtHealth.setTextColor(Color.GREEN)
                    binding.imgHealth.setImageResource(R.drawable.good)
                }
                BatteryManager.BATTERY_HEALTH_COLD -> {
                    binding.txtHealth.text = "your battery temperature is cold, its wonderful! "
                    binding.txtHealth.setTextColor(Color.BLUE)
                    binding.imgHealth.setImageResource(R.drawable.cold)
                }
                BatteryManager.BATTERY_HEALTH_OVERHEAT -> {
                    binding.txtHealth.text = "your battery is overheated, be alert its not good! "
                    binding.txtHealth.setTextColor(Color.YELLOW)
                    binding.imgHealth.setImageResource(R.drawable.overheat)
                }
                BatteryManager.BATTERY_HEALTH_OVER_VOLTAGE -> {
                    binding.txtHealth.text = "your battery has overload, please turn your phone off! "
                    binding.txtHealth.setTextColor(Color.RED)
                    binding.imgHealth.setImageResource(R.drawable.voltage_high)
                }
                else -> {
                    binding.txtHealth.text = "your battery is completely dead, please change it! "
                    binding.txtHealth.setTextColor(Color.parseColor("000000"))
                    binding.imgHealth.setImageResource(R.drawable.death)
                }
            }


        }

    }


    override fun onBackPressed() {



        val dialogBuilder = AlertDialog.Builder(this)
            .setMessage("Do you want to exit the app ?")
            .setCancelable(true)
            .setPositiveButton("Yes", DialogInterface.OnClickListener{
                dialog, id -> finish()

            })
            .setNegativeButton("Cancel", DialogInterface.OnClickListener{
                    dialog, id -> dialog.cancel()
            })

        val alert = dialogBuilder.create()
        alert.setTitle("Exit App")
        alert.show()





    }





}



