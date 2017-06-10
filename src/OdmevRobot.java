import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.util.Arrays;
import java.util.Timer;

public class OdmevRobot implements KeyListener {
	protected ChatFrame chat;
	protected String vzdevek;
	protected long cas;
	protected String vzdevekRobot;
	protected boolean isActive;
	
	public OdmevRobot(ChatFrame chat, String vzdevek, long cas) {
		this.chat = chat;
		this.vzdevek = vzdevek;
		this.cas = cas;
		isActive = false;
		chat.input.addKeyListener(this);
		String vmesni = stringCopy(vzdevek);
		vmesni += "'s echo";
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
	
	@Override
	public void keyPressed(KeyEvent e) {
		if (vzdevek.equals(chat.getImeEditor().getText()) && isActive) {
			if (e.getSource().equals(chat.input)) {
				if (e.getKeyChar() == '\n') {
					System.out.println("Sproži timer!");
					Timer zamik = new Timer();
					zamik.schedule(new Odmev(this, chat.input.getText()), cas);
				}
			}
		}
	}
	
	public void newMessage(String message) {
		if (isActive) {
			Timer zamik = new Timer();
			zamik.schedule(new Odmev(this, message), cas);
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