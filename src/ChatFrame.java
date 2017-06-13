import java.awt.Color;
import java.awt.Container;
import java.awt.FlowLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import javax.swing.ButtonGroup;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
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

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.util.ISO8601DateFormat;

public class ChatFrame extends JFrame implements ActionListener, KeyListener, WindowListener {
	
	/**
	 * @param output - Displays messages
	 * @param input - Input window for your message
	 */
	private static final long serialVersionUID = 1L;
	private JTextField input;
	private JTextField imeEditor;
	private JButton gumbPrijavi;
	private JButton gumbOdjavi;
	private String komuPosiljamo;
	private RobotZaSporocila robot;
	private JTextPane dosegljivi;
	private RobotDosegljivi robotDosegljivi;
	private JSplitPane splitter;
	private Color colorMyName, colorOthers, colorMe, colorMsgOthers;
	private JTextPane output;
	private JMenuBar menuBar;
	private JMenu fileMenu, optionsMenu, robotMenu;
	private JMenuItem miLogin, miLogout, miStopRobot;
	private ButtonGroup groupColorsMyName, groupColorsOthers, groupColorsMe, groupColorsMsgOthers, groupColorsBg;
	private JRadioButtonMenuItem cbRedMyName, cbGreenMyName, cbBlueMyName, cbBlackMyName, cbCyanMyName, cbMagentaMyName, cbOrangeMyName, cbCustomMyName, cbRandomMyName; // Barve za imena pri sporoèilih, ki jih pošlje uporabnik -- Imena imajo cb na zaèetku, ker so prvotni bili CheckBoxi
	private JRadioButtonMenuItem cbRedOthers, cbGreenOthers, cbBlueOthers, cbBlackOthers, cbCyanOthers, cbMagentaOthers, cbOrangeOthers, cbCustomOthers, cbRandomOthers; // Barve za imena pri sporoèilih, ki jih pošljejo ostali
	private JRadioButtonMenuItem cbRedMe, cbGreenMe, cbBlueMe, cbBlackMe, cbCyanMe, cbMagentaMe, cbOrangeMe, cbCustomMe, cbRandomMe; // Barve za sporoèila, ki jih pošlje uporabnik
	private JRadioButtonMenuItem cbRedMsgOthers, cbGreenMsgOthers, cbBlueMsgOthers, cbBlackMsgOthers, cbCyanMsgOthers, cbMagentaMsgOthers, cbOrangeMsgOthers, cbCustomMsgOthers, cbRandomMsgOthers; // Barve za sporoèila, ki jih pošljejo ostali
	private JRadioButtonMenuItem cbWhiteBg, cbBlackBg, cbGrayBg;
	private JSlider fontSizeSlider, colorRedSlider, colorGreenSlider, colorBlueSlider;
	private int fontSize;
	private JComboBox<String> whoMenu;
	private String[] online;
	private Map<String, Long> lastActive;
	private JMenuItem miStartRobot;
	private JSlider robotDelaySlider;
	private long robotDelay;
	private List<OdmevRobot> echoRobots;
	private JScrollPane drsnikLevo;
	private JButton buttonConfirm;

