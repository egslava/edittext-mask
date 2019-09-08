package ru.egslava.edittextphonenumber;

import android.app.Activity;
import android.os.Bundle;
import br.com.sapereaude.maskedEditText.MaskedEditText;

public class MainActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        MaskedEditText phone = (MaskedEditText)findViewById(R.id.phone_input);
    }
}
