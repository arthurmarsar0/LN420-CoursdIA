package projet1.gomoku.controllers.ai;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import projet1.gomoku.controllers.PlayerController;
import projet1.gomoku.gamecore.Coords;
import projet1.gomoku.gamecore.GomokuBoard;
import projet1.gomoku.gamecore.enums.Player;
import projet1.gomoku.gamecore.enums.TileState;


public class AI_Minimax extends PlayerController {

    private final int maxDepth;
    private final int winLen = 5;
    private final int vicinityRadius = 2; // limita geração de jogadas às casas próximas

    public AI_Minimax(int depth) {
        this.maxDepth = Math.max(1, depth);
    }

    @Override
    public Coords play(GomokuBoard board, Player player) {
        //WIN-IN-1
        Coords myWin = findImmediateWin(board, player);
        if (myWin != null) return myWin;

        //BLOCK-IN-1
        Player opp = switchPlayer(player);
        Coords block = findImmediateWin(board, opp);
        if (block != null) return block;

        // Minimax avec alpha-beta
        List<Coords> moves = generateCandidateMoves(board, vicinityRadius, player);
        if (moves.isEmpty()) { //In case generateCandidateMoves doesn't works properly
            return new Coords(board.getWidth() / 2, board.getHeight() / 2); //TODO: (test) Change for a random position 
        }

        int bestScore = Integer.MIN_VALUE;
        Coords bestMove = moves.get(0);

        for (Coords m : moves) {
            if (!isEmpty(board, m)) continue;
            GomokuBoard next = board.clone();
            set(next, m, toTile(player));
            int score = minimax(next, maxDepth - 1, Integer.MIN_VALUE, Integer.MAX_VALUE, switchPlayer(player), player);
            if (score > bestScore) {
                bestScore = score;
                bestMove = m;
            }
        }
        return bestMove;
    }

    /** Older versions failed to play immediate wins or loses, made that to secure that play and to minimize 
     * the response time, because of not needing MiniMax in situations that is obvious that it will lose
     * */
    private Coords findImmediateWin(GomokuBoard board, Player player) {
        TileState me = toTile(player);
        int w = board.getWidth(), h = board.getHeight();
        for (int y = 0; y < h; y++) {
            for (int x = 0; x < w; x++) {
                if (get(board, x, y) != TileState.Empty) continue;
                GomokuBoard tmp = board.clone();
                tmp.set(x, y, me);
                if (hasN(tmp, me, winLen)) {
                    return new Coords(x, y);
                }
            }
        }
        return null;
    }

    private int minimax(GomokuBoard board, int depth, int alpha, int beta, Player toMove, Player me) {
        Integer termScore = terminalScore(board, me);
        if (termScore != null) return termScore;
        if (depth == 0) return evaluate(board, me);

        List<Coords> moves = generateCandidateMoves(board, vicinityRadius, toMove);
        if (moves.isEmpty()) return 0;

        boolean maximizing = (toMove == me);
        if (maximizing) {
            int best = Integer.MIN_VALUE;
            for (Coords m : moves) {
                if (!isEmpty(board, m)) continue;
                GomokuBoard next = board.clone();
                set(next, m, toTile(toMove));
                int val = minimax(next, depth - 1, alpha, beta, switchPlayer(toMove), me);

                // update BEST n ALPHA
                if (val > best) best = val;
                if (val > alpha) alpha = val;

                // AlphaBeta pruning 
                if (beta <= alpha) break;
            }
            return best;
        } else {
            int best = Integer.MAX_VALUE;
            for (Coords m : moves) {
                if (!isEmpty(board, m)) continue;
                GomokuBoard next = board.clone();
                set(next, m, toTile(toMove));
                int val = minimax(next, depth - 1, alpha, beta, switchPlayer(toMove), me);

                // update BEST n BETA 
                if (val < best) best = val;
                if (val < beta) beta = val; //TODO: Check if that comparaisons is actually right

                // AlphaBeta pruning
                if (beta <= alpha) break;
            }
            return best;
        }
    }

