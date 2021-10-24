package com.lofod.chepuha

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.lofod.chepuha.fragments.EnterAnswerFragment
import com.lofod.chepuha.fragments.MenuFragment
import com.lofod.chepuha.fragments.StoryFragment
import com.lofod.chepuha.fragments.WaitingRoomFragment
import kotlinx.serialization.ExperimentalSerializationApi

@ExperimentalSerializationApi
class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val menuFragment = MenuFragment.newInstance()
        //TODO сохранять и восстанавливать последнее имя пользователя в sharedPrefs
        openFragment(menuFragment)
    }

    private fun openFragment(fragment: Fragment) {
        val transaction = supportFragmentManager.beginTransaction()

        with(transaction) {
            replace(R.id.frameLayout, fragment)
            commit()
        }
    }

    fun openWaitingRoomFragment() {
        val waitingRoomFragment = WaitingRoomFragment.newInstance(StoreManager.getInstance().player)
        openFragment(waitingRoomFragment)
    }

    fun openEnterAnswerFragment() {
        val enterAnswerFragment = EnterAnswerFragment.newInstance()
        openFragment(enterAnswerFragment)
    }

    fun openStoryFragment() {
        val storyFragment = StoryFragment.newInstance()
        openFragment(storyFragment)
    }

}