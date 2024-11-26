import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Random;
import java.util.Stack;

public class TicTacToeGame extends JFrame implements ActionListener {
    private JPanel mainMenu, gamePanel, settingsPanel;
    private JButton[][] buttons;
    private boolean playerXTurn = true;
    private String difficulty = "Medium";
    private Random random = new Random();
    private Stack<Move> movesStack = new Stack<>();
    private JButton undoButton, backButton, settingsButton;
    private Color xColor = Color.CYAN;
    private Color oColor = Color.RED;
    private Color boardColor = Color.DARK_GRAY;
    private int boardSize = 3;
    private int cellSize = 80;

    public TicTacToeGame() {
        setTitle("Tic Tac Toe Game");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new CardLayout());

        initMainMenu();
        initSettingsPanel();
        initGamePanel();

        add(mainMenu, "MainMenu");
        add(settingsPanel, "SettingsPanel");
        add(gamePanel, "GamePanel");
        showMainMenu();
    }

    private void initMainMenu() {
        mainMenu = new JPanel(new BorderLayout());
        JLabel title = new JLabel("Tic Tac Toe", JLabel.CENTER);
        title.setFont(new Font("Arial", Font.BOLD, 24));
        title.setForeground(Color.CYAN);

        JButton startGameButton = new JButton("Start Game");
        startGameButton.setFont(new Font("Arial", Font.BOLD, 18));
        startGameButton.addActionListener(e -> startGame());

        settingsButton = new JButton("Settings");
        settingsButton.setFont(new Font("Arial", Font.BOLD, 18));
        settingsButton.addActionListener(e -> showSettings());

        mainMenu.setBackground(Color.BLACK);
        mainMenu.add(title, BorderLayout.CENTER);
        JPanel buttonPanel = new JPanel();
        buttonPanel.setBackground(Color.BLACK);
        buttonPanel.add(startGameButton);
        buttonPanel.add(settingsButton);
        mainMenu.add(buttonPanel, BorderLayout.SOUTH);
    }

    private void initSettingsPanel() {
        settingsPanel = new JPanel(new GridLayout(5, 2));
        settingsPanel.setBackground(Color.BLACK);

        settingsPanel.add(new JLabel("Board Size (3-5):", JLabel.RIGHT));
        JTextField boardSizeField = new JTextField(String.valueOf(boardSize));
        settingsPanel.add(boardSizeField);

        settingsPanel.add(new JLabel("X Color:", JLabel.RIGHT));
        JButton xColorButton = new JButton();
        xColorButton.setBackground(xColor);
        xColorButton.addActionListener(e -> {
            xColor = JColorChooser.showDialog(this, "Choose X Color", xColor);
            xColorButton.setBackground(xColor);
        });
        settingsPanel.add(xColorButton);

        settingsPanel.add(new JLabel("O Color:", JLabel.RIGHT));
        JButton oColorButton = new JButton();
        oColorButton.setBackground(oColor);
        oColorButton.addActionListener(e -> {
            oColor = JColorChooser.showDialog(this, "Choose O Color", oColor);
            oColorButton.setBackground(oColor);
        });
        settingsPanel.add(oColorButton);

        settingsPanel.add(new JLabel("Board Color:", JLabel.RIGHT));
        JButton boardColorButton = new JButton();
        boardColorButton.setBackground(boardColor);
        boardColorButton.addActionListener(e -> {
            boardColor = JColorChooser.showDialog(this, "Choose Board Color", boardColor);
            boardColorButton.setBackground(boardColor);
        });
        settingsPanel.add(boardColorButton);

        JButton saveSettingsButton = new JButton("Save Settings");
        saveSettingsButton.addActionListener(e -> {
            boardSize = Integer.parseInt(boardSizeField.getText());
            resetGamePanel();
            showMainMenu();
        });
        settingsPanel.add(saveSettingsButton);
    }

    private void initGamePanel() {
        gamePanel = new JPanel(new BorderLayout());
        resetGamePanel();
        
        JPanel controlsPanel = new JPanel();
        controlsPanel.setLayout(new FlowLayout());
        controlsPanel.setBackground(Color.BLACK);

        undoButton = new JButton("Undo");
        undoButton.addActionListener(e -> undoLastMove());
        undoButton.setBackground(Color.DARK_GRAY);
        undoButton.setForeground(Color.WHITE);
        undoButton.setFont(new Font("Arial", Font.BOLD, 16));

        backButton = new JButton("Back to Main Menu");
        backButton.addActionListener(e -> showMainMenu());
        backButton.setBackground(Color.DARK_GRAY);
        backButton.setForeground(Color.WHITE);
        backButton.setFont(new Font("Arial", Font.BOLD, 16));

        controlsPanel.add(undoButton);
        controlsPanel.add(backButton);

        gamePanel.add(controlsPanel, BorderLayout.SOUTH);
    }

    private void resetGamePanel() {
        if (buttons != null) {
            for (JButton[] buttonRow : buttons) {
                for (JButton button : buttonRow) {
                    gamePanel.remove(button);
                }
            }
        }
        
        buttons = new JButton[boardSize][boardSize];
        JPanel boardPanel = new JPanel(new GridLayout(boardSize, boardSize));
        boardPanel.setBackground(boardColor);
        initializeButtons(boardPanel);
        
        gamePanel.add(boardPanel, BorderLayout.CENTER);
    }

    private void initializeButtons(JPanel panel) {
        for (int row = 0; row < boardSize; row++) {
            for (int col = 0; col < boardSize; col++) {
                buttons[row][col] = new JButton("");
                buttons[row][col].setFont(new Font("Arial", Font.BOLD, cellSize));
                buttons[row][col].setFocusPainted(false);
                buttons[row][col].setBackground(boardColor);
                buttons[row][col].setForeground(Color.WHITE);
                buttons[row][col].addActionListener(this);
                panel.add(buttons[row][col]);
            }
        }
    }

    private void showSettings() {
        ((CardLayout) getContentPane().getLayout()).show(getContentPane(), "SettingsPanel");
    }

    private void showMainMenu() {
        ((CardLayout) getContentPane().getLayout()).show(getContentPane(), "MainMenu");
    }

    private void startGame() {
        resetBoard();
        setDifficulty();
        ((CardLayout) getContentPane().getLayout()).show(getContentPane(), "GamePanel");
    }

    private void setDifficulty() {
        String[] options = {"Easy", "Medium", "Hard"};
        difficulty = (String) JOptionPane.showInputDialog(
                this,
                "Choose difficulty:",
                "Difficulty Level",
                JOptionPane.QUESTION_MESSAGE,
                null,
                options,
                options[1]
        );
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        JButton clickedButton = (JButton) e.getSource();
        if (!clickedButton.getText().equals("") || !playerXTurn) return;

        clickedButton.setText("X");
        clickedButton.setForeground(xColor);
        saveMove(1, clickedButton);

        if (checkWin()) {
            showEndGameMessage("Player X wins!");
            return;
        } else if (isBoardFull()) {
            showEndGameMessage("It's a draw!");
            return;
        }

        playerXTurn = false;
        botMove();
    }

    private void botMove() {
        if (difficulty.equals("Easy")) {
            randomMove();
        } else if (difficulty.equals("Medium")) {
            if (!blockOrWinMove()) {
                randomMove();
            }
        } else if (difficulty.equals("Hard")) {
            minimaxMove();
        }

        if (checkWin()) {
            showEndGameMessage("Bot wins!");
            return;
        } else if (isBoardFull()) {
            showEndGameMessage("It's a draw!");
            return;
        }

        playerXTurn = true;
    }

    private void randomMove() {
        int row, col;
        do {
            row = random.nextInt(boardSize);
            col = random.nextInt(boardSize);
        } while (!buttons[row][col].getText().equals(""));

        buttons[row][col].setText("O");
        buttons[row][col].setForeground(oColor);
        saveMove(2, buttons[row][col]);
    }

    private boolean blockOrWinMove() {
        for (int i = 0; i < boardSize; i++) {
            if (checkAndPlace(i, 0, i, 1, i, 2)) return true;
            if (checkAndPlace(0, i, 1, i, 2, i)) return true;
        }
        return checkAndPlace(0, 0, 1, 1, 2, 2) || checkAndPlace(0, 2, 1, 1, 2, 0);
    }

    private boolean checkAndPlace(int r1, int c1, int r2, int c2, int r3, int c3) {
        if (r3 >= boardSize || c3 >= boardSize) return false;
        String mark = buttons[r1][c1].getText();
        if (mark.equals("") || mark.equals("O")) return false;

        if (buttons[r2][c2].getText().equals(mark) && buttons[r3][c3].getText().equals("")) {
            buttons[r3][c3].setText("O");
            buttons[r3][c3].setForeground(oColor);
            saveMove(2, buttons[r3][c3]);
            return true;
        }
        return false;
    }

    private void minimaxMove() {
        int[] bestMove = minimax(0, true);
        buttons[bestMove[0]][bestMove[1]].setText("O");
        buttons[bestMove[0]][bestMove[1]].setForeground(oColor);
        saveMove(2, buttons[bestMove[0]][bestMove[1]]);
    }

    private int[] minimax(int depth, boolean isMaximizing) {
        if (checkWin()) return new int[]{-1, -1, -1}; // Player X wins
        if (isBoardFull()) return new int[]{-1, -1, 0}; // Draw

        int bestScore = isMaximizing ? Integer.MIN_VALUE : Integer.MAX_VALUE;
        int[] bestMove = new int[]{-1, -1, bestScore};

        for (int row = 0; row < boardSize; row++) {
            for (int col = 0; col < boardSize; col++) {
                if (buttons[row][col].getText().equals("")) {
                    buttons[row][col].setText(isMaximizing ? "O" : "X");
                    int score = minimax(depth + 1, !isMaximizing)[2];
                    buttons[row][col].setText("");
                    
                    if (isMaximizing) {
                        if (score > bestScore) {
                            bestScore = score;
                            bestMove[0] = row;
                            bestMove[1] = col;
                            bestMove[2] = bestScore;
                        }
                    } else {
                        if (score < bestScore) {
                            bestScore = score;
                            bestMove[0] = row;
                            bestMove[1] = col;
                            bestMove[2] = bestScore;
                        }
                    }
                }
            }
        }
        return bestMove;
    }

    private boolean checkWin() {
        for (int i = 0; i < boardSize; i++) {
            if (checkRow(i) || checkCol(i)) return true;
        }
        return checkDiagonal() || checkAntiDiagonal();
    }

    private boolean checkRow(int row) {
        String mark = buttons[row][0].getText();
        return !mark.equals("") && mark.equals(buttons[row][1].getText()) && mark.equals(buttons[row][2].getText());
    }

    private boolean checkCol(int col) {
        String mark = buttons[0][col].getText();
        return !mark.equals("") && mark.equals(buttons[1][col].getText()) && mark.equals(buttons[2][col].getText());
    }

    private boolean checkDiagonal() {
        String mark = buttons[0][0].getText();
        return !mark.equals("") && mark.equals(buttons[1][1].getText()) && mark.equals(buttons[2][2].getText());
    }

    private boolean checkAntiDiagonal() {
        String mark = buttons[0][boardSize - 1].getText();
        return !mark.equals("") && mark.equals(buttons[1][boardSize - 2].getText()) && mark.equals(buttons[2][0].getText());
    }

    private boolean isBoardFull() {
        for (JButton[] buttonRow : buttons) {
            for (JButton button : buttonRow) {
                if (button.getText().equals("")) return false;
            }
        }
        return true;
    }

    private void saveMove(int player, JButton button) {
        movesStack.push(new Move(player, button.getText(), button));
    }

    private void undoLastMove() {
        if (movesStack.isEmpty()) return;

        Move lastMove = movesStack.pop();
        lastMove.button.setText("");
        playerXTurn = !playerXTurn;
    }

    private void resetBoard() {
        for (JButton[] buttonRow : buttons) {
            for (JButton button : buttonRow) {
                button.setText("");
            }
        }
        movesStack.clear();
        playerXTurn = true;
    }

    private void showEndGameMessage(String message) {
        JOptionPane.showMessageDialog(this, message);
        resetBoard();
        showMainMenu();
    }

    public static void main(String[] args) {
        TicTacToeGame game = new TicTacToeGame();
        game.setSize(600, 600);
        game.setVisible(true);
    }

    private static class Move {
        int player;
        String mark;
        JButton button;

        Move(int player, String mark, JButton button) {
            this.player = player;
            this.mark = mark;
            this.button = button;
        }
    }
}
