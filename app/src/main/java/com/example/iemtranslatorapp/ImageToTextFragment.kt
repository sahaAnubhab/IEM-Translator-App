package com.example.iemtranslatorapp

import android.Manifest.permission.CAMERA
import android.R
import android.app.Activity
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.os.Bundle
import android.provider.MediaStore
import android.speech.tts.TextToSpeech
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.example.iemtranslatorapp.databinding.FragmentImageToTextBinding
import com.example.iemtranslatorapp.model.DatabaseHelper
import com.google.mlkit.nl.translate.TranslateLanguage
import com.google.mlkit.nl.translate.Translation
import com.google.mlkit.nl.translate.Translator
import com.google.mlkit.nl.translate.TranslatorOptions
import com.google.mlkit.vision.text.TextRecognition
import com.google.mlkit.vision.text.TextRecognizer
import com.google.mlkit.vision.text.chinese.ChineseTextRecognizerOptions
import com.google.mlkit.vision.text.japanese.JapaneseTextRecognizerOptions
import com.google.mlkit.vision.text.korean.KoreanTextRecognizerOptions
import com.google.mlkit.vision.text.latin.TextRecognizerOptions
import java.util.*

class ImageToTextFragment : Fragment() {
    private var imageBitmap: Bitmap? = null


    private var _binding: FragmentImageToTextBinding? = null
    private val binding get() = _binding!!
    lateinit var mTTS1 : TextToSpeech
    lateinit var mTTS2 : TextToSpeech
    private lateinit var recognizer : TextRecognizer
    lateinit var translator : Translator
    lateinit var cm : ClipboardManager
    private lateinit var da : DatabaseHelper
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    private fun checkCameraPermission() : Boolean {
        val cameraPerms = ContextCompat.checkSelfPermission(requireContext(), CAMERA)
        return cameraPerms == PackageManager.PERMISSION_GRANTED
    }

    private fun requestPerms() {
        val PERMISSION_CODE = 200
        ActivityCompat.requestPermissions(requireActivity(), arrayOf(CAMERA), PERMISSION_CODE)
    }

