package com.azhar.quran.model

import java.io.Serializable

/**
 * Created by Azhar Rivaldi on 22-12-2019.
 */

class ModelSurah : Serializable {

    var arti: String? = null

    @JvmField
    var asma: String? = null

    @JvmField
    var ayat: String? = null

    @JvmField
    var nama: String? = null

    @JvmField
    var type: String? = null
    var audio: String? = null

    @JvmField
    var nomor: String? = null
    var keterangan: String? = null

}