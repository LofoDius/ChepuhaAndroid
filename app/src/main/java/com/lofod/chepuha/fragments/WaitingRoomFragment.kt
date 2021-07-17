package com.lofod.chepuha.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.lofod.chepuha.MainActivity
import com.lofod.chepuha.R
import com.lofod.chepuha.adapters.PlayersAdapter
import com.lofod.chepuha.databinding.FragmentWaitingRoomBinding
import com.lofod.chepuha.model.Player

class WaitingRoomFragment : Fragment() {

    private var _binding: FragmentWaitingRoomBinding? = null
    private val binding get() = _binding!!

    private var _adapter: PlayersAdapter? = null
    private val adapter get() = _adapter!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentWaitingRoomBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {

    }

    fun setupWaitingList(players: MutableList<Player>) {
        val activity = requireActivity() as MainActivity
        _adapter = PlayersAdapter(activity.playerName, players)

        with(binding.waitingList) {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = this@WaitingRoomFragment.adapter
        }
    }

    companion object {
        @JvmStatic
        fun newInstance() = WaitingRoomFragment()
    }

}