    @Deprecated("Deprecated in Java")
    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if(grantResults.isNotEmpty()){
            if(grantResults[0]==PackageManager.PERMISSION_GRANTED){
                Toast.makeText(context, "Camera per granted", Toast.LENGTH_SHORT).show()
            }
        }
    }
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentImageToTextBinding.inflate(inflater, container, false)
        da= DatabaseHelper(requireContext())
        binding.camera.setOnClickListener{
            if(checkCameraPermission()) {
                imageFromCamera()
            }else{
                Toast.makeText(context, "Hello", Toast.LENGTH_SHORT).show()
                requestPerms()
            }
        }

        cm = requireContext().getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
        mTTS1 = TextToSpeech(context) { }
        mTTS2 = TextToSpeech(context) { }

        val array = arrayOf("English", "Chinese", "Japanese", "Korean")
        val adapter = ArrayAdapter(requireContext(), R.layout.simple_spinner_item, array)
        binding.spinnerLang1.adapter = adapter
        binding.spinnerLang1.onItemSelectedListener = object :
            AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                parent: AdapterView<*>,
                view: View, position: Int, id: Long
            ) {
                val translatorOption = TranslatorOptions.Builder()
                    .setSourceLanguage(lang(binding.spinnerLang1.selectedItem.toString()))
                    .setTargetLanguage(lang(binding.spinnerLang2.selectedItem.toString()))
                    .build()

                translator = Translation.getClient(translatorOption)

                translator.downloadModelIfNeeded().addOnSuccessListener {
//
                }
                mTTS1.language = Locale.forLanguageTag(lang(binding.et1.text.toString()))
                mTTS2.language = Locale.forLanguageTag(lang(binding.et2.text.toString()))

                Toast.makeText(context, lang(binding.et1.text.toString()), Toast.LENGTH_SHORT).show()
                initializeRecognizer(binding.spinnerLang1.selectedItem.toString())

            }

            override fun onNothingSelected(parent: AdapterView<*>) {
                // write code to perform some action
            }
        }

        binding.spinnerLang2.adapter = adapter
        binding.spinnerLang2.onItemSelectedListener = object :
            AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                parent: AdapterView<*>,
                view: View, position: Int, id: Long
            ) {
                val translatorOption =TranslatorOptions.Builder()
                    .setSourceLanguage(lang(binding.spinnerLang1.selectedItem.toString()))
                    .setTargetLanguage(lang(binding.spinnerLang2.selectedItem.toString()))
                    .build()

                translator = Translation.getClient(translatorOption)

                translator.downloadModelIfNeeded().addOnSuccessListener {

                }
                translate()
                mTTS1.language = Locale.forLanguageTag(lang(binding.et1.text.toString()))
                mTTS2.language = Locale.forLanguageTag(lang(binding.et2.text.toString()))

                Toast.makeText(context, lang(binding.et2.text.toString()), Toast.LENGTH_SHORT).show()

            }

            override fun onNothingSelected(parent: AdapterView<*>) {
                // write code to perform some action
            }
        }

        binding.gallery.setOnClickListener{
            val intent = Intent()
            intent.type = "image/*"
            intent.action = Intent.ACTION_GET_CONTENT
            startActivityForResult(Intent.createChooser(intent, "Select Picture"), PICK_IMAGE);
        }

        binding.copyButton.setOnClickListener{
            cm.text = binding.et2.text.toString()
            Toast.makeText(context, "Text copied to clipboard.", Toast.LENGTH_SHORT).show()
        }
        binding.copyButton1.setOnClickListener{
            cm.text = binding.et1.text.toString()
            Toast.makeText(context, "Text copied to clipboard.", Toast.LENGTH_SHORT).show()
        }
        binding.deleteButton.setOnClickListener{
            binding.et1.setText("")
            binding.et2.setText("")
        }

        binding.deleteButton1.setOnClickListener{
            binding.et1.setText("")
            binding.et2.setText("")
        }

        binding.speakerButton1.setOnClickListener{
            mTTS1.speak(binding.et1.text.toString(), TextToSpeech.QUEUE_FLUSH, null)
        }
        binding.speakerButton.setOnClickListener{
            mTTS1.speak(binding.et2.text.toString(), TextToSpeech.QUEUE_FLUSH, null)
        }
        return binding.root
    }

    val PICK_IMAGE = 1

    private fun initializeRecognizer(lang: String) {
        if(lang == "English"){
            recognizer = TextRecognition.getClient(TextRecognizerOptions.DEFAULT_OPTIONS)
        }else if(lang == "Chinese"){
            recognizer = TextRecognition.getClient(ChineseTextRecognizerOptions.Builder().build())
        }else if(lang == "Japanese"){
            recognizer = TextRecognition.getClient(JapaneseTextRecognizerOptions.Builder().build())
        }
        else if (lang == "Korean"){
            recognizer = TextRecognition.getClient(KoreanTextRecognizerOptions.Builder().build())
        }
    }

    private fun translate() {
        translator.translate(binding.et1.text.toString()).addOnSuccessListener {
            binding.et2.setText(it)
        }
    }

    val CAMERA_REQUEST_CODE = 0

    private fun imageFromCamera() {
        val callCameraIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        if (callCameraIntent.resolveActivity(requireContext().packageManager) != null) {
            startActivityForResult(callCameraIntent, CAMERA_REQUEST_CODE)
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        when(requestCode) {
            CAMERA_REQUEST_CODE -> {
                if (resultCode == Activity.RESULT_OK && data != null) {
                    imageBitmap = data.extras?.get("data") as Bitmap
                    recognizeText(imageBitmap!!)
                }
            }
            PICK_IMAGE -> {
                if(resultCode == Activity.RESULT_OK && data != null){
                    val uri= data.data
                    val bitmap = MediaStore.Images.Media.getBitmap(context?.contentResolver, uri)
                    recognizeText(bitmap)
                }
            }
            else -> {
                Toast.makeText(context, "Unrecognized request code", Toast.LENGTH_SHORT).show()
            }
        }
    }
    private fun recognizeText(image: Bitmap) {

        // [START run_detector]
        val result = recognizer.process(image,0)
            .addOnSuccessListener { visionText ->
                // Task completed successfully
                // [START_EXCLUDE]
                // [START get_text]
                for (block in visionText.textBlocks) {
                    val boundingBox = block.boundingBox
                    val cornerPoints = block.cornerPoints
                    val text = block.text
                    binding.et1.append(text)
                    translator.translate(binding.et1.text.toString()).addOnSuccessListener {
                        binding.et2.setText(it)
                        val tsLong = System.currentTimeMillis() / 1000
                        val ts = tsLong.toString()
                        da.onAdd(it,binding.et1.text.toString(),ts)
                        Toast.makeText(context, "small", Toast.LENGTH_SHORT).show()
                    }
                }
                // [END get_text]
                // [END_EXCLUDE]
            }
            .addOnFailureListener { e ->
                // Task failed with an exception
                // ...
            }
        // [END run_detector]
    }


    fun lang(lang: String): String {

        if(lang == "English"){
            return TranslateLanguage.ENGLISH
        }
        else if(lang == "Chinese"){
            return TranslateLanguage.CHINESE
        }else if(lang == "Hindi"){
            return TranslateLanguage.HINDI
        }
        else if(lang == "Japanese"){
            return TranslateLanguage.JAPANESE
        }
        else if (lang == "Korean"){
            return TranslateLanguage.KOREAN
        }
        return ""
    }
}