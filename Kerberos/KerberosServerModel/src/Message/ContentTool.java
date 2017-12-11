package Message;

import java.util.List;

import net.sf.json.JSONObject;
import Databean.Information;
import Databean.User;

public class ContentTool {
	
	/**
	 * 将报文的content转为String
	 * @param content
	 * @return
	 */
	public static String getInfor(byte[] content){
		return new String(content);
	}
	
	/**
	 * 从报文的content字段中解出一条Infomation
	 * @param content
	 * @return
	 */
	public static Information getInfo(byte[] content){
		String con = new String(content);
		JSONObject jsonObj = JSONObject.fromObject(con);
		JSONObject jsonObject = jsonObj.getJSONObject("data");
		return new Information(jsonObject.getInt("ID"),jsonObject.getString("content"),jsonObject.getLong("from"),jsonObject.getLong("to"));
	}
	
	/**
	 * 将一条消息转换成json格式的字符串的字节数组
	 * @param info
	 * @return
	 */
	public static byte[] getInfoBytes(Information info){
		JSONObject jsonObject = new JSONObject();
		jsonObject.put("data", info);
		return jsonObject.toString().getBytes();
	}
	
	/**
	 * 将一组消息转换成json格式的字符串的字节数组
	 * @param infos
	 * @return
	 */
	public static byte[] getInfoBytes(List<Information> infos){
		JSONObject jsonObject = new JSONObject();
		jsonObject.put("data", infos);
		return jsonObject.toString().getBytes();
	}
	
	/**
	 * 将一个user转换成json格式的字符串的字节数组
	 * @param u
	 * @return
	 */
	public static byte[] getUserBytes(User u){
		JSONObject jsonObject = new JSONObject();
		jsonObject.put("data", u);
		return jsonObject.toString().getBytes();
	}
	
	/**
	 * 将一组user信息转换成json格式，返回其字符串的字节数组
	 * @param us
	 * @return
	 */
	public static byte[] getUserBytes(List<User> us){
		JSONObject jsonObject = new JSONObject();
		jsonObject.put("data", us);
		return jsonObject.toString().getBytes();
	}
	
	
}
