package cn.cslg.weatherkotlin

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import org.jetbrains.anko.defaultSharedPreferences
import org.jetbrains.anko.startActivity

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        //如果已经选择过某个城市的weather_id，直接跳过去
        print(defaultSharedPreferences.getString("weather_id",""))
        if(!"".equals(defaultSharedPreferences.getString("weather_id",""))){
            startActivity<WeatherActivity>()
            finish()
        }
    }
}
