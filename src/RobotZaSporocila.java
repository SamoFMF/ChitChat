import java.util.Timer;
import java.util.TimerTask;

public class RobotZaSporocila extends TimerTask {
	private ChatFrame chat;
	
	public RobotZaSporocila(ChatFrame chat) {
		this.chat = chat;
	}
	
	public void activate() {
		Timer timer = new Timer();
		timer.scheduleAtFixedRate(this, 0, 100);
	}

	@Override
	public void run() {
		// Posljemo strezniku zahtevo za sporocila
		chat.writeMessages();
	}
}
