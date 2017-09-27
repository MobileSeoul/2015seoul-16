package com.exam.zzz_other_menu;

import java.io.Serializable;

public class Info_save_class implements Serializable{

	private String mart_name;
	private String name;
	private int price;
	private double lat;
	private double lon;

	public Info_save_class(String mart_name, String name, int price, double lat, double lon) {
		this.mart_name = mart_name;
		this.name = name;
		this.price = price;
		this.lat = lat;
		this.lon = lon;
	}

	public Info_save_class() {
	}

	public void setMartName(String mart_name) {

		this.mart_name = mart_name;
	}

	public void setName(String name) {

		this.name = name;
	}

	public void setPrice(int price) {

		this.price = price;
	}

	public void setLat(int lat) {

		this.lat = lat;
	}

	public void setLon(int lon) {

		this.lon = lon;
	}

	public String getMartName() {

		return mart_name;
	}

	public String getName() {

		return name;
	}

	public int getPrice() {

		return price;
	}

	public double getLat() {

		return lat;
	}

	public double getLon() {

		return lon;
	}
}