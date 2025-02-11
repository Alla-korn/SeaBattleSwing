package Server;


import Game.Coordinate;
import Game.Ship;

import java.io.Serializable;

public class GameMessage implements Serializable
{
    public enum MessageType
    {
        START_GAME, CONNECTED, PLAYER_ASSIGNMENT, FIRST_TURN, SECOND_TURN,
        SHOT, DESTROY_SHIP, LOOSE, WIN, RAN, MISS, PLAY, READY
    }

    private final MessageType messageType;
    private final String content;

    private Coordinate coordinate;

    private Ship ship;

    private int id;

    public GameMessage(MessageType messageType, String content)
    {
        this.messageType = messageType;
        this.content = content;
    }

    public GameMessage(MessageType messageType, String textContent, Coordinate content, int id)
    {
        this.messageType = messageType;
        this.content = textContent;
        this.coordinate = content;
        this.id = id;
    }

    public GameMessage(MessageType messageType, String textContent, Ship ship, int id)
    {
        this.messageType = messageType;
        this.content = textContent;
        this.ship = ship;
        this.id = id;
    }

    public GameMessage(MessageType messageType, String content, int id_)
    {
        this.messageType = messageType;
        this.content = content;
        this.id = id_;
    }

    public MessageType getMessageType() {
        return messageType;
    }

    public String getContent() {
        return content;
    }

    public Coordinate getCoordinate()
    {
        return coordinate;
    }

    public Ship getShip() {
        return ship;
    }

    public int getId() {
        return id;
    }
}