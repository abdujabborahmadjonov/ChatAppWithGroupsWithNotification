package dev.abdujabbor.chatappsimple.fragments

import android.Manifest
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.drawable.BitmapDrawable
import android.media.MediaPlayer
import android.media.RingtoneManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Vibrator
import android.util.Log
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.ContextCompat
import androidx.core.content.ContextCompat.getSystemService
import androidx.core.view.GravityCompat
import androidx.fragment.app.Fragment
import androidx.navigation.NavOptions
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.material.navigation.NavigationView
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import dev.abdujabbor.chatappsimple.MainActivity
import dev.abdujabbor.chatappsimple.R
import dev.abdujabbor.chatappsimple.adapters.UsersAdapter
import dev.abdujabbor.chatappsimple.adapters.ViewPagerAdapter
import dev.abdujabbor.chatappsimple.databinding.FragmentAllUsersBinding
import dev.abdujabbor.chatappsimple.databinding.HeaderLayoutBinding
import dev.abdujabbor.chatappsimple.models.MyPerson


class AllUsersFragment : Fragment() {
    private val serverKey = ""
    val binding by lazy { FragmentAllUsersBinding.inflate(layoutInflater) }
    private lateinit var database: FirebaseDatabase
    private lateinit var rvAdapter: UsersAdapter
    lateinit var userlist: ArrayList<MyPerson>
    private lateinit var reference: DatabaseReference
    lateinit var auth: FirebaseAuth

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {


        auth=FirebaseAuth.getInstance()
        binding.viewPager.isSaveEnabled=false
        val adapter = ViewPagerAdapter(requireActivity())
        binding.viewPager.adapter = adapter
        binding.apply {
            TabLayoutMediator(tabLayout, viewPager) { tab, position ->
                tab.icon = when (position) {
                    0 -> ContextCompat.getDrawable(requireContext(), R.drawable.baseline_person_24)
                    1 -> ContextCompat.getDrawable(requireContext(), R.drawable.baseline_groups_24)
                    else -> throw IllegalStateException("Invalid position $position")
                }
            }.attach()
        }

        loadheader()








        binding.toolbar.setOnLongClickListener {
            FirebaseAuth.getInstance().signOut()
            val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(dev.abdujabbor.chatappsimple.R.string.default_web_client_id))
                .requestEmail().build()

            val googleSigningClient = GoogleSignIn.getClient(requireActivity(), gso)
            googleSigningClient.signOut()

            Toast.makeText(requireContext(), "sdg", Toast.LENGTH_SHORT).show()
            true
        }
        binding.toolbar.setNavigationOnClickListener {
            binding.drawerlayout.openDrawer(GravityCompat.START)
            val vibratorService =
                requireActivity().getSystemService(Context.VIBRATOR_SERVICE) as Vibrator
            vibratorService.vibrate(60)
        }
        binding.navView.setNavigationItemSelectedListener {
            when (it.itemId) {
                R.id.logout -> {
                    val vibratorService = requireActivity().getSystemService(Context.VIBRATOR_SERVICE) as Vibrator
                    vibratorService.vibrate(50)
                    FirebaseAuth.getInstance().signOut()
                    val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                        .requestIdToken(getString(R.string.default_web_client_id))
                        .requestEmail().build()

                    val googleSigningClient = GoogleSignIn.getClient(requireActivity(), gso)
                    googleSigningClient.signOut()

                    findNavController().navigate(
                        R.id.authMainFragment,
                        null,
                        NavOptions.Builder()
                            .setPopUpTo(findNavController().currentDestination?.id ?: 0, true)
                            .build()
                    )
                }
                R.id.settins->{
                    showNotification()
                }

            }
            true
        }
        /* val searchView = binding.toolbar.menu.getItem(0).actionView as SearchView
         searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
             override fun onQueryTextSubmit(p0: String?): Boolean {
                 return true
             }

             override fun onQueryTextChange(p0: String?): Boolean {
                 if (p0 == null || p0.trim().isEmpty()) {
                     resetSearch()
                     return false
                 }

                 val none = ArrayList(userlist)
                 for (value in userlist) {
                     if (!value.displayName!!.lowercase(Locale.getDefault())
                             .contains(p0.lowercase())
                     ) {
                         none.remove(value)
                     }
                 }
                 rvAdapter = UsersAdapter(none, requireContext(),requireContext()@AllUsersFragment)
                 binding.recyclerview.adapter = rvAdapter
                 return false
             }
         })

         */
        return binding.root
    }

//    fun resetSearch() {
//
//        rvAdapter = UsersAdapter(userlist, requireContext(),requireContext())
//
//        binding.recyclerview.adapter = rvAdapter
//    }

    fun loadheader() {
        val bindingheader by lazy { HeaderLayoutBinding.inflate(layoutInflater) }
        val header = binding.navView.getHeaderView(0)
        val userimage = header.findViewById<ImageView>(com.google.firebase.database.R.id.icon)
        val usernamr = header.findViewById<TextView>(dev.abdujabbor.chatappsimple.R.id.headername)

        Toast.makeText(requireContext(), auth.currentUser?.photoUrl.toString(), Toast.LENGTH_SHORT)
            .show()
        Log.d("salam", auth.currentUser?.displayName.toString())
        Glide.with(requireActivity()).load(auth.currentUser?.photoUrl).into(userimage)
        usernamr.text =
            "${auth.currentUser?.displayName} "
    }
    private fun showNotification(){
        if (Build.VERSION.SDK_INT>= Build.VERSION_CODES.O){
            val chanel1 = NotificationChannel("n","n", NotificationManager.IMPORTANCE_DEFAULT)
            val manager = activity?.getSystemService(NotificationManager::class.java)
            manager!!.createNotificationChannel(chanel1)
            val builder: NotificationCompat.Builder = NotificationCompat.Builder(requireContext(),"n")
                .setContentTitle("Hi I am CHat")
                .setContentText("meesege")
                .setSmallIcon(R.drawable.ic_baseline_error_24)
                .setAutoCancel(false)
            val defaultRingtoneUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)

            val managerCompat = NotificationManagerCompat.from(requireContext())
            val mediaPlayer = MediaPlayer.create(requireContext(),defaultRingtoneUri)
            mediaPlayer.start()

            val contentIntent = PendingIntent.getActivity(requireContext(),0, Intent(requireContext(),
                MainActivity::class.java), PendingIntent.FLAG_IMMUTABLE)
            builder.setContentIntent(contentIntent)
            if (ActivityCompat.checkSelfPermission(
                    requireContext(),
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
