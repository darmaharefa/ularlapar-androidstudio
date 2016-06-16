package com.harefa.dk.ularlapar;

class Particle {
    protected int mPosX;
    protected int mPosY;
    Particle(int x, int y) {
        mPosX = x;
        mPosY = y;
    }
    public void MoveParticle(int x, int y){
        mPosX = x;
        mPosY = y;
    }
    public int GetX() {
        return mPosX;
    }
    public int GetY() {
        return mPosY;
    }
    public Boolean TestCollision(Particle p) {
        if( mPosX == p.GetX() &&
                mPosY == p.GetY() ){
            return true;
        }else{
            return false;
        }
    }

}