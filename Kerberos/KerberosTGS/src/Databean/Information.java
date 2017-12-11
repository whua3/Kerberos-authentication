package Databean;

public class Information {
	private int ID;
	private String content = null;
	private long from;
	private long to;
	public static int TYPE = 1;
	
	/**
	 * 
	 * @param id 消息id
	 * @param con 消息内容文本
	 * @param f 源
	 * @param t 目的
	 */
	public Information(int id, String con, long f, long t){
		this.ID = id;
		this.content = con;
		this.from = f;
		this.to = t;
	}

	
	public void setID(int iD) {
		ID = iD;
	}


	public void setContent(String content) {
		this.content = content;
	}


	public void setFrom(long from) {
		this.from = from;
	}


	public void setTo(long to) {
		this.to = to;
	}


	public int getID() {
		return ID;
	}

	public String getContent() {
		return content;
	}

	public long getFrom() {
		return from;
	}

	public long getTo() {
		return to;
	}
	
	public String toString(){
		return "{ \"ID\":"+ID+", \"content\":\""+content+"\", \"from\":"+from+", \"to\":"+to+" }";
	}
	
//	public static void main(String[] argv){
//		Information info = new Information(1,"hello wrold! sdsaf fwdasd dfsdsaf", 23123, 23131);
//		System.out.println(info);
//	}
}
