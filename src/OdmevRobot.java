import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.Timer;

public class OdmevRobot implements KeyListener {
	protected ChatFrame chat;
	protected String vzdevek;
	private long cas;
	
	public OdmevRobot(ChatFrame chat, String vzdevek, long cas) {
		this.chat = chat;
		this.vzdevek = vzdevek;
		this.cas = cas;
		this.chat.input.addKeyListener(this);
	}

	@Override
	public void keyPressed(KeyEvent e) {
		if (e.getSource() == chat.input) {
			if (e.getKeyChar() == '\n') {
				Timer zamik = new Timer();
				zamik.schedule(new Odmev(this, chat.input.getText()), cas);
			}
		}
	}

	@Override
	public void keyReleased(KeyEvent arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void keyTyped(KeyEvent arg0) {
		// TODO Auto-generated method stub
		
	}
}
