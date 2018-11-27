package com.example.bence.koinz

import android.os.AsyncTask
import java.io.IOException
import java.io.InputStream
import java.net.HttpURLConnection
import java.net.URL

interface DownLoadCompleteListener {
    fun downloadComplete(result: String)
}


class DownloadFileTask(private val caller: DownLoadCompleteListener):
        AsyncTask<String, Void, String> (){

    override fun doInBackground(vararg urls: String): String =try {
       loadFileFromNetwork(urls[0])
    }catch(e:IOException){
        "Unable to load content. Check your network connection."
    }

    private fun loadFileFromNetwork(urlString: String): String {
        val stream:InputStream = downloadUrl(urlString)
        // Read input from stream, build result as a string
        return String(stream.readBytes())
    }


    @Throws(IOException::class)
    private fun downloadUrl(urlString: String): InputStream{
        val url = URL(urlString)
        val conn = url.openConnection() as HttpURLConnection
        conn.readTimeout = 10000 //milliseconds
        conn.connectTimeout = 15000 //milliseconds
        conn.requestMethod= "GET"
        conn.doInput = true
        conn.connect() //Starts query
        return conn.inputStream
    }

    override fun onPostExecute(result: String) {
        super.onPostExecute(result)

        caller.downloadComplete(result)
    }
}