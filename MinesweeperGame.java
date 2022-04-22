package com.codegym.games.minesweeper;

import com.codegym.engine.cell.Color;
import com.codegym.engine.cell.Game;

import java.util.ArrayList;
import java.util.List;

public class MinesweeperGame extends Game {
    private static final int SIDE = 9;

    private static final String MINE = "\uD83D\uDCA3";
    private static final String FLAG = "\uD83D\uDEA9";

    private GameObject[][] gameField = new GameObject[SIDE][SIDE];

    private int countClosedTiles = SIDE * SIDE;
    private int countFlags;
    private int score;
    private int countMinesOnField;
    private boolean isGameStopped;

    @Override
    public void initialize() {
        setScreenSize(SIDE, SIDE);//set screen size
        createGame();//start the game
    }

    @Override
    public void onMouseLeftClick(int x, int y) {
        if (isGameStopped) {
            restart();
            return;
        }
        openTile(x,y);
    }//on leftClick invoke openTile method

    @Override
    public void onMouseRightClick(int x, int y) {// on rightClick invoke markTile on clicked xy coordinate
        markTile(x, y);
    }

    private void createGame() {
        //isGameStopped = false;
        for (int y = 0; y < SIDE; y++) {
            for (int x = 0; x < SIDE; x++) {
                boolean isMine = getRandomNumber(10) < 1;
                if (isMine) {
                    countMinesOnField++;
                }
                gameField[y][x] = new GameObject(x, y, isMine);
                setCellValueEx(x, y, Color.ORANGE, "");
            }
        }
        countMineNeighbors();
        countFlags = countMinesOnField;
    }

    private void countMineNeighbors() {
        for (int y = 0; y < SIDE; y++) {
            for (int x = 0; x < SIDE; x++) {
                GameObject gameObject = gameField[y][x];

                if (!gameObject.isMine) {
                    gameObject.countMineNeighbors = Math.toIntExact(getNeighbors(gameObject).stream().filter(neighbor -> neighbor.isMine).count());
                }
            }
        }
    }

    private void restart() {
        countClosedTiles = SIDE * SIDE;
        score = 0;
        setScore(score);
        countMinesOnField = 0;
        isGameStopped = false;
        createGame();
    }

    private void openTile(int x, int y) {
        GameObject gameObject = gameField[y][x];

        if (gameObject.isOpen || gameObject.isFlag || isGameStopped) {
            return;
        }
        countClosedTiles--;
        gameObject.isOpen = true;
        setCellColor(x, y, Color.GREEN);

        if (gameObject.isMine) {//if it is a mine
            setCellValueEx(gameObject.x, gameObject.y, Color.RED, MINE);//display mine
            gameOver();
            return;
        }

        this.score += 5;
        setScore(score);

        if (countClosedTiles == countMinesOnField) {
            win();
        }
        if (gameObject.countMineNeighbors > 0) {
            setCellValue(x, y, String.valueOf(gameObject.countMineNeighbors));
        } else {
            getNeighbors(gameObject).forEach(neighbor -> openTile(neighbor.x, neighbor.y));
        }
    }
    private void gameOver() {
        showMessageDialog(Color.WHITE, "Game over", Color.BLACK, 50);
        isGameStopped = true;
    }
    private void win() {
        showMessageDialog(Color.WHITE, "You Win!", Color.VIOLET, 50);
        isGameStopped = true;
    }
    private void markTile(int x, int y) {
        GameObject gameObject = gameField[y][x];

        if (gameObject.isOpen || isGameStopped || (countFlags <= 0 && !gameObject.isFlag)) {//if cell is open, or (no more flags and cell is already flagged)
            return;//exit markTile method
        }
        if (gameObject.isFlag) {//if cell is already flagged
            countFlags++;//add a flag back to arsenal
            gameObject.isFlag = false;//remove the flag
            setCellValueEx(x, y, Color.ORANGE, "");
        } else {
            countFlags--;//decrement flag inventory
            gameObject.isFlag = true;//turn cell into a flag
            setCellValueEx(x, y, Color.YELLOW, FLAG);

        }

    }


    private List<GameObject> getNeighbors(GameObject gameObject) {
        List<GameObject> result = new ArrayList<>();
        for (int y = gameObject.y - 1; y <= gameObject.y + 1 ; y++) {
            for (int x = gameObject.x - 1; x <= gameObject.x + 1 ; x++) {
                if (y < 0 || y >= SIDE) {
                    continue;
                }
                if (x < 0 || x >= SIDE) {
                    continue;
                }
                if (x == gameObject.x && y == gameObject.y) {
                    continue;
                }
                result.add(gameField[y][x]);
            }
        }
        return result;
    }
}
