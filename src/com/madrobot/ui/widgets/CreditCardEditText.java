package com.madrobot.ui.widgets;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import android.content.Context;
import android.text.Editable;
import android.text.InputFilter;
import android.text.InputType;
import android.text.Spanned;
import android.util.AttributeSet;

import com.madrobot.ui.widgets.EditTexts.DigitsLengthFilter;
import com.madrobot.ui.widgets.EditTexts.DigitsLengthFilter.MaxDigitsGetter;

/**
 * An EditText that formats text like credit card numbers
 * @author elton.stephen.kent
 *
 */
public class CreditCardEditText extends com.madrobot.ui.widgets.EditTexts.FormattedEditText<String> {
	public static final String AMEX_CREDIT = "AMEX_CREDIT";
	public static final String AMEX_DEBIT = "AMEX_DEBIT";

	public static final String BANK_ACCOUNT = "BANK_ACCOUNT";
	private static Map<Integer, String> cardTypes = new HashMap<Integer, String>();
	public static final String CREDIT_CARD = "_CREDIT";
	public static final String DEBIT_CARD = "_DEBIT";
	public static final String DISCOVER_CREDIT = "DISCOVER_CREDIT";
	public static final String DISCOVER_DEBIT = "DISCOVER_DEBIT";
	private static final int LENGTH = 16;
	private static final int LENGTH_AMEX = 15;
	public static final String MASTERCARD_CREDIT = "MASTERCARD_CREDIT";
	public static final String MASTERCARD_DEBIT = "MASTERCARD_DEBIT";
	
	public static final String REVOLUTIONCARD = "REVOLUTIONCARD";
	public static final String VISA_CREDIT = "VISA_CREDIT";
	
	public static final String VISA_DEBIT = "VISA_DEBIT";
	static {
		cardTypes.put(4, VISA_CREDIT);
		cardTypes.put(3, AMEX_CREDIT);
		cardTypes.put(6011, DISCOVER_CREDIT);
		cardTypes.put(5, MASTERCARD_CREDIT);
	}

	public static String getMaskedLast4(String last4) {
		return "**** **** **** " + last4;
	}

	private String source = "";

	public CreditCardEditText(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	public CreditCardEditText(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
	}

	@Override
	protected void doCustomize(Context context) {
		setInputType(InputType.TYPE_CLASS_PHONE);
		setFilters(new InputFilter[] { new DigitsLengthFilter(
				new MaxDigitsGetter() {
					@Override
					public int getMax(CharSequence source) {
						return getMaxLength();
					}
				}) {
			@Override
			protected String getFormattedDigits(Spanned dest) {
				return source;
			}
		} });
//		setHint("Card Number");
		// setHint("xxxx xxxx xxxx xxxx");
//		setTag("Card Number");
		super.doCustomize(context);
		addTextChangedListener(new FormattedTextWatcher() {
			@Override
			public void onTextChanged(CharSequence arg0, int arg1, int arg2,
					int arg3) {
				int sourceIndex = arg0.toString().replace(" ", "").length();
				if (arg3 == 1 && arg2 == 0) {
					source = source.subSequence(0, sourceIndex - 1).toString()
							+ arg0.subSequence(arg0.length() - 1, arg0.length());
				} else if (arg3 == 0 && arg2 == 1) {
					source = source.subSequence(0, sourceIndex).toString();
				}
			}
		});
	}

	private String doObscure(String formatted) {
		char[] chars = formatted.toCharArray();
		int trailing = (getCardType() != null && getCardType()
				.equalsIgnoreCase(AMEX_CREDIT)) ? 3 : 4;

		for (int i = formatted.length(); i > 0; i--) {
			char c = chars[i - 1];
			if (c != ' ' && i < getMaxLength(true, true) - trailing
					&& i < formatted.length())
				chars[i - 1] = '*';
		}
		return new String(chars);
	}

	@Override
	protected void format(Editable arg0) {
		CharSequence dollars = doObscure(getFormatted(getValue()));
		replaceText(arg0, dollars);
	}

	public String getCardType() {
		if (getValue().length() == 0)
			return null;
		String actual = getValue();
		String digit = actual.substring(0, 1);
		if (actual.length() > 0) {
			try {
				if (digit.equals("6") && actual.length() > 3) {
					digit = actual.substring(0, 4);
				} else {
					digit = actual.substring(0, 1);
				}
				return cardTypes.get(Integer.parseInt(digit));
			} catch (Exception e) {
				return null;
			}
		} else
			return null;
	}

	@Override
	protected String getFormatted(String typedText) {
		String regex = "(\\d{0,4})(\\d{0,4})(\\d{0,4})(\\d{0,4})";
		String replace = "$1 $2 $3 $4";
		if (getCardType() != null
				&& getCardType()
						.equalsIgnoreCase(AMEX_CREDIT)) {
			regex = "(\\d{0,4})(\\d{0,6})(\\d{0,5})";
			replace = "$1 $2 $3";
		}
		Matcher matcher = Pattern.compile(regex).matcher(typedText);
		return matcher.replaceFirst(replace).trim();
	}

	protected int getMaxLength() {
		return getMaxLength(false, true);
	}

	protected int getMaxLength(boolean padded, boolean isCredit) {
		if (isCredit
				&& getCardType() != null
				&& getCardType()
						.equalsIgnoreCase(AMEX_CREDIT))
			return LENGTH_AMEX + (padded ? 2 : 0);
		else
			return LENGTH + (padded ? 3 : 0);
	}

	@Override
	public String getValue() {
		return source;
	}

	@Override
	protected void setFocus() {
		String text = getText().toString();
		text = Pattern.compile("[\\s]+$").matcher(text).replaceFirst("");
		setSelection(text.length());
	}

	@Override
	public boolean validate() {
		return getValue().length() == getMaxLength();
	}

	public boolean validate(boolean isCredit) {
		return getValue().length() == getMaxLength(false, isCredit) && getValue().matches("\\d{13,16}");//check if the card number is all numeric
		}

	public boolean validateCardType(boolean isCredit) {
		String cardType = getCardType();
		if (cardType == null)
			return false;
		if (!isCredit)
			return cardType.equalsIgnoreCase(VISA_CREDIT)
					|| cardType
							.equalsIgnoreCase(MASTERCARD_CREDIT);
		return true;
	}
}