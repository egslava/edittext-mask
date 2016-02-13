package br.com.sapereaude.maskedEditText;

import android.graphics.Color;
//import android.support.annotation.ColorInt;
import android.support.annotation.NonNull;
import android.text.Editable;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextWatcher;
import android.text.style.ForegroundColorSpan;
import android.widget.EditText;

import java.util.Arrays;

/**
 * Created by samohvalov on 05.02.2016.
 */
public class MaskTextWatcher implements TextWatcher {
    private char emptySymbol;
    private char charRepresentation = '#';
    private EditText editText;
    private String mask;
    private String allowedChars;
    private int[] rawToMask;
    private int[] maskToRaw;
    private RawText rawText;
    private boolean editingBefore;
    private boolean editingOnChanged;
    private boolean editingAfter;
    private boolean initialized;
    private boolean ignore;
    private int selection;
    private int lastValidMaskPosition;
    protected int maxRawLength;
    private ReturnFormatString returnFormatString;
    private int maskStyleColor;
    private int textStyleColor;

    public MaskTextWatcher(@NonNull EditText editText){
        this(editText, '_', null, null);
    }

    public MaskTextWatcher(@NonNull EditText editText, char emptySymbol, String mask, String allowedChars) {
        this.editText = editText;
        this.emptySymbol = emptySymbol == '\u0000' ? '_' : emptySymbol;
        String defaultMask = "##:##";
        this.mask = mask == null ? defaultMask : mask;
        this.allowedChars = allowedChars == null ? "1234567890" : allowedChars;
        this.maskStyleColor = Color.parseColor("#999999");

        this.textStyleColor = Color.parseColor("#000000");
        cleanUp();
    }

    protected void cleanUp() {
        initialized = false;

        generatePositionArrays();

        rawText = new RawText();
        selection = rawToMask[0];

        editingBefore = true;
        editingOnChanged = true;
        editingAfter = true;
        if (hasHint()) {
            editText.setText(null);
        } else {
            editText.setText(makeColorMaskedText());
        }
        editingBefore = false;
        editingOnChanged = false;
        editingAfter = false;

        maxRawLength = maskToRaw[previousValidPosition(mask.length() - 1)] + 1;
        lastValidMaskPosition = findLastValidMaskPosition();
        initialized = true;
    }

    private int findLastValidMaskPosition() {
        for (int i = maskToRaw.length - 1; i >= 0; i--) {
            if (maskToRaw[i] != -1) return i;
        }
        throw new RuntimeException("Mask contains only the representation char");
    }

    private boolean hasHint() {
        return editText.getHint() != null;
    }

    public void setColorMask(int colorMask){
        this.maskStyleColor = colorMask;
    }

    public void setColorText(int colorText){
        this.textStyleColor = colorText;
    }

    public void setColorMask(String colorMask){
        this.maskStyleColor = Color.parseColor(colorMask);
    }

    public void setColorText(String colorText){
        this.textStyleColor = Color.parseColor(colorText);
    }

    public void setMask(String mask) {
        this.mask = mask;
        cleanUp();
    }

    public char getEmptySymbol(){
        return emptySymbol;
    }

    public void setOnReturnStringFormat(ReturnFormatString fString){
        this.returnFormatString = fString;
    }

    protected String getSelectedString(){
        if(editText.getSelectionStart() == 0 && editText.getSelectionEnd() == maskToRaw.length - 1)
            return getString();

        String selectedRaw = editText.getText().toString()
                .substring(editText.getSelectionStart(), editText.getSelectionEnd());
        String selected = "";
        for(char c: selectedRaw.toCharArray()){
            if(!mask.contains(String.valueOf(c))){
                selected = selected.concat(String.valueOf(c));
            }
        }
        if(returnFormatString != null){
            return returnFormatString.getString(selected);
        } else return selected;
    }

    public String getString(){
        if(returnFormatString != null){
            return returnFormatString.getString(rawText.text);
        } else return rawText.text;
    }

