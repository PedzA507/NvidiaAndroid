package th.ac.rmutto.houseprice

import android.annotation.SuppressLint
import android.content.DialogInterface
import android.os.Bundle
import android.os.StrictMode
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import okhttp3.FormBody
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody
import org.json.JSONObject

class MainActivity : AppCompatActivity() {
    @SuppressLint("DefaultLocale")

    // Initialize EditText variables for all input fields
    lateinit var editTextOpen: EditText
    lateinit var editTextHigh: EditText
    lateinit var editTextLow: EditText
    lateinit var editTextClose: EditText // Add this line for 'close'
    lateinit var editTextAdjClose: EditText
    lateinit var editTextVolume: EditText

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        // To run network operations on a main thread or as a synchronous task.
        val policy = StrictMode.ThreadPolicy.Builder().permitAll().build()
        StrictMode.setThreadPolicy(policy)

        // Find all EditText fields by ID
        editTextOpen = findViewById(R.id.editTextOpen)
        editTextHigh = findViewById(R.id.editTextHigh)
        editTextLow = findViewById(R.id.editTextLow)
        editTextClose = findViewById(R.id.editTextClose) // Add this line for 'close'
        editTextAdjClose = findViewById(R.id.editTextAdjClose)
        editTextVolume = findViewById(R.id.editTextVolume)
        val btnPredict = findViewById<Button>(R.id.btnPredict)

        btnPredict.setOnClickListener {
            val url: String = getString(R.string.root_url) + "/api/nvidia"


            val okHttpClient = OkHttpClient()
            val formBody: RequestBody = FormBody.Builder()
                .add("open", editTextOpen.text.toString())
                .add("high", editTextHigh.text.toString())
                .add("low", editTextLow.text.toString())
                .add("close", editTextClose.text.toString()) // Add this line for 'close'
                .add("adjclose", editTextAdjClose.text.toString())
                .add("volume", editTextVolume.text.toString())
                .build()
            val request: Request = Request.Builder()
                .url(url)
                .post(formBody)
                .build()

            val response = okHttpClient.newCall(request).execute()
            if (response.isSuccessful) {
                val data = JSONObject(response.body!!.string())
                if (data.length() > 0) {
                    val predictedPrice = data.getDouble("price")
                    val message = "ราคาประเมินของ Nvidia คือ $predictedPrice ดอลลาร์"
                    val builder = AlertDialog.Builder(this)
                    builder.setTitle("ระบบประเมินราคา Nvidia!!")
                    builder.setMessage(message)
                    builder.setNeutralButton("OK", clearText())
                    val alert = builder.create()
                    alert.show()
                }
            } else {
                Toast.makeText(applicationContext, "ไม่สามารถเชื่อต่อกับเซิร์ฟเวอร์ได้", Toast.LENGTH_LONG).show()
            }
        }// button predict
    }// onCreate function

    private fun clearText(): DialogInterface.OnClickListener? {
        return DialogInterface.OnClickListener { dialog, which ->
            editTextOpen.text.clear()
            editTextHigh.text.clear()
            editTextLow.text.clear()
            editTextClose.text.clear() // Add this line for 'close'
            editTextAdjClose.text.clear()
            editTextVolume.text.clear()
            editTextOpen.requestFocus()
        }
    }

}// main class
