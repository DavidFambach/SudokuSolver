package sudokusolver;

public class Sudoku implements SudokuObject {
    private final int[] valueArray;
    private final Field[] fields = new Field[81];
    private final Area[] rows;
    private final Area[] columns;
    private final Area[] squares;
    private boolean wasChanged;


    public Sudoku(int[] input) {
        this.valueArray = input;

        for (int i = 0; i < 81; i++)
            fields[i] = new Field(i);

        rows = new Area[9];
        Field[] areaFieldsRow = new Field[9];

        columns = new Area[9];
        Field[] areaFieldsColumn = new Field[9];

        squares = new Area[9];
        Field[] areaFieldsSquare = new Field[9];

        for (int r = 0; r < 9; r++) {
            for (int c = 0; c < 9; c++) {
                areaFieldsRow[c] = fields[r * 9 + c];
                areaFieldsColumn[c] = fields[r + c * 9];
                areaFieldsSquare[c] = fields[((r % 3) * 3 + (r / 3) * 27 + (c % 3) + (c / 3) * 9)];
            }

            this.rows[r] = new Area(r, this, areaFieldsRow);
            for (Field f : areaFieldsRow)
                f.setAreas(this.rows[r], null, null);

            this.columns[r] = new Area(r, this, areaFieldsColumn);
            for (Field f : areaFieldsColumn)
                f.setAreas(null, this.columns[r], null);

            this.squares[r] = new Area(r, this, areaFieldsSquare);
            for (Field f : areaFieldsSquare)
                f.setAreas(null, null, this.squares[r]);
        }

        for (Field f : fields) {
            int id = f.getId();
            f.setValue(valueArray[id]);
        }

        wasChanged = true;

    }

    public boolean solve() {
        int progress = 0;
        int progressCounter = 0;
        while (this.wasChanged) {
            this.skim('c', this.columns);
            this.skim('r', this.rows);
            this.skim('s', this.squares);

            for (Area r : this.rows)
                r.solve();
            for (Area c : this.columns)
                c.solve();
            for (Area s : this.squares)
                s.solve();

            if (progressCounter == 4)
                break;

            if (progress == this.progress())
                progressCounter++;
            else
                progressCounter = 0;


            progress = this.progress();
        }
        return isCorrect();
    }

    private void skim(char areaType, Area[] areas) {
        Note[] columnsNotes;
        columnsNotes = new Note[3];
        Note[] rowsNotes;
        rowsNotes = new Note[3];

        for (Area area : areas) {
            for (int a = 0; a < 3; a++) {
                rowsNotes[a] = new Note();
                rowsNotes[a].eraseMultipleNotes(0b111111111);

                rowsNotes[a] = Note.comparison('|',
                        rowsNotes[a], area.getField(3 * a).getNote());
                rowsNotes[a] = Note.comparison('|',
                        rowsNotes[a], area.getField(1 + 3 * a).getNote());
                rowsNotes[a] = Note.comparison('|',
                        rowsNotes[a], area.getField(2 + 3 * a).getNote());

                if (areaType != 's')
                    continue;

                columnsNotes[a] = new Note();
                columnsNotes[a].eraseMultipleNotes(0b111111111);

                columnsNotes[a] = Note.comparison('|',
                        columnsNotes[a], area.getField(a).getNote());
                columnsNotes[a] = Note.comparison('|',
                        columnsNotes[a], area.getField(3 + a).getNote());
                columnsNotes[a] = Note.comparison('|',
                        columnsNotes[a], area.getField(6 + a).getNote());
            }

            // look if there are some notes which appears only in one column or row.
            for (int note = 1; note < 10; note++) {
                for (int i = 0; i < 3; i++) {
                    if (rowsNotes[i].contains(note) &&
                            !(rowsNotes[(i + 1) % 3].contains(note) || rowsNotes[(i + 2) % 3].contains(note))) {
                        for (Field f : areas[((area.getId() + 1) % 9) % 3 + 3 * (area.getId() / 3)].consecutiveFields(i * 3, 3, 1))
                            f.eraseNote(note);
                        for (Field f : areas[((area.getId() + 2) % 9) % 3 + 3 * (area.getId() / 3)].consecutiveFields(i * 3, 3, 1))
                            f.eraseNote(note);
                    }

                    if (areaType != 's')
                        continue;

                    if (columnsNotes[i].contains(note) &&
                            !(columnsNotes[(i + 1) % 3].contains(note) || columnsNotes[(i + 2) % 3].contains(note))) {
                        for (Field f : areas[(area.getId() + 3) % 9].consecutiveFields(i, 3, 3))
                            f.eraseNote(note);
                        for (Field f : areas[(area.getId() + 6) % 9].consecutiveFields(i, 3, 3))
                            f.eraseNote(note);
                    }

                }
            }
        }
    }

    private boolean isCorrect() {
        for (Area r : this.rows)
            if (!r.isCorrect()) {
                System.out.println("Fehler bei Row " + r.getId());
                return false;
            }
        for (Area c : this.columns)
            if (!c.isCorrect()) {
                System.out.println("Fehler bei Column " + c.getId());
                return false;
            }
        for (Area s : this.squares)
            if (!s.isCorrect()) {
                System.out.println("Fehler bei Square " + s.getId());
                return false;
            }
        return true;
    }