    public String getMask() {
        return this.mask;
    }

    public void setCharRepresentation(char charRepresentation) {
        this.charRepresentation = charRepresentation;
        cleanUp();
    }

    public char getCharRepresentation() {
        return this.charRepresentation;
    }

    /**
     * Generates positions for values characters. For instance:
     * Input data: mask = "+7(###)###-##-##
     * After method execution:
     * rawToMask = [3, 4, 5, 6, 8, 9, 11, 12, 14, 15]
     * maskToRaw = [-1, -1, -1, 0, 1, 2, -1, 3, 4, 5, -1, 6, 7, -1, 8, 9]
     * charsInMask = "+7()- " (and space, yes)
     */
    private void generatePositionArrays() {
        int[] aux = new int[mask.length()];
        maskToRaw = new int[mask.length()];

        int charIndex = 0;
        for (int i = 0; i < mask.length(); i++) {
            char currentChar = mask.charAt(i);
            if (currentChar == charRepresentation) {
                aux[charIndex] = i;
                maskToRaw[i] = charIndex++;
            } else {
                maskToRaw[i] = -1;
            }
        }

        rawToMask = new int[charIndex];
        System.arraycopy(aux, 0, rawToMask, 0, charIndex);
    }

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {
        if (!editingBefore) {
            editingBefore = true;
            if (start > lastValidMaskPosition) {
                ignore = true;
            }
            int rangeStart = start;
            if (after == 0) {
                rangeStart = erasingStart(start);
            }
            Range range = calculateRange(rangeStart, start + count);
            if (range.start != -1) {
                rawText.subtractFromString(range);
            }
            if (count > 0) {
                selection = previousValidPosition(start);
            }
        }
    }

