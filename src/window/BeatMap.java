package window;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Scanner;

import javax.swing.JLabel;
import javax.swing.JPanel;

import objects.Note;

public class BeatMap extends JPanel implements Runnable {

	public static HashMap<Integer, Integer> keyMap;
	
	private static final int HIT_WINDOW = 15,
							 CENTER_SIZE = 100,
							 NOTE_WIDTH = 20,
							 FINAL_MAP = 6;
	private static final Color WINDOW_COLOR = new Color(255, 0, 0, 100);
	
	static {
		keyMap = new HashMap<>();
		keyMap.put(KeyEvent.VK_NUMPAD6, 0);
		keyMap.put(KeyEvent.VK_NUMPAD3, 1);
		keyMap.put(KeyEvent.VK_NUMPAD2, 2);
		keyMap.put(KeyEvent.VK_NUMPAD1, 3);
		keyMap.put(KeyEvent.VK_NUMPAD4, 4);
		keyMap.put(KeyEvent.VK_NUMPAD7, 5);
		keyMap.put(KeyEvent.VK_NUMPAD8, 6);
		keyMap.put(KeyEvent.VK_NUMPAD9, 7);
		
	}
	
	private ArrayList<LinkedList<Note>> tracks;
	private HashMap<Integer, Boolean> keys;
	private LinkedList<Integer> held;
	private int score, numTracks, mapNum, minScore, scrollSpeed;
	private Font scoreFont;
	private boolean isOppositeDay;
	
	public BeatMap(int mapNum) {
		this.mapNum = mapNum;
		Scanner scan = new Scanner(getClass().getClassLoader().getResourceAsStream("rec/track"+mapNum+".txt"));
		numTracks = scan.nextInt();
		scrollSpeed = scan.nextInt();
		minScore = scan.nextInt();
		scan.nextLine();
		
		tracks = new ArrayList<>();
		for(int i = 0; i < numTracks; i++)
			tracks.add(new LinkedList<>());
		keys = new HashMap<>();
		held = new LinkedList<>();
		score = 0;
		try {
			scoreFont = Font.createFont(Font.TRUETYPE_FONT, getClass().getClassLoader().getResourceAsStream("rec/PixelFont.ttf")).deriveFont(30f);
		}
		catch(Exception e) {
			scoreFont = new Font(Font.SANS_SERIF, Font.BOLD, 30);
		}
		isOppositeDay = false;
		
		for(int i = 0; i < numTracks; i++) {
			Scanner sc = new Scanner(scan.nextLine());
			int offset = 0;
			while(sc.hasNextInt()) {
				int gap = sc.nextInt();
				offset += Math.abs(gap);
				tracks.get(i).add(new Note(offset, gap<0, new Color((int)(Math.random()*128), (int)(Math.random()*128), (int)(Math.random()*128))));
			}
		}
		
		setLayout(new BorderLayout());
		setBackground(Color.WHITE);
		addKeyListener(new KeyListener() {
			@Override
			public void keyTyped(KeyEvent e) {}

			@Override
			public void keyPressed(KeyEvent e) {
				keys.put(e.getKeyCode(), true);
			}

			@Override
			public void keyReleased(KeyEvent e) {
				keys.put(e.getKeyCode(), false);
			}
		});
		setFocusable(true);
		
		new Thread(this).start();
	}
	
	@Override
	public Dimension getPreferredSize() {
		return Frame.defaultSize;
	}
	
	@Override
	protected void paintComponent(Graphics g) {
		super.paintComponent(g);
		
		Graphics2D g2 = (Graphics2D)g;
		g2.setColor(isOppositeDay ? Color.BLACK : Color.WHITE);
		g2.fillRect(0, 0, getWidth(), getHeight());
		
		g2.setColor(isOppositeDay ? Color.WHITE : Color.BLACK);
		g2.setFont(scoreFont);
		g2.drawString(String.format("%010d", score), 10, 40);
		g2.drawOval(getWidth()/2-CENTER_SIZE/2, getHeight()/2-CENTER_SIZE/2, CENTER_SIZE, CENTER_SIZE);
		
		Iterator<LinkedList<Note>> it = tracks.iterator();
		for(int i = 0; i < numTracks; i++) {
			g2.setColor(isOppositeDay ? Color.WHITE : Color.BLACK);
			
			g2.drawLine(getWidth()/2, getHeight()/2+CENTER_SIZE/2, getWidth(), getHeight()/2+CENTER_SIZE/2);
			g2.drawLine(getWidth()/2, getHeight()/2-CENTER_SIZE/2, getWidth(), getHeight()/2-CENTER_SIZE/2);
			
			for(Note note: it.next()) {
				g2.setColor(note.getColor(isOppositeDay));
				g2.fillRect(getWidth()/2+CENTER_SIZE/2+note.getDistance(), getHeight()/2-CENTER_SIZE/2, NOTE_WIDTH, CENTER_SIZE);
			}
			
			g2.setColor(WINDOW_COLOR);
			g2.fillRect(getWidth()/2+CENTER_SIZE/2, getHeight()/2-CENTER_SIZE/2, HIT_WINDOW+NOTE_WIDTH, CENTER_SIZE);
			
			g2.rotate(Math.PI*2/numTracks, getWidth()/2, getHeight()/2);
		}
	}
	
	public boolean tick() {
		for(LinkedList<Note> track: tracks) {
			for(Note note: track)
				note.tick(scrollSpeed);
			if(!track.isEmpty() && track.get(0).getDistance() <= 0) {
				if(track.remove().flips())
					isOppositeDay = !isOppositeDay;
				score -= 100;
			}
		}
		
		int track = -1;
		for(int k: keys.keySet()) {
			if(keyMap.containsKey(k) && keys.get(k)) {
				if(!held.contains(k)) {
					track = keyMap.get(k);
					held.add(k);
				}
			}
			else {
				if(held.contains(k))
					held.remove(Integer.valueOf(k));
			}
		}
		if(track >= 0) {
			if(numTracks != keyMap.size())
				track /= keyMap.size()/numTracks;
			if(isOppositeDay)
				track = (track+numTracks/2)%numTracks;
			if(!tracks.get(track).isEmpty()) {
				if(tracks.get(track).get(0).getDistance() <= HIT_WINDOW+NOTE_WIDTH) {
					if(tracks.get(track).remove().flips())
						isOppositeDay = !isOppositeDay;
					score += 500;
				}
			}
		}
		
		for(LinkedList<Note> trackList: tracks)
			if(!trackList.isEmpty())
				return true;
		return false;
	}

	@Override
	public void run() {
		try {
			JPanel panel = new JPanel();
			panel.setBackground(Color.WHITE);
			panel.setLayout(new BorderLayout());
			JLabel label = new JLabel("Map "+mapNum, JLabel.CENTER);
			label.setFont(scoreFont);
			panel.add(label, BorderLayout.CENTER);
			add(panel, BorderLayout.CENTER);
			revalidate();
			repaint();
			Thread.sleep(3000);
			remove(panel);
			
			requestFocusInWindow();
			while(tick()) {
				repaint();
				Thread.sleep(5);
			}
			if(score >= minScore)
				label.setText("MAP COMPLETE "+score);
			else
				label.setText("MAP FAILED "+score);
			add(panel);
			repaint();
			Thread.sleep(3000);
			if(mapNum < FINAL_MAP)
				Frame.next(new BeatMap(mapNum+1));
		}
		catch(InterruptedException e) {
			e.printStackTrace();
		}
	}
	
}
