package br.com.sapereaude.maskedEditText;

import android.content.Context;
import android.content.res.TypedArray;
import android.text.Editable;
import android.text.SpannableStringBuilder;
import android.text.TextWatcher;
import android.text.style.ForegroundColorSpan;
import android.util.AttributeSet;
import android.view.KeyEvent;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;


public class MaskedEditText extends EditText implements TextWatcher {

    public static final String SPACE = " ";
    private String mask;
	private char charRepresentation;
	private boolean keepHint;
	private int[] rawToMask;
	private RawText rawText;
	private boolean editingBefore;
	private boolean editingOnChanged;
	private boolean editingAfter;
	private int[] maskToRaw;
	private char[] charsInMask;
	private int selection;
	private boolean initialized;
	private boolean ignore;
	protected int maxRawLength;
	private int lastValidMaskPosition;
	private boolean selectionChanged;
	private OnFocusChangeListener focusChangeListener;
    private String allowedChars;
    private String deniedChars;

    public MaskedEditText(Context context) {
		super(context);
		init();
	}
	
	public MaskedEditText(Context context, AttributeSet attrs) {
		super(context, attrs);
		init();
		
		TypedArray attributes = context.obtainStyledAttributes(attrs, R.styleable.MaskedEditText);
		mask = attributes.getString(R.styleable.MaskedEditText_mask);

        allowedChars = attributes.getString(R.styleable.MaskedEditText_allowed_chars);
        deniedChars = attributes.getString(R.styleable.MaskedEditText_denied_chars);

		String representation = attributes.getString(R.styleable.MaskedEditText_char_representation);
		
		if(representation == null) {
			charRepresentation = '#';
		}
		else {
			charRepresentation = representation.charAt(0);
		}

		keepHint = attributes.getBoolean(R.styleable.MaskedEditText_keep_hint, false);
		
		cleanUp();
		
		// Ignoring enter key presses
		setOnEditorActionListener(new OnEditorActionListener() {
			@Override
			public boolean onEditorAction(TextView v, int actionId,KeyEvent event) {
				switch (actionId) {
//				case EditorInfo.IME_ACTION_NEXT:
					// fixing actionNext
//					return false;
				default:
					return true;
				}
			}
		});
		attributes.recycle();
	}

	/** @param listener - its onFocusChange() method will be called before performing MaskedEditText operations, 
	 * related to this event. */
	@Override
	public void setOnFocusChangeListener(OnFocusChangeListener listener) {
		focusChangeListener = listener;
	}
	
	private void cleanUp() {
		initialized = false;
		
		generatePositionArrays();
		
		rawText = new RawText();
		selection = rawToMask[0];

		editingBefore = true;
		editingOnChanged = true;
		editingAfter = true;
		if(hasHint() && rawText.length() == 0) {
            this.setText(makeMaskedTextWithHint());
		} else {
            this.setText(makeMaskedText());
		}
		editingBefore = false;
		editingOnChanged = false;
		editingAfter = false;
		
		maxRawLength = maskToRaw[previousValidPosition(mask.length() - 1)] + 1;
		lastValidMaskPosition = findLastValidMaskPosition();
		initialized = true;
		
		super.setOnFocusChangeListener(new OnFocusChangeListener() {
			@Override
			public void onFocusChange(View v, boolean hasFocus) {
				if (focusChangeListener != null) {
					focusChangeListener.onFocusChange(v, hasFocus);
				}
					
				if (hasFocus()) {
					selectionChanged = false;
					MaskedEditText.this.setSelection(lastValidPosition());
				}
			}
		});
	}

	private int findLastValidMaskPosition() {
		for(int i = maskToRaw.length - 1; i >= 0; i--) {
			if(maskToRaw[i] != -1) return i;
		}
		throw new RuntimeException("Mask must contain at least one representation char");
	}

	private boolean hasHint() {
		return getHint() != null;
	}
	
	public MaskedEditText(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		init();
	}
	
	public void setMask(String mask) {
		this.mask = mask;
		cleanUp();
	}
	
	public String getMask() {
		return this.mask;
	}

