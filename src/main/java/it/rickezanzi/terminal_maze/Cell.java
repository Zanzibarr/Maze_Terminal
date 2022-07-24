package it.rickezanzi.terminal_maze;

public class Cell {

    public static final char VISITED_SIGN = '-';

    private final int[] coords = new int[2];
    private char sign;
    private Cell right;
    private Cell down;
    private int birth;

    public Cell(int _row, int _col) {

        coords[0] = _row;
        coords[1] = _col;
        sign = ' ';

        right = null;
        down = null;

        birth = 0;

    }

    public int[] coords() { return coords; }

    public void setSign(char _sign) { sign = _sign; }

    public char sign() { return sign; }

    public void setRight(Cell _cell) { right = _cell; }

    public void setDown(Cell _cell) { down = _cell; }

    public Cell right() { return right; }

    public Cell down() { return down; }

    public void setBirth(int _birth) { birth = _birth; }

    public int birth() { return birth; }

}
