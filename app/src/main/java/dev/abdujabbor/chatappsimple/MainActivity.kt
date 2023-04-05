package dev.abdujabbor.chatappsimple

import android.Manifest
import android.annotation.SuppressLint
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.ContentValues.TAG
import android.content.Intent
import android.content.pm.PackageManager
import android.media.MediaPlayer
import android.media.RingtoneManager
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.messaging.FirebaseMessaging
import dev.abdujabbor.chatappsimple.utils.MyData
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.text.SimpleDateFormat
import java.util.*


class MainActivity : AppCompatActivity() {
    lateinit var database :FirebaseDatabase
    lateinit var dataRef:DatabaseReference
    lateinit var auth:FirebaseAuth
    lateinit var itemRef:DatabaseReference
    @SuppressLint("StringFormatInvalid")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
         auth = FirebaseAuth.getInstance()
         database = FirebaseDatabase.getInstance()
        dataRef = database.getReference("akkauntlar")

        if (auth.currentUser!=null) itemRef = dataRef.child(auth.uid!!).child("tick")
    }
// agar button ezilganda dialogda koplab ochilgan emaillarni chiqarib berish
    suspend fun addGoogleAcconuntEntering()=  CoroutineScope(Dispatchers.IO).launch {
        try {

        }catch (e:java.lang.Exception) {
            withContext(Dispatchers.Main) {
                Toast.makeText(this@MainActivity, e.message, Toast.LENGTH_SHORT).show()
            }
        }
    }

    override fun onStop() {
        super.onStop()
       val a=  SimpleDateFormat("MMMMM.dd  HH:mm").format(Date())

        if (auth.currentUser!=null)   itemRef.setValue("last seen at $a ")
    }

    override fun onResume() {
        super.onResume()

        if (auth.currentUser!=null)   itemRef.setValue("online")

    }

    private fun showNotification(){
        if (Build.VERSION.SDK_INT>=Build.VERSION_CODES.O){
                val chanel1 =NotificationChannel("n","n",NotificationManager.IMPORTANCE_HIGH)
            val manager = getSystemService(NotificationManager::class.java)
            manager.createNotificationChannel(chanel1)
            val builder:NotificationCompat.Builder = NotificationCompat.Builder(this,"n")
                .setContentTitle("Hi I am CHat")
                .setContentText("meesege")
                .setSmallIcon(R.drawable.ic_baseline_error_24)
                .setAutoCancel(false)
            val defaultRingtoneUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)

            val managerCompat = NotificationManagerCompat.from(this)
                val mediaPlayer = MediaPlayer.create(this,defaultRingtoneUri)
                mediaPlayer.start()

            val contentIntent = PendingIntent.getActivity(this,0, Intent(this,MainActivity::class.java),PendingIntent.FLAG_UPDATE_CURRENT)
        builder.setContentIntent(contentIntent)
            if (ActivityCompat.checkSelfPermission(
                    this,
                    Manifest.permission.POST_NOTIFICATIONS
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                // TODO: Consider calling
                //    ActivityCompat#requestPermissions
                // here to request the missing permissions, and then overriding
                //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                //                                          int[] grantResults)
                // to handle the case where the user grants the permission. See the documentation
                // for ActivityCompat#requestPermissions for more details.
                return
            }
            managerCompat.notify(999,builder.build())
        }
    }

}
