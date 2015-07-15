package com.uiSearch.customViews;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.ColorMatrix;
import android.graphics.ColorMatrixColorFilter;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Point;
import android.graphics.Rect;
import android.graphics.Region;
import android.util.AttributeSet;
import android.widget.ImageView;

import com.uiSearch.data.entity.BaseEntity;
import com.uiSearch.data.entity.SearchSuggestionsEntity;
import com.uiSearch.utils.Constants;
import com.uiSearch.utils.CustomListener;

import java.util.Iterator;
import java.util.List;
import com.uisearch.presentation.R;


public class CustomImageView extends ImageView {
    private Bitmap backTile, bmp;
    private Paint textPaint;
    private int i = 0;
    private Context mContext;
    private int rInc;
    private int r;
    private boolean drawFlag;
    private String TAG;
    private List<SearchSuggestionsEntity> mResults;
    private int offset = 5;
    private CustomListener listener;
    private float mAngle;
    private Paint mPaint;
    int rainDropRadius;

    public void setListener(CustomListener customListener) {
        listener = customListener;
    }

    public CustomImageView(Context context, AttributeSet attrs) {
        super(context, attrs);
        setWillNotDraw(false);
        TAG = getClass().getCanonicalName();
        rainDropRadius=Integer.parseInt(Constants.RainDropRadius.getValue());
        backTile = BitmapFactory.decodeResource(getResources(), R.drawable.tiles);
        bmp = Bitmap.createBitmap(backTile.getWidth(), backTile.getHeight(), Bitmap.Config.ARGB_8888);
        mContext = context;
        textPaint = new Paint();
        textPaint.setAntiAlias(true);
        textPaint.setColor(Color.parseColor("#7FFFD4"));
        textPaint.setStyle(Paint.Style.FILL);
        textPaint.setTextSize(rainDropRadius / 3);
        textPaint.setUnderlineText(true);
        textPaint.ascent();
        mPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        invalidate();
    }

    public void setPoints(List<SearchSuggestionsEntity> results) {
        if (results.size() > 0) {
            mResults = results;
            rInc = rainDropRadius / (10 * results.size());
            if (rInc == 0) rInc = rainDropRadius / 5;
            int xDist = (3 * (2 * rainDropRadius)) / 2;
            Iterator<SearchSuggestionsEntity> itr = results.iterator();
            int counter = 0;
            Point prevPoint = new Point();
            prevPoint.x = 0;
            prevPoint.y = 0;
            while (itr.hasNext()) {
                SearchSuggestionsEntity object = itr.next();
                Point point = new Point();
                if (counter % 2 == 0)
                    point.y = 2 * rainDropRadius + offset;
                else
                    point.y = getHeight() - 2 * rainDropRadius - offset;
                int lenghtOfText = 0;
                if (counter == 0) {
                    point.x = xDist;
                } else {
                    lenghtOfText = (int) Math.ceil(textPaint.measureText(object.getName()));
                    if (xDist < lenghtOfText) {
                        point.x = prevPoint.x + lenghtOfText + offset;
                    } else {
                        point.x = prevPoint.x + xDist + offset;
                    }
                }
                object.setPoinOnCanvas(point);
                prevPoint.x = point.x;
                prevPoint.y = point.y;
                ++counter;
                if (counter == results.size()) {
                    listener.setCanvasSize(point.x + rainDropRadius + lenghtOfText);
                }
            }
            drawFlag = true;
            r = 0;
            listener.enableEditText();
        }
    }

