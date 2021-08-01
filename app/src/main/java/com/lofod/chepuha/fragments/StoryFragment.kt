package com.lofod.chepuha.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.lofod.chepuha.adapters.StoryAdapter
import com.lofod.chepuha.databinding.FragmentStoryBinding
import com.lofod.chepuha.model.Answer

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
        // TODO запросики
    }

    fun setupStoryAdapter(story: MutableList<Answer>) {
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