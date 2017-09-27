package com.exam.zzz_other_menu;

// 소그룹 정보입력
public class Select_mart_Model{

	private String group;
	private String MartName;
	private int BookMarker;
	private long id;
	
	public String getMartName()
	{
		return MartName;
	}

	public void setMartName(String MartName)
	{
		this.MartName = MartName;
	}
	
	public int getBookMarker()
	{
		return BookMarker;
	}
	
	public void setBookMarker(int BookMarker)
	{
		this.BookMarker = BookMarker;
	}

	public String getGroup()
	{
		return group;
	}

	public void setGroup(String group)
	{
		this.group = group;
	}

	public long getId()
	{
		return id;
	}

	public void setId(long id)
	{
		this.id = id;
	}
}
