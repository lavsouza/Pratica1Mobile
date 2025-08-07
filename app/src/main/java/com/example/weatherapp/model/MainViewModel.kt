package com.example.weatherapp.model

import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import com.example.weatherapp.api.WeatherService
import com.example.weatherapp.api.toForecast
import com.example.weatherapp.api.toWeather
import com.example.weatherapp.db.fb.FBCity
import com.example.weatherapp.db.fb.FBDatabase
import com.example.weatherapp.db.fb.FBUser
import com.example.weatherapp.db.fb.toFBCity
import com.example.weatherapp.ui.nav.Route
import com.google.android.gms.maps.model.LatLng


class MainViewModel(
    private val db: FBDatabase,
    private val service: WeatherService
) : ViewModel(), FBDatabase.Listener {

    private var _page = mutableStateOf<Route>(Route.Home)
    var page: Route
        get() = _page.value
        set(tmp) { _page.value = tmp }

    private var _city = mutableStateOf<City?>(null)
    var city: City?
        get() = _city.value
        set(tmp) { _city.value = tmp?.copy() }

    private val _cities = mutableStateMapOf<String, City>()
    val cities: List<City>
        get() = _cities.values.toList()

    private val _user = mutableStateOf<User?>(null)
    val user: User?
        get() = _user.value

    init {
        db.setListener(this)
    }

    fun remove(city: City) {
        db.remove(city.toFBCity())
    }

    fun add(name: String) {
        service.getLocation(name) { lat, lng ->
            if (lat != null && lng != null) {
                db.add(City(name = name, location = LatLng(lat, lng)).toFBCity())
            }
        }
    }

    fun add(location1: String, location: LatLng) {
        service.getName(location.latitude, location.longitude) { name ->
            if (name != null) {
                db.add(City(name = name, location = location).toFBCity())
            }
        }
    }

    fun loadWeather(name: String) {
        service.getWeather(name) { apiWeather ->
            val newCity = _cities[name]?.copy(weather = apiWeather?.toWeather())
            if (newCity != null) {
                _cities[name] = newCity
            }
        }
    }

    fun loadForecast(name: String) {
        service.getForecast(name) { apiForecast ->
            val newCity = _cities[name]!!.copy( forecast = apiForecast?.toForecast() )
            _cities.remove(name)
            _cities[name] = newCity
            city = if (city?.name == name) newCity else city
        }
    }

    fun loadBitmap(name: String) {
        val city = _cities[name]
        service.getBitmap(city?.weather!!.imgUrl) { bitmap ->
            val newCity = city.copy(
                weather = city.weather?.copy(
                    bitmap = bitmap
                )
            )
            _cities.remove(name)
            _cities[name] = newCity
        }
    }

//    fun getCities(): List<City> = List(20) { i ->
//        City(name = "Cidade $i", weather = "Carregando clima...")
//    }

    override fun onUserLoaded(user: FBUser) {
        _user.value = user.toUser()
    }

    override fun onUserSignOut() {
        // implementar se necessário
    }

    override fun onCityAdded(city: FBCity) {
        _cities[city.name!!] = city.toCity()
    }

    override fun onCityUpdated(city: FBCity) {
        _cities.remove(city.name)
        _cities[city.name!!] = city.toCity()
        if (_city.value?.name == city.name) { _city.value = city.toCity() }
    }

    override fun onCityRemoved(city: FBCity) {
        _cities.remove(city.name)
        if (_city.value?.name == city.name) { _city.value = null }
    }
}