package com.example.inTesi.prova.activities

import android.app.ProgressDialog
import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.preference.PreferenceManager
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import com.example.inTesi.R
import com.google.android.gms.tasks.OnSuccessListener
import com.google.android.gms.tasks.Task
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.OnProgressListener
import com.google.firebase.storage.StorageReference
import com.google.firebase.storage.UploadTask

class UploadPDF : AppCompatActivity() {
    lateinit var upload_btn : Button;
    lateinit var pdf_name : EditText
    lateinit var storageReference: StorageReference
    lateinit var databaseReference: DatabaseReference
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_upload_pdf)
        upload_btn = findViewById(R.id.upload_btn)
        pdf_name = findViewById(R.id.filename)

        //Database part
        storageReference = FirebaseStorage.getInstance().getReference()
        databaseReference = FirebaseDatabase.getInstance().getReference("Uploads")

        upload_btn.setOnClickListener(object : View.OnClickListener{
            override fun onClick(v: View?) {
                selectFiles()
            }
        })
    }

    private fun selectFiles() {
        val intent : Intent = Intent()
        intent.setType("application/pdf")
        intent.setAction(Intent.ACTION_GET_CONTENT)
        startActivityForResult(Intent.createChooser(intent,"Select PDF files"),1)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if(requestCode == 1 && resultCode == RESULT_OK && data != null && data.data != null)
        {
            UploadFiles(data.data)
        }

    }
    private fun backToMain()
    {
        val intent : Intent = Intent(this@UploadPDF,MainActivity::class.java)
        startActivity(intent)
    }

    private fun UploadFiles(data : Uri?)
    {
        var progressDialog : ProgressDialog = ProgressDialog(this)
        progressDialog.setTitle("Uploading..")
        progressDialog.show()
        val reference : StorageReference = storageReference.child("Uploads/"+pdf_name.text.toString()+".pdf")
        if (data != null) {
            reference.putFile(data).addOnSuccessListener(object : OnSuccessListener<UploadTask.TaskSnapshot>{
                override fun onSuccess(taskSnapshot: UploadTask.TaskSnapshot?)
                {
                    val uriTask : Task<Uri> = taskSnapshot?.storage?.downloadUrl as Task<Uri>
                    while (!uriTask.isComplete);
                    val url : Uri = uriTask.getResult()
                    val pdfClass : pdfClass = pdfClass(pdf_name.text.toString(),url.toString())
                    databaseReference.child(databaseReference.push().key.toString()).setValue(pdfClass)
                    Toast.makeText(this@UploadPDF, "Successfully uploaded", Toast.LENGTH_SHORT).show()
                    progressDialog.dismiss()
                    backToMain()
                }

            }).addOnProgressListener(object : OnProgressListener<UploadTask.TaskSnapshot>{
                override fun onProgress(snapshot: UploadTask.TaskSnapshot) {
                    if (snapshot != null) run {
                        var progress: Long =
                            (100 * snapshot.bytesTransferred) / snapshot.totalByteCount
                        progressDialog.setMessage("Uploaded : " + progress+"%")
                    }
                }
            });
        }

        }
    }
