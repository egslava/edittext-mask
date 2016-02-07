package ru.egslava.edittextphonenumber;

import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;

import br.com.sapereaude.maskedEditText.MaskTextWatcher;
import br.com.sapereaude.maskedEditText.MaskedEditText;

public class MainActivity extends ActionBarActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        MaskedEditText phone = (MaskedEditText)findViewById(R.id.phone_input);
        EditText editText = (EditText)findViewById(R.id.edit_text);
        editText.addTextChangedListener(new MaskTextWatcher(editText, '_', null, null));
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
