package com.harefa.dk.ularlapar;

import java.util.ArrayList;

class Maze {

    private int Width = 0;
    private int Height = 0;

    private ArrayList<Particle> Balls = new ArrayList<Particle>();

    Maze(int width, int height) {
        reset(width, height);
    }

    public void reset(int width, int height){
        Width  =  width;
        Height  = height;

        Balls.clear();
    }
    public void reflect(int width) {
        for(int i= 0;i<Balls.size();i++){
            Balls.get(i).MoveParticle(
                    width -1 - Balls.get(i).GetX(),
                    Balls.get(i).GetY());
        }
    }

    public int getParticleCount() {
        return Balls.size();
    }

    public float getPosX(int i) {
        return Balls.get(i).mPosX;
    }

    public float getPosY(int i) {
        return Balls.get(i).mPosY;
    }
    public Boolean TestCollision(Particle p){
        int i = Balls.size()-1;
        for(; i>=0 ; i--){
            if (Balls.get(i).TestCollision(p)){
                return true;
            }
        }
        return false;
    }
}

