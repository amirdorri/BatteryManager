package com.example.batterymanager.service

import android.annotation.SuppressLint
import android.app.*
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.media.RingtoneManager
import android.net.Uri
import android.os.*
import androidx.core.app.NotificationCompat
import com.example.batterymanager.R

class BatteryAlarmService : Service() {

    var manager: NotificationManager? = null

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        createNotificationChannel()
        startNotification()
        registerReceiver(BatteryInfoReceiver, IntentFilter(Intent.ACTION_BATTERY_CHANGED))
        return START_STICKY
    }

    //test




    override fun onBind(intent: Intent?): IBinder? {
        return null
    }

private fun createNotificationChannel(){

    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
        val serviceChannel = NotificationChannel(CHANNEL_ID, CHANNEL_NAME, NotificationManager.IMPORTANCE_MIN)
        manager = getSystemService(NotificationManager::class.java)
        manager?.createNotificationChannel(serviceChannel)
    }
}

    private var BatteryInfoReceiver: BroadcastReceiver = object : BroadcastReceiver() {

        @SuppressLint("SetTextI18n", "RestrictedApi")
        override fun onReceive(context: Context, intent: Intent) {
            val batteryLevel = intent.getIntExtra(BatteryManager.EXTRA_LEVEL, 0)
            var plugState = ""
            if (intent.getIntExtra(BatteryManager.EXTRA_PLUGGED, 0) == 0) {
                plugState = "your phone is unplugged"
            }
            else {
                plugState = "your phone is charging"


            }

          if (batteryLevel>84){
            startAlarm()
              plugState = "your phone is fully charged"
              stopSelf()

          }


            updateNotification(batteryLevel, plugState)


        }
    }



    private fun startNotification(){
            val notification = NotificationCompat.Builder(this,CHANNEL_ID )

            .setContentTitle("Loading...")
            .setContentText("wait for battery data")
            .setSmallIcon(R.drawable.active)
            .build()



        startForeground(NOTIFICATION_ID, notification)
    }


    private fun startAlarm() {
        val alarm: Uri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)
        val ring = RingtoneManager.getRingtone(applicationContext, alarm)
        ring.play()
        ring.stop()

        val vib = getSystemService(VIBRATOR_SERVICE) as Vibrator
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            vib.vibrate(VibrationEffect.createOneShot(1200, VibrationEffect.DEFAULT_AMPLITUDE))

        } else {
            vib.vibrate(1200)
        }
    }




    private fun updateNotification(batteryLevel: Int, plugState: String){
        val notification = NotificationCompat.Builder(this,CHANNEL_ID )
            .setContentTitle(plugState)
            .setContentText("battery charge: $batteryLevel %")
            .setSmallIcon(R.drawable.active)
            .build()




        manager?.notify(NOTIFICATION_ID, notification)

    }

    companion object{
        const val CHANNEL_ID = "BatteryManagerChannel"
        const val CHANNEL_NAME = "BatteryManagerService"
        const val NOTIFICATION_ID = 1

    }

}




