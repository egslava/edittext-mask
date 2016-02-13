package br.com.sapereaude.maskedEditText;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.graphics.Typeface;
import android.view.ActionMode;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.Toast;

/**
 * Created by samohvalov on 05.02.2016.
 */
public class PhoneTextWatcher extends MaskTextWatcher {

    public PhoneTextWatcher(final EditText editText, final Context context) {
        super(editText, '_', "+#(###)###-##-##", "1234567890");
        editText.setTypeface(Typeface.MONOSPACE);//специально для > 5.0
        setOnReturnStringFormat(new MaskTextWatcher.ReturnFormatString() {
            @Override
            public String getString(String defaultString) {
                return "+".concat(defaultString.replaceAll(String.valueOf(getEmptySymbol()), ""));
            }
        });
        editText.setCustomSelectionActionModeCallback(new ActionMode.Callback() {
            @Override
            public boolean onCreateActionMode(ActionMode mode, Menu menu) {
                return true;
            }

            @Override
            public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
                return true;
            }

            @Override
            public boolean onActionItemClicked(ActionMode mode, MenuItem item) {
                ClipboardManager clipboardManager = (ClipboardManager) context.getSystemService(Context.CLIPBOARD_SERVICE);
                switch (item.getItemId()) {
                    case android.R.id.copy:
                        String copy = getSelectedString();
                        ClipData clip = ClipData.newPlainText(copy, copy);
                        clipboardManager.setPrimaryClip(clip);
                        Toast.makeText(context, "Copy to clipboard.", Toast.LENGTH_SHORT).show();
                        break;
                    case android.R.id.paste:
                        if (clipboardManager.getPrimaryClip().getItemCount() > 0) {
                            editText.setText(clipboardManager.getPrimaryClip().getItemAt(0).getText());
                        }
                        break;
                    case android.R.id.cut:
                        ClipData clip1 = ClipData.newPlainText(getString(), getString());
                        clipboardManager.setPrimaryClip(clip1);
                        editText.setText("");
                        break;
                    case android.R.id.selectAll:
                        editText.selectAll();
                        break;
                }
                mode.finish();
                return true;
            }

            @Override
            public void onDestroyActionMode(ActionMode mode) {

            }
        });

        cleanUp();
    }
}
