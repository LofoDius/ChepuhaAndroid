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
import com.lofod.chepuha.model.response.QuestionResponse
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kotlinx.coroutines.flow.collect
import org.hildan.krossbow.stomp.StompClient
import org.hildan.krossbow.stomp.conversions.kxserialization.subscribe
import org.hildan.krossbow.stomp.conversions.kxserialization.withJsonConversions
import org.hildan.krossbow.stomp.use

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

            lifecycleScope.launch { setupWebSocketConnection() }
            // TODO запросики
        }
    }

    private suspend fun setupWebSocketConnection() {
        withContext(Dispatchers.IO) {
            val session = StompClient().connect(getString(R.string.ws_url_connections)).withJsonConversions()
            session.use { s ->
                val gameCode = (requireActivity() as MainActivity).gameCode

                val questionSub = s.subscribe(getString(R.string.topic_question) + gameCode, QuestionResponse.serializer())
                questionSub.collect { response ->
                    if (response.question == "game ended") {
                        val activity = requireActivity() as MainActivity
                        activity.openStoryFragment()
                        return@collect
                    }

                    binding.question.text = response.question
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