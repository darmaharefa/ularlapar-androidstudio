package com.harefa.dk.ularlapar;

import java.util.Timer;
import java.util.TimerTask;
import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.BitmapFactory.Options;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Paint.Align;
import android.graphics.Rect;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.DisplayMetrics;
import android.view.MotionEvent;
import android.view.View;

public class MainActivity extends Activity {

    private SnakeView SnakeView;
    private SensorManager SensorManager;

    // Digunakan untuk menyimpan data game setting
    // SharedPreferences hampir sama dengan session di web
    private SharedPreferences Prefs;

    private Timer autoUpdate;
    private SnakeTimer UpdateEvent = new SnakeTimer();

    private int Speed;
    private int Width;
    private Boolean GemsEnabled = true;
    private int GemProbability = 50;
    private int GemLife = 20;
    private Boolean DrawScore = true;
    private Boolean DrawGemTimers = true;


    private static final int MAX_WAIT = 660;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Akses Sensor dari perangkat
        SensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);

        // Memanggil SharedPreference Manager
        Prefs = PreferenceManager.getDefaultSharedPreferences(getBaseContext());

        //Load Preferences
        Speed = MAX_WAIT - Prefs.getInt("velocity", 320);
        Width=  Prefs.getInt("screen_width", 10);

        GemsEnabled = Prefs.getBoolean("gems_enabled", true);
        GemProbability = Prefs.getInt("gem_probability", 50);
        GemLife =  Prefs.getInt("gem_life", 20);

        DrawScore =  Prefs.getBoolean("print_score", true);
        DrawGemTimers =  Prefs.getBoolean("print_gem_timers", true);

        // ContentView yang digunakan adalah contentview dari class SnakeView (Custom View)
        SnakeView = new SnakeView(getApplicationContext());
        setContentView(SnakeView);

        autoUpdate = new Timer();
        UpdateEvent = new SnakeTimer();
        autoUpdate.schedule(UpdateEvent, 0 ,Speed);
        SnakeView.startSnake();
    }

    public enum Direction {
        UP,  DOWN, LEFT, RIGHT
    }
    class SnakeTimer extends TimerTask {
        @Override
        public void run() {
            runOnUiThread(new Runnable() {
                public void run() {
                    SnakeView.postInvalidate();
                }
            });
        }
    }

    // SnakeView (Custome View)
    class SnakeView extends View implements SensorEventListener{

        private Sensor Accelerometer;

        private static final int NUM_FRUIT = 6;
        private static final int NUM_GEMS = 3;

        private int UnitWidth;
        private int UnitHeight;
        private int UnitSize;

        private int PixWidth;
        private int PixHeight;

        private Boolean Runing = false;
        private Boolean Displayable = false;
        private Boolean RequiresReset = false;
        private Bitmap BmpBody;
        private Bitmap BmpHead;
        private Bitmap BmpBg;
        private Bitmap BmpMaze;
        private Bitmap BmpFruit[]= new Bitmap[NUM_FRUIT];
        private Bitmap BmpGems[]= new Bitmap[NUM_GEMS];

        private Paint TextPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        private Paint ScorePaint= new Paint(Paint.ANTI_ALIAS_FLAG);

        private String Text = getText(R.string.welcome).toString();

        private Direction direction;
        private Direction oldDirection;
        private final Snake Snake = new Snake(Prefs.getInt("start_length", 6));
        private final Fruit Fruit = new Fruit(0,0);
        private final Maze Maze = new Maze(12, 16);
        private final GemList Gems= new GemList();

        public void Start(){
            Text = getText(R.string.welcome).toString();
            Snake.reset(Prefs.getInt("start_length", 6));
            reset();
            Runing = true;
            Displayable = true;
            RequiresReset=false;
        }
        public void Pause(){
            Text = getText(R.string.paused).toString();
            Runing = false;
        }
        public void Resume(){
            Text = getText(R.string.welcome).toString();
            if(RequiresReset){
                Snake.reset(Prefs.getInt("start_length", 6));
                reset();
            }
            Displayable = true;
            Runing = true;
            RequiresReset=false;
        }
        public void Stop(){
            Text = getText(R.string.welcome).toString();
            Displayable = false;
            Snake.reset(Prefs.getInt("start_length", 6));
            Runing = false;
        }

        public void startSnake() {
            // Sensor yang digunakan adalah sensor accelerometer dengan sensivitas rendah (SENSOR_DELAY_UI)
            SensorManager.registerListener(this, Accelerometer, SensorManager.SENSOR_DELAY_UI);
        }

        public void populateFruit(Bitmap fruit[], int size) {
            Bitmap apple = BitmapFactory.decodeResource(getResources(), R.drawable.apple);
            fruit[0] = Bitmap.createScaledBitmap( apple , size , size , true);

            Bitmap apple2 = BitmapFactory.decodeResource(getResources(), R.drawable.apple2);
            fruit[1] = Bitmap.createScaledBitmap( apple2 , size , size , true);

            Bitmap orange = BitmapFactory.decodeResource(getResources(), R.drawable.orange);
            fruit[2] = Bitmap.createScaledBitmap( orange , size , size , true);

            Bitmap banana = BitmapFactory.decodeResource(getResources(), R.drawable.banana);
            fruit[3] = Bitmap.createScaledBitmap( banana , size , size , true);

            Bitmap pear = BitmapFactory.decodeResource(getResources(), R.drawable.pear);
            fruit[4] = Bitmap.createScaledBitmap( pear , size , size , true);

            Bitmap cherry = BitmapFactory.decodeResource(getResources(), R.drawable.cherry);
            fruit[5] = Bitmap.createScaledBitmap( cherry , size , size , true);

        }
        public void populateGems(Bitmap gems[], int size) {
            Bitmap diamond = BitmapFactory.decodeResource(getResources(), R.drawable.diamond);
            gems[0] = Bitmap.createScaledBitmap( diamond , size , size , true);

            Bitmap emerald = BitmapFactory.decodeResource(getResources(), R.drawable.emerald);
            gems[1] = Bitmap.createScaledBitmap( emerald , size , size , true);

            Bitmap ruby = BitmapFactory.decodeResource(getResources(), R.drawable.ruby);
            gems[2] = Bitmap.createScaledBitmap( ruby , size , size , true);

        }

        public SnakeView(Context context) {
            super(context);
            Accelerometer = SensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);

            DisplayMetrics metrics = new DisplayMetrics();
            getWindowManager().getDefaultDisplay().getMetrics(metrics);

            PixHeight = metrics.heightPixels;
            PixWidth = metrics.widthPixels;

            direction=Direction.DOWN;
            oldDirection=Direction.DOWN;

            reset();

            Options opts = new Options();
            opts.inDither = true;
            opts.inPreferredConfig = Bitmap.Config.RGB_565;
            Bitmap bg  = BitmapFactory.decodeResource(getResources(), R.drawable.background, opts);
            BmpBg = Bitmap.createScaledBitmap( bg , PixWidth, PixHeight, true);

            TextPaint.setTextAlign(Align.CENTER);
            TextPaint.setARGB(205, 255, 255, 255);
            TextPaint.setTextSize(36);

            ScorePaint.setTextAlign(Align.LEFT);
            ScorePaint.setARGB(180, 255, 255, 255);
            ScorePaint.setTextSize(18);
        }

        public void regenerateSprites()
        {
            UnitWidth = Width;
            UnitHeight = (UnitWidth * PixHeight) / PixWidth;
            UnitSize = PixWidth/UnitWidth;

            Bitmap body = BitmapFactory.decodeResource(getResources(), R.drawable.body);
            BmpBody = Bitmap.createScaledBitmap( body , UnitSize ,
                    UnitSize, true);
            Bitmap head = BitmapFactory.decodeResource(getResources(), R.drawable.eye);
            BmpHead = Bitmap.createScaledBitmap( head , UnitSize ,
                    UnitSize, true);
            Bitmap maze = BitmapFactory.decodeResource(getResources(), R.drawable.brick);
            BmpMaze = Bitmap.createScaledBitmap( maze , UnitSize ,
                    UnitSize, true);

            populateFruit(BmpFruit, UnitSize );

            populateGems(BmpGems,UnitSize);

        }
        public void reset(){
            regenerateSprites();
            resetSnakePosition();
            resetMaze();
            Gems.reset();
            randomiseFruit();


        }
        public void randomiseFruit(){
            Fruit.randomise(UnitWidth, UnitHeight, Snake,Maze, null, Gems);
        }
        public void resetSnakePosition(){
            Snake.SetPosition(UnitWidth/2, UnitHeight/2);
        }
        public void resetMaze(){
            Maze.reset(UnitWidth, UnitHeight);
        }
