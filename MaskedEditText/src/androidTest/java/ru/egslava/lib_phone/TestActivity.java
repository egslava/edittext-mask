package ru.egslava.lib_phone;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.CheckBox;
import android.widget.CompoundButton;

import br.com.sapereaude.maskedEditText.MaskedEditText;

import static br.com.sapereaude.maskedEditText.test.R.*;

/**
 * Created by egslava on 04/03/2017.
 */

public class TestActivity extends Activity implements CompoundButton.OnCheckedChangeListener {

    private MaskedEditText maskedEditText;
    private CheckBox vcbKeepHint;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(layout.activity_main);
        vcbKeepHint = findViewById(id.keep_hint);
        maskedEditText = findViewById(id.vControl);
        vcbKeepHint.setOnCheckedChangeListener(this);
    }

    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        maskedEditText.setKeepHint(vcbKeepHint.isChecked());
    }
}
