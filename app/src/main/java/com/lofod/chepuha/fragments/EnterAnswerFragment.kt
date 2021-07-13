package com.lofod.chepuha.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.lofod.chepuha.R

class EnterAnswerFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        return inflater.inflate(R.layout.fragment_enter_answer, container, false)
    }

    companion object {
        @JvmStatic
        fun newInstance() = EnterAnswerFragment()
    }

}