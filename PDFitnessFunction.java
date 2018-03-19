/******************************************************************************
*  A Teaching GA					  Developed by Hal Stringer & Annie Wu, UCF
*  Version 2, January 18, 2004
*******************************************************************************/

import java.io.*;
import java.util.*;
import java.text.*;

class PDFitnessFunction extends FitnessFunction{

/*******************************************************************************
*                            INSTANCE VARIABLES                                *
*******************************************************************************/

	public int steps;
	public int MAX_RECORD_SIZE = 8;
	public int roundRobinScore;

/*******************************************************************************
*                            STATIC VARIABLES                                  *
*******************************************************************************/

/*******************************************************************************
*                              CONSTRUCTORS                                    *
*******************************************************************************/

	public PDFitnessFunction() {
		name = "Prisoners Dilemma";
		steps = 1500;
		
		Strategy[] opponents = new Strategy[5];
		opponents[0] = new StrategyTitForTat();
		opponents[1] = new StrategyAlwaysCooperate();
		opponents[2] = new StrategyAlwaysDefect();
		opponents[3] = new StrategyRandom();
		opponents[4] = new StrategyTitForTwoTats();
		
		for (int i = 0; i < opponents.length; i++)
		{
			for (int j = i+1; j < opponents.length; j++)
			{
				IteratedPD ipd = new IteratedPD(opponents[i], opponents[j]);
				ipd.runSteps(steps);
				roundRobinScore += ipd.player1Score();
				roundRobinScore += ipd.player2Score();
			}
		}
	}

/*******************************************************************************
*                                MEMBER METHODS                                *
*******************************************************************************/

//  COMPUTE A CHROMOSOME'S RAW FITNESS *************************************

	public void doRawFitness(Chromo X){
		// Extract info from genome
		int firstMove = getFirstMove(X);
		boolean response = getResponse(X);
		int recordLength = getRecordLength(X);
		int[] recordWeights = getRecordWeights(X);
		int pWildcard = getPWildcard(X);

		// Initialize candidate Strategy
		Strategy strat = new StrategyWildcard(firstMove, response, recordLength, recordWeights, pWildcard);
		int score = 0;
		int opp_score = 0;
		
		// Set up opponents
		Strategy[] opponents = new Strategy[5];
		opponents[0] = new StrategyTitForTat();
		opponents[1] = new StrategyAlwaysCooperate();
		opponents[2] = new StrategyAlwaysDefect();
		opponents[3] = new StrategyRandom();
		opponents[4] = new StrategyTitForTwoTats();

		// run Interated Prisoners' Dilemmas
		for (Strategy opponent : opponents){
			IteratedPD ipd = new IteratedPD(strat, opponent);
			ipd.runSteps(steps);
			score += ipd.player1Score();
			opp_score += ipd.player2Score();
		}
		
		// return raw score
		X.rawFitness = ((double)score)/((double)(score + opp_score + roundRobinScore));
	}

	private int getFirstMove(Chromo X){
		return Integer.parseInt(X.chromo.substring(0,1));
	}
	private boolean getResponse(Chromo X){
		if (Integer.parseInt(X.chromo.substring(1,2)) == 0)
			return false;
		return true;
	}
	private int getRecordLength(Chromo X){
		return Integer.parseInt(X.chromo.substring(2,5), 2);
	}
	private int[] getRecordWeights(Chromo X){
		int[] weights = new int[MAX_RECORD_SIZE];
		for (int i=0; i<MAX_RECORD_SIZE; i++){
			int start = 5 + (4*i);
			weights[i] = Integer.parseInt(X.chromo.substring(start,start+4), 2);
		}
		return weights;
	}
	private int getPWildcard(Chromo X){
		return Integer.parseInt(X.chromo.substring(5+(4*MAX_RECORD_SIZE)), 2);
	}


//  PRINT OUT AN INDIVIDUAL GENE TO THE SUMMARY FILE *********************************

	public void doPrintGenes(Chromo X, FileWriter output) throws java.io.IOException{
		System.out.println("Executing FF Gene Output");
	}


/*******************************************************************************
*                             STATIC METHODS                                   *
*******************************************************************************/


}   // End of OneMax.java ******************************************************

