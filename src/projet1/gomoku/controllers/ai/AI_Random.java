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

/**Représente un IA qui cherche les coups en se positionnant sur chaque case, puis en vérifiant le contenu des 4 cases autour dans les 8 directions */
public class AI_Random extends PlayerController {
 
    public AI_Random(){
        super();
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
    
  
    public Coords[] getAvailableMoves(GomokuBoard board, Player player) {
        Coords currentCellCoords = new Coords();
        
        TileState playerCellState = player == Player.White ? TileState.White : TileState.Black;
        
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

        Stream<Map.Entry<Coords, Integer>> sorted = moves.entrySet().stream().sorted(Collections.reverseOrder(Map.Entry.comparingByValue())); // Trier les coups par ordre de priorité décroissante

        return sorted.map(Map.Entry::getKey).toArray(Coords[]::new); // Retourner les coordonnées des coups
    }

    
    
	@Override
	public Coords play(GomokuBoard board, Player player) {
		// on retourne le premier coup
		return getAvailableMoves(board, player)[0];
	}
}