package ru.myitschool.nasa_bootcamp.data.model.spaceX.history

import com.google.gson.annotations.SerializedName
import ru.myitschool.nasa_bootcamp.data.model.models.HistoryModel

class History(
    @field:SerializedName("id") val id: Int,
    @field:SerializedName("title") val title: String,
    @field:SerializedName("launch_year") val launch_year: Int,
    @field:SerializedName("event_date_utc") val event_date_utc: String,
    @field:SerializedName("details") val details: String
) {

    public fun createHistoryModel(): HistoryModel {
        return HistoryModel(id, title, launch_year, event_date_utc, details)
    }
}
///Можно добавить ссылки на википедию/сайт spaceX