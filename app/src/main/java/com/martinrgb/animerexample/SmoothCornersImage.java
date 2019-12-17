package com.martinrgb.animerexample;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Point;
import android.graphics.PointF;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.Xfermode;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.util.Log;

import androidx.appcompat.widget.AppCompatImageView;


public class SmoothCornersImage extends AppCompatImageView {

    private final Paint paint = new Paint();
    private final Xfermode mode = new PorterDuffXfermode(PorterDuff.Mode.SRC_IN);

    private Bitmap imageBitmap;
    private Rect dstRect;
    private Matrix matrix;

    float cx, cy;

    private float WIDTH = 400;
    private float HEIGHT = 400;
    private float SKETCH_ROUND_RECT_RADIUS = 100f;

    private boolean ROUND_TL = true,ROUND_TR = true,ROUND_BL = true,ROUND_BR = true;
    private boolean isSquare = false;

    public SmoothCornersImage(Context context) {
        super(context);
        getAttributes(null);
    }

    public SmoothCornersImage(Context context, AttributeSet attrs) {
        super(context, attrs);
        getAttributes(attrs);
    }

    public SmoothCornersImage(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        getAttributes(attrs);
    }

    private void getAttributes(AttributeSet attr) {



        if(attr == null){
            return;
        }
        else{
            TypedArray typedArray = getContext()
                    .getTheme()
                    .obtainStyledAttributes(attr, R.styleable.SmoothCornersImage, 0, 0);
            try {
                SKETCH_ROUND_RECT_RADIUS = typedArray.getInteger(R.styleable.SmoothCornersImage_smooth_radius, 20);
                isSquare = typedArray.getBoolean(R.styleable.SmoothCornersImage_is_square, false);
            } finally {
                typedArray.recycle();
            }
        }



    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        if(w != 0 && h != 0){
            this.cx = w / 2.0f;
            this.cy = h / 2.0f;
            dstRect = new Rect(0, 0, w, h);
            this.WIDTH = isSquare()?Math.min(w,h):w;
            this.HEIGHT = isSquare()?Math.min(w,h):h;

            matrix = new Matrix();
            Point canvasCenter = new Point((int)this.cx,(int)this.cy);
            Point bmpCenter = new Point(this.imageBitmap.getWidth() / 2, this.imageBitmap.getHeight() / 2);

            float xRatio = bmpCenter.x/this.cx;
            float yRatio = bmpCenter.y/this.cy;

            float ratio;
            if(xRatio > yRatio){
                ratio = 1/yRatio;
            }
            else {
                ratio = 1/xRatio;
            }

            Log.e("ratio", String.valueOf(ratio));

            matrix.postTranslate(cx - bmpCenter.x, cy - bmpCenter.y);
            matrix.postScale(ratio,ratio, cx, cy);

        }
    }

    @Override
    public void setImageBitmap(Bitmap bm) {
        super.setImageBitmap(bm);
        this.imageBitmap = bm;
    }

    @Override
    public void setImageDrawable(Drawable drawable) {
        super.setImageDrawable(drawable);

        if(drawable instanceof BitmapDrawable)
            this.imageBitmap = ((BitmapDrawable)drawable).getBitmap();
        else {

            int width = drawable.getIntrinsicWidth();
            int height = drawable.getIntrinsicHeight();

            if(width != -1 && height != -1)
                this.imageBitmap = Bitmap.createBitmap(width
                        , height
                        , Bitmap.Config.ARGB_8888);
            else
                this.imageBitmap = Bitmap.createBitmap(1
                        , 1
                        , Bitmap.Config.ARGB_8888);

            Canvas canvas = new Canvas(this.imageBitmap);
            drawable.setBounds(0, 0, canvas.getWidth(), canvas.getHeight() );
            drawable.draw(canvas);
        }
    }

