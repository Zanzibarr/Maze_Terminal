package it.rickezanzi.terminal_maze;

import java.util.*;

public class Maze {

    //lateral size of the maze
    private static int maze_size;
    //lateral size of the visual
    private static int visual_size;

    //maze
    private static Cell[][] maze;
    //current visualization
    private static int[] visual;

    private static final int SIZE_PER_UNIT = 33;
    private static int difficulty;
    //private static double looping_generator_probability;
    private static int visited_cells;
    private static int max_loops;
    private static int loop_count;
    private static int loop_requests;


    private static int[] creation_init;
    //current position of the player
    private static int[] player;
    //current position of the finish
    private static int[] finish;

    public static void main(String args[]) {

        Maze m;
        int diff = (new Scanner(System.in)).nextInt();
        //for (int i = 1; i < 10; i++) {
            double loops = 0;
            double mdn = 0;
            double act_diff = 0;
            //int rep = (new Scanner(System.in)).nextInt();
            //for (int j = 0; j < rep; j++) {
                m = new Maze();
                m.set_difficulty(diff);
                m.create();
                loops += loop_count;
                double act_mdn = medium_distance_neighborg();
                mdn += act_mdn;
                act_diff += (maze_size * act_mdn / (visual_size * loop_count));
            //}
            System.out.println("Difficulty: "+diff);
            System.out.println("Maze size: "+maze_size);
            System.out.println("Visual size: "+visual_size);
            System.out.println("Loops: "+loops/*/rep*/);
            System.out.println("MDN: "+mdn/*/rep*/);
            System.out.println("Act_Difficulty: "+act_diff/*/rep*/);
            System.out.println("Creation_init: "+((int)creation_init[0]*maze_size+creation_init[1])+"("+creation_init[0]+";"+creation_init[1]+")");
            System.out.println();

        //}
        m.show();
        System.out.println(find_path(maze[0][0], maze[maze_size-1][maze_size-1]));
        /*Scanner in = new Scanner(System.in);
        while(true) {
            m.show_window();
            System.out.println("move: ");
            m.move_player(in.next().charAt(0));
        }*/

    }

    //region creation
    public void create() {

        maze_construction();

        player = new int[]{0,0};
        visual = new int[]{0,0};

    }

    private void maze_initialization() {

        visited_cells = 0;

        maze_size = difficulty * 33;
        maze = new Cell[maze_size][maze_size];

        for (int i = 0; i < maze_size; i++)
            for (int j = 0; j < maze_size; j++)
                maze[i][j] = new Cell(i, j);

        visual_size = difficulty * 22;
        max_loops = difficulty * 10;
        loop_requests = 0;

    }

    private void maze_construction() {

        maze_initialization();

        loop_count = 0;

        Stack<Cell> list = new Stack<>();
        Random rand = new Random();
        boolean moving_forward = true;
        int row = rand.nextInt(1, maze_size - 1);
        int col = rand.nextInt(1, maze_size - 1);
        Cell cell = maze[row][col];
        creation_init = new int[]{row, col};

        list.add(cell);

        while(!list.isEmpty()) {
            cell = list.peek();
            ArrayList<Cell> neighborgs = cell_neighbours(cell, false);
            if (neighborgs.isEmpty()) {
                if (moving_forward) {
                    cell.setSign(Cell.VISITED_SIGN);
                    create_loop(cell);
                }
                moving_forward = false;
                list.pop();
            } else {
                Cell next_cell = neighborgs.get(rand.nextInt(neighborgs.size()));
                cell.setSign(Cell.VISITED_SIGN);
                next_cell.setBirth(cell.birth() + 1);
                moving_forward = true;
                connect_cells(cell, next_cell);
                list.push(next_cell);
                visited_cells++;
            }
        }

    }
    //endregion

    //region movements
    public void move_player(char _direction) {

        int[] new_coords = new int[2];
        new_coords[0] = player[0];
        new_coords[1] = player[1];

        switch (_direction) {
            case 'w' -> new_coords[0] = player[0] - 1;
            case 's' -> new_coords[0] = player[0] + 1;
            case 'a' -> new_coords[1] = player[1] - 1;
            case 'd' -> new_coords[1] = player[1] + 1;
        }

        if (check_coords(new_coords) && check_connection(maze[player[0]][player[1]], maze[new_coords[0]][new_coords[1]]))
            player = new_coords;

        player_in_sight();

    }

    private void player_in_sight() {

        if (player[0] < visual[0] || player[0] >= visual[0] + visual_size || player[1] < visual[1] || player[1] >= visual[1] + visual_size) {
            visual[0] = Math.min(Math.max(0, player[0] - (visual_size / 2)), maze_size - visual_size);
            visual[1] = Math.min(Math.max(0, player[1] - (visual_size / 2)), maze_size - visual_size);
        }

    }
    //endregion

    //region custom tweaks
    public void set_difficulty(int _difficulty) { difficulty = _difficulty; }

    /*public void set_size(int _size) {

        custom = true;
        if (_size > 1.3 * difficulty * SIZE_PER_UNIT) _size = (int) (1.3 * difficulty * SIZE_PER_UNIT);
        if (_size < (difficulty - .5) * SIZE_PER_UNIT) _size = (int) ((difficulty - .5) * SIZE_PER_UNIT);
        maze_size = _size;

    }*/
    //endregion

