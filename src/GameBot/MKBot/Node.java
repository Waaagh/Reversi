package GameBot.MKBot;

import java.util.List;
import java.util.ArrayList;
import Board.Move;
import Board.ReversiBoard;

public class Node{
    private int protagonist;
    private Node parrent;
    private List<Node> children;
    private Move moveToGetHere;
    private Node bestChild;
    private int activePlayer; // The protagonist player is always 1 and the opponent is always 2
    private ReversiBoard board;
    private double nodeValue;
    private double branchValue;
    private static double DEFAULT_NODE_VALUE = -1;
    
    public Node(){
        protagonist = 1;
        parrent = null;
        children = new ArrayList<Node>();
        moveToGetHere = null;
        bestChild = null;
        activePlayer = 1;
        board = null;
        nodeValue = DEFAULT_NODE_VALUE;
        branchValue = DEFAULT_NODE_VALUE;
    }

    public Node(int protagonist, Node parrent, Move moveToGetHere, int activePlayer, ReversiBoard board){
        this.protagonist = protagonist;
        this.parrent = parrent;
        children = new ArrayList<Node>();
        this.moveToGetHere = moveToGetHere;
        bestChild = null;
        this.activePlayer = activePlayer;
        this.board = board;
        this.nodeValue = DEFAULT_NODE_VALUE;
        this.branchValue = DEFAULT_NODE_VALUE;
    }
    
    public void spawnChildren(){
        ArrayList<Move> allPotentialMoves = board.allPotentialMoves(activePlayer);
        for(Move m : allPotentialMoves){
            ReversiBoard newBoard = board.copy();
            newBoard.doMove(m, activePlayer);
            children.add(new Node(protagonist, this, m, 3-activePlayer, newBoard));
        }
    }
    
    public void spawnNewChildTier(){
        if(children.size() > 0){
            for(Node n : children){
                n.spawnNewChildTier();
            }
        }else{
            spawnChildren();
        }
    }
    
    public double computeNodeValue(){
        double scorePlayer1 = board.getScore(1);
        double scorePlayer2 = board.getScore(2);
        nodeValue = scorePlayer1/scorePlayer2;
        return nodeValue;
    }
    
    public void updateLeafNodeValues(){
        if(children.size() > 0){
            for(Node n : children){
                n.updateLeafNodeValues();
            }
        }else if(nodeValue == DEFAULT_NODE_VALUE){
            computeNodeValue();
        }
    }
    
    public double updateBranchValue(){
        double tmpValue = 0;
        if(activePlayer == protagonist){
            branchValue = -1 * Double.POSITIVE_INFINITY;
        }else{
            branchValue = Double.POSITIVE_INFINITY;
        }
        if(children.size() > 0){
            for(Node n : children){
                tmpValue = n.updateBranchValue();
                if((tmpValue > branchValue && activePlayer == protagonist) || (tmpValue < branchValue && activePlayer != protagonist)){
                    branchValue = tmpValue;
                    bestChild = n;
                }
            }
        }else{
            branchValue = nodeValue;
        }
        return branchValue;
    }
    
    public Node findChildByMove(Move m){
        for(Node child : children){
            if(child.moveToGetHere.equals(m)){
                return child;
            }
        }
        return null;
    }
    
    public Move getBestMove(){
        if(children.size() > 0 && bestChild.moveToGetHere != null){
            return bestChild.moveToGetHere.copy();
        }else{
            return new Move(true); // pass
        }
    }
    
    public int getNumberOfChildren(){
        return children.size();
    }
}