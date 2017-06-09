import java.awt.Adjustable;
import java.awt.Color;
import java.awt.Container;
import java.awt.FlowLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.AdjustmentEvent;
import java.awt.event.AdjustmentListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.util.Date;
import java.util.List;

import javax.swing.ButtonGroup;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JRadioButtonMenuItem;
import javax.swing.JScrollBar;
import javax.swing.JScrollPane;
import javax.swing.JSlider;
import javax.swing.JSplitPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.JTextPane;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.SimpleAttributeSet;
import javax.swing.text.StyleConstants;
import javax.swing.text.StyleContext;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.util.ISO8601DateFormat;

public class ChatFrame extends JFrame implements ActionListener, KeyListener, WindowListener {
	
	/**
	 * @param output - Displays messages
	 * @param input - Input window for your message
	 */
	private static final long serialVersionUID = 1L;
	protected JTextField input;
	protected JTextField imeEditor;
	private JButton gumbPrijavi;
	private JButton gumbOdjavi;
	private String komuPosiljamo;
	private RobotZaSporocila robot;
	protected JTextArea dosegljivi;
	private RobotDosegljivi robotDosegljivi;
	private JSplitPane splitter;
	private Color colorMyName, colorOthers, colorMe, colorMsgOthers;
	private JTextPane output;
	private JMenuBar menuBar;
	private JMenu fileMenu;
	private JMenuItem miLogin, miLogout;
	private JMenu optionsMenu;
	private ButtonGroup groupColorsMyName, groupColorsOthers, groupColorsMe, groupColorsMsgOthers;
	private JRadioButtonMenuItem cbRedMyName, cbGreenMyName, cbBlueMyName, cbBlackMyName; // Barve za imena pri sporoèilih, ki jih pošlje uporabnik
	private JRadioButtonMenuItem cbRedOthers, cbGreenOthers, cbBlueOthers, cbBlackOthers; // Barve za imena pri sporoèilih, ki jih pošljejo ostali
	private JRadioButtonMenuItem cbRedMe, cbGreenMe, cbBlueMe, cbBlackMe; // Barve za sporoèila, ki jih pošlje uporabnik
	private JRadioButtonMenuItem cbRedMsgOthers, cbGreenMsgOthers, cbBlueMsgOthers, cbBlackMsgOthers; // Barve za sporoèila, ki jih pošljejo ostali
	private JSlider fontSizeSlider;
	private int fontSize;
	private String[] testNames;
	private JComboBox<String> whoMenu;
	private String[] online;
	private Container pane;
	private GridBagConstraints grid;
	protected JTextPane test;

