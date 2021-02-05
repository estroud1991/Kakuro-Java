package groupProject;

import java.awt.Color;
import java.awt.Container;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;

import javax.swing.ImageIcon;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPopupMenu;
import javax.swing.KeyStroke;
import javax.swing.SwingUtilities;
import javax.swing.filechooser.FileNameExtensionFilter;

/**
 * 
 * @author Nick Thompson, Erik Stroud
 * @version 1.0.0
 * 
 */

@SuppressWarnings("serial")
public class Board extends JFrame {
	private JMenuBar menuBar;
	private JMenu gameMenu;
	private JMenu fileMenu;
	private Object[][] tilesMatrix;
	private int boardDimensions;
	private String loadedFile;

	private MouseListenerController mLctrl;
	private WindowListenerController wLctrl;
	private Container cntnr;
	private MenuController menuctrl;

	public static void main(String[] args) {
		Board playBoard = new Board();
		playBoard.setVisible(true);
	}

	private void resize(int dimensions) {
		cntnr.removeAll();
		boardDimensions = dimensions;
		tilesMatrix = new Object[boardDimensions][boardDimensions];
		cntnr.setLayout(new GridLayout(boardDimensions, boardDimensions, 2, 2));
		buildMenu();
		repaint();
	}

	/**
	 * Class constructor that creates the Board object.
	 */

	public Board() {
		setTitle("Kakuro");
		setSize(650, 650);
		setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);

		cntnr = getContentPane();
		cntnr.setBackground(Color.gray);
		setResizable(false);

		buildMenu();

		mLctrl = new MouseListenerController();
		wLctrl = new WindowListenerController();
		menuctrl = new MenuController();