    //############  onDraw ############
    @Override
    protected void onDraw(Canvas canvas) {

        if(imageBitmap != null){
            canvas.saveLayer(0,
                    0,
                    canvas.getWidth(),
                    canvas.getHeight(),
                    paint,
                    Canvas.ALL_SAVE_FLAG);

            paint.setAntiAlias(true);
            paint.setFilterBitmap(true);
            paint.setDither(true);

            Path path;
            if(isSquare){
                if(SKETCH_ROUND_RECT_RADIUS == WIDTH/2){
                    canvas.translate(cx-WIDTH/2, cy-HEIGHT/2);
                    drawAndroidRoundRect(0,0,WIDTH,HEIGHT,SKETCH_ROUND_RECT_RADIUS,SKETCH_ROUND_RECT_RADIUS,ROUND_TL,ROUND_TR,ROUND_BL,ROUND_BR,canvas);
                }
                else{
                    path = SketchRealSmoothRect(0, 0, WIDTH , WIDTH , SKETCH_ROUND_RECT_RADIUS,SKETCH_ROUND_RECT_RADIUS,
                            ROUND_TL,ROUND_TR,ROUND_BL,ROUND_BR);
                    canvas.drawPath(path,paint);
                }
            }
            else{

                if(SKETCH_ROUND_RECT_RADIUS == Math.min(WIDTH,HEIGHT)/2){
                    canvas.translate(cx-WIDTH/2, cy-HEIGHT/2);
                    drawAndroidRoundRect(0,0,WIDTH,HEIGHT,SKETCH_ROUND_RECT_RADIUS,SKETCH_ROUND_RECT_RADIUS,ROUND_TL,ROUND_TR,ROUND_BL,ROUND_BR,canvas);
                }
                else{
                    path = SketchRealSmoothRect(0, 0, WIDTH , HEIGHT , SKETCH_ROUND_RECT_RADIUS,SKETCH_ROUND_RECT_RADIUS,
                            ROUND_TL,ROUND_TR,ROUND_BL,ROUND_BR);
                    canvas.drawPath(path,paint);
                }

            }

            paint.setXfermode(mode);

            if(imageBitmap.getWidth() != 1){

                //canvas.drawBitmap(this.imageBitmap, -(this.imageBitmap.getWidth() - canvas.getWidth())/2, -(this.imageBitmap.getHeight() - canvas.getHeight())/2, paint);
                //canvas.drawBitmap(this.imageBitmap, 0,0, paint);
                canvas.drawBitmap(this.imageBitmap,matrix,paint);
            }
            else{
                canvas.drawBitmap(this.imageBitmap, null, dstRect, paint);
            }

            paint.reset();
        }
    }



    //############  Define Path ############
    public Path SketchRealSmoothRect(
            float left, float top, float right, float bottom, float rx, float ry,
            boolean tl, boolean tr, boolean bl, boolean br
    ){

        Path path = new Path();
        if (rx < 0) rx = 0;
        if (ry < 0) ry = 0;
        float width = right - left;
        float height = bottom - top;
        float posX = cx - width/2;
        float posY = cy - height/2;

        float r = rx;

        float vertexRatio;
        if(r/Math.min(width/2,height/2) > 0.5){
            float percentage = ((r/Math.min(width/2,height/2)) - 0.5f)/0.4f;
            float clampedPer = Math.min(1,percentage);
            vertexRatio = 1.f - (1 - 1.104f/1.2819f)*clampedPer;
        }
        else{
            vertexRatio = 1.f;
        }

        float controlRatio;
        if(r/Math.min(width/2,height/2) > 0.6){
            float percentage = ((r/Math.min(width/2,height/2)) - 0.6f)/0.3f;
            float clampedPer = Math.min(1,percentage);
            controlRatio = 1 + (0.8717f/0.8362f - 1)*clampedPer;
        }
        else{
            controlRatio = 1;
        }

        path.moveTo(posX + width/2 , posY);
        if(!tr){
            path.lineTo(posX + width, posY);
        }
        else{

            path.lineTo(posX + Math.max(width/2,width - r/100.0f*128.19f*vertexRatio), posY);
            path.cubicTo(posX + width - r/100.f*83.62f*controlRatio, posY,posX + width - r/100.f*67.45f,posY + r/100.f*4.64f, posX + width - r/100.f*51.16f, posY + r/100.f*13.36f);
            path.cubicTo(posX + width - r/100.f*34.86f, posY + r/100.f*22.07f,posX + width - r/100.f*22.07f,posY + r/100.f*34.86f, posX + width - r/100.f*13.36f, posY + r/100.f*51.16f);
            path.cubicTo(posX + width - r/100.f*4.64f, posY + r/100.f*67.45f,posX + width,posY + r/100.f*83.62f*controlRatio, posX + width, posY + Math.min(height/2,r/100.f*128.19f*vertexRatio));
        }


        if(!br){
            path.lineTo(posX + width, posY + height);
        }
        else{
            path.lineTo(posX + width, posY + Math.max(height/2,height - r/100.f*128.19f*vertexRatio));
            path.cubicTo(posX + width, posY + height - r/100.f*83.62f*controlRatio,posX + width - r/100.f*4.64f,posY + height - r/100.f*67.45f, posX + width - r/100.f*13.36f, posY + height -  r/100.f*51.16f);
            path.cubicTo(posX + width - r/100.f*22.07f, posY + height - r/100.f*34.86f,posX + width - r/100.f*34.86f,posY + height - r/100.f*22.07f, posX + width - r/100.f*51.16f, posY + height - r/100.f*13.36f);
            path.cubicTo(posX + width - r/100.f*67.45f, posY + height - r/100.f*4.64f,posX + width - r/100.f*83.62f*controlRatio,posY + height, posX + Math.max(width/2,width - r/100.f*128.19f*vertexRatio), posY + height);

        }


        if(!bl){
            path.lineTo(posX, posY + height);
        }
        else{
            path.lineTo(posX + Math.min(width/2,r/100.f*128.19f*vertexRatio), posY + height);
            path.cubicTo(posX +  r/100.f*83.62f*controlRatio, posY + height,posX + r/100.f*67.45f,posY + height - r/100.f*4.64f, posX + r/100.f*51.16f, posY + height -  r/100.f*13.36f);
            path.cubicTo(posX +  r/100.f*34.86f, posY + height - r/100.f*22.07f,posX + r/100.f*22.07f,posY + height - r/100.f*34.86f, posX + r/100.f*13.36f, posY + height - r/100.f*51.16f);
            path.cubicTo(posX  + r/100.f*4.64f, posY + height - r/100.f*67.45f,posX ,posY + height - r/100.f*83.62f*controlRatio, posX , posY + Math.max(height/2,height - r/100.f*128.19f*vertexRatio));

        }

        if(!tl){
            path.lineTo(posX, posY);
        }
        else{
            path.lineTo(posX, posY + Math.min(height/2,r/100.f*128.19f*vertexRatio));
            path.cubicTo(posX, posY + r/100.f*83.62f*controlRatio,posX + r/100.f*4.64f,posY + r/100.f*67.45f, posX + r/100.f*13.36f, posY + r/100.f*51.16f);
            path.cubicTo(posX +  r/100.f*22.07f, posY + r/100.f*34.86f,posX + r/100.f*34.86f,posY +  r/100.f*22.07f, posX + r/100.f*51.16f, posY + r/100.f*13.36f);
            path.cubicTo(posX  + r/100.f*67.45f, posY +  r/100.f*4.64f,posX + r/100.f*83.62f*controlRatio,posY, posX + Math.min(width/2,r/100.f*128.19f*vertexRatio), posY);

        }

        path.close();

        return path;
    }

