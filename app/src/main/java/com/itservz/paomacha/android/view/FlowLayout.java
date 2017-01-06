package com.itservz.paomacha.android.view;

/**
 * Created by raju.athokpam on 06-01-2017.
 */

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;

public class FlowLayout extends ViewGroup {
    private int line_height;
    private final static String TAG = "FlowLayout";

    public static class FlowLayoutParams extends ViewGroup.LayoutParams {

        public final int horizontal_spacing;
        public final int vertical_spacing;

        /**v
         * @param horizontal_spacing Pixels between items, horizontally
         * @param vertical_spacing Pixels between items, vertically
         */
        public FlowLayoutParams(int horizontal_spacing, int vertical_spacing) {
            super(0, 0);
            this.horizontal_spacing = horizontal_spacing;
            this.vertical_spacing = vertical_spacing;
        }
    }

    public FlowLayout(Context context) {
        super(context);
    }

    public FlowLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        assert (MeasureSpec.getMode(widthMeasureSpec) != MeasureSpec.UNSPECIFIED);

        final int width = MeasureSpec.getSize(widthMeasureSpec) - getPaddingLeft() - getPaddingRight();
        int height = MeasureSpec.getSize(heightMeasureSpec) - getPaddingTop() - getPaddingBottom();
        final int count = getChildCount();
        int line_height = 0;

        int xpos = getPaddingLeft();
        int ypos = getPaddingTop();

        int childHeightMeasureSpec;
        if (MeasureSpec.getMode(heightMeasureSpec) == MeasureSpec.AT_MOST) {
            childHeightMeasureSpec = MeasureSpec.makeMeasureSpec(height, MeasureSpec.AT_MOST);
        } else {
            childHeightMeasureSpec = MeasureSpec.makeMeasureSpec(0, MeasureSpec.UNSPECIFIED);
        }


        for (int i = 0; i < count; i++) {
            final View child = getChildAt(i);
            if (child.getVisibility() != GONE) {
                final LayoutParams lp = child.getLayoutParams();
                child.measure(MeasureSpec.makeMeasureSpec(width, MeasureSpec.AT_MOST), childHeightMeasureSpec);
                final int childw = child.getMeasuredWidth();
                line_height = Math.max(line_height, child.getMeasuredHeight() + lp.height);

                if (xpos + childw > width) {
                    xpos = getPaddingLeft();
                    ypos += line_height;
                }

                xpos += childw + lp.width;
            }
        }
        this.line_height = line_height;

        if (MeasureSpec.getMode(heightMeasureSpec) == MeasureSpec.UNSPECIFIED) {
            height = ypos + line_height;

        } else if (MeasureSpec.getMode(heightMeasureSpec) == MeasureSpec.AT_MOST) {
            if (ypos + line_height < height) {
                height = ypos + line_height;
            }
        }
        setMeasuredDimension(width, height);
    }

    @Override
    protected ViewGroup.LayoutParams generateDefaultLayoutParams() {
        return new LayoutParams(1, 1); // default of 1px spacing
    }

    @Override
    protected boolean checkLayoutParams(ViewGroup.LayoutParams p) {
        if (p instanceof LayoutParams) {
            return true;
        }
        return false;
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        final int count = getChildCount();
        final int width = r - l;
        int xpos = getPaddingLeft();
        int ypos = getPaddingTop();

        for (int i = 0; i < count; i++) {
            final View child = getChildAt(i);
            if (child.getVisibility() != GONE) {
                final int childw = child.getMeasuredWidth();
                final int childh = child.getMeasuredHeight();
                final LayoutParams lp = (LayoutParams) child.getLayoutParams();
                if (xpos + childw > width) {
                    xpos = getPaddingLeft();
                    ypos += line_height;
                }
                child.layout(xpos, ypos, xpos + childw, ypos + childh);
                xpos += childw + lp.width;
            }
        }
    }

    /*private void populateLinks(LinearLayout ll, ArrayList<String> collection, String header) {

        Display display = getWindowManager().getDefaultDisplay();
        int maxWidth = display.getWidth() - 10;

        if (collection.size() > 0) {
            LinearLayout llAlso = new LinearLayout(this);
            llAlso.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT));
            llAlso.setOrientation(LinearLayout.HORIZONTAL);

            TextView txtString = new TextView(this);
            txtString.setText(header);

            llAlso.addView(txtString);
            txtString.measure(0, 0);

            int widthSoFar = txtString.getMeasuredWidth();
            for (String samItem : collection) {
                TextView txtSamItem = new TextView(this, null, android.R.attr.textColorLink);
                txtSamItem.setText(samItem);
                txtSamItem.setPadding(10, 0, 0, 0);
                txtSamItem.setTag(samItem);
                txtSamItem.setOnClickListener(new OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        TextView self = (TextView) v;
                        String ds = (String) self.getTag();
                        Intent myIntent = new Intent();
                        myIntent.putExtra("link_info", ds);
                        setResult("link_clicked", myIntent);
                        finish();
                    }
                });

                txtSamItem.measure(0, 0);
                widthSoFar += txtSamItem.getMeasuredWidth();

                if (widthSoFar >= maxWidth) {
                    ll.addView(llAlso);

                    llAlso = new LinearLayout(this);
                    llAlso.setLayoutParams(new LayoutParams( LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT));
                    llAlso.setOrientation(LinearLayout.HORIZONTAL);

                    llAlso.addView(txtSamItem);
                    widthSoFar = txtSamItem.getMeasuredWidth();
                } else {
                    llAlso.addView(txtSamItem);
                }
            }

            ll.addView(llAlso);
        }
    }*/
}