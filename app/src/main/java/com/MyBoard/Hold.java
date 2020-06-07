package com.MyBoard;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.drawable.ShapeDrawable;
import android.graphics.drawable.shapes.OvalShape;
import android.view.MotionEvent;
import android.view.View;

public class Hold extends View
{
    public ShapeDrawable drawable;
    public int x, y, width, height, lineWidth, colour, prevX, prevY, globalXPos, globalYPos, imageWidth, imageHeight;
    private int mTouchSlop;
    private int mTouchMode;
    public static final int TOUCH_MODE_TAP = 1;
    public static final int TOUCH_MODE_DOWN = 2;
    public boolean isInBounds = true;
    public boolean canImageMove;
    private static final int StartHoldColour = 0xff74AC23;
    private static final int HoldColour = 0xff3895D3;
    private static final int FinishHoldColour = 0xffC21807;
    public boolean issh = false;
    public boolean isfh = false;
    public byte scaling = Byte.MAX_VALUE;

    public Hold(Context context, int xpos, int ypos, int imWidth, int imHeight, int LineWidth)
    {
        super(context);
        this.x = xpos;
        this.y = ypos;
        this.width = imWidth;
        this.height = imHeight;
        this.lineWidth = LineWidth;

        this.colour = HoldColour;
        this.drawable = new ShapeDrawable(new OvalShape());
        this.drawable.getPaint().setColor(HoldColour);
        this.drawable.getPaint().setStrokeWidth(lineWidth);
        this.drawable.getPaint().setStyle(Paint.Style.STROKE);
        this.drawable.setBounds(x, y, x + width, y + height);
    }

    public void setSize(int width)
    {
        drawable.setBounds(drawable.getBounds().left, drawable.getBounds().top, drawable.getBounds().left + width, drawable.getBounds().top + width);
        invalidate();
    }

    @Override
    protected void onDraw(Canvas canvas)
    {
        drawable.draw(canvas);
    }

    public void updateColour()
    {
        if (isfh)
        {
            drawable.getPaint().setColor(FinishHoldColour);
        }
        else if (issh)
        {
            drawable.getPaint().setColor(StartHoldColour);
        }
        else
        {
            drawable.getPaint().setColor(HoldColour);
        }
        invalidate();
    }

    public boolean holdHandleTouch(MotionEvent event)
    {
        int positionX = (int)event.getX();
        int positionY = (int)event.getY();
        switch(event.getAction())
        {
            case MotionEvent.ACTION_DOWN:
            {
                mTouchMode = TOUCH_MODE_DOWN;
                canImageMove = false;
                if(drawable.getBounds().contains(positionX, positionY))
                {
                    prevX = positionX;
                    prevY = positionY;
                    canImageMove = true;
                }
            }
            break;

            case MotionEvent.ACTION_MOVE:
            {
                if(canImageMove)
                {
                    // Check if we have moved far enough that it looks more like a
                    // scroll than a tap
                    final int distY = Math.abs(positionY - prevY);
                    final int distX = Math.abs(positionX - prevX);

                    if (distX > mTouchSlop || distY > mTouchSlop)
                    {
                        int deltaX =  positionX-prevX;
                        int deltaY =  positionY-prevY;

                        // invalidate current position as we are moving...
                        int left = drawable.getBounds().left + deltaX;
                        int top = drawable.getBounds().top + deltaY;
                        int right = left + width;
                        int bottom = top + height;
                        drawable.setBounds(left, top, right, bottom);

                        prevX = positionX;
                        prevY = positionY;
                        invalidate();
                    }
                }
            }
            break;

            case MotionEvent.ACTION_UP:
                canImageMove = false;
                break;
        }
        return true;
    }

    public int scaleReturn()
    {
        int Scale = 100 * scaling/Byte.MAX_VALUE;
        if (Scale < 50)
        {
            Scale = 50;
        }
        if (Scale > 100)
        {
            Scale = 100;
        }
        this.width = Scale;
        this.height = Scale;
        return Scale;
    }

    public void onSelect()
    {
        AddProblem.isStartHold.setVisibility(VISIBLE);
        AddProblem.isFinishHold.setVisibility(VISIBLE);
        AddProblem.increaseScale.setVisibility(VISIBLE);
        AddProblem.decreaseScale.setVisibility(VISIBLE);
        AddProblem.delete.setVisibility(VISIBLE);
        AddProblem.isStartHold.setChecked(issh);
        AddProblem.isFinishHold.setChecked(isfh);
        updateColour();
    }

}