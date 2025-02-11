package Game;


import com.sun.source.doctree.SerialDataTree;

import java.io.Serializable;

public class Ship implements Serializable
{
    private Coordinate startCoordinate;
    private Coordinate endCoordinate;

    private int size;

    private int ran;

    public Ship(Coordinate start, Coordinate end, int size)
    {
        this.startCoordinate = start;
        this.endCoordinate = end;
        this.size = size;

        ran = 0;
    }

    public boolean isDestroy()
    {
        ran++;
        if(ran == size)
        {
            return true;
        }
        else
        {
            return false;
        }
    }

    public void setSize(int size) {
        this.size = size;
    }

    public int getSize() {
        return size;
    }

    public void setCoordinate(Coordinate coordinate) {
        this.startCoordinate = coordinate;
    }

    public Coordinate getCoordinate() {
        return startCoordinate;
    }

    public void setEndCoordinate(Coordinate endCoordinate) {
        this.endCoordinate = endCoordinate;
    }

    public Coordinate getEndCoordinate() {
        return endCoordinate;
    }
}