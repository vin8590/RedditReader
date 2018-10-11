package com.gmail.vin8590.redditreader

import android.app.Activity
import android.app.ProgressDialog
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.graphics.Bitmap
import android.os.AsyncTask
import android.os.Bundle
import android.support.v7.app.AlertDialog
import android.support.v7.app.AppCompatActivity
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*

import com.gmail.vin8590.data.RedData
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.nostra13.universalimageloader.core.ImageLoader
import kotlinx.android.synthetic.main.activity_red.*

import java.io.BufferedReader
import java.io.IOException
import java.io.InputStream
import java.io.InputStreamReader
import java.net.HttpURLConnection
import java.net.MalformedURLException
import java.net.URL
import javax.net.ssl.HttpsURLConnection

class RedReader : AppCompatActivity() {

    internal var prefs: SharedPreferences? = null
    lateinit var gridview: GridView
    lateinit var progressBar: ProgressBar


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_red)
        actionBar.setTitle(R.string.app_name)
        actionBar.setSubtitle(AppClass.name)
        gridview = findViewById<View>(R.id.gridview) as GridView
         progressBar = this.progressBar1
    }

    override fun onPostCreate(savedInstanceState: Bundle?) {
        super.onPostCreate(savedInstanceState)
        prefs = getSharedPreferences("com.gmail.vin8590.redditreader", Context.MODE_PRIVATE)
        if (prefs!!.getBoolean("firstrun", true)) {
            val loginInt = Intent(this, LoginActivity::class.java)
            startActivity(loginInt)
            prefs!!.edit().putBoolean("firstrun", false).commit()
            prefs!!.edit().putString("name", AppClass.name).commit()
        } else {
            AppClass.name = prefs!!.getString("name", "guest")
        }

    }

    override fun onResume() {
        super.onResume()
        progressBar.visibility = View.VISIBLE
        JsonTask().execute("https://www.reddit.com/r/all.json")
        progressBar.visibility = View.GONE
        try {
            if (AppClass.redData != null) {
                gridview.adapter = ImageAdapter(this)
                gridview.onItemClickListener = AdapterView.OnItemClickListener { parent, v, position, id ->
                    val nextPage = Intent(this, DetailsPage::class.java)
                    nextPage.putExtra("pos", position)
                    startActivity(nextPage)
                }
            }
        }catch(e:Exception){
            val builder = AlertDialog.Builder(this@RedReader)
            builder.setTitle("Error")
            builder.setMessage("Error in File format")
        }
    }

    override fun onBackPressed() {
        super.onBackPressed()
        finish()
    }

    inner class JsonTask : AsyncTask<String, String, String>() {

        override fun onPreExecute() {
            super.onPreExecute()

        }

        override fun doInBackground(vararg params: String): String? {
            try {
                var result: String? = null
                do{
                    result = URL(params[0]).readText()
                }while( result == null);
                return result
            } catch (e: MalformedURLException) {
                e.printStackTrace()
            } catch (e: IOException) {
                e.printStackTrace()
            }catch(e: Exception)
            {
                val builder = AlertDialog.Builder(this@RedReader)
                builder.setTitle("Error")
                builder.setMessage("Error in File format")
                builder.show()
            }
            return null
        }

        override fun onPostExecute(result: String) {
            super.onPostExecute(result)
            try{
                val gson = GsonBuilder().create()
                AppClass.redData = gson.fromJson(result, RedData::class.java)
            }catch(e:Exception){
                // Error in converting Json to Object because edited parameter in child 8 is a number rather than boolean
                val builder = AlertDialog.Builder(this@RedReader)
                builder.setTitle("Error")
                builder.setMessage("Error in File format")
                builder.show()
            }

        }
    }
}

class ImageAdapter(private val mContext: Context) : BaseAdapter() {


    override fun getCount(): Int {
        return AppClass.redData.data.children.size
    }

    override fun getItem(position: Int): Any? {
        return null
    }

    override fun getItemId(position: Int): Long {
        return 0
    }

    // create a new ImageView for each item referenced by the Adapter
    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {

        val view: View

        if (convertView == null) {

            val inflater = mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
            view = inflater.inflate(R.layout.grid_cell, null)
        } else {

            view = convertView
        }

        val imgView = view.findViewById<View>(R.id.grid_img) as ImageView
        imgView.scaleType = ImageView.ScaleType.CENTER_CROP
        var img = ImageLoader.getInstance().loadImageSync(AppClass.redData.data.children[position].data.thumbnail)
        imgView.setImageBitmap(img)

        val txt1 = view.findViewById<View>(R.id.grid_txt1) as TextView
        txt1.setText(AppClass.redData.data.children[position].data.title)

        val txt2 = view.findViewById<View>(R.id.grid_txt2) as TextView
        txt1.setText(AppClass.redData.data.children[position].data.author)

        return view
    }
}