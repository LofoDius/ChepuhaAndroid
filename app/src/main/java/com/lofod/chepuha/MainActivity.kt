package com.lofod.chepuha

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.fragment.app.Fragment
import com.lofod.chepuha.fragments.MenuFragment

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val menuFragment = MenuFragment.newInstance()
        //TODO сохранять и восстанавливать последнее имя пользователя в sharedPrefs
        openFragment(menuFragment)
    }

    fun openFragment(fragment: Fragment) {
        val transaction = supportFragmentManager.beginTransaction()

        with(transaction) {
            replace(R.id.frameLayout, fragment)
            commit()
        }
    }

}