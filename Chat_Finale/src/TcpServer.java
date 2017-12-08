import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.net.*;
import javax.swing.*;
import javax.swing.event.*;

public class TcpServer extends JFrame implements Runnable
{
	JPanel jp;
	JTextField jt;
	JTextArea ta;
	JLabel l;
	boolean typing;
	ServerSocket ss;
	Timer t;
	Socket s;
	Thread th;
	PrintWriter pw;
	BufferedReader br;

	public TcpServer(InetAddress addr,int portNumber)
	{
		setTitle("TCP Chatbox Chatting with "+ addr.getHostName());
		setDefaultCloseOperation(EXIT_ON_CLOSE);

		// Create a JPanel and set layout
		jp=new JPanel();
		jp.setLayout(new GridLayout(2,1));
		l=new JLabel();
		jp.add(l);

		// Create a timer that executes every 1 millisecond
		t=new Timer(1,new ActionListener(){
			public void actionPerformed(ActionEvent ae)
			{
				// If the user isn't typing, he is thinking
				if(!typing)
				l.setText("Thinking..");
			}
		});
		t.setInitialDelay(2000);

		// Create JTextField, add it.
		jt=new JTextField();
		jp.add(jt);
		// Add typo bar to the south,
		add(jp,BorderLayout.SOUTH);


		// Add a KeyListener
		jt.addKeyListener(new KeyAdapter(){
			public void keyPressed(KeyEvent ke)
			{
				l.setText("You are typing..");
				t.stop();
				typing=true;
				if(ke.getKeyCode()==KeyEvent.VK_ENTER)
					typo(jt.getText());
			}

			public void keyReleased(KeyEvent ke)
			{
				typing=false;
				if(!t.isRunning())
				t.start();
			}
		});
		// Create a textarea
		ta=new JTextArea();

		// Make it non-editable
		ta.setEditable(false);

		// Set some margin, for the text
		ta.setMargin(new Insets(10,10,10,10));

		// Set a scrollpane
		JScrollPane js=new JScrollPane(ta);
		add(js);

		addWindowListener(new WindowAdapter(){
			public void windowOpened(WindowEvent we)
			{
				// Get the focus when window is opened
				jt.requestFocus();
			}
		});

		setSize(500,500);
		setLocationRelativeTo(null);
		setVisible(true);

			try{
				ss=new ServerSocket(portNumber);//Socket for server
				s=ss.accept();//accepts request from client
				//   System.out.println(s);
				//below line reads input from InputStreamReader
				br=new BufferedReader(new InputStreamReader(s.getInputStream()));
				//below line writes output to OutPutStream
				pw=new PrintWriter(s.getOutputStream(),true);
			}catch(Exception e)
			{
			}
			th=new Thread(this);//start a new thread
			th.setDaemon(true);//set the thread as demon
			th.start();

	}

	//This method will called after clicking on Send button.
	public void typo(String text)
	{
		pw.println(jt.getText());
		if(text.trim().isEmpty()) return;
		//write the value of textfield into PrintWriter
		ta.append("You :"+text+"\n");
		jt.setText("");//clean the textfield
		l.setText("");
	}
	String receive;
	//Thread running as a process in background
	public void run() {
		while(true)
		{
			try{
				receive = br.readLine();
				if(receive.trim().isEmpty()) continue;
				ta.append("User :"+receive+"\n");//Append to TextArea
			}catch(Exception e) {}
		}
	}
}