    @Override
    public void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        drawBackGround(canvas);
        Paint paint = mPaint;
        paint.setColor(Color.RED);
        paint.setStyle(Paint.Style.FILL);
        ColorMatrix cm = new ColorMatrix();
        mAngle += 4;
        if (mAngle > 180) {
            mAngle = 0;
        } else {
            invalidate();
        }
        //convert our animated angle [-180...180] to a contrast value of [-1..1]
        float contrast = mAngle / 180.f;
        setContrast(cm, contrast);
        paint.setColorFilter(new ColorMatrixColorFilter(cm));
        int counter = 1;
        int height = (backTile.getHeight() < getHeight()) ? backTile.getHeight() : getHeight();
        if (mResults != null) {
            Iterator<SearchSuggestionsEntity> itr = mResults.iterator();
            while (itr.hasNext()) {
                SearchSuggestionsEntity entity = itr.next();
                Point point = entity.getPoinOnCanvas();
                if (point != null) {
                    Point pointToMagnify = getPointToMagnify(point);
                    int x = pointToMagnify.x;
                    int y = pointToMagnify.y;
                    Bitmap bitmapClipped = magnify(x, y);
                    canvas.drawBitmap(bitmapClipped, point.x - x, point.y - y, paint);
                    if (mResults != null && ((SearchSuggestionsEntity)mResults.get(counter - 1)).getName() != null)
                        canvas.drawText(((SearchSuggestionsEntity)mResults.get(counter - 1)).getName(), counter * (3 * rainDropRadius), y, textPaint);
                    ++counter;
                }
            }
        }

    }

    private Point getPointToMagnify(Point point) {
        int x = point.x;
        int y = point.y;
        if (x > backTile.getWidth()) {
            x = x % backTile.getWidth();
        }
        if ((backTile.getWidth() - x) < 2 * rainDropRadius)
            x = backTile.getWidth() - 2 * rainDropRadius;
        if (x < 2 * rainDropRadius) x = 2 * rainDropRadius;
        if (y > backTile.getHeight()) {
            y = y % backTile.getHeight();
        }
        if ((backTile.getHeight() - y) < 2 * rainDropRadius)
            y = backTile.getHeight() - 2 * rainDropRadius;
        if (y < 2 * rainDropRadius) y = 2 * rainDropRadius;
        return new Point(x, y);
    }

    private static void setContrast(ColorMatrix cm, float contrast) {
        float scale = contrast + 1.f;
        float translate = (-.5f * scale + .5f) * 255.f;
        cm.set(new float[]{
                scale, 0, 0, 0, 0,
                0, scale, 0, 0, 0,
                0, 0, scale, 0, 0,
                0, 0, 0, 1, 0});
    }

    private void drawBackGround(Canvas canvas) {
        int width = this.getWidth();
        int ht = this.getHeight();
        int n = width / backTile.getWidth();
        int m = ht / backTile.getHeight();
        if (m > 0 && n > 0) {
            for (int j = 0; j < m; j++) {
                for (int i = 0; i < n; i++) {
                    canvas.drawBitmap(backTile, i * backTile.getWidth(), j * backTile.getHeight(), null);
                }
            }
        } else if (m == 0) {
            for (int i = 0; i <= n; i++) {
                canvas.drawBitmap(backTile, i * backTile.getWidth(), 0, null);
            }
        } else if (n == 0) {
            for (int j = 0; j < m; j++) {
                canvas.drawBitmap(backTile, 0, j * backTile.getHeight(), null);
            }
        }
    }

    private Bitmap magnify(int pivotX, int pivotY) {
        final int width = backTile.getWidth();
        final int height = backTile.getHeight();
        bmp = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
        final Path path = new Path();
        int x = pivotX;
        int y = pivotY;
        if (r < rainDropRadius) {
            r += rInc;
            invalidate();
        } else if (r == rainDropRadius) {
            drawFlag = false;
        }
        path.addCircle(
                x
                , y
                , r - r / 5
                , Path.Direction.CCW);
        final Canvas canvas = new Canvas(bmp);
        Paint paint = new Paint();
        paint.setColor(Color.RED);
        paint.setStyle(Paint.Style.FILL);
        paint.setShadowLayer(1f, 0f, 1f, Color.WHITE);
        int i = r;
        int a = x - i;
        int b = y - i;
        int c = x + i;
        int d = y + i;
        Rect rect = new Rect(a, b, c, d);
        i = r / 2;
        a = x - i;
        b = y - i;
        c = x + i;
        d = y + i;
        Rect rect2 = new Rect(a, b, c, d);
        canvas.clipPath(path, Region.Op.INTERSECT);
        canvas.drawBitmap(backTile, rect2, rect, null);
        return bmp;
    }
}
