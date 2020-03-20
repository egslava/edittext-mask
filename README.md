# MaskedEditText
[![Download](https://api.bintray.com/packages/egorenkov/maven/edittext-mask/images/download.svg) ](https://bintray.com/egorenkov/maven/edittext-mask/_latestVersion) [![Build Status](https://travis-ci.org/egslava/edittext-mask.svg?branch=master)](https://travis-ci.org/egslava/edittext-mask)

![MaskedEditText - the library for masked input of phone numbers, social security numbers and so on for Android](publish/README.gif)

This project derives from [toshikurauchi/MaskedEditText](https://github.com/toshikurauchi/MaskedEditText), but it's been adapted for gradle build system and has additional features:

1. filter allowed chars
2. filter denied chars
3. user can use chars from mask in his input (in original version of this library user couldn't use digit '7' in the '+7(XXX)XXX-XX-XX' pattern).
4. You can keep hints even when user started typing.

So it allows you to use masks for phones, urls, etc.

Enjoy!

<a href='https://play.google.com/store/apps/details?id=ru.egslava.edittextmaskdemo&utm_source=github&pcampaignid=MKT-Other-global-all-co-prtnr-py-PartBadge-Mar2515-1'><img width=200 alt='Get it on Google Play' src='https://play.google.com/intl/en_us/badges/images/generic/en_badge_web_generic.png'/></a>

*********************************
## en_US
MaskedEditText is a simple Android EditText with customizable input mask support.

For instance, you need user specified his phone in format +7(XXX)XXX-XX-XX. You also know user should have the only possibility to write digits but minuses, brackets and "+7" should appear automatically.

### Usage

Add this to your `build.gradle` :
```groovy
compile 'ru.egslava:MaskedEditText:1.0.5'
```
Or download project and plug it in as a library.


Add _xmlns:mask="http://schemas.android.com/apk/res-auto"_ to your layout xml root:

      <br.com.sapereaude.maskedEditText.MaskedEditText
        android:id="@+id/phone_input"
        android:layout_width="match_parent"
        android:layout_height="wrap_content"
        android:inputType="phone"
        android:typeface="monospace"
        app:allowed_chars="1234567890"
        app:mask="+7(###)###-##-##"
        android:hint="1234567890"
        app:keep_hint="true"
        />    
Where _mask_ is the input mask you want and '#' is an editable position (will be replaced by a whitespace on screen).

You can optionally set the representation character (in case you don't want to use '#'):

    <br.com.sapereaude.maskedEditText.MaskedEditText
        android:layout_width="fill_parent"
        android:layout_height="wrap_content"
        app:mask="ccc.ccc.ccc-cc"
        app:char_representation="c"
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
	
To enable Enter softkey action (IME action):

	<br.com.sapereaude.maskedEditText.MaskedEditText
	    ...
	    app:enable_ime_action="true"
	    ...
	/>
    
Or programmatically:

	MaskedEditText editText = (MaskedEditText) findViewById(R.id.my_edit_text)
	editText.setImeActionEnabled(true);
	
*************************************************************************************************
## ru_RU

MaskedEditText - это всего лишь EditText, но с возможностью задавать произвольную маску.

Например, нужно ввести телефон в формате +7(XXX)XXX-XX-XX. Причём можно ввести только цифры, а скобочки, дефисы и "+7" должны подставляться самостоятельно.

### Использование

Вписать в `build.gradle`:
```groovy
compile 'ru.egslava:MaskedEditText:1.0.5'
```
или скачать проект и подключить как библиотеку.

Добавить _xmlns:mask="http://schemas.android.com/apk/res-auto"_ в корневой элемент файла разметки:

      <br.com.sapereaude.maskedEditText.MaskedEditText
        android:id="@+id/phone_input"
        android:layout_width="match_parent"
        android:layout_height="wrap_content"
        android:inputType="phone"
        android:typeface="monospace"
        app:allowed_chars="1234567890"
        app:mask="+7(###)###-##-##"
        android:hint="1234567890"
        app:keep_hint="true"
        />

_mask_ задаёт требуемую маску, символ '#' задаёт редактируемую позицию (и будет заменён на пробел на экране).

Если использовать '#' нельзя, то можно попробовать использовать другой символ:

    <br.com.sapereaude.maskedEditText.MaskedEditText
        android:layout_width="fill_parent"
        android:layout_height="wrap_content"
        app:mask="ccc.ccc.ccc-cc"
        app:char_representation="c"
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
	
Чтобы включить обработку нажатия Enter (IME action):

	<br.com.sapereaude.maskedEditText.MaskedEditText
	    ...
	    mask:enable_ime_action="true"
	    ...
	/>
    
Или программно:

	MaskedEditText editText = (MaskedEditText) findViewById(R.id.my_edit_text)
	editText.setImeActionEnabled(true);
