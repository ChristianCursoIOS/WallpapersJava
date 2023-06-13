package com.appscloud.wallpapersjava.modelo;

import android.os.Parcel;
import android.os.Parcelable;

import androidx.annotation.NonNull;

import java.io.Serializable;

//implementamos la interface Parcelable para transoformar al objeto Administrador en un formato
//que pueda ser pasado mediante un intent y a esta conversion se le llama serializar el objeto
//NOTA: UTILIZAMOS PARCELABLE POR RECOMENDACION DE ANDROID EN SU DOCUMENTACION
public class Administrador implements Parcelable {
    private String UID;
    private String NOMBRES;
    private String APELLIDOS;
    private String EMAIL;
    private String IMAGEN;
    private int EDAD;

    public Administrador() {
    }


    public Administrador(String UID, String NOMBRES, String APELLIDOS, String EMAIL, String IMAGEN, int EDAD) {
        this.UID = UID;
        this.NOMBRES = NOMBRES;
        this.APELLIDOS = APELLIDOS;
        this.EMAIL = EMAIL;
        this.IMAGEN = IMAGEN;
        this.EDAD = EDAD;
    }


    protected Administrador(Parcel in) {
        UID = in.readString();
        NOMBRES = in.readString();
        APELLIDOS = in.readString();
        EMAIL = in.readString();
        IMAGEN = in.readString();
        EDAD = in.readInt();
    }


    public static final Creator<Administrador> CREATOR = new Creator<Administrador>() {
        @Override
        public Administrador createFromParcel(Parcel in) {
            return new Administrador(in);
        }

        @Override
        public Administrador[] newArray(int size) {
            return new Administrador[size];
        }
    };

    public String getUID() {
        return UID;
    }

    public void setUID(String UID) {
        this.UID = UID;
    }

    public String getNOMBRES() {
        return NOMBRES;
    }

    public void setNOMBRES(String NOMBRES) {
        this.NOMBRES = NOMBRES;
    }

    public String getAPELLIDOS() {
        return APELLIDOS;
    }

    public void setAPELLIDOS(String APELLIDOS) {
        this.APELLIDOS = APELLIDOS;
    }

    public String getEMAIL() {
        return EMAIL;
    }

    public void setEMAIL(String EMAIL) {
        this.EMAIL = EMAIL;
    }

    public String getIMAGEN() {
        return IMAGEN;
    }

    public void setIMAGEN(String IMAGEN) {
        this.IMAGEN = IMAGEN;
    }

    public int getEDAD() {
        return EDAD;
    }

    public void setEDAD(int EDAD) {
        this.EDAD = EDAD;
    }


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(@NonNull Parcel parcel, int i) {
        parcel.writeString(UID);
        parcel.writeString(NOMBRES);
        parcel.writeString(APELLIDOS);
        parcel.writeString(EMAIL);
        parcel.writeString(IMAGEN);
        parcel.writeInt(EDAD);
    }
}
