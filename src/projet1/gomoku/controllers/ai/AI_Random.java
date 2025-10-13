package projet1.gomoku.controllers.ai;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Stream;
import java.util.*;
import java.util.stream.Stream;
//import java.util.Random;

import projet1.gomoku.controllers.PlayerController;
import projet1.gomoku.gamecore.Coords;
import projet1.gomoku.gamecore.GomokuBoard;
import projet1.gomoku.gamecore.enums.Player;
import projet1.gomoku.gamecore.enums.TileState;
import projet1.gomoku.gamecore.enums.WinnerState;

/**Représente un IA qui cherche les coups en se positionnant sur chaque case, puis en vérifiant le contenu des 4 cases autour dans les 8 directions */
public class AI_Random extends PlayerController {
	
	private int nMinMax;
	private static final int WIN_SCORE = 1_000_000; // score para vitórias/derrotas
    private static final int DRAW_SCORE = 0;

    public AI_Random(int minimaxDepth){
        super();
        this.nMinMax = minimaxDepth;
    }

    public AI_Random(){
        super();
        this.nMinMax = 2;
    }

    public int evaluateBoard(GomokuBoard board, Player player){
        /* Il y a sans doute des choses à modifier ici*/
    
    	TileState playerCellState = player == Player.White ? TileState.White : TileState.Black;
    	TileState adversaryCellState = player == Player.White ? TileState.Black : TileState.White;
    	
    	int score = 0;
//    	Random random = new Random();
    	
    	/*Test 1, coupes aléatoires  -- OK*/
//       	score = random.nextInt(100);
       	
       	/*Test 2, toujours a la 8eme Column -- OK
       	
    	Coords currentCellCoords = new Coords();
    	for (currentCellCoords.row = 0; currentCellCoords.row < GomokuBoard.size; currentCellCoords.row++){
            for (currentCellCoords.column = 0; currentCellCoords.column < GomokuBoard.size; currentCellCoords.column++){
                if (board.get(currentCellCoords) == TileState.Black && currentCellCoords.column == 8 ){ // Si la case est vide
                	score += 10;
                }
            }
        }
    	*/
    	
    	/*Test 3, Coupes à la gauche du coupe précédent -- /OK */
    	/*Esse método ainda n ta reconhecendo a ultima coluna do tabuleiro*/
    	
    	Coords currentCellCoords = new Coords();
//    	for (currentCellCoords.row = 0; currentCellCoords.row < GomokuBoard.size; currentCellCoords.row++){
//            for (currentCellCoords.column = 0; currentCellCoords.column < GomokuBoard.size; currentCellCoords.column++){
//                if (board.get(currentCellCoords) == TileState.Black){ 
//                	if(currentCellCoords.column + 1 < GomokuBoard.size) {
//                		if(board.get(new Coords(currentCellCoords.row, currentCellCoords.column + 1)) == TileState.White){ // Isso está errado
//                				score += 10;
//                			
//                		}
//                	}
////                	score += 10;
//                }
//            }
//        }
    	
    	
    	
    	/* Test 4: Fazendo ele checar primeiro o board por linha, depois por coluna e depois por diagonal*/
    	boolean seqAdv = false;
//    	boolean seqBlack = false;
    	
    	int length = 0;
//    	int lengthBlack = 0;
    	

    	for (currentCellCoords.row = 0; currentCellCoords.row < GomokuBoard.size; currentCellCoords.row++){ // Linha ->
            for (currentCellCoords.column = 0; currentCellCoords.column < GomokuBoard.size; currentCellCoords.column++){
                if (board.get(currentCellCoords) == adversaryCellState && seqAdv == false){ 
                	seqAdv = true;
                	length = 1;
                } else if (board.get(currentCellCoords) == adversaryCellState && seqAdv == true) {
                	length++;
                } else if (board.get(currentCellCoords) == TileState.Empty && seqAdv == true) {
                	switch(length) {
                	case 1:
                		score -= 1;
                		break;
                	case 2:
                		score -= 10;
                		break;
                	case 3:
                		score -= 100;
                		break;
                	case 4:
                		score -= 1000;
                		break;
                	}
                	seqAdv = false;
                	length = 0;
                } else if (board.get(currentCellCoords)== playerCellState) {
                	seqAdv = false;
                	length = 0;
                }
                
            }
            
            seqAdv = false;
            length = 0;
            
            for (currentCellCoords.column = GomokuBoard.size - 1; currentCellCoords.column > -1; currentCellCoords.column--){ // Linha <-
                if (board.get(currentCellCoords) == adversaryCellState && seqAdv == false){ 
                	seqAdv = true;
                	length = 1;
                } else if (board.get(currentCellCoords) == adversaryCellState && seqAdv == true) {
                	length++;
                } else if (board.get(currentCellCoords) == TileState.Empty && seqAdv == true) {
                	switch(length) {
                	case 1:
                		score -= 1;
                		break;
                	case 2:
                		score -= 10;
                		break;
                	case 3:
                		score -= 100;
                		break;
                	case 4:
                		score -= 1000;
                		break;
                	}
                	seqAdv = false;
                	length = 0;
                } else if (board.get(currentCellCoords) == playerCellState) {
                	seqAdv = false;
                	length = 0;
                }
                
            }
        }
    	
    	
    	for (currentCellCoords.column = 0; currentCellCoords.column < GomokuBoard.size; currentCellCoords.column++){ // Coluna (Bas)
            for (currentCellCoords.row = 0; currentCellCoords.row < GomokuBoard.size; currentCellCoords.row++){
                if (board.get(currentCellCoords) == adversaryCellState && seqAdv == false){ 
                	seqAdv = true;
                	length = 1;
                } else if (board.get(currentCellCoords) == adversaryCellState && seqAdv == true) {
                	length++;
                } else if (board.get(currentCellCoords) == TileState.Empty && seqAdv == true) {
                	switch(length) {
                	case 1:
                		score -= 1;
                		break;
                	case 2:
                		score -= 10;
                		break;
                	case 3:
                		score -= 100;
                		break;
                	case 4:
                		score -= 1000;
                		break;
                	}
                	seqAdv = false;
                	length = 0;
                } else if (board.get(currentCellCoords)== playerCellState) {
                	seqAdv = false;
                	length = 0;
                }
                
            }
            
            seqAdv = false;
            length = 0;
            
            for (currentCellCoords.row = GomokuBoard.size - 1; currentCellCoords.row > -1; currentCellCoords.row--){ //Coluna (Haut)
                if (board.get(currentCellCoords) == adversaryCellState & seqAdv == false){ 
                	seqAdv = true;
                	length = 1;
                } else if (board.get(currentCellCoords) == adversaryCellState & seqAdv == true) {
                	length++;
                } else if (board.get(currentCellCoords) == TileState.Empty & seqAdv == true) {
                	switch(length) {
                	case 1:
                		score -= 1;
                		break;
                	case 2:
                		score -= 10;
                		break;
                	case 3:
                		score -= 100;
                		break;
                	case 4:
                		score -= 1000;
                		break;
                	}
                	seqAdv = false;
                	length = 0;
                } else if (board.get(currentCellCoords)== playerCellState) {
                	seqAdv = false;
                	length = 0;
                }
                
            }
        }
    	
    	//Diagonais
    	
    	// Nous utilisons ces stratégies simplement pour vous familiariser avec le code du jeu et le langage de programmation Java.
    	
        return score;
    }
    
