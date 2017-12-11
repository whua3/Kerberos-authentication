package Client;

public enum ServerType {
	AS,TGS,CHATSERVER,FILESERVER;
	public static ServerType getValue(String server){
		return valueOf(server.toUpperCase());
	}
}
