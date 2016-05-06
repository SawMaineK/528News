package com.smk.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class NewsCategory implements Serializable{

@SerializedName("id")
@Expose
private Integer id;
@SerializedName("name")
@Expose
private String name;

@SerializedName("background_color")
@Expose
private String backgroundColor;

@SerializedName("background_image")
@Expose
private String backgroundImage;
@SerializedName("advertise_name")
@Expose
private String advertiseName;
@SerializedName("advertise_desc")
@Expose
private String advertiseDesc;
@SerializedName("advertise_image")
@Expose
private String advertiseImage;

public NewsCategory(Integer id, String name, String color, String image, String advName, String advDesc, String advImage) {
	super();
	this.id = id;
	this.name = name;
    this.backgroundColor = color;
    this.backgroundImage = image;
    this.advertiseName = advName;
    this.advertiseDesc = advDesc;
    this.advertiseImage = advImage;
}

/**
* 
* @return
* The id
*/
public Integer getId() {
return id;
}

/**
* 
* @param id
* The id
*/
public void setId(Integer id) {
this.id = id;
}

/**
* 
* @return
* The name
*/
public String getName() {
return name;
}

/**
* 
* @param name
* The name
*/
public void setName(String name) {
this.name = name;
}

@Override
public String toString() {
	return name;
}

	public String getBackgroundColor() {
		return backgroundColor;
	}

	public void setBackgroundColor(String backgroundColor) {
		this.backgroundColor = backgroundColor;
	}

	public String getBackgroundImage() {
		return backgroundImage;
	}

	public void setBackgroundImage(String backgroundImage) {
		this.backgroundImage = backgroundImage;
	}

    public String getAdvertiseName() {
        return advertiseName;
    }

    public void setAdvertiseName(String advertiseName) {
        this.advertiseName = advertiseName;
    }

    public String getAdvertiseDesc() {
        return advertiseDesc;
    }

    public void setAdvertiseDesc(String advertiseDesc) {
        this.advertiseDesc = advertiseDesc;
    }

    public String getAdvertiseImage() {
        return advertiseImage;
    }

    public void setAdvertiseImage(String advertiseImage) {
        this.advertiseImage = advertiseImage;
    }
}