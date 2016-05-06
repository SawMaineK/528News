package com.smk.model;

import java.io.Serializable;

public class CityNews implements Serializable {
		
	private Integer id;
	private String title;
	private String description;
	private String image;
	private String credit_image_url;
	private String credit_name;
	private String created_at;
	private String updated_at;

	public CityNews(String title, String description, String image) {
		this.title = title;
		this.description = description;
		this.image = image;
	}

	public Integer getId() {
		return id;
	}
	public void setId(Integer id) {
		this.id = id;
	}
	public String getTitle() {
		return title;
	}
	public void setTitle(String title) {
		this.title = title;
	}
	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
	}
	public String getImage() {
		return image;
	}
	public void setImage(String image) {
		this.image = image;
	}
	public String getCreated_at() {
		return created_at;
	}
	public void setCreated_at(String created_at) {
		this.created_at = created_at;
	}
	public String getUpdated_at() {
		return updated_at;
	}
	public void setUpdated_at(String updated_at) {
		this.updated_at = updated_at;
	}

	public String getCredit_image_url() {
		return credit_image_url;
	}

	public void setCredit_image_url(String credit_image_url) {
		this.credit_image_url = credit_image_url;
	}

	public String getCredit_name() {
		return credit_name;
	}

	public void setCredit_name(String credit_name) {
		this.credit_name = credit_name;
	}

	@Override
	public String toString() {
		return "CityNews [id=" + id + ", title=" + title + ", description="
				+ description + ", image=" + image + ", created_at="
				+ created_at + ", updated_at=" + updated_at + "]";
	}
	
	
}
