package com.harefa.dk.ularlapar;

/**
 * Created by root on 13/06/16.
 */

class Fruit extends Particle{

    private int fruitType;

    protected int NUM_FRUIT=6;

    java.util.Random rand = new java.util.Random();

    Fruit(int x, int y) {
        super(x, y);
    }
    public void randomise( int width, int height,
                           Snake snake, Maze maze, Fruit fruit, GemList gems){

        final Boolean grid[][]= new Boolean[width][height];
        int x,y,n;
        int count = 0;

        // Pilih buah secara random
        fruitType = (int)abs(rand.nextInt() % NUM_FRUIT);

        for(x=0; x<width;x++){
            for(y=0;y<height;y++){
                grid[x][y]=false;
            }
        }
        if(snake != null){
            for(n=0;n < snake.getParticleCount();n++ ){
                grid[((int) snake.getPosX(n))%width]
                        [((int) snake.getPosY(n))%height]=true;
            }
        }
        if(maze != null)
            for(n=0;n < maze.getParticleCount();n++ ){
                grid[((int) maze.getPosX(n))%width]
                        [((int) maze.getPosY(n))%height]=true;
            }
        if(fruit != null){
            grid[((int) fruit.GetX())%width]
                    [((int) fruit.GetY())%height]=true;
        }
        if(gems != null){
            for(n=0;n < gems.getParticleCount();n++ ){
                grid[((int) gems.getPosX(n))%width]
                        [((int) gems.getPosY(n))%height]=true;
            }
        }

        for(x=0; x<width;x++){
            for(y=0;y<height;y++){
                if(grid[x][y]==false){
                    count++;
                }
            }
        }

        if(count==0){
            x=width+2;
            y=height+2;
            return;
        }
        count = (int)abs(rand.nextInt() % count);
        for(x=0; x<width;x++){
            for(y=0;y<height;y++){
                if(grid[x][y]==false){
                    if(count == 0 ){
                        mPosX = x;
                        mPosY = y;
                        return;
                    }
                    count--;
                }
            }
        }
    }
    private int abs(int i) {
        if(i>=0)
            return i;
        return -i;
    }
    public int getType(){
        return fruitType;
    }

}
