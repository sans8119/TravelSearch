package com.uiSearch.customViews;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.DashPathEffect;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Paint.Style;
import android.graphics.Path;
import android.graphics.PathMeasure;
import android.graphics.Point;
import android.graphics.Rect;
import android.graphics.RectF;
import android.os.CountDownTimer;
import android.util.AttributeSet;
import android.widget.HorizontalScrollView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.uiSearch.data.entity.BaseEntity;
import com.uiSearch.data.entity.SearchSuggestionsEntity;
import com.uisearch.presentation.R;

import java.util.List;



public class CustomLinearLayout extends LinearLayout {

    private Path fromTopath = new Path();
    private Path partOfFromToPathWhileAnimating = new Path();
    private Paint fromToPathPaint = new Paint();
    private Paint fromToIconPaint;

    private Context mContext;
    private float x1;
    private float y1;
    private float x3;
    private float y3;
    private int counter;
    private final int fromToPathFadeCount = 4;

    private boolean rainDropsAnimation;
    private boolean fromToPathAnimation;

    private Path rainDropPath;
    private int rainDropPathDrawCounter = 0;
    private Bitmap rainDropBitmap;
    private Matrix rainDropMatrix;
    private Matrix secondRainDropMatrix = new Matrix();
    private float[] pos = new float[2];
    private boolean rainDropPathSetterFlag = true;

    private final int radius=5;
    private final int offset = 14;

