import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.Arrays;
import java.util.Timer;

public class OdmevRobot implements KeyListener {
	private ChatFrame chat;
	private String vzdevek;
	private long cas;
	private String vzdevekRobot;
	private boolean isActive;
	
	public OdmevRobot(ChatFrame chat, String vzdevek, long cas) {
		this.chat = chat;
		this.vzdevek = vzdevek;
		this.cas = cas;
		isActive = false;
		chat.getInput().addKeyListener(this);
		String vmesni = stringCopy(vzdevek);
		vmesni += "'s echo ";
		vmesni += Integer.toString((int) cas);
		int st = 1;
		while (Arrays.asList(chat.getOnline()).contains(vmesni)) {
			vmesni += ' ';
			vmesni += Integer.toString(st);
			st++;
		}
		vzdevekRobot = vmesni;
	}
	
	public OdmevRobot(ChatFrame chat, long cas) {
		this(chat, chat.getImeEditor().getText(), cas);
	}
	
	public String stringCopy(String a) {
		String b = new String();
		int n = a.length();
		for (int i = 0; i < n; i++) {
			b += a.charAt(i);
		}
		return b;
	}
	
	public void activate() {
		if (!isActive) {
			try {
				chat.robotLogin(vzdevekRobot);
				isActive = true;
			} catch (Exception e) {
				System.out.println("Robot neuspešno prijavljen!");
			}
		}
	}
	
	public void cancel() {
		if (isActive) {
			chat.robotLogout(vzdevekRobot);
			isActive = false;
		}
	}
	
	public void newMessage(String message) {
		if (isActive) {
			Timer zamik = new Timer();
			zamik.schedule(new Odmev(this, message), cas);
		}
	}
	
	public ChatFrame getChat() {
		return chat;
	}

	public void setChat(ChatFrame chat) {
		this.chat = chat;
	}

	public String getVzdevek() {
		return vzdevek;
	}

	public void setVzdevek(String vzdevek) {
		this.vzdevek = vzdevek;
	}

	public long getCas() {
		return cas;
	}

	public void setCas(long cas) {
		this.cas = cas;
	}

	public String getVzdevekRobot() {
		return vzdevekRobot;
	}

	public void setVzdevekRobot(String vzdevekRobot) {
		this.vzdevekRobot = vzdevekRobot;
	}

	public boolean isActive() {
		return isActive;
	}

	public void setActive(boolean isActive) {
		this.isActive = isActive;
	}

	@Override
	public void keyPressed(KeyEvent e) {
		if (vzdevek.equals(chat.getImeEditor().getText()) && isActive) {
			if (e.getSource().equals(chat.getInput())) {
				if (e.getKeyChar() == '\n' && !chat.getInput().getText().isEmpty()) {
					System.out.println("Sproži timer!");
					Timer zamik = new Timer();
					zamik.schedule(new Odmev(this, chat.getInput().getText()), cas);
				}
			}
		}
	}

	@Override
	public void keyReleased(KeyEvent e) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void keyTyped(KeyEvent e) {
		// TODO Auto-generated method stub
		
	}
}