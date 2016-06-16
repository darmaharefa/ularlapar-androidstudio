package com.harefa.dk.ularlapar;

/**
 * Created by root on 13/06/16.
 */
public class Gem extends Fruit{

    private int Life;
    Gem(int x, int y, int life) {
        super(x, y);
        NUM_FRUIT = 3;
        Life = life;
    }
    public  int getLife(){
        return Life;
    }
    public void progress(){
        Life--;
    }
}
