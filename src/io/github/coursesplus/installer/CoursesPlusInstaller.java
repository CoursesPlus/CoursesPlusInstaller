package io.github.coursesplus.installer;

import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.attribute.PosixFilePermission;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;

import com.apple.eawt.Application;

@SuppressWarnings("serial")
public class CoursesPlusInstaller extends JFrame implements ActionListener {
	public static final String VERSION = "0.1";

	public static Logger logger;

	public static Dimension screenSize;

	public static Font font;
	public static Font bigFont;
	public static Font titleFont;

	public static final String BASE_PATH = "/Users" + System.getProperty("user.name") + "Library/Application Support/Google/Chrome/";
	public static final String FOLDER_NAME = "External Extensions";
	public static final String EXT_ID = "pieincmodljnbihihjnapcmhdddhbpgi";
	public static final String EXT_FILE = "{\"external_update_url\": \"https://clients2.google.com/service/update2/crx\"}";

	public static final String LOAD_PAGE = "data:text/html,<h1>Loading, please wait...</h1><h2>Do not navigate away from this page while installation is in progress.</h2>";

	public CoursesPlusInstaller() {
		super("CoursesPlus Installer v" + VERSION);

		logger = Logger.getLogger(CoursesPlusInstaller.class.getName());

		screenSize = java.awt.Toolkit.getDefaultToolkit().getScreenSize();

		Application.getApplication().setDockIconImage(new ImageIcon(getClass().getResource("Logo.png")).getImage());
		public class Launcher {
  		public static void main(String[] args) {
    		System.setProperty("com.apple.mrj.application.apple.menu.about.name", "CoursesPlus Installer");
    		JFrame jframe = new MyJFrame();
    		jframe.setVisible(true);
  		}
		}

		try {
			// try and load Lato
			font = Font.createFont(Font.TRUETYPE_FONT, getClass().getResourceAsStream("Lato-Regular.ttf"));

			font = font.deriveFont(14f);
			bigFont = font.deriveFont(32f);
			titleFont = font.deriveFont(64f);
		} catch (Exception e) {
		    // if it failed, fallbacks
			// but first log failure
			logger.log(Level.WARNING, "Failed to load fonts - " + e.getMessage(), e);
			font = new Font("SansSerif", Font.PLAIN, 14);
			bigFont = new Font("SansSerif", Font.PLAIN, 32);
			titleFont = new Font("SansSerif", Font.PLAIN, 64);
		}

		setLayout(new GridLayout(3, 1));

		JLabel title = new JLabel("CoursesPlus", JLabel.CENTER);
			title.setFont(titleFont);
		add(title);

		JButton install = new JButton("Install");
			install.setFont(bigFont);
			install.addActionListener(this);
		add(install);

		JLabel info = new JLabel("<html><div width=\"400px\" style=\"text-align:center;\">Hi " + System.getProperty("user.name") + "! This installer will install CoursesPlus into Google Chrome. It will connect to the Internet to download the latest version of CoursesPlus, and then set up automatic updates. Anonymous analytics may be sent to us for statistical purposes. If an error occurs, you will be given the option to transmit information about the error to us so we can fix it.</div></html>", JLabel.CENTER);
			info.setFont(font);
		add(info);

		setBounds((screenSize.width - 600) / 2, (screenSize.height - 400) / 2, 600, 400);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

		setVisible(true);
	}

	public static void main(String[] args) {
		new CoursesPlusInstaller();
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		Object source = e.getSource();
		String text = ((JButton)source).getText();
		switch (text) {
			case "Install":
				JOptionPane.showMessageDialog(null, "Make sure you've closed open Chrome windows. Any windows remaining open will automatically be closed after pressing OK.", "Close Chrome windows", JOptionPane.WARNING_MESSAGE);

				try {
					Runtime.getRuntime().exec(new String[]{"killall", "Google Chrome"});
				} catch (IOException e4) {
					// TODO Auto-generated catch block
					e4.printStackTrace();
				}

				File destDir = new File(BASE_PATH + FOLDER_NAME);
				if (!destDir.exists()) {
					destDir.mkdir();
					destDir = new File(BASE_PATH + FOLDER_NAME);
				}
				try {
					PrintWriter writer = new PrintWriter(BASE_PATH + FOLDER_NAME + "/" + EXT_ID + ".json", "UTF-8");
					writer.write(EXT_FILE);
					writer.close();
				} catch (FileNotFoundException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				} catch (UnsupportedEncodingException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}

				// Clear out extension blacklist so user can reinstall extension if they removed it
				// should ask NLTL to put CoursesPlus on policy-set whitelist....
				Path blDir = Paths.get("/Users/" + System.getProperty("user.name") + "/Library/Application Support/Google/Chrome/Safe Browsing Extension Blacklist");
				try {
					Files.deleteIfExists(blDir);
				} catch (IOException e3) {
					// TODO Auto-generated catch block
					e3.printStackTrace();
				}

				Path extensionDir = Paths.get("/Users/" + System.getProperty("user.name") + "/Library/Application Support/Google/Chrome/Default/Extensions");
				Set<PosixFilePermission> orig = null;
				try {
					// save original Extensions dir permissions
					orig = Files.getPosixFilePermissions(extensionDir);

					// make Extensions dir writeable
					Set<PosixFilePermission> perms = Files.getPosixFilePermissions(extensionDir);
					perms.add(PosixFilePermission.OWNER_WRITE);
					Files.setPosixFilePermissions(extensionDir, perms);

					// open Chrome
					String[] cmd = {"/Applications/Google Chrome.app/Contents/MacOS/Google Chrome", LOAD_PAGE};
					Process chrome = Runtime.getRuntime().exec(cmd);
					Thread.sleep(7500);
					Runtime.getRuntime().exec(new String[]{"killall", "Google Chrome"});
					Thread.sleep(100);
					Runtime.getRuntime().exec(new String[]{"killall", "Google Chrome"});

					// restore Extensions dir permissions
					Files.setPosixFilePermissions(extensionDir, orig);
				} catch (IOException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				} catch (InterruptedException e1) {
					// TODO Auto-generated catch block
					if (orig != null) {
						// restore the permissions
						try {
							Files.setPosixFilePermissions(extensionDir, orig);
						} catch (IOException e2) {
							// TODO Auto-generated catch block
							e2.printStackTrace();
						}
					}

					e1.printStackTrace();
				}

				JOptionPane.showMessageDialog(null, "The installation has been completed. See you later, " + System.getProperty("user.name"));

				break;

			default:
				logger.warning("Unknown button pressed - " + text);
				break;
		}
	}
}
