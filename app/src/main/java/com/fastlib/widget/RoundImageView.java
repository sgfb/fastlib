package com.fastlib.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.BitmapShader;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.graphics.Shader;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.media.ThumbnailUtils;
import android.support.v7.widget.AppCompatImageView;
import android.util.AttributeSet;
import android.util.Log;
import android.widget.RemoteViews;

import com.fastlib.R;

/**
 * Created by Mikhael LOPEZ on 09/10/2015.
 */
@RemoteViews.RemoteView
public class RoundImageView extends AppCompatImageView {
    public static final int TYPE_CIRCLE=0;
    public static final int TYPE_ROUND_RECT=1;
    // Properties
    private int type;
    private int borderWidth;
    private int canvasX;
    private int canvasY;
    private int roundX;
    private int roundY;

    // Object used to draw
    private Bitmap image;
    private Drawable drawable;
    private Paint paint;
    private Paint paintBorder;
    private RectF mRect=new RectF();

    //region Constructor & Init Method
    public RoundImageView(final Context context) {
        this(context, null);
    }

    public RoundImageView(Context context, AttributeSet attrs) {
        this(context, attrs, R.attr.circularImageViewStyle);
    }

    public RoundImageView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context, attrs, defStyleAttr);
    }

    private void init(Context context, AttributeSet attrs, int defStyleAttr) {
        // Init paint
        paint = new Paint();
        paint.setAntiAlias(true);

        paintBorder = new Paint();
        paintBorder.setAntiAlias(true);

        // Load the styled attributes and set their properties
        TypedArray attributes = context.obtainStyledAttributes(attrs, R.styleable.RoundImageView, defStyleAttr, 0);

        // Init Border
        if (attributes.getBoolean(R.styleable.RoundImageView_border,false)) {
            int defaultBorderSize = (int) (4 * getContext().getResources().getDisplayMetrics().density + 0.5f);
            setBorderWidth(attributes.getDimensionPixelOffset(R.styleable.RoundImageView_border_width, defaultBorderSize));
            setBorderColor(attributes.getColor(R.styleable.RoundImageView_border_color, Color.WHITE));
        }

        // Init Shadow
        if (attributes.getBoolean(R.styleable.RoundImageView_shadow, false)) {
            addShadow();
        }
        type=attributes.getInteger(R.styleable.RoundImageView_type,0);
        roundX=attributes.getInteger(R.styleable.RoundImageView_round_x,5);
        roundY=attributes.getInteger(R.styleable.RoundImageView_round_y,5);
        attributes.recycle();
    }
    //endregion

    //region Set Attr Method
    public void setBorderWidth(int borderWidth) {
        this.borderWidth = borderWidth;
        this.requestLayout();
        this.invalidate();
    }

    public void setBorderColor(int borderColor) {
        if (paintBorder != null)
            paintBorder.setColor(borderColor);
        this.invalidate();
    }

    public void addShadow() {
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.HONEYCOMB) {
            setLayerType(LAYER_TYPE_SOFTWARE, paintBorder);
        }
        paintBorder.setShadowLayer(4.0f, 0.0f, 2.0f, Color.BLACK);
    }
    //endregion

    //region Draw Method
    @Override
    public void onDraw(Canvas canvas) {
        // Load the bitmap
        loadBitmap();

        // Check if image isn't null
        if (image == null)
            return;

        canvasX = canvas.getWidth();
        canvasY=canvas.getHeight();
        // circleCenter is the x or y of the view's center
        // radius is the radius in pixels of the cirle to be drawn
        // paint contains the shader that will texture the shape
        if(type==TYPE_CIRCLE){
            int canvasSize=Math.min(canvasY,canvasX);
            int circleCenter = (canvasSize - (borderWidth * 2)) / 2;
            canvas.drawCircle(circleCenter + borderWidth, circleCenter + borderWidth, circleCenter + borderWidth - 4.0f, paintBorder);
            canvas.drawCircle(circleCenter + borderWidth, circleCenter + borderWidth, circleCenter - 4.0f, paint);
        }
        else if(type==TYPE_ROUND_RECT){
            mRect.set(0,0,canvas.getWidth(),canvas.getHeight());
            canvas.drawRoundRect(mRect,roundX,roundY,paint);
        }
    }

    private void loadBitmap() {
        if (this.drawable == getDrawable())
            return;

        this.drawable = getDrawable();
        this.image = drawableToBitmap(this.drawable);
        updateShader();
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        canvasX = w;
        canvasY=h;
        if (image != null)
            updateShader();
    }

    private void updateShader() {
        if (this.image == null)
            return;
        BitmapShader shader = new BitmapShader(Bitmap.createScaledBitmap(
                ThumbnailUtils.extractThumbnail(image, canvasX,
                        canvasY), canvasX, canvasY, false),
                Shader.TileMode.CLAMP, Shader.TileMode.CLAMP);
        paint.setShader(shader);
    }

    private Bitmap drawableToBitmap(Drawable drawable) {
        if (drawable == null) {
            return null;
        } else if (drawable instanceof BitmapDrawable) {
            return ((BitmapDrawable) drawable).getBitmap();
        }

        int intrinsicWidth = drawable.getIntrinsicWidth();
        int intrinsicHeight = drawable.getIntrinsicHeight();

        if (!(intrinsicWidth > 0 && intrinsicHeight > 0))
            return null;

        try {
            // Create Bitmap object out of the drawable
            Bitmap bitmap = Bitmap.createBitmap(intrinsicWidth, intrinsicHeight, Bitmap.Config.ARGB_8888);
            Canvas canvas = new Canvas(bitmap);
            drawable.setBounds(0, 0, canvas.getWidth(), canvas.getHeight());
            drawable.draw(canvas);
            return bitmap;
        } catch (OutOfMemoryError e) {
            // Simply return null of failed bitmap creations
            Log.e(getClass().toString(), "Encountered OutOfMemoryError while generating bitmap!");
            return null;
        }
    }
    //endregion

    //region Mesure Method
    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int width = measureWidth(widthMeasureSpec);
        int height = measureHeight(heightMeasureSpec);
        setMeasuredDimension(width, height);
    }

    private int measureWidth(int measureSpec) {
        int result;
        int specMode = MeasureSpec.getMode(measureSpec);
        int specSize = MeasureSpec.getSize(measureSpec);

        if (specMode == MeasureSpec.EXACTLY) {
            // The parent has determined an exact size for the child.
            result = specSize;
        } else if (specMode == MeasureSpec.AT_MOST) {
            // The child can be as large as it wants up to the specified size.
            result = specSize;
        } else {
            // The parent has not imposed any constraint on the child.
            result = canvasX;
        }

        return result;
    }

    private int measureHeight(int measureSpecHeight) {
        int result;
        int specMode = MeasureSpec.getMode(measureSpecHeight);
        int specSize = MeasureSpec.getSize(measureSpecHeight);

        if (specMode == MeasureSpec.EXACTLY) {
            // We were told how big to be
            result = specSize;
        } else if (specMode == MeasureSpec.AT_MOST) {
            // The child can be as large as it wants up to the specified size.
            result = specSize;
        } else {
            // Measure the text (beware: ascent is a negative number)
            result = canvasX;
        }

        return (result + 2);
    }
    //endregion
}
