package cn.cslg.weatherkotlin.weatherapi

data class Weather(
    val HeWeather5: List<HeWeather5>
)

data class HeWeather5(
    val aqi: Aqi,
    val basic: Basic,
    val daily_forecast: List<DailyForecast>,
    val now: Now,
    val status: String,
    val suggestion: Suggestion
)

data class Aqi(
    val city: City
)

data class City(
    val aqi: String,
    val co: String,
    val no2: String,
    val o3: String,
    val pm10: String,
    val pm25: String,
    val qlty: String,
    val so2: String
)

data class Basic(
    val city: String,
    val cnty: String,
    val id: String,
    val lat: String,
    val lon: String,
    val update: Update
)

data class Update(
    val loc: String,
    val utc: String
)

data class DailyForecast(
    val astro: Astro,
    val cond: Cond,
    val date: String,
    val hum: String,
    val pcpn: String,
    val pop: String,
    val pres: String,
    val tmp: Tmp,
    val uv: String,
    val vis: String,
    val wind: Wind
)

data class Astro(
    val mr: String,
    val ms: String,
    val sr: String,
    val ss: String
)

data class Cond(
    val code_d: String,
    val code_n: String,
    val txt_d: String,
    val txt_n: String
)

data class Tmp(
    val max: String,
    val min: String
)

data class Wind(
    val deg: String,
    val dir: String,
    val sc: String,
    val spd: String
)

data class Now(
    val cond: CondX,
    val fl: String,
    val hum: String,
    val pcpn: String,
    val pres: String,
    val tmp: String,
    val vis: String,
    val wind: WindX
)

data class CondX(
    val code: String,
    val txt: String
)

data class WindX(
    val deg: String,
    val dir: String,
    val sc: String,
    val spd: String
)

data class Suggestion(
    val air: Air,
    val comf: Comf,
    val cw: Cw,
    val drsg: Drsg,
    val flu: Flu,
    val sport: Sport,
    val trav: Trav,
    val uv: Uv
)

data class Air(
    val brf: String,
    val txt: String
)

data class Comf(
    val brf: String,
    val txt: String
)

data class Cw(
    val brf: String,
    val txt: String
)

data class Drsg(
    val brf: String,
    val txt: String
)

data class Flu(
    val brf: String,
    val txt: String
)

data class Sport(
    val brf: String,
    val txt: String
)

data class Trav(
    val brf: String,
    val txt: String
)

data class Uv(
    val brf: String,
    val txt: String
)