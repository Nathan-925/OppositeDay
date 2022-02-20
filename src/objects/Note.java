package objects;

import java.awt.Color;

public class Note {

	private int distance;
	private boolean flips;
	private Color color, oppositeColor;
	
	public Note(int distance, boolean flips, Color color) {
		this.distance = distance;
		this.flips = flips;
		this.color = color;
		oppositeColor = new Color(255-color.getRed(), 255-color.getGreen(), 255-color.getBlue());
	}
	
	public void tick(int speed) {
		distance -= speed;
	}
	
	public int getDistance() {
		return distance;
	}
	
	public boolean flips() {
		return flips;
	}
	
	public Color getColor(boolean isOppositeDay) {
		return isOppositeDay ? oppositeColor : color;
	}
	
}
