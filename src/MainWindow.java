import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Vector;

import javax.swing.*;

import ch.ethz.ssh2.Connection;
import ch.ethz.ssh2.SCPClient;
import ch.ethz.ssh2.SFTPv3Client;
import ch.ethz.ssh2.SFTPv3DirectoryEntry;
import ch.ethz.ssh2.Session;

public class MainWindow extends JFrame {

	private static final long serialVersionUID = 1L;
	static int transButtonWidth = 60;
	static int screenWidth = 800, screenHeight = 600;
	static int serverWidth = screenWidth / 2 - transButtonWidth / 2;
	static int serverHeight = screenHeight * 7 / 8;
	static int staticElem = 0;
	static final long maxServerBytes = 5368709120L;
	static boolean isMainWindowOpen = false;
	static JFrame mainWindowFrame = new JFrame("Cruzbox");
	static JList<String> serverFilesJList;
	static JScrollPane serverScroller;
	static JFileChooser yourFiles;
	static JComboBox<String> pathComboBox;
	static JButton logoutButton;
	static JButton getButton;
	static JButton putButton;
	static JButton upOneLevelButton;
	static JButton homeButton;
	static JButton newFolderButton;
	static JButton renameButton;
	static JButton deleteButton;
	JTextField fileNameField;
	JTextField fileFindField;
	JLabel fileFindLabel;
	JLabel comboBoxLabel;
	JLabel fileNameLabel;
	static String home = "";
	static String userName;
	static String currentLocalDir = "";
	static String currentServerDir = "";
	static String yourSelection = "";
	ClassLoader picLoader = getClass().getClassLoader();
	String pathToUpIcon = "javax/swing/plaf/metal/icons/ocean/upFolder.gif";
	String pathToHomeIcon = "javax/swing/plaf/metal/icons/ocean/homeFolder.gif";
	String pathToNewFolderIcon = "javax/swing/plaf/metal/icons/ocean/newFolder.gif";
	String pathToFileIcon = "javax/swing/plaf/metal/icons/ocean/file.gif";
	String pathToFolderIcon = "javax/swing/plaf/metal/icons/ocean/directory.gif";
	static ImageIcon upIcon;
	static ImageIcon homeIcon;
	static ImageIcon newFolderIcon;
	static ImageIcon folderIcon;
	static ImageIcon fileIcon;
	IconListRenderer renderer = new IconListRenderer();
	static String[][] elem;
	static String[] newList;
	SCPClient transferClient;
	static Connection mainCon = new Connection(IntroWindow.serverIP);
	static Session openSession;
	static SFTPv3Client fileAtrib;
	InputStream incoming;
	OutputStream outgoing;

