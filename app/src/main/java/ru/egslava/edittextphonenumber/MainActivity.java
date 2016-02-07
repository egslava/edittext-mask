package ru.egslava.edittextphonenumber;

import android.app.Activity;
import android.os.Bundle;
import android.widget.EditText;

import br.com.sapereaude.maskedEditText.MaskTextWatcher;

public class MainActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        EditText editText = (EditText) findViewById(R.id.edit_text);
        MaskTextWatcher maskTextWatcher = new MaskTextWatcher(editText);
        editText.addTextChangedListener(maskTextWatcher);
    }
}
