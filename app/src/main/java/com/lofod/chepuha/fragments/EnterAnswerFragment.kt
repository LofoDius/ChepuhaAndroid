package com.lofod.chepuha.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.widget.doAfterTextChanged
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import com.gmail.bishoybasily.stomp.lib.StompClient
import com.lofod.chepuha.MainActivity
import com.lofod.chepuha.R
import com.lofod.chepuha.StoreManager
import com.lofod.chepuha.databinding.FragmentEnterAnswerBinding
import com.lofod.chepuha.model.Answer
import com.lofod.chepuha.model.request.AnswerRequest
import com.lofod.chepuha.model.response.BaseResponse
import com.lofod.chepuha.model.response.QuestionResponse
import com.lofod.chepuha.retrofit.API
import com.lofod.chepuha.retrofit.RetrofitClient
import com.pranavpandey.android.dynamic.toasts.DynamicToast
import io.reactivex.disposables.Disposable
import kotlinx.coroutines.*
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.json.Json
import okhttp3.OkHttpClient
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.util.concurrent.TimeUnit

@ExperimentalSerializationApi
class EnterAnswerFragment : Fragment() {

    private var _binding: FragmentEnterAnswerBinding? = null
    private val binding get() = _binding!!

    private var questionNumber = 0

    private lateinit var stompConnection: Disposable

    private var isWaitingNextQuestion = false

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentEnterAnswerBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        binding.sendAnswer.isEnabled = false
        binding.sendAnswer.setOnClickListener {
            it.isEnabled = false
            isWaitingNextQuestion = true

            val answer = binding.inputAnswer.text.toString()
            if (answer.isEmpty()) {
                DynamicToast.makeWarning(
                    requireContext(),
                    "А где смешнявка? \uD83D\uDE21",
                    Toast.LENGTH_SHORT
                ).show()
                return@setOnClickListener
            }

            val store = StoreManager.getInstance()
            RetrofitClient.getClient().create(API::class.java)
                .sendMessage(
                    AnswerRequest(
                        Answer(questionNumber, answer, store.player.name),
                        store.gameCode
                    )
                )
                .enqueue(object : Callback<BaseResponse> {
                    override fun onResponse(
                        call: Call<BaseResponse>,
                        response: Response<BaseResponse>
                    ) {
                        val mainActivity = activity ?: return

                        if (response.body()?.code == 0) {
                            runBlocking { delay(100) }
                            if (isWaitingNextQuestion) {
                                binding.inputAnswer.visibility = View.INVISIBLE
                                binding.sendAnswer.visibility = View.INVISIBLE
                                binding.question.text = """Ждем ответики других игроков 💤"""
                            }
                        } else {
                            DynamicToast.makeWarning(
                                requireContext(),
                                "Ошибочка на серве, хз шо делать, \nно можно еще раз отправить попробовать"
                            ).show()

                        }
                        mainActivity.runOnUiThread {
                            binding.sendAnswer.isEnabled = true
                        }
                    }

                    override fun onFailure(call: Call<BaseResponse>, t: Throwable) {
                        requireActivity().runOnUiThread {
                            DynamicToast.makeWarning(
                                requireContext(),
                                "Ну тут проблемка какая-то образовалась"
                            ).show()
                            binding.sendAnswer.isEnabled = true
                        }
                    }
                })
        }

        binding.inputAnswer.doAfterTextChanged {
            binding.sendAnswer.isEnabled = it.toString().isNotEmpty()
        }
        lifecycleScope.launch { setupWebSocketConnection() }
    }

    private suspend fun setupWebSocketConnection() {
        withContext(Dispatchers.IO) {
            val httpClient = OkHttpClient.Builder()
                .readTimeout(10, TimeUnit.SECONDS)
                .writeTimeout(10, TimeUnit.SECONDS)
                .connectTimeout(10, TimeUnit.SECONDS)
                .build()

            val client =
                StompClient(httpClient, 100L).apply { url = getString(R.string.ws_url_connections) }

            stompConnection = client.connect().subscribe()

            val store = StoreManager.getInstance()
            if (client.join(getString(R.string.topic_question) + store.gameCode).subscribe {
                    isWaitingNextQuestion = false
                    val response = Json.decodeFromString<QuestionResponse>(it)

                    val activity = requireActivity() as MainActivity
                    activity.runOnUiThread {
                        if (response.question == "game ended") {
                            activity.openStoryFragment()
                            return@runOnUiThread
                        }

                        binding.question.text = response.question
                        binding.inputAnswer.visibility = View.VISIBLE
                        binding.sendAnswer.visibility = View.VISIBLE
                        questionNumber = response.questionNumber
                        binding.inputAnswer.text.clear()
                    }
                }.isDisposed) {
                DynamicToast.makeError(requireContext(), "Вебсокет сыбался!").show()
                setupWebSocketConnection()
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        stompConnection.dispose()
    }

    companion object {
        @JvmStatic
        fun newInstance() = EnterAnswerFragment()
    }

}