package com.madrobot.ui.widgets;

import java.util.regex.Pattern;

import android.content.Context;
import android.telephony.PhoneNumberFormattingTextWatcher;
import android.text.InputFilter;
import android.text.InputType;
import android.text.Spanned;
import android.util.AttributeSet;
import android.widget.TextView;

import com.madrobot.ui.widgets.EditTexts.CustomEditText;

/**
 * EditText that formats the text entered like phone numbers
 * @author elton.stephen.kent
 *
 */
public class PhoneEditText extends CustomEditText<String> {
	private class LeadingOnesLengthFilter extends EditTexts.DigitsLengthFilter {

		public LeadingOnesLengthFilter(int max) {
			super(max);
		}
		
		@Override
		protected String getFormattedDigits(Spanned dest) {
			return getValue();
		}
		
		@Override
		protected boolean isEdgeCase(CharSequence source) {
			return "".equals(EditTexts.extractNumber(source.toString()));
		}
	}
	
	private static final int LENGTH = 10;

	protected static String extractWithoutLeadingOne(TextView tv) {
		String number = EditTexts.extractDigits(tv);
		return Pattern.compile("^1").matcher(number).replaceFirst("");
	}

	public PhoneEditText(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	public PhoneEditText(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
	}

	@Override
	protected void doCustomize(Context context) {
		setInputType(InputType.TYPE_CLASS_PHONE);
//		setTag("Phone");
//		setHint("Phone #");
		setFilters(new InputFilter[] {new LeadingOnesLengthFilter(LENGTH)});
		addTextChangedListener(new PhoneNumberFormattingTextWatcher());
	}
	
	@Override
	public String getValue() {
		return extractWithoutLeadingOne(this);
	}
	
	@Override
	public boolean validate() {
		return LENGTH == getValue().length();
	}
}