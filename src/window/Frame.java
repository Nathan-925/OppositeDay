package window;

import java.awt.Dimension;
import java.io.IOException;

import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;

public class Frame {
	
	public static Dimension defaultSize = new Dimension(800, 800);
	
	private static JFrame frame;
	
	public static void main(String args[]) throws IOException, InterruptedException {
		frame = new JFrame("Opposite Day");
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setResizable(false);
		JPanel panel = new JPanel();
		panel.setPreferredSize(defaultSize);
		panel.add(new JLabel(new ImageIcon(ImageIO.read(Frame.class.getClassLoader().getResourceAsStream("rec/Splash0.png")))));
		frame.getContentPane().add(panel);
		frame.pack();
		frame.setVisible(true);
		Thread.sleep(5000);
		panel.removeAll();
		panel.add(new JLabel(new ImageIcon(ImageIO.read(Frame.class.getClassLoader().getResourceAsStream("rec/Splash1.png")))));
		panel.revalidate();
		panel.repaint();
		Thread.sleep(5000);
		panel.removeAll();
		panel.add(new JLabel(new ImageIcon(ImageIO.read(Frame.class.getClassLoader().getResourceAsStream("rec/Splash2.png")))));
		panel.revalidate();
		panel.repaint();
		Thread.sleep(5000);
		
		frame.getContentPane().removeAll();
		frame.getContentPane().add(new BeatMap(6));
		frame.getContentPane().revalidate();
		
	}
	
	public static void next(JComponent c) {
		frame.getContentPane().add(c);
	}
}
