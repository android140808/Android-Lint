package xyz.glorin.customlintchecker

import android.annotation.SuppressLint
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.Message
import android.util.Log

class MainActivity : AppCompatActivity() {



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        handler.sendEmptyMessageDelayed(1, 2000)

        Log.d("Test", "This is a test log")
    }

    companion object{
        val  handler = object : Handler() {}
    }
}