	public String getRawText() {
		return this.rawText.getText();
	}
	
	public void setCharRepresentation(char charRepresentation) {
		this.charRepresentation = charRepresentation;
		cleanUp();
	}
	
	public char getCharRepresentation() {
		return this.charRepresentation;
	}

    /**
     *  Generates positions for values characters. For instance:
     *  Input data: mask = "+7(###)###-##-##
     *  After method execution:
     *  rawToMask = [3, 4, 5, 6, 8, 9, 11, 12, 14, 15]
     *  maskToRaw = [-1, -1, -1, 0, 1, 2, -1, 3, 4, 5, -1, 6, 7, -1, 8, 9]
     *  charsInMask = "+7()- " (and space, yes)
     */
	private void generatePositionArrays() {
		int[] aux = new int[mask.length()];
		maskToRaw = new int[mask.length()];
		String charsInMaskAux = "";
		
		int charIndex = 0;
		for(int i = 0; i < mask.length(); i++) {
			char currentChar = mask.charAt(i);
			if(currentChar == charRepresentation) {
				aux[charIndex] = i;
				maskToRaw[i] = charIndex++;
			}
			else {
				String charAsString = Character.toString(currentChar);
				if(!charsInMaskAux.contains(charAsString)) {
					charsInMaskAux = charsInMaskAux.concat(charAsString);
				}
				maskToRaw[i] = -1;
			}
		}
		if(charsInMaskAux.indexOf(' ') < 0) {
			charsInMaskAux = charsInMaskAux + SPACE;
		}
		charsInMask = charsInMaskAux.toCharArray();
		
		rawToMask = new int[charIndex];
		for (int i = 0; i < charIndex; i++) {
			rawToMask[i] = aux[i];
		}
	}
	
	private void init() {
		addTextChangedListener(this);
	}
	
	@Override
	public void beforeTextChanged(CharSequence s, int start, int count,
			int after) {
		if(!editingBefore) {
			editingBefore = true;
			if(start > lastValidMaskPosition) {
				ignore = true;
			}
			int rangeStart = start;
			if(after == 0) {
				rangeStart = erasingStart(start);
			}
			Range range = calculateRange(rangeStart, start + count);
			if(range.getStart() != -1) {
				rawText.subtractFromString(range);
			}
			if(count > 0) {
				selection = previousValidPosition(start);
			}
		}
	}

	private int erasingStart(int start) {
		while(start > 0 && maskToRaw[start] == -1) {
			start--;
		}
		return start;
	}

	@Override
	public void onTextChanged(CharSequence s, int start, int before, int count) {
		if(!editingOnChanged && editingBefore) {
			editingOnChanged = true;
			if(ignore) {
				return;
			}
			if(count > 0) {
				int startingPosition = maskToRaw[nextValidPosition(start)];
				String addedString = s.subSequence(start, start + count).toString();
				count = rawText.addToString(clear(addedString), startingPosition, maxRawLength);
				if(initialized) {
					int currentPosition;
					if(startingPosition + count < rawToMask.length) 
						currentPosition = rawToMask[startingPosition + count];
					else
						currentPosition = lastValidMaskPosition + 1;
					selection = nextValidPosition(currentPosition);
				}
			}
		}
	}

	@Override
	public void afterTextChanged(Editable s) {
		if(!editingAfter && editingBefore && editingOnChanged) {
			editingAfter = true;
            if (hasHint() && (keepHint || rawText.length() == 0)) {
                setText(makeMaskedTextWithHint());
			} else {
                setText(makeMaskedText());
            }
			
			selectionChanged = false;
			setSelection(selection);
			
			editingBefore = false;
			editingOnChanged = false;
			editingAfter = false;
			ignore = false;
		}
	}
	
