package com.teamagam.gimelgimel.app.common.base.view;

import android.content.Context;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.teamagam.gimelgimel.R;
import com.teamagam.gimelgimel.domain.map.entities.geometries.PointGeometry;

import butterknife.BindView;
import butterknife.ButterKnife;

public class LongLatPicker extends LinearLayout {

    private static final String LONGLAT_NAMESPACE = "http://schemas.android.com/apk/res-auto";
    private static final String LABEL_ATTRIBUTE_STRING = "label";
    private static final int MIN_LAT_VALUE = -90;
    private static final int MAX_LAT_VALUE = 90;
    private static final int MIN_LONG_VALUE = -180;
    private static final int MAX_LONG_VALUE = 180;
    private static final NoOpListener NO_OP_LISTENER = new NoOpListener();

    @BindView(R.id.long_lat_picker_long)
    EditText mLongEditText;

    @BindView(R.id.long_lat_picker_lat)
    EditText mLatEditText;

    @BindView(R.id.long_lat_picker_text_view)
    TextView mLabelTextView;

    private OnValidStateChangedListener mListener;

    public LongLatPicker(Context context, AttributeSet attrs) {
        super(context, attrs);
        LayoutInflater inflater = LayoutInflater.from(context);
        View inflate = inflater.inflate(R.layout.long_lat_picker, this);

        ButterKnife.bind(inflate, this);

        mListener = NO_OP_LISTENER;

        mLatEditText.addTextChangedListener(new ValidityTextWatcher());
        mLongEditText.addTextChangedListener(new ValidityTextWatcher());

        mLatEditText.addTextChangedListener(new MinMaxTextWatcher(MIN_LAT_VALUE, MAX_LAT_VALUE));
        mLongEditText.addTextChangedListener(new MinMaxTextWatcher(MIN_LONG_VALUE, MAX_LONG_VALUE));

        initLabel(attrs);
    }


    public boolean hasPoint() {
        return !getLat().isNaN() && !getLong().isNaN();
    }

    public PointGeometry getPoint() {
        return new PointGeometry(getLat(), getLong());
    }

    public void setOnValidStateChangedListener(OnValidStateChangedListener listener) {
        if (listener == null) {
            mListener = NO_OP_LISTENER;
        } else {
            mListener = listener;
        }
    }

    private void initLabel(AttributeSet attrs) {
        String labelString = getLabelString(attrs);
        if (labelString == null || labelString.isEmpty()) {
            mLabelTextView.setVisibility(GONE);
        } else {
            mLabelTextView.setText(labelString);
        }
    }

    private String getLabelString(AttributeSet attrs) {
        int resId = attrs.getAttributeResourceValue(LONGLAT_NAMESPACE, LABEL_ATTRIBUTE_STRING, 0);
        if (resId != 0) {
            return getContext().getResources().getString(resId);
        } else {
            return attrs.getAttributeValue(LONGLAT_NAMESPACE, LABEL_ATTRIBUTE_STRING);
        }
    }

    private Float getLong() {
        return getNumeric(mLongEditText);
    }

    private Float getLat() {
        return getNumeric(mLatEditText);
    }

    private Float getNumeric(EditText editText) {
        String editTextString = editText.getText().toString();
        return isNumeric(editTextString) ? Float.valueOf(editTextString) : Float.NaN;
    }

    private static boolean isNumeric(String s) {
        try {
            Double.parseDouble(s);
            return true;
        } catch (NumberFormatException nfe) {
            return false;
        }
    }

    public interface OnValidStateChangedListener {
        void onValid();

        void onInvalid();
    }

    private static class NoOpListener implements OnValidStateChangedListener {
        @Override
        public void onValid() {

        }

        @Override
        public void onInvalid() {

        }
    }

    private static class MinMaxTextWatcher implements TextWatcher {

        private double mMin;
        private double mMax;

        MinMaxTextWatcher(double min, double max) {
            mMin = min;
            mMax = max;
        }

        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {

        }

        @Override
        public void afterTextChanged(Editable s) {
            String text = s.toString();
            if (isNumeric(text)) {
                double val = Double.parseDouble(text);
                if (val > mMax) {
                    changeText(s, mMax);
                } else if (val < mMin) {
                    changeText(s, mMin);
                }
            }
        }

        private void changeText(Editable s, double number) {
            s.replace(0, s.length(), Double.toString(number));
        }
    }

    private class ValidityTextWatcher implements TextWatcher {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {

        }

        @Override
        public void afterTextChanged(Editable s) {
            if (hasPoint()) {
                mListener.onValid();
            } else {
                mListener.onInvalid();
            }
        }
    }
}