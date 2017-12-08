import java.io.Serializable;
import java.net.InetAddress;
import java.util.Random;

public class Key implements Serializable{
	private InetAddress ip;
	private String password=new String();
	public int rank;
	Key(String pass){
		try {
			ip=InetAddress.getLocalHost();
		}
		catch(Exception e) {
			e.printStackTrace();
		}

		password = pass;
		rankSet();
	}

	InetAddress getInetAddress() {
		return ip;
	}

	boolean checkPassword(String x) {
		return x.equals(password);
	}
	boolean checkPassword(Key x) {
		return x.password.equals(password);
	}
	void rankSet() {
		Random rand=new Random();
		rank = rand.nextInt(64000); 
	}
}
