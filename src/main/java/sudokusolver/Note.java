package sudokusolver;

public class Note implements SudokuObject {
    private final boolean[] notes;

    public Note() {
        this.notes = new boolean[]{true, true, true, true, true, true, true, true, true};
    }


    public void eraseMultipleNotes(int[] notesToErase) {
        for (int note : notesToErase)
            if (note > 0 && note < 10)
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
        //
    }

    public boolean[] getNotes() {
        return notes;
    }

    /**
     * @return returns an integer array witch contains every unerased note.
     */
    public int[] getUnerasedNotes() {
        int[] r = new int[this.numberOfNotes()];
        int arrayPointer = 0;
        int noteAsInt = 1;

        for (boolean n : this.notes) {
            if (n) {
                r[arrayPointer] = noteAsInt;
                arrayPointer++;
            }
            noteAsInt++;
        }
        return r;
    } // public int[] getUnerasedNotes()

    /**
     * @param i the note to be erased.
     */
    public void eraseNote(int i) {
        this.notes[i - 1] = false;
    } // public void eraseNote(int i)

    /**
     * @param note
     * @return
     */
    public boolean contains(int note) {
        return (this.notes[note - 1]);
    } // public boolean contains(int note)

    /**
     * @return returns the number of notes in a field.
     */
    public int numberOfNotes() {
        int quantity = 0;
        for (boolean n : this.notes)
            if (n)
                quantity++;
        return quantity;
    } // public int numberOfNotes()

    public static Note comparison(char method, Note note1, Note note2) {
        Note returnNote = new Note();

        for (int i = 0; i < 9; i++)
            switch (method) {
                case '&':
                    if (!(note1.getNotes()[i] && note2.getNotes()[i]))
                        returnNote.eraseNote(i + 1);
                    break;

                case '|':
                    if (!(note1.getNotes()[i] || note2.getNotes()[i]))
                        returnNote.eraseNote(i + 1);
                    break;

                case '^':
                    if (note1.getNotes()[i] == note2.getNotes()[i])
                        returnNote.eraseNote(i + 1);
                    break;
                default:
                    throw new IllegalStateException("Unexpected value: " + method);
            }
        return returnNote;
    }
}
