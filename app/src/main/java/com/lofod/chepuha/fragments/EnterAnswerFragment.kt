package com.lofod.chepuha.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.lofod.chepuha.R
import com.lofod.chepuha.databinding.FragmentEnterAnswerBinding

class EnterAnswerFragment : Fragment() {

    private var _binding: FragmentEnterAnswerBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentEnterAnswerBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        binding.sendAnswer.setOnClickListener {
            if (binding.inputAnswer.toString().isEmpty()) {
                Toast.makeText(requireContext(), "А где смешнявка?", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            // TODO запросики
        }
    }

    companion object {
        @JvmStatic
        fun newInstance() = EnterAnswerFragment()
    }

}