package sudokusolver;

public class sudokuSolver {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {

        int[] correctable = {
                0,0,0,0,0,6,8,0,0,
                1,0,0,0,0,0,0,0,0,
                0,2,0,0,0,0,3,5,0,
                0,5,0,7,0,0,2,3,0,
                0,0,0,3,0,8,0,0,0,
                0,0,9,0,6,0,0,0,0,
                0,0,4,0,0,5,6,0,2,
                0,9,0,2,1,0,0,7,0,
                0,0,1,0,0,0,0,0,0
        };

        int[] easy   = {
                0,0,3,0,2,0,6,0,0,
                9,0,0,3,0,5,0,0,1,
                0,0,1,8,0,6,4,0,0,
                0,0,8,1,0,2,9,0,0,
                7,0,0,0,0,0,0,0,8,
                0,0,6,7,0,8,2,0,0,
                0,0,2,6,0,9,5,0,0,
                8,0,0,2,0,3,0,0,9,
                0,0,5,0,1,0,3,0,0
        };

        int[] master = {
                0,0,0,0,0,3,0,1,7,
                0,1,5,0,0,9,0,0,8,
                0,6,0,0,0,0,0,0,0,
                1,0,0,0,0,7,0,0,0,
                0,0,9,0,0,0,2,0,0,
                0,0,0,5,0,0,0,0,4,
                0,0,0,0,0,0,0,2,0,
                5,0,0,6,0,0,3,4,0,
                3,4,0,2,0,0,0,0,0
        };

        boolean solved;

        Sudoku sudoku = new Sudoku(correctable);
        solved = sudoku.solve();
        sudoku.printValues();
        if(!solved)
            sudoku.printNotes();

        sudoku = new Sudoku(master);
        solved = sudoku.solve();
        sudoku.printValues();
        if(!solved)
            sudoku.printNotes();

    }

}
