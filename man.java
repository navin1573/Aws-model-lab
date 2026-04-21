import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

class main {
    private static final char WALL = '#';
    private static final char PATH = ' ';
    private static final char START = 'S';
    private static final char END = 'E';
    private static final char SOLUTION = '.';

    static class Maze {
        private final int rows;
        private final int cols;
        private final char[][] grid;
        private final Random random = new Random();

        Maze(int logicalRows, int logicalCols) {
            // Use odd dimensions for wall/cell layout
            this.rows = logicalRows * 2 + 1;
            this.cols = logicalCols * 2 + 1;
            this.grid = new char[rows][cols];
            fill(WALL);
            generate();
            placeStartAndEnd();
        }

        private void fill(char c) {
            for (int r = 0; r < rows; r++) {
                for (int col = 0; col < cols; col++) {
                    grid[r][col] = c;
                }
            }
        }

        private void generate() {
            boolean[][] visited = new boolean[rows][cols];
            carve(1, 1, visited);
        }

        private void carve(int r, int c, boolean[][] visited) {
            visited[r][c] = true;
            grid[r][c] = PATH;

            List<int[]> dirs = new ArrayList<>();
            dirs.add(new int[]{-2, 0}); // up
            dirs.add(new int[]{2, 0});  // down
            dirs.add(new int[]{0, -2}); // left
            dirs.add(new int[]{0, 2});  // right
            Collections.shuffle(dirs, random);

            for (int[] d : dirs) {
                int nr = r + d[0];
                int nc = c + d[1];

                if (isInside(nr, nc) && !visited[nr][nc]) {
                    // Carve wall between current and next cell
                    grid[r + d[0] / 2][c + d[1] / 2] = PATH;
                    carve(nr, nc, visited);
                }
            }
        }

        private boolean isInside(int r, int c) {
            return r > 0 && r < rows - 1 && c > 0 && c < cols - 1;
        }

        private void placeStartAndEnd() {
            grid[1][1] = START;
            grid[rows - 2][cols - 2] = END;
        }

        boolean solve() {
            boolean[][] visited = new boolean[rows][cols];
            return dfs(1, 1, visited);
        }

        private boolean dfs(int r, int c, boolean[][] visited) {
            if (!isInside(r, c) && !(r == 1 && c == 1) && !(r == rows - 2 && c == cols - 2)) {
                return false;
            }
            if (visited[r][c]) return false;
            if (grid[r][c] == WALL) return false;

            if (grid[r][c] == END) return true;

            visited[r][c] = true;

            int[][] dirs = {{-1, 0}, {1, 0}, {0, -1}, {0, 1}};
            for (int[] d : dirs) {
                int nr = r + d[0];
                int nc = c + d[1];
                if (nr < 0 || nr >= rows || nc < 0 || nc >= cols) continue;

                if (dfs(nr, nc, visited)) {
                    if (grid[r][c] == PATH) {
                        grid[r][c] = SOLUTION;
                    }
                    return true;
                }
            }
            return false;
        }

        void print() {
            for (char[] row : grid) {
                System.out.println(row);
            }
        }
    }

    public static void main(String[] args) {
        int logicalRows = 10;
        int logicalCols = 20;

        if (args.length == 2) {
            try {
                logicalRows = Math.max(2, Integer.parseInt(args[0]));
                logicalCols = Math.max(2, Integer.parseInt(args[1]));
            } catch (NumberFormatException ignored) {
                System.out.println("Invalid args. Using defaults: 10 20");
            }
        }

        Maze maze = new Maze(logicalRows, logicalCols);

        System.out.println("Generated Maze:");
        maze.print();

        if (maze.solve()) {
            System.out.println("\nSolved Maze:");
            maze.print();
        } else {
            System.out.println("\nNo solution found.");
        }
    }
}