    public void drawAndroidRoundRect(
            float left, float top, float right, float bottom, float rx, float ry,
            boolean tl, boolean tr, boolean bl, boolean br,Canvas canvas
    ){
        float w = right - left;
        float h = bottom - top;
        final Rect rect = new Rect((int)left, (int)top, (int)right,(int)bottom);
        final RectF rectF = new RectF(rect);
        canvas.drawRoundRect(rectF,rx,ry, paint);

        if(!tl){
            canvas.drawRect(0, 0, w/2, h/2, paint);
            Path path = new Path();
            path.moveTo(0,h/2);
            path.lineTo(0,0);
            path.lineTo(w/2,0);
            canvas.drawPath(path,paint);
        }
        if(!tr){
            canvas.drawRect(w/2, 0, w, h/2, paint);
            Path path = new Path();
            path.moveTo(w/2,0);
            path.lineTo(w,0);
            path.lineTo(w,h/2);
            canvas.drawPath(path,paint);
        }

        if(!bl){
            canvas.drawRect(0, h/2, w/2, h, paint);
            Path path = new Path();
            path.moveTo(0,h/2);
            path.lineTo(0,h);
            path.lineTo(w/2,h);
            canvas.drawPath(path,paint);
        }

        if(!br){
            canvas.drawRect(w/2, h/2, w, h, paint);
            Path path = new Path();
            path.moveTo(w/2,h);
            path.lineTo(w,h);
            path.lineTo(w,h/2);
            canvas.drawPath(path,paint);
        }

    }

    //############  Getter & Setter ############
    public float getRoundRadius() {
        return SKETCH_ROUND_RECT_RADIUS;
    }

    public void setRoundRadius(float radius){
        this.SKETCH_ROUND_RECT_RADIUS =  Math.max(0,getRadiusInMaxRange(WIDTH,HEIGHT,radius));
        this.invalidate();
    }

    public float getMAXRadius(float width,float height){
        float minBorder;
        if(width > height){
            minBorder = height;
        }
        else{
            minBorder = width;
        }
        return minBorder/2;
    }
    private float getRadiusInMaxRange(float width,float height,float radius) {
        float realRadius = Math.min(radius, getMAXRadius(width, height));
        return realRadius;
    }

    public PointF getRectSize(){
        return new PointF(getRectWidth(),getRectWidth());
    }

    public void setRectSize(float width,float height){
        setRectWidth(width);
        setRectHeight(height);
        requestLayout();
        //this.invalidate();
    }

    private float getRectWidth() {
        return WIDTH;
    }

    private void setRectWidth(float WIDTH) {
        this.WIDTH = WIDTH;
        getLayoutParams().width = (int)WIDTH;
    }

    private float getRectHeight() {
        return HEIGHT;
    }

    private void setRectHeight(float HEIGHT) {
        this.HEIGHT = HEIGHT;
        getLayoutParams().height = (int)HEIGHT;

    }

    public boolean isSquare() {
        return isSquare;
    }

    public void setIsSquare(boolean square) {
        isSquare = square;
        this.invalidate();
    }

    public void setRectRoundEnable(boolean tl,boolean tr,boolean bl,boolean br){
        this.ROUND_TL = tl;
        this.ROUND_TR = tr;
        this.ROUND_BL = bl;
        this.ROUND_BR = br;
        this.invalidate();
    }
}