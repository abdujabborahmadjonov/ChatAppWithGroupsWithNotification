package dev.abdujabbor.chatappsimple.fragments

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.navigation.NavOptions
import androidx.navigation.fragment.findNavController
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.firebase.FirebaseException
import com.google.firebase.auth.*
import com.google.firebase.database.*
import dev.abdujabbor.chatappsimple.R
import dev.abdujabbor.chatappsimple.databinding.DialogLoginEmailBinding
import dev.abdujabbor.chatappsimple.databinding.FragmentAuthMainBinding
import dev.abdujabbor.chatappsimple.models.MyPerson
import dev.abdujabbor.chatappsimple.utils.MyData
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import java.util.concurrent.TimeUnit

class AuthMainFragment : Fragment() {

    private val binding by lazy { FragmentAuthMainBinding.inflate(layoutInflater) }
    lateinit var database: FirebaseDatabase
    lateinit var dataRef: DatabaseReference
    lateinit var auth: FirebaseAuth
    lateinit var userlist: ArrayList<MyPerson>
    lateinit var storedVerificationId: String
    lateinit var dialog: AlertDialog
    lateinit var resendToken: PhoneAuthProvider.ForceResendingToken
    private lateinit var callbacks: PhoneAuthProvider.OnVerificationStateChangedCallbacks

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(getString(R.string.default_web_client_id))
            .requestEmail().build()

        val googleSigningClient = GoogleSignIn.getClient(requireActivity(), gso)
        auth = FirebaseAuth.getInstance()
        database = FirebaseDatabase.getInstance()
        dataRef = database.getReference("akkauntlar")





        if (auth.currentUser != null) {
            MyData.USER = MyPerson(
                auth.uid,
                auth.currentUser?.photoUrl.toString(),
                auth.currentUser?.displayName,"online"
            )

            findNavController().navigate(
                R.id.allUsersFragment,
                null,
                NavOptions.Builder()
                    .setPopUpTo(findNavController().currentDestination?.id ?: 0, true).build()
            )


        }

        binding.signingoogel.setOnClickListener {
            startActivityForResult(googleSigningClient.signInIntent, 1)
        }



        return binding.root
    }
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == 1) {
            val task = GoogleSignIn.getSignedInAccountFromIntent(data)

            try {
                val account = task.getResult(ApiException::class.java)
                Log.d("TAG", "onActivityResult: ${account.displayName}")
                fireBaseAuthWithGoogle(account.idToken)

            } catch (e: Exception) {
                Log.d("TAG", "onActivityResult: FAILURE ${e.toString()}")
            }

        }

    }

    private fun fireBaseAuthWithGoogle(idToken: String?) {
        val credential = GoogleAuthProvider.getCredential(idToken, null)
        auth.signInWithCredential(credential).addOnCompleteListener {
            if (it.isSuccessful) {
                Log.d("TAG", "fireBaseAuthWithGoogle: Sign in with credential SUCCESSFUL")
                val user = auth.currentUser
                dataRef.child(auth.uid!!)
                    .setValue(
                        MyPerson(
                            auth.uid,
                            user?.photoUrl.toString(),

                            user?.displayName,"online"
                        )
                    )
//                Toast.makeText(context, "${user?.displayName}", Toast.LENGTH_SHORT).show()
                MyData.USER = MyPerson(
                    auth.uid,  user?.photoUrl.toString(),user?.displayName,"online"
                )
                findNavController().navigate(
                    R.id.allUsersFragment,
                    null,
                    NavOptions.Builder()
                        .setPopUpTo(findNavController().currentDestination?.id ?: 0, true).build()
                )
            } else {
                Log.d("TAG", "fireBaseAuthWithGoogle: Sign in with credential FAILED")
                Toast.makeText(context, "${it.exception?.message}", Toast.LENGTH_SHORT).show()

            }
        }

    }


}