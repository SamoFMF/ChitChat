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
		robot.getChat().sendMessage(robot.getVzdevekRobot(), text);
	}

}
