/**
 * Class containing the Wildcard strategy.
 * @author	Didier Lessage
 */

import java.util.*;

public class StrategyWildcard extends Strategy
{
    /**
     * Encoding for wildcard strategy.
     */

    int m_firstMove; // First move the strategy performs when first facing an opponent
    boolean m_bCopyOpponent; // If true, default move is to copy opponent; if false, do opposite
    int[] m_opponentRecord; // Holds past moves of opponent
    int[] m_recordWeights; // Weight of each past opponent move
    double m_wildcard; // Percentage chance between 1-32% that strategy will do opposite of m_copyOpponent
    public static Random r = new Random();
    int m_defectWeight, m_cooperateWeight, m_nextMove = 0;


    // 0 = defect, 1 = cooperate

    public StrategyWildcard(int i_firstMove, boolean i_copyOpponent, int i_recordSize, int[] i_recordWeights, int i_wildcard)
    {
        name = "Wildcard";
        m_firstMove = i_firstMove;
        m_bCopyOpponent = i_copyOpponent;
        m_opponentRecord = new int[i_recordSize]; // 0th position is most recent move
        m_recordWeights = new int[i_recordSize]; // 0th position correlates with 0th position in opponent record array
        m_wildcard = i_wildcard/100.0f; // Convert passed in wildcard to a percentage, assumes "passer" will start from 1%

        // Store weights for all stored opponent past moves
        for(int i=0; i<i_recordSize; i++)
        {
            m_recordWeights[i] = i_recordWeights[i]/100; // Assumes record weights given still need to be convereted to decimal
            m_opponentRecord[i] = -1; // Store invalid values initially
        }
    }  /* StrategyWildcard */

    public int nextMove()
    {
        if(m_opponentRecord.length <1)
            return 1;

        UpdateRecord();
        if(m_opponentRecord[1] == -1)
            return m_firstMove;
        
        UpdateWeights();

        m_nextMove = 1;
        if(m_cooperateWeight >= m_defectWeight)
            m_nextMove = 0;

        if(m_bCopyOpponent)
            FlipMove();

        if(bDoWildcard())
        {
            FlipMove();
            System.out.println("Wildcard!\n");
        }

        return m_nextMove;
    }  /* nextMove */

    // Shift every record over one entry to the right ([0], [1], ..., [recordLength-1])
    private void UpdateRecord()
    {
        for(int i=m_opponentRecord.length - 1; i>0; i--)
            m_opponentRecord[i] = m_opponentRecord[i-1];
        m_opponentRecord[0] = opponentLastMove;
    }/* UpdateRecord */

    // Calculate weights of opponent's past m_opponentRecord.length() moves
    private void UpdateWeights()
    {
        m_defectWeight = 0;
        m_cooperateWeight = 0;
        for(int i=0; i<m_opponentRecord.length; i++)
        {
            if(m_opponentRecord[i] == 0)
                m_defectWeight += m_recordWeights[i];
            else if(m_opponentRecord[i] == 1)
                m_cooperateWeight += m_recordWeights[i];
        }
    }/* UpdateWeights */

    private boolean bDoWildcard()
    {
        return ((m_wildcard > r.nextDouble())? true : false);
    }/* bDoWildcard */

    private void FlipMove()
    {
        if(m_nextMove == 0)
            m_nextMove = 1;
        else
            m_nextMove = 0;
    } /* FlipMove */

    int GetFirstMove(){return m_firstMove;}
    boolean GetCopyOpponent(){return m_bCopyOpponent;}
    int GetRecordLength(){return m_opponentRecord.length;}
    int[] GetWeights(){return m_recordWeights;}
    double GetWildcardChance(){return m_wildcard;}



}  /* class StrategyWildcard */