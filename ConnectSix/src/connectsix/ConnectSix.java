package connectsix;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedInputStream;
import java.io.InputStream;
import java.util.Random;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.sound.sampled.DataLine;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;

public final class ConnectSix extends JFrame implements ActionListener {
	private Boolean playsoundMode = true;


    /**
     * 
     */
    private static final long serialVersionUID = 1L;

    private int playerTurn; // Players turns (even numbers: player plays, odd numbers: computer plays)
    private int usedCellCnt; // Number of used cells
    private int playerslastmovecolumn; // Column which player latest moved
    private int rowsOfBoard; // Number of Rows
    private int columnsOfBoard; // Number of Columns
	private String playerColour; // Player's figure colour
	private String compColour;// Computer's figure colour
    private Boolean computersFirstMove;
	private Boolean restartInProgress = false;
	private int playerMoveCnt, computerMoveCnt; // Counter for each player

    // GUI Requirements
    private final JFrame myFrame; // Frame
    private final JPanel myButtonPanel; // Panel
	private JPanel myInfoPanel; // Info panel to show players' move in Text Area
	private JPanel displayPanel = new JPanel(new BorderLayout(8, 5));
	private final JButton[][] myButtons; // Buttons are jbutton type and two dimensional array
	private JButton myRestartButton; // Restart Button
	private JButton changeColourButton;
	private JTextArea myTextArea = new JTextArea(15, 75); // Text Area
	private JScrollPane scroll_text_area = new JScrollPane(myTextArea); // To set scroll bar for Text Area
    private myCell myGameBoard[][]; // Game Board
    private final GridLayout myGrid; // GridLayout


    private String[] playerFigureColour = { "Yellow", "Magenta", "Cyan", "Green", "Orange", "Black", "Pink", "Gray",
	    "Red", "Blue" }; // Player's colour options
    private String[] computerFigureColour = new String[9]; // used for computer colour options, it is set after player
							   // chooses a colour.

    // Button icons
	ImageIcon emptyIcon;
    ImageIcon winnerIcon;
    ImageIcon myPlayerIcon;
    ImageIcon computerIcon;

    public static void main(String[] args) {

	new ConnectSix();

    }

    public ConnectSix() {

	rowsOfBoard = 8; // Number of Rows
	columnsOfBoard = 9; // Number of Columns
	playerTurn = 0; // Players turns (even numbers: player plays, odd numbers: computer plays)
	usedCellCnt = 0; // Total number of used cells
	playerslastmovecolumn = 0; // Player's last move column
	playerMoveCnt = 0; // Player's move count
	computerMoveCnt = 0; // Computer's move count
	playerColour = "";
	compColour = "";
	computersFirstMove = false; // to control if computer's first move or not
	emptyIcon = createImageIcon("emptyicon.png");

	myFrame = new JFrame("Connect Six Game"); // Name of the window
	myButtonPanel = new JPanel(); // Panel for all Buttons
	myInfoPanel = new JPanel(); // Panel for Text Area and Restart Button

	createBoardCells(); // Creates 2D Cell array with Cell object instances

	myButtons = new JButton[rowsOfBoard][columnsOfBoard]; // Creates button array
	myGrid = new GridLayout(rowsOfBoard, columnsOfBoard); // Creates GridLayout
	myButtonPanel.setLayout(myGrid);

	myTextArea.setBackground(Color.BLUE); // myTextArea background colour is set here
	myTextArea.setForeground(Color.WHITE); // foreground colour is set here
	myTextArea.setFont(new Font("Times New Roman", Font.PLAIN, 14)); // myTextArea's font is set here
	myTextArea.setEditable(false); // myTextArea is set to non editable by user
	myTextArea.setColumns(75); // myTextArea's column size is set here
	myTextArea.setRows(3); // myTextArea's row size is set here
	myTextArea.setLineWrap(true); // lines will be wrapped if they are too long to fit within the allocated width.
	myTextArea.setWrapStyleWord(true); // lines will be wrapped at wordboundaries if they are too long to fit within
										// the allocated width.
	myRestartButton = new JButton("Restart");
	myRestartButton.setBounds(700, 700, 35, 35); // button's coordinates and sizes defined
	myRestartButton.addActionListener(this); // Sets Action Listener for Restart Button

	changeColourButton = new JButton("Change Colour");
	changeColourButton.setBounds(750, 700, 35, 35); // button's coordinates and sizes defined
	changeColourButton.addActionListener(this); // Sets Action Listener for Restart Button

	prepareBoard();// Initialise the board *
	addButtons(); // Add buttons and sets listener for them *

	playSound(1);// plays sound effect 'gong'

	myInfoPanel.add(scroll_text_area);
	myInfoPanel.add(myRestartButton);
	myInfoPanel.add(changeColourButton);

	displayPanel.add(myButtonPanel);
	displayPanel.add(myInfoPanel, BorderLayout.SOUTH);

	// Frame functions
	myFrame.setContentPane(displayPanel);
	myFrame.pack(); // Automatic sizing of the window based on the added swing components
	myFrame.setResizable(false); // non-resizable game board
	myFrame.setLocationRelativeTo(null); // Window will be at the centre of the screen
	myFrame.setVisible(true); // Show frame
	myFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE); // Sets the close operation of the frame

