# MaskedEditText

*************************************************************************************************
## en_US

MakedEditText is a simple Android EditText with customizable input mask support.

For instance, you need user specified his phone in format +7(XXX)XXX-XX-XX. You also know user should have the only possibility to write digits but minuses, brackets and "+7" should appear automatically.

### Usage

Add _xmlns:mask="http://schemas.android.com/apk/res/com.your.app.package"_ to your layout xml root.
Also you need to download project and plug it in as a library (project doesn't have a maven repository) so you can start using the library:

      <br.com.sapereaude.maskedEditText.MaskedEditText
        android:id="@+id/phone_input"
        android:layout_width="match_parent"
        android:layout_height="wrap_content"
        android:inputType="phone"
        android:typeface="monospace"
        mask:allowed_chars="1234567890"
        mask:mask="+7(###)###-##-##"
        />    
Where _mask_ is the input mask you want and '#' is an editable position (will be replaced by a whitespace on screen).
    
You can optionally set the representation character (in case you don't want to use '#'):

    <br.com.sapereaude.maskedEditText.MaskedEditText 
        android:layout_width="fill_parent"
        android:layout_height="wrap_content"
        mask:mask="ccc.ccc.ccc-cc"
        mask:char_representation="c"
    />

You can also change the mask and the representation character programatically:

	MaskedEditText editText = (MaskedEditText) findViewById(R.id.my_edit_text)
	// Setting the representation character to '$'
	editText.setCharRepresentation('$');
	// Logging the representation character
	Log.i("Representation character", editText.getCharRepresentation());
	// Setting the mask
	editText.setMask("##/##/####");
	// Logging the mask
	Log.i("Mask", editText.getMask());
	
*************************************************************************************************
## ru_RU

MarkedEditText - это всего лишь EditText, но с возможностью задавать произвольную маску. 

Например, нужно ввести телефон в формате +7(XXX)XXX-XX-XX. Причём можно ввести только цифры, а скобочки, дефисы и "+7" должны подставляться самостоятельно.

### Использование

Добавить _xmlns:mask="http://schemas.android.com/apk/res-auto"_ в корневой элемент файла разметки. Кроме того, нужно скачать проект и добавить его в зависимости к основному проекту, после чего элемент можно начать использовать:

      <br.com.sapereaude.maskedEditText.MaskedEditText
        android:id="@+id/phone_input"
        android:layout_width="match_parent"
        android:layout_height="wrap_content"
        android:inputType="phone"
        android:typeface="monospace"
        mask:allowed_chars="1234567890"
        mask:mask="+7(###)###-##-##"
        />
    
_mask_ задаёт требуемую маску, символ '#' задаёт редактируемую позицию (и будет заменён на пробел на экране).
    
Если использовать '#' нельзя, то можно попробовать использовать другой символ:

    <br.com.sapereaude.maskedEditText.MaskedEditText 
        android:layout_width="fill_parent"
        android:layout_height="wrap_content"
        mask:mask="ccc.ccc.ccc-cc"
        mask:char_representation="c"
    />

Кроме того, всё тоже самое можно сделать и программно:

	MaskedEditText editText = (MaskedEditText) findViewById(R.id.my_edit_text)
	// Setting the representation character to '$'
	editText.setCharRepresentation('$');
	// Logging the representation character
	Log.i("Representation character", editText.getCharRepresentation());
	// Setting the mask
	editText.setMask("##/##/####");
	// Logging the mask
	Log.i("Mask", editText.getMask());
