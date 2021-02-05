package groupProject;

import java.awt.Color;
import java.util.ArrayList;
import java.util.Collections;

import javax.swing.BorderFactory;
import javax.swing.JButton;

/**
 * 
 * @author Nick Thompson, Erik Stroud
 * @version 1.0.0
 * 
 */

@SuppressWarnings("serial")
public class InputTile extends JButton {
	private int value = 0;
	private ConstraintTile topTile = null;
	private ConstraintTile leftTile = null;
	private ArrayList<Integer> origPossNumbs = new ArrayList<Integer>();
	private ArrayList<Integer> possNumbs = new ArrayList<Integer>();
	private ArrayList<Integer> usedNumbs = new ArrayList<Integer>();
	private int row, column;

	/**
	 * Class constructor that creates an InputTile object.
	 */
	public InputTile() {
		super();
		setBackground(Color.white);
		setBorder(BorderFactory.createLineBorder(Color.green, 2));
		setBorderPainted(false);
	}

	/**
	 * Resets the possible integers an InputTile can accept.
	 */
	public void resetPossInts() {
		possNumbs.clear();
		for (int i : origPossNumbs) {
			possNumbs.add(i);
		}
		Collections.sort(possNumbs);
	}

	/**
	 * Sets the position of the InputTile by setting its row and column class
	 * variables.
	 * 
	 * @param row    The row the InputTile is in.
	 * @param column The column the InputTile is in.
	 */
	public void setPosition(int row, int column) {
		this.row = row;
		this.column = column;
	}

	/**
	 * Gets the position of the InputTile.
	 * 
	 * @return
	 */
	public int[] getPosition() {
		return new int[] { row, column };
	}

	/**
	 * Sets the value of the InputTile.
	 * 
	 * @param inputtedValue
	 */
	public void setValue(int inputtedValue) {
		value = inputtedValue;
	}

	/**
	 * Sets the text of the InputTile. If the value is 0 (default), the InputTile is
	 * kept blank.
	 */
	public void setText() {
		if (value == 0) {
			setText("");
		} else {
			setText(String.valueOf(value));
		}
	}

	/**
	 * Gets the value of the InputTile.
	 * 
	 * @return
	 */
	public int getValue() {
		return value;
	}

	/**
	 * Sets the ConstraintTile that is constraining the InputTile from above.
	 * 
	 * @param t
	 */
	public void setTopTile(ConstraintTile t) {
		topTile = t;
	}

	/**
	 * Sets the ConstraintTile that is constraining the InputTile from the left.
	 * 
	 * @param t
	 */
	public void setLeftTile(ConstraintTile t) {
		leftTile = t;
	}

	/**
	 * Creates an ArrayList of possible integers that can be typed into the
	 * InputTile.
	 * 
	 * @param vertInts The list of integers that can be used when going vertically
	 * @param horInts  The list of integers that can be used when going horizontally
	 */
	public void setPossInts(ArrayList<ArrayList<Integer>> vertInts, ArrayList<ArrayList<Integer>> horInts) {
		ArrayList<Integer> possNumbsVert = new ArrayList<Integer>();
		ArrayList<Integer> possNumbsHor = new ArrayList<Integer>();

		// Getting possible vertical integers
		for (int i = 0; i < vertInts.size(); i++) {
			for (int j = 0; j < vertInts.get(i).size(); j++) {
				if (!possNumbsVert.contains(vertInts.get(i).get(j))) {
					possNumbsVert.add(vertInts.get(i).get(j));
				}
			}
		}

		// Getting possible horizontal integers
		for (int i = 0; i < horInts.size(); i++) {
			for (int j = 0; j < horInts.get(i).size(); j++) {
				if (!possNumbsHor.contains(horInts.get(i).get(j))) {
					possNumbsHor.add(horInts.get(i).get(j));
				}
			}
		}

		// Getting the common numbers between vertical and horizontal lists
		for (int i = 0; i < possNumbsHor.size(); i++) {
			for (int j = 0; j < possNumbsVert.size(); j++) {
				if (possNumbsHor.get(i) == possNumbsVert.get(j)) {
					possNumbs.add(possNumbsHor.get(i));
				}
			}
		}

		// Sorts the possible numbers and adds them to a copy for future use
		Collections.sort(possNumbs);
		for (int i : possNumbs) {
			origPossNumbs.add(i);
		}
		Collections.sort(origPossNumbs);
	}

	/**
	 * Removes the possible integers from the possNumbs class variable and adds it
	 * to the usedNumbs class variable.
	 * 
	 * @param rv The integers that is being moved from possible to used
	 */
	public void removePossInt(int rv) {
		if (possNumbs.contains(rv)) {
			possNumbs.remove(possNumbs.indexOf(rv));
		}
		this.usedNumbs.add(rv);
		Collections.sort(possNumbs);
	}

	/**
	 * Gets a String of integers that are possible for the InputTile.
	 * 
	 * @return String version of possNumbs
	 */
	public String getPossIntString() {
		return String.valueOf(possNumbs);
	}

	/**
	 * Adds the integer to the possNumbs class variable and removes it from the
	 * usedNumbs class variable.
	 * 
	 * @param av The integer being added back to possNumbs.
	 */
	public void addPossInt(int av) {
		if (usedNumbs.contains(av)) {
			usedNumbs.remove(usedNumbs.indexOf(av));
		}

		if (origPossNumbs.contains(av)) {
			this.possNumbs.add(av);
		}
		Collections.sort(possNumbs);
	}

	/**
	 * Gets the numbers that are possible for the InputTile.
	 * 
	 * @return
	 */
	public ArrayList<Integer> getPossInts() {
		return possNumbs;
	}

	/**
	 * Gets the ConstraintTile that is constraining the InputTile from the left.
	 * 
	 * @return
	 */
	public ConstraintTile getLeftTile() {
		return leftTile;
	}

	/**
	 * Gets the ConstraintTile that is constraining the InputTile from above.
	 * 
	 * @return
	 */
	public ConstraintTile getTopTile() {
		return topTile;
	}
}
