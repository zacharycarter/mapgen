package com.carterza;

import squidpony.squidmath.Coord;

/**
 * Created by zachcarter on 12/24/16.
 */
public class CellularAutomata implements ICellularAutomata
{
    private int[][] grid;
    private int stepCounter;
    private CAStep stepFunction;

    public interface CAStep
    {
        public abstract int[][] work(int[][] grid);
    }

    /**
     * Initializes the automata with a copy of the initial data.
     * @param initialData
     */
    public CellularAutomata(int[][] initialData, CAStep step)
    {
        this.grid = initialData.clone();
        this.stepCounter = 0;
        this.stepFunction = step;
    }

    @Override
    public void step()
    {
        grid = stepFunction.work(grid);
        this.stepCounter++;
    }

    /**
     * Progresses a given number of steps.
     * @param steps
     */
    public void step(int steps)
    {
        for(int i=0; i<steps; i++)
        {
            step();
        }
    }

    public int[][] result()
    {
        return grid;
    }

}
