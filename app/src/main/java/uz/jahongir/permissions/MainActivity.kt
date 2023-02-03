package uz.jahongir.permissions

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Color
import android.net.Uri
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.ContactsContract
import android.provider.MediaStore
import android.widget.SearchView
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.core.app.ActivityCompat
import androidx.recyclerview.widget.RecyclerView
import uz.jahongir.permissions.adapter.MyContactRvAdapter
import uz.jahongir.permissions.databinding.ActivityMainBinding
import uz.jahongir.permissions.helper.MyButton
import uz.jahongir.permissions.helper.MySwipeHelper
import uz.jahongir.permissions.interfaces.MyButtonClickListener
import uz.jahongir.permissions.models.MyContacts

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private lateinit var list: ArrayList<MyContacts>
    private lateinit var myContactRvAdapter: MyContactRvAdapter
    lateinit var contactList: ArrayList<MyContacts>

    private val cols = listOf<String>(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME, ContactsContract.CommonDataKinds.Phone.NUMBER).toTypedArray()

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        contactList = ArrayList()
        myContactRvAdapter = MyContactRvAdapter(contactList)

        //Swipe gesture
        val swipe = object : MySwipeHelper(this, binding.rv, 160) {
            override fun instantiateMyButton(viewHolder: RecyclerView.ViewHolder, buffer: MutableList<MyButton>){

                buffer.add(MyButton(this@MainActivity, "", 5, R.drawable.ic_phone, Color.parseColor("#54ED68"),
                    object : MyButtonClickListener {
                        override fun onClick(position: Int) {
                            makeCall(position)

                            Toast.makeText(this@MainActivity, "Call", Toast.LENGTH_SHORT).show()
                        }
                    }
                ))

                buffer.add(MyButton(this@MainActivity, "", 5, R.drawable.ic_message, Color.parseColor("#3399FF"),
                    object : MyButtonClickListener {
                        override fun onClick(position: Int) {
                            val intent = Intent(this@MainActivity, SMS_Sending::class.java)
                            intent.putExtra("key", contactList[position])
                            startActivity(intent)
                            Toast.makeText(this@MainActivity, "Message", Toast.LENGTH_SHORT).show()
                        }
                    }
                ))
            }
        }

        if (ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.READ_CONTACTS
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.READ_CONTACTS), 1)
        } else {
            readContact()
        }
    }

    private fun makeCall(position: Int) {
        val TELEPHONE_SCHEMA = "tel:"
        val PRESERVED_CHARACTER = "+"
        val HK_COUNTRY_CODE = "998"
        val HK_OBSERVATORY_PHONE_NUMBER = myContactRvAdapter.getContactAt(position).number

        // Step 1: Define the phone call uri
        val phoneCallUri =
            Uri.parse(TELEPHONE_SCHEMA + PRESERVED_CHARACTER + HK_COUNTRY_CODE + HK_OBSERVATORY_PHONE_NUMBER)

        // Step 2: Set Intent action to `ACTION_DIAL`
        val phoneCallIntent = Intent(Intent.ACTION_DIAL).also {
            // Step 3. Set phone call uri to Intent data
            it.setData(phoneCallUri)
        }

        // Step 4: Pass the Intent to System to start any <Activity> which can accept `ACTION_DIAL`
        startActivity(phoneCallIntent)
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        if (requestCode == 1) {
            if (grantResults.size == 1 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                readContact()
            }
        }
    }

    @SuppressLint("Range")
    @RequiresApi(Build.VERSION_CODES.O)
    private fun readContact() {

        val contacts = contentResolver.query(
            ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
            null,
            null,
            null
        )

        while (contacts!!.moveToNext()) {
            val myContacts = MyContacts(
                contacts!!.getString(contacts!!.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME)),
                contacts!!.getString(contacts!!.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER))
            )

            val imageUrl =
                contacts!!.getString(contacts.getColumnIndex(ContactsContract.CommonDataKinds.Phone.PHOTO_URI))

            if (imageUrl != null) {
                myContacts.image =
                    MediaStore.Images.Media.getBitmap(contentResolver, Uri.parse(imageUrl))
            }
            contactList.add(myContacts)
        }

        //contacts.close()
        binding.rv.adapter = MyContactRvAdapter(contactList)

        binding.search.setOnQueryTextListener(object : SearchView.OnQueryTextListener,
            androidx.appcompat.widget.SearchView.OnQueryTextListener {

            override fun onQueryTextSubmit(query: String?): Boolean {
                return false
            }

            override fun onQueryTextChange(newText: String?): Boolean {

                contentResolver.query(
                    ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
                    cols,
                    "${ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME} LIKE?",
                    Array(1) { "%newText%" },
                    ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME
                )?.close()
                return false
            }
        })
    }
}





