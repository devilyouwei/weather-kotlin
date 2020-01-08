package cn.cslg.weatherkotlin

import android.app.ProgressDialog
import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.LinearLayout
import android.widget.ListView
import android.widget.TextView
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import org.jetbrains.anko.doAsync
import org.jetbrains.anko.find
import org.jetbrains.anko.support.v4.defaultSharedPreferences
import org.jetbrains.anko.support.v4.startActivity
import org.jetbrains.anko.uiThread
import java.net.URL
import java.util.*

/**
 * Created by devil on 2017/5/20.
 */
class ChooseAreaFragment : Fragment() {
    private val LEVEL_PROVINCE = 0
    private val LEVEL_CITY = 1
    private val LEVEL_COUNTY = 2
    private var current_level = 0

    private val URL = "http://guolin.tech/api/china/"

    private var provinceList = ArrayList<Province>()
    private var cityList = ArrayList<City>()
    private var countyList = ArrayList<County>()

    private var selectedProvince: Province? = null
    private var selectedCity: City? = null
    private var selectedCounty: County? = null

    private var backBtn: Button? = null
    private var titleText: TextView? = null
    private var listView: ListView? = null

    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?, savedInstanceState: Bundle?): View {
        val view = inflater!!.inflate(R.layout.choose_area, container, false)

        if (activity is WeatherActivity) {
            //设置偏移状态栏高度，防止状态栏遮罩
            val res = activity.resources
            val resId = res.getIdentifier("status_bar_height", "dimen", "android");
            val height = res.getDimensionPixelSize(resId)
            val paddingContent = view.find<LinearLayout>(R.id.padding_content)
            paddingContent.setPadding(0, height, 0, 0)
        }

        titleText = view.find<TextView>(R.id.title_text)
        listView = view.find<ListView>(R.id.list_view)
        backBtn = view.find<Button>(R.id.back_button)

        return view
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        //列表点击监听事件
        listView!!.setOnItemClickListener {
            _, _, position, _ ->
            when (current_level) {
                LEVEL_PROVINCE -> {
                    selectedProvince = provinceList[position]
                    queryCity()
                }
                LEVEL_CITY -> {
                    selectedCity = cityList[position]
                    queryCounty()
                }
                LEVEL_COUNTY -> {
                    selectedCounty = countyList[position]
                    defaultSharedPreferences.edit().putString("weather_id", selectedCounty!!.weather_id).apply()
                    if (activity is MainActivity) {
                        startActivity<WeatherActivity>()
                        activity.finish()   //将MainActivity销毁掉
                    } else if (activity is WeatherActivity) {
                        val act = activity as WeatherActivity
                        act.drawLayout!!.closeDrawers()
                        act.swipeRefresh!!.isRefreshing = true      //显示下拉刷新
                        act.requestWeather(selectedCounty!!.weather_id)
                    }
                }
            }
        }

        //返回按钮监听事件
        backBtn!!.setOnClickListener {
            if (current_level == LEVEL_CITY)
                queryProvince()
            else if (current_level == LEVEL_COUNTY)
                queryCity()
        }
        queryProvince()
    }

    private fun queryProvince() {
        titleText!!.text = "中国"
        backBtn!!.visibility = View.INVISIBLE     //隐藏返回键
        showProgress()

        doAsync {
            val s = URL(URL).readText()

            uiThread {
                closeProgress()
                val t = object : TypeToken<List<Province>>() {}.type
                provinceList = Gson().fromJson<List<Province>>(s, t) as ArrayList<Province>
                val adapter = ProvinceAdapter(context, R.layout.list_city_item, provinceList)
                listView!!.adapter = adapter
                listView!!.setSelection(0)
                current_level = LEVEL_PROVINCE
            }
        }

    }

    private fun queryCity() {
        titleText!!.text = selectedProvince!!.name       //标题为当前省份
        backBtn!!.visibility = View.VISIBLE   //显示返回键
        showProgress()

        doAsync {
            val s = URL(URL + "/" + selectedProvince!!.id).readText()

            uiThread {
                closeProgress()
                val t = object : TypeToken<List<City>>() {}.type
                cityList = Gson().fromJson<List<City>>(s, t) as ArrayList<City>
                val adapter = CityAdapter(context, R.layout.list_city_item, cityList)
                listView!!.adapter = adapter
                listView!!.setSelection(0)
                current_level = LEVEL_CITY
            }
        }
    }

    private fun queryCounty() {
        titleText!!.text = selectedCity!!.name       //标题为当前市
        backBtn!!.visibility = View.VISIBLE   //显示返回键
        showProgress()

        doAsync {
            val s = URL(URL + "/" + selectedProvince!!.id + "/" + selectedCity!!.id).readText()

            uiThread {
                closeProgress()
                val t = object : TypeToken<List<County>>() {}.type
                countyList = Gson().fromJson<List<County>>(s, t) as ArrayList<County>
                val adapter = CountyAdapter(context, R.layout.list_city_item, countyList)
                listView!!.adapter = adapter
                listView!!.setSelection(0)
                current_level = LEVEL_COUNTY
            }
        }
    }


    //进度条
    private var progress: ProgressDialog? = null

    private fun showProgress(message: String = "加载中") {
        if (progress == null) {
            progress = ProgressDialog(activity)
            progress!!.setMessage(message)
            progress!!.setCancelable(false)
        }
        progress!!.show()
    }

    private fun closeProgress() {
        if (progress != null)
            progress!!.dismiss()
    }

}
