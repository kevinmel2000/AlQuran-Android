package com.azhar.quran.activities

import android.annotation.SuppressLint
import android.app.ProgressDialog
import android.media.AudioManager
import android.media.MediaPlayer
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.text.Html
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.androidnetworking.AndroidNetworking
import com.androidnetworking.common.Priority
import com.androidnetworking.error.ANError
import com.androidnetworking.interfaces.JSONArrayRequestListener
import com.azhar.quran.R
import com.azhar.quran.adapter.AyatAdapter
import com.azhar.quran.model.ModelAyat
import com.azhar.quran.model.ModelSurah
import com.azhar.quran.networking.Api
import kotlinx.android.synthetic.main.activity_detail_surah.*
import org.json.JSONArray
import org.json.JSONException
import java.io.IOException
import java.util.*

/**
 * Created by Azhar Rivaldi on 22-12-2019.
 */

class DetailSurahActivity : AppCompatActivity() {

    var nomor: String? = null
    var nama: String? = null
    var arti: String? = null
    var type: String? = null
    var ayat: String? = null
    var keterangan: String? = null
    var audio: String? = null
    var modelSurah: ModelSurah? = null
    var ayatAdapter: AyatAdapter? = null
    var progressDialog: ProgressDialog? = null
    var modelAyat: MutableList<ModelAyat> = ArrayList()
    var mHandler: Handler? = null

    @SuppressLint("RestrictedApi")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_detail_surah)

        //set toolbar
        toolbar_detail.setTitle(null)
        setSupportActionBar(toolbar_detail)
        assert(supportActionBar != null)
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)

        mHandler = Handler()

        //get data dari ListSurah
        modelSurah = intent.getSerializableExtra("detailSurah") as ModelSurah
        if (modelSurah != null) {
            nomor = modelSurah!!.nomor
            nama = modelSurah!!.nama
            arti = modelSurah!!.arti
            type = modelSurah!!.type
            ayat = modelSurah!!.ayat
            audio = modelSurah!!.audio
            keterangan = modelSurah!!.keterangan

            fabStop.setVisibility(View.GONE)
            fabPlay.setVisibility(View.VISIBLE)

            //Set text
            tvHeader.setText(nama)
            tvTitle.setText(nama)
            tvSubTitle.setText(arti)
            tvInfo.setText("$type - $ayat Ayat ")

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) tvKet.setText(Html.fromHtml(keterangan,
                    Html.FROM_HTML_MODE_COMPACT))
            else {
                tvKet.setText(Html.fromHtml(keterangan))
            }

            //get & play Audio
            val mediaPlayer = MediaPlayer()
            fabPlay.setOnClickListener(View.OnClickListener {
                try {
                    mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC)
                    mediaPlayer.setDataSource(audio)
                    mediaPlayer.prepare()
                    mediaPlayer.start()
                } catch (e: IOException) {
                    e.printStackTrace()
                }
                fabPlay.setVisibility(View.GONE)
                fabStop.setVisibility(View.VISIBLE)
            })

            fabStop.setOnClickListener(View.OnClickListener {
                mediaPlayer.stop()
                mediaPlayer.reset()
                fabPlay.setVisibility(View.VISIBLE)
                fabStop.setVisibility(View.GONE)
            })
        }

        progressDialog = ProgressDialog(this)
        progressDialog!!.setTitle("Mohon Tunggu")
        progressDialog!!.setCancelable(false)
        progressDialog!!.setMessage("Sedang menampilkan data...")

        rvAyat.setLayoutManager(LinearLayoutManager(this))
        rvAyat.setHasFixedSize(true)

        //Methods get data
        listAyat
    }

    private val listAyat: Unit
        get() {
            progressDialog!!.show()
            AndroidNetworking.get(Api.URL_LIST_AYAT)
                    .addPathParameter("nomor", nomor)
                    .setPriority(Priority.MEDIUM)
                    .build()
                    .getAsJSONArray(object : JSONArrayRequestListener {
                        override fun onResponse(response: JSONArray) {
                            for (i in 0 until response.length()) {
                                try {
                                    progressDialog!!.dismiss()
                                    val dataApi = ModelAyat()
                                    val jsonObject = response.getJSONObject(i)
                                    dataApi.nomor = jsonObject.getString("nomor")
                                    dataApi.arab = jsonObject.getString("ar")
                                    dataApi.indo = jsonObject.getString("id")
                                    dataApi.terjemahan = jsonObject.getString("tr")
                                    modelAyat.add(dataApi)
                                    showListAyat()
                                } catch (e: JSONException) {
                                    e.printStackTrace()
                                    Toast.makeText(this@DetailSurahActivity, "Gagal menampilkan data!",
                                            Toast.LENGTH_SHORT).show()
                                }
                            }
                        }
                        override fun onError(anError: ANError) {
                            progressDialog!!.dismiss()
                            Toast.makeText(this@DetailSurahActivity, "Tidak ada jaringan internet!",
                                    Toast.LENGTH_SHORT).show()
                        }
                    })
        }

    private fun showListAyat() {
        ayatAdapter = AyatAdapter(this@DetailSurahActivity, modelAyat)
        rvAyat!!.adapter = ayatAdapter
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == android.R.id.home) {
            finish()
            return true
        }
        return super.onOptionsItemSelected(item)
    }
}