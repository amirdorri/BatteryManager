package com.example.batterymanager.activity

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import com.example.batterymanager.databinding.ActivityStarterBinding
import com.example.batterymanager.helper.SpManager
import java.util.*
import kotlin.concurrent.timerTask

class StarterActivity : AppCompatActivity() {


    private lateinit var binding:ActivityStarterBinding


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding=ActivityStarterBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)


        val textArray = arrayOf(
            "Make Your Battery Powerful",
            "Make Your Battery Charging faster",
            "Make Your Battery Safer",
            "Prevent From any Problem",
            "Manage Your Battery",
            "Notify when Your Battery is Fulled")

        var x = 1
        for(x in 1..6){

            helpTextGenerator((x*1000).toLong(), textArray[x-1])
        }







        Timer().schedule(timerTask {
            startActivity(Intent(this@StarterActivity, MainActivity::class.java))
            finish()
        }, 7000)


    }


    private fun helpTextGenerator(delayTime: Long, helpText: String) {
        Timer().schedule(timerTask {
            runOnUiThread(timerTask {

                binding.helpTxt.text=helpText
            })
        }, delayTime)

    }
}