    public CustomLinearLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        setWillNotDraw(false);
        mContext = context;
        fromToIconPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        fromToIconPaint.setColor(Color.BLACK);
        fromToIconPaint.setStyle(Style.FILL);
        fromToPathPaint.setStrokeWidth(2);
        fromToPathPaint.setStrokeCap(Paint.Cap.ROUND);
        fromToPathPaint.setColor(Color.parseColor("#5B5B5B"));
        fromToPathPaint.setStyle(Style.STROKE);
        fromToPathPaint.setPathEffect(new DashPathEffect(new float[]{2, 2}, 20));
        rainDropPath = new Path();
        rainDropMatrix = new Matrix();
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        super.onLayout(changed, l, t, r, b);
        for (int i = 0; i < getChildCount(); i++) {
            Rect bounds = new Rect();
            if (getChildAt(i).getId() == R.id.from_relative_lay/*R.id.from_edit_text*/) {
                int offset=2;
                RelativeLayout toRelativeLayout=(RelativeLayout)getChildAt(i);
                TextView fromMsgOnSpinner= (TextView)(((LinearLayout)toRelativeLayout.getChildAt(1)).getChildAt(0));
                fromMsgOnSpinner.getPaint().getTextBounds(fromMsgOnSpinner.getText().toString().trim(), 0, fromMsgOnSpinner.getText().length(), bounds);
               x1=bounds.width()+toRelativeLayout.getLeft()+fromMsgOnSpinner.getPaddingLeft()+2*radius+3*offset;
                y1 = getChildAt(i).getY() + getChildAt(i).getHeight() / 2;
            } else if (getChildAt(i).getId() == R.id.to_relative_lay/*R.id.to_edit_text*/) {
                RelativeLayout toRelativeLayout=(RelativeLayout)getChildAt(i);
                TextView toMsgOnSpinner= (TextView)(((LinearLayout)toRelativeLayout.getChildAt(1)).getChildAt(0));
                toMsgOnSpinner.getPaint().getTextBounds(toMsgOnSpinner.getText().toString().trim(), 0, toMsgOnSpinner.getText().length(), bounds);
                x3=bounds.width()+toRelativeLayout.getLeft()+toMsgOnSpinner.getPaddingLeft()+offset;
                y3 = getChildAt(i).getY() + getChildAt(i).getHeight() / 2;
                drawPathBetweenFromAndToEditTexts(mContext, (int) x1, (int) y1, (int) x3, (int) y3);
            }
            if (rainDropPathSetterFlag) {
                if (getChildAt(i).getId() == R.id.clouds) {
                    Rect rect = new Rect();
                    getChildAt(i).getHitRect(rect);
                    rainDropPath.moveTo(rect.centerX(), rect.exactCenterY());
                }
                if (getChildAt(i).getId() == R.id.horizontal_scroll) {
                    Rect rect = new Rect();
                    getChildAt(i).getHitRect(rect);
                    rainDropPath.lineTo(rect.exactCenterX(), rect.exactCenterY());
                    rainDropPathSetterFlag = false;
                }
            }
        }
    }



    @Override
    protected void dispatchDraw(Canvas canvas) {
        super.dispatchDraw(canvas);
        canvas.drawPath(partOfFromToPathWhileAnimating, fromToPathPaint);
        fromToIconPaint.setColor(Color.RED);
        fromToIconPaint.setStyle(Style.FILL_AND_STROKE);
        canvas.drawCircle(x1, y1, radius, fromToIconPaint);
        fromToIconPaint.setStyle(Style.STROKE);
        canvas.drawCircle(x1, y1, radius * 2, fromToIconPaint);
        fromToIconPaint.setStyle(Style.FILL);
        fromToIconPaint.setColor(Color.GREEN);
        drawArrows(new Point[]{new Point((int) x3, (int) (y3 - offset)), new Point((int) (x3 - offset), (int) (y3 + offset)), new Point((int) (x3 + offset), (int) (y3 + offset))}, canvas, fromToIconPaint);
        if (rainDropBitmap != null) {
            canvas.drawBitmap(rainDropBitmap, rainDropMatrix, null);
            canvas.drawBitmap(rainDropBitmap, secondRainDropMatrix, null);
        }
    }

    @Override
    public void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        canvas.drawPath(partOfFromToPathWhileAnimating, fromToPathPaint);
        if (rainDropBitmap != null) {
            canvas.drawBitmap(rainDropBitmap, rainDropMatrix, null);
            canvas.drawBitmap(rainDropBitmap, secondRainDropMatrix, null);
            RectF rect = new RectF();
            rainDropPath.computeBounds(rect, true);
            if (Math.round(pos[1]) == Math.round(rect.bottom)) {
                rainDropBitmap.recycle();
                rainDropBitmap = null;
            }
        }
    }

    private void drawArrows(Point[] point, Canvas canvas, Paint paint) {
        float[] points = new float[8];
        points[0] = point[0].x;
        points[1] = point[0].y;
        points[2] = point[1].x;
        points[3] = point[1].y;
        points[4] = point[2].x;
        points[5] = point[2].y;
        points[6] = point[0].x;
        points[7] = point[0].y;

        canvas.drawVertices(Canvas.VertexMode.TRIANGLES, 8, points, 0, null, 0, null, 0, null, 0, 0, paint);
        Path path = new Path();
        path.moveTo(point[0].x, point[0].y);
        path.lineTo(point[1].x, point[1].y);
        path.lineTo(point[2].x, point[2].y);
        canvas.drawPath(path, paint);
    }

    public void drawPathBetweenFromAndToEditTexts(Context context, int a, int b, int c, int d) {
        x1 = a;
        y1 = b;
        x3 = c;
        y3 = d;

        fromTopath.moveTo(x1, y1);
        fromTopath.cubicTo(x1 + x1 / 2, y1 + (y3 - y1) / 3, x1 - x1 / 1.2f, y1 + (y3 - y1) / 1.5f, x3, y3);
        final long DRAW_TIME = 10000;
        CountDownTimer timer = new CountDownTimer(DRAW_TIME, 100) {
            private boolean flag = true;
            int alpha = 255;
            PathMeasure measure = new PathMeasure();
            Path segmentPath = new Path();
            float start = 0;

            @Override
            public void onTick(long millisUntilFinished) {
                float percent = ((float) (DRAW_TIME - millisUntilFinished))
                        / (float) DRAW_TIME;
                if (flag) {
                    fromToPathPaint.setAlpha(255);
                    measure.setPath(fromTopath, false);

                    float length = measure.getLength() * percent;
                    measure.getSegment(start, length, segmentPath, true);
                    start = length;
                    partOfFromToPathWhileAnimating.addPath(segmentPath);
                } else {
                    fromToPathPaint.setAlpha((int) (alpha - alpha * percent));
                }
                invalidate();
            }

            @Override
            public void onFinish() {
                measure.getSegment(start, measure.getLength(), segmentPath,
                        true);
                partOfFromToPathWhileAnimating.addPath(segmentPath);

                if (counter < fromToPathFadeCount) {
                    flag = !flag;
                    this.start();

                } else {
                    if (!flag)
                        this.start();
                    flag = true;
                }
                if (flag) {
                    fromToPathPaint.setAlpha(255);
                    if (counter < fromToPathFadeCount) {
                        segmentPath = new Path();
                        measure = new PathMeasure();
                        start = 0;
                        partOfFromToPathWhileAnimating.reset();
                        counter++;
                    }
                }
                invalidate();
            }
        };
        timer.start();
    }

    public void createRainDrops(final List<SearchSuggestionsEntity> results) {
        final int animSteps = 20;
        final long DRAW_TIME = 7000;
        rainDropPathDrawCounter=0;
        rainDropMatrix.reset();
        secondRainDropMatrix.reset();
        rainDropPathSetterFlag=true;
        rainDropBitmap=null;
        pos[0]=0; pos[1]=0;

        final PathMeasure pm = new PathMeasure(rainDropPath, false);
        final float fSegmentLen = pm.getLength() / animSteps;//20 animation steps
        final float[] tan = new float[2];

        CountDownTimer timer = new CountDownTimer(DRAW_TIME, DRAW_TIME / animSteps) {
            private boolean flag=false;

            @Override
            public void onTick(long millisUntilFinished) {
                if(rainDropBitmap==null)
                rainDropBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.water_drop);
                rainDropMatrix.reset();
                int distance = (int) fSegmentLen * rainDropPathDrawCounter;
                pm.getPosTan(distance, pos, tan);
                rainDropPathDrawCounter++;
                rainDropMatrix.setTranslate(pos[0], pos[1]);
                RectF rect = new RectF();
                rainDropPath.computeBounds(rect, true);
                    if (Math.round(pos[1]) == Math.round(rect.bottom)) {
                        secondRainDropMatrix.setTranslate(pos[0], pos[1] - rainDropBitmap.getHeight());
                    } else {
                        secondRainDropMatrix.setTranslate(pos[0], pos[1] - 2 * rainDropBitmap.getHeight());
                    }
                if (Math.round(pos[1])>= Math.round(rect.bottom)) {
                    setPointsOnCustomImageView();
                    flag=true;
                }
                    invalidate();
            }

            @Override
            public void onFinish() {
                RectF rect = new RectF();
                rainDropPath.computeBounds(rect, true);
                if (Math.round(pos[1])>= Math.round(rect.bottom)) {
                    setPointsOnCustomImageView();
                }else {
                    start();
                }
            }

            private void setPointsOnCustomImageView(){
                for(int i=0;i<CustomLinearLayout.this.getChildCount();i++){
                    if(CustomLinearLayout.this.getChildAt(i).getId()==R.id.horizontal_scroll){
                        CustomImageView leafImageView=(CustomImageView)((LinearLayout)((HorizontalScrollView)CustomLinearLayout.this.getChildAt(i)).getChildAt(0)).getChildAt(0);
                        leafImageView.setPoints(results);
                        leafImageView.invalidate();
                    }
                    boolean flag=false;
                }
            }
        };
        timer.start();
    }

}