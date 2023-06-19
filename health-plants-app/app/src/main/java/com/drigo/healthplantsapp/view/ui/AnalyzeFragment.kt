package com.drigo.healthplantsapp.view.ui

import android.app.Activity
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.Matrix
import android.os.Bundle
import android.provider.MediaStore
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.drigo.healthplantsapp.databinding.FragmentAnalyzeBinding
import com.drigo.healthplantsapp.shared.ClassifierImage

class AnalyzeFragment : Fragment() {
    private val mCameraRequestCode = 0
    private lateinit var binding: FragmentAnalyzeBinding
    private lateinit var mClassifier: ClassifierImage
    private lateinit var mBitmap: Bitmap
    private val mInputSize = 200 //224
    private val mModelPath = "model.tflite"
    private val mLabelPath = "labels.txt"

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentAnalyzeBinding.inflate(inflater, container, false)
        binding.lifecycleOwner = viewLifecycleOwner

        mClassifier = ClassifierImage(resources.assets, mModelPath, mLabelPath, mInputSize)
        binding.fbCam.setOnClickListener {
            val callCameraIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
            startActivityForResult(callCameraIntent, mCameraRequestCode)
        }

        return binding.root
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if(requestCode == mCameraRequestCode){
            if(resultCode == Activity.RESULT_OK && data != null) {
                mBitmap = data.extras!!.get("data") as Bitmap
                mBitmap = scaleImage(mBitmap)
                binding.mPhotoImageView.setImageBitmap(mBitmap)
                val model_output = mClassifier.recognizeImage(scaleImage(mBitmap)).firstOrNull()
                binding.mResultTextView.text = model_output?.title
                binding.mResultTextView2.text = model_output?.confidence.toString()
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

}