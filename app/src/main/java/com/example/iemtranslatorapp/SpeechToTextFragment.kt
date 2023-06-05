package com.example.iemtranslatorapp

import android.Manifest
import android.app.Activity
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.speech.tts.TextToSpeech
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.example.iemtranslatorapp.databinding.FragmentSpeechToTextBinding
import com.github.squti.androidwaverecorder.WaveRecorder
import com.google.mlkit.nl.translate.TranslateLanguage
import com.google.mlkit.nl.translate.Translation
import com.google.mlkit.nl.translate.Translator
import com.google.mlkit.nl.translate.TranslatorOptions
import org.vosk.Model
import org.vosk.Recognizer
import org.vosk.android.RecognitionListener
import org.vosk.android.SpeechStreamService
import org.vosk.android.StorageService
import java.io.File
import java.io.FileNotFoundException
import java.io.IOException
import java.io.InputStream
import java.util.*

class SpeechToTextFragment : Fragment(), RecognitionListener {
    private var _binding: FragmentSpeechToTextBinding? = null
    private val binding get() = _binding!!

    lateinit var translator : Translator
    lateinit var mTTS1 : TextToSpeech
    lateinit var mTTS2 : TextToSpeech
    val isRecording = arrayOf(false)

    var filePath: String? = null
    var waveRecorder:WaveRecorder? = null
    private lateinit var model: Model
    lateinit var cm : ClipboardManager

    val PERMISSIONS_REQUEST_RECORD_AUDIO = 200
    private var speechStreamService: SpeechStreamService? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        _binding = FragmentSpeechToTextBinding.inflate(inflater, container, false)

