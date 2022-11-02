package com.example.networkcommunicationlasexercise

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.lifecycle.lifecycleScope
import com.example.networkcommunicationlasexercise.databinding.ActivityMainBinding
import com.example.networkcommunicationlasexercise.models.CovidData
import com.google.android.material.snackbar.Snackbar
import kotlinx.coroutines.launch
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.Response
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

const val API_AUTHORIZATION_HEADER = "X-RapidAPI-Key"

class AuthorizationInterceptor : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        val request = chain.request()

        val newRequest = request.newBuilder().addHeader(
            API_AUTHORIZATION_HEADER,
            "1a14206c5dmshf0ab2a7745daaacp15e52cjsnf6ea3f562f60",
        ).build()
        return chain.proceed(newRequest)
    }
}

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    private val logging = HttpLoggingInterceptor()
    private val authorizationInterceptor = AuthorizationInterceptor()
    private val client = OkHttpClient.Builder()
        .addInterceptor(logging)
        .addInterceptor(authorizationInterceptor)
        .build()

    private val retrofit = Retrofit.Builder()
        .client(client)
        .baseUrl("https://covid-193.p.rapidapi.com")
        .addConverterFactory(GsonConverterFactory.create())
        .build()

    private val apiService = retrofit.create(ApiService::class.java)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        retrieveCovidCasesDetails()
        logging.level = HttpLoggingInterceptor.Level.BODY
    }

    private fun setCovidDetails(covidResult: CovidData) {
        binding.newCases.text = getString(R.string.new_cases, covidResult.response[0].cases.new)
        binding.activeCases.text = getString(R.string.active_cases, covidResult.response[0].cases.active)
        binding.criticalCases.text = getString(R.string.critical_cases, covidResult.response[0].cases.critical)
        binding.recovered.text = getString(R.string.recovered, covidResult.response[0].cases.recovered)
        binding.total.text = getString(R.string.total, covidResult.response[0].cases.total)
        binding.day.text = getString(R.string.day, covidResult.response[0].day)
    }

    private fun retrieveCovidCasesDetails() {
        lifecycleScope.launch {
            try {
                val details = apiService.getCovidDetails()
                setCovidDetails(details)
            } catch (e: Exception) {
                Snackbar.make(
                    findViewById(R.id.main_view),
                    "Retrieving cases unSuccessful",
                    Snackbar.LENGTH_INDEFINITE
                ).setAction("Retry") { retrieveCovidCasesDetails() }.show()
            }
        }
    }
}