	public ChatFrame() {
		super();
		// Nastavimo naslov
		setTitle("Chit Chat");
		Container pane = this.getContentPane();
		pane.setLayout(new GridBagLayout());
		
		// Nastavimo default barve, velikost pisave in druge privzete vrednosti spremenljivk
		colorMyName = Color.BLUE;
		colorMe = Color.RED;
		colorOthers = Color.CYAN;
		colorMsgOthers = Color.ORANGE;
		fontSize = 12;
		robotDelay = (long) 2500;
		echoRobots = new ArrayList<OdmevRobot>();
		
		// Nastavimo zaèetni set lastActive
		lastActive = new TreeMap<String, Long>();
		
		// Dodajmo zgornji menu
		// Najprej dodamo menu bar
		menuBar = new JMenuBar();
		setJMenuBar(menuBar);
		
		// Dodamo menu "File"
		fileMenu = new JMenu("Datoteka");
		miLogin = new JMenuItem("Prijava");
		miLogout = new JMenuItem("Odjava");
		miLogout.setEnabled(false);
		JMenuItem miExit = new JMenuItem("Izhod");
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
					// Izklopimo oba robota
					robot.cancel();
					robotDosegljivi.cancel();
					// Imamo prijavljenega uporabnika					
					try {
						// Uporabnika poizkusimo odjaviti
						HttpCommands.odjava(imeEditor.getText());
						System.exit(0);
					} catch (Exception e1) {
						// Tu je uporabniku bolj kot ne vseeno
						// On je zaprl aplikacijo, vsi roboti itd. so se izklopili
						// Edino kar se je zgodilo je, da je ostal 'vpisan' in se bo morda moral vpisati z drugim uporabniškim imenom
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
		optionsMenu = new JMenu("Nastavitve");
		// Ustvarimo podmenu "Color"
		JMenu colorSubMenu = new JMenu("Barva pisave");
		// Ustvarimo subsubmenu "Names"
		JMenu namesSubSubMenu = new JMenu("Imena");
		// Ustvarimo subsubsubmenu "My name"
		JMenu myName = new JMenu("Moje ime");
		cbRedMyName = new JRadioButtonMenuItem("Rdeèa");
		cbBlueMyName = new JRadioButtonMenuItem("Modra", true);
		cbGreenMyName = new JRadioButtonMenuItem("Zelena");
		cbBlackMyName = new JRadioButtonMenuItem("Èrna");
		cbCyanMyName = new JRadioButtonMenuItem("Sinje modra");
		cbMagentaMyName = new JRadioButtonMenuItem("Škrlatna");
		cbOrangeMyName = new JRadioButtonMenuItem("Oranžna");
		cbCustomMyName = new JRadioButtonMenuItem("Poljubna");
		cbRandomMyName = new JRadioButtonMenuItem("Nakljuèna");
		// Dodamo možnosti v submenu "Color"
		myName.add(cbBlueMyName);
		myName.add(cbRedMyName);
		myName.add(cbGreenMyName);
		myName.add(cbBlackMyName);
		myName.add(cbCyanMyName);
		myName.add(cbMagentaMyName);
		myName.add(cbOrangeMyName);
		myName.add(cbCustomMyName);
		myName.add(cbRandomMyName);
		// Dodamo te barve v grupo, ki bo poskrbela, da imamo lahko izbrano le 1
		groupColorsMyName = new ButtonGroup();
		groupColorsMyName.add(cbBlueMyName);
		groupColorsMyName.add(cbRedMyName);
		groupColorsMyName.add(cbGreenMyName);
		groupColorsMyName.add(cbBlackMyName);
		groupColorsMyName.add(cbCyanMyName);
		groupColorsMyName.add(cbMagentaMyName);
		groupColorsMyName.add(cbOrangeMyName);
		groupColorsMyName.add(cbCustomMyName);
		groupColorsMyName.add(cbRandomMyName);
		// Dodamo listenerje
		cbRedMyName.addActionListener(this);
		cbBlueMyName.addActionListener(this);
		cbGreenMyName.addActionListener(this);
		cbBlackMyName.addActionListener(this);
		cbCyanMyName.addActionListener(this);
		cbMagentaMyName.addActionListener(this);
		cbOrangeMyName.addActionListener(this);
		cbCustomMyName.addActionListener(this);
		cbRandomMyName.addActionListener(this);
		// Dodamo "My name" v "Names"
		namesSubSubMenu.add(myName);
		// Ustvarimo subsubsubmenu "Others"
		JMenu otherNames = new JMenu("Ostala");
		cbRedOthers = new JRadioButtonMenuItem("Rdeèa");
		cbBlueOthers = new JRadioButtonMenuItem("Modra");
		cbGreenOthers = new JRadioButtonMenuItem("Zelena");
		cbBlackOthers = new JRadioButtonMenuItem("Èrna");
		cbCyanOthers = new JRadioButtonMenuItem("Sinje modra", true);
		cbMagentaOthers = new JRadioButtonMenuItem("Škrlatna");
		cbOrangeOthers = new JRadioButtonMenuItem("Oranžna");
		cbCustomOthers = new JRadioButtonMenuItem("Poljubna");
		cbRandomOthers = new JRadioButtonMenuItem("Nakljuèna");
		// Dodamo možnosti v submenu "Color"
		otherNames.add(cbBlueOthers);
		otherNames.add(cbRedOthers);
		otherNames.add(cbGreenOthers);
		otherNames.add(cbBlackOthers);
		otherNames.add(cbCyanOthers);
		otherNames.add(cbMagentaOthers);
		otherNames.add(cbOrangeOthers);
		otherNames.add(cbCustomOthers);
		otherNames.add(cbRandomOthers);
		// Dodamo te barve v grupo, ki bo poskrbela, da imamo lahko izbrano le 1
		groupColorsOthers = new ButtonGroup();
		groupColorsOthers.add(cbBlueOthers);
		groupColorsOthers.add(cbRedOthers);
		groupColorsOthers.add(cbGreenOthers);
		groupColorsOthers.add(cbBlackOthers);
		groupColorsOthers.add(cbCyanOthers);
		groupColorsOthers.add(cbMagentaOthers);
		groupColorsOthers.add(cbOrangeOthers);
		groupColorsOthers.add(cbCustomOthers);
		groupColorsOthers.add(cbRandomOthers);
		// Dodamo listenerje
		cbRedOthers.addActionListener(this);
		cbBlueOthers.addActionListener(this);
		cbGreenOthers.addActionListener(this);
		cbBlackOthers.addActionListener(this);
		cbCyanOthers.addActionListener(this);
		cbMagentaOthers.addActionListener(this);
		cbOrangeOthers.addActionListener(this);
		cbCustomOthers.addActionListener(this);
		cbRandomOthers.addActionListener(this);
		// Dodamo "Others" v "Names"
		namesSubSubMenu.add(otherNames);
		// Dodamo "Names" v "Color"
		colorSubMenu.add(namesSubSubMenu);
		
		// Ustvarimo subsubmenu "Messages"
		JMenu messagesSubSubMenu = new JMenu("Sporoèila");
		// Ustvarimo subsubsubmenu "My messages"
		JMenu myMessages = new JMenu("Moja sporoèila");
		cbRedMe = new JRadioButtonMenuItem("Rdeèa", true);
		cbBlueMe = new JRadioButtonMenuItem("Modra");
		cbGreenMe = new JRadioButtonMenuItem("Zelena");
		cbBlackMe = new JRadioButtonMenuItem("Èrna");
		cbCyanMe = new JRadioButtonMenuItem("Sinje modra");
		cbMagentaMe = new JRadioButtonMenuItem("Škrlatna");
		cbOrangeMe = new JRadioButtonMenuItem("Oranžna");
		cbCustomMe = new JRadioButtonMenuItem("Poljubna");
		cbRandomMe = new JRadioButtonMenuItem("Nakljuèna");
		// Dodamo možnosti v submenu "Color"
		myMessages.add(cbBlueMe);
		myMessages.add(cbRedMe);
		myMessages.add(cbGreenMe);
		myMessages.add(cbBlackMe);
		myMessages.add(cbCyanMe);
		myMessages.add(cbMagentaMe);
		myMessages.add(cbOrangeMe);
		myMessages.add(cbCustomMe);
		myMessages.add(cbRandomMe);
		// Dodamo te barve v grupo, ki bo poskrbela, da imamo lahko izbrano le 1
		groupColorsMe = new ButtonGroup();
		groupColorsMe.add(cbBlueMe);
		groupColorsMe.add(cbRedMe);
		groupColorsMe.add(cbGreenMe);
		groupColorsMe.add(cbBlackMe);
		groupColorsMe.add(cbCyanMe);
		groupColorsMe.add(cbMagentaMe);
		groupColorsMe.add(cbOrangeMe);
		groupColorsMe.add(cbCustomMe);
		groupColorsMe.add(cbRandomMe);
		// Dodamo listenerje
		cbRedMe.addActionListener(this);
		cbBlueMe.addActionListener(this);
		cbGreenMe.addActionListener(this);
		cbBlackMe.addActionListener(this);
		cbCyanMe.addActionListener(this);
		cbMagentaMe.addActionListener(this);
		cbOrangeMe.addActionListener(this);
		cbCustomMe.addActionListener(this);
		cbRandomMe.addActionListener(this);
		// Dodamo "My messages" v "Messages"
		messagesSubSubMenu.add(myMessages);
		// Ustvarimo subsubsubmenu "Other"
		JMenu otherMessages = new JMenu("Ostala");
		cbRedMsgOthers = new JRadioButtonMenuItem("Rdeèa");
		cbBlueMsgOthers = new JRadioButtonMenuItem("Modra");
		cbGreenMsgOthers = new JRadioButtonMenuItem("Zelena");
		cbBlackMsgOthers = new JRadioButtonMenuItem("Èrna");
		cbCyanMsgOthers = new JRadioButtonMenuItem("Sinje modra");
		cbMagentaMsgOthers = new JRadioButtonMenuItem("Škrlatna");
		cbOrangeMsgOthers = new JRadioButtonMenuItem("Oranžna", true);
		cbCustomMsgOthers = new JRadioButtonMenuItem("Poljubna");
		cbRandomMsgOthers = new JRadioButtonMenuItem("Nakljuèna");
		// Dodamo možnosti v submenu "Color"
		otherMessages.add(cbBlueMsgOthers);
		otherMessages.add(cbRedMsgOthers);
		otherMessages.add(cbGreenMsgOthers);
		otherMessages.add(cbBlackMsgOthers);
		otherMessages.add(cbCyanMsgOthers);
		otherMessages.add(cbMagentaMsgOthers);
		otherMessages.add(cbOrangeMsgOthers);
		otherMessages.add(cbCustomMsgOthers);
		otherMessages.add(cbRandomMsgOthers);
		// Dodamo te barve v grupo, ki bo poskrbela, da imamo lahko izbrano le 1
		groupColorsMsgOthers = new ButtonGroup();
		groupColorsMsgOthers.add(cbBlueMsgOthers);
		groupColorsMsgOthers.add(cbRedMsgOthers);
		groupColorsMsgOthers.add(cbGreenMsgOthers);
		groupColorsMsgOthers.add(cbBlackMsgOthers);
		groupColorsMsgOthers.add(cbCyanMsgOthers);
		groupColorsMsgOthers.add(cbMagentaMsgOthers);
		groupColorsMsgOthers.add(cbOrangeMsgOthers);
		groupColorsMsgOthers.add(cbCustomMsgOthers);
		groupColorsMsgOthers.add(cbRandomMsgOthers);
		// Dodamo listenerje
		cbRedMsgOthers.addActionListener(this);
		cbBlueMsgOthers.addActionListener(this);
		cbGreenMsgOthers.addActionListener(this);
		cbBlackMsgOthers.addActionListener(this);
		cbCyanMsgOthers.addActionListener(this);
		cbMagentaMsgOthers.addActionListener(this);
		cbOrangeMsgOthers.addActionListener(this);
		cbCustomMsgOthers.addActionListener(this);
		cbRandomMsgOthers.addActionListener(this);
		// Dodamo "Other" v "Messages"
		messagesSubSubMenu.add(otherMessages);
		// Dodamo "Messages" v "Colors"
		colorSubMenu.add(messagesSubSubMenu);
		// Dodamo podmenu "Colors" v menu "Options"
		optionsMenu.add(colorSubMenu);
		// Ustvarimo menu "Font Size"
		JMenu fontSizeSubMenu = new JMenu("Velikost pisave");
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
		
		// Ustvarimo podmenu "Background color"
		JMenu bgColorSubMenu = new JMenu("Barva ozadja");
		cbWhiteBg = new JRadioButtonMenuItem("Bela", true);
		cbGrayBg = new JRadioButtonMenuItem("Siva");
		cbBlackBg = new JRadioButtonMenuItem("Èrna");
		// Dodamo možnosti v submetu bg color
		bgColorSubMenu.add(cbWhiteBg);
		bgColorSubMenu.add(cbGrayBg);
		bgColorSubMenu.add(cbBlackBg);
		// Dodamo te barve v grupo, ki bo poskrbela, da imamo lahko izbrano le 1
		groupColorsBg = new ButtonGroup();
		groupColorsBg.add(cbWhiteBg);
		groupColorsBg.add(cbGrayBg);
		groupColorsBg.add(cbBlackBg);
		// Dodamo listenerje
		cbWhiteBg.addActionListener(this);
		cbGrayBg.addActionListener(this);
		cbBlackBg.addActionListener(this);
		// Dodamo bg color submenu v menu options
		optionsMenu.add(bgColorSubMenu);
		// Ustvarimo slajderje za RGB
		JMenu rgbColorSubMenu = new JMenu("Ustvari barvo");
		// Ustvarimo slider za rdeèo barvo
		JLabel textRedSlider = new JLabel("Red");
		colorRedSlider = new JSlider(0, 255, 127);
		colorRedSlider.setMajorTickSpacing(50);
		colorRedSlider.setMinorTickSpacing(10);
		colorRedSlider.setPaintTicks(true);

		JLabel textGreenSlider = new JLabel("Green");
		colorGreenSlider = new JSlider(0, 255, 127);
		colorGreenSlider.setMajorTickSpacing(50);
		colorGreenSlider.setMinorTickSpacing(10);
		colorGreenSlider.setPaintTicks(true);
		
		JLabel textBlueSlider = new JLabel("Blue");
		colorBlueSlider = new JSlider(0, 255, 127);
		colorBlueSlider.setMajorTickSpacing(50);
		colorBlueSlider.setMinorTickSpacing(10);
		colorBlueSlider.setPaintTicks(true);

		buttonConfirm = new JButton("Potrdi");
		buttonConfirm.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				int r = colorRedSlider.getValue();
				int g = colorGreenSlider.getValue();
				int b = colorBlueSlider.getValue();
				if (cbCustomMyName.isSelected()) colorMyName = new Color(r, g, b);
				if (cbCustomOthers.isSelected()) colorOthers = new Color(r, g, b);
				if (cbCustomMe.isSelected()) colorMe = new Color(r, g, b);
				if (cbCustomMsgOthers.isSelected()) colorMsgOthers = new Color(r, g, b);
			}
		});
		// Dodamo sliderje in potrdi gumb v sub menu
		rgbColorSubMenu.add(textRedSlider);
		rgbColorSubMenu.add(colorRedSlider);
		rgbColorSubMenu.add(textGreenSlider);
		rgbColorSubMenu.add(colorGreenSlider);
		rgbColorSubMenu.add(textBlueSlider);
		rgbColorSubMenu.add(colorBlueSlider);
		rgbColorSubMenu.add(buttonConfirm);
		// Dodamo v "Options"
		optionsMenu.add(rgbColorSubMenu);
		// Dodamo menu "Options" v menuBar
		menuBar.add(optionsMenu);
		
		// Dodamo menu Robot
		robotMenu = new JMenu("Robot");
		miStartRobot = new JMenuItem("Zaženi");
		// Dodamo listenerja
		miStartRobot.addActionListener(this);
		// Dodamo v menu Robot
		robotMenu.add(miStartRobot);
		
		// Ustvarimo menu "Delay"
		JMenu robotDelaySubMenu = new JMenu("Nastavi zamik");
		robotDelaySlider = new JSlider(1000, 10000, 2500);
		robotDelaySlider.setMajorTickSpacing(3000);
		robotDelaySlider.setMinorTickSpacing(500);
		robotDelaySlider.setPaintTicks(true);
		robotDelaySlider.setPaintLabels(true);
		// Dodamo listener
		robotDelaySlider.addChangeListener(new ChangeListener() {
			public void stateChanged(ChangeEvent e) {
				JSlider source = (JSlider) e.getSource();
				if (!source.getValueIsAdjusting()) {
					robotDelay = (long) source.getValue();
				}
			}
		});
		// Dodamo slider v podmenu
		robotDelaySubMenu.add(robotDelaySlider);
		// Dodamo podmenu v menu "Options"
		robotMenu.add(robotDelaySubMenu);
		// Dodajmo razlago
		JMenu info = new JMenu("Navodila");
		JTextArea infoText = new JTextArea();
		infoText.setText("Robot bo ponavljal sporoèila\nosebe, ki jo imate izbrano.\nÈe nimate nikogar izbranega,\nbo robot ponavljal vaša sporoèila.\nZamiki robotov, ki oponašajo\nisto osebo, se morajo razlikovati\nza vsaj pol sekunde.");