//        @Override
//        protected void onSizeChanged(int w, int h, int oldw, int oldh) {
//            // Never run due to settings in the manifest
//        }

        @Override
        public void onSensorChanged(SensorEvent event) {
            float xSens;
            float ySens;
            if (event.sensor.getType() != Sensor.TYPE_ACCELEROMETER)
                return;

            // Menyimpan data dari accelerometer ke x dan y
            // kita menggunakan x dan y karena pergerakan ular Atas, Bawah, Kiri dan Kanan
            xSens = event.values[1];
            ySens = event.values[0];

            if( xSens > 0 &&
                    abs(xSens)>abs(ySens) &&
                    oldDirection!=Direction.UP){
                direction = Direction.DOWN;
            } else if( ySens > 0 &&
                    abs(ySens)>=abs(xSens) &&
                    oldDirection!=Direction.RIGHT){
                direction = Direction.LEFT;
            } else if(xSens <= 0 &&
                    abs(xSens)>abs(ySens) &&
                    oldDirection!=Direction.DOWN){
                direction = Direction.UP;
            }else if(ySens <= 0 &&
                    abs(ySens)>=abs(xSens) &&
                    oldDirection!=Direction.LEFT){
                direction = Direction.RIGHT;
            }
        }
        @Override
        public boolean onTouchEvent(MotionEvent x){
            Resume();
            return true;
        }

        private void drawString(Canvas canvas, Paint paint, String str, int x, int y) {
            String[] lines = str.split("\n");
            final Rect bounds = new Rect();
            int h = 0;
            for (int i = 0; i < lines.length; ++i) {
                canvas.drawText(lines[i], x, y + i*h, paint);
                paint.getTextBounds(lines[i], 0, lines[i].length(), bounds);

                if(i == 0){
                    h = bounds.height()+8;
                }
            }
        }

        private float abs(float x) {
            if(x>=0)
                return x;
            return -x;
        }
        private void drawObject(Canvas c, Bitmap b,
                                float xc,  float xs,
                                float yc,  float ys ,
                                float xPos,float yPos )
        {
            final float x = xc + xPos * xs;
            final float y = yc - yPos * ys;

            c.drawBitmap(b, x, y, null);



            // Gambar ulang ular saat menembus dinding
            if(xPos==0){
                final float gx = xc + UnitWidth * xs;
                c.drawBitmap(b, gx, y, null);
            }
            if(xPos == UnitWidth-1){
                final float gx = xc - xs;
                c.drawBitmap(b, gx, y, null);
            }
            if(yPos==0){
                final float gy = yc - UnitHeight * ys;
                c.drawBitmap(b, x, gy, null);
            }
            if(yPos== UnitHeight-1){
                final float gy = yc + ys;
                c.drawBitmap(b, x, gy, null);
            }

            // Gambar ulang ualar saat disalah satu sudut
            if(xPos==0 && yPos == 0 ){
                final float gx = xc + UnitWidth * xs;
                final float gy = yc - UnitHeight * ys;
                c.drawBitmap(b, gx, gy, null);
            }
            if(xPos==0 && yPos == UnitHeight-1){
                final float gx = xc + UnitWidth * xs;
                final float gy = yc + ys;
                c.drawBitmap(b, gx, gy, null);
            }
            if(xPos==UnitWidth-1 && yPos == 0){
                final float gx = xc -xs;
                final float gy = yc - UnitHeight * ys;
                c.drawBitmap(b, gx, gy, null);
            }
            if(xPos==UnitWidth-1 && yPos == UnitHeight-1){
                final float gx = xc - xs;
                final float gy = yc + ys;
                c.drawBitmap(b, gx, gy, null);
            }

        }
        @Override
        protected void onDraw(Canvas canvas) {

            // Gambar Background

            canvas.drawBitmap(BmpBg, 0, 0, null);

            if(direction == null)
            {
                direction = Direction.DOWN;
            }
            if( Runing ){
                Snake.updatePositions(direction,UnitWidth, UnitHeight);



                if(Fruit.TestCollision(Snake.getParticle(0))){
                    Snake.IncreaseLength(Prefs.getInt("fruit_gain", 1));
                    Fruit.randomise(UnitWidth, UnitHeight,Snake,Maze, null,Gems);
                }

                if(Snake.TestCollision(Maze)){
                    // Saat Kalah
                    Text = getText(R.string.game_over).toString()
                            .concat(" ".concat(
                                    String.valueOf(
                                            Snake.getScore()
                                    )));
                    RequiresReset = true;
                    Runing = false;
                }
                Gems.progress(GemsEnabled,GemProbability, GemLife,
                        UnitWidth, UnitHeight,
                        Maze , Snake, Fruit);

                direction = Gems.doEvent(UnitWidth, UnitHeight,
                        Maze , Snake, Fruit,
                        direction);

                oldDirection = direction;

            }
            if (Displayable){
                final float xs = UnitSize;
                final float ys = UnitSize;

                //HACK
                final int xGap = (PixWidth-((int)xs)* UnitWidth)/2;
                final int yGap = (PixHeight-((int)ys)* UnitHeight)/2;

                final float xc = 0 + xGap;
                final float yc = canvas.getHeight()-ys - yGap;

                final Rect boundingRect = new Rect(xGap,yGap,PixWidth-xGap,PixHeight-yGap);
                TextPaint.setStyle(Paint.Style.STROKE);
                canvas.drawRect(boundingRect, TextPaint);
                TextPaint.setStyle(Paint.Style.FILL);

                int count = Maze.getParticleCount();
                for (int i = 0; i <count; i++) {
                    drawObject(canvas, BmpMaze,
                            xc,xs, yc,ys ,
                            Maze.getPosX(i),
                            Maze.getPosY(i) );
                }

                count = Snake.getParticleCount();
                for (int i = count - 1; i >=0; i--) {
                    if (i == 0){
                        drawObject(canvas, BmpHead,
                                xc,xs, yc,ys ,
                                Snake.getPosX(i),
                                Snake.getPosY(i) );
                    }else{
                        drawObject(canvas, BmpBody,
                                xc,xs, yc,ys ,
                                Snake.getPosX(i),
                                Snake.getPosY(i) );
                    }
                }
                count = Gems.getParticleCount();
                for (int i = 0; i <count; i++) {
                    drawObject(canvas, BmpGems[Gems.getType(i)],
                            xc,xs, yc,ys ,
                            Gems.getPosX(i),
                            Gems.getPosY(i));
                }
                drawObject(canvas, BmpFruit[Fruit.getType()],
                        xc,xs, yc,ys ,
                        Fruit.GetX(),
                        Fruit.GetY());
                if(DrawScore){
                    canvas.drawText("SCORE: "+Snake.getScore(), xGap+3, yGap+18, ScorePaint);
                }
                if(DrawGemTimers){
                    count = Gems.getParticleCount();
                    int xOff=0;
                    Rect bounds = new Rect();
                    String txt;
                    for (int i = 0; i <count; i++) {
                        switch(Gems.getType(i)){
                            case 0://Diamond
                                ScorePaint.setARGB(180, 150, 150, 255);
                                break;
                            case 1://Emerald
                                ScorePaint.setARGB(180, 150, 255, 150);
                                break;
                            case 2://Ruby
                                ScorePaint.setARGB(180, 255, 150, 150);
                                break;
                        }
                        txt =""+ Gems.getLife(i)+" ";
                        canvas.drawText(txt,
                                xGap+3+xOff, yc +ys -3,
                                ScorePaint);
                        ScorePaint.getTextBounds(""+ Gems.getLife(i)+" ",
                                0, txt.length(), bounds);
                        xOff+=bounds.width()+6;

                    }

                    ScorePaint.setARGB(180, 255, 255, 255);
                }

            }
            if(!Runing)
            {
                drawString( canvas,TextPaint, Text,  PixWidth/2, PixHeight/3);
            }
        }
        @Override
        public void onAccuracyChanged(Sensor sensor, int accuracy) {
        }
    }
}