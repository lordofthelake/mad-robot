package com.madrobot.ui.widgets;

import java.util.regex.Pattern;

import android.content.Context;
import android.text.Editable;
import android.text.InputFilter;
import android.text.Spanned;
import android.text.TextWatcher;
import android.util.AttributeSet;
import android.widget.EditText;
import android.widget.TextView;

public class EditTexts {	
	public static abstract class CustomEditText<T> extends EditText{
		public CustomEditText(Context context, AttributeSet attrs) {
			super(context, attrs);
			doCustomizeInner(context);
		}

		public CustomEditText(Context context, AttributeSet attrs, int defStyle) {
			super(context, attrs, defStyle);
			doCustomizeInner(context);
		}

		protected abstract void doCustomize(Context context);
		
		private void doCustomizeInner(Context context) {
			//TODO set the hint color
//			setHintTextColor(0x000000);
			doCustomize(context);
		}

		protected String extractNumber() {
			return EditTexts.extractDigits(this);
		}
		public abstract T getValue();
		public abstract boolean validate();
	}
	
    protected static class DigitsLengthFilter implements InputFilter {
    	protected interface MaxDigitsGetter {
    		int getMax(CharSequence source);
    	}
    	
    	private MaxDigitsGetter mMaxGetter;
        public DigitsLengthFilter(final int max) {
            mMaxGetter = new MaxDigitsGetter() {
				@Override
				public int getMax(CharSequence source) {
					return max;
				}
			};
        }
        
        public DigitsLengthFilter(MaxDigitsGetter getter) {
            mMaxGetter = getter;
        }

        @Override
		public CharSequence filter(CharSequence source, int start, int end,
                                   Spanned dest, int dstart, int dend) {
            int keep = mMaxGetter.getMax(source) - (getFormattedDigits(dest).length() - (dend - dstart));

            if (keep == 0 && isEdgeCase(source))
            	return null;
            if (keep <= 0) {
                return "";
            } else if (keep >= end - start) {
                return null; // keep original
            } else {
                return source.subSequence(start, start + keep);
            }
        }
        
        protected String getFormattedDigits(Spanned dest) {
        	return extractNumber(dest.toString());
        }
        
        protected boolean isEdgeCase(CharSequence source) {
        	return false;
        }
    }

	public static abstract class FormattedEditText<T> extends CustomEditText<T> {
		protected class FormattedTextWatcher implements TextWatcher {
			private boolean editing = false;

			@Override
			public synchronized void afterTextChanged(Editable arg0) {
				if (!editing)
				{
					editing = true;
					format(arg0);
				}
				setFocus();
				editing = false;
			}
			
			@Override
			public void beforeTextChanged(CharSequence arg0, int arg1, int arg2,
					int arg3) {
			}
			
			@Override
			public void onTextChanged(CharSequence arg0, int arg1, int arg2, int arg3) {
			}
		}

		public FormattedEditText(Context context, AttributeSet attrs) {
			super(context, attrs);
		}
		
		public FormattedEditText(Context context, AttributeSet attrs, int defStyle) {
			super(context, attrs, defStyle);
		}

		@Override
		protected void doCustomize(Context context) {
			addTextChangedListener(new FormattedTextWatcher());
		}
		
		protected void format(Editable arg0) {
			CharSequence dollars = getFormatted(getValue());
			replaceText(arg0, dollars);
		}
		
		protected abstract CharSequence getFormatted(T typedText);
		
		protected void replaceText(Editable arg0, CharSequence dollars) {
			if (!dollars.equals(getText().toString()))
				arg0.replace(0, arg0.length(), dollars);
		}
		
		protected void setFocus() { }
	}
	
	public static class NumericFilter implements InputFilter {

		@Override
		public CharSequence filter(CharSequence arg0, int arg1, int arg2,
				Spanned arg3, int arg4, int arg5) {
			return extractNumber(arg0.toString());
		}	
	}
	
	public static String extractDigits(TextView tv) {
		return extractNumber(tv.getText().toString());
	}
	
	public static String extractNumber(String tv) {
		String expression="\\D";   
		Pattern pattern = Pattern.compile(expression,Pattern.CASE_INSENSITIVE);  
		return pattern.matcher(tv).replaceAll("");
	}
}
