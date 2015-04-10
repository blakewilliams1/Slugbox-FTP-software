import java.awt.BorderLayout;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.*;



public class IntroWindow {
	static int screenWidth = 500, screenHeight = 300;
	static int buttonWidth=80;
	static int buttonTextGap = 30;
	static int buttonGap = (screenWidth-2*buttonWidth)/3;
	static int textBoxLength = 15, columnWidth = 12;
	static int textBoxPosY = (int)(screenHeight*(float)2/5);
	static int textBoxGap = (screenWidth-textBoxLength*columnWidth*2)/3;
	static int serverSize = 700;
	static String serverIP = "128.114.108.152";
	static int versionNum = 1;
	static JFrame introWindowFrame = new JFrame("Welcome to Cruzbox");
	static JPanel introWindowContainer = new ImagePanel(screenWidth, screenHeight);
	static JLabel usernameLabel = new JLabel("Username");
	static JLabel passwordLabel = new JLabel("Password");
	static JLabel newUserLabel = new JLabel("New User");
	static JLabel forgotPasswordLabel = new JLabel("Forgot password");
	static JLabel deleteUserLabel = new JLabel("Request account deletion");
	static JTextField userNameBox = new JTextField("", textBoxLength);
	static JPasswordField passwordBox = new JPasswordField("", textBoxLength);
	static JButton attemptConnect = new JButton("Connect");
	static JButton quitButton = new JButton("Quit");
	static JButton[] introButtons = {attemptConnect, quitButton};
	static MainWindow theMeat;
	
	public IntroWindow(){
		SpringLayout introLayout = new SpringLayout();
		buildIntroLayout(introLayout);
		introWindowContainer.add(usernameLabel);
		introWindowContainer.add(passwordLabel);
		introWindowContainer.add(userNameBox);
	    introWindowContainer.add(passwordBox);
		for(int i=0;i<introButtons.length;i++){
		    introWindowContainer.add(introButtons[i]);
		    }
		buildListeners();
		Font font =new Font("MONOSPACED", Font.PLAIN, 10);
		forgotPasswordLabel.setFont(font);
		newUserLabel.setFont(font);
		deleteUserLabel.setFont(font);
		introWindowFrame.setSize(screenWidth, screenHeight);
		introWindowFrame.add(introWindowContainer);
		introWindowFrame.setResizable(false);
		introWindowFrame.setVisible(true);
	}
	public static void main(String[] args){
		@SuppressWarnings("unused")
		IntroWindow skybox = new IntroWindow();
	}
	private static void buildListeners() {
		passwordBox.addActionListener(new ActionListener(){
			@Override
			public void actionPerformed(ActionEvent arg0) {
				openMainWindow();
			}});
		attemptConnect.addActionListener(new ActionListener(){
			@Override
			public void actionPerformed(ActionEvent arg0) {
				openMainWindow();	
			}});
		quitButton.addActionListener(new ActionListener(){
			@Override
			public void actionPerformed(ActionEvent e) {
				quitProgram();
			}});
	}
	private static void buildIntroLayout(SpringLayout introLayout) {
		introWindowContainer.setLayout(introLayout);
		introLayout.putConstraint(SpringLayout.NORTH, userNameBox, textBoxPosY, SpringLayout.NORTH, introWindowContainer);
		introLayout.putConstraint(SpringLayout.WEST, userNameBox, textBoxGap, SpringLayout.WEST, introWindowContainer);
		introLayout.putConstraint(SpringLayout.NORTH, passwordBox, textBoxPosY, SpringLayout.NORTH, introWindowContainer);
		introLayout.putConstraint(SpringLayout.WEST, passwordBox, textBoxGap, SpringLayout.EAST, userNameBox);
		introLayout.putConstraint(SpringLayout.NORTH, attemptConnect, buttonTextGap, SpringLayout.SOUTH, userNameBox);
		introLayout.putConstraint(SpringLayout.NORTH, quitButton, buttonTextGap, SpringLayout.SOUTH, userNameBox);
		introLayout.putConstraint(SpringLayout.WEST, quitButton, buttonGap, SpringLayout.EAST, attemptConnect);
		introLayout.putConstraint(SpringLayout.WEST, attemptConnect, buttonGap, SpringLayout.WEST, introWindowContainer);
		introLayout.putConstraint(SpringLayout.SOUTH, usernameLabel, -10, SpringLayout.NORTH, userNameBox);
		introLayout.putConstraint(SpringLayout.WEST, usernameLabel, 0, SpringLayout.WEST, userNameBox);
		introLayout.putConstraint(SpringLayout.SOUTH, passwordLabel, -10, SpringLayout.NORTH, passwordBox);
		introLayout.putConstraint(SpringLayout.WEST, passwordLabel, 0, SpringLayout.WEST, passwordBox);
		introLayout.putConstraint(SpringLayout.NORTH, newUserLabel, 3, SpringLayout.SOUTH, userNameBox);
		introLayout.putConstraint(SpringLayout.WEST, newUserLabel, textBoxGap, SpringLayout.WEST, introWindowContainer);
		introLayout.putConstraint(SpringLayout.NORTH, forgotPasswordLabel, 3, SpringLayout.SOUTH, passwordBox);
		introLayout.putConstraint(SpringLayout.WEST, forgotPasswordLabel, 0, SpringLayout.WEST, passwordBox);
		introLayout.putConstraint(SpringLayout.SOUTH, deleteUserLabel, -1, SpringLayout.SOUTH, introWindowContainer);
		introLayout.putConstraint(SpringLayout.EAST, deleteUserLabel, -1, SpringLayout.EAST, introWindowContainer);
	}
	public static void openMainWindow(){
		theMeat = new MainWindow();
	}
	public static void quitProgram(){
		 introWindowFrame.dispose();
	}
	public static void showError(String title, String message) {
		final JFrame errorScreen = new JFrame(title);
		JPanel errorPanel = new JPanel(new BorderLayout());
		JTextArea errorMessage = new JTextArea(message);
		JButton closeButton = new JButton("Ok");
		errorMessage.setEditable(false);
		errorPanel.add(BorderLayout.SOUTH,closeButton);
		errorPanel.add(errorMessage);
		errorScreen.add(errorPanel);
		closeButton.addActionListener(new ActionListener(){
			@Override
			public void actionPerformed(ActionEvent arg0) {
				errorScreen.dispose();
			}});
		errorMessage.setLineWrap(true);
		errorMessage.setWrapStyleWord(true);
		int sqrSize = (int) Math.sqrt(message.length()*8*15)+1;
		errorScreen.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		errorScreen.setSize(sqrSize*2,sqrSize/2+52);
		errorScreen.toFront();
		errorScreen.repaint();
		errorScreen.setVisible(true);
	}
}