    private class ScoredMove {
    	final int score;
    	final Coords move;
    	
    	ScoredMove(int score, Coords move){
    		this.score = score;
    		this.move = move;
    	}
    }
    
    
    private ScoredMove minMax(GomokuBoard board, Player player, Player toPlay, int depth, int alpha, int beta) {
    	
    	//condições finais para parar a recursão || Final conditions to stop the recursive call
    	
    	switch(board.getWinnerState()) {
    	case WinnerState.Black:
    		if(player == Player.Black) return new ScoredMove(+WIN_SCORE - (WIN_SCORE/1000)*(this.nMinMax - depth), null);
    		if(player == Player.White) return new ScoredMove(-WIN_SCORE + (WIN_SCORE/1000)*(this.nMinMax - depth), null);
    	case WinnerState.White:
    		if(player == Player.White) return new ScoredMove(+WIN_SCORE - (WIN_SCORE/1000)*(this.nMinMax - depth), null);
    		if(player == Player.Black) return new ScoredMove(-WIN_SCORE + (WIN_SCORE/1000)*(this.nMinMax - depth), null);
    	case WinnerState.Tie:
    		return new ScoredMove(evaluateBoard(board, player), null);
    	case WinnerState.None:
    		break;
    	}
    	if(depth == 0) return new ScoredMove(evaluateBoard(board, player), null);
    	
    	// Algorithm recursive 
    	
    	boolean maximazing = (player == toPlay); //vai saber se o jogardor simulado atual é o mesmo da vez original
    	ScoredMove best = new ScoredMove(maximazing? Integer.MIN_VALUE : Integer.MAX_VALUE, null);
    	
    	
    	//Still working on the MinMax algorithm 
    	
    	
    }

    private List<Coords> orderedMoves(GomokuBoard board, Player p){ //Really similar to the getAvaibleMoves, however its returns a List insted of an Array
        Map<Coords, Integer> scored = new HashMap<>();
        Coords c = new Coords();
        TileState ts = p == Player.Black? TileState.Black: TileState.White;

        for (c.row = 0; c.row < GomokuBoard.size; c.row++){
            for (c.column = 0; c.column < GomokuBoard.size; c.column++){
                if (board.get(c) == TileState.Empty){
                    board.set(c, ts);
                    int s = evaluateBoard(board, p);
                    board.set(c, TileState.Empty);
                    scored.put(new Coords(c.row, c.column), s);
                }
            }
        }
        return scored.entrySet()
                     .stream()
                     .sorted(Collections.reverseOrder(Map.Entry.comparingByValue()))
                     .map(Map.Entry::getKey)
                     .toList();
    }
    
    
    
