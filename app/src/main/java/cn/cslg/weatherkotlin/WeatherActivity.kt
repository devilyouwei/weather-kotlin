package cn.cslg.weatherkotlin

import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.support.v4.view.GravityCompat
import android.support.v4.widget.DrawerLayout
import android.support.v4.widget.SwipeRefreshLayout
import android.support.v7.app.AppCompatActivity
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.widget.*
import cn.cslg.weatherkotlin.weatherapi.HeWeather5
import cn.cslg.weatherkotlin.weatherapi.Weather
import com.bumptech.glide.Glide
import com.google.gson.Gson
import org.jetbrains.anko.defaultSharedPreferences
import org.jetbrains.anko.doAsync
import org.jetbrains.anko.find
import org.jetbrains.anko.uiThread
import java.net.URL

class WeatherActivity : AppCompatActivity() {

    private var weatherLayout: ScrollView? = null
    private var titleCity: TextView? = null
    private var titleUpdateTime: TextView? = null
    private var degreeText: TextView? = null
    private var weatherInfoText: TextView? = null
    private var forecastLayout: LinearLayout? = null
    private var aqiText: TextView? = null
    private var pm25Text: TextView? = null
    private var comfortText: TextView? = null
    private var carWashText: TextView? = null
    private var sportText: TextView? = null
    private var bingImg: ImageView? = null
    private var navButton:Button?=null

    //以下两个属性是公共的，在ChooseAreaFragment中调用
    var drawLayout:DrawerLayout?=null
    var swipeRefresh:SwipeRefreshLayout? = null

    private val URL = "https://free-api.heweather.com/v5/weather"
    private val KEY = "32d1c829ed7d483086f4f5b4d5947cef"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        //状态栏透明化
        if (Build.VERSION.SDK_INT >= 21) {
            val v = window.decorView
            v.systemUiVisibility = View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN or View.SYSTEM_UI_FLAG_LAYOUT_STABLE
            window.statusBarColor = Color.TRANSPARENT
        }
        setContentView(R.layout.activity_weather)

        swipeRefresh = find<SwipeRefreshLayout>(R.id.swipe_refresh)
        swipeRefresh!!.setColorSchemeResources(R.color.colorPrimary)
        weatherLayout = find<ScrollView>(R.id.weather_layout)
        titleCity = find<TextView>(R.id.title_city)
        titleUpdateTime = find<TextView>(R.id.title_update_time)
        degreeText = find<TextView>(R.id.degree_text)
        weatherInfoText = find<TextView>(R.id.weather_info_text)
        forecastLayout = find<LinearLayout>(R.id.forecast_layout)
        aqiText = find<TextView>(R.id.aqi_text)
        pm25Text = find<TextView>(R.id.pm25_text)
        comfortText = find<TextView>(R.id.comfort_text)
        carWashText = find<TextView>(R.id.car_wash_text)
        sportText = find<TextView>(R.id.sport_text)
        bingImg = find<ImageView>(R.id.bing_pic_img)
        drawLayout = find<DrawerLayout>(R.id.drawer_layout)
        loadBingImg()
        navButton = find<Button>(R.id.nav_button)
        navButton!!.setOnClickListener{
            drawLayout!!.openDrawer(GravityCompat.START)
        }

        val weatherId = defaultSharedPreferences.getString("weather_id","")
        weatherLayout!!.visibility = View.INVISIBLE
        requestWeather(weatherId)
        swipeRefresh!!.setOnRefreshListener {
            //刷新当前的城市weather_id
            requestWeather(defaultSharedPreferences.getString("weather_id",""))
        }
    }

    //从服务器加载天气信息
    fun requestWeather(wid: String) {
        val url = "${URL}?city=${wid}&key=${KEY}"
        doAsync{
            val s = URL(url).readText()

            uiThread {
                val weather = Gson().fromJson(s, Weather::class.java)
                //关闭下拉刷新
                swipeRefresh!!.isRefreshing = false
                Log.d("url",url)
                Log.d("url",weather.toString())
                showWeatherInfo(weather.HeWeather5[0])
            }
        }
    }

    //显示出天气信息
    private fun showWeatherInfo(w: HeWeather5) {
        //有时候请求出来是空数据（API问题吧）需要判定，否则报nullPointer异常
        if(w.status.equals("unknown city")){
            return
        }

        titleCity!!.text = w.basic.city
        titleUpdateTime!!.text = w.basic.update.loc
        degreeText!!.text = "${w.now.tmp} ℃"
        weatherInfoText!!.text = w.now.cond.txt + "     " + w.now.wind.dir + "  " + w.now.wind.sc + "级"
        forecastLayout!!.removeAllViews()
        for (d in w.daily_forecast) {
            val v = LayoutInflater.from(this).inflate(R.layout.forecast_item, forecastLayout, false)
            val dateText = v.find<TextView>(R.id.date_text)
            val infoText = v.find<TextView>(R.id.info_text)
            val maxText = v.find<TextView>(R.id.max_text)
            val minText = v.find<TextView>(R.id.min_text)
            dateText.text = d.date
            maxText.text = d.tmp.max
            minText.text = d.tmp.min
            if (d.cond.code_d == d.cond.code_n) {
                infoText.text = d.cond.txt_d
            } else {
                infoText.text = d.cond.txt_d + "->" + d.cond.txt_n
            }
            forecastLayout!!.addView(v)
        }
        if (w.aqi != null) {
            aqiText!!.text = w.aqi.city.aqi
            pm25Text!!.text = w.aqi.city.pm25
        }
        comfortText!!.text = "舒适度：" + w.suggestion.comf.txt
        carWashText!!.text = "洗车指数：" + w.suggestion.cw.txt
        sportText!!.text = "运动建议：" + w.suggestion.sport.txt
        weatherLayout!!.visibility = View.VISIBLE
    }

    //获取每日一图的API
    private fun loadBingImg() {
        val url = "http://guolin.tech/api/bing_pic"
        doAsync{
            val s = URL(url).readText()
            uiThread {
                Glide.with(this@WeatherActivity).load(s).into(bingImg)
            }
        }
    }
}