//		infoText.setText("Robot will echo whoever\nyou have selected right now.\nIf you have noone selected,\nit will echo your messages.");
		info.add(infoText);
		robotMenu.add(info);
		// Dodamo gumb za odjavo
		miStopRobot = new JMenuItem("Ustavi");
		miStopRobot.addActionListener(this);
		robotMenu.add(miStopRobot);
		// Dodamo menu Robot v menuBar
		menuBar.add(robotMenu);
		
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
		output.setBackground(Color.WHITE);
		drsnikLevo = new JScrollPane(output);
		GridBagConstraints outputConstraint = new GridBagConstraints();
		outputConstraint.gridx = 0;
		outputConstraint.gridy = 0;
		outputConstraint.fill = GridBagConstraints.BOTH;
		outputConstraint.weightx = 1;
		outputConstraint.weighty = 1;
		levo.add(drsnikLevo, outputConstraint);
		
		// Polje kamor bo izpisan seznam dosegljivih uporabnikov
		dosegljivi = new JTextPane();
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
		splitter.setResizeWeight(1);
		GridBagConstraints splitterConstraint = new GridBagConstraints();
		splitterConstraint.gridx = 0;
		splitterConstraint.gridy = 1;
		splitterConstraint.fill = GridBagConstraints.BOTH;
		splitterConstraint.weightx = 1;
		splitterConstraint.weighty = 1;
		pane.add(splitter, splitterConstraint);
		
		// Nastavimo nastavitve gumbov
		urediGumbe(false);
		
		// Sledimo dogodkom povezanim z oknom
		addWindowListener(this);
		
		// Doloèimo, da se okno zapre in ne le skrije, èe kliknemo križec
		setDefaultCloseOperation(DISPOSE_ON_CLOSE);
	}
	
	/**
	 * Updates {@code lastActive} to contain currently online users
	 */
	public void syncUsers() {
		List<Uporabnik> users = getAllOnlineUsersList();
		String[] usernames = new String[users.size()];
		int st = 0;
		// Preverimo, èe so kaki novi uporabniki online
		for (Uporabnik i : users) {
			usernames[st] = i.getUsername();
			st++;
			if (lastActive.containsKey(i.getUsername())) continue;
			lastActive.put(i.getUsername(), i.getLastActive().getTime());
		}
		
		// Preverimo, èe kakšni uporabniki niso veè online
		List<String> toRemove = new ArrayList<String>();
		for (String i : lastActive.keySet()) {
			if (Arrays.asList(usernames).contains(i)) continue;
			toRemove.add(i);
		}
		for (String i : toRemove) {
			lastActive.remove(i);
		}
	}
	
	public void updateLastActive(String username, Date date) {
		lastActive.put(username, date.getTime());
	}
	
	public void addOnlineUser(String name, boolean isAway) {
		Color c;
		if (name.equals(imeEditor.getText())) {
			c = colorMyName;
		} else {
			c = colorOthers;
		}
		
		if (dosegljivi.getBackground().equals(Color.BLACK) && c.equals(Color.BLACK)) c = Color.WHITE;
		
		StyleContext sc = StyleContext.getDefaultStyleContext();
		AttributeSet aset = sc.addAttribute(SimpleAttributeSet.EMPTY, StyleConstants.Foreground, c);
		
		aset = sc.addAttribute(aset, StyleConstants.Alignment, StyleConstants.ALIGN_JUSTIFIED);
		aset = sc.addAttribute(aset, StyleConstants.FontSize, fontSize);
		
		int len = dosegljivi.getDocument().getLength();
		try {
			dosegljivi.getDocument().insertString(len, name, aset);
			len = dosegljivi.getDocument().getLength();
			if (isAway) {
				aset = sc.addAttribute(aset, StyleConstants.Italic, true);
				aset = sc.addAttribute(aset, StyleConstants.FontSize, fontSize-3);
				dosegljivi.getDocument().insertString(len, "   (Away)\n" , aset);
			} else {
				dosegljivi.getDocument().insertString(len, "\n", aset);
			}
		} catch (BadLocationException e) {
			// Do tega exceptiona naèeloma ne bi smelo priti, ker vedno postavljamo index na zadnje mesto
			// v dokumentu, ki ga pa izraèunamo tik pred tem
			System.out.println("Iz nekega razloga smo dobili pri \"addOnlineUsers\" index iz mej!");
		}
	}
	
	public void addOnlineUser(String name) {
		addOnlineUser(name, false);
	}
	
	public void addAllOnlineUsers(List<Uporabnik> users) {
		dosegljivi.setText("");
		for (Uporabnik i : users) {
			addOnlineUser(i.getUsername(), checkIfAway(i));
		}
	}
	
	public boolean checkIfAway(Uporabnik user, int ms) {
		// Preverimo, èe je user bil aktiven v zadnjih ms milisekundah
		if (lastActive.containsKey(user.getUsername())) {
			if (new Date().getTime() - lastActive.get(user.getUsername()) > ms) {
				return true;
			}
		}
		return false;
	}
	
	public boolean checkIfAway(Uporabnik user) {
		// Po defaultu preverimo za 5 minut (= 300.000 ms)
		return checkIfAway(user, 300000);
	}
	
	public void updateWhoMenu(String[] online) {
		DefaultComboBoxModel<String> model = new DefaultComboBoxModel<String>(online);
		String name = komuPosiljamo.isEmpty() ? null : komuPosiljamo;
		if (!komuPosiljamo.isEmpty()) {
			if (Arrays.asList(online).contains(name)) {
				model.setSelectedItem(komuPosiljamo);
			} else {
				model.setSelectedItem(null);
			}
		} else {
			model.setSelectedItem(null);
		}
		whoMenu.setModel(model);
	}
	
	public void posodobiDrsnikLevo() {
		JScrollBar vertical = drsnikLevo.getVerticalScrollBar();
		vertical.setValue(vertical.getMaximum());
	}

	/**
	 * @param message - the message content
	 */
	private void adminMessage(String message) {
		Color c;
		if (output.getBackground().equals(Color.BLACK)) {
			c = Color.WHITE;
		} else {
			c = Color.BLACK;
		}
		
		StyleContext sc = StyleContext.getDefaultStyleContext();
		AttributeSet aset = sc.addAttribute(SimpleAttributeSet.EMPTY, StyleConstants.Foreground, c);
		
		aset = sc.addAttribute(aset, StyleConstants.FontFamily, "Impact");
		aset = sc.addAttribute(aset, StyleConstants.Alignment, StyleConstants.ALIGN_JUSTIFIED);
		
		int len = output.getDocument().getLength();
		try {
			output.getDocument().insertString(len, message + '\n', aset);
			posodobiDrsnikLevo();
		} catch (BadLocationException e) {
			// Do tega exceptiona naèeloma ne bi smelo priti, ker vedno postavljamo index na zadnje mesto
			// v dokumentu, ki ga pa izraèunamo tik pred tem
			System.out.println("Iz nekega razloga smo dobili pri \"adminMessage\" index iz mej!");
		}
	}
	
	public void addSender(String sender, String recipient, String who) {
		Color c;
		if (who.equals("Me")) {
			c = colorMyName;
		} else {
			c = colorOthers;
		}
		
		if (dosegljivi.getBackground().equals(Color.BLACK) && c.equals(Color.BLACK)) c = Color.WHITE;
		
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
			posodobiDrsnikLevo();
		} catch (BadLocationException e) {
			// Do tega exceptiona naèeloma ne bi smelo priti, ker vedno postavljamo index na zadnje mesto
			// v dokumentu, ki ga pa izraèunamo tik pred tem
			System.out.println("Iz nekega razloga smo dobili pri \"addSender\" index iz mej!");
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
		
		if (dosegljivi.getBackground().equals(Color.BLACK) && c.equals(Color.BLACK)) c = Color.WHITE;
		
		StyleContext sc = StyleContext.getDefaultStyleContext();
		AttributeSet aset = sc.addAttribute(SimpleAttributeSet.EMPTY, StyleConstants.Foreground, c);
		
		aset = sc.addAttribute(aset, StyleConstants.FontFamily, "Courier New");
		aset = sc.addAttribute(aset, StyleConstants.Alignment, StyleConstants.ALIGN_JUSTIFIED);
		aset = sc.addAttribute(aset, StyleConstants.FontSize, fontSize);
		
		int len = output.getDocument().getLength();
		try {
			output.getDocument().insertString(len, message + '\n', aset);
			posodobiDrsnikLevo();
		} catch (BadLocationException e) {
			// Do tega exceptiona naèeloma ne bi smelo priti, ker vedno postavljamo index na zadnje mesto
			// v dokumentu, ki ga pa izraèunamo tik pred tem
			System.out.println("Iz nekega razloga smo dobili pri \"addContent\" index iz mej!");
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

	public JTextField getImeEditor() {
		return imeEditor;
	}

	public void setImeEditor(JTextField imeEditor) {
		this.imeEditor = imeEditor;
	}
	
	public JTextField getInput() {
		return input;
	}

	public void setInput(JTextField input) {
		this.input = input;
	}

	public List<Uporabnik> getAllOnlineUsersList() {
		// TODO - lahko uporabiš v `RobotDosegljivi` - zaenkrat le za preverjanje, èe user online
		List<Uporabnik> uporabniki;
		try {
			String jsonUporabniki = HttpCommands.pridobiUporabnike();
			ObjectMapper mapper = new ObjectMapper();
			mapper.setDateFormat(new ISO8601DateFormat());
			TypeReference<List<Uporabnik>> t = new TypeReference<List<Uporabnik>>() { };
			uporabniki = mapper.readValue(jsonUporabniki, t);
			return uporabniki;
		} catch (Exception e) {
			e.printStackTrace();
			adminMessage("Povezava s strežnikom prekinjena!");
			System.out.println("Prišlo je do težav s povezavo med uporabnikom in strežnikom.");
			return new ArrayList<Uporabnik>();
		}
	}
	
	public boolean isOnline(String name) {
		List<Uporabnik> users = getAllOnlineUsersList();
		boolean on = false;
		for (Uporabnik i : users) {
			if (i.getUsername().equals(name)) {
				on = true;
				break;
			}
		}
		return on;
	}
	
	/**
	 * Asks server for our messages and writes them down on {@code output}
	 */
	public void writeMessages() {
		try {
			String jsonSporocila = HttpCommands.pridobiSporocila(imeEditor.getText());
			ObjectMapper mapper = new ObjectMapper();
			TypeReference<List<Sporocilo>> t = new TypeReference<List<Sporocilo>>() { };
			List<Sporocilo> sporocila = mapper.readValue(jsonSporocila, t);
			
			for (Sporocilo i : sporocila) {
				if (i.isGlobal()) {
					addMessage(i.getPosiljatelj(), i.getMsg(), "Others");
				} else {
					addMessage(i.getPosiljatelj(), i.getPrejemnik(), i.getMsg(), "Others");
				}
				updateLastActive(i.getPosiljatelj(), i.getSentAt());
				for (OdmevRobot or : echoRobots) {
					if (i.getPosiljatelj().equals(or.getVzdevek())) {
						or.newMessage(i.getMsg());
					}
				}
			}
		} catch (Exception e) {
			// Preverimo, èe je uporabnik še online
			if (!isOnline(imeEditor.getText())) {
				// Uporabnik ni veè vpisan
				// Poizkusimo ga izpisati in ponovno vpisati
				userLogout();
				adminMessage("Ponovno se prijavite z istim uporabniškim imenom, da zmanjšate možnost izgube sporoèil.");
			} else {
				adminMessage("Prišlo je do neprièakovanih težav. Morda ste izgubili kakšno sporoèilo.");
				System.out.println("Zaradi neznane težave sporoèil nismo uspeli pridobiti!");
			}
		}
	}
	
	public void sendMessage(String sender, String message, String recipient) {
		PosljiSporocilo sporocilo;
		if (recipient.isEmpty()) {
			// Imamo global sporoèilo (namenjeno vsem)
			sporocilo = new PosljiSporocilo(true, message);
		} else {
			// Sporoèilo je namenjeno le 1 osebi
			sporocilo = new PosljiSporociloDirect(false, message, recipient);
		}
		ObjectMapper mapper = new ObjectMapper();
		mapper.setDateFormat(new ISO8601DateFormat());
		try {
			// Sporoèilo zapišemov JSON formatu
			String jsonSporocilo = mapper.writeValueAsString(sporocilo);
			System.out.println(jsonSporocilo);

			// Sporoèilo poizkusimo poslati strežniku
			HttpCommands.posljiSporocilo(sender, jsonSporocilo);
			
			// Izpišemo ga na našem zaslonu, èe smo ga poslali mi (Funkcijo uporablja tudi robot)
			if (sender.equals(imeEditor.getText())) {
				if (sporocilo.isGlobal()) {
					addMessage(imeEditor.getText(), input.getText(), "Me");
				} else {
					addMessage(imeEditor.getText(), recipient, input.getText(), "Me");
				}
				// Pobrišemo input polje
				input.setText("");
				// Posodobimo lastActive
				updateLastActive(imeEditor.getText(), new Date());
			}
		} catch (Exception e) {
			// TODO - ??
			System.out.println("Napaka pri \"sendMessage\"! Sporoèilo ni bilo poslano!");
			e.printStackTrace();
		}
	}
	
	public void sendMessage(String sender, String message) {
		sendMessage(sender, message, "");
	}
	
	public void robotLogin(String name) throws Exception {
		HttpCommands.prijava(name);
		adminMessage("Robot " + name + " je bil uspešno prijavljen!");
	}
	
	public void urediGumbe(boolean isOnline) {
		if (isOnline) {
			imeEditor.setEditable(false);
			gumbPrijavi.setEnabled(false);
			gumbOdjavi.setEnabled(true);
			input.setEditable(true);
			whoMenu.setEnabled(true);
			miLogin.setEnabled(false);
			miLogout.setEnabled(true);
			miStartRobot.setEnabled(true);
			miStopRobot.setEnabled(true);
		} else {
			imeEditor.setEditable(true);
			gumbPrijavi.setEnabled(true);
			gumbOdjavi.setEnabled(false);
			input.setEditable(false);
			whoMenu.setEnabled(false);
			miLogin.setEnabled(true);
			miLogout.setEnabled(false);
			miStartRobot.setEnabled(false);
			miStopRobot.setEnabled(false);
		}
	}
	
	public void userLogin() {
		try {
			// Poizkusimo se prijaviti
			if (imeEditor.getText().isEmpty()) imeEditor.setText(System.getProperty("user.name"));
			
			HttpCommands.prijava(imeEditor.getText());
			
			// Primerno vklopimo / izklopimo razna polja & gumbe
			urediGumbe(true);
			
			// Uporabniku sporoèimo, da se je uspešno prijavil
			adminMessage("Uspešno ste se prijavili!");
			
			// Vklopimo robota, ki nam bo prikazoval sporoèila ostalih
			robot = new RobotZaSporocila(this);
			robot.activate();
			
			// Vklopimo robota, ki nam bo prikazoval dosegljive uporabnike
			robotDosegljivi = new RobotDosegljivi(this);
			robotDosegljivi.activate();
		} catch (Exception e) {
			adminMessage("Neuspešna prijava! Uporabnik s takšnim vzdevkom že obstaja.");
			System.out.println("Neuspešna prijava! Username že obstaja.");
		}
	}
	
	/**
	 * Method used for signing out a robot named {@code name}
	 * @param name - robots name {@code vzdevekRobot}
	 */
	public void robotLogout(String name) {
		try {
			// Robota poizkusimo odjaviti
			HttpCommands.odjava(name);
			
			// Uporabniku sporoèimo, da je robota uspešno odjavil
			adminMessage("Robot " + name + " je bil uspešno odjavljen!");
		} catch (Exception e) {
			if (isOnline(name)) {
				// Robota nismo uspeli izpisati
				// Pustimo ga na miru, saj ga `userLogout` nastavi na null in ne bo veè niè delal
				adminMessage("Robot " + name + " ni bil uspešno izpisan, je bil pa unièen in bo sèasoma izginil.");
			} else {
				// Nekako se je izpisal, kar nam odgovarja
				adminMessage("Robot " + name + " je odjavljen!");
			}
		}
	}
	
	/**
	 * Disables all {@code OdmevRobot}'s that echo user named {@code name}
	 * @param name - username
	 */
	public void disableOdmevRobot(String name) {
		int n = echoRobots.size();
		List<OdmevRobot> odstrani = new ArrayList<OdmevRobot>();
		for (int i = 0; i < n; i++) {
			if (echoRobots.get(i).getVzdevek().equals(name)) {
				echoRobots.get(i).cancel();
				odstrani.add(echoRobots.get(i));
			}
		}
		for (OdmevRobot or : odstrani) {
			echoRobots.remove(or);
		}
	}
	
	public void disableOdmevRobot() {
		int n = echoRobots.size();
		List<OdmevRobot> odstrani = new ArrayList<OdmevRobot>();
		for (int i = 0; i < n; i++) {
			echoRobots.get(i).cancel();
			odstrani.add(echoRobots.get(i));
		}
		for (OdmevRobot or : odstrani) {
			echoRobots.remove(or);
		}
	}
	
	/**
	 * Method used for signing out the user
	 */
	public void userLogout() {
		// Odjavimo oba robota za sporoèila
		robot.cancel();
		robotDosegljivi.cancel();

		// Odjavimo vse OdmevRobot-e
		disableOdmevRobot();
		
		try {
			// Uporabnika poizkusimo odjaviti
			HttpCommands.odjava(imeEditor.getText());
			
			// Primerno vklopimo / izklopimo razna polja & gumbe
			urediGumbe(false);
			
			// Uporabniku sporoèimo, da se je uspešno odjavil
			adminMessage("Uspešno ste se odjavili!");
			
			// Pobrišemo polje z dosegljivimi uporabniki
			dosegljivi.setText("");
		} catch (Exception e) {
			System.out.println("Težave pri odjavi!");
			
			// Preverimo, èe je uporabnik še vpisan
			if (isOnline(imeEditor.getText())) {
				// Uporabnik je še vpisan
				// To mu sporoèimo
				adminMessage("Neuspešna odjava!");
				adminMessage("Izgubili ste odmeve.");
				System.out.println("Uporabnik je ostal vpisan, kljub zahtevi po odjavi!");
				// Ponovno aktiviramo oba robota, EchoRobots so žal izgubljeni
				robot = new RobotZaSporocila(this);
				robot.activate();
				robotDosegljivi = new RobotDosegljivi(this);
				robotDosegljivi.activate();
			} else {
				// Uporabnik ni veè vpisan
				// To mu sporoèimo
				adminMessage("Bili ste odjavljeni!");
				System.out.println("Uporabnik se je odjavil, vendar ne po lastni želji!");
				// Primerno vklopimo / izklopimo razna polja & gumbe
				urediGumbe(false);
			}
		}
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		if (e.getSource().equals(gumbPrijavi) || e.getSource().equals(miLogin)) {
			userLogin();
		} else if (e.getSource().equals(gumbOdjavi) || e.getSource().equals(miLogout)) {
			userLogout();
		} else if (e.getSource().equals(cbRedMyName)) {
			colorMyName = Color.RED;
		} else if (e.getSource().equals(cbBlueMyName)) {
			colorMyName = Color.BLUE;
		} else if (e.getSource().equals(cbGreenMyName)) {
			colorMyName = Color.GREEN;
		} else if (e.getSource().equals(cbBlackMyName)) {
			colorMyName = Color.BLACK;
		} else if (e.getSource().equals(cbCyanMyName)) {
			colorMyName = Color.CYAN;
		} else if (e.getSource().equals(cbMagentaMyName)) {
			colorMyName = Color.MAGENTA;
		} else if (e.getSource().equals(cbOrangeMyName)) {
			colorMyName = Color.ORANGE;
		} else if (e.getSource().equals(cbCustomMyName)) {
			int r = colorRedSlider.getValue();
			int g = colorGreenSlider.getValue();
			int b = colorBlueSlider.getValue();
			colorMyName = new Color(r, g, b);
		} else if (e.getSource().equals(cbRandomMyName)) {
			int r = (int) (Math.random() * 255);
			int g = (int) (Math.random() * 255);
			int b = (int) (Math.random() * 255);
			colorMyName = new Color(r, g, b);
		} else if (e.getSource().equals(cbRedOthers)) {
			colorOthers = Color.RED;
		} else if (e.getSource().equals(cbBlueOthers)) {
			colorOthers = Color.BLUE;
		} else if (e.getSource().equals(cbGreenOthers)) {
			colorOthers = Color.GREEN;
		} else if (e.getSource().equals(cbBlackOthers)) {
			colorOthers = Color.BLACK;
		} else if (e.getSource().equals(cbCyanOthers)) {
			colorOthers = Color.CYAN;
		} else if (e.getSource().equals(cbMagentaOthers)) {
			colorOthers = Color.MAGENTA;
		} else if (e.getSource().equals(cbOrangeOthers)) {
			colorOthers = Color.ORANGE;
		} else if (e.getSource().equals(cbCustomOthers)) {
			int r = colorRedSlider.getValue();
			int g = colorGreenSlider.getValue();
			int b = colorBlueSlider.getValue();
			colorOthers = new Color(r, g, b);
		} else if (e.getSource().equals(cbRandomOthers)) {
			int r = (int) (Math.random() * 255);
			int g = (int) (Math.random() * 255);
			int b = (int) (Math.random() * 255);
			colorOthers = new Color(r, g, b);
		} else if (e.getSource().equals(cbRedMe)) {
			colorMe = Color.RED;
		} else if (e.getSource().equals(cbBlueMe)) {
			colorMe = Color.BLUE;
		} else if (e.getSource().equals(cbGreenMe)) {
			colorMe = Color.GREEN;
		} else if (e.getSource().equals(cbBlackMe)) {
			colorMe = Color.BLACK;
		} else if (e.getSource().equals(cbCyanMe)) {
			colorMe = Color.CYAN;
		} else if (e.getSource().equals(cbMagentaMe)) {
			colorMe = Color.MAGENTA;
		} else if (e.getSource().equals(cbOrangeMe)) {
			colorMe = Color.ORANGE;
		} else if (e.getSource().equals(cbCustomMe)) {
			int r = colorRedSlider.getValue();
			int g = colorGreenSlider.getValue();
			int b = colorBlueSlider.getValue();
			colorMe = new Color(r, g, b);
		} else if (e.getSource().equals(cbRandomMe)) {
			int r = (int) (Math.random() * 255);
			int g = (int) (Math.random() * 255);
			int b = (int) (Math.random() * 255);
			colorMe = new Color(r, g, b);
		} else if (e.getSource().equals(cbRedMsgOthers)) {
			colorMsgOthers = Color.RED;
		} else if (e.getSource().equals(cbBlueMsgOthers)) {
			colorMsgOthers = Color.BLUE;
		} else if (e.getSource().equals(cbGreenMsgOthers)) {
			colorMsgOthers = Color.GREEN;
		} else if (e.getSource().equals(cbBlackMsgOthers)) {
			colorMsgOthers = Color.BLACK;
		} else if (e.getSource().equals(cbCyanMsgOthers)) {
			colorMsgOthers = Color.CYAN;
		} else if (e.getSource().equals(cbMagentaMsgOthers)) {
			colorMsgOthers = Color.MAGENTA;
		} else if (e.getSource().equals(cbOrangeMsgOthers)) {
			colorMsgOthers = Color.ORANGE;
		} else if (e.getSource().equals(cbCustomMsgOthers)) {
			int r = colorRedSlider.getValue();
			int g = colorGreenSlider.getValue();
			int b = colorBlueSlider.getValue();
			colorMsgOthers = new Color(r, g, b);
		} else if (e.getSource().equals(cbRandomMsgOthers)) {
			int r = (int) (Math.random() * 255);
			int g = (int) (Math.random() * 255);
			int b = (int) (Math.random() * 255);
			colorMsgOthers = new Color(r, g, b);
		} else if (e.getSource().equals(cbWhiteBg)) {
			output.setBackground(Color.WHITE);
			dosegljivi.setBackground(Color.WHITE);
		} else if (e.getSource().equals(cbGrayBg)) {
			output.setBackground(Color.GRAY);
			dosegljivi.setBackground(Color.GRAY);
		} else if (e.getSource().equals(cbBlackBg)) {
			output.setBackground(Color.BLACK);
			dosegljivi.setBackground(Color.BLACK);
		} else if (e.getSource().equals(miStartRobot)) {
			String name = komuPosiljamo.isEmpty() ? imeEditor.getText() : komuPosiljamo;
			if (isValidRobot(name, robotDelay)) {
				OdmevRobot r = new OdmevRobot(this, name, robotDelay);
				r.activate();
				echoRobots.add(r);
			}
		} else if (e.getSource().equals(miStopRobot)) {
			disableOdmevRobot(komuPosiljamo.isEmpty() ? imeEditor.getText() : komuPosiljamo);
		}
	}
	
	public boolean isValidRobot(String name, long delay) {
		boolean isValid = true;
		for (OdmevRobot or : echoRobots) {
			if (or.getVzdevek().equals(name) && or.isActive() && absValue(delay-or.getCas()) < 500) isValid = false;
		}
		return isValid;
	}
	
	public long absValue(long x) {
		return x>=0 ? x : -x;
	}

	@Override
	public void keyTyped(KeyEvent e) {
		if (e.getSource() == this.input) {
			if (e.getKeyChar() == '\n' && !input.getText().isEmpty()) {
				sendMessage(imeEditor.getText(), input.getText(), komuPosiljamo);
			}
		} else if (e.getSource() == this.imeEditor) {
			if (e.getKeyChar() == '\n') {
				userLogin();
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
			
			// Izklopimo oba robota
			robot.cancel();
			robotDosegljivi.cancel();
			
			// Odjavimo vse OdmevRobot-e
			disableOdmevRobot();
			
			try {
				// Uporabnika poizkusimo odjaviti
				HttpCommands.odjava(imeEditor.getText());
			} catch (Exception e1) {
				// Tu je uporabniku bolj kot ne vseeno
				// On je zaprl aplikacijo, vsi roboti itd. so se izklopili
				// Edino kar se je zgodilo je, da je ostal 'vpisan' in se bo morda moral vpisati z drugim uporabniškim imenom
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
