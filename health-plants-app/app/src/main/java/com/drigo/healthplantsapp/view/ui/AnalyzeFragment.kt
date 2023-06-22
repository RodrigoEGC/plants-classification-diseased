package com.drigo.healthplantsapp.view.ui

import android.app.Activity
import android.app.Dialog
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.Color
import android.graphics.Matrix
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.provider.MediaStore
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.drigo.healthplantsapp.R
import com.drigo.healthplantsapp.databinding.FragmentAnalyzeBinding
import com.drigo.healthplantsapp.shared.ClassifierImage
import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject
import java.io.IOException
import java.io.InputStream
import java.nio.charset.Charset


class AnalyzeFragment : Fragment() {
    private val mCameraRequestCode = 0
    private lateinit var binding: FragmentAnalyzeBinding
    private lateinit var mClassifier: ClassifierImage
    private lateinit var mBitmap: Bitmap
    lateinit var myDialog: Dialog
    private val mInputSize = 200 //224
    private val mModelPath = "model.tflite"
    private val mLabelPath = "labels.txt"
    private var pname:String? = ""
    private var pCite:String? = ""
    private var pRemidy:String? = ""

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentAnalyzeBinding.inflate(inflater, container, false)
        binding.lifecycleOwner = viewLifecycleOwner

        mClassifier = ClassifierImage(resources.assets, mModelPath, mLabelPath, mInputSize)
        myDialog= Dialog(requireActivity())

        binding.fbCam.setOnClickListener {
            val callCameraIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
            startActivityForResult(callCameraIntent, mCameraRequestCode)
        }

        binding.fbInfo.setOnClickListener {
            customDialog()
        }

        return binding.root
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if(requestCode == mCameraRequestCode){
            if(resultCode == Activity.RESULT_OK && data != null) {
                mBitmap = data.extras!!.get("data") as Bitmap
                mBitmap = scaleImage(mBitmap)
                binding.mPhotoImageView.setImageBitmap(mBitmap)
                val modelOutput = mClassifier.recognizeImage(scaleImage(mBitmap)).firstOrNull()
                binding.mResultTextView.text = modelOutput?.title
                binding.mResultTextView2.text = modelOutput?.confidence.toString()
                if (binding.mResultTextView.text.toString().isNotEmpty() && binding.mResultTextView2.text.toString().isNotEmpty())
                    binding.fbInfo.visibility = View.VISIBLE
            }
        }
    }

    private fun scaleImage(bitmap: Bitmap?): Bitmap {
        val width = bitmap!!.width
        val height = bitmap.height
        val scaledWidth = mInputSize.toFloat() / width
        val scaledHeight = mInputSize.toFloat() / height
        val matrix = Matrix()
        matrix.postScale(scaledWidth, scaledHeight)
        return Bitmap.createBitmap(bitmap, 0, 0, width, height, matrix, true)
    }

    private fun customDialog() {
        myDialog.setContentView(R.layout.detail_dialog)
        myDialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        myDialog.show()


        val titlePlant = myDialog.findViewById(R.id.pltd_name) as TextView
        val cite = myDialog.findViewById(R.id.symptoms) as TextView
        val remidy = myDialog.findViewById(R.id.management) as TextView

        titlePlant.text  = binding.mResultTextView.text

        val sname = titlePlant.text.toString()

        try
        {
            val obj= JSONObject(loadJSONFromAsset())
            val jArray= JSONArray()
            jArray.put(obj)
            for (i in 0 until jArray.length()){
                val plant = jArray.getJSONObject(i)
                val keys = plant.keys()
                while (keys.hasNext()) {
                    val key = keys.next()
                    pname = key.replace("_", "").toLowerCase()
                    val snameReplace = sname.replace(" ", "")
                    if (snameReplace == pname) {
                        val desiredObject = plant.getJSONObject(key)
                        pCite = desiredObject.getString("cite")
                        pRemidy= desiredObject.getString("remidy")
                    }
                }
                cite.text="$pCite"
                remidy.text="$pRemidy"
            }

        }
        catch (e: IOException) {
            e.printStackTrace()
        }
    }

    private fun loadJSONFromAsset(): String? {
        var json: String? = null
        try {
            var inputStream: InputStream = resources.assets.open("remidies.json")
            val size=inputStream.available()
            val buffer=ByteArray(size)
            val charset: Charset =Charsets.UTF_8
            inputStream.read(buffer)
            inputStream.close()
            json=String(buffer,charset)
        }
        catch (e:IOException){
            e.printStackTrace()
            return null
        }
        return json
    }

}