	public ChatFrame() {
		super();
		// Nastavimo naslov
		setTitle("Chit Chat");
		Container pane = this.getContentPane();
		pane.setLayout(new GridBagLayout());
		
		// Nastavimo default barve in velikost pisave
		colorMyName = Color.BLUE;
		colorMe = Color.red;
		colorOthers = Color.GREEN;
		colorMsgOthers = Color.black;
		fontSize = 12;
		
		// Dodajmo zgornji menu
		// Najprej dodamo menu bar
		menuBar = new JMenuBar();
		setJMenuBar(menuBar);
		
		// Dodamo menu "File"
		fileMenu = new JMenu("File");
		miLogin = new JMenuItem("Login");
		miLogout = new JMenuItem("Logout");
		JMenuItem miExit = new JMenuItem("Exit");
		// Dodamo možnosti v fileMenu
		fileMenu.add(miLogin);
		fileMenu.add(miLogout);
		fileMenu.add(miExit);
		// Dodamo listenerje
		miLogin.addActionListener(this);
		miLogout.addActionListener(this);
		miExit.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if (gumbOdjavi.isEnabled()) {
					// Imamo prijavljenega uporabnika					
					try {
						// Uporabnika poizkusimo odjaviti
						HttpCommands.odjava(imeEditor.getText());
						// Izklopimo oba robota
						robot.cancel();
						robotDosegljivi.cancel();
						System.exit(0);
					} catch (Exception e1) {
						System.out.println("Neuspešna odjava!");
					}
				} else {
					System.exit(0);
				}
			}
		});
		// Vstavimo fileMenu v menuBar
		menuBar.add(fileMenu);
		
		// Ustvarimo menu "Options"
		optionsMenu = new JMenu("Options");
		// Ustvarimo podmenu "Color"
		JMenu colorSubMenu = new JMenu("Color");
		// Ustvarimo subsubmenu "Names"
		JMenu namesSubSubMenu = new JMenu("Names");
		// Ustvarimo subsubsubmenu "My name"
		JMenu myName = new JMenu("My name");
		cbRedMyName = new JRadioButtonMenuItem("Red", true);
		cbBlueMyName = new JRadioButtonMenuItem("Blue");
		cbGreenMyName = new JRadioButtonMenuItem("Green");
		cbBlackMyName = new JRadioButtonMenuItem("Black");
		// Dodamo možnosti v submenu "Color"
		myName.add(cbBlueMyName);
		myName.add(cbRedMyName);
		myName.add(cbGreenMyName);
		myName.add(cbBlackMyName);
		// Dodamo te barve v grupo, ki bo poskrbela, da imamo lahko izbrano le 1
		groupColorsMyName = new ButtonGroup();
		groupColorsMyName.add(cbBlueMyName);
		groupColorsMyName.add(cbRedMyName);
		groupColorsMyName.add(cbGreenMyName);
		groupColorsMyName.add(cbBlackMyName);
		// Dodamo listenerje
		cbRedMyName.addActionListener(this);
		cbBlueMyName.addActionListener(this);
		cbGreenMyName.addActionListener(this);
		cbBlackMyName.addActionListener(this);
		// Dodamo "My name" v "Names"
		namesSubSubMenu.add(myName);
		// Ustvarimo subsubsubmenu "Others"
		JMenu otherNames = new JMenu("Others");
		cbRedOthers = new JRadioButtonMenuItem("Red", true);
		cbBlueOthers = new JRadioButtonMenuItem("Blue");
		cbGreenOthers = new JRadioButtonMenuItem("Green");
		cbBlackOthers = new JRadioButtonMenuItem("Black");
		// Dodamo možnosti v submenu "Color"
		otherNames.add(cbBlueOthers);
		otherNames.add(cbRedOthers);
		otherNames.add(cbGreenOthers);
		otherNames.add(cbBlackOthers);
		// Dodamo te barve v grupo, ki bo poskrbela, da imamo lahko izbrano le 1
		groupColorsOthers = new ButtonGroup();
		groupColorsOthers.add(cbBlueOthers);
		groupColorsOthers.add(cbRedOthers);
		groupColorsOthers.add(cbGreenOthers);
		groupColorsOthers.add(cbBlackOthers);
		// Dodamo listenerje
		cbRedOthers.addActionListener(this);
		cbBlueOthers.addActionListener(this);
		cbGreenOthers.addActionListener(this);
		cbBlackOthers.addActionListener(this);
		cbRedOthers.addActionListener(this);
		// Dodamo "Others" v "Names"
		namesSubSubMenu.add(otherNames);
		// Dodamo "Names" v "Color"
		colorSubMenu.add(namesSubSubMenu);
		
		// Ustvarimo subsubmenu "Messages"
		JMenu messagesSubSubMenu = new JMenu("Messages");
		// Ustvarimo subsubsubmenu "My messages"
		JMenu myMessages = new JMenu("My messages");
		cbRedMe = new JRadioButtonMenuItem("Red", true);
		cbBlueMe = new JRadioButtonMenuItem("Blue");
		cbGreenMe = new JRadioButtonMenuItem("Green");
		cbBlackMe = new JRadioButtonMenuItem("Black");
		// Dodamo možnosti v submenu "Color"
		myMessages.add(cbBlueMe);
		myMessages.add(cbRedMe);
		myMessages.add(cbGreenMe);
		myMessages.add(cbBlackMe);
		// Dodamo te barve v grupo, ki bo poskrbela, da imamo lahko izbrano le 1
		groupColorsMe = new ButtonGroup();
		groupColorsMe.add(cbBlueMe);
		groupColorsMe.add(cbRedMe);
		groupColorsMe.add(cbGreenMe);
		groupColorsMe.add(cbBlackMe);
		// Dodamo listenerje
		cbRedMe.addActionListener(this);
		cbBlueMe.addActionListener(this);
		cbGreenMe.addActionListener(this);
		cbBlackMe.addActionListener(this);
		cbRedMe.addActionListener(this);
		// Dodamo "My messages" v "Messages"
		messagesSubSubMenu.add(myMessages);
		// Ustvarimo subsubsubmenu "Other"
		JMenu otherMessages = new JMenu("Other");
		cbRedMsgOthers = new JRadioButtonMenuItem("Red", true);
		cbBlueMsgOthers = new JRadioButtonMenuItem("Blue");
		cbGreenMsgOthers = new JRadioButtonMenuItem("Green");
		cbBlackMsgOthers = new JRadioButtonMenuItem("Black");
		// Dodamo možnosti v submenu "Color"
		otherMessages.add(cbBlueMsgOthers);
		otherMessages.add(cbRedMsgOthers);
		otherMessages.add(cbGreenMsgOthers);
		otherMessages.add(cbBlackMsgOthers);
		// Dodamo te barve v grupo, ki bo poskrbela, da imamo lahko izbrano le 1
		groupColorsMsgOthers = new ButtonGroup();
		groupColorsMsgOthers.add(cbBlueMsgOthers);
		groupColorsMsgOthers.add(cbRedMsgOthers);
		groupColorsMsgOthers.add(cbGreenMsgOthers);
		groupColorsMsgOthers.add(cbBlackMsgOthers);
		// Dodamo listenerje
		cbRedMsgOthers.addActionListener(this);
		cbBlueMsgOthers.addActionListener(this);
		cbGreenMsgOthers.addActionListener(this);
		cbBlackMsgOthers.addActionListener(this);
		cbRedMsgOthers.addActionListener(this);
		// Dodamo "Other" v "Messages"
		messagesSubSubMenu.add(otherMessages);
		// Dodamo "Messages" v "Colors"
		colorSubMenu.add(messagesSubSubMenu);
		// Dodamo podmenu "Colors" v menu "Options"
		optionsMenu.add(colorSubMenu);
		// Ustvarimo menu "Font Size"
		JMenu fontSizeSubMenu = new JMenu("Font size");
		fontSizeSlider = new JSlider(12, 20, 12);
		fontSizeSlider.setMajorTickSpacing(1);
		fontSizeSlider.setPaintTicks(true);
		fontSizeSlider.setPaintLabels(true);
		// Dodamo listener
		fontSizeSlider.addChangeListener(new ChangeListener() {
			public void stateChanged(ChangeEvent e) {
				JSlider source = (JSlider) e.getSource();
				if (!source.getValueIsAdjusting()) {
					fontSize = source.getValue();
				}
			}
		});
		// Dodamo slider v podmenu
		fontSizeSubMenu.add(fontSizeSlider);
		// Dodamo podmenu v menu "Options"
		optionsMenu.add(fontSizeSubMenu);
		// Dodamo menu "Options" v menuBar
		menuBar.add(optionsMenu);
		
		
		// Ustvarimo panel za zgornjo vrstico (vzdevek, prijava, odjava)
		JPanel zgoraj = new JPanel();
		zgoraj.setLayout(new FlowLayout(FlowLayout.LEFT));
		GridBagConstraints zgorajConstraint = new GridBagConstraints();
		zgorajConstraint.gridx = 0;
		zgorajConstraint.gridy = 0;
		zgorajConstraint.fill = GridBagConstraints.HORIZONTAL;
		zgorajConstraint.weightx = 1;
		zgorajConstraint.weighty = 0;
		pane.add(zgoraj, zgorajConstraint);
		
		// Text "Vzdevek:"
		JLabel vzdevek = new JLabel("Vzdevek:");
		zgoraj.add(vzdevek);
		
		// Polje kamor vpišemo vzdevek
		imeEditor = new JTextField(10);
		zgoraj.add(imeEditor);
		imeEditor.addKeyListener(this);
		
		// Gumb za prijavo
		gumbPrijavi = new JButton("Prijavi!");
		zgoraj.add(gumbPrijavi);
		gumbPrijavi.addActionListener(this);
		
		// Gumb za odjavo
		gumbOdjavi = new JButton("Odjavi!");
		zgoraj.add(gumbOdjavi);
		gumbOdjavi.addActionListener(this);
		gumbOdjavi.setEnabled(false);
		
		// Ustvarimo panel levo od splitterja
		JPanel levo = new JPanel();
		levo.setLayout(new GridBagLayout());
		
		// Ustvarimo panel desno od splitterja
		JPanel desno = new JPanel();
		desno.setLayout(new GridBagLayout());

		// Polje kamor bodo izpisana sporoèila
		output = new JTextPane();
		output.setEditable(false);
		JScrollPane drsnikLevo = new JScrollPane(output);
		scrollToBottom(drsnikLevo);
		GridBagConstraints outputConstraint = new GridBagConstraints();
		outputConstraint.gridx = 0;
		outputConstraint.gridy = 0;
		outputConstraint.fill = GridBagConstraints.BOTH;
		outputConstraint.weightx = 1;
		outputConstraint.weighty = 1;
		levo.add(drsnikLevo, outputConstraint);
		
		// Polje kamor bo izpisan seznam dosegljivih uporabnikov
		dosegljivi = new JTextArea(20, 10);
		dosegljivi.setEditable(false);
		JScrollPane drsnikDesno = new JScrollPane(dosegljivi);
		GridBagConstraints dosegljiviConstraint = new GridBagConstraints();
		dosegljiviConstraint.gridx = 0;
		dosegljiviConstraint.gridy = 0;
		dosegljiviConstraint.fill = GridBagConstraints.BOTH;
		dosegljiviConstraint.weightx = 1;
		dosegljiviConstraint.weighty = 1;
		desno.add(drsnikDesno, dosegljiviConstraint);
		
		// Polje za vnos sporoèila
		input = new JTextField(40);
		input.setEditable(false);
		GridBagConstraints inputConstraint = new GridBagConstraints();
		inputConstraint.gridx = 0;
		inputConstraint.gridy = 1;
		inputConstraint.fill = GridBagConstraints.HORIZONTAL;
		inputConstraint.weightx = 1;
		inputConstraint.weighty = 0;
		levo.add(input, inputConstraint);
		input.addKeyListener(this);
		
		// Polje za vnos prejemnika sporoèila
		komuPosiljamo = new String();
		online = new String[]{};
		whoMenu = new JComboBox<String>(online);
		whoMenu.setEditable(true);
		whoMenu.setEnabled(false);
		whoMenu.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				@SuppressWarnings("unchecked")
				JComboBox<String> cb = (JComboBox<String>) e.getSource();
				if (cb.getSelectedIndex() == -1) {
					komuPosiljamo = new String("");
				} else {
					String who = cb.getSelectedItem().toString();
					komuPosiljamo = new String(who);
				}
			}
		});
		GridBagConstraints komuConstraint = new GridBagConstraints();
		komuConstraint.gridx = 0;
		komuConstraint.gridy = 1;
		komuConstraint.fill = GridBagConstraints.HORIZONTAL;
		komuConstraint.weightx = 1;
		komuConstraint.weighty = 0;
		desno.add(whoMenu, komuConstraint);
		
		// Splitter, ki loèi levo in desno stran
		splitter = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, levo, desno);
		splitter.setOneTouchExpandable(true);
		GridBagConstraints splitterConstraint = new GridBagConstraints();
		splitterConstraint.gridx = 0;
		splitterConstraint.gridy = 1;
		splitterConstraint.fill = GridBagConstraints.BOTH;
		splitterConstraint.weightx = 1;
		splitterConstraint.weighty = 1;
		pane.add(splitter, splitterConstraint);
		
		// Sledimo dogodkom povezanim z oknom
		addWindowListener(this);
		
		// Doloèimo, da se okno zapre in ne le skrije, èe kliknemo križec
		setDefaultCloseOperation(DISPOSE_ON_CLOSE);
		
		// Testiramo
		test = new JTextPane();
		addOnlineUser("Samo", true);
		addOnlineUser("Samo", false);
		addOnlineUser("Testni uporabnik");
		
		GridBagConstraints grid = new GridBagConstraints();
		grid.gridx = 0;
		grid.gridy = 2;
		grid.fill = GridBagConstraints.BOTH;
		grid.weightx = 1;
		grid.weighty = 0;
		pane.add(test, grid);
	}
	
	public void addOnlineUser(String name, boolean isAway) {
		StyleContext sc = StyleContext.getDefaultStyleContext();
		AttributeSet aset = sc.addAttribute(SimpleAttributeSet.EMPTY, StyleConstants.Foreground, Color.BLACK);
		
		aset = sc.addAttribute(aset, StyleConstants.Alignment, StyleConstants.ALIGN_JUSTIFIED);
		aset = sc.addAttribute(aset, StyleConstants.FontSize, fontSize);
		
		int len = test.getDocument().getLength();
		try {
			test.getDocument().insertString(len, name, aset);
			len = test.getDocument().getLength();
			if (isAway) {
				aset = sc.addAttribute(aset, StyleConstants.Italic, true);
				aset = sc.addAttribute(aset, StyleConstants.FontSize, fontSize-3);
				test.getDocument().insertString(len, "   (Away)\n" , aset);
			} else {
				test.getDocument().insertString(len, "\n", aset);
			}
		} catch (BadLocationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public void addOnlineUser(String name) {
		addOnlineUser(name, false);
	}
	
	public void addAllOnlineUsers(List<Uporabnik> users) {
		test.setText("");
		for (Uporabnik i : users) {
			addOnlineUser(i.getUsername(), checkIfAway(i));
		}
	}
	
	public boolean checkIfAway(Uporabnik user, int ms) {
		// Preverimo, èe je user bil aktiven v zadnjih ms milisekundah
		Date time = new Date();
		if (time.getTime() - user.getLastActive().getTime() > ms) {
			return true;
		} else {
			return false;
		}
	}
	
	public boolean checkIfAway(Uporabnik user) {
		// Po defaultu preverimo za 5 minut (= 300.000 ms)
		return checkIfAway(user, 300000);
	}
	
	private void scrollToBottom(JScrollPane scrollPane) {
	    JScrollBar verticalBar = scrollPane.getVerticalScrollBar();
	    AdjustmentListener downScroller = new AdjustmentListener() {
	        @Override
	        public void adjustmentValueChanged(AdjustmentEvent e) {
	            Adjustable adjustable = e.getAdjustable();
	            adjustable.setValue(adjustable.getMaximum());
	            verticalBar.removeAdjustmentListener(this);
	        }
	    };
	    verticalBar.addAdjustmentListener(downScroller);
	}
	
	public JComboBox<String> getTestMenu() {
		return whoMenu;
	}

	public void setTestMenu(JComboBox<String> testMenu) {
		this.whoMenu = testMenu;
	}
	
	public void testBox(String[] online) {
		DefaultComboBoxModel<String> model = new DefaultComboBoxModel<String>(online);
		model.setSelectedItem(null);
		whoMenu.setModel(model);
	}

	/**
	 * @param message - the message content
	 */
	private void adminMessage(String message) {
		StyleContext sc = StyleContext.getDefaultStyleContext();
		AttributeSet aset = sc.addAttribute(SimpleAttributeSet.EMPTY, StyleConstants.Foreground, Color.BLACK);
		
		aset = sc.addAttribute(aset, StyleConstants.FontFamily, "Impact");
		aset = sc.addAttribute(aset, StyleConstants.Alignment, StyleConstants.ALIGN_JUSTIFIED);
		
		int len = output.getDocument().getLength();
		try {
			output.getDocument().insertString(len, message + '\n', aset);
		} catch (BadLocationException e) {
			System.out.println("Prišlo je do napake pri izpisu sporoèila, kar se ne bi smelo zgoditi!");
		}
	}
	
	public void addSender(String sender, String recipient, String who) {
		Color c;
		if (who.equals("Me")) {
			c = colorMyName;
		} else {
			c = colorOthers;
		}
		
		StyleContext sc = StyleContext.getDefaultStyleContext();
		AttributeSet aset = sc.addAttribute(SimpleAttributeSet.EMPTY, StyleConstants.Foreground, c);
		
		aset = sc.addAttribute(aset, StyleConstants.FontFamily, "Comic Sans MS");
		aset = sc.addAttribute(aset, StyleConstants.Alignment, StyleConstants.ALIGN_JUSTIFIED);
		aset = sc.addAttribute(aset, StyleConstants.FontSize, fontSize+3);
		aset = sc.addAttribute(aset, StyleConstants.Bold, true);
		
		int len = output.getDocument().getLength();
		try {
			if (recipient.isEmpty()) {
				output.getDocument().insertString(len, sender + ": ", aset);
			} else {
				output.getDocument().insertString(len, sender + " -> " + recipient + ":  ", aset);
			}
		} catch (BadLocationException e) {
			System.out.println("Prišlo je do napake pri izpisu sporoèila, kar se ne bi smelo zgoditi!");
		}
	}
	
	public void addSender(String sender, String who) {
		addSender(sender, "", who);
	}
	
	public void addContent(String message, String who) {
		Color c;
		if (who.equals("Me")) {
			c = colorMe;
		} else {
			c = colorMsgOthers;
		}
		
		StyleContext sc = StyleContext.getDefaultStyleContext();
		AttributeSet aset = sc.addAttribute(SimpleAttributeSet.EMPTY, StyleConstants.Foreground, c);
		
		aset = sc.addAttribute(aset, StyleConstants.FontFamily, "Courier New");
		aset = sc.addAttribute(aset, StyleConstants.Alignment, StyleConstants.ALIGN_JUSTIFIED);
		aset = sc.addAttribute(aset, StyleConstants.FontSize, fontSize);
		
		int len = output.getDocument().getLength();
		try {
			output.getDocument().insertString(len, message + '\n', aset);
		} catch (BadLocationException e) {
			System.out.println("Prišlo je do napake pri izpisu sporoèila, kar se ne bi smelo zgoditi!");
		}
	}
	
	public void addMessage(String sender, String recipient, String message, String who) {
		addSender(sender, recipient, who);
		addContent(message, who);
	}
	
	public void addMessage(String sender, String message, String who) {
		addMessage(sender, "", message, who);
	}
	
	public String[] getOnline() {
		return online;
	}

	public void setOnline(String[] online) {
		this.online = online;
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		if (e.getSource().equals(gumbPrijavi) || e.getSource().equals(miLogin)) {
			try {
				// Poizkusimo se prijaviti
				if (imeEditor.getText().isEmpty()) imeEditor.setText(System.getProperty("user.name"));
				
				HttpCommands.prijava(imeEditor.getText());
				
				// Primerno vklopimo / izklopimo razna polja & gumbe
				imeEditor.setEditable(false);
				gumbOdjavi.setEnabled(true);
				gumbPrijavi.setEnabled(false);
				input.setEditable(true);
				whoMenu.setEnabled(true);
				
				// Uporabniku sporoèimo, da se je uspešno prijavil
				adminMessage("Uspešno ste se prijavili!");
				
				// Vklopimo robota, ki nam bo prikazoval sporoèila ostalih
				robot = new RobotZaSporocila(this);
				robot.activate();
				
				// Vklopimo robota, ki nam bo prikazoval dosegljive uporabnike
				robotDosegljivi = new RobotDosegljivi(this);
				robotDosegljivi.activate();
			} catch (Exception e1) {
				adminMessage("Neuspešna prijava!");
				System.out.println("Neuspešna prijava!");
			}
		} else if (e.getSource().equals(gumbOdjavi) || e.getSource().equals(miLogout)) {
			try {
				// Uporabnika poizkusimo odjaviti
				HttpCommands.odjava(imeEditor.getText());
				
				// Primerno vklopimo / izklopimo razna polja & gumbe
				imeEditor.setEditable(true);
				gumbOdjavi.setEnabled(false);
				gumbPrijavi.setEnabled(true);
				input.setEditable(false);
				whoMenu.setEnabled(false);
				
				// Uporabniku sporoèimo, da se je uspešno odjavil
				adminMessage("Uspešno ste se odjavili!");
				
				// Odjavimo oba robota
				robot.cancel();
				robotDosegljivi.cancel();
				
				// Pobrišemo polje z dosegljivimi uporabniki
				dosegljivi.setText("");
			} catch (Exception e1) {
				adminMessage("Neuspešna odjava!");
				System.out.println("Neuspešna odjava!");
			}
		} else if (e.getSource().equals(cbRedMyName)) {
			colorMyName = Color.RED;
		} else if (e.getSource().equals(cbBlueMyName)) {
			colorMyName = Color.BLUE;
		} else if (e.getSource().equals(cbGreenMyName)) {
			colorMyName = Color.GREEN;
		} else if (e.getSource().equals(cbBlackMyName)) {
			colorMyName = Color.BLACK;
		} else if (e.getSource().equals(cbRedOthers)) {
			colorOthers = Color.RED;
		} else if (e.getSource().equals(cbBlueOthers)) {
			colorOthers = Color.BLUE;
		} else if (e.getSource().equals(cbGreenOthers)) {
			colorOthers = Color.GREEN;
		} else if (e.getSource().equals(cbBlackOthers)) {
			colorOthers = Color.BLACK;
		} else if (e.getSource().equals(cbRedMe)) {
			colorMe = Color.RED;
		} else if (e.getSource().equals(cbBlueMe)) {
			colorMe = Color.BLUE;
		} else if (e.getSource().equals(cbGreenMe)) {
			colorMe = Color.GREEN;
		} else if (e.getSource().equals(cbBlackMe)) {
			colorMe = Color.BLACK;
		} else if (e.getSource().equals(cbRedMsgOthers)) {
			colorMsgOthers = Color.RED;
		} else if (e.getSource().equals(cbBlueMsgOthers)) {
			colorMsgOthers = Color.BLUE;
		} else if (e.getSource().equals(cbGreenMsgOthers)) {
			colorMsgOthers = Color.GREEN;
		} else if (e.getSource().equals(cbBlackMsgOthers)) {
			colorMsgOthers = Color.BLACK;
		}
	}

	@Override
	
	public void keyTyped(KeyEvent e) {
		if (e.getSource() == this.input) {
			if (e.getKeyChar() == '\n') {
				PosljiSporocilo sporocilo;
				if (komuPosiljamo.isEmpty()) {
					// Imamo global sporoèilo
					sporocilo = new PosljiSporocilo(true, input.getText());
				} else {
					// Sporoèilo je namenjeno le 1 osebi
					sporocilo = new PosljiSporociloDirect(false, input.getText(), komuPosiljamo);
				}
				System.out.println(sporocilo);
				ObjectMapper mapper = new ObjectMapper();
				mapper.setDateFormat(new ISO8601DateFormat());
				try {
					// Sporoèilo zapišemo v JSON formatu
					String jsonSporocilo = mapper.writeValueAsString(sporocilo);
					System.out.println(jsonSporocilo);
					
					// Sporoèilo poizkusimo poslati strežniku
					HttpCommands.posljiSporocilo(imeEditor.getText(), jsonSporocilo);
					
					// Izpišemo ga na našem zaslonu
					if (sporocilo.isGlobal()) {
						addMessage(imeEditor.getText(), input.getText(), "Me");
					} else {
						addMessage(imeEditor.getText(), komuPosiljamo, input.getText(), "Me");
					}
					
					// Pobrišemo input polje
					input.setText("");
				} catch (Exception e1) {
					// TODO Auto-generated catch block
					System.out.println("Napaka pri keyTyped!");
					e1.printStackTrace();
				}
			}
		}
	}

	@Override
	public void keyPressed(KeyEvent e) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void keyReleased(KeyEvent e) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void windowActivated(WindowEvent e) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void windowClosed(WindowEvent e) {
		// Okno se je zaprlo
		if (gumbOdjavi.isEnabled()) {
			// Imamo prijavljenega uporabnika
			try {
				// Uporabnika poizkusimo odjaviti
				HttpCommands.odjava(imeEditor.getText());
				// Izklopimo oba robota
				robot.cancel();
				robotDosegljivi.cancel();
				System.exit(0);
			} catch (Exception e1) {
				System.out.println("Neuspešna odjava!");
			}
		}
	}

	@Override
	public void windowClosing(WindowEvent e) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void windowDeactivated(WindowEvent e) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void windowDeiconified(WindowEvent e) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void windowIconified(WindowEvent e) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void windowOpened(WindowEvent e) {
		// Ob odprtju novega okna nastavimo privzet focus
		imeEditor.requestFocus();
	}
}
