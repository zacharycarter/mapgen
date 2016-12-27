package com.carterza.math;

import com.carterza.common.Direction;

import java.io.Serializable;
import java.util.Arrays;

public class Vec2I implements Comparable<Vec2I>, Serializable {
    
    private static final long serialVersionUID = 1L;
    
    public final int x, y;
    
    /**
     * Create coordinates at the given X and Y position.
     * 
     * @param x the position along the left-right dimension
     * @param y the position along the top-bottom dimension
     */
    public Vec2I(int x, int y) {
        this.x = x;
        this.y = y;
    }
    
    public boolean isZero() {
        return x == 0 && y == 0;
    }

    /**
     * Gets the coordinates of the next space in the given direction
     * 
     * @param d the direction
     */
    public Vec2I add(Direction d) {
        return add(d.x,d.y);
    }
    
    public Vec2I add(Vec2I xy) {
        return new Vec2I(x + xy.x, y + xy.y);
    }
    
    public Vec2I add(int dx, int dy) {
        return new Vec2I(x + dx, y + dy);
    }
    
    public Vec2I subtract(Vec2I other) {
        return new Vec2I(x-other.x, y-other.y);
    }
    
    public Vec2I multiply(int m) {
        return new Vec2I(m*x, m*y);
    }
    
    public double magnitude() {
        return toVec2D().magnitude();
    }
    
    public Vec2D unit() {
        return toVec2D().unit();
    }
    
    public Direction nearestCardinalDirection() {
        return toVec2D().nearestCardinalDirection();
    }
    
    public double squareDistanceTo(Vec2I other) {
        return toVec2D().squareDistanceTo(other.toVec2D());
    }
    
    public double distanceTo(Vec2I other) {
        return toVec2D().distanceTo(other.toVec2D());
    }
    
    @Override
    public int compareTo(Vec2I other) {
        int d = this.x - other.x;
        if (d == 0) {
            d = this.y - other.y;
        }
        return d;
    }
    
    /**
     * Determines whether this Vec2I and another Vec2I are next to each other.
     * 
     * @param other the other Vec2I
     * @return whether they are adjacent
     */
    public boolean isAdjacent(Vec2I other) {
        int dx = Math.abs(x - other.x),
            dy = Math.abs(y - other.y);
        return (dx == 1 && dy == 0) || (dx == 0 && dy == 1);
    }

    /**
     * Gets the direction from this Vec2I to another Vec2I.
     * 
     * @param other the other Vec2I
     * @return the direction the other Vec2I is in
     * @throws AssertionError if the direction to the other Vec2I cannot be
     *                          described with compass directions, e.g. if it's
     *                          diagonal
     */
    public Direction getDirectionTo(Vec2I other) {
        int dx = x - other.x,
            dy = y - other.y;
        assert dx == 0 || dy == 0;
        if (dx < 0) return Direction.E;
        if (dx > 0) return Direction.W;
        if (dy < 0) return Direction.S;
        assert dy > 0;
        return Direction.N;
    }
    
    public double distance(Vec2I other) {
        int dx = x - other.x,
            dy = y - other.y;
        return Math.sqrt(dx*dx + dy*dy);
    }
    
    public Vec2D toVec2D() {
        return new Vec2D(this);
    }

    public int dot(Vec2I other) {
        return x * other.x + y * other.y;
    }
    
    @Override
    public boolean equals(Object other) {
         if (other instanceof Vec2I) {
             Vec2I o = (Vec2I)other;
             return this.x == o.x && this.y == o.y;
         } else {
             return super.equals(other);
         }
    }

    public String toString() {
        return x+","+y;
    }
    
    @Override
    public int hashCode() {
        return Arrays.hashCode(new Object[]{ x, y });
    }
}
