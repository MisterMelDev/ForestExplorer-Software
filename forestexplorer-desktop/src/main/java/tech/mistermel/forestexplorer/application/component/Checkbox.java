package tech.mistermel.forestexplorer.application.component;

import java.util.function.Consumer;

import processing.core.PApplet;
import processing.core.PConstants;

public class Checkbox extends ClickableComponent {

	private static final int BOX_SIZE = 16, CHECKED_BOX_MARGIN = 2, TEXT_MARGIN = 5, TEXT_ALIGN_VALUE = -4, FONT_SIZE = 14;
	
	private PApplet applet;
	private Consumer<Boolean> callback;
	private String text;
	
	private boolean checked;
	
	public Checkbox(PApplet applet, String text, int x, int y) {
		super(x, y, BOX_SIZE, BOX_SIZE);
		
		/* We need to set the width outside of the super constructor,
		 * because the text size needs to be set first for correct results. */
		applet.textSize(FONT_SIZE);
		this.width = (int) (BOX_SIZE + TEXT_MARGIN + applet.textWidth(text));
		
		this.applet = applet;
		this.text = text;
	}

	public void draw() {
		applet.fill(255);
		applet.rect(this.getX(), this.getY(), BOX_SIZE, BOX_SIZE);
		
		if(checked) {
			applet.fill(100, 255, 0);
			applet.noStroke();
			applet.rect(this.getX() + CHECKED_BOX_MARGIN, this.getY() + CHECKED_BOX_MARGIN, BOX_SIZE - CHECKED_BOX_MARGIN * 2, BOX_SIZE - CHECKED_BOX_MARGIN * 2);
			applet.fill(255);
		}
		
		applet.textSize(FONT_SIZE);
		applet.textAlign(PConstants.LEFT, PConstants.BASELINE);
		applet.text(text, this.getX() + BOX_SIZE + TEXT_MARGIN, this.getY() + BOX_SIZE + TEXT_ALIGN_VALUE);
	}
	
	public void setCallback(Consumer<Boolean> callback) {
		this.callback = callback;
	}
	
	@Override
	public void onClick() {
		checked = !checked;
		
		if(callback != null) {
			callback.accept(checked);
		}
	}
	
	public boolean isChecked() {
		return checked;
	}
	
}
