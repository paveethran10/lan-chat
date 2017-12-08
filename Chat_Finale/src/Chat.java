import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.MulticastSocket;
import java.util.Scanner;
public class Chat {
	static boolean interrupt=true;
	static Key inKey,outKey;

    public static void main(String[] args) throws IOException, InterruptedException {
    	Scanner s=new Scanner(System.in);
    	int portNumber=13251;
    	MulticastSocket ms=new MulticastSocket(portNumber);
    	//MS is @port 13251
    	ms.joinGroup(InetAddress.getByName("225.225.225.227"));
    	ms.setBroadcast(true);
    	//System.out.println("Connection Established !");
    	//System.out.println("Listening @"+ms.getInetAddress());

    	//Converting Object to transmittable byte array
    	ByteArrayOutputStream baos=new ByteArrayOutputStream();
    	ObjectOutputStream oos=new ObjectOutputStream(baos);
    	System.out.println("Enter the key for connection ! :");
    	outKey=new Key(s.next());
    	oos.writeObject(outKey);
    	//Conversion Finished !
    	//New thread to get interrupted by client when the key matches
    	new Thread(){
    		public void run() {
    			while(true) {
    				MulticastSocket interrupt_socket;
					try {
						interrupt_socket = new MulticastSocket(portNumber);
						interrupt_socket.joinGroup(InetAddress.getByName("225.225.225.227"));
	    				byte b[] = new byte[512];
	    				DatagramPacket dp=new DatagramPacket(b,b.length);
	    				interrupt_socket.receive(dp);
	    				ByteArrayInputStream bais=new ByteArrayInputStream(b);
	    				ObjectInputStream ois=new ObjectInputStream(bais);
	    				//System.out.println("Listening ...");

	    				inKey=(Key)ois.readObject();
	    				if(inKey.getInetAddress().equals(outKey.getInetAddress())) {
	    					//System.out.println("Same Key Detected !!");
	    					continue;
	    					//interrupt=false;
	    					//System.out.println("TRUE");
	    					//interrupt_socket.close();
	    				}
	    				else if(inKey.checkPassword(outKey))
	    				{
	    					System.out.println("Key validation Successful !! ");
	    					Thread.sleep(500);
	    					interrupt=false;
	    					//System.out.println("TRUE");
	    					interrupt_socket.close();
	    					break;
	    				}
					} catch (IOException e) {
						e.printStackTrace();
					} catch (ClassNotFoundException e) {
						e.printStackTrace();
					} catch (InterruptedException e) {
						e.printStackTrace();
					}

    			}

    		}
    	}.start();

    	while(interrupt) {
    		DatagramPacket dp = new DatagramPacket(baos.toByteArray(),baos.toByteArray().length, InetAddress.getByName("225.225.225.227"), portNumber);
    		//The Port number in the datagram packet is the DESTINATION PORT NUMBER !!!
    		ms.send(dp);
    		Thread.sleep(250);											//Milliseconds Delay !!!
    	}
    	ms.close();

    	//System.out.println("Tcp executing");
    	//System.out.println("Inkey Addr:"+inKey.getInetAddress()+"\nRank :"+inKey.rank+"\nOutKey Rank :"+outKey.rank);

    	if(inKey.rank>outKey.rank) {
    			Thread.sleep(3000);
				System.out.println("Starting Client ...");
				new TcpClient(inKey.getInetAddress(),portNumber);
    	}
    	else if(inKey.rank<outKey.rank){
				System.out.println("Starting Server ...");
				new TcpServer(inKey.getInetAddress(),portNumber);
		}
    	else{
    		System.out.println("The universe has conspired a great event.\nVisit your Josiyaar for more details !");
    	}
    }
}
