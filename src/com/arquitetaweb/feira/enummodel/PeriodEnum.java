package com.arquitetaweb.feira.enummodel;

import com.google.gson.annotations.SerializedName;

/**
 * Created by publisoft on 31/07/2014.
 */
public enum PeriodEnum {
    @SerializedName("madrugada")
    MADRUGADA,
    @SerializedName("manha")
    MANHA,
    @SerializedName("tarde")
    TARDE,
    @SerializedName("noite")
    NOITE
}
