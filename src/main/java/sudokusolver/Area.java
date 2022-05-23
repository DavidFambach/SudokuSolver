package sudokusolver;

public class Area implements SudokuObject {
    private final int id;
    private final Field[] fields;
    private boolean wasChanged;
    private final Sudoku game;

    public Area(int id, Sudoku game, Field[] fields) {
        this.id = id;
        this.game = game;
        this.fields = fields.clone();
        this.wasChanged = true;
    } // public Area (int id, Sudoku game, Field[] fields)

    public int getId() {
        return this.id;
    } // public int getId()

    /**
     * @return Returns the area as a String of the form: #000#000#000#
     */
    public String toString() {
        String returnString = "#";

        for (int i = 0; i < 9; i++) {
            returnString += fields[i].toString();

            if (i % 3 == 2)
                returnString += "#";
        }

        return returnString;
    } // public String toString()

    /**
     * Outputs a row. The note is then in the fields if it is available in this field.
     * Otherwise, a whitespace is output.
     * The output has the form #000#000#000#
     *
     * @param note The note for which the row should be printed.
     */
    public void printNote(int note) {
        System.out.print("#");

        for (int i = 0; i < 9; i++) {
            fields[i].printNote(note + 1);

            if (i % 3 == 2)
                System.out.print("#");
        }
    } // public void printNote(int note)

    /**
     * @param value The number to be searched for.
     * @return returns whether the number occurs in the area or not.
     */
    public boolean contains(int value) {
        for (Field f : fields) {
            if (f.getValue() == value)
                return true;
        }
        return false;
    } // public boolean contains(int value)

    /**
     * @return returns for each number whether it occurs in the area.
     */
    public boolean[] contains() {
        boolean[] r = new boolean[9]; // return array
        for (int i = 1; i < 10; i++) // Go through all numbers
            r[i - 1] = contains(i);    // and note whether they occur in the area.
        return r;
    } // public boolean[] contains()

    @Override
    public void eraseNote(int note) {
        this.wasChanged();

        for (Field f : fields)
            f.eraseNote(note);
    } // public void eraseNote(int note)

    @Override
    public void eraseMultipleNotes(int[] notes) {
        for (int note : notes)
            eraseNote(note);
    } // public void eraseMultipleNotes(int[] notes)

    /**
     * @param notes Binary number. Every 1 in the nth place deletes the n+1st note.
     */
    @Override
    public void eraseMultipleNotes(int notes) {
        if (Integer.highestOneBit(notes) > 9){
            System.out.println("fehler");

        }
        for (int i = 0; i < 9; i++)
            if ((notes & (1 << i)) > 0)
                eraseNote(i + 1);
    } // public void eraseMultipleNotes(int notes)

    public void eliminateLonelyNotice() {
        int[] notes = new int[]{0, 0, 0, 0, 0, 0, 0, 0, 0};

        for (Field f : fields)
            for (int i = 0; i < 9; i++)
                if (f.getNote().getNotes()[i])
                    notes[i]++;

        for (int i = 0; i < 9; i++)
            if (notes[i] == 1)
                for (Field f : fields)
                    if (f.getNote().contains(i + 1))
                        f.setValue(i + 1);
    }

    public void solve() {
        if (!this.wasChanged)
            return;
        this.wasChanged = false;

        //nakedPairs();
        eliminateLonelyNotice();
        for (Field f : this.fields)
            f.solve();
    }

    private void nakedPairs() {
        int[] numberOfNotesInArea = new int[]{0, 0, 0, 0, 0, 0, 0, 0, 0};
        Field[][] occurrenceOfNotes = new Field[9][9];
        boolean companionsJustOccurWithNote;
        Note[] companions = new Note[9];
        for (int i = 0; i < 9; i++)
            companions[i] = new Note();


        Note antiNote;

        for (Field field : this.fields) {
            for (int note : field.getNote().getUnerasedNotes())
                if (note != 0) {
                    numberOfNotesInArea[note - 1]++;
                    occurrenceOfNotes[note - 1][numberOfNotesInArea[note - 1] - 1] = field;
                    companions[note - 1] = Note.comparison('&', companions[note - 1], field.getNote());
                }
        }

        for (int note = 0; note < 9; note++) {
            if (numberOfNotesInArea[note] != companions[note].numberOfNotes()) {
                continue;
            }
            companionsJustOccurWithNote = true;
            for (int c : companions[note].getUnerasedNotes())
                if (c != 0 && numberOfNotesInArea[c - 1] != numberOfNotesInArea[note]) {
                    companionsJustOccurWithNote = false;
                    break;
                }

            if (!companionsJustOccurWithNote)
                continue;

            antiNote = new Note();
            for (Field field : occurrenceOfNotes[note])
                if (field != null) {
                    antiNote = Note.comparison('^', antiNote, companions[note]);
                    field.eraseMultipleNotes(antiNote.getUnerasedNotes());
                }

        }
    }

    public boolean isCorrect() {
        boolean[] contains = contains();
        for (boolean b : contains)
            if (!b)
                return false;
        return true;
    }

    public void wasChanged() {
        this.wasChanged = true;
        this.game.wasChanged();
    }

    public Field getField(int field) {
        return fields[field];
    }

    public Field[] consecutiveFields(int start, int size, int distance) {
        Field[] returnFields = new Field[size];
        int pointer = 0;
        for (int i = start; pointer < size; i = (i + distance) % 9, pointer++)
            returnFields[pointer] = this.fields[i];

        return returnFields;
    }
}
