package connectsix;

//OOP to hold cells' states
//9 empty places
//1- places that occupied by player
//2- places that occupied by computer
//0 high water mark for each column
public class myCell {
//Fields
    private int cellState;

//Constructor
    public myCell() {
	cellState = 0; // It is used for High Water Mark
    }

    // Methods
    public void setMyCellState(int newCellState) {
	cellState = newCellState;
    }

    public int getMyCellState() {
	return cellState;
    }

}
