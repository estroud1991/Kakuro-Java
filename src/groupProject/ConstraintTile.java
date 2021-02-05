package groupProject;

import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;

import javax.swing.JPanel;

/**
 * 
 * @author Nick Thompson, Erik Stroud
 * @version 1.0.0
 * 
 */

@SuppressWarnings("serial")
public class ConstraintTile extends JPanel {
	private boolean createLine = false;
	private int bottomLeftValue;
	private int topRightValue;
	private int horDist = 0;
	private int vertDist = 0;
	private int row;
	private int column;

	private static Color backgroundColor = new Color(23, 22, 23);
	private static Color foregroundColor = Color.white;

	/**
	 * Class constructor that creates a ConstraintTile object. Accepts a boolean
	 * flag that tells the ConstraintTile object to be fully blank or contain values
	 * separated by a diagonal line.
	 * 
	 * @param blankFlag
	 */
	public ConstraintTile(boolean blankFlag) {
		super();
		setBackground(backgroundColor);
		if (!blankFlag) {
			createLine = true;
			setForeground(foregroundColor);
			repaint();
		}
	}

	/**
	 * Sets the position of the ConstraintTile by setting its row and column class
	 * variables.
	 * 
	 * @param row    The row the ConstraintTile is in.
	 * @param column The column the ConstraintTile is in.
	 */
	public void setPosition(int row, int column) {
		this.row = row;
		this.column = column;
	}

	/**
	 * Paints the ConstraintTile with its backgroundColor and paints a diagonal
	 * white line if the ConstrantTile is not blank. Calls the drawNumbers method.
	 */
	public void paintComponent(Graphics g) {
		if (createLine) {
			super.paintComponent(g);
			g.setColor(Color.white);
			g.drawLine(0, 0, getWidth(), getHeight());
			drawNumbers(g);
		} else {
			super.paintComponent(g);
			g.setColor(backgroundColor);
			g.drawLine(0, 0, getWidth() / 2, getHeight() / 2);
		}
	}

	/**
	 * Sets the font and draws the String on the ConstraintTile.
	 * 
	 * @param g
	 */
	public void drawNumbers(Graphics g) {
		g.setFont(new Font("Arial", Font.PLAIN, 26));
		String bottomLeftString;
		String topRightString;
		if (bottomLeftValue < 10) {
			bottomLeftString = " " + String.valueOf(bottomLeftValue);
		} else {
			bottomLeftString = String.valueOf(bottomLeftValue);
		}
		if (topRightValue < 10) {
			topRightString = String.valueOf(topRightValue) + " ";
		} else {
			topRightString = String.valueOf(topRightValue);
		}

		FontMetrics fm = g.getFontMetrics();
		int stringAccent = fm.getAscent();

		// Bottom left
		if (!bottomLeftString.equals(" 0")) {
			int bLstringWidth = fm.stringWidth(bottomLeftString);
			int xBLCoordinate = (getWidth() - bLstringWidth) / getWidth();
			int yBLCoordinate = getHeight() - fm.getHeight() + stringAccent;
			g.drawString(bottomLeftString, xBLCoordinate, yBLCoordinate);
		}

		// Top right
		if (!topRightString.equals("0 ")) {
			int tRstringWidth = fm.stringWidth(topRightString);
			int xTRCoordinate = getWidth() - tRstringWidth;
			int yTRCoordinate = getHeight() - fm.getHeight();
			g.drawString(topRightString, xTRCoordinate, yTRCoordinate);
		}
	}

	/**
	 * Sets the values of the ConstraintTile.
	 * 
	 * @param s
	 */
	public void setValues(String s) {
		String[] text = s.split("/");
		bottomLeftValue = Integer.valueOf(text[0]);
		topRightValue = Integer.valueOf(text[1]);
	}

	/**
	 * Gets the top right value of the ConstraintTile.
	 * 
	 * @return topRightValue
	 */
	public int getTopRightValue() {
		return topRightValue;
	}

	/**
	 * Gets the bottom left value of the ConstraintTile.
	 * 
	 * @return bottomLeftValue
	 */
	public int getBottomLeftValue() {
		return bottomLeftValue;
	}

	/**
	 * Gets the vertical distance the ConstraintTile covers.
	 * 
	 * @return vertDist
	 */
	public int getVertDist() {
		return vertDist;
	}

	/**
	 * Gets the horizontal distance the ConstraintTile covers.
	 * 
	 * @return horDist
	 */
	public int getHorDist() {
		return horDist;
	}

	/**
	 * Increments the vertDist class variable.
	 */
	public void incrementVertDist() {
		vertDist++;
	}

	/**
	 * Increments the horDist class variable.
	 */
	public void incrementHorDist() {
		horDist++;
	}

	/**
	 * Overrides the built-in toString() method to return a String consisting of
	 * "bottomLeftValue/topRightValue."
	 */
	public String toString() {
		return bottomLeftValue + "/" + topRightValue;
	}

}