package com.gmail.vin8590.redditreader

import android.app.Application
import com.gmail.vin8590.data.RedData
import java.util.*

class AppClass : Application() {
    companion object {

        public var name: String? = null
        public lateinit var redData: RedData
    }
}