		addWindowListener(wLctrl);
		emptyBoard(1);
	}

	private void emptyBoard(int dimensions) {
		resize(dimensions);
		InputTile iTile = new InputTile();
		iTile.setIcon(new ImageIcon("Resources/menuscreen.gif"));
		iTile.setBackground(new Color(23, 22, 23));
		iTile.setText("Click to read in a board");
		iTile.setHorizontalTextPosition(iTile.getWidth() / 2);
		iTile.setFocusPainted(false);
		iTile.setFont(new Font("Monospaced", Font.PLAIN, 32));
		iTile.addMouseListener(mLctrl);
		cntnr.add(iTile);
		tilesMatrix[0][0] = iTile;
		iTile.addActionListener(menuctrl);

	}

	private void loadBoard(String filename) {
		String fileString = fileReader(filename);
		String[] fileStringRows = fileString.replace("\r", "").split("\n");
		resize(fileStringRows.length);

		String[][] fileStringMatrix = new String[fileStringRows.length][];
		for (int i = 0; i < fileStringRows.length; i++) {
			fileStringMatrix[i] = fileStringRows[i].split(",");
		}

		for (int i = 0; i < boardDimensions; i++) {
			for (int j = 0; j < boardDimensions; j++) {
				if (fileStringMatrix[i][j].equals("E")) {
					InputTile iTile = new InputTile();
					iTile.setValue(0);
					iTile.setText();
					iTile.setFont(new Font("Arial", Font.PLAIN, 36));
					iTile.addMouseListener(mLctrl);
					iTile.setPosition(i, j);
					cntnr.add(iTile);
					tilesMatrix[i][j] = iTile;
				} else if (fileStringMatrix[i][j].matches("\\d")) {
					InputTile iTile = new InputTile();
					iTile.setValue(Integer.valueOf(fileStringMatrix[i][j]));
					iTile.setText();
					iTile.setFont(new Font("Arial", Font.PLAIN, 36));
					iTile.addMouseListener(mLctrl);
					iTile.setPosition(i, j);
					cntnr.add(iTile);
					tilesMatrix[i][j] = iTile;
				} else if (fileStringMatrix[i][j].equals("B")) {
					ConstraintTile cTile = new ConstraintTile(true);
					cTile.setPosition(i, j);
					cntnr.add(cTile);
					tilesMatrix[i][j] = cTile;
				} else {
					ConstraintTile cTile = new ConstraintTile(false);
					cTile.setPosition(i, j);
					cntnr.add(cTile);
					cTile.setValues(fileStringMatrix[i][j]);
					tilesMatrix[i][j] = cTile;
				}
			}
		}

		ConstraintTile cTile = null;
		InputTile iTile = null;

		// Sets the InputTile's left constraining ConstraintTile
		for (int i = 0; i < boardDimensions; i++) {
			for (int j = 0; j < boardDimensions; j++) {
				if (tilesMatrix[i][j] instanceof ConstraintTile) {
					cTile = (ConstraintTile) tilesMatrix[i][j];
				} else if (tilesMatrix[i][j] instanceof InputTile) {
					iTile = (InputTile) tilesMatrix[i][j];
					iTile.setLeftTile(cTile);
					cTile.incrementHorDist();
				}
			}
		}

		// Sets the InputTile's top constraining ConstraintTile
		for (int j = 0; j < boardDimensions; j++) {
			for (int i = 0; i < boardDimensions; i++) {
				if (tilesMatrix[i][j] instanceof ConstraintTile) {
					cTile = (ConstraintTile) tilesMatrix[i][j];
				} else if (tilesMatrix[i][j] instanceof InputTile) {
					iTile = (InputTile) tilesMatrix[i][j];
					iTile.setTopTile(cTile);
					cTile.incrementVertDist();

				}
			}
		}

		// Creates an ArrayList from 1-9 (inclusive)
		ArrayList<Integer> possibleNumbers = new ArrayList<Integer>();
		for (int i = 1; i < 10; i++) {
			possibleNumbers.add(i);
		}

		// Gets possible numbers for the InputTile
		for (int i = 0; i < boardDimensions; i++) {
			for (int j = 0; j < boardDimensions; j++) {
				if (tilesMatrix[i][j] instanceof InputTile) {
					iTile = (InputTile) tilesMatrix[i][j];

					// Creates nested ArrayLists of possible vertical and horizontal values for the
					// InputTile
					ArrayList<ArrayList<Integer>> iTilePossVert = calculateSums(possibleNumbers,
							iTile.getTopTile().getBottomLeftValue(), new ArrayList<Integer>(),
							iTile.getTopTile().getVertDist(), new ArrayList<ArrayList<Integer>>());
					ArrayList<ArrayList<Integer>> iTilePossHor = calculateSums(possibleNumbers,
							iTile.getLeftTile().getTopRightValue(), new ArrayList<Integer>(),
							iTile.getLeftTile().getHorDist(), new ArrayList<ArrayList<Integer>>());

					// Uses those 2 lists to set the possible integer values the InputTile
					iTile.setPossInts(iTilePossVert, iTilePossHor);
					iTilePossVert.clear();
					iTilePossHor.clear();
				}
			}
		}

		revalidate();
	}

	private void buildMenu() {
		menuBar = new JMenuBar();
		fileMenu = new JMenu("File");
		gameMenu = new JMenu("Game");

		fileMenu.setMnemonic(KeyEvent.VK_F);
		gameMenu.setMnemonic(KeyEvent.VK_G);

		JMenuItem menuItem = new JMenuItem("New");
		KeyStroke shortcut = KeyStroke.getKeyStroke("control N");
		menuItem.addActionListener(menuctrl);
		menuItem.setMnemonic(KeyEvent.VK_N);
		menuItem.setAccelerator(shortcut);
		fileMenu.add(menuItem);

		menuItem = new JMenuItem("Open");
		shortcut = KeyStroke.getKeyStroke("control O");
		menuItem.addActionListener(menuctrl);
		menuItem.setMnemonic(KeyEvent.VK_O);
		menuItem.setAccelerator(shortcut);
		fileMenu.add(menuItem);

		menuItem = new JMenuItem("Save");
		shortcut = KeyStroke.getKeyStroke("control S");
		menuItem.addActionListener(menuctrl);
		menuItem.setMnemonic(KeyEvent.VK_S);
		menuItem.setAccelerator(shortcut);
		fileMenu.add(menuItem);

		menuItem = new JMenuItem("Save as");
		shortcut = KeyStroke.getKeyStroke(KeyEvent.VK_S, 19);
		menuItem.addActionListener(menuctrl);
		menuItem.setMnemonic(KeyEvent.VK_A);
		menuItem.setAccelerator(shortcut);
		fileMenu.add(menuItem);

		menuItem = new JMenuItem("Exit");
		shortcut = KeyStroke.getKeyStroke("control E");
		menuItem.addActionListener(menuctrl);
		menuItem.setMnemonic(KeyEvent.VK_E);
		menuItem.setAccelerator(shortcut);
		fileMenu.add(menuItem);

		menuItem = new JMenuItem("Reset");
		shortcut = KeyStroke.getKeyStroke("control R");
		menuItem.addActionListener(menuctrl);
		menuItem.setMnemonic(KeyEvent.VK_R);
		menuItem.setAccelerator(shortcut);
		gameMenu.add(menuItem);

		menuItem = new JMenuItem("Exit");
		shortcut = KeyStroke.getKeyStroke("control E");
		menuItem.addActionListener(menuctrl);
		menuItem.setMnemonic(KeyEvent.VK_E);
		menuItem.setAccelerator(shortcut);
		gameMenu.add(menuItem);

		menuBar.add(fileMenu);
		menuBar.add(gameMenu);
		setJMenuBar(menuBar);
	}

	private String fileReader(String filename) {
		String fileData = "";
		try {
			fileData = new String(Files.readAllBytes(Paths.get(filename)));
		} catch (IOException ie) {
			ie.printStackTrace();
		}
		return fileData;
	}

	private ArrayList<ArrayList<Integer>> calculateSums(ArrayList<Integer> possibleNumbers, int target,
			ArrayList<Integer> partial, int groupLen, ArrayList<ArrayList<Integer>> possibleSums) {
		int sum = 0;
		for (int i : partial) {
			sum += i;
		}
		if (sum == target && partial.size() == groupLen) {
			possibleSums.add(partial);
		}
		for (int i = 0; i < possibleNumbers.size(); i++) {
			ArrayList<Integer> remaining = new ArrayList<Integer>();
			int number = possibleNumbers.get(i);
			for (int j = i + 1; j < possibleNumbers.size(); j++)
				remaining.add(possibleNumbers.get(j));
			ArrayList<Integer> partial_rec = new ArrayList<Integer>(partial);
			partial_rec.add(number);
			calculateSums(remaining, target, partial_rec, groupLen, possibleSums);
		}

		return possibleSums;
	}

	private String[][] formatOutput() {
		String[][] fileStringMatrix = new String[boardDimensions][boardDimensions];
		for (int i = 0; i < boardDimensions; i++) {
			for (int j = 0; j < boardDimensions; j++) {
				if (tilesMatrix[i][j] instanceof InputTile) {
					if (((InputTile) tilesMatrix[i][j]).getValue() == 0) {
						fileStringMatrix[i][j] = "E";
					} else {
						fileStringMatrix[i][j] = String.valueOf(((InputTile) tilesMatrix[i][j]).getValue());
					}
				} else {
					if (((ConstraintTile) tilesMatrix[i][j]).getBottomLeftValue() == 0
							&& ((ConstraintTile) tilesMatrix[i][j]).getTopRightValue() == 0) {
						fileStringMatrix[i][j] = "B";
					} else {
						fileStringMatrix[i][j] = ((ConstraintTile) tilesMatrix[i][j]).toString();
					}
				}
			}
		}
		return fileStringMatrix;
	}

	private boolean promptForSave() {
		String[][] fileStringMatrix = formatOutput();
		if (loadedFile == null) {
			return false;
		}
		String currentFileString = fileReader(loadedFile);
		String[] currentFileStringRows = currentFileString.replace("\r", "").split("\n");
		String[][] currentFileStringMatrix = new String[currentFileStringRows.length][];
		for (int i = 0; i < currentFileStringRows.length; i++) {
			currentFileStringMatrix[i] = currentFileStringRows[i].split(",");
		}
		for (int i = 0; i < boardDimensions; i++) {
			for (int j = 0; j < boardDimensions; j++) {
				if (!fileStringMatrix[i][j].equals(currentFileStringMatrix[i][j])) {
					return true;
				}
			}
		}
		return false;
	}

	private class WindowListenerController implements WindowListener {
		// Ask Narayan about documenting
		@Override
		public void windowActivated(WindowEvent we) {
			// TODO Auto-generated method stub

		}

		@Override
		public void windowClosed(WindowEvent we) {
			// TODO Auto-generated method stub

		}

		@Override
		public void windowClosing(WindowEvent we) {
			// TODO Auto-generated method stub
			if (promptForSave()) {
				int selectedOption = JOptionPane.showConfirmDialog(null, "Save before exiting?", "Choose",
						JOptionPane.YES_NO_OPTION);
				if (selectedOption == JOptionPane.YES_OPTION) {
					String[][] fileStringMatrix = formatOutput();
					FileWriter fw = null;
					try {
						fw = new FileWriter(loadedFile, false);
						BufferedWriter bw = new BufferedWriter(fw);
						PrintWriter out = new PrintWriter(bw);
						for (int i = 0; i < boardDimensions; i++) {
							for (int j = 0; j < boardDimensions; j++) {
								if (j == boardDimensions - 1) {
									out.write(fileStringMatrix[i][j] + System.getProperty("line.separator"));
								} else {
									out.write(fileStringMatrix[i][j] + ",");
								}
							}
						}
						out.close();
					} catch (IOException e) {
						e.printStackTrace();
					}
					System.exit(0);
				} else {
					System.exit(0);
				}
			} else {
				int selectedOption = JOptionPane.showConfirmDialog(null, "Are you sure you want to exit?", "Choose",
						JOptionPane.YES_NO_OPTION);
				if (selectedOption == JOptionPane.YES_OPTION) {
					System.exit(0);
				}
			}

		}

		@Override
		public void windowDeactivated(WindowEvent we) {
			// TODO Auto-generated method stub

		}

		@Override
		public void windowDeiconified(WindowEvent we) {
			// TODO Auto-generated method stub

		}

		@Override
		public void windowIconified(WindowEvent we) {
			// TODO Auto-generated method stub

		}

		@Override
		public void windowOpened(WindowEvent we) {
			// TODO Auto-generated method stub

		}

	}

	private class MenuController implements ActionListener {
		@Override
		public void actionPerformed(ActionEvent ae) {
			// TODO Auto-generated method stub
			Object clickedObject = ae.getSource();
			if (clickedObject instanceof JMenuItem) {
				JMenuItem clickedMenuItem = (JMenuItem) clickedObject;
				if (clickedMenuItem.getText() == "Reset" || clickedMenuItem.getText() == "New") {
					int selectedOption = JOptionPane.showConfirmDialog(null,
							"Are you sure you want to reset the board?", "Choose", JOptionPane.YES_NO_OPTION);
					if (selectedOption == JOptionPane.YES_OPTION)
						for (int i = 0; i < boardDimensions; i++) {
							for (int j = 0; j < boardDimensions; j++) {
								if (tilesMatrix[i][j] instanceof InputTile) {
									((InputTile) tilesMatrix[i][j]).setValue(0);
									((InputTile) tilesMatrix[i][j]).setText();
									((InputTile) tilesMatrix[i][j]).resetPossInts();
								}
							}
						}

				} else if (clickedMenuItem.getText() == "Open") {
					JFileChooser fileChooser = new JFileChooser(
							new File(System.getProperty("user.dir") + "/Resources"));
					FileNameExtensionFilter filter = new FileNameExtensionFilter("TEXT FILES", "txt", "text");
					fileChooser.setFileFilter(filter);
					int returnValue = fileChooser.showOpenDialog(null);
					if (returnValue == JFileChooser.APPROVE_OPTION) {
						File selectedFile = fileChooser.getSelectedFile();
						loadedFile = "Resources/" + selectedFile.getName();
						loadBoard(loadedFile);
					}

				} else if (clickedMenuItem.getText() == "Save") {
					String[][] fileStringMatrix = formatOutput();
					FileWriter fw = null;
					try {
						fw = new FileWriter(loadedFile, false);
						BufferedWriter bw = new BufferedWriter(fw);
						PrintWriter out = new PrintWriter(bw);
						for (int i = 0; i < boardDimensions; i++) {
							for (int j = 0; j < boardDimensions; j++) {
								if (j == boardDimensions - 1) {
									out.write(fileStringMatrix[i][j] + System.getProperty("line.separator"));
								} else {
									out.write(fileStringMatrix[i][j] + ",");
								}
							}
						}
						out.close();
					} catch (IOException e) {
						e.printStackTrace();
					}

				} else if (clickedMenuItem.getText() == "Save as") {
					JFileChooser fileChooser = new JFileChooser(
							new File(System.getProperty("user.dir") + "/Resources"));
					FileNameExtensionFilter filter = new FileNameExtensionFilter("TEXT FILES", "txt", "text");
					fileChooser.setFileFilter(filter);
					int returnValue = fileChooser.showSaveDialog(null);
					if (returnValue == JFileChooser.APPROVE_OPTION) {
						File selectedFile = fileChooser.getSelectedFile();
						String selectedFilePath = selectedFile.getPath();
						if (!selectedFilePath.endsWith(".txt")) {
							selectedFile = new File(selectedFilePath + ".txt");
						}
						loadedFile = "Resources/" + selectedFile.getName();
					}
					String[][] fileStringMatrix = formatOutput();
					FileWriter fw = null;
					try {
						fw = new FileWriter(loadedFile, false);
						BufferedWriter bw = new BufferedWriter(fw);
						PrintWriter out = new PrintWriter(bw);
						for (int i = 0; i < boardDimensions; i++) {
							for (int j = 0; j < boardDimensions; j++) {
								if (j == boardDimensions - 1) {
									out.write(fileStringMatrix[i][j] + System.getProperty("line.separator"));
								} else {
									out.write(fileStringMatrix[i][j] + ",");
								}
							}
						}
						out.close();
					} catch (IOException e) {
						e.printStackTrace();
					}

				} else if (clickedMenuItem.getText() == "Exit") {
					if (promptForSave()) {
						int selectedOption = JOptionPane.showConfirmDialog(null, "Save before exiting?", "Choose",
								JOptionPane.YES_NO_OPTION);
						if (selectedOption == JOptionPane.YES_OPTION) {
							String[][] fileStringMatrix = formatOutput();
							FileWriter fw = null;
							try {
								fw = new FileWriter(loadedFile, false);
								BufferedWriter bw = new BufferedWriter(fw);
								PrintWriter out = new PrintWriter(bw);
								for (int i = 0; i < boardDimensions; i++) {
									for (int j = 0; j < boardDimensions; j++) {
										if (j == boardDimensions - 1) {
											out.write(fileStringMatrix[i][j] + System.getProperty("line.separator"));
										} else {
											out.write(fileStringMatrix[i][j] + ",");
										}
									}
								}
								out.close();
							} catch (IOException e) {
								e.printStackTrace();
							}
							System.exit(0);
						} else {
							System.exit(0);
						}
					} else {
						int selectedOption = JOptionPane.showConfirmDialog(null, "Are you sure you want to exit?",
								"Choose", JOptionPane.YES_NO_OPTION);
						if (selectedOption == JOptionPane.YES_OPTION) {
							System.exit(0);
						}
					}
				}
			} else if (clickedObject instanceof InputTile) {
				JFileChooser fileChooser = new JFileChooser(new File(System.getProperty("user.dir") + "/Resources"));
				FileNameExtensionFilter filter = new FileNameExtensionFilter("TEXT FILES", "txt", "text");
				fileChooser.setFileFilter(filter);
				int returnValue = fileChooser.showOpenDialog(null);
				if (returnValue == JFileChooser.APPROVE_OPTION) {
					File selectedFile = fileChooser.getSelectedFile();
					loadedFile = "Resources/" + selectedFile.getName();
					loadBoard(loadedFile);
				}
			}

		}
	}

	private class MouseListenerController implements MouseListener {
		private KeyListenerController kLctrl = new KeyListenerController();
		private InputTile previouslyClickedObject = null;
		private InputTile clickedTile;

		@Override
		public void mouseReleased(MouseEvent me) {
			// TODO Auto-generated method stub
		}

		@Override
		public void mouseClicked(MouseEvent me) {
			// TODO Auto-generated method stub
			Object clickedObject = me.getSource();
			clickedTile = (InputTile) clickedObject;
			if (SwingUtilities.isLeftMouseButton(me)) {
				if (previouslyClickedObject == null) {
					clickedTile.setBorderPainted(true);
					clickedTile.addKeyListener(kLctrl);
					previouslyClickedObject = clickedTile;
				} else if (previouslyClickedObject != clickedTile) {
					previouslyClickedObject.setBorderPainted(false);
					previouslyClickedObject.removeKeyListener(kLctrl);
					clickedTile.setBorderPainted(true);
					clickedTile.addKeyListener(kLctrl);
					previouslyClickedObject = clickedTile;
				}
			}

			else if (SwingUtilities.isRightMouseButton(me)) {
				JPopupMenu menu = new JPopupMenu();
				if (clickedTile.getPossInts().isEmpty()) {
					menu.add(new JMenuItem("No hint available"));
				} else {
					menu.add(new JMenuItem(String.valueOf(clickedTile.getPossInts())));
				}
				clickedTile.add(menu);
				menu.show(me.getComponent(), me.getX(), me.getY());
			}

		}

		@Override
		public void mouseEntered(MouseEvent me) {
			// TODO Auto-generated method stub
		}

		@Override
		public void mouseExited(MouseEvent me) {
			// TODO Auto-generated method stub

		}

		@Override
		public void mousePressed(MouseEvent me) {
			// TODO Auto-generated method stub
		}
	}

	private class KeyListenerController implements KeyListener {
		@Override
		public void keyPressed(KeyEvent ke) {

			// TODO Auto-generated method stub
			Object keyPressedObject = ke.getSource();
			InputTile typedInTile = (InputTile) keyPressedObject;
			int validIntChecker = 0;
			try {
				validIntChecker = Integer.valueOf(String.valueOf(ke.getKeyChar()));
			} catch (NumberFormatException e) {
			}
			if (ke.getKeyChar() == KeyEvent.VK_BACK_SPACE || ke.getKeyChar() == KeyEvent.VK_DELETE) {
				addBackToPossible(typedInTile);
				typedInTile.setText("");
				typedInTile.setValue(0);
			} else if (typedInTile.getPossInts().contains(validIntChecker)) {
				if (typedInTile.getValue() != 0) {
					addBackToPossible(typedInTile);

				}
				removeFromPossible(ke, typedInTile);
				typedInTile.setText(String.valueOf(ke.getKeyChar()));

			} else if (ke.isControlDown() || ke.isShiftDown()) {

			} else {
				Toolkit.getDefaultToolkit().beep();
			}

		}

		public void removeFromPossible(KeyEvent ke, InputTile typedInTile) {
			// Only needs to accept possible values
			int[] position = typedInTile.getPosition();
			int row = position[0];
			int column = position[1];
			typedInTile.setValue(Integer.valueOf(String.valueOf(ke.getKeyChar())));

			// Going to the left
			while (tilesMatrix[row][column] instanceof InputTile && column >= 0) {
				((InputTile) tilesMatrix[row][column]).removePossInt(Integer.valueOf(String.valueOf(ke.getKeyChar())));
				column--;
			}

			// Going to the right
			row = position[0];
			column = position[1];
			while (tilesMatrix[row][column] instanceof InputTile && column < boardDimensions) {
				((InputTile) tilesMatrix[row][column]).removePossInt(Integer.valueOf(String.valueOf(ke.getKeyChar())));
				column++;
				if (column == boardDimensions) {
					break;
				}
			}

			// Going up
			row = position[0];
			column = position[1];
			while (tilesMatrix[row][column] instanceof InputTile && row >= 0) {
				((InputTile) tilesMatrix[row][column]).removePossInt(Integer.valueOf(String.valueOf(ke.getKeyChar())));
				row--;

			}

			// Going down
			row = position[0];
			column = position[1];
			while (tilesMatrix[row][column] instanceof InputTile && row < boardDimensions) {
				((InputTile) tilesMatrix[row][column]).removePossInt(Integer.valueOf(String.valueOf(ke.getKeyChar())));
				row++;

				if (row == boardDimensions) {
					break;
				}
			}
		}

		public void addBackToPossible(InputTile typedInTile) {
			int[] position = typedInTile.getPosition();
			int row = position[0];
			int column = position[1];
			int currentValue = typedInTile.getValue();

			((InputTile) tilesMatrix[row][column]).addPossInt(currentValue);

			// Going left
			while (tilesMatrix[row][column] instanceof InputTile && column >= 0) {
				if (!tilesMatrix[row][column].equals(typedInTile)) {

					((InputTile) tilesMatrix[row][column]).addPossInt(currentValue);
					column--;
				} else {
					column--;
				}
			}

			// Going to the right
			row = position[0];
			column = position[1];
			while (tilesMatrix[row][column] instanceof InputTile && column < boardDimensions) {
				if (!tilesMatrix[row][column].equals(typedInTile)) {
					((InputTile) tilesMatrix[row][column]).addPossInt(currentValue);
					column++;
					if (column == boardDimensions) {
						break;
					}
				} else {
					column++;
					if (column == boardDimensions) {
						break;
					}
				}
			}

			// Going up
			row = position[0];
			column = position[1];
			while (tilesMatrix[row][column] instanceof InputTile && row >= 0) {
				if (!tilesMatrix[row][column].equals(typedInTile)) {
					((InputTile) tilesMatrix[row][column]).addPossInt(currentValue);
					row--;
				} else {
					row--;
				}
			}

			// Going down
			row = position[0];
			column = position[1];
			while (tilesMatrix[row][column] instanceof InputTile && row < boardDimensions) {
				if (!tilesMatrix[row][column].equals(typedInTile)) {
					((InputTile) tilesMatrix[row][column]).addPossInt(currentValue);
					row++;
					if (row == boardDimensions) {
						break;
					}
				} else {
					row++;
					if (row == boardDimensions) {
						break;
					}
				}
			}
		}

		@Override
		public void keyReleased(KeyEvent ke) {
			// TODO Auto-generated method stub

		}

		@Override
		public void keyTyped(KeyEvent ke) {
			// TODO Auto-generated method stub
		}

	}
}