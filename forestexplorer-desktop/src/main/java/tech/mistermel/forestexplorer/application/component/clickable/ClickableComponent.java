package tech.mistermel.forestexplorer.application.component.clickable;

import tech.mistermel.forestexplorer.application.component.Component;

public abstract class ClickableComponent implements Component {

	protected int x, y, width, height;
	
	public ClickableComponent(int x, int y, int width, int height) {
		this.x = x;
		this.y = y;
		this.width = width;
		this.height = height;
	}
	
	public abstract void onClick();
	
	public void checkClick(int mouseX, int mouseY) {
		if(intersects(mouseX, mouseY)) {
			onClick();
		}
	}
	
	public boolean intersects(int mouseX, int mouseY) {
		return mouseX >= x && mouseY >= y && mouseX <= x + width && mouseY <= y + height;
	}
	
	public int getX() {
		return x;
	}
	
	public int getY() {
		return y;
	}
	
	public int getWidth() {
		return width;
	}
	
	public int getHeight() {
		return height;
	}
	
}
