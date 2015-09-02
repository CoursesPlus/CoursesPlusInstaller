package io.github.coursesplus.installer;

import java.awt.Dimension;
import java.awt.Font;
import java.awt.GraphicsDevice;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;

@SuppressWarnings("serial")
public class CoursesPlusInstaller extends JFrame implements ActionListener {
	public static final String VERSION = "0.1";
	
	public static Logger logger;
	
	public static Dimension screenSize;

	public static Font font;
	public static Font bigFont;
	public static Font titleFont;
	
	public static final String EXT_FILE = "";
	
	public CoursesPlusInstaller() {
		super("CoursesPlus Installer v" + VERSION);
		
		logger = Logger.getLogger(CoursesPlusInstaller.class.getName());
		
		screenSize = java.awt.Toolkit.getDefaultToolkit().getScreenSize();
		
		try {
			// try and load Lato
			font = Font.createFont(Font.TRUETYPE_FONT, new File("Lato-Regular.ttf"));
			
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

		JLabel info = new JLabel("<html><div width=\"400px\" style=\"text-align:center;\">This installer will install CoursesPlus into Google Chrome. It will connect to the Internet to download the latest version of CoursesPlus, and then set up automatic updates. Anonymous analytics may be sent to us for statistical purposes. If an error occurs, you will be given the option to transmit information about the error to us so we can fix it.</div></html>", JLabel.CENTER);
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
				JOptionPane.showMessageDialog(null, "To continue, please close all open Chrome windows.", "Close Chrome windows", JOptionPane.WARNING_MESSAGE);
				getGraphicsConfiguration().getDevice().setFullScreenWindow(this);
				break;
				
			default:
				logger.warning("Unknown button pressed - " + text);
				break;
		}
	}
}