    //region visuals
    public void show_window() {

        if(!check_coords(visual) || visual[0] * maze_size + visual[1] > (maze_size - visual_size) * maze_size + (maze_size - visual_size)) return;

        if (visual[0] == 0) {
            for (int i = 0; i < visual_size - 1; i++)
                System.out.print("__");
            System.out.println("___");
        }

        for (int i = visual[0]; i < visual[0] + visual_size; i++)  {
            if (visual[1] == 0) System.out.print("|");
            for (int j = visual[1]; j < visual[1] + visual_size; j++) {
                if (check_player_position(new int[]{i, j})) System.out.print("O");
                else System.out.print((maze[i][j].down() == null) ? "_" : " ");  //posizione
                System.out.print((maze[i][j].right() == null) ? "|" : "_"); //alla destra
            }
            System.out.println();
        }

    }

    public void show() {

        for (int i = 0; i < maze_size - 1; i++)
            System.out.print("__");

        System.out.println("___");

        for (int i = 0; i < maze_size; i++) {
            System.out.print("|");
            for (int j = 0; j < maze_size; j++) {
                System.out.print((maze[i][j].down() == null) ? "_" : " ");
                System.out.print((maze[i][j].right() == null) ? "|" : "_");
            }
            System.out.println();
        }

        System.out.println("\n");

    }
    //endregion

    //region utility
    private static ArrayList<Cell> cell_neighbours(Cell _cell, boolean visited_included) {

        int[] coords = _cell.coords();
        ArrayList<Cell> neighbours = new ArrayList<>();

        for (int i = -1; i <= 1; i++)
            for (int j = -1; j <= 1; j++) {

                if (i == 0 && j == 0 || Math.abs(i) == 1 && Math.abs(j) == 1) continue;

                int[] new_coords = { coords[0] + i, coords[1] + j };

                if (check_coords(new_coords) && (visited_included || maze[new_coords[0]][new_coords[1]].sign() != Cell.VISITED_SIGN))
                    neighbours.add(maze[new_coords[0]][new_coords[1]]);

            }

        return neighbours;

    }

    private void connect_cells(Cell _c1, Cell _c2) {

        int[] coords1 = _c1.coords();
        int[] coords2 = _c2.coords();

        boolean horizontal = coords1[0] == coords2[0];

        if (coords1[0] * maze_size + coords1[1] < coords2[0] * maze_size + coords2[1]) {
            if (horizontal) _c1.setRight(_c2);
            else _c1.setDown(_c2);
        } else {
            if (horizontal) _c2.setRight(_c1);
            else _c2.setDown(_c1);
        }

    }

    private static boolean check_connection(Cell _c1, Cell _c2) {

        return (_c1.right() == _c2 || _c1.down() == _c2 || _c2.right() == _c1 || _c2.down() == _c1);

    }

    private void create_loop(Cell _cell) {

        loop_requests++;

        int presumed_loops = (int) Math.pow(difficulty, 2) * 110;

        if (loop_count >= max_loops || loop_requests < presumed_loops * (loop_count + 1) / (loop_count + 2))
            return;

        loop_count++;

        ArrayList<Cell> neighbours = cell_neighbours(_cell, true);
        int[] distances = new int[neighbours.size()];
        int max_distance_index = 0;
        int max_distance = 0;
        for (int i = 0; i < neighbours.size(); i++) {
            distances[i] = neighbours.get(i).birth() - _cell.birth();
            if (distances[i] > max_distance) {
                max_distance_index = i;
                max_distance = distances[i];
            }
        }

        connect_cells(_cell, neighbours.get(max_distance_index));

    }

    private static double medium_distance_neighborg() {

        int sum = 0;
        int counter = 0;

        for (int i = 0; i < maze_size - 1; i++)
            for (int j = 0; j < maze_size - 1; j++) {
                if (maze[i][j].right() == null) {
                    sum += find_path(maze[i][j], maze[i][j+1]);
                    counter++;
                }
                if (maze[i][j].down() == null) {
                    sum += find_path(maze[i][j], maze[i+1][j]);
                    counter++;
                }
            }

        return (double) sum / counter;

    }

    private static int find_path(Cell _start, Cell _end) {

        for (int i = 0; i < maze_size; i++)
            for (int j = 0; j < maze_size; j++) {
                maze[i][j].setSign(' ');
                maze[i][j].setBirth(0);
            }

        Queue<Map.Entry<Cell, Integer>> to_visit = new PriorityQueue<>(Comparator.comparingInt(Map.Entry::getValue));
        to_visit.add(new AbstractMap.SimpleEntry<>(_start, hamming(_start, _end)));
        Cell cell = to_visit.peek().getKey();
        boolean quit_cond = false;

        while(!quit_cond && !to_visit.isEmpty()) {
            cell = to_visit.poll().getKey();
            ArrayList<Cell> neighbours = cell_neighbours(cell, false);
            Cell finalCell = cell;
            neighbours.removeIf(n -> !check_connection(finalCell, n));
            for (Cell c : neighbours) {
                c.setSign(Cell.VISITED_SIGN);
                c.setBirth(cell.birth() + 1);
                if (c.equals(_end)) quit_cond = true;
                to_visit.add(new AbstractMap.SimpleEntry<>(c, hamming(c, _end)));
            }
        }

        return cell.birth();

    }

    private static int hamming(Cell _start, Cell _end) {

        int[] coords_start = _start.coords();
        int[] coords_end = _end.coords();

        return (Math.abs(coords_start[0] - coords_end[0]) + Math.abs(coords_start[1] - coords_end[1]));

    }

    private boolean check_player_position(int[] _coords) {

        return player[0] == _coords[0] && player[1] == _coords[1];

    }

    private static boolean check_coords(int[] _coords) {

        return _coords[0] >= 0 && _coords[0] < maze_size && _coords[1] >= 0 && _coords[1] < maze_size;

    }
    //endregion

}
