package com.car.frpc_android.ui

import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.Navigation
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.NavigationUI
import com.car.frpc_android.CommonUtils
import com.car.frpc_android.R
import com.car.frpc_android.database.Config
import com.car.frpc_android.databinding.ActivityMainBinding
import com.google.android.material.navigation.NavigationView
import com.jeremyliao.liveeventbus.LiveEventBus
import io.reactivex.Observer
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers

class MainActivity : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener {
    lateinit var binding: ActivityMainBinding
    private var mAppBarConfiguration: AppBarConfiguration? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setSupportActionBar(binding.bar.toolbar)
        mAppBarConfiguration = AppBarConfiguration.Builder(
            R.id.nav_home
        )
            .setOpenableLayout(binding.drawerLayout)
            .build()
        val navController = Navigation.findNavController(this, R.id.nav_host_fragment)
        NavigationUI.setupActionBarWithNavController(this, navController, mAppBarConfiguration!!)
        NavigationUI.setupWithNavController(binding.navView, navController)
        binding.navView.setNavigationItemSelectedListener(this)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.action_new_text -> CommonUtils.getStringFromRaw(this@MainActivity, R.raw.frpc)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(object : Observer<String> {
                    override fun onSubscribe(d: Disposable) {}
                    override fun onNext(content: String) {
                        LiveEventBus.get<Any>(IniEditActivity.INTENT_EDIT_INI).post(Config(content))
                        startActivity(Intent(this@MainActivity, IniEditActivity::class.java))
                    }

                    override fun onError(e: Throwable) {}
                    override fun onComplete() {}
                })
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.main, menu)
        return true
    }

    override fun onSupportNavigateUp(): Boolean {
        val navController = Navigation.findNavController(this, R.id.nav_host_fragment)
        return (NavigationUI.navigateUp(navController, mAppBarConfiguration!!)
                || super.onSupportNavigateUp())
    }

    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.logcat -> {
                startActivity(Intent(this, LogcatActivity::class.java))
                return true
            }
        }
        return super.onOptionsItemSelected(item)
    }
}