package com.class100.yunshixun

import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.class100.atropos.env.context.permission.AtPermission
import com.class100.atropos.env.context.permission.PermissionCallback
import com.class100.atropos.generic.AtFreqClick


class MainActivity : AppCompatActivity() {
    private val multiClick = lazy {
        AtFreqClick(5, 1000)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setListener()
    }

    private fun setListener() {
        findViewById<View>(android.R.id.content).setOnClickListener { _ ->
            multiClick.value.onClick {

            }
        }

        findViewById<View>(R.id.tv_check_permission).setOnClickListener {
            checkPermission()
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
}