package me.hsy.mycanvas.ui.main

import android.annotation.SuppressLint
import android.content.ContentResolver
import android.content.Context
import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.OpenableColumns
import android.util.Log
import android.view.View
import android.webkit.MimeTypeMap
import android.widget.Button
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.core.net.toFile
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import me.hsy.mycanvas.R
import me.hsy.mycanvas.ui.home.beans.Course
import me.hsy.mycanvas.ui.home.beans.State
import me.hsy.mycanvas.ui.main.beans.Assignment
import me.hsy.mycanvas.ui.main.beans.InfoBean
import me.hsy.mycanvas.util.TimeParser
import org.w3c.dom.Text
import java.io.File
import java.io.InputStream
import java.net.URI.create
import android.content.ActivityNotFoundException




// TODO: choose file and submit
class AssignmentActivity : AppCompatActivity() {

    private val gson = GsonBuilder().create()
    private var assignmentTitle: TextView? = null
    private var dueText: TextView? = null
    private var submitBtn: Button? = null
    private var pointText: TextView? = null
    private var submitText: TextView? = null
    private var descriptionText: TextView? = null
    private var state: State? = null
    private var fileBox: RelativeLayout? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_assignment)

        val courseNameInt = intent.getIntExtra("courseNameInt", 3)
        val assignmentName = intent.getStringExtra("assignmentName")
        val dueTime = intent.getLongExtra("dueTime", 0)
        val stateInt = intent.getIntExtra("submissionState", 0)
        state = State.from(stateInt)

        val courseName = Course.from(courseNameInt)
        title = "Assignment for ${courseName.courseName}"

        submitText = findViewById(R.id.submit)
        pointText = findViewById(R.id.point)
        dueText = findViewById(R.id.due_time)
        descriptionText = findViewById(R.id.assignment_description)
        assignmentTitle = findViewById(R.id.assignment_title)


        submitBtn = findViewById(R.id.btn_submit)
        if (state == State.TODO) {
            submitBtn?.text = "SUBMIT ASSIGNMENT"
        } else {
            submitBtn?.text = "RE-SUBMIT ASSIGNMENT"
        }
        submitBtn?.setOnClickListener {
            val intent = Intent(Intent.ACTION_GET_CONTENT)
            intent.addCategory(Intent.CATEGORY_OPENABLE)
            intent.type = "*/*"
            startActivityForResult(
                intent,
                REQUEST_FOR_FILE
            )
        }

        fileBox = findViewById(R.id.file_box)
        fileBox?.visibility = View.INVISIBLE

        // load
        val inputStream: InputStream? = assets?.open("assignment_sample.json") ?: null
        val bodyString: String? = inputStream?.bufferedReader().use{it?.readText()}
        if (inputStream != null) {
            Log.d("@=>", "Json loaded")
        }
        val assignment = gson.fromJson(bodyString, Assignment::class.java)

        assignmentTitle?.text = assignmentName
        dueText?.text = TimeParser.millTodate(dueTime)
        pointText?.text = assignment.score.toString()
        submitText?.text = assignment.submitting
        descriptionText?.text = assignment.description
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == REQUEST_FOR_FILE) {
            if (data == null) {
                return
            }
            if (state == State.TODO) {
                state = State.DONE
                submitBtn?.text = "RE-SUBMIT ASSIGNMENT"
                setResult(RESULT_OK)
            }
            val uri: Uri? = data.data
            var path:String = uri!!.path!!
            val name = uriToFileName(uri, this)
            fileBox?.findViewById<TextView>(R.id.file_name)?.text = name
            fileBox?.findViewById<TextView>(R.id.file_path)?.text = path
            fileBox?.visibility = View.VISIBLE
            fileBox?.setOnClickListener{
                val intent = Intent(Intent.ACTION_VIEW)
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                intent.setDataAndType(uri, "application/pdf")
                try {
                    this@AssignmentActivity.startActivity(intent)
                } catch (e: ActivityNotFoundException) {
                    Log.w("@=>", "Activity was not found for intent, $intent")
                }
            }
        }
    }

    @SuppressLint("Range")
    fun uriToFileName(uri:Uri, context: Context): String{
        return when(uri.scheme){
            ContentResolver.SCHEME_FILE -> uri.toFile().name
            ContentResolver.SCHEME_CONTENT->{
                val cursor = context.contentResolver.query(uri, null, null, null, null, null)
                cursor?.let {
                    it.moveToFirst()
                    val displayName = it.getString(it.getColumnIndex(OpenableColumns.DISPLAY_NAME))
                    cursor.close()
                    displayName
                }?:"${System.currentTimeMillis()}.${MimeTypeMap.getSingleton().getExtensionFromMimeType(context.contentResolver.getType(uri))}}"

            }
            else -> "${System.currentTimeMillis()}.${MimeTypeMap.getSingleton().getExtensionFromMimeType(context.contentResolver.getType(uri))}}"
        }
    }
    companion object {
        const val REQUEST_FOR_FILE = 1919
    }
}