	@Override
	protected void onSelectionChanged(int selStart, int selEnd) {
		// On Android 4+ this method is being called more than 1 time if there is a hint in the EditText, what moves the cursor to left
		// Using the boolean var selectionChanged to limit to one execution
		if(initialized ){
			if(!selectionChanged) {
                selStart = fixSelection(selStart);
                selEnd = fixSelection(selEnd);
				setSelection(selStart, selEnd);
				selectionChanged = true;
			} else{
			    //check to see if the current selection is outside the already entered text
				if(selStart > rawText.length() - 1){
					setSelection(fixSelection(selStart),fixSelection(selEnd));
				}
			}
		}
		super.onSelectionChanged(selStart, selEnd);
	}
	
	private int fixSelection(int selection) {
		if(selection > lastValidPosition()) {
			return lastValidPosition();
		} 
		else {
			return nextValidPosition(selection);
		}
	}

	private int nextValidPosition(int currentPosition) {
		while(currentPosition < lastValidMaskPosition && maskToRaw[currentPosition] == -1) {
			currentPosition++;
		}
		if(currentPosition > lastValidMaskPosition) return lastValidMaskPosition + 1;
		return currentPosition;
	}
	
	private int previousValidPosition(int currentPosition) {
		while(currentPosition >= 0 && maskToRaw[currentPosition] == -1) {
			currentPosition--;
			if(currentPosition < 0) {
				return nextValidPosition(0);
			}
		}
		return currentPosition;
	}
	
	private int lastValidPosition() {
		if(rawText.length() == maxRawLength) {
			return rawToMask[rawText.length() - 1] + 1;
		}
		return nextValidPosition(rawToMask[rawText.length()]);
	}
	
	private String makeMaskedText() {
        int maskedTextLength;
        if (rawText.length() < rawToMask.length) {
            maskedTextLength = rawToMask[rawText.length()];
        } else {
            maskedTextLength = mask.length();
        }
		char[] maskedText = new char[maskedTextLength]; //mask.replace(charRepresentation, ' ').toCharArray();
        for (int i = 0; i < maskedText.length; i++) {
            int rawIndex = maskToRaw[i];
            if (rawIndex == -1) {
                maskedText[i] = mask.charAt(i);
            } else {
                maskedText[i] = rawText.charAt(rawIndex);
            }
        }
		return new String(maskedText);
	}

    private CharSequence makeMaskedTextWithHint() {
        SpannableStringBuilder ssb = new SpannableStringBuilder();
        int mtrv;
        int maskFirstChunkEnd = rawToMask[0];
        for(int i = 0; i < mask.length(); i++) {
            mtrv = maskToRaw[i];
            if (mtrv != -1) {
                if (mtrv < rawText.length()) {
                    ssb.append(rawText.charAt(mtrv));
                } else {
                    ssb.append(getHint().charAt(maskToRaw[i]));
                }
            } else {
                ssb.append(mask.charAt(i));
            }
            if ((keepHint && rawText.length() < rawToMask.length && i >= rawToMask[rawText.length()])
                    || (!keepHint && i >= maskFirstChunkEnd)) {
                ssb.setSpan(new ForegroundColorSpan(getCurrentHintTextColor()), i, i + 1, 0);
            }
        }
        return ssb;
    }

	private Range calculateRange(int start, int end) {
		Range range = new Range();
		for(int i = start; i <= end && i < mask.length(); i++) {
			if(maskToRaw[i] != -1) {
				if(range.getStart() == -1) {
					range.setStart(maskToRaw[i]);
				}
				range.setEnd(maskToRaw[i]);
			}
		}
		if(end == mask.length()) {
			range.setEnd(rawText.length());
		}
		if(range.getStart() == range.getEnd() && start < end) {
			int newStart = previousValidPosition(range.getStart() - 1);
			if(newStart < range.getStart()) {
				range.setStart(newStart);
			}
		}
		return range;
	}
	
	private String clear(String string) {
        if (deniedChars != null){
            for(char c: deniedChars.toCharArray()){
			    string = string.replace(Character.toString(c), "");
            }
        }

        if (allowedChars != null){
            StringBuilder builder = new StringBuilder(string.length());

            for(char c: string.toCharArray() ){
                if (allowedChars.contains(String.valueOf(c) )){
                    builder.append(c);
                }
            }

            string = builder.toString();
        }

		return string;
	}
}
