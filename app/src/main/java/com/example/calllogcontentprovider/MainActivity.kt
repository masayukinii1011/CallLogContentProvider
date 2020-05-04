package com.example.calllogcontentprovider

import android.Manifest
import android.annotation.SuppressLint
import android.content.pm.PackageManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.CallLog
import android.widget.SimpleCursorAdapter
import androidx.core.app.ActivityCompat
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    //contentResolver.queryで抽出したい項目
    var cols = listOf<String>(
        CallLog.Calls._ID,
        CallLog.Calls.NUMBER,
        CallLog.Calls.TYPE,
        CallLog.Calls.DURATION
    ).toTypedArray()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        /**
         * パーミッションの許可が無い場合、許可ダイアログを表示
         */
        if (ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.READ_CALL_LOG
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(
                this,
                Array(1) { Manifest.permission.READ_CALL_LOG },
                101
            )
        } else {
            displayLog()
        }
    }

    /**
     * 許可ダイアログの結果を受け取る
     */
    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == 101 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            displayLog()
        }
    }

    @SuppressLint("MissingPermission")
    private fun displayLog() {
        //SQLiteのCursor
        //(検索したいURI, 抽出したい項目, 絞り込み条件, 絞り込みパラメータ, ソート)
        val rs = contentResolver.query(
            CallLog.Calls.CONTENT_URI,
            cols, null, null, "${CallLog.Calls.LAST_MODIFIED} DESC"
        )

        //表示するColumn
        val from = listOf<String>(
            CallLog.Calls.NUMBER,
            CallLog.Calls.DURATION,
            CallLog.Calls.TYPE
        ).toTypedArray()

        //バインドするID
        val to = intArrayOf(R.id.textView1, R.id.textView2, R.id.textView3)

        //ListViewへ渡す
        //(コンテキスト,表示するレイアウト,SQLiteのCursor,表示するColumn,バインドするID,？)
        val adapter =
            SimpleCursorAdapter(applicationContext, R.layout.mylayout, rs, from, to, 0)
        listview.adapter = adapter
    }
}
