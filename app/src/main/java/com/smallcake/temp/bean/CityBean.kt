package com.smallcake.temp.bean

import android.text.TextUtils
import com.smallcake.smallutils.CharacterParser
import java.io.Serializable

/**
 * Date:2021/7/9 17:16
 * Author:SmallCake
 * Desc:
 **/

data class CityBean(
    var name: String,
): Serializable {
    var firstLetter=CharacterParser.getFirstLetter(name)     //拼音

}