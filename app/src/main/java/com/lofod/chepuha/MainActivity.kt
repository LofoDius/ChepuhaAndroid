package com.lofod.chepuha

import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import android.view.Gravity
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.lofod.chepuha.fragments.EnterAnswerFragment
import com.lofod.chepuha.fragments.MenuFragment
import com.lofod.chepuha.fragments.StoryFragment
import com.lofod.chepuha.fragments.WaitingRoomFragment
import com.pranavpandey.android.dynamic.toasts.DynamicToast
import kotlinx.serialization.ExperimentalSerializationApi

@ExperimentalSerializationApi
class MainActivity : AppCompatActivity() {

    private var lastBackPressTime = 0L

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val prefs = getSharedPreferences(
            getString(R.string.shared_preferences_file_name),
            Context.MODE_PRIVATE
        )

        StoreManager.getInstance().userName = prefs.getString("lastUsedUsername", "")!!

        openMenuFragment()
    }

    private fun openFragment(fragment: Fragment) {
        val transaction = supportFragmentManager.beginTransaction()

        with(transaction) {
            replace(R.id.frameLayout, fragment)
            commit()
        }
    }

    fun openWaitingRoomFragment() {
        val waitingRoomFragment = WaitingRoomFragment.newInstance()
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

    fun openMenuFragment() {
        val menuFragment = MenuFragment.newInstance()
        openFragment(menuFragment)
    }

    override fun onBackPressed() {
        val activeFragment = supportFragmentManager.fragments[0]
        when {
            activeFragment is StoryFragment -> {
                openMenuFragment()
            }
            lastBackPressTime + 2000 > System.currentTimeMillis() -> {
                super.onBackPressed()
                finish()
            }
            else -> {
                val toast = DynamicToast.make(
                    this,
                    "Нажмите еще раз, чтобы закрыть чепуху",
                    Toast.LENGTH_SHORT
                )
                toast.setGravity(Gravity.CENTER_HORIZONTAL, 16, 0)
                toast.show()

                lastBackPressTime = System.currentTimeMillis()
            }
        }
    }

    override fun onPause() {
        super.onPause()
        getSharedPreferences(getString(R.string.shared_preferences_file_name), Context.MODE_PRIVATE)
            .edit()
            .putString("lastUsedUsername", StoreManager.getInstance().userName)
            .apply()
    }
}