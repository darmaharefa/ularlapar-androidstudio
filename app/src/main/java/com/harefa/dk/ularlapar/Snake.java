package com.harefa.dk.ularlapar;

import java.util.ArrayList;

import android.preference.PreferenceManager;
import android.util.Log;

import com.harefa.dk.ularlapar.MainActivity.Direction;

class Snake {
    private int initial_particles = 6;
    static final int INTIAL_POSX = 6;
    static final int INTIAL_POSY = 6;

    private int score = 0;
    PreferenceManager Prefs;
    private ArrayList<Particle> Balls = new ArrayList<Particle>();

    Snake(int length) {
        initialise( length );
        
    }
    public void initialise(int length){
        initial_particles = length;
        for (int i = 0; i <initial_particles; i++) {
            Balls.add( new Particle(INTIAL_POSX,INTIAL_POSY) );
        }

    }
    public void reset(int length){
        Balls.clear();
        initialise(length);
        score = 0;
    }
    public void IncreaseLength(int amount){

        int n,x,y;
        for(n=0 ; n < amount; n++){
            x = Balls.get(Balls.size()-1).GetX();
            y = Balls.get(Balls.size()-1).GetY();
            Balls.add(new Particle(x,y));
        }
        score++;
    }
    /*
     *
     */
    public void SetPosition(int x, int y) {
        int i = Balls.size()-1;
        for(; i>=0 ; i--){
            Balls.get(i).MoveParticle( x, y );
        }
    }

    public void updatePositions(Direction d, int xSize, int ySize) {
        int i = Balls.size()-1;
        for(; i>0 ; i--){
            Balls.get(i).MoveParticle( Balls.get(i-1).GetX() , Balls.get(i-1).GetY() );
        }

        Particle first = Balls.get(0);

        switch(d){
            case UP:
                first.MoveParticle( Balls.get(0).GetX() , Balls.get(0).GetY()+1 );
                break;

            case DOWN:
                first.MoveParticle( Balls.get(0).GetX() , Balls.get(0).GetY()-1 );
                break;
            case LEFT:
                first.MoveParticle( Balls.get(0).GetX()-1 , Balls.get(0).GetY() );
                break;
            case RIGHT:
            default:
                first.MoveParticle( Balls.get(0).GetX()+1 , Balls.get(0).GetY() );
                break;
        }

        if(first.GetX() < 0){
            first.MoveParticle(xSize-1, first.GetY());
        }
        if(first.GetX() >= xSize){
            first.MoveParticle(0, first.GetY());
        }
        if(first.GetY() < 0){
            first.MoveParticle(first.GetX(), ySize - 1);
        }
        if(first.GetY() >= ySize){
            first.MoveParticle(first.GetX(), 0 );
        }

    }
    public void resetLength() {
        if(initial_particles<Balls.size()){
            Balls.subList(initial_particles,Balls.size()).clear();
        }
    }
    public Direction reverse(Direction d) {
        Particle tmp;
        for(int i= 0;i<Balls.size()/2;i++){
            tmp= Balls.get(i);
            Balls.set(i, Balls.get(Balls.size()-1-i));
            Balls.set(Balls.size()-1-i, tmp);
        }
        for(int i= 1;i<Balls.size();i++){

            if(Balls.get(0).GetX()<Balls.get(i).GetX()){
                return Direction.LEFT;
            }
            if(Balls.get(0).GetX()>Balls.get(i).GetX()){
                return Direction.RIGHT;
            }
            if(Balls.get(0).GetY()<Balls.get(i).GetY()){
                return Direction.DOWN;
            }
            if(Balls.get(0).GetY()>Balls.get(i).GetY()){
                return Direction.UP;
            }
        }
        return d;
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
//    public int getInitialParticleCount() {
//        return  initial_particles;
//    }

    public float getPosX(int i) {
        return Balls.get(i).mPosX;
    }

    public float getPosY(int i) {
        return Balls.get(i).mPosY;
    }
    public Particle getParticle(int i) {
        return Balls.get(i);
    }
    public int getScore() {
        return score;
    }
    public void awardPoints(int p) {
        score+=p;
    }
    public Boolean TestCollision( Maze maze){
        int i = 1;

        Particle first = Balls.get(0);

        if(maze.TestCollision(first)){
            return true;
        }

        for( ; i<Balls.size(); i++ ){
            if(first.TestCollision(Balls.get(i))){
                return true;
            }
        }
        return false;
    }
}