        val array = arrayOf("English", "Chinese","Hindi", "Japanese", "Korean")
        val adapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, array)
        cm = requireContext().getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
        binding.spinnerLang1.adapter = adapter
        binding.spinnerLang1.onItemSelectedListener = object :
            AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                parent: AdapterView<*>,
                view: View, position: Int, id: Long
            ) {
                mTTS1 = TextToSpeech(context) { }
                mTTS2 = TextToSpeech(context) { }
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

                // Check if user has given permission to record audio, init the model after permission is granted
                val permissionCheck = ContextCompat.checkSelfPermission(
                    context!!.applicationContext,
                    Manifest.permission.RECORD_AUDIO
                )
                if (permissionCheck != PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions(
                        requireActivity(),
                        arrayOf(Manifest.permission.RECORD_AUDIO),
                        PERMISSIONS_REQUEST_RECORD_AUDIO
                    )
                } else {
                    initModel(model(binding.spinnerLang1.selectedItem.toString()))
                    Toast.makeText(context, model(binding.spinnerLang1.selectedItem.toString()), Toast.LENGTH_SHORT).show()
                }
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
                val translatorOption = TranslatorOptions.Builder()
                    .setSourceLanguage(lang(binding.spinnerLang1.selectedItem.toString()))
                    .setTargetLanguage(lang(binding.spinnerLang2.selectedItem.toString()))
                    .build()

                translator = Translation.getClient(translatorOption)

                translator.downloadModelIfNeeded().addOnSuccessListener {

                }
                translate()
                mTTS1.language = Locale.forLanguageTag(lang(binding.et1.text.toString()))
                mTTS2.language = Locale.forLanguageTag(lang(binding.et2.text.toString()))


            }

            override fun onNothingSelected(parent: AdapterView<*>) {
                // write code to perform some action
            }
        }

        binding.recordButton.setOnClickListener{
            if (!isRecording[0]) {

                isRecording[0] = true
                try {
                    startRecordingWav()
                    binding.recordButton.setImageResource(R.drawable.stop_record_button)
                } catch (e: Exception) {
                    e.printStackTrace()
                    Log.d("TAG", e.message!!)
                }
            } else {

                try {
                    stopRecordingWav()
                    isRecording[0] = false
                    binding.recordButton.setImageResource(R.drawable.record_button)
                } catch (e: IOException) {
                    Toast.makeText(context, e.message, Toast.LENGTH_SHORT).show()
                    Log.d("hello", e.message!!)
                }
            }
        }

        binding.importButton.setOnClickListener{
            recognizeFile()
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

    fun lang(lang: String): String {

        when (lang) {
            "English" -> {
                return TranslateLanguage.ENGLISH
            }
            "Chinese" -> {
                return TranslateLanguage.CHINESE
            }
            "Hindi" -> {
                return TranslateLanguage.HINDI
            }
            "Japanese" -> {
                return TranslateLanguage.JAPANESE
            }
            "Korean" -> {
                return TranslateLanguage.KOREAN
            }
            else -> return ""
        }
    }

    fun model(lang : String) : String{
        when (lang) {
            "English" -> {
                return "en-US"
            }
            "Chinese" -> {
                return "zh-CN"
            }
            "Hindi" -> {
                return "hi-IN"
            }
            "Japanese" -> {
                return "ja-JP"
            }
            "Korean" -> {
                return "kk-KZ"
            }
            else -> return ""
        }
    }

    private fun translate() {
        translator.translate(binding.et1.text.toString()).addOnSuccessListener {
            binding.et2.setText(it)
        }
    }

    @Throws(IOException::class)
    private fun startRecordingWav() {
        val time = Calendar.getInstance().timeInMillis
        filePath =
            Environment.getExternalStorageDirectory().toString() + "/Documents/" + time + ".wav"
        val file: File = File(filePath)
        Log.d("helloCan", file.toURI().toString())
        waveRecorder = WaveRecorder(filePath!!)
        waveRecorder!!.startRecording()
    }

    @Throws(IOException::class)
    private fun stopRecordingWav() {
        Toast.makeText(context, "Audio saved. Converting to text now", Toast.LENGTH_SHORT).show()
        waveRecorder!!.stopRecording()

        val rec = Recognizer(model, 16000f)
        var `is`: InputStream? = null
        `is` = requireContext().contentResolver.openInputStream(
            Uri.parse(
                File(filePath!!).toURI().toString()
            )
        )

        speechStreamService = SpeechStreamService(rec, `is`, 16000F)
        speechStreamService!!.start(this)
    }

    private fun initModel(model : String) {
        StorageService.unpack(context, model, "model",
            { model ->
                Log.d("unpack_model", "model.toString()")
                this.model = model
                Toast.makeText(context, "Hello", Toast.LENGTH_SHORT).show()
            },
            { exception: IOException ->
                Toast.makeText(context, exception.message, Toast.LENGTH_SHORT).show()
            })
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String?>, grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == PERMISSIONS_REQUEST_RECORD_AUDIO) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Recognizer initialization is a time-consuming and it involves IO,
                // so we execute it in async task
                initModel(model(binding.spinnerLang1.selectedItem.toString()))
            } else {

            }
        }

        initModel(model(binding.spinnerLang1.selectedItem.toString()))
    }

    override fun onPartialResult(hypothesis: String?) {
//        binding.et1.setText(
//            hypothesis!!.replace("text", "")
//                .replace("{", "")
//                .replace("}", "")
//                .replace(":", "")
//                .replace("partial", "")
//                .replace("\"", "")
//        )
    }

    override fun onResult(hypothesis: String?) {
        binding.et1.append(
            hypothesis!!.replace("text", "")
                .replace("{", "")
                .replace("}", "")
                .replace(":", "")
                .replace("partial", "")
                .replace("\"", "")
        )
    }

    override fun onFinalResult(hypothesis: String?) {
        binding.et1.append(
            hypothesis!!.replace("text", "")
                .replace("{", "")
                .replace("}", "")
                .replace(":", "")
                .replace("partial", "")
                .replace("\"", "")
        )

        translate()
    }

    override fun onError(exception: java.lang.Exception?) {
        Toast.makeText(context, exception?.message, Toast.LENGTH_SHORT).show()
    }

    override fun onTimeout() {
        Toast.makeText(context, "hypothesis", Toast.LENGTH_SHORT).show()
    }

    @Throws(IOException::class)
    private fun recognizeFile() {
        if (speechStreamService != null) {
            speechStreamService!!.stop()
            speechStreamService = null
        } else {
            val audio = Intent()
            audio.type = "audio/*"
            audio.action = Intent.ACTION_OPEN_DOCUMENT
            startActivityForResult(Intent.createChooser(audio, "Select Audio"), PICK_AUDIO)

//                if (ais.skip(44) != 44) throw new IOException("File too short");

//                speechStreamService = new SpeechStreamService(rec, ais, 16000);
//                speechStreamService.start(this);
        }
    }

    private val PICK_AUDIO = 1
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == PICK_AUDIO && resultCode == Activity.RESULT_OK) {
            // Audio is Picked in format of URI
            assert(data != null)
            val AudioUri = data!!.data
            Log.d("hello", AudioUri.toString())
            var ais: InputStream
            //            File aisFile = new File(String.valueOf(AudioUri));

            val rec = Recognizer(model, 16000f)
            //
            Log.d("hello", model.toString())
            var `is`: InputStream? = null
            try {
                `is` = context?.applicationContext?.contentResolver?.openInputStream(AudioUri!!)
                Log.d("hello", AudioUri.toString())
            } catch (e: FileNotFoundException) {
                e.printStackTrace()
            }
            try {
                assert(`is` != null)
                if (`is`!!.skip(44) != 44L) try {
                    throw IOException("File too short")
                } catch (e: IOException) {
                    e.printStackTrace()
                }
            } catch (e: IOException) {
                e.printStackTrace()
            }
            speechStreamService = SpeechStreamService(rec, `is`, 16000F)
            speechStreamService!!.start(this)
        }

    }

}