    public Coords[] getAvailableMoves(GomokuBoard board, Player player) {
        Coords currentCellCoords = new Coords();
        
        Coords[] cellPlayedForMinMax;
        
        Player advPlayer = player == Player.White? Player.Black : Player.White;
        
        TileState playerCellState = player == Player.White ? TileState.White : TileState.Black;
        
        TileState aux = playerCellState;
        
        TileState advCellState = playerCellState == TileState.White? TileState.Black : TileState.White;
        
        
        Map<Coords, Integer> moves = new HashMap<>();
        Map<Coords, Integer> moves2 = new HashMap<>();

        for (currentCellCoords.row = 0; currentCellCoords.row < GomokuBoard.size; currentCellCoords.row++){
            for (currentCellCoords.column = 0; currentCellCoords.column < GomokuBoard.size; currentCellCoords.column++){
                if (board.get(currentCellCoords) == TileState.Empty){ // Si la case est vide
                    
                    board.set(currentCellCoords, playerCellState); // Jouer le coup
                    int score = evaluateBoard(board, player); // Evaluer le coup
                    System.out.print("row: " + currentCellCoords.row + " column: " + currentCellCoords.column + " Score: " + score + "n");
                    System.out.println();
                    board.set(currentCellCoords, TileState.Empty); // Annuler le coup

                    moves.put(currentCellCoords.clone(), score); // Enregistrer le coup only after the MinMax aplicado
                }
            }
        }
        
        
        /*Fazendo com MinMax  -- At the begin we thought on doing the Min Max algorithm inside get available moves, however wasn't the best choice (We decided to make a recursive algorithm)
         * 01 - ver uma jogada da IA
         * 02 - fazer uma jogada para o outro jogador 
         * 
         * 
         * */
////        
//        for (currentCellCoords.row = 0; currentCellCoords.row < GomokuBoard.size; currentCellCoords.row++){
//            for (currentCellCoords.column = 0; currentCellCoords.column < GomokuBoard.size; currentCellCoords.column++){
//                if (board.get(currentCellCoords) == TileState.Empty){ // Si la case est vide
////                	int score = 0;
//                	board.set(currentCellCoords, playerCellState); // Jouer le coup
//                	int score = evaluateBoard(board, player); // Evaluer le coup
////                  System.out.print("row: " + currentCellCoords.row + " column: " + currentCellCoords.column + " Score: " + score + "n");
////                  System.out.println();
//                  
//////
////                  moves.put(currentCellCoords.clone(), score); // Enregistrer le coup only after the MinMax aplicado
//                	
//                    for(int i = 0; i < nMinMax - 1; i++) {
//                    	aux = (i % 2 == 0) ? advCellState : playerCellState;
//                    	Player playerAux = (i % 2 == 0)? advPlayer : player;
//                    	Coords currentCellCoords2 = new Coords();
//                    	
//                    	for(currentCellCoords2.row = 0; currentCellCoords2.row < GomokuBoard.size; currentCellCoords.row++) {
//                    		for(currentCellCoords2.column = 0; currentCellCoords2.column < GomokuBoard.size; currentCellCoords.column++) {
//                    			 board.set(currentCellCoords2, aux); // Jouer le coup
//                    			 int scoreAux = evaluateBoard(board, playerAux);
////                    			 score += (i%2 == 0)? -scoreAux: scoreAux; // Evaluer le coup
//                    			 board.set(currentCellCoords2, TileState.Empty);
//                    			 
//                    			 moves2.put(currentCellCoords2.clone(), scoreAux);
//                    		}
//                    	}
//	                   
//                    	Stream<Map.Entry<Coords, Integer>> sorted2 = moves2.entrySet().stream().sorted(Collections.reverseOrder(Map.Entry.comparingByValue())); // Trier les coups par ordre de priorité décroissante
//	                
//	
//	                   
//                    }
//                    
//                    board.set(currentCellCoords, TileState.Empty); // Annuler le coup
//                    moves.put(currentCellCoords.clone(), score); // Enregistrer le coup only after the MinMax aplicado
//                }
//            }
//        }
        

        Stream<Map.Entry<Coords, Integer>> sorted = moves.entrySet().stream().sorted(Collections.reverseOrder(Map.Entry.comparingByValue())); // Trier les coups par ordre de priorité décroissante

        return sorted.map(Map.Entry::getKey).toArray(Coords[]::new); // Retourner les coordonnées des coups
    }

    
    
	@Override
	public Coords play(GomokuBoard board, Player player) {
		// on retourne le premier coup
		return getAvailableMoves(board, player)[0];
	}
}