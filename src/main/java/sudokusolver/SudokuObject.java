package sudokusolver;

public interface SudokuObject {

    void eraseNote(int note);
    void eraseMultipleNotes(int[] notes);
    void eraseMultipleNotes(int notes);
    void wasChanged();
}