    private int erasingStart(int start) {
        while (start > 0 && maskToRaw[start] == -1) {
            start--;
        }
        return start;
    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {
        if (!editingOnChanged && editingBefore) {
            editingOnChanged = true;
            if (ignore) {
                return;
            }
            if (count > 0) {
                int startingPosition = maskToRaw[nextValidPosition(start)];
                String addedString = s.subSequence(start, start + count).toString();
                count = rawText.addToString(clear(addedString), startingPosition, maxRawLength);
                if (initialized) {
                    int currentPosition;
                    if (startingPosition + count < rawToMask.length)
                        currentPosition = rawToMask[startingPosition + count];
                    else currentPosition = lastValidMaskPosition + 1;
                    selection = nextValidPosition(currentPosition);
                }
            }
        }
    }

    @Override
    public void afterTextChanged(Editable s) {
        if (!editingAfter && editingBefore && editingOnChanged) {
            editingAfter = true;
            if (rawText.length() == 0 && hasHint()) {
                selection = 0;
                editText.setText(null);
            } else {
                editText.setText(makeColorMaskedText());
            }

            editText.setSelection(selection);

            editingBefore = false;
            editingOnChanged = false;
            editingAfter = false;
            ignore = false;
        }
    }

    private int fixSelection(int selection) {
        if (selection > lastValidPosition()) {
            return lastValidPosition();
        } else {
            return nextValidPosition(selection);
        }
    }

    private int nextValidPosition(int currentPosition) {
        while (currentPosition < lastValidMaskPosition && maskToRaw[currentPosition] == -1) {
            currentPosition++;
        }
        if (currentPosition > lastValidMaskPosition) return lastValidMaskPosition + 1;
        return currentPosition;
    }

    private int previousValidPosition(int currentPosition) {
        while (currentPosition >= 0 && maskToRaw[currentPosition] == -1) {
            currentPosition--;
            if (currentPosition < 0) {
                return nextValidPosition(0);
            }
        }
        return currentPosition;
    }

    private int lastValidPosition() {
        if (rawText.length() == maxRawLength) {
            return rawToMask[rawText.length() - 1] + 1;
        }
        return nextValidPosition(rawToMask[rawText.length()]);
    }

    private String makeMaskedText() {
        char[] maskedText = mask.replace(charRepresentation, emptySymbol).toCharArray();
        for (int i = 0; i < rawToMask.length; i++) {
            if (i < rawText.length()) {
                maskedText[rawToMask[i]] = rawText.charAt(i);
            } else {
                maskedText[rawToMask[i]] = emptySymbol;
            }
        }
        return new String(maskedText);
    }

    private SpannableString makeColorMaskedText(){
        SpannableString raw = new SpannableString(makeMaskedText());
        for(int count = 0; count < maskToRaw.length; count++){
            raw.setSpan(maskToRaw[count] == -1 ||
                    ( maskToRaw[count] >= 0 && maskToRaw[count] < rawText.text.length() && rawText.text.charAt(maskToRaw[count]) == emptySymbol) ?
                    new ForegroundColorSpan(maskStyleColor) : new ForegroundColorSpan(textStyleColor), count, count + 1, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        }
        return raw;
    }

    private Range calculateRange(int start, int end) {
        Range range = new Range();
        for (int i = start; i <= end && i < mask.length(); i++) {
            if (maskToRaw[i] != -1) {
                if (range.start == -1) {
                    range.start = maskToRaw[i];
                }
                range.end = maskToRaw[i];
            } else {
                if (i == end && range.start == range.end && start != end) {
                    end++;
                }
            }
        }
        if (end == mask.length()) {
            range.end = rawText.length();
        }
        return range;
    }

    private String clear(String string) {
        StringBuilder builder = new StringBuilder(string.length());

        for (char c : string.toCharArray()) {
            if (allowedChars.contains(String.valueOf(c))) {
                builder.append(c);
            }
        }
        return builder.toString();
    }

    private class RawText{
        private String text;

        public RawText() {
            char[] defaultText = new char[rawToMask.length];
            Arrays.fill(defaultText, emptySymbol);
            text = new String(defaultText);
        }

        /**
         * text = 012345678, range = 123 => text = 03456789
         * @param range
         */
        public void subtractFromString(Range range) {
            String firstPart = "";
            String lastPart = "";

            if (range.start > 0 && range.start <= text.length()) {
                firstPart = text.substring(0, range.start);
            }
            if (range.end >= 0 && range.end < text.length()) {
                lastPart = text.substring(range.end, text.length());
            }
            char[] chars = new char[range.end - range.start];
            Arrays.fill(chars, emptySymbol);
            text = firstPart.concat(new String(chars)).concat(lastPart);
        }

        /**
         * @param newString New String to be added
         * @param start     Position to insert newString
         * @param maxLength Maximum raw text length
         * @return Number of added characters
         */
        public int addToString(String newString, int start, int maxLength) {
            String firstPart = "";
            String lastPart = "";

            if (newString == null || newString.equals("")) {
                return 0;
            } else if (start < 0) {
                throw new IllegalArgumentException("Start position must be non-negative");
            } else if (start > text.length()) {
                throw new IllegalArgumentException("Start position must be less than the actual text length");
            }

            int count = newString.length();

            if (start > 0) {
                firstPart = text.substring(0, start);
            }
            boolean iCan = true;
            if (start >= 0 && start < text.length()) {
                lastPart = text.substring(start, text.length());
                if (lastPart.indexOf("_") == 0) {
                    if (lastPart.length() == 1) {
                        lastPart = "";
                    } else {
                        lastPart = lastPart.substring(1);
                    }
                    iCan = false;
                }
            }
            if (text.length() + newString.length() > maxLength && iCan) {
                count = maxLength - text.length();
                newString = newString.substring(0, count);
            }
            text = firstPart.concat(newString).concat(lastPart);
            return count;
        }

        public int length() {
            return text.length();
        }

        public char charAt(int position) {
            return text.charAt(position);
        }
    }

    public class Range {
        private int start;
        private int end;

        Range() {
            start = -1;
            end = -1;
        }
    }

    public static interface ReturnFormatString{
        String getString(String defaultString);
    }
}

