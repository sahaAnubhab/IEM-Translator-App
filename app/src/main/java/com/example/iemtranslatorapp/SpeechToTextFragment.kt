package com.example.iemtranslatorapp

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.iemtranslatorapp.databinding.FragmentSpeechToTextBinding
import com.example.iemtranslatorapp.databinding.FragmentTextToTextBinding

class SpeechToTextFragment : Fragment() {
    private var _binding: FragmentSpeechToTextBinding? = null
    private val binding get() = _binding!!
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        _binding = FragmentSpeechToTextBinding.inflate(inflater, container, false)



        return binding.root
    }

}