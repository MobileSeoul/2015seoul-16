package com.exam.zzz_other_menu;

import java.util.ArrayList;

import android.content.Context;

import android.util.Log;

import android.view.LayoutInflater;
import android.view.View.OnClickListener;
import android.view.View;
import android.view.ViewGroup;

import android.widget.BaseExpandableListAdapter;
import android.widget.ImageView;
import android.widget.Button;
import android.widget.TextView;

import com.example.n_mart.R;

public class Adapter extends BaseExpandableListAdapter {

	int values = 0;
	int uprange = 20;
	int downrange = 0;

	// LayoutInflater를 가저오려면 컨텍스트를 넘겨받아야 한다.

	private Context context;

	// 대 그룹에 보여줄 리스트 

	private ArrayList<GM> groups; 

	// 대 그룹을 눌렀을때 보여주는 자식 리스트

	private ArrayList<ArrayList<Model>> children;

	// xml으로 생성한 UI를 가저다 준다.

	private LayoutInflater inflater;

	public Adapter(Context context, ArrayList<GM> gropus, ArrayList<ArrayList<Model>> children)
	{
		this.groups = gropus;
		this.children = children;
		this.inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		this.context = context;
	}

	public ArrayList<ArrayList<Model>> getAllList()
	{
		return this.children;
	}

	@Override
	public boolean areAllItemsEnabled()
	{
		return false;
	}

	@Override
	public Model getChild(int groupPosition, int childPosition)
	{
		return children.get(groupPosition).get(childPosition);
	}

	@Override
	public long getChildId(int groupPosition, int childPosition)
	{
		return children.get(groupPosition).get(childPosition).getPrice();
	}

	// children을 보여준다. ArrayAdapter의 getView와 동일하게 처리하면 된다.

	@Override
	public View getChildView(int groupPosition, int childPosition, boolean isLastChild, View convertView, ViewGroup parent)
	{
		final Model model = getChild(groupPosition, childPosition);
		View view = convertView;
		if (view == null)
		{
			view = inflater.inflate(R.layout.child_row, null);
		}

		if (model != null)
		{
			ImageView img = (ImageView) view.findViewById(R.id.imageView1);
			TextView childName = (TextView) view.findViewById(R.id.childname);
			TextView childAge = (TextView) view.findViewById(R.id.rgb);
			final TextView childAmount = (TextView) view.findViewById(R.id.mainamounttv);
			Button childUpButton = (Button) view.findViewById(R.id.mainupbtn);
			Button childDownButton = (Button) view.findViewById(R.id.maindownbtn);

			childName.setText(model.getItemName());
			childAge.setText(model.getPrice() + "원");
			childAmount.setText(model.getAmount() + "");

			childUpButton.setFocusable(false);
			childDownButton.setFocusable(false);

			childUpButton.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {

					Log.v("Adapter.java", "getChildView내의 +버튼 눌렀을때 작동함수");

					values = Integer.parseInt(childAmount.getText().toString());

					if (values >= downrange && values < uprange) {
						values++;
					} else if (values > uprange) {
						values = downrange;
					}
					childAmount.setText("" + values);
					model.setAmount(values);
				}
			});

			childDownButton.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {

					Log.v("list.java", "getChildView내의 -버튼 눌렀을때 작동함수");

					values = Integer.parseInt(childAmount.getText().toString());

					if (values > downrange && values <= uprange) {

						if (values == 0) {
						} else {
							values--;
						}

					} else if (values < downrange) {
						values = uprange;
					}

					childAmount.setText(values + "");
					model.setAmount(values);
				}
			});

			if (model.getItemName().contains("사과")) {
				img.setImageResource(R.drawable.apple);
			} else if (model.getItemName().contains("배")) {
				if (model.getItemName().contains("배추")) {
					img.setImageResource(R.drawable.bechoo);
				} else{
					img.setImageResource(R.drawable.pear);
				}
			} else if (model.getItemName().contains("무")) {
				img.setImageResource(R.drawable.moo);
			} else if (model.getItemName().contains("양파")) {
				img.setImageResource(R.drawable.onion);
			} else if (model.getItemName().contains("상추")) {
				img.setImageResource(R.drawable.sangchoo);
			} else if (model.getItemName().contains("오이")) {
				img.setImageResource(R.drawable.oe);
			} else if (model.getItemName().contains("호박")) {
				img.setImageResource(R.drawable.pumpkin);
			} else if (model.getItemName().contains("쇠고기")) {
				img.setImageResource(R.drawable.beef);
			} else if (model.getItemName().contains("돼지고기")) {
				img.setImageResource(R.drawable.pork);
			} else if (model.getItemName().contains("닭고기")) {
				img.setImageResource(R.drawable.chicken);
			} else if (model.getItemName().contains("달걀")) {
				img.setImageResource(R.drawable.egg);
			} else if (model.getItemName().contains("조기")) {
				img.setImageResource(R.drawable.jogi);
			} else if (model.getItemName().contains("명태")) {
				img.setImageResource(R.drawable.myungtae);
			} else if (model.getItemName().contains("동태")) {
				img.setImageResource(R.drawable.myungtae);
			} else if (model.getItemName().contains("오징어")) {
				img.setImageResource(R.drawable.ojing);
			} else if (model.getItemName().contains("고등어")) {
				img.setImageResource(R.drawable.gofish);
			}
		}
		return view;
	}

	@Override
	public int getChildrenCount(int groupPosition)
	{
		return children.get(groupPosition).size();
	}

	@Override
	public String getGroup(int groupPosition)
	{
		return groups.get(groupPosition).getName();
	}

	@Override
	public int getGroupCount()
	{
		return groups.size();
	}

	@Override
	public long getGroupId(int groupPosition)
	{
		return groupPosition;
	}

	// 대그룹을 보여준다. ArrayAdapter의 getView와 동일하게 처리하면 된다.

	@Override
	public View getGroupView(int groupPosition, boolean isExpanded, View convertView, ViewGroup parent)
	{
		View view = convertView;
		String group = (String) getGroup(groupPosition);
		if (view == null)
		{
			view = inflater.inflate(R.layout.group_row, null);
		}
		TextView tv = (TextView) view.findViewById(R.id.groupName);
		ImageView img = (ImageView) view.findViewById(R.id.Select_mart_imageView2);

		tv.setText(group);

		if (group.contains("과일")) {
			img.setImageResource(R.drawable.list_down);
		} else if (group.contains("농산물")) {
			img.setImageResource(R.drawable.list_down);
		} else if (group.contains("축산물")) {
			img.setImageResource(R.drawable.list_down);
		} else if (group.contains("수산물")) {
			img.setImageResource(R.drawable.list_down);
		}


		if(isExpanded){
			if (group.contains("과일")) {
				img.setImageResource(R.drawable.list_up);
			} else if (group.contains("농산물")) {
				img.setImageResource(R.drawable.list_up);
			} else if (group.contains("축산물")) {
				img.setImageResource(R.drawable.list_up);
			} else if (group.contains("수산물")) {
				img.setImageResource(R.drawable.list_up);
			}
		}

		return view;
	}

	@Override
	public boolean hasStableIds()
	{
		return true;
	}

	@Override
	public boolean isChildSelectable(int groupPosition, int childPosition)
	{
		return true;
	}
}