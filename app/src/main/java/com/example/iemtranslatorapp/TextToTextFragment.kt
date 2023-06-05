package com.example.iemtranslatorapp

import android.content.ClipboardManager
import android.content.Context
import android.os.Bundle
import android.speech.tts.TextToSpeech
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.core.content.ContextCompat.getSystemService
import androidx.core.content.getSystemService
import androidx.fragment.app.Fragment
import com.example.iemtranslatorapp.databinding.FragmentTextToTextBinding
import com.google.mlkit.nl.translate.TranslateLanguage
import com.google.mlkit.nl.translate.Translation
import com.google.mlkit.nl.translate.Translator
import com.google.mlkit.nl.translate.TranslatorOptions
import java.util.*

class TextToTextFragment : Fragment() {

    private var _binding: FragmentTextToTextBinding? = null
    private val binding get() = _binding!!
    lateinit var translator : Translator
    lateinit var cm : ClipboardManager
    lateinit var mTTS1 : TextToSpeech
    lateinit var mTTS2 : TextToSpeech

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        _binding = FragmentTextToTextBinding.inflate(inflater, container, false)

        val array = arrayOf("English","Hindi", "Chinese")
        val adapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, array)
            cm = requireContext().getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager

        binding.spinnerLang1.adapter = adapter
        binding.spinnerLang1.onItemSelectedListener = object :
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
                mTTS1.language = Locale.forLanguageTag(lang(binding.et1.text.toString()))
                mTTS2.language = Locale.forLanguageTag(lang(binding.et2.text.toString()))
            }

            override fun onNothingSelected(parent: AdapterView<*>) {
                // write code to perform some action
            }
        }

        mTTS1 = TextToSpeech(context) { }
        mTTS2 = TextToSpeech(context) { }
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
            }

            override fun onNothingSelected(parent: AdapterView<*>) {
                // write code to perform some action
            }
        }

        binding.et1.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {

            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {

            }

            override fun afterTextChanged(s: Editable?) {
                translator.translate(s.toString()).addOnSuccessListener {
                    binding.et2.setText(it)
                }
            }

        })

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

    private fun translate() {
        translator.translate(binding.et1.text.toString()).addOnSuccessListener {
            binding.et2.setText(it)
        }
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
        return ""
    }
}