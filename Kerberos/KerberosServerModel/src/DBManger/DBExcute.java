package DBManger;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import net.sf.json.JSONObject;

import Databean.Information;
import Databean.User;


public class DBExcute {
	private static String sql;
	private static ResultSet rs;
	
	/**
	 * 用户注册
	 * userName用户名（昵称）
	 * passwd密码
	 * 成功返回用userID
	 * 失败返回0
	 */
	public static int register(String userName, String passwd) {
		sql = "insert into user(UserName,Passwd) values ('" + userName + "','"
				+ passwd + "')";
		DataConn.OpenConn();
		int re = DataConn.executeUpdate(sql);
		if (re == 0) {
			System.err.println("注册失败，请重试！");
			return 0;
		}
		String sql2 = "select UserID from user where UserName='" + userName
				+ "' && Passwd='" + passwd + "'";
		int userID = 0;
		rs = DataConn.executeQuery(sql2);
		try {
			while (rs.next()) {
				userID = rs.getInt(1);
				System.out.println("userID：" + rs.getString(1));
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return userID;
	}
	
	/**
	 * 用户登录，更改用户状态
	 * userID用户ID
	 * 登录成功返回用户名
	 * 失败返回null
	 */
	public static String logIn(int userID) {
		// 
		String sql2 = "update user set Status=1 where UserID='" + userID
				+ "'";
		DataConn.OpenConn();
		int re = DataConn.executeUpdate(sql2);
		if (re == 0) {
			System.err.println("用户状态更新失败！");
			return null;
		}
		sql = "select UserName from user where UserID='" + userID + "'";
		rs = DataConn.executeQuery(sql);
		String usrname = null;
		try {
			while (rs.next()) {
				usrname = rs.getString(1);
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return usrname;
	}

	/**
	 * 更改用户密码
	 * newPasswd新密码
	 * userID用户ID
	 * 成功返回1
	 * 失败返回0
	 */
	public static int changePasswd(int userID, String newPasswd) {
		sql = "update user set Passwd='" + newPasswd + "' where UserID='"
				+ userID + "'";
		DataConn.OpenConn();
		int re = DataConn.executeUpdate(sql);
		if (re == 0) {
			System.err.println("密码更新失败！");
			return 0;
		}
		System.out.println("密码更新成功！");
		return 1;
	}
	
	/**
	 * 用户下线
	 * userID用户ID
	 * 成功返回1
	 * 失败返回0
	 */
	public static int offline(int userID) {
		sql = "update user set Status=0 where UserID='" + userID + "'";
		DataConn.OpenConn();
		int re = DataConn.executeUpdate(sql);
		if (re == 0) {
			System.err.println("用户下线失败！");
			return 0;
		}
		System.out.println("用户下线成功！");
		return 1;
	}
	
	/**
	 * 添加好友
	 * userID用户ID
	 * friendID要添加的好友ID
	 * 成功返回1
	 * 失败返回0
	 */
	public static int addFriend(int userID, int friendID) {
		// 在该用户的好友列表中查看是否已经添加该好友
		sql = "select FriendID from User_user where UserID='" + userID + "'";
		DataConn.OpenConn();
		rs = DataConn.executeQuery(sql);
		int temp;
		try {
			while (rs.next()) {
				temp = rs.getInt(1);
				if (temp == friendID){
					return 0;
				}
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		sql = "insert into User_user (UserID, FriendID) values ('"+userID+"','"+friendID+"')";
		int re = DataConn.executeUpdate(sql);
		if (re == 0) {
			System.err.println("好友添加失败！");
			return 0;
		}
		sql = "insert into User_user (UserID, FriendID) values ('"+friendID+"','"+userID+"')";
		re = DataConn.executeUpdate(sql);
		if (re == 0) {
			System.err.println("好友添加失败！");
			return 0;
		}
		return 0;
	}
	

	/**
	 * 查看好友
	 * userID用户ID
	 * 成功返回1，并输出好友的ID和用户名
	 * 失败返回0
	 */
	public static List<User> viewFriends(int myid) {
		sql = "select FriendID,UserName from User_user,user where User_user.FriendID=user.UserID AND User_user.UserID='"
				+ myid + "'";
		DataConn.OpenConn();
		rs = DataConn.executeQuery(sql);
		List<User> persons = new ArrayList<User>();
		try {
			while (rs.next()) {
				int id = rs.getInt(1);
				String name = rs.getString(2);
				User person = new User(id,name);
				persons.add(person);
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return persons;
	}
	
	/**
	 * 记录消息,将消息记录到数据库
	 * status 消息发送状态，接收和未接收
	 * 成功返回1
	 * 失败返回0
	 */
	public static int recordInformation(Information info, int status) {
		sql = "insert into info (InfoID,Type,Content) values ('"+ info.getID() +"','"+ info.TYPE + "','"
				+ info.getContent() + "')";
		DataConn.OpenConn();
		int re = DataConn.executeUpdate(sql);
		if (re == 0) {
			System.err.println("消息记录失败！");
			return 0;
		}
		Date date = new Date();
		SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmss");
		String time = sdf.format(date);
		String sql3="insert into User_info (InfoId,Sender,Receiver,Status,Time) values " +
				"('"+info.getID()+"','"+info.getFrom()+"','"+info.getTo()+"','"+status+"','"+time+"')";
		re = DataConn.executeUpdate(sql3);
		if (re == 0) {
			System.err.println("消息记录失败！");
			return 0;
		}
		return 1;
	}

	/**
	 * AS获取用户密码
	 * userID用户ID
	 * 成功返回密码
	 * 失败返回null
	 */
	public static String getPasswd(int userID) {
		sql = "select Passwd from user where UserID='" + userID + "'";
		DataConn.OpenConn();
		rs = DataConn.executeQuery(sql);
		String passwd = null;
		try {
			while (rs.next()) {
				passwd = rs.getString(1);
			}
			System.out.println("密码为：" + passwd);
			return passwd;
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return null;
	}
	
	/**
	 * 接到消息后，消息状态改变
	 * to消息的接收方
	 * 成功返回1
	 * 失败返回0
	 */
	public static int infoStatusChange(int to) {
		sql = "update User_info set Status=1 where Receiver='" + to + "'";
		DataConn.OpenConn();
		int re = DataConn.executeUpdate(sql);
		if (re == 0) {
			System.err.println("消息状态更新失败！");
			return 0;
		}
		return 1;
	}
	
	/**
	 * 查看与某个好友的聊天记录
	 * userID用户ID
	 * friendID好友ID
	 * 成功输出聊天记录，并返回1
	 * 失败返回0
	 */
	public static List<Information> checkChatHistory(int userID, int friendID) {
		sql = "select Content,info.InfoID,Sender,Receiver from User_info, info where (User_info.InfoId=info.InfoID) AND ((Sender='" + userID
				+ "' && Receiver='" + friendID + "') || (Sender='" + friendID
				+ "' && Receiver='" + userID + "')) AND Type='1'";
		DataConn.OpenConn();
		rs = DataConn.executeQuery(sql);
		List<Information> informations = new ArrayList<Information>();
		try {
			while (rs.next()) {
				String content = rs.getString(1);
				int id = rs.getInt(2);
				int from = rs.getInt(3);
				int to = rs.getInt(4);
				Information info = new Information(id,content,from,to);
				informations.add(info);
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return informations;
	}
	
	/**
	 * 获取用户的离线消息
	 * @param userId
	 * @return
	 */
	public static List<Information> getOfflineInfo(int userId){
		sql = "select Content,info.InfoID,Sender from User_info, info where User_info.InfoId=info.InfoID AND Status='0' AND Receiver='"+userId+"'";
		DataConn.OpenConn();
		rs = DataConn.executeQuery(sql);
		List<Information> informations = new ArrayList<Information>();
		try {
			while (rs.next()) {
				String content = rs.getString(1);
				int id = rs.getInt(2);
				int from = rs.getInt(3);
				Information info = new Information(id,content,from,userId);
				informations.add(info);
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return informations;
	}
	
	public static int getMAXInfoID(){
		sql = "select max(InfoID) from info";
		DataConn.OpenConn();
		rs = DataConn.executeQuery(sql);
		int value = 0;
		try {
			while (rs.next()) {
				value = rs.getInt(1);
			}
		}catch (SQLException e) {
			e.printStackTrace();
		}
		return value;
	}
	
//	public static void main(String[] argv){
//		
//		System.out.println(getMAXInfoID());
//		
//	}
}
