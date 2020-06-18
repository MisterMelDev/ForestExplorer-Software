package tech.mistermel.forestexplorer.application.component.clickable;

import processing.core.PApplet;

public class Button extends ClickableComponent {

	private static final int MARGIN = 5;
	
	private PApplet applet;
	private Runnable callback;
	private String text;
	private int textSize;
	
	private boolean clicked;
	
	public Button(PApplet applet, int x, int y, String text, int textSize, int width) {
		super(x, y, width, textSize + MARGIN);
		
		this.applet = applet;
		this.text = text;
		this.textSize = textSize;
	}
	
	@Override
	public void draw() {
		if(!clicked && applet.mousePressed && intersects(applet.mouseX, applet.mouseY))
			this.clicked = true;
		
		applet.fill(255);
		applet.rect(x, y, width, height);
		
		applet.fill(clicked ? 255 : 0, 0, 0);
		applet.textSize(textSize);
		applet.text(text, x + (width - applet.textWidth(text)) / 2, y + (height + textSize - applet.textDescent()) / 2);
	}
	
	public void setCallback(Runnable callback) {
		this.callback = callback;
	}
	
	@Override
	public void onClick() {
		this.clicked = false;
		callback.run();
	}
	
}
