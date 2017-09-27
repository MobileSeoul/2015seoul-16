package com.exam.zzz_other_menu;

// 소그룹 정보입력
public class Model {


	private String MartName;
	private String ItemName;
	private int Price;
	private int Amount;
	private double Lat;
	private double Lon;

	public int getPrice()
	{
		return Price;
	}
	
	public void setPrice(int Price)
	{
		this.Price = Price;
	}

	public String getMartName()
	{
		return MartName;
	}

	public void setMartName(String MartName)
	{
		this.MartName = MartName;
	}

	public int getAmount()
	{
		return Amount;
	}

	public void setAmount(int Amount)
	{
		this.Amount = Amount;
	}

	public String getItemName()
	{
		return ItemName;
	}

	public void setItemName(String ItemName)
	{
		this.ItemName = ItemName;
	}
	
	public double getLat()
	{
		return Lat;
	}
	
	public void setLat(double Lat)
	{
		this.Lat = Lat;
	}
	
	public double getLon()
	{
		return Lon;
	}
	
	public void setLon(double Lon)
	{
		this.Lon = Lon;
	}
}
