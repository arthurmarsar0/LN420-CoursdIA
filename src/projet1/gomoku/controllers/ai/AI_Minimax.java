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

/**
 * IA Minimax com poda alpha-beta para Gomoku 15x15.
 * Melhorias:
 *  - Pré-cheque de vitória imediata (win-in-1)
 *  - Pré-cheque de bloqueio imediato (block-in-1) se o adversário tiver win-in-1
 *  - Ordenação de lances (move ordering) por heurística rápida
 *  - Busca com alpha-beta
 */
public class AI_Minimax extends PlayerController {

    private final int maxDepth;
    private final int winLen = 5;
    private final int vicinityRadius = 2; // limita geração de jogadas às casas próximas

    public AI_Minimax(int depth) {
        this.maxDepth = Math.max(1, depth);
    }

    @Override
    public Coords play(GomokuBoard board, Player player) {
        // 0) WIN-IN-1: se eu tenho um lance que fecha 5, jogo imediatamente
        Coords myWin = findImmediateWin(board, player);
        if (myWin != null) return myWin;

        // 1) BLOCK-IN-1: se o adversário tem um win-in-1, bloquear imediatamente
        Player opp = switchPlayer(player);
        Coords block = findImmediateWin(board, opp);
        if (block != null) return block;

        // 2) Minimax com alpha-beta
        List<Coords> moves = generateCandidateMoves(board, vicinityRadius);
        if (moves.isEmpty()) {
            // fallback: centro
            return new Coords(board.getWidth() / 2, board.getHeight() / 2);
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

    /** Procura um lance imediato que feche 5 para o jogador p. Retorna a coordenada ou null. */
    private Coords findImmediateWin(GomokuBoard board, Player p) {
        TileState me = toTile(p);
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
        Integer t = terminalScore(board, me);
        if (t != null) return t;
        if (depth == 0) return evaluate(board, me);

        List<Coords> moves = generateCandidateMoves(board, vicinityRadius);
        if (moves.isEmpty()) return 0;

        boolean maximizing = (toMove == me);
        if (maximizing) {
            int best = Integer.MIN_VALUE;
            for (Coords m : moves) {
                if (!isEmpty(board, m)) continue;
                GomokuBoard next = board.clone();
                set(next, m, toTile(toMove));
                int val = minimax(next, depth - 1, alpha, beta, switchPlayer(toMove), me);

                // >>> Atualiza melhor valor e ALPHA (limite inferior do MAX)
                if (val > best) best = val;
                if (val > alpha) alpha = val;

                // >>> PODA ALPHA-BETA: se beta <= alpha, não precisa explorar o restante
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

                // >>> Atualiza melhor valor e BETA (limite superior do MIN)
                if (val < best) best = val;
                if (val < beta) beta = val;

                // >>> PODA ALPHA-BETA: se beta <= alpha, não precisa explorar o restante
                if (beta <= alpha) break;
            }
            return best;
        }
    }

    // ===== Geração de jogadas próximas às peças existentes =====
    private List<Coords> generateCandidateMoves(GomokuBoard board, int radius) {
        int w = board.getWidth();
        int h = board.getHeight();
        boolean[][] near = new boolean[h][w];
        boolean anyStone = false;

        for (int y = 0; y < h; y++) {
            for (int x = 0; x < w; x++) {
                if (get(board, x, y) != TileState.Empty) {
                    anyStone = true;
                    for (int dy = -radius; dy <= radius; dy++) {
                        for (int dx = -radius; dx <= radius; dx++) {
                            int nx = x + dx, ny = y + dy;
                            if (nx >= 0 && ny >= 0 && nx < w && ny < h) {
                                near[ny][nx] = true;
                            }
                        }
                    }
                }
            }
        }

        List<Coords> moves = new ArrayList<>();
        if (anyStone) {
            for (int y = 0; y < h; y++) {
                for (int x = 0; x < w; x++) {
                    if (near[y][x] && get(board, x, y) == TileState.Empty) {
                        moves.add(new Coords(x, y));
                    }
                }
            }
        } else {
            // Primeira jogada: jogar no centro
            moves.add(new Coords(w / 2, h / 2));
        }

        // Ordenação por heurística rápida (move ordering) para melhorar a poda
        Collections.sort(moves, new Comparator<Coords>() {
            @Override
            public int compare(Coords a, Coords b) {
                int sa = quickMoveScore(board, a);
                int sb = quickMoveScore(board, b);
                return Integer.compare(sb, sa); // desc
            }
        });
        return moves;
    }

    private int quickMoveScore(GomokuBoard board, Coords c) {
        // Heurística barata: número de vizinhos ocupados + grande bônus se a jogada fecha 5 (pra qualquer cor)
        int score = 0;
        for (int dy = -1; dy <= 1; dy++) {
            for (int dx = -1; dx <= 1; dx++) {
                if (dx == 0 && dy == 0) continue;
                int nx = c.column + dx, ny = c.row + dy;
                if (nx >= 0 && ny >= 0 && nx < board.getWidth() && ny < board.getHeight()) {
                    if (get(board, nx, ny) != TileState.Empty) score++;
                }
            }
        }
        // bônus forte se a jogada fecha 5 (prioriza táticas imediatas na ordenação)
        for (TileState who : new TileState[]{TileState.White, TileState.Black}) {
            GomokuBoard tmp = board.clone();
            tmp.set(c.column, c.row, who);
            if (hasN(tmp, who, winLen)) score += 10_000;
        }
        return score;
    }

    // ===== Avaliação Heurística =====
    private int evaluate(GomokuBoard board, Player me) {
        Player opp = switchPlayer(me);

        // vitória/derrota imediata valem muito
        if (hasN(board, toTile(me), winLen)) return 1_000_000;
        if (hasN(board, toTile(opp), winLen)) return -1_000_000;

        int s = 0;
        s += 10   * countOpen(board, toTile(me), 2);
        s += 80   * countOpen(board, toTile(me), 3);
        s += 800  * countOpen(board, toTile(me), 4);
        s += 300  * countSemiOpen(board, toTile(me), 4);
        s += 50   * countSemiOpen(board, toTile(me), 3);

        s -= 12   * countOpen(board, toTile(opp), 2);
        s -= 100  * countOpen(board, toTile(opp), 3);
        s -= 1200 * countOpen(board, toTile(opp), 4);
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

    // ===== Varreduras e utilitários de tabuleiro =====

    private boolean isFull(GomokuBoard board) {
        for (int y = 0; y < board.getHeight(); y++) {
            for (int x = 0; x < board.getWidth(); x++) {
                if (get(board, x, y) == TileState.Empty) return false;
            }
        }
        return true;
    }

    private boolean hasN(GomokuBoard board, TileState who, int n) { //Para checar se existem n peças numa direção
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
        // conta janelas de tamanho 5 com 'len' peças de 'who', 0 do oponente e DUAS pontas abertas
        return countPatterns(board, who, len, true);
    }

    private int countSemiOpen(GomokuBoard board, TileState who, int len) {
        // como acima, mas aceita apenas UMA ponta aberta
        return countPatterns(board, who, len, false);
    }

    private int countPatterns(GomokuBoard board, TileState who, int len, boolean requireTwoOpenEnds) {
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
                        cx += d[0]; cy += d[1];
                        steps++;
                    }
                    if (blocked) continue;
                    if (stones == len && steps == 5 && empties >= (5 - len)) {
                        // verificar pontas abertas (células antes e depois da janela)
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

    // ===== Acesso seguro ao tabuleiro =====

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
