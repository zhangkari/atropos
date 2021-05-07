package com.class100.yunshixun

import android.os.Bundle
import android.view.View
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.class100.atropos.env.context.AtDevices
import com.class100.atropos.env.context.permission.AtPermission
import com.class100.atropos.env.context.permission.PermissionCallback
import com.class100.atropos.generic.AtFreqClick
import com.class100.atropos.generic.AtRuntime

class MainActivity : AppCompatActivity() {
    private val multiClick = lazy {
        AtFreqClick(5, 1000)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setListener()
        initView()
    }

    private fun setListener() {
        findViewById<View>(android.R.id.content).setOnClickListener { _ ->
            multiClick.value.onClick {

            }
        }

        findViewById<View>(R.id.tv_check_permission).setOnClickListener {
            checkPermission()
        }

        findViewById<View>(R.id.btn_exec).setOnClickListener {
            val et = findViewById<EditText>(R.id.et_exec);
            val cmd = et.text.toString()
            val result = AtRuntime.exec(cmd);
            et.setText(result.status.toString() + ":" + result.content)
        }
    }

    private fun checkPermission() {
        AtPermission.requestAppPermission(this,
            object : PermissionCallback {
                override fun onGrantedEntirely() {
                    Toast.makeText(this@MainActivity, "Grant All", Toast.LENGTH_SHORT).show()
                }

                override fun onPermissionDenied(permissions: List<String>) {
                    val sb = StringBuilder()
                    permissions.forEach {
                        sb.append(it).append("\n")
                    }
                    Toast.makeText(this@MainActivity, sb.toString(), Toast.LENGTH_SHORT).show()
                }
            })
    }

    private fun initView() {
        findViewById<TextView>(R.id.tv_device_id).text = AtDevices.getDeviceUUID()
    }
}