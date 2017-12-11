package Client;

public class PrepareConn {

	public static boolean returnKerberos(long id , String pwd) throws Exception{
		byte[] receiveBuffer = new byte[8216];
		receiveBuffer=ClientOperation.clientToAS(id, 2);
		receiveBuffer=ClientOperation.clientToTgs(id,pwd,3,receiveBuffer);
		byte num=ClientOperation.clientToPSever(6,receiveBuffer);
		if (num==7){
			return true;	
		}
		return false;
	}
}
