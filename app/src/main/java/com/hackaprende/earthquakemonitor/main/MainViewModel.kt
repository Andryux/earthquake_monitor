package com.hackaprende.earthquakemonitor.main

import android.app.Application
import android.util.Log
import androidx.lifecycle.*
import com.hackaprende.earthquakemonitor.Earthquake
import com.hackaprende.earthquakemonitor.api.ApiResponseStatus
import com.hackaprende.earthquakemonitor.database.getDatabase
import kotlinx.coroutines.launch
import java.net.UnknownHostException

private val TAG = MainViewModel::class.java.simpleName

class MainViewModel(application: Application, private val sortType: Boolean) : AndroidViewModel(application) {
    private val database = getDatabase(application)
    private val repository = MainRepository(database)

    private val _statusLiveData = MutableLiveData<ApiResponseStatus>()
    val statusLiveData: LiveData<ApiResponseStatus>
        get() = _statusLiveData

    private val _eqListLiveData = MutableLiveData<MutableList<Earthquake>>()
    val eqListLiveData: LiveData<MutableList<Earthquake>>
        get() = _eqListLiveData

    init {
        reloadEarthquakesFromDatabase(sortType)
    }

    private fun reloadEarthquakes(){
        viewModelScope.launch {
            try {
                _statusLiveData.value = ApiResponseStatus.LOADING
                _eqListLiveData.value = repository.fetchEarthquakes(sortType)
                _statusLiveData.value = ApiResponseStatus.DONE
            } catch (e: UnknownHostException){
                _statusLiveData.value = ApiResponseStatus.NO_INTERNET_CONNECTION
                Log.d(TAG, "No internet connection", e)
            }/*catch (e: UnknownHostException) {
                if (eqListLiveData.value == null || eqListLiveData.value!!.isEmpty()) {
                    _statusLiveData.value = ApiResponseStatus.NO_INTERNET_CONNECTION
                } else {
                    _statusLiveData.value = ApiResponseStatus.DONE
                }
            }*/
        }
    }

    fun reloadEarthquakesFromDatabase(sortByMagnitude: Boolean){
        viewModelScope.launch {
            _eqListLiveData.value = repository.fetchEarthquakesFromDb(sortByMagnitude)
            if(_eqListLiveData.value!!.isEmpty()){
                reloadEarthquakes()
            }
        }
    }
}