package com.diycomputerscience.minesweeper;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;

public class FilePersistenceStrategy implements PersistenceStrategy {

	private String fileName;
	
	public FilePersistenceStrategy(String fileName) {
		this.fileName = fileName;
	}
	
	@Override
	public void save(Board board) throws PersistenceException {
		PrintWriter printWriter = null;
		try {
			printWriter = new PrintWriter(this.fileName);
			Square squares[][] = board.getSquares();
			for(int row=0; row<squares.length; row++) {
				Square squareRow[] = squares[row];
				for(int col=0; col<squareRow.length; col++) {
					Square square = squareRow[col];
					String data = dataForSquare(row, col, square);
					printWriter.println(data);
				}
			}
		} catch(IOException ioe) {
			throw new PersistenceException("Could not save data", ioe);
		} finally {
			if(printWriter != null) {
				printWriter.close();
			}
		}
	}

	@Override
	public Board load() throws PersistenceException {
		Square squares[][] = new Square[Board.MAX_ROWS][Board.MAX_COLS];
		BufferedReader reader = null;
		try {
			reader = new BufferedReader(new FileReader(this.fileName));
			String data = null;
			while((data = reader.readLine()) != null) {
				String dataParts[] = data.split(" ");
				Square square = new Square();
				int row = Integer.parseInt(dataParts[0]);
				int col = Integer.parseInt(dataParts[1]);
				String strState = dataParts[2];
				int count = Integer.parseInt(dataParts[3]);
				boolean mine = Boolean.parseBoolean(dataParts[4]);
				square.setCount(count);
				square.setMine(mine);
				if("COVERED".equals(strState)) {
					square.setState(Square.SquareState.COVERED);
				} else if("UNCOVERED".equals(strState)) {
					square.setState(Square.SquareState.UNCOVERED);
				} else if("MARKED".equals(strState)) {
					square.setState(Square.SquareState.MARKED);
				}
				squares[row][col] = square;
			}
			Board board = new Board();
			board.setSquares(squares);
			board.computeCounts();
			return board;
		} catch(IOException ioe) {
			throw new PersistenceException("Could not load data", ioe);
		} finally {
			if(reader != null) {
				try {
					reader.close();
				} catch(IOException ioe) {
					System.out.println("Could not close reader to db file");
				}
			}
		}		
	}
	
	public static String dataForSquare(int row, int col, Square square) {
		return row + " " + col + " " + square.getState() + " " + square.getCount() + " " + square.isMine();
	}

}
