package com.freelunch.foodtaboo;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class GridViewBindData {
	public static  List<Map<String, Object>> setData(String matchstr, List<PriData> dataList) {
		List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();

		Map<String, Object> map;

		for (int i=0; i<dataList.size(); i++)
		{
			if (dataList.get(i).srcName.equalsIgnoreCase(matchstr))
			{
				map = new HashMap<String, Object>();
				map.put("photo", R.drawable.food);
				map.put("name", dataList.get(i).dstName);
				map.put("degree", R.drawable.star_1 + dataList.get(i).degree - 1);
				map.put("hint", dataList.get(i).hint);

				list.add(map);		
			}
			else if (dataList.get(i).dstName.equalsIgnoreCase(matchstr))
			{
				map = new HashMap<String, Object>();
				map.put("photo", R.drawable.food);
				map.put("name", dataList.get(i).srcName);
				map.put("degree", R.drawable.star_1 + dataList.get(i).degree - 1);
				map.put("hint", dataList.get(i).hint);

				list.add(map);					
			}
		}
		
		return list;
	}
	
	public static  List<Map<String, Object>> setData(List<String> dataList) {
		List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();

		Map<String, Object> map;

		for (int i=0; i<dataList.size(); i++)
		{
			map = new HashMap<String, Object>();
			map.put("photo", R.drawable.food);
			map.put("name", dataList.get(i));
			map.put("degree", null);
			map.put("hint", null);

			list.add(map);		
		}
		
		return list;
	}	
}
