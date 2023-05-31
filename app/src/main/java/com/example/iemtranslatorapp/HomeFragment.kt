package com.example.iemtranslatorapp

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.iemtranslatorapp.databinding.FragmentHomeBinding


class HomeFragment : Fragment() {

    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        binding.textToTextTranslationButton.setOnClickListener{
            replaceFragment(TextToTextFragment())
        }
        binding.imageTranslationButton.setOnClickListener{
            replaceFragment(ImageToTextFragment())
        }
        binding.speechToTextTranslationButton.setOnClickListener{
            replaceFragment(SpeechToTextFragment())
        }
        return binding.root
    }

    private fun replaceFragment(fragment : Fragment){
        val fragmentTransaction = parentFragmentManager.beginTransaction()
        fragmentTransaction.replace(R.id.fragment_container, fragment)
        fragmentTransaction.addToBackStack(null);
        fragmentTransaction.commit();
    }
}