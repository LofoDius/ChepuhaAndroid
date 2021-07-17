package com.lofod.chepuha.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.widget.doAfterTextChanged
import androidx.fragment.app.Fragment
import com.lofod.chepuha.MainActivity
import com.lofod.chepuha.R
import com.lofod.chepuha.databinding.FragmentMenuBinding

class MenuFragment : Fragment() {

    private var _binding: FragmentMenuBinding? = null
    private val binding get() = _binding!!

    private var userName: String = ""
    private var gameCode: String = ""

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentMenuBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        binding.createGame.setOnClickListener {
            userName = binding.username.text.toString()

            with(requireActivity() as MainActivity) {
                gameCode = binding.inputGameCode.toString()
                userName = binding.username.toString()
            }
            //TODO отправка запроса на бэк
        }

        binding.inputGameCode.doAfterTextChanged {
            if (it.toString().length == 3) {
                gameCode = it.toString()

                with(binding.inputGameCode) {
                    isEnabled = false
                    setTextColor(this@MenuFragment.requireContext().getColor(R.color.input_code_disabled))
                    setLineColor(this@MenuFragment.requireContext().getColor(R.color.input_code_disabled))
                }

                //TODO запрос
            }
        }
    }

    companion object {
        @JvmStatic
        fun newInstance() = MenuFragment()
    }

}