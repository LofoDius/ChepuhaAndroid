package com.lofod.chepuha.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.gmail.bishoybasily.stomp.lib.StompClient
import com.lofod.chepuha.MainActivity
import com.lofod.chepuha.R
import com.lofod.chepuha.StoreManager
import com.lofod.chepuha.adapters.PlayersAdapter
import com.lofod.chepuha.databinding.FragmentWaitingRoomBinding
import com.lofod.chepuha.model.Player
import com.lofod.chepuha.retrofit.API
import com.lofod.chepuha.retrofit.RetrofitClient
import com.pranavpandey.android.dynamic.toasts.DynamicToast
import io.reactivex.disposables.Disposable
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.json.Json
import okhttp3.OkHttpClient
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.util.concurrent.TimeUnit

@ExperimentalSerializationApi
class WaitingRoomFragment(private val player: Player) : Fragment() {

    private var _binding: FragmentWaitingRoomBinding? = null
    private val binding get() = _binding!!

    private var _adapter: PlayersAdapter? = null
    private val adapter get() = _adapter!!

    private lateinit var stompConnection: Disposable

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentWaitingRoomBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        lifecycleScope.launch { setupWebSocketConnection() }
        binding.waitingListRefresh.setOnRefreshListener { getConnectedPlayers() }
    }

    private fun getConnectedPlayers() {
        val gameCode = StoreManager.getInstance().gameCode
        RetrofitClient.getClient().create(API::class.java).getConnectedPlayer(gameCode)
            .enqueue(object : Callback<MutableList<Player>> {
                override fun onResponse(call: Call<MutableList<Player>>, response: Response<MutableList<Player>>) {
                    setupWaitingList(
                        if (response.body() != null) {
                            response.body()!!.add(player)
                            response.body()!!
                        } else mutableListOf(player)
                    )
                    binding.waitingListRefresh.isRefreshing = false
                }

                override fun onFailure(call: Call<MutableList<Player>>, t: Throwable) {
                    DynamicToast.makeWarning(
                        requireContext(),
                        "Хз кто в комнате сейчас сидит, \nно если потянуть вниз, то мы еще раз попробуем список получить",
                        Toast.LENGTH_LONG
                    ).show()
                    binding.waitingListRefresh.isRefreshing = false
                }
            })
    }

    private fun setupWaitingList(players: MutableList<Player>) {
        _adapter = PlayersAdapter(StoreManager.getInstance().userName, players)

        with(binding.waitingList) {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = this@WaitingRoomFragment.adapter
        }
    }

    private suspend fun setupWebSocketConnection() {
        withContext(Dispatchers.IO) {
            val httpClient = OkHttpClient.Builder()
                .readTimeout(10, TimeUnit.SECONDS)
                .writeTimeout(10, TimeUnit.SECONDS)
                .connectTimeout(10, TimeUnit.SECONDS)
                .build()

            val client = StompClient(httpClient, 100L).apply { url = getString(R.string.ws_url_connections) }

            stompConnection = client.connect().subscribe()

            val store = StoreManager.getInstance()

            if (client.join(getString(R.string.topic_connections) + store.gameCode).subscribe {
                    val player = Json.decodeFromString<Player>(it)
                    adapter.addPlayer(player)
                }.isDisposed) {
                DynamicToast.makeError(requireContext(), "Вебсокет сыбался!").show()
                setupWebSocketConnection()
            }

            if (client.join(getString(R.string.topic_game_started) + store.gameCode).subscribe {
                    if (it == "Started") {
                        val activity = requireActivity() as MainActivity
                        activity.openEnterAnswerFragment()
                    }
                }.isDisposed) {
                DynamicToast.makeError(requireContext(), "Вебсокет сыбался!").show()
                setupWebSocketConnection()
            }

//            val session = StompClient().connect(getString(R.string.ws_url_connections)).withJsonConversions()
//            session.use { s ->
//                val connectionsSub: Flow<Player> =
//                    s.subscribe(getString(R.string.topic_connections) + gameCode, Player.serializer())
//                connectionsSub.collect { newPlayer ->
//                    adapter.addPlayer(newPlayer)
//                }
//
//                val gameStartedSub: Flow<String> = s.subscribeText(getString(R.string.topic_game_started) + gameCode)
//                gameStartedSub.collect { msg ->
//                    if (msg == "Started") {
//                        val activity = requireActivity() as MainActivity
//                        activity.openEnterAnswerFragment()
//                    }
//                }
//            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        stompConnection.dispose()
    }

    companion object {
        @JvmStatic
        fun newInstance(player: Player) = WaitingRoomFragment(player)
    }

}