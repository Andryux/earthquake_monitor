package com.hackaprende.earthquakemonitor.main

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.hackaprende.earthquakemonitor.Earthquake
import com.hackaprende.earthquakemonitor.R
import com.hackaprende.earthquakemonitor.api.ApiResponseStatus
import com.hackaprende.earthquakemonitor.api.WorkerUtil
import com.hackaprende.earthquakemonitor.databinding.ActivityMainBinding
import com.hackaprende.earthquakemonitor.details.EqDetailActivity

private const val SORT_TYPE_KEY = "sort_type"

class MainActivity : AppCompatActivity() {

    private lateinit var  viewModel: MainViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.eqRecycler.layoutManager = LinearLayoutManager(this)

        WorkerUtil.scheduleSync(this)

        val sortType = getSortType()

        viewModel = ViewModelProvider(this,
            MainViewModelFactory(application, sortType)).get(MainViewModel::class.java)

        val adapter = EqAdapter(this)
        binding.eqRecycler.adapter = adapter

        adapter.setOnItemClickListener {
            openDetailActivity(it)
        }

        viewModel.eqListLiveData.observe(this, Observer {
            eqList  ->
            adapter.submitList(eqList)

            handleEmptyView(eqList, binding)
            /*
            adapter.submitList(it)
            if (it.size == 0) {
                recyclerView.visibility = View.GONE
                binding.eqEmptyView.visibility = View.VISIBLE
            } else {
                recyclerView.visibility = View.VISIBLE
                binding.eqEmptyView.visibility = View.GONE
            }
            */
        })

        viewModel.statusLiveData.observe(this, Observer {
            if (it == ApiResponseStatus.LOADING) {
                binding.loadingWheel.visibility = View.VISIBLE
            } else {
                binding.loadingWheel.visibility = View.GONE
            }

            if (it == ApiResponseStatus.NO_INTERNET_CONNECTION) {
                Toast.makeText(this, R.string.no_internet_connection,
                    Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun getSortType(): Boolean {
        val prefs = getPreferences(MODE_PRIVATE)
        return prefs.getBoolean(SORT_TYPE_KEY, false)
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.main_menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        val itemId = item.itemId
        if(itemId == R.id.main_menu_sort_magnitude){
            viewModel.reloadEarthquakesFromDatabase(true)
            saveSortType(true)
        }else if(itemId == R.id.main_menu_sort_time){
            viewModel.reloadEarthquakesFromDatabase(false)
            saveSortType(false)
        }
        return super.onOptionsItemSelected(item)
    }

    private fun saveSortType(sortByMagnitude: Boolean){
        val prefs = getPreferences(MODE_PRIVATE)
        val editor = prefs.edit()
        editor.putBoolean(SORT_TYPE_KEY, sortByMagnitude)
        editor.apply()
    }

    private fun handleEmptyView(eqList: MutableList<Earthquake>, binding: ActivityMainBinding){
        if(eqList.isEmpty()){
            binding.eqEmptyView.visibility = View.VISIBLE
        }else{
            binding.eqEmptyView.visibility = View.GONE
        }
    }

    private fun openDetailActivity(earthquake: Earthquake) {
        val intent = Intent(this, EqDetailActivity::class.java)
        intent.putExtra(EqDetailActivity.EQ_KEY, earthquake)
        startActivity(intent)
    }
}