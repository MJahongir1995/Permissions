package uz.jahongir.permissions

import android.Manifest
import android.Manifest.permission.SEND_SMS
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.telephony.SmsManager
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import com.github.florent37.runtimepermission.kotlin.askPermission
import com.github.florent37.runtimepermission.kotlin.coroutines.experimental.askPermission
import kotlinx.android.synthetic.main.activity_sms_sending.*
import uz.jahongir.permissions.databinding.ActivitySmsSendingBinding
import uz.jahongir.permissions.models.MyContacts

class SMS_Sending : AppCompatActivity() {

    private val binding by lazy { ActivitySmsSendingBinding.inflate(layoutInflater) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        val contact = intent.getSerializableExtra("key") as MyContacts
        binding.name.text = contact.name
        binding.number.text = contact.number

        binding.btnSend.setOnClickListener {
            askPermission(Manifest.permission.SEND_SMS){
                //all permissions already granted or just granted

                val text = binding.edtText.text.toString()
                var obj = SmsManager.getDefault()
                obj.sendTextMessage(contact.number,
                    null,  text,
                    null, null)
                Toast.makeText(this, "Successfully sent", Toast.LENGTH_SHORT).show()

            }.onDeclined { e ->
                if (e.hasDenied()) {

                    AlertDialog.Builder(this)
                        .setMessage("If don't give permission the app may not work!")
                        .setPositiveButton("Yes") { dialog, which ->
                            e.askAgain();
                        } //ask again
                        .setNegativeButton("No") { dialog, which ->
                            dialog.dismiss();
                        }
                        .show();
                }

                if(e.hasForeverDenied()) {
                    //the list of forever denied permissions, user has check 'never ask again'

                    // you need to open setting manually if you really need it
                    e.goToSettings();
                }
            }
        }

        binding.back.setOnClickListener {
            finish()
        }
    }
}