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
import com.lofod.chepuha.model.Player
import com.lofod.chepuha.model.request.StartGameRequest
import com.lofod.chepuha.retrofit.API
import com.lofod.chepuha.retrofit.RetrofitClient
import com.pranavpandey.android.dynamic.toasts.DynamicToast
import retrofit2.create
import java.util.*

class MenuFragment : Fragment() {

    private var _binding: FragmentMenuBinding? = null
    private val binding get() = _binding!!

    private var userName: String = ""
    private var gameCode: String = ""

    private val api = RetrofitClient.getClient().create(API::class.java)

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

            // TODO проверить отображение эмодзи
            if (userName.isEmpty()) {
                DynamicToast.makeError(requireContext(), "А как вас мама называет? \uD83D\uDC36").show()
                return@setOnClickListener
            }

            with(requireActivity() as MainActivity) {
                gameCode = binding.inputGameCode.toString()
                userName = binding.username.toString()
            }

            api.createGame(StartGameRequest(Player(userName, UUID.randomUUID())))
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