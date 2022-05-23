package sudokusolver;

public class Field implements SudokuObject{
    private final int id;
    private int value;
    private final Note note;
    private boolean wasChanged;

    private Area column;
    private Area row;
    private Area square;

    public Field(int id) {
        this.id = id;
        this.value = 0;
        this.note = new Note();
        this.wasChanged = true;
    } // public Field (int id)

    public int getValue() {
        return this.value;
    }

    public void setValue(int value) {
        // Check whether the value is already set.
        if (this.value != 0)
            return;

        // Check whether the value to be set is in the correct value range.
        if (value < 1 || value > 10)
            return;

        this.value = value; // set the value.
        this.note.eraseMultipleNotes(new int[]{1, 2, 3, 4, 5, 6, 7, 8, 9}); // erase every note of the field.
        if (this.row != null)
            this.row.eraseNote(value);
        if (this.column != null)
            this.column.eraseNote(value);
        if (this.square != null)
            this.square.eraseNote(value);

        this.wasChanged();
    } // public void setValue(int value)

    public void wasChanged() {
        this.wasChanged = true;
        if (this.row != null)
            this.row.wasChanged();
        if (this.column != null)
            this.column.wasChanged();
        if (this.square != null)
            this.square.wasChanged();
    }

    public int getId() {
        return this.id;
    }

    public void setAreas(Area row, Area column, Area square) {
        if (this.row == null && row != null)
            this.row = row;

        if (this.column == null && column != null)
            this.column = column;

        if (this.square == null && square != null)
            this.square = square;
    }

    /**
     * @return Returns the value of the field as a String. If the value is 0, the method returns a single whitespace.
     */
    public String toString() {
        if (this.getValue() == 0)
            return " ";

        return Integer.toString(this.getValue());
    } // public String toString()

    /**
     * If the note you are looking for still exists, it will be printed in the console, otherwise a
     * whitespace will be printed.
     *
     * @param note The note you are looking for.
     */
    public void printNote(int note) {
        if (this.note.contains(note))
            System.out.print(note);
        else
            System.out.print(" ");
    } // public void printNote (int note)

    public void eraseNote(int note) {
        this.note.eraseNote(note);
        this.wasChanged();
    }

    public void eraseMultipleNotes(int[] note) {
        this.note.eraseMultipleNotes(note);
        this.wasChanged();
    }

    @Override
    public void eraseMultipleNotes(int notes) {
        if (Integer.highestOneBit(notes) > 9) {
            System.err.println("Invalid Value");
            return;
        }
        for (int i = 0; i < 9; i++)
            if ((notes & (1 << i)) > 0)
                eraseNote(i + 1);
    }

    public Note getNote(){
        return this.note;
    }

    public int[] getUnerasedNotes (){
        return this.note.getUnerasedNotes();
    }

    public void eraseLonelyNotes(){
        if(!this.wasChanged)
            return;
        this.wasChanged = false;

        if(this.note.numberOfNotes() == 1)
            this.setValue(this.note.getUnerasedNotes()[0]);
    }

    public void solve() {
        wasChanged = false;
        this.eraseLonelyNotes();
    }

}