	setColours();

	chooseFirstPlayer();// To select first player

	playSound(3); // play sound effect 'begin'
	printcurrentpic();// printing Cells' states on the console
    }

	public void actionPerformed(ActionEvent e) {

		String myCommand = e.getActionCommand().toString();
		if (myCommand == "Restart") {
			restartGame();
		}
		if (myCommand == "Change Colour") {
			setColours();

		}
	}

	/**
	 * This method is used to select first player
	 */
	public void chooseFirstPlayer() {

		// Decision for first player
		int result = JOptionPane.showConfirmDialog(myFrame, "Do you want the computer to play first?",
				"First Player choser", JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE);
		if (result == JOptionPane.YES_OPTION) {
			computersFirstMove = true;
			playerTurn++; // playerTurn is set to 1, so computer starts first (Even values: Player's turn,
			// Odd values:Computer's turn)
		}
	}

    /**
     * Creates 2D dynamic Cell array with Cell object instances
     */
    public void createBoardCells() {
	// Creates dynamic Cell array for the game board
	myGameBoard = new myCell[rowsOfBoard][columnsOfBoard];
	for (int i = 0; i < rowsOfBoard; i++) {
	    for (int j = 0; j < columnsOfBoard; j++) {
		myGameBoard[i][j] = new myCell(); // Cell object instances are put into the myGameBoard array
	    }
	}
    }

	public void setColours() {
		// Selecting colour for Player's figure
		playerColour = (String) JOptionPane.showInputDialog(myFrame, "Select one of the colour for you.",
				"Player's Colour", JOptionPane.OK_OPTION, null, playerFigureColour, playerFigureColour[8]);

		if (playerColour == null) {
			playerColour = "Red"; // If player clicks Cancel button, Red colour is given as default
		}

		int j = 0;
		for (int i = 0; i < playerFigureColour.length; i++) {
			if (playerColour.equals(playerFigureColour[i]) == false) {
				computerFigureColour[j] = playerFigureColour[i]; // For computer's colour selection
				// computerFigureColour array is filled except the colour which was
				// selected by the player
				j++;
			}
		}
		// Selecting colour for Computer's figure
		compColour = (String) JOptionPane.showInputDialog(myFrame, "Select one of the colour for the computer. ",
				"Computer's Colour", JOptionPane.OK_OPTION, null, computerFigureColour, computerFigureColour[0]);
		if (compColour == null && playerColour.equals("Yellow") == false) {
			compColour = "Yellow"; // If player clicks Cancel button for computer, Yellow colour is assigned as
			// default
		} else if (compColour == null && playerColour.equals("Yellow") == true) {
			compColour = "Red"; // if player select Yellow colour and Cancel button is clicked for computer, Red
			// colour is assigned for computer
		}

		// Players Image Icons are set for their figure
		myPlayerIcon = createImageIcon(getPlayerColourIcon(playerColour)); // Sets player's image icon colour
		computerIcon = createImageIcon(getPlayerColourIcon(compColour));// Sets computer's image icon colour

		// while playing if you change colour, this part changes players' and computers'
		// places with the new colours.
		for (int k = 0; k < rowsOfBoard; ++k) {
			for (int m = 0; m < columnsOfBoard; ++m) {
				if (myGameBoard[k][m].getMyCellState() == 1)
					myButtons[k][m].setIcon(myPlayerIcon);
				else if (myGameBoard[k][m].getMyCellState() == 2)
					myButtons[k][m].setIcon(computerIcon);

			}
		}
	}

    /**
     * Adds buttons to the game board and sets listener for them
     */
    public void addButtons() {
		for (int row_n = 0; row_n < rowsOfBoard; ++row_n) {
			for (int col_n = 0; col_n < columnsOfBoard; ++col_n) {
				myButtons[row_n][col_n] = new JButton(emptyIcon); // All buttons' are set with Empty Icon picture
				myButtons[row_n][col_n].setBackground(Color.BLUE);
				myButtons[row_n][col_n].addActionListener(new listenButtonEvents());
				myButtonPanel.add(myButtons[row_n][col_n]); // Add buttons to panel
	    }
	}
    }

    /**
     * Initialises all cells as empty
     */
    public void prepareBoard() {
	// All cells are set 9 except 7th row. At the beginning 7th row is High Water
	// Mark
	for (int i = rowsOfBoard - 2; i >= 0; --i) {
	    for (int j = columnsOfBoard - 1; j >= 0; --j) {
		myGameBoard[i][j].setMyCellState(9); // 9 means empty cells
	    }
	}
    }

    /**
     * states the game winner, If the six cells are the same, player wins the game
     * 
     * @param winner integer (player is equal to 1, computer 2)
     */
	public void checkWinnerPlayer(int playerID) {
		for (int row_n = 0; row_n < rowsOfBoard; ++row_n) {
			for (int col_n = 0; col_n < columnsOfBoard; ++col_n) {
				if (myGameBoard[row_n][col_n].getMyCellState() == playerID) {
		    // Checks (UP TO DOWN)
					if (row_n + 5 < rowsOfBoard) {
						if (myGameBoard[row_n + 1][col_n].getMyCellState() == playerID
								&& myGameBoard[row_n + 2][col_n].getMyCellState() == playerID
								&& myGameBoard[row_n + 3][col_n].getMyCellState() == playerID
								&& myGameBoard[row_n + 4][col_n].getMyCellState() == playerID
								&& myGameBoard[row_n + 5][col_n].getMyCellState() == playerID) {

			    if (playerID == 1)
				winnerIcon = createImageIcon("winner1.gif");
			    else
				winnerIcon = createImageIcon("winner2.gif");

			myButtons[row_n][col_n].setIcon(winnerIcon);
			myButtons[row_n + 1][col_n].setIcon(winnerIcon);
			myButtons[row_n + 2][col_n].setIcon(winnerIcon);
			myButtons[row_n + 3][col_n].setIcon(winnerIcon);
			myButtons[row_n + 4][col_n].setIcon(winnerIcon);
			myButtons[row_n + 5][col_n].setIcon(winnerIcon);

			    stateWinner(playerID);
			}
		    }
		    // Checks (LEFT TO RIGHT)
			if (col_n + 5 < columnsOfBoard) {
				if (myGameBoard[row_n][col_n + 1].getMyCellState() == playerID
						&& myGameBoard[row_n][col_n + 2].getMyCellState() == playerID
						&& myGameBoard[row_n][col_n + 3].getMyCellState() == playerID
						&& myGameBoard[row_n][col_n + 4].getMyCellState() == playerID
						&& myGameBoard[row_n][col_n + 5].getMyCellState() == playerID) {

			    if (playerID == 1)
				winnerIcon = createImageIcon("winner1.gif");
			    else
				winnerIcon = createImageIcon("winner2.gif");

			myButtons[row_n][col_n].setIcon(winnerIcon);
			myButtons[row_n][col_n + 1].setIcon(winnerIcon);
			myButtons[row_n][col_n + 2].setIcon(winnerIcon);
			myButtons[row_n][col_n + 3].setIcon(winnerIcon);
			myButtons[row_n][col_n + 4].setIcon(winnerIcon);
			myButtons[row_n][col_n + 5].setIcon(winnerIcon);

			    stateWinner(playerID);
			}
		    }

		    // Checks diagonal ( \ LEFT TO RIGHT)
			if (row_n < rowsOfBoard - 5 && col_n < columnsOfBoard - 5) {
				if (myGameBoard[row_n + 1][col_n + 1].getMyCellState() == playerID
						&& myGameBoard[row_n + 2][col_n + 2].getMyCellState() == playerID
						&& myGameBoard[row_n + 3][col_n + 3].getMyCellState() == playerID
						&& myGameBoard[row_n + 4][col_n + 4].getMyCellState() == playerID
						&& myGameBoard[row_n + 5][col_n + 5].getMyCellState() == playerID) {

			    if (playerID == 1)
				winnerIcon = createImageIcon("winner1.gif");
			    else
				winnerIcon = createImageIcon("winner2.gif");

			myButtons[row_n][col_n].setIcon(winnerIcon);
			myButtons[row_n + 1][col_n + 1].setIcon(winnerIcon);
			myButtons[row_n + 2][col_n + 2].setIcon(winnerIcon);
			myButtons[row_n + 3][col_n + 3].setIcon(winnerIcon);
			myButtons[row_n + 4][col_n + 4].setIcon(winnerIcon);
			myButtons[row_n + 5][col_n + 5].setIcon(winnerIcon);

			    stateWinner(playerID);
			}
		    }

		    // Checks diagonal ( / RIGHT TO LEFT)
			if (row_n < rowsOfBoard - 5 && col_n - 5 >= 0) {
				if (myGameBoard[row_n + 1][col_n - 1].getMyCellState() == playerID
						&& myGameBoard[row_n + 2][col_n - 2].getMyCellState() == playerID
						&& myGameBoard[row_n + 3][col_n - 3].getMyCellState() == playerID
						&& myGameBoard[row_n + 4][col_n - 4].getMyCellState() == playerID
						&& myGameBoard[row_n + 5][col_n - 5].getMyCellState() == playerID) {

			    if (playerID == 1)
				winnerIcon = createImageIcon("winner1.gif");
			    else
				winnerIcon = createImageIcon("winner2.gif");

			myButtons[row_n][col_n].setIcon(winnerIcon);
			myButtons[row_n + 1][col_n - 1].setIcon(winnerIcon);
			myButtons[row_n + 2][col_n - 2].setIcon(winnerIcon);
			myButtons[row_n + 3][col_n - 3].setIcon(winnerIcon);
			myButtons[row_n + 4][col_n - 4].setIcon(winnerIcon);
			myButtons[row_n + 5][col_n - 5].setIcon(winnerIcon);

			stateWinner(playerID);
			}
		    }

		    // if there is no empty place on the board. Game is drawn
		    if (usedCellCnt == rowsOfBoard * columnsOfBoard) {
			stateWinner(3);
		    }

		}
	    }
	}
} // End StateWinner function

    /**
	 * States winner player and triggers the restartGame()
	 * 
	 * @param checkWinnerPlayer integer: (1- Player , 2-Computer 3-Draw)
	 */
	public void stateWinner(int checkWinnerPlayer) {

		JFrame framestateWinner = new JFrame();

		if (checkWinnerPlayer == 1) { // Player is the winner
		myTextArea.append("Player wins!\n");
		playSound(6);// plays sound effect 'you_win'
	    JOptionPane.showMessageDialog(framestateWinner,
				"Player is the winner.", "Game over.",
		    JOptionPane.INFORMATION_MESSAGE);

	} else if (checkWinnerPlayer == 2) { // Computer is the winner
		myTextArea.append("Computer wins!\n");
		playSound(4);// plays sound effect 'winner'
		playSound(9);// plays sound effect 'applause'
	    JOptionPane.showMessageDialog(framestateWinner,
				"Computer is the winner.", "Game over.",
		    JOptionPane.INFORMATION_MESSAGE);

	} else if (checkWinnerPlayer == 3) { // Game is drawn
		myTextArea.append("Game is drawn!\n");
		playSound(7);// plays sound effect 'drawn'
	    JOptionPane.showMessageDialog(framestateWinner,
				"No winner. The game is drawn.", "Game over.",
		    JOptionPane.INFORMATION_MESSAGE);
	}

	String msg = "******************** Game Over! ********************"
			+ "\nDo you wish to play again? ";

	// it is asked to players if they want to play again
	JDialog.setDefaultLookAndFeelDecorated(true);
	int response = JOptionPane.showConfirmDialog(null, msg, "Confirm", JOptionPane.YES_NO_OPTION,
			JOptionPane.QUESTION_MESSAGE);
	if (response == JOptionPane.NO_OPTION) {
		System.exit(0); // if players choose NO option program finishes
	} else if (response == JOptionPane.YES_OPTION) {
		restartGame();
	} else if (response == JOptionPane.CLOSED_OPTION) {
		System.exit(0); // if players click the cross sign of the dialog box, program finishes
	}

    }

    /**
     * After the game ends, set required object to initial state and restarts the
     * game
     */
	public void restartGame() {

		// All cells are set 9 except 7th row.
		prepareBoard();

		for (int i = 0; i < rowsOfBoard; ++i) {
			for (int j = 0; j < columnsOfBoard; ++j) {
				myButtons[i][j].setIcon(emptyIcon); // All buttons' are set with Empty Icon picture

				if (i == rowsOfBoard - 1) {
					myGameBoard[i][j].setMyCellState(0); // At the beginning 7th row is High Water Mark
				}
			}
		}

		usedCellCnt = 0; // Resets Total number of used cells
		playerslastmovecolumn = 0; // Resets Player's last move column
		playerMoveCnt = 0; // Resets player's move count
		computerMoveCnt = 0;// Resets computer's move count
		computersFirstMove = false;
		restartInProgress = true;
		playerTurn = 0;// Resets player turn

		chooseFirstPlayer(); // To select first player
		myTextArea.append("Game was restarted.\n");// Resets Text Area
		playSound(2); // play sound effect 'ready'

    }

    /**
     *
     * Action listener to game button Computer vs Player 1
     */
    private class listenButtonEvents implements ActionListener {
	@Override
	public void actionPerformed(ActionEvent e) {
		
	    try {
		for (int i = rowsOfBoard - 1; i >= 0; --i) // Checks the buttons up to down position
		{
		    for (int j = 0; j <= columnsOfBoard - 1; ++j) {
			// Gets the button component that was clicked
			if (myButtons[i][j] == e.getSource()) {

				if (0 == playerTurn % 2 && restartInProgress == false) // Player's operations
			    {
					for (int k = 0; k <= rowsOfBoard; ++i) {
				    // Player's Operations
				    // Fills the board starting from down towards to up
				    if (myGameBoard[i - k][j].getMyCellState() == 0) {
					myButtons[i - k][j].setIcon(myPlayerIcon); // Changes button icon
					myGameBoard[i - k][j].setMyCellState(1); // Sets cell state for player
					usedCellCnt++; // Increases used cell number
					playerMoveCnt++;// Increases player's move count
					playerTurn++;// Changes player's turn from player to computer (Even values:
					// Player, Odd values:Computer)
					playSound(5); // plays sound effect 'player_move'
					setHighWaterMarkEmpty(i - k - 1, j); // The upper cell is set as High Water Mark for that
					// column
					playerslastmovecolumn = j;
					myTextArea.append("Player's move:" + playerMoveCnt + " [Row:Column]= [" + i + ":" + j + "]\n");
					checkWinnerPlayer(1); // Checks if player wins the game
					break;
				    }
				}

				printcurrentpic(); // logs cells' states
				break;
			    }

			    // Computer's Movements
				if (1 == playerTurn % 2 && restartInProgress == false) {
					playerTurn++; // Changes player's turn from player to computer (Even values: Player, Odd
					// values:Computer)
					++usedCellCnt;// Increases used cell number
					++computerMoveCnt;// Increases computer's move count
					moveComputer(); // Decision Engine for computer's move

				printcurrentpic();
				break;
			    } else {
				invalidMoveMsg();
			    }
			} // END EVENT SOURCE
		    } // END SECOND FOR LOOP
		} // END FIRST FOR LOOP

	    } // END TRY
	    catch (Exception ex) {
		invalidMoveMsg();
	    }
		restartInProgress = false;
	} // END ACTION PERFORMED

    } // END listenButtonEvents CLASS

    /**
     * The cell is set as High Water Mark for the column
     * 
     * @param rowPos    integer: row position on the board
     * @param columnPos integer: column position on the board
     */
	public void setHighWaterMarkEmpty(int rowPos, int columnPos) {
		// in case out of bound of myGameBoard try-catch mechanism prevent the code from
		// crashing.
	try {
	    myGameBoard[rowPos][columnPos].setMyCellState(0);
	} catch (Exception ex) {
	}
    }

    /**
	 * Computer's move decision engine
	 * 
	 */
    public void moveComputer() {
	int m, i;
	boolean decisionFlag = false;
	Random randGen = new Random();
	int decidedPlace;
	int mycounter = 0;
	int outercounter = 0; // Just in case prevent the infinite loop

	// If computer starts first, it randomly select the first place
	if (computersFirstMove == true) {
	    computersFirstMove = false;
	    playerslastmovecolumn = randGen.nextInt(columnsOfBoard); // generates number between 0-8
	}

	// Basic idea is play on top of player's latest move or left column of it or
	// right column of it.
	while (mycounter < 10 && decisionFlag == false && outercounter < 100) {
	    // If player plays the last cell of a column and if the left and right side of
	    // this cell is entirely full, Loop goes infinite. So I created a variable named
	    // outercounter as a control to break the while-loop, if it reaches 100.

		decidedPlace = randGen.nextInt(3); // generates number between 0-2 (very important)

	    if (decisionFlag == false && decidedPlace == 0) { // Left side of the player's latest move
		if (playerslastmovecolumn - 1 > 0) {
		    for (i = rowsOfBoard - 1; (i >= 0); --i) {
			if (myGameBoard[i][playerslastmovecolumn - 1].getMyCellState() == 0 && decisionFlag == false) {
				playSound(5);// plays sound effect 'player_move'
			    myButtons[i][playerslastmovecolumn - 1].setIcon(computerIcon); // Set new button icon
			    myGameBoard[i][playerslastmovecolumn - 1].setMyCellState(2); // Set cell state
				myTextArea.append("Computer's move:" + computerMoveCnt + " [Row:Column]= [" + i + ":"
						+ (playerslastmovecolumn - 1) + "]\n");
				setHighWaterMarkEmpty(i - 1, playerslastmovecolumn - 1);// The upper cell is set as High
				// Water Mark for that column
				checkWinnerPlayer(2); // Checks if the computer wins or not
			    decisionFlag = true;
				mycounter++;
			}
		    }

		}
	    }
	    if (decisionFlag == false && decidedPlace == 1) { // Right side of the player's latest move
		if (playerslastmovecolumn + 1 < 9) {
		    for (i = rowsOfBoard - 1; (i >= 0); --i) {
			if (myGameBoard[i][playerslastmovecolumn + 1].getMyCellState() == 0 && decisionFlag == false) {
				playSound(5);// plays sound effect 'player_move'
			    myButtons[i][playerslastmovecolumn + 1].setIcon(computerIcon); // Set new button icon
			    myGameBoard[i][playerslastmovecolumn + 1].setMyCellState(2); // Set cell state
				myTextArea.append("Computer's move:" + computerMoveCnt + " [Row:Column]= [" + i + ":"
						+ (playerslastmovecolumn + 1) + "]\n");
				setHighWaterMarkEmpty(i - 1, playerslastmovecolumn + 1);// The upper cell is set as High
				// Water Mark for that column
				checkWinnerPlayer(2); // Checks if the computer wins or not
			    decisionFlag = true;
				mycounter++;
			}
		    }

		}
	    }

	    if (decisionFlag == false && decidedPlace == 2) { // Top of the player's latest move
		for (i = rowsOfBoard - 1; (i >= 0); --i) {
		    if (myGameBoard[i][playerslastmovecolumn].getMyCellState() == 0 && decisionFlag == false) {
				playSound(5);// plays sound effect 'player_move'
			myButtons[i][playerslastmovecolumn].setIcon(computerIcon); // Set new button icon
			myGameBoard[i][playerslastmovecolumn].setMyCellState(2); // Set cell state
			myTextArea.append(
					"Computer's move:" + computerMoveCnt + " [Row:Column]= [" + i + ":" + playerslastmovecolumn
							+ "]\n");
			setHighWaterMarkEmpty(i - 1, playerslastmovecolumn);// The upper cell is set as High Water Mark
			// for that column
			checkWinnerPlayer(2); // Checks if the computer wins or not
			decisionFlag = true;
			mycounter++;
		    }
		}

	    }
	    outercounter++;
	}
	// If system can't find a place near players latest movement, basically it fills
	// left to right very first empty place
	for (i = rowsOfBoard - 1; (i >= 0) && decisionFlag == false; --i) {
		for (m = 0; (m < columnsOfBoard) && decisionFlag == false; ++m) {
		if (myGameBoard[i][m].getMyCellState() == 0) {
			playSound(5);// plays sound effect 'player_move'
		    myButtons[i][m].setIcon(computerIcon); // Set new button icon
		    myGameBoard[i][m].setMyCellState(2); // Set cell state to 2 for computer
			myTextArea.append("Computer's move:" + computerMoveCnt + " [Row:Column]= [" + i + ":" + m + "]\n");
			setHighWaterMarkEmpty(i - 1, m); // The upper cell is set as High Water Mark for that column
			checkWinnerPlayer(2); // Checks if the computer wins or not
		    decisionFlag = true;
		}
	    }
	}
    }

    /**
     * raises warning message for exceptional movement cases
     * 
     */
    public void invalidMoveMsg() {
	if (usedCellCnt > 0) {
	    JFrame frameWarning = new JFrame();
	    JOptionPane.showMessageDialog(frameWarning, "Invalid Movement !\nThe cell is not empty.", "Warning",
		    JOptionPane.WARNING_MESSAGE);
	}
    }

    /**
     * According to the colour selection, returns the related file for icons.
     *
     * @param Colour String:selected colour from the dialog box
     */
    public String getPlayerColourIcon(String Colour) {
	String iconFile = "";

	switch (Colour) {

	case "Magenta":
	    iconFile = ("fig0.png");
	    break;
	case "Yellow":
	    iconFile = ("fig1.png");
	    break;
	case "Cyan":
	    iconFile = ("fig2.png");
	    break;
	case "Green":
	    iconFile = ("fig3.png");
	    break;
	case "Orange":
	    iconFile = ("fig4.png");
	    break;
	case "Black":
	    iconFile = ("fig5.png");
	    break;
	case "Pink":
	    iconFile = ("fig6.png");
	    break;
	case "Gray":
	    iconFile = ("fig7.png");
	    break;
	case "Red":
	    iconFile = ("fig8.png");
	    break;
	case "Blue":
	    iconFile = ("fig9.png");
	    break;
	default:
	    break;
	}
	return iconFile;
    }

    /**
     * Sets image icons for players from related PNG file
     * 
     * @param myIconPicFile String: image file name
     */
    public ImageIcon createImageIcon(String myIconPicFile) {
	java.net.URL imgURL = getClass().getClassLoader().getResource(myIconPicFile); // gets image file's full path
	if (imgURL != null) {
	    return new ImageIcon(imgURL);
	} else {
	    System.err.println("Couldn't find Icon file: " + myIconPicFile);
	    return null;
	}
    }

    // Shows every cell's current value
    public void printcurrentpic() {
	System.out.println("*************************************************");
	for (int i = 0; i < rowsOfBoard; i++) {
	    for (int j = 0; j < columnsOfBoard; j++) {
		System.out
				.println("myGameBoard Cell:[" + i + "][" + j + "]" + " State:" + myGameBoard[i][j].getMyCellState()
						+ " ");
	    }
	}
    }

	// Sound Effects will be added: gong, begin, ready, winner, gameover, falling
	// figure
	public void playSound(int i) {
		String path = "tada.wav";
		// this method plays sound effects
		if (playsoundMode == true) {


			switch (i) {
			case 1:
				path = "gong.wav";
				break;
			case 2:
				path = "ready.wav";
				break;
			case 3:
				path = "begin.wav";
				break;
			case 4:
				path = "winner.wav";
				break;
			case 5:
				path = "player_move3.wav";
				break;
			case 6:
				path = "you_win.wav";
				break;
			case 7:
				path = "drawn.wav";
				break;
			case 8:
				path = "tada.wav";
				break;
			case 9:
				path = "applause.wav";
				break;

			}
		}
		AudioFormat format;
		DataLine.Info info;
		Clip clip;

		try {

			java.net.URL url = this.getClass().getClassLoader().getResource(path);
			InputStream in = url.openStream();

			AudioInputStream stream = AudioSystem.getAudioInputStream(new BufferedInputStream(in));
			format = stream.getFormat();
			info = new DataLine.Info(Clip.class, format);
			clip = (Clip) AudioSystem.getLine(info);
			clip.open(stream);
			clip.start();

		} catch (Exception e) {
			e.printStackTrace();
			System.out.println("Error: " + this.getClass().getClassLoader().getResourceAsStream("path"));

		}
	}
}
