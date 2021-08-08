package com.lofod.chepuha.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.lofod.chepuha.MainActivity
import com.lofod.chepuha.R
import com.lofod.chepuha.adapters.PlayersAdapter
import com.lofod.chepuha.databinding.FragmentWaitingRoomBinding
import com.lofod.chepuha.model.Player
import com.lofod.chepuha.retrofit.API
import com.lofod.chepuha.retrofit.RetrofitClient
import com.pranavpandey.android.dynamic.toasts.DynamicToast
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
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.create

class WaitingRoomFragment(private val player: Player) : Fragment() {

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
        lifecycleScope.launch { setupWebSocketConnection() }
        binding.waitingListRefresh.setOnRefreshListener { getConnectedPlayers() }
    }

    private fun getConnectedPlayers() {
        RetrofitClient.getClient().create(API::class.java).getConnectedPlayer(gameCode)
            .enqueue(object : Callback<MutableList<Player>> {
                override fun onResponse(call: Call<MutableList<Player>>, response: Response<MutableList<Player>>) {
                    setupWaitingList(
                        if (response.body() != null) {
                            response.body()!!.add(player)
                            response.body()!!
                        } else mutableListOf(player)
                    )
                }

                override fun onFailure(call: Call<MutableList<Player>>, t: Throwable) {
                    DynamicToast.makeWarning(
                        requireContext(),
                        "Хз кто в комнате сейчас сидит, \nно если потянуть вниз, то мы еще раз попробуем список получить",
                        Toast.LENGTH_LONG
                    ).show()
                }
            })
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