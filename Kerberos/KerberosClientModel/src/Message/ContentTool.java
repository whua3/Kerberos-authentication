package Message;

import java.util.ArrayList;
import java.util.List;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import Databean.User;

public class ContentTool {
	
	public static List<User> getUsser(byte[] content){
		
		String buffer = new String(content);
		List<User> usr = new ArrayList<User>();
		System.out.printf(buffer);
		JSONObject jsonObj = JSONObject.fromObject(buffer);
		JSONArray jsonArray = jsonObj.getJSONArray("data");
		for(int i=0; i<jsonArray.size(); i++ ){
			JSONObject jsonObject = jsonArray.getJSONObject(i);
			String name = jsonObject.getString("name");
			long id = jsonObject.getLong("id");
			usr.add(new User(id,name));
		}
		return usr;
	}
}
