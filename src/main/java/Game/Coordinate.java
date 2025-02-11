package Game;


import java.io.Serializable;

public class Coordinate implements Serializable
{
    private int X;
    private int Y;

    public Coordinate(int x, int y)
    {
        this.X = x;
        this.Y = y;
    }

    public Coordinate() {}

    @Override
    public String toString() {
        return getX() + " " + getY();
    }

    @Override

    public boolean equals(Object obj) {
        if (this == obj) return true; // Проверяем, равен ли объект самому себе
        if (obj == null || getClass() != obj.getClass()) return false; // Проверяем, является ли объект экземпляром класса Coordinate
        Coordinate that = (Coordinate) obj; // Приводим объект к классу Coordinate
        return getX() == that.getX() && getY() == that.getY(); // Сравниваем координаты
    }

    public void setX(int x) {
        X = x;
    }

    public void setY(int y) {
        Y = y;
    }

    public int getX() {
        return X;
    }

    public int getY() {
        return Y;
    }
}