    private int progress() {
        int sumValues = 0;
        for (Field f : fields)
            sumValues += f.getValue();
        return sumValues;
    }


    /**
     * Prints the game into the console like this:
     * #############
     * #000#000#000#
     * #000#000#000#
     * #000#000#000#
     * #############
     * #000#000#000#
     * #000#000#000#
     * #000#000#000#
     * #############
     * #000#000#000#
     * #000#000#000#
     * #000#000#000#
     * #############
     * <p>
     * Every '0' will be replaced with the value of the field. If the value of the field is 0, a single whitespace will
     * be printed.
     */
    public void printValues() {
        System.out.println("#############"); // Prints the first line.
        for (int i = 0; i < 9; i++) {
            System.out.println(rows[i].toString()); // Prints each Row of the game.

            if (i % 3 == 2)
                System.out.println("#############"); // After each third row, there is a separation line.

        }
        System.out.println();
    }

    /**
     * Prints the game into the console like this:
     * <p>
     * #############  #############  #############  #############  #############
     * #111#111#111#  #222#222#222#  #333#333#333#  #444#444#444#  #555#555#555#
     * #111#111#111#  #222#222#222#  #333#333#333#  #444#444#444#  #555#555#555#
     * #111#111#111#  #222#222#222#  #333#333#333#  #444#444#444#  #555#555#555#
     * #############  #############  #############  #############  #############
     * #111#111#111#  #222#222#222#  #333#333#333#  #444#444#444#  #555#555#555#
     * #111#111#111#  #222#222#222#  #333#333#333#  #444#444#444#  #555#555#555#
     * #111#111#111#  #222#222#222#  #333#333#333#  #444#444#444#  #555#555#555#
     * #############  #############  #############  #############  #############
     * #111#111#111#  #222#222#222#  #333#333#333#  #444#444#444#  #555#555#555#
     * #111#111#111#  #222#222#222#  #333#333#333#  #444#444#444#  #555#555#555#
     * #111#111#111#  #222#222#222#  #333#333#333#  #444#444#444#  #555#555#555#
     * #############  #############  #############  #############  #############
     * <p>
     * #############  #############  #############  #############
     * #666#666#666#  #777#777#777#  #888#888#888#  #999#999#999#
     * #666#666#666#  #777#777#777#  #888#888#888#  #999#999#999#
     * #666#666#666#  #777#777#777#  #888#888#888#  #999#999#999#
     * #666#666#666#  #777#777#777#  #888#888#888#  #999#999#999#
     * #666#666#666#  #777#777#777#  #888#888#888#  #999#999#999#
     * #666#666#666#  #777#777#777#  #888#888#888#  #999#999#999#
     * #666#666#666#  #777#777#777#  #888#888#888#  #999#999#999#
     * #666#666#666#  #777#777#777#  #888#888#888#  #999#999#999#
     * #666#666#666#  #777#777#777#  #888#888#888#  #999#999#999#
     * #############  #############  #############  #############
     * <p>
     * The numbers are only entered in the field if they are also present in the note of the field.
     * Otherwise, whitespace is output.
     */
    public void printNotes() {
        // First row
        // This is how a line looks like in the first row.
        String line = "#############  #############  #############  #############  #############";

        System.out.println(line); // Prints the first line of the first row.
        for (int r = 0; r < 9; r++) {
            for (int i = 0; i < 5; i++) {
                rows[r].printNote(i); // Prints each row with the respective notes of the fields
                System.out.print("  "); // Separates the rows with different notes
            }
            System.out.println();
            if (r % 3 == 2)
                System.out.println(line); // After each third row, there is a separation line.
        }
        System.out.println();

        // Second
        // This is how a line looks like in the second row.
        line = "#############  #############  #############  #############";

        System.out.println(line); // Prints the first line of the second row.
        for (int r = 0; r < 9; r++) {
            for (int i = 5; i < 9; i++) {
                rows[r].printNote(i); // Prints each row with the respective notes of the fields
                System.out.print("  "); // Separates the rows with different notes
            }
            System.out.println();
            if (r % 3 == 2)
                System.out.println(line); // After each third row, there is a separation line.
        }
        System.out.println();
    } // public void printNotes()

    @Override
    public void eraseNote(int note) {
        for (Field f : this.fields)
            f.eraseNote(note);
    }

    @Override
    public void eraseMultipleNotes(int[] notes) {
        for (int note : notes)
            eraseNote(note);
    }

    @Override
    public void eraseMultipleNotes(int notes) {
        for (int i = 0; i < 9; i++) {
            if ((notes & (1 << i)) > 0)
                eraseNote(i + 1);
        }
    }

    @Override
    public void wasChanged() {
        this.wasChanged = true;
    }

    public int[] sudokuAsArray() {
        for (Field field : fields)
            valueArray[field.getId()] = field.getValue();

        return valueArray;
    }
}