    // 
    private List<Coords> generateCandidateMoves(GomokuBoard board, int radius, Player toMove) {
        int width = board.getWidth();
        int height = board.getHeight();
        boolean[][] near = new boolean[height][width];
        boolean anyStone = false;
        
        List<Coords> moves = new ArrayList<>();

        Coords w = findImmediateWin(board, toMove);
        if (w != null) { moves.add(w); return moves; }

        Coords b = findImmediateWin(board, switchPlayer(toMove));
        if (b != null) { moves.add(b); return moves; }

        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                if (get(board, x, y) != TileState.Empty) {
                    anyStone = true;
                    for (int dy = -radius; dy <= radius; dy++) {
                        for (int dx = -radius; dx <= radius; dx++) {
                            int nx = x + dx, ny = y + dy;
                            if (nx >= 0 && ny >= 0 && nx < width && ny < height) {
                                near[ny][nx] = true;
                            }
                        }
                    }
                }
            }
        }

        if (anyStone) {
            for (int y = 0; y < height; y++) {
                for (int x = 0; x < width; x++) {
                    if (near[y][x] && get(board, x, y) == TileState.Empty) {
                        moves.add(new Coords(x, y));
                    }
                }
            }
        } else {
            // Default Value
            moves.add(new Coords(width / 2, height / 2));
        }

        // move ordering for helping the pruning
        Collections.sort(moves, new Comparator<Coords>() {
            @Override
            public int compare(Coords a, Coords b) {
                int sa = quickMoveScore(board, a);
                int sb = quickMoveScore(board, b);
                return Integer.compare(sb, sa); 
            }
        });
        
        if (moves.size() > 40) {
            moves = moves.subList(0, 40);
        }
        
        return moves;
    }

    private int quickMoveScore(GomokuBoard board, Coords coord) {
    	int score = 0;

    	//Check for neighbors' density
    	for (int dy = -1; dy <= 1; dy++) {		
	        for (int dx = -1; dx <= 1; dx++) {
	            if (dx == 0 && dy == 0) continue;
	            int nx = coord.column + dx, ny = coord.row + dy;
	            if (nx >= 0 && ny >= 0 && nx < board.getWidth() && ny < board.getHeight()) {
	                if (get(board, nx, ny) != TileState.Empty) score++;
	            }
	        }
	    }
	
	    for (TileState who : new TileState[]{TileState.White, TileState.Black}) { //Check for winning moves
	        // make
	        TileState prev = get(board, coord);
	        board.set(coord.column, coord.row, who);
	        boolean win = hasN(board, who, winLen);
	        // unmake
	        board.set(coord.column, coord.row, prev);
	
	        if (win) score += 10000;
	    }
	
	    for (TileState who : new TileState[]{TileState.White, TileState.Black}) {
	        TileState prev = get(board, coord);
	        board.set(coord.column, coord.row, who);
	        int broken4 = countBroken(board, who, 4);
	        board.set(coord.column, coord.row, prev);
	        if (broken4 > 0) score += 60 * broken4;
	    }
	
	    return score;
	}


    //Heuristic evaluation - Called just in the end
    private int evaluate(GomokuBoard board, Player me) {
        Player opp = switchPlayer(me);

        // 
        if (hasN(board, toTile(me), winLen)) return 1_000_000;
        if (hasN(board, toTile(opp), winLen)) return -1_000_000;

        int s = 0;
        s += 10   * countOpen(board, toTile(me), 2);
        s += 180  * countOpen(board, toTile(me), 3);
        s += 1300 * countOpen(board, toTile(me), 4);
        s += 300  * countSemiOpen(board, toTile(me), 4);
        s += 50   * countSemiOpen(board, toTile(me), 3);
        s += 800  * countBroken(board, toTile(me), 4); //Agora conta padroes saltados X X _ X X
        s += 80   * countBroken(board, toTile(me), 3); // X _ X X

        s -= 1200 * countBroken(board, toTile(opp), 4);
        s -= 120  * countBroken(board, toTile(opp), 3);
        s -= 12   * countOpen(board, toTile(opp), 2);
        s -= 220  * countOpen(board, toTile(opp), 3);
        s -= 1500 * countOpen(board, toTile(opp), 4);
        s -= 400  * countSemiOpen(board, toTile(opp), 4);
        s -= 60   * countSemiOpen(board, toTile(opp), 3);

        return s;
    }

    private Integer terminalScore(GomokuBoard board, Player me) {
        if (hasN(board, toTile(me), winLen)) return 1_000_000;
        if (hasN(board, toTile(switchPlayer(me)), winLen)) return -1_000_000;
        if (isFull(board)) return 0;
        return null;
    }

    // Auxiliary functions

    private boolean isFull(GomokuBoard board) {
        for (int y = 0; y < board.getHeight(); y++) {
            for (int x = 0; x < board.getWidth(); x++) {
                if (get(board, x, y) == TileState.Empty) return false;
            }
        }
        return true;
    }

    private boolean hasN(GomokuBoard board, TileState who, int n) { 
        int w = board.getWidth(), h = board.getHeight();
        int[][] dirs = new int[][] { {1,0},{0,1},{1,1},{1,-1} };
        for (int y = 0; y < h; y++) {
            for (int x = 0; x < w; x++) {
                if (get(board, x, y) != who) continue;
                for (int[] d : dirs) {
                    int len = 1;
                    int nx = x + d[0], ny = y + d[1];
                    while (nx >= 0 && ny >= 0 && nx < w && ny < h && get(board, nx, ny) == who) {
                        len++;
                        if (len >= n) return true;
                        nx += d[0]; ny += d[1];
                    }
                }
            }
        }
        return false;
    }

    private int countOpen(GomokuBoard board, TileState who, int len) {
        // double opened sequence
        return countPatterns(board, who, len, true);
    }

    private int countSemiOpen(GomokuBoard board, TileState who, int len) {
        // single opened sequence
        return countPatterns(board, who, len, false);
    }

    private int countPatterns(GomokuBoard board, TileState who, int len, boolean requireTwoOpenEnds) {
    	/* TODO: Think of a logic that consider spaced sequences (e.g: W W _ W W) and give additional 
    	 * point in that cases (the example isn't counted as a sequence that can potentially end a game - Did it
    	 * */
        int w = board.getWidth(), h = board.getHeight();
        TileState opp = (who == TileState.White ? TileState.Black : TileState.White);
        int[][] dirs = new int[][] { {1,0},{0,1},{1,1},{1,-1} };
        int count = 0;

        for (int y = 0; y < h; y++) {
            for (int x = 0; x < w; x++) {
                for (int[] d : dirs) {
                    int cx = x, cy = y;
                    int stones = 0;
                    int empties = 0;
                    boolean blocked = false;

                    int steps = 0;
                    while (cx >= 0 && cy >= 0 && cx < w && cy < h && steps < 5) {
                        TileState t = get(board, cx, cy);
                        if (t == who) stones++;
                        else if (t == opp) { blocked = true; break; }
                        else empties++;
                        cx += d[0]; 
                        cy += d[1];
                        steps++;
                    }
                    if (blocked) continue;
                    if (stones == len && steps == 5 && empties >= (5 - len)) {
                        // Check sequence openness
                        int px = x - d[0], py = y - d[1];
                        int sx = cx, sy = cy;
                        boolean openFront = (px >= 0 && py >= 0 && px < w && py < h && get(board, px, py) == TileState.Empty);
                        boolean openBack  = (sx >= 0 && sy >= 0 && sx < w && sy < h && get(board, sx, sy) == TileState.Empty);
                        int openEnds = (openFront ? 1 : 0) + (openBack ? 1 : 0);
                        if ((requireTwoOpenEnds && openEnds == 2) || (!requireTwoOpenEnds && openEnds == 1)) {
                            count++;
                        }
                    }
                }
            }
        }
        return count;
    }
    
	 private int countBroken(GomokuBoard board, TileState who, int len) {
	     final int[][] DIRS = { {1,0}, {0,1}, {1,1}, {1,-1} };
	     final int w = board.getWidth();
	     final int h = board.getHeight();
	     final TileState opp = (who == TileState.White ? TileState.Black : TileState.White);
	
	     int count = 0;
	
	     for (int[] d : DIRS) {
	         final int dx = d[0], dy = d[1];
	
	         for (int y = 0; y < h; y++) {
	             for (int x = 0; x < w; x++) {
	
	                 int stones = 0, empties = 0;
	                 boolean blocked = false;
	                 int[] emptyPos = new int[5];
	                 int emptyCnt = 0;
	
	                 int cx = x, cy = y, steps = 0;
	                 while (cx >= 0 && cy >= 0 && cx < w && cy < h && steps < 5) {
	                     TileState t = board.get(cx, cy);
	                     if (t == who) {
	                         stones++;
	                     } else if (t == opp) {
	                         blocked = true; break; 
	                     } else {
	                         empties++;
	                         emptyPos[emptyCnt++] = steps; 
	                     }
	                     cx += dx; cy += dy; steps++;
	                 }
	                 if (blocked || steps < 5) continue;
	
	                 if (stones == len && empties == (5 - len)) {
	                     boolean hasInternalEmpty = false;
	                     for (int i = 0; i < emptyCnt; i++) {
	                         int pos = emptyPos[i];
	                         if (pos >= 1 && pos <= 3) { // empty in the middle
	                             hasInternalEmpty = true; break;
	                         }
	                     }
	                     if (hasInternalEmpty) count++;
	                 }
	             }
	         }
	     }
	     return count;
	 }


    private TileState get(GomokuBoard b, int x, int y) {
        return b.get(x, y);
    }
    private TileState get(GomokuBoard b, Coords c) {
        return b.get(c.column, c.row);
    }
    private void set(GomokuBoard b, Coords c, TileState v) {
        b.set(c.column, c.row, v);
    }

    private boolean isEmpty(GomokuBoard b, Coords c) {
        return get(b, c) == TileState.Empty;
    }

    private TileState toTile(Player p) {
        return (p == Player.White) ? TileState.White : TileState.Black;
    }

    private Player switchPlayer(Player p) {
        return (p == Player.White) ? Player.Black : Player.White;
    }
}
