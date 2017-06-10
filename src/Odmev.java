import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.util.TimerTask;

public class Odmev extends TimerTask {
	private OdmevRobot robot;
	private String text;
	
	public Odmev(OdmevRobot robot, String text) {
		this.robot = robot;
		this.text = text;
	}
	
	
	@Override
	public void run() {
		System.out.println("Poslali smo sporoèilo!");
		robot.chat.sendMessage(robot.vzdevekRobot, text);
	}

}
