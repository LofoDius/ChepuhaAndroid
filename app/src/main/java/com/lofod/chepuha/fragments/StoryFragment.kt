package com.lofod.chepuha.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.lofod.chepuha.StoreManager
import com.lofod.chepuha.adapters.StoryAdapter
import com.lofod.chepuha.databinding.FragmentStoryBinding
import com.lofod.chepuha.model.Answer
import com.lofod.chepuha.model.request.StoryRequest
import com.lofod.chepuha.model.response.StoryResponse
import com.lofod.chepuha.retrofit.API
import com.lofod.chepuha.retrofit.RetrofitClient
import com.pranavpandey.android.dynamic.toasts.DynamicToast
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class StoryFragment : Fragment() {

    private var _binding: FragmentStoryBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentStoryBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        binding.storyRefresh.setOnRefreshListener { getStory() }
        getStory()
    }

    private fun getStory() {
        val store = StoreManager.getInstance()
        RetrofitClient.getClient().create(API::class.java).getStory(StoryRequest(store.player.id, store.gameCode))
            .enqueue(object : Callback<StoryResponse> {
                override fun onResponse(call: Call<StoryResponse>, response: Response<StoryResponse>) {
                    if (response.body() != null) {
                        setupStoryAdapter(response.body()!!.story.answers as MutableList<Answer>)
                    } else {
                        DynamicToast.makeWarning(
                            requireContext(),
                            "Серв прислал пустой ответ\nСкорее всего, разраб - долбаеб"
                        ).show()
                    }
                    binding.storyRefresh.isRefreshing = false
                }

                override fun onFailure(call: Call<StoryResponse>, t: Throwable) {
                    DynamicToast.makeWarning(requireContext(), """Запрос ушел за хлебом и не вернулся 🤣""").show()
                    binding.storyRefresh.isRefreshing = false
                }

            })
    }

    private fun setupStoryAdapter(story: MutableList<Answer>) {
        with(binding.storyList) {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = StoryAdapter(story)
        }
    }

    companion object {
        @JvmStatic
        fun newInstance() = StoryFragment()
    }

}