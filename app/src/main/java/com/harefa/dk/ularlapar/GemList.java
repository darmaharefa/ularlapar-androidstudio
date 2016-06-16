package com.harefa.dk.ularlapar;

/**
 * Created by root on 13/06/16.
 */
import java.util.ArrayList;

import com.harefa.dk.ularlapar.MainActivity.Direction;

public class GemList {
    private ArrayList<Gem> Gems = new ArrayList<Gem>();
    int Turn =0;

    GemList() {
    }

    public void progress(boolean enabled, int frequency, int life,
                         int width, int height,
                         Maze maze ,Snake snake, Fruit fruit){
        if(enabled){
            if( Turn > frequency ){
                Turn = 0;
                Gem newGem = new Gem(1,1,life );
                newGem.randomise(width, height, snake, maze, fruit, this);
                Gems.add(newGem);
            }
        }
        for(int i=0;i< Gems.size();i++){
            Gems.get(i).progress();
            if(Gems.get(i).getLife()<0){
                Gems.remove(i);
                i--;
            }
        }
        Turn ++;

    }
    public Direction doEvent(int width, int height,
                             Maze maze ,Snake snake, Fruit fruit,
                             Direction dir){
        for(int i=0;i< Gems.size();i++){

            if(Gems.get(i).TestCollision(snake.getParticle(0))){

                snake.awardPoints(5);

                switch( Gems.get(i).getType() ){
                    case(0):
                        resetSnake(snake);
                        break;
                    case(1):
                        dir = reverseSnake(snake, dir);
                        break;
                    case(2):
                        dir = reflectObjects( width, maze, snake, fruit, dir);
                        break;
                }

                Gems.remove(i);
                i--;
            }
        }
        return dir;
    }
    private void resetSnake(Snake snake){
        snake.resetLength();
    }
    private Direction reverseSnake(Snake snake, Direction dir){
        return snake.reverse(dir);
    }
    private Direction reflectObjects(int width, Maze maze ,Snake snake, Fruit fruit,
                                     Direction dir){
        reflect(width);
        maze.reflect(width);
        snake.reflect(width);
        fruit.MoveParticle(width-1-fruit.GetX(), fruit.GetY());
        // Ganti arah saat makan gems
        if(dir==Direction.LEFT)
            return Direction.RIGHT;
        if(dir==Direction.RIGHT)
            return Direction.LEFT;
        return dir;

    }
    public void reflect(int width) {
        for(int i= 0;i<Gems.size();i++){
            Gems.get(i).MoveParticle(
                    width -1 - Gems.get(i).GetX(),
                    Gems.get(i).GetY());
        }
    }
    public void reset(){
        Turn =0;
        Gems.clear();
    }
    public int getParticleCount() {
        return Gems.size();
    }

    public float getPosX(int i) {
        return Gems.get(i).mPosX;
    }

    public float getPosY(int i) {
        return Gems.get(i).mPosY;
    }
    public int getType(int i){
        return Gems.get(i).getType();
    }
    public int getLife(int i){
        return Gems.get(i).getLife();
    }
    public Particle getParticle(int i) {
        return Gems.get(i);
    }
}


