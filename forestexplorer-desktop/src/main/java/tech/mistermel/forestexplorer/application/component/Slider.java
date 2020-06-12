package tech.mistermel.forestexplorer.application.component;

import java.util.function.Consumer;

import processing.core.PApplet;

public class Slider extends ClickableComponent {

	private PApplet applet;
	
	private String text;
	private int xPos;
	private int percent;
	
	private Consumer<Integer> callback;
	
	private boolean waitForRelease;
	private boolean latestSent = true;
	
	public Slider(PApplet applet, String text, int x, int y, int length) {
		super(x, y, length, 10);
		
		this.text = text;
		this.applet = applet;
		
		this.xPos = length;
		this.percent = 100;
	}
	
	public void setCallback(Consumer<Integer> callback) {
		this.callback = callback;
	}

	@Override
	public void draw() {
		applet.stroke(255);
		applet.strokeWeight(10);
		applet.line(x, y, x + width, y);
		
		applet.noStroke();
		String percentText = percent + "%";
		applet.text(percentText, x + xPos - applet.textWidth(percentText) / 2, y + 25);
		applet.text(text, x + (width - applet.textWidth(text)) / 2, y - 15);
		
		applet.fill(255, 0, 0);
		applet.ellipse(x + xPos, y, 20, 20);
		
		if(!applet.mousePressed && waitForRelease && !latestSent) {
			latestSent = true;
			callback.accept(percent);
		}
	}
	
	@Override
	public boolean intersects(int mouseX, int mouseY) {
		return mouseX >= x && mouseY >= y - 10 && mouseX <= x + width && mouseY <= y + 10;
	}
	
	@Override
	public void onClick() {
		if(applet.mouseX < x || applet.mouseX > x + width) {
			return;
		}
		
		xPos = applet.mouseX - x;
		percent = (int) PApplet.map(xPos, 0, width, 0, 100);
		
		latestSent = false;
		if(!waitForRelease)
			callback.accept(percent);
	}
	
	public void setWaitForRelease(boolean waitForRelease) {
		this.waitForRelease = waitForRelease;
	}

	public int getPercent() {
		return percent;
	}
	
}
