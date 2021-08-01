package com.lofod.chepuha.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.lofod.chepuha.MainActivity
import com.lofod.chepuha.R
import com.lofod.chepuha.adapters.PlayersAdapter
import com.lofod.chepuha.databinding.FragmentWaitingRoomBinding
import com.lofod.chepuha.model.Player
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.hildan.krossbow.stomp.StompClient
import org.hildan.krossbow.stomp.conversions.kxserialization.subscribe
import org.hildan.krossbow.stomp.conversions.kxserialization.withJsonConversions
import org.hildan.krossbow.stomp.subscribeText
import org.hildan.krossbow.stomp.use

class WaitingRoomFragment(val player: Player) : Fragment() {

    private var _binding: FragmentWaitingRoomBinding? = null
    private val binding get() = _binding!!

    private var _adapter: PlayersAdapter? = null
    private val adapter get() = _adapter!!

    private lateinit var gameCode: String

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentWaitingRoomBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        gameCode = (requireActivity() as MainActivity).gameCode
        binding.gameCode.text = gameCode
        setupWaitingList(mutableListOf(player))
        lifecycleScope.launch { setupWebSocketConnection() }
    }

    private fun setupWaitingList(players: MutableList<Player>) {
        val activity = requireActivity() as MainActivity
        _adapter = PlayersAdapter(activity.playerName, players)

        with(binding.waitingList) {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = this@WaitingRoomFragment.adapter
        }
    }

    private suspend fun setupWebSocketConnection() {
        withContext(Dispatchers.IO) {
            val session = StompClient().connect(getString(R.string.ws_url_connections)).withJsonConversions()
            session.use { s ->
                val connectionsSub: Flow<Player> =
                    s.subscribe(getString(R.string.topic_connections) + gameCode, Player.serializer())
                connectionsSub.collect { newPlayer ->
                    adapter.addPlayer(newPlayer)
                }

                val gameStartedSub: Flow<String> = s.subscribeText(getString(R.string.topic_game_started) + gameCode)
                gameStartedSub.collect { msg ->
                    if (msg == "Started") {
                        val activity = requireActivity() as MainActivity
                        activity.openEnterAnswerFragment()
                    }
                }
            }
        }
    }

    companion object {
        @JvmStatic
        fun newInstance(player: Player) = WaitingRoomFragment(player)
    }

}