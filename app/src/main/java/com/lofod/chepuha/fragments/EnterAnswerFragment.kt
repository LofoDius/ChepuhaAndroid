package com.lofod.chepuha.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import com.lofod.chepuha.MainActivity
import com.lofod.chepuha.R
import com.lofod.chepuha.databinding.FragmentEnterAnswerBinding
import com.lofod.chepuha.model.Answer
import com.lofod.chepuha.model.request.AnswerRequest
import com.lofod.chepuha.model.response.BaseResponse
import com.lofod.chepuha.model.response.QuestionResponse
import com.lofod.chepuha.retrofit.API
import com.lofod.chepuha.retrofit.RetrofitClient
import com.pranavpandey.android.dynamic.toasts.DynamicToast
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kotlinx.coroutines.flow.collect
import org.hildan.krossbow.stomp.StompClient
import org.hildan.krossbow.stomp.conversions.kxserialization.subscribe
import org.hildan.krossbow.stomp.conversions.kxserialization.withJsonConversions
import org.hildan.krossbow.stomp.use
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.create

class EnterAnswerFragment : Fragment() {

    private var _binding: FragmentEnterAnswerBinding? = null
    private val binding get() = _binding!!

    private var questionNumber = 0

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
            val answer = binding.inputAnswer.text.toString()
            val activity = requireActivity() as MainActivity
            if (answer.isEmpty()) {
                Toast.makeText(requireContext(), "А где смешнявка?", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            RetrofitClient.getClient().create(API::class.java)
                .sendMessage(AnswerRequest(Answer(questionNumber, answer, activity.player.name), activity.gameCode))
                .enqueue(object : Callback<BaseResponse> {
                    override fun onResponse(call: Call<BaseResponse>, response: Response<BaseResponse>) {
                        if (response.body()?.code == 0) {
                            binding.inputAnswer.visibility = View.INVISIBLE
                            binding.sendAnswer.visibility = View.INVISIBLE
                            binding.question.text = """Ждем ответики других игроков 💤"""
                        } else {
                            DynamicToast.makeWarning(
                                requireContext(),
                                "Ошибочка на серве, хз шо делать, \nно можно еще раз отправить попробовать"
                            ).show()
                        }
                    }

                    override fun onFailure(call: Call<BaseResponse>, t: Throwable) {
                        DynamicToast.makeWarning(requireContext(), "Ну тут проблемка какая-то образовалась").show()
                    }

                })
        }
        lifecycleScope.launch { setupWebSocketConnection() }
    }

    private suspend fun setupWebSocketConnection() {
        withContext(Dispatchers.IO) {
            val session = StompClient().connect(getString(R.string.ws_url_connections)).withJsonConversions()
            session.use { s ->
                val gameCode = (requireActivity() as MainActivity).gameCode

                val questionSub =
                    s.subscribe(getString(R.string.topic_question) + gameCode, QuestionResponse.serializer())
                questionSub.collect { response ->
                    if (response.question == "game ended") {
                        val activity = requireActivity() as MainActivity
                        activity.openStoryFragment()
                        return@collect
                    }

                    binding.question.text = response.question
                    binding.inputAnswer.visibility = View.VISIBLE
                    binding.sendAnswer.visibility = View.VISIBLE
                    questionNumber = response.questionNumber
                    binding.inputAnswer.text.clear()
                }
            }
        }
    }

    companion object {
        @JvmStatic
        fun newInstance() = EnterAnswerFragment()
    }

}