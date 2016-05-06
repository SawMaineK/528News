package com.smk.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class CurrecyRate implements Serializable{

@SerializedName("info")
@Expose
private String info;
@SerializedName("description")
@Expose
private String description;
@SerializedName("timestamp")
@Expose
private String timestamp;
@SerializedName("rates")
@Expose
private Rates rates;

/**
* 
* @return
* The info
*/
public String getInfo() {
return info;
}

/**
* 
* @param info
* The info
*/
public void setInfo(String info) {
this.info = info;
}

/**
* 
* @return
* The description
*/
public String getDescription() {
return description;
}

/**
* 
* @param description
* The description
*/
public void setDescription(String description) {
this.description = description;
}

/**
* 
* @return
* The timestamp
*/
public String getTimestamp() {
return timestamp;
}

/**
* 
* @param timestamp
* The timestamp
*/
public void setTimestamp(String timestamp) {
this.timestamp = timestamp;
}

/**
* 
* @return
* The rates
*/
public Rates getRates() {
return rates;
}

/**
* 
* @param rates
* The rates
*/
public void setRates(Rates rates) {
this.rates = rates;
}

}