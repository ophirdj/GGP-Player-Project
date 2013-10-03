package debugging;

import java.util.Set;

import org.ggp.base.util.gdl.grammar.GdlSentence;

public class TicTacToeBoardPrint {

	public static char[][] parseState(Set<GdlSentence> contents) {
		char board[][] = new char[3][3];
		for (GdlSentence s : contents) {
			String str = s.get(0).toString();
			if (str.contains("cell")) {
				int row = Integer.parseInt(str.substring(7, 8));
				int col = Integer.parseInt(str.substring(9, 10));
				char type = str.charAt(11);
				board[row - 1][col - 1] = type;
			}
		}
		return board;
	}

	public static void printBoard(char[][] board) {
		for (int i = 0; i < 3; i++) {
			for (int j = 0; j < 3; j++) {
				if(board[i][j] == 'b') System.out.println("  ");
				System.out.print("" + board[i][j] + " ");
			}
			System.out.println();
		}
		System.out.println();
	}
	
	public static void printTicTacToe(Set<GdlSentence> contents, long verboseType) {
		if (Verbose.isVerbose(verboseType)){
			printBoard(parseState(contents));
		}
		
	}
}