	public MainWindow() {
		try {
			mainCon.connect();
			userName = IntroWindow.userNameBox.getText();
			boolean didItConnect = mainCon.authenticateWithPassword(userName,new String(IntroWindow.passwordBox.getPassword()));
			if (didItConnect) {
				fileAtrib = new SFTPv3Client(mainCon);
				isMainWindowOpen = true;
				IntroWindow.introWindowFrame.dispose();
			}
			openSession = mainCon.openSession();
			incoming = openSession.getStdout();
			openSession.execCommand("pwd");
			String[][] dirArray = getStringFromInputStream(incoming);
			home = dirArray[0][0]+"/";
			currentServerDir = home;
			openSession.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		if (isMainWindowOpen) {
			upIcon = new ImageIcon(picLoader.getResource(pathToUpIcon));
			picLoader = getClass().getClassLoader();
			homeIcon = new ImageIcon(picLoader.getResource(pathToHomeIcon));
			picLoader = getClass().getClassLoader();
			newFolderIcon = new ImageIcon(
					picLoader.getResource(pathToNewFolderIcon));
			picLoader = getClass().getClassLoader();
			folderIcon = new ImageIcon(picLoader.getResource(pathToFolderIcon));
			picLoader = getClass().getClassLoader();
			fileIcon = new ImageIcon(picLoader.getResource(pathToFileIcon));
			yourFiles = new JFileChooser();
			SpringLayout serverLayout = new SpringLayout();
			JPanel transferButtons = new JPanel(new GridLayout(2, 1));
			JPanel completeServerPanel = new JPanel();
			JPanel yoursAndServers = new JPanel(new BorderLayout());
			JPanel buttonPanel = new JPanel(new CardLayout());
			JPanel everything = new JPanel(new BorderLayout());

			everything.add(yoursAndServers);
			everything.add(BorderLayout.SOUTH, buttonPanel);
			comboBoxLabel = new JLabel("Look In: ");
			fileNameLabel = new JLabel("File Name: ");
			fileFindLabel = new JLabel("Find: ");

			pathComboBox = new JComboBox<String>();
			upOneLevelButton = new JButton(upIcon);
			homeButton = new JButton(homeIcon);
			newFolderButton = new JButton(folderIcon);
			renameButton = new JButton("Rename");
			deleteButton = new JButton("Delete");
			getButton = new JButton("<");
			putButton = new JButton(">");
			logoutButton = new JButton("Logout/Quit");
			fileNameField = new JTextField(22);
			fileFindField = new JTextField(22);
			completeServerPanel.add(pathComboBox);
			completeServerPanel.add(comboBoxLabel);
			completeServerPanel.add(homeButton);
			completeServerPanel.add(upOneLevelButton);
			completeServerPanel.add(newFolderButton);
			completeServerPanel.add(renameButton);
			completeServerPanel.add(deleteButton);
			completeServerPanel.add(fileNameField);
			completeServerPanel.add(fileNameLabel);
			completeServerPanel.add(fileFindField);
			completeServerPanel.add(fileFindLabel);
			transferButtons.add(getButton);
			transferButtons.add(putButton);
			buttonPanel.add(logoutButton);
			String[] dirPaths = { "" };
			serverFilesJList = new JList<String>(dirPaths);
			serverScroller = new JScrollPane(serverFilesJList);
			completeServerPanel.add(serverScroller);
			yoursAndServers.add(BorderLayout.WEST, yourFiles);
			yoursAndServers.add(BorderLayout.CENTER, transferButtons);
			yoursAndServers.add(BorderLayout.EAST, completeServerPanel);
			mainWindowFrame.add(everything);
			yourFiles.setFileSelectionMode(JFileChooser.FILES_AND_DIRECTORIES);
			comboBoxLabel.setLabelFor(pathComboBox);
			fileFindLabel.setLabelFor(fileFindField);
			fileNameLabel.setLabelFor(fileNameField);
			mainWindowFrame.setSize(screenWidth, screenHeight);
			mainWindowFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
			yourFiles.setPreferredSize(new Dimension(serverWidth, screenHeight));
			completeServerPanel.setPreferredSize(new Dimension(serverWidth,screenHeight));
			mainWindowFrame.setVisible(true);

			refreshServerDir();
			addGUIListeners();
			buildServerLayout(completeServerPanel, serverLayout);
		}
	}

	private void buildServerLayout(JPanel p, SpringLayout l) {
		p.setLayout(l);
		l.putConstraint(SpringLayout.NORTH, comboBoxLabel, screenHeight / 35,SpringLayout.NORTH, p);
		l.putConstraint(SpringLayout.WEST, comboBoxLabel, serverWidth / 25,SpringLayout.WEST, p);
		l.putConstraint(SpringLayout.NORTH, pathComboBox, screenHeight / 50,SpringLayout.NORTH, p);
		l.putConstraint(SpringLayout.WEST, pathComboBox, serverWidth / 5,SpringLayout.WEST, p);
		l.putConstraint(SpringLayout.NORTH, upOneLevelButton,screenHeight / 50, SpringLayout.NORTH, p);
		l.putConstraint(SpringLayout.WEST, upOneLevelButton, serverWidth / 25,SpringLayout.EAST, pathComboBox);
		l.putConstraint(SpringLayout.NORTH, homeButton, screenHeight / 50,SpringLayout.NORTH, p);
		l.putConstraint(SpringLayout.WEST, homeButton, serverWidth / 75,SpringLayout.EAST, upOneLevelButton);
		l.putConstraint(SpringLayout.NORTH, newFolderButton, screenHeight / 50,SpringLayout.NORTH, p);
		l.putConstraint(SpringLayout.WEST, newFolderButton, serverWidth / 75,SpringLayout.EAST, homeButton);
		l.putConstraint(SpringLayout.NORTH, serverScroller,screenHeight / 11, SpringLayout.NORTH, p);
		l.putConstraint(SpringLayout.WEST, serverScroller, serverWidth / 20,	SpringLayout.WEST, p);
		l.putConstraint(SpringLayout.NORTH, deleteButton,screenHeight * 17 / 20, SpringLayout.NORTH, p);
		l.putConstraint(SpringLayout.WEST, deleteButton, serverWidth * 15 / 20,	SpringLayout.WEST, p);
		l.putConstraint(SpringLayout.NORTH, renameButton,	screenHeight * 17 / 20, SpringLayout.NORTH, p);
		l.putConstraint(SpringLayout.WEST, renameButton, serverWidth / 2 - 3,SpringLayout.WEST, p);
		l.putConstraint(SpringLayout.NORTH, fileNameField,screenHeight * 59 / 80, SpringLayout.NORTH, p);
		l.putConstraint(SpringLayout.WEST, fileNameField, serverWidth * 3 / 10,SpringLayout.WEST, p);
		l.putConstraint(SpringLayout.NORTH, fileNameLabel,screenHeight * 59 / 80, SpringLayout.NORTH, p);
		l.putConstraint(SpringLayout.WEST, fileNameLabel, serverWidth / 20,SpringLayout.WEST, p);
		l.putConstraint(SpringLayout.NORTH, fileFindField,screenHeight * 63 / 80, SpringLayout.NORTH, p);
		l.putConstraint(SpringLayout.WEST, fileFindField, serverWidth*3/10,SpringLayout.WEST, p);
		l.putConstraint(SpringLayout.NORTH, fileFindLabel,screenHeight * 63 / 80, SpringLayout.NORTH, p);
		l.putConstraint(SpringLayout.WEST, fileFindLabel, serverWidth *3/ 20,SpringLayout.WEST, p);

		pathComboBox.setPreferredSize(new Dimension(150, 25));
		upOneLevelButton.setPreferredSize(new Dimension(25, 25));
		homeButton.setPreferredSize(new Dimension(25, 25));
		newFolderButton.setPreferredSize(new Dimension(25, 25));
		serverScroller.setPreferredSize(new Dimension(serverWidth * 9 / 10, screenHeight * 19 / 32));
	}

	private void addGUIListeners() {
		fileFindField.addActionListener(new ActionListener(){
			@Override
			public void actionPerformed(ActionEvent arg0) {
				String findThis = fileFindField.getText();
				for(int i=0;i<newList.length;i++){
					if(newList[i].contains(findThis)){
						serverFilesJList.setSelectedIndex(i);
						return;
					}
				}
			}});
		renameButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				renameServerDir();
			}
		});
		deleteButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				deleteServerDir();
			}
		});
		upOneLevelButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				exitServerDir();
			}
		});
		homeButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				currentServerDir =home;
				refreshServerDir();
			}
		});
		newFolderButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				makeServerDir();
			}
		});
		getButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				getFile();
			}
		});
		putButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				putFile();
			}
		});
		logoutButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				mainCon.close();
				mainWindowFrame.dispose();
			}
		});
		serverFilesJList.addMouseListener(new MouseListener() {
			public void mouseClicked(MouseEvent arg0) {
				if (serverFilesJList.getSelectedIndex() >= 0) {
					staticElem = serverFilesJList.getSelectedIndex();
					fileNameField.setText(newList[staticElem]);
				}
				if (arg0.getClickCount() == 2) {
					enterServerDir();
				}
			}
			public void mouseEntered(MouseEvent arg0) {}
			public void mouseExited(MouseEvent arg0) {}
			public void mousePressed(MouseEvent arg0) {}
			public void mouseReleased(MouseEvent arg0) {}
		});
		mainWindowFrame.addWindowListener(new WindowListener() {
			@Override
			public void windowActivated(WindowEvent arg0) {}
			@Override
			public void windowClosed(WindowEvent arg0) {
				dispose();
			}
			@Override
			public void windowClosing(WindowEvent arg0) {}
			@Override
			public void windowDeactivated(WindowEvent arg0) {}
			@Override
			public void windowDeiconified(WindowEvent arg0) {}
			@Override
			public void windowIconified(WindowEvent arg0) {}
			@Override
			public void windowOpened(WindowEvent arg0) {}
		});
	}

	protected void renameServerDir() {
		if (staticElem == -1)
			return;
		final String itemName = newList[staticElem];
		String labelString = "What would you like to rename" + itemName
				+ " to?";
		final JFrame renameFrame = new JFrame();
		JPanel renamePanel = new JPanel(new BorderLayout());
		JLabel renameLabel = new JLabel(labelString);
		final JTextField renameText = new JTextField();
		JButton cancelButton = new JButton("Cancel");
		JButton renameButton = new JButton("Rename");
		renamePanel.add(BorderLayout.NORTH, renameLabel);
		renamePanel.add(BorderLayout.CENTER, renameText);
		renamePanel.add(BorderLayout.SOUTH, cancelButton);
		renamePanel.add(BorderLayout.SOUTH, renameButton);
		renameFrame.add(renamePanel);
		renameFrame.setSize((int) (labelString.length() * 8), 90);
		renameFrame.setVisible(true);
		cancelButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				renameFrame.dispose();
				return;
			}
		});
		renameButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				try {
					openSession = mainCon.openSession();
					openSession.execCommand("mv " + currentServerDir + itemName
							+ " " + currentServerDir + renameText.getText());
					openSession.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
				refreshServerDir();
				renameFrame.dispose();
				return;
			}
		});
	}

	protected void makeServerDir() {
		final JFrame newFolderFrame = new JFrame("Make a new folder");
		JPanel newFolderPanel = new JPanel(new BorderLayout());
		JLabel descrip = new JLabel("Enter a name for the new folder");
		final JTextField fileNameField = new JTextField();
		JButton cancelButton = new JButton("Cancel");
		JButton makeFileButton = new JButton("Create");

		newFolderPanel.add(BorderLayout.NORTH, descrip);
		newFolderPanel.add(fileNameField);
		newFolderPanel.add(BorderLayout.SOUTH, cancelButton);
		newFolderPanel.add(BorderLayout.SOUTH, makeFileButton);
		newFolderFrame.add(newFolderPanel);

		cancelButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				newFolderFrame.dispose();
			}
		});
		makeFileButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				try {
					openSession = mainCon.openSession();
					openSession.execCommand("cd " + currentServerDir
							+ " && mkdir " + fileNameField.getText());
					refreshServerDir();
					openSession.close();
					newFolderFrame.dispose();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		});
		newFolderFrame.setSize(250, 90);
		newFolderFrame.setVisible(true);
	}

	private void deleteServerDir() {
		try {
			openSession = mainCon.openSession();
			int dialog = JOptionPane.YES_NO_OPTION;
			int dialogResult = JOptionPane.showConfirmDialog(null,
					"Are you sure?", "Warning", dialog);
			if (dialogResult == JOptionPane.YES_OPTION) {
				int fileIndex = serverFilesJList.getSelectedIndex();
				String fileToDelete = newList[fileIndex];
				if (fileAtrib.lstat(currentServerDir + fileToDelete)
						.isRegularFile()) {
					fileAtrib.rm(currentServerDir + fileToDelete);
				} else
					emptyFilledDir(currentServerDir + fileToDelete);
				openSession.close();
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		refreshServerDir();
	}

	private void emptyFilledDir(String absPath) {
		try {
			
			@SuppressWarnings("rawtypes")
			Vector fileList = fileAtrib.ls(absPath);
			SFTPv3DirectoryEntry[] fileEntries = new SFTPv3DirectoryEntry[fileList.size()];
			ArrayList<String> fileNames = new ArrayList<String>();
			for (int i = 0; i < fileList.size(); i++) {
				fileEntries[i] = (SFTPv3DirectoryEntry) fileList.elementAt(i);
				if (!fileEntries[i].filename.replace('.', ' ').trim().isEmpty()) {
					fileNames.add(fileEntries[i].filename);
				}
			}
			String[] fileArray = fileNames.toArray(new String[0]);
			for (int i = 0; i < fileArray.length; i++) {
				String path = absPath + "/" + fileArray[i];
				if (fileAtrib.lstat(path).isRegularFile()) {
					fileAtrib.rm(path);
					continue;
				}
				if (fileAtrib.lstat(path).isDirectory()) {
					emptyFilledDir(path);
				}
			}
			fileAtrib.rmdir(absPath);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	private void refreshServerDir() {
		try {
			openSession = mainCon.openSession();
		} catch (IOException e1) {
			e1.printStackTrace();
		}
		ArrayList<String> protoFileList = new ArrayList<String>();
		ArrayList<String> protoDirList = new ArrayList<String>();
		incoming = openSession.getStdout();
		outgoing = openSession.getStdin();
		String[] fileArray = null;
		try{
			currentServerDir = currentServerDir.trim();
			@SuppressWarnings("rawtypes")
			
			Vector fileList = fileAtrib.ls(currentServerDir);
			SFTPv3DirectoryEntry[] fileEntries = new SFTPv3DirectoryEntry[fileList.size()];
			ArrayList<String> fileNames = new ArrayList<String>();
			for (int i = 0; i < fileList.size(); i++) {
				fileEntries[i] = (SFTPv3DirectoryEntry) fileList.elementAt(i);
				if (!fileEntries[i].filename.startsWith(".")) {
					fileNames.add(fileEntries[i].filename);
				}
			}
			fileArray = fileNames.toArray(new String[0]);
			currentServerDir = currentServerDir.trim();
			for (int i = 0; i < fileArray.length; i++) {
				if (fileAtrib.lstat(currentServerDir + fileArray[i]).isDirectory()) {
					protoDirList.add(fileArray[i]);
				} else protoFileList.add(fileArray[i]);
			}
		}catch(IOException e){
			e.printStackTrace();
			try {
				openSession.execCommand("cd " + currentServerDir + " && ls");
				elem = getStringFromInputStream(incoming);
				for (int i = 0; i < elem.length; i++) {
					if (fileAtrib.lstat(currentServerDir + elem[i][0])
							.isDirectory()) {
						protoDirList.add(elem[i][0]);
					} else protoFileList.add(elem[i][0]);
				}
		} catch (IOException e2) {
			e2.printStackTrace();
		}
		}
		pathComboBox.removeAllItems();
		pathComboBox.addItem(userName);
		char[] currentPathToChar = currentServerDir.toCharArray();
		int previousSlash = 0;
		for (int i = 0; i < currentPathToChar.length; i++) {
			if (currentPathToChar[i] == '/') {
				String item = currentServerDir.substring(previousSlash, i);
				pathComboBox.addItem(item);
				previousSlash = i;
			}
		}
		pathComboBox.setSelectedIndex(pathComboBox.getItemCount()-1);
		ArrayList<String> filesAndDirs = new ArrayList<String>(protoDirList);
		filesAndDirs.addAll(protoFileList);
		newList = filesAndDirs.toArray(new String[0]);
		serverFilesJList.setListData(newList);
		serverFilesJList.setCellRenderer(renderer);
		openSession.close();
	}

	private void enterServerDir() {
		int selectedElem = serverFilesJList.getSelectedIndex();
		String pathExt = "";
		if (selectedElem >= 0)
			pathExt = newList[selectedElem] + "/";
		try {
			if (!fileAtrib.lstat(
					currentServerDir.trim() + newList[selectedElem])
					.isDirectory())
				return;
		} catch (IOException e) {
			e.printStackTrace();
		}
		currentServerDir = currentServerDir + pathExt;
		refreshServerDir();
	}

	private void exitServerDir() {
		//if(currentServerDir.equals("home"))return;
		char[] dirCharArray = currentServerDir.toCharArray();
		int substringIndex = dirCharArray.length;
		int ignoreFirst = 0;
		for (int i = 0; i < dirCharArray.length; i++) {
			if (dirCharArray[dirCharArray.length - i - 1] == ('/')) {
				ignoreFirst++;
			}
			if (ignoreFirst == 2) {
				substringIndex = dirCharArray.length - i;
				break;
			}
			if (dirCharArray.length - i - 1 == 0 && ignoreFirst == 1) {
				substringIndex = 0;
			}
		}
		currentServerDir = currentServerDir.substring(0, substringIndex);
		refreshServerDir();
	}

	private void getFile() {
		int selectedElem = serverFilesJList.getSelectedIndex();
		if (selectedElem == -1)
			return;
		String[] localCurrentFiles = yourFiles.getCurrentDirectory().list();
		for (int i = 0; i < localCurrentFiles.length; i++) {
		}
		transferClient = new SCPClient(mainCon);
		String tempServerDir = currentServerDir
				+ serverFilesJList.getSelectedValue();
		try {
			if (fileAtrib.lstat(tempServerDir).isRegularFile()) {
				transferClient.get(tempServerDir, yourFiles
						.getCurrentDirectory().toString());
			} else if (fileAtrib.lstat(tempServerDir).isDirectory()) {
				getDir(yourFiles.getCurrentDirectory().toString() + "/"
						+ serverFilesJList.getSelectedValue(), tempServerDir);
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		yourFiles.rescanCurrentDirectory();
	}

	private void getDir(String localPath, String serverPath) {
		File newLocalFile = new File(localPath);
		if (!newLocalFile.exists()) {
			newLocalFile.mkdir();
			try {
				@SuppressWarnings("rawtypes")
				Vector fileList = fileAtrib.ls(serverPath);
				SFTPv3DirectoryEntry[] fileEntries = new SFTPv3DirectoryEntry[fileList
						.size()];
				ArrayList<String> fileNames = new ArrayList<String>();
				for (int i = 0; i < fileList.size(); i++) {
					fileEntries[i] = (SFTPv3DirectoryEntry) fileList
							.elementAt(i);
					if (!fileEntries[i].filename.replace('.', ' ').trim()
							.isEmpty()) {
						fileNames.add(fileEntries[i].filename);
					}
				}
				for (int i = 0; i < fileNames.size(); i++) {
					if (fileAtrib.lstat(serverPath + "/" + fileNames.get(i))
							.isRegularFile()) {
						transferClient.get(serverPath + "/" + fileNames.get(i),
								newLocalFile.toString());
					} else if (fileAtrib.lstat(
							serverPath + "/" + fileNames.get(i)).isDirectory()) {
						getDir(newLocalFile.toString() + "/" + fileNames.get(i),
								serverPath + "/" + fileNames.get(i));
					}
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		} else {
			IntroWindow
					.showError(
							"File already exists",
							"You can't copy this folder yet, "
									+ "since there is another folder with the same name.");
		}
	}

	private void putFile() {
		transferClient = new SCPClient(mainCon);
		String currentLocalFile = yourFiles.getSelectedFile().getPath();
		String currentDirName = yourFiles.getSelectedFile().getName();
		try {
			if (new File(currentLocalFile).isFile()) {
					transferClient.put(currentLocalFile, currentServerDir);
				}
			 else if (yourFiles.getSelectedFile().isDirectory()) {
				Session session = mainCon.openSession();
				session.execCommand("cd " + currentServerDir + " && mkdir "
						+ currentDirName);
				session.close();
				putDir(currentLocalFile, currentServerDir
						+ currentDirName);
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		refreshServerDir();
	}

	private void putDir(String currentLocalFile, String currentDirName) {
		String[] fileContents = new File(currentLocalFile).list();
		try {
			for (int i = 0; i < fileContents.length; i++) {
				if (new File(currentLocalFile + "/" + fileContents[i]).isDirectory()) {
						Session session = mainCon.openSession();
						session.execCommand("cd " + currentDirName + " && mkdir " + fileContents[i]);
						session.close();
						putDir(currentLocalFile + "/" + fileContents[i],currentDirName + "/" + fileContents[i]);	
				}
				if (new File(currentLocalFile + "/" + fileContents[i]).isFile()) {
						String file = currentLocalFile + "/" + fileContents[i];
						transferClient.put(file, currentDirName);					
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public static String[][] getStringFromInputStream(InputStream is) {
		BufferedReader br = null;
		StringBuilder sb = new StringBuilder();
		String line;
		ArrayList<String> fileList = new ArrayList<String>();
		String[][] fileArray = null;
		try {
			br = new BufferedReader(new InputStreamReader(is));
			while ((line = br.readLine()) != null) {
				sb.append(line);
				fileList.add(line);
			}
			fileArray = new String[fileList.size()][1];
			for (int i = 0; i < fileList.size(); i++) {
				fileArray[i][0] = fileList.get(i);
			}
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			if (br != null) {
				try {
					br.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
		return fileArray;
	}
}
