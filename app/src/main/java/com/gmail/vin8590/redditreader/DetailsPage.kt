package com.gmail.vin8590.redditreader

import android.graphics.Bitmap
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.view.View
import android.webkit.WebView
import android.widget.ImageView
import android.widget.TextView

import com.nostra13.universalimageloader.core.ImageLoader

class DetailsPage : AppCompatActivity() {
    private var position: Int = 0
    private var banner: ImageView? = null
    private var title: TextView? = null
    private var auther: TextView? = null
    private var body: WebView? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.details_page)
        position = intent.getIntExtra("pos", 0)
        actionBar.setTitle(R.string.app_name)
        actionBar.setSubtitle(AppClass.name)
        banner = findViewById<ImageView>(R.id.detail_img)
        title = findViewById<TextView>(R.id.detail_title)
        auther = findViewById<TextView>(R.id.detail_auther)
        body = findViewById<WebView>(R.id.detail_body)
    }

    override fun onResume() {
        super.onResume()
        val img = ImageLoader.getInstance()
        val bmp = img.loadImageSync(AppClass.redData.data.children[position].data.preview.images[0].source.url)
        banner!!.setImageBitmap(bmp)
        title!!.text = AppClass.redData.data.children[position].data.title
        auther!!.text = AppClass.redData.data.children[position].data.author
        val url = AppClass.redData.data.children[position].data.permalink
        body!!.loadUrl("https://www.reddit.com/$url")

    }
}
