package com.lofod.chepuha.fragments

import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.widget.doAfterTextChanged
import androidx.fragment.app.Fragment
import com.lofod.chepuha.MainActivity
import com.lofod.chepuha.R
import com.lofod.chepuha.StoreManager
import com.lofod.chepuha.databinding.FragmentMenuBinding
import com.lofod.chepuha.model.Player
import com.lofod.chepuha.model.request.ConnectToGameRequest
import com.lofod.chepuha.model.request.StartGameRequest
import com.lofod.chepuha.model.response.BaseResponse
import com.lofod.chepuha.model.response.StartGameResponse
import com.lofod.chepuha.retrofit.API
import com.lofod.chepuha.retrofit.RetrofitClient
import com.pranavpandey.android.dynamic.toasts.DynamicToast
import kotlinx.serialization.ExperimentalSerializationApi
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.util.*

@ExperimentalSerializationApi
class MenuFragment : Fragment() {

    private var _binding: FragmentMenuBinding? = null
    private val binding get() = _binding!!

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
            val userName = binding.username.text.toString()

            // TODO проверить отображение эмодзи
            if (userName.isEmpty()) {
                DynamicToast.makeError(requireContext(), "А как вас мама называет? \uD83D\uDC36").show()
                return@setOnClickListener
            }

            val store = StoreManager.getInstance()
            with(store) {
                this.userName = binding.username.toString()
                player = Player(this.userName, UUID.randomUUID())
            }

            val activity = requireActivity() as MainActivity
            api.createGame(StartGameRequest(store.player)).enqueue(object : Callback<StartGameResponse> {
                override fun onResponse(call: Call<StartGameResponse>, response: Response<StartGameResponse>) {
                    if (response.body()!!.code == 0) {
                        store.gameCode = response.body()!!.gameCode
                        activity.openWaitingRoomFragment()
                    } else
                        DynamicToast.makeWarning(requireContext(), "Сервер не хочет запускать игру \uD83D\uDE14").show()
                }

                override fun onFailure(call: Call<StartGameResponse>, t: Throwable) {
                    DynamicToast.makeWarning(requireContext(), "Почему-то не получилось создать игру \uD83D\uDE14")
                        .show()
                }
            })
        }

        binding.inputGameCode.doAfterTextChanged {
            if (it.toString().length == 3) {
                val gameCode = it.toString()
                val userName = binding.username.text.toString()

                if (userName.isEmpty()) {
                    DynamicToast.makeError(requireContext(), "А как вас мама называет? \uD83D\uDC36").show()
                    return@doAfterTextChanged
                }

                with(binding.inputGameCode) {
                    isEnabled = false
                    setTextColor(this@MenuFragment.requireContext().getColor(R.color.input_code_disabled))
                    setLineColor(this@MenuFragment.requireContext().getColor(R.color.input_code_disabled))
                }

                val store = StoreManager.getInstance()
                with(store) {
                    this.gameCode = gameCode
                    player = Player(userName, UUID.randomUUID())
                }

                val activity = requireActivity() as MainActivity
                api.connectToGame(ConnectToGameRequest(gameCode, store.player))
                    .enqueue(object : Callback<BaseResponse> {
                        override fun onResponse(call: Call<BaseResponse>, response: Response<BaseResponse>) {
                            if (response.body()!!.code == 0)
                                activity.openWaitingRoomFragment()
                            else {
                                DynamicToast.makeWarning(
                                    requireContext(),
                                    "Есть мнение, шо этот код никому не нужен \uD83D\uDD95"
                                ).show()
                                with(binding.inputGameCode) {
                                    isEnabled = true
                                    setTextColor(Color.BLACK)
                                    setLineColor(Color.BLACK)
                                }
                            }
                        }

                        override fun onFailure(call: Call<BaseResponse>, t: Throwable) {
                            DynamicToast.makeWarning(
                                requireContext(),
                                "Шото запросик наш заблудился, А СЕРВЕР ТАК ДАЛЕКО \uD83D\uDE28"
                            ).show()
                        }

                    })
            }
        }
    }

    companion object {
        @JvmStatic
        fun newInstance() = MenuFragment()
    }

}