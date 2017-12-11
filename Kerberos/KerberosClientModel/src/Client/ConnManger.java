package Client;

public class ConnManger {
	private SocketConn conn = null;
	
	public ConnManger(String server){
		switch(ServerType.getValue(server)){
		case AS: conn = new ASConn(); break;
		case TGS: conn = new TGSConn(); break;
		case CHATSERVER: conn = ChatServerConn.getConn(); break;
		case FILESERVER: conn = FileServerConn.getConn(); break;
		}
	}
	
	public SocketConn getConn(){
		return conn;
	}
}
