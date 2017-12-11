package APP;

import wgl.Login;
import Client.ConnManger;
import Databean.User;

public class Application {
	
	public static User user = null; //全局唯一的用户身份
	
	public static ConnManger cm = null;  //全局唯一的连接管理
	
	public static final byte ON_LINE = 1; //上线
	public static final byte ADD_FRIEND = 2; //添加好友
	public static final byte GET_CHATHISTORY = 3; //获取聊天记录
	public static final byte ALTER_PWD = 4; //修改密码
	public static final byte OFF_LINE = 5; //下线
	public static final byte CHAT = 6; //聊天
	public static final byte GET_FRIEND = 7; //获取好友列表
	public static final byte GET_OFFLINEINFO = 8; 
	 
	 //server2上的服务
	public static final int CHECK_CHAT_HISTORY = 9;//查看聊天记录
	public static final int UPLOAD_FILE = 10;//上传文件
	public static final int DOWNLOAD_FILE = 11;//下载文件
	public static final int CHANGE_FILE_STATUS = 12;//更改文件接收状态
	 
	 //源
	public static final int AS = 1;//AS服务器
	public static final int TGS = 2;//TGS服务器
	public static final int PSERVERCHAT = 3;//应用服务器（消息）
	public static final int PSERVERFILE = 4;//应用服务器（文件）
	 
	 //反馈respond
	public static final byte NEGATIVE = 0;//该字段未被激活
	public static final byte SUCCESS = 1;//操作成功
	public static final byte DENY = 2;//拒绝服务
	public static final byte FAILTOUPLOAD = 3;//文件上传失败
	public static final byte FAILTODOWNLOAD = 4;//文件下载失败
	public static final byte USEROFFLINE = 5;//消息投递成功，但用户处于离线状态
	public static final byte OVERTIME = 6;//消息超时
	public static final byte FAIL = 9;
	
	/**
	 * 主函数，应用入口
	 * @param argv
	 */
	public static void main(String[] argv){
		new Login();
	}
}
