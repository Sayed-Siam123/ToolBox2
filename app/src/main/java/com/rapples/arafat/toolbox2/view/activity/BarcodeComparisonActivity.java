package com.rapples.arafat.toolbox2.view.activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.TextView;

import com.rapples.arafat.toolbox2.R;
import com.rapples.arafat.toolbox2.databinding.ActivityBarcodeComparisonBinding;
import com.rapples.arafat.toolbox2.util.SharedPref;

public class BarcodeComparisonActivity extends AppCompatActivity {
    private static final String ACTION_BARCODE_DATA = "com.honeywell.sample.action.BARCODE_DATA";
    private static final String ACTION_CLAIM_SCANNER = "com.honeywell.aidc.action.ACTION_CLAIM_SCANNER";
    private static final String ACTION_RELEASE_SCANNER = "com.honeywell.aidc.action.ACTION_RELEASE_SCANNER";
    private static final String EXTRA_SCANNER = "com.honeywell.aidc.extra.EXTRA_SCANNER";
    private static final String EXTRA_PROFILE = "com.honeywell.aidc.extra.EXTRA_PROFILE";
    private static final String EXTRA_PROPERTIES = "com.honeywell.aidc.extra.EXTRA_PROPERTIES";
    private ActivityBarcodeComparisonBinding binding;
    private String barcode;
    private boolean flag = false;
    private boolean masterScannerOpen = true;
    private boolean referScannerOpen = true;


    private BroadcastReceiver barcodeDataReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (ACTION_BARCODE_DATA.equals(intent.getAction())) {
                int version = intent.getIntExtra("version", 0);
                if (version >= 1) {
                    String aimId = intent.getStringExtra("aimId");
                    String charset = intent.getStringExtra("charset");
                    String codeId = intent.getStringExtra("codeId");
                    String data = intent.getStringExtra("data");
                    byte[] dataBytes = intent.getByteArrayExtra("dataBytes");
                    String timestamp = intent.getStringExtra("timestamp");

                    if (!flag) {
                        binding.masterbarCodeFromSCET.setText(data);
                        binding.masterScanDigitCount.setText(data.length() + " Digits");
                        binding.masterScanCodeType.setText(defineCodeName(codeId));


                        flag = true;
                    } else {
                        binding.barCodeFromSCET.setText(data);
                        binding.scanDigitCount.setText(data.length() + " Digits");
                        binding.scanCodeType.setText(defineCodeName(codeId));
                        flag = false;
                    }


                }
            }
        }
    };

    private String defineCodeName(String codeId) {
        String codeName;
        switch (codeId) {
            case ".":
                codeName = "DOTCODE";
                break;
            case "1":
                codeName = "CODE1";
                break;
            case ";":
                codeName = "MERGED_COUPON";
                break;
            case "<":
                codeName = "CODE32";
                break;
            case ">":
                codeName = "LABELCODE_IV";
                break;
            case "=":
                codeName = "TRIOPTIC";
                break;
            case "'?'":
                codeName = "KOREA_POST";
                break;
            case ",":
                codeName = "INFOMAIL";
                break;
            case "[":
                codeName = "SWEEDISH_POST";
                break;
            case "|":
                codeName = "RM_MAILMARK";
                break;
            case "'":
                codeName = "EAN13_ISBN";
                break;
            case "]":
                codeName = "BRAZIL_POS";
                break;
            case "A":
                codeName = "AUS_POST";
                break;
            case "B":
                codeName = "BRITISH_POST";
                break;
            case "C":
                codeName = "CANADIAN_POST";
                break;
            case "D":
                codeName = "EAN8";
                break;
            case "E":
                codeName = "UPCE";
                break;
            case "G'":
                codeName = "BC412";
                break;
            case "H":
                codeName = "HAN_XIN_CODE";
                break;
            case "I":
                codeName = "GS1_128";
                break;
            case "J":
                codeName = "JAPAN_POST";
                break;
            case "K":
                codeName = "KIX_CODE";
                break;
            case "L":
                codeName = "PLANET_CODE";
                break;
            case "M":
                codeName = "INTELLIGENT_MAIL";
                break;
            case "N":
                codeName = "ID_TAGS";
                break;
            case "O":
                codeName = "OCR";
                break;
            case "P":
                codeName = "POSTNET";
                break;
            case "Q":
                codeName = "HK25";
                break;
            case "R":
                codeName = "MICROPDF";
                break;
            case "S":
                codeName = "SECURE_CODE";
                break;
            case "T":
                codeName = "TLC39";
                break;
            case "U":
                codeName = "ULTRACODE";
                break;
            case "V":
                codeName = "CODABLOCK_A";
                break;
            case "W":
                codeName = "POSICODE";
                break;
            case "X":
                codeName = "GRID_MATRIX";
                break;
            case "Y":
                codeName = "NEC25";
                break;
            case "Z":
                codeName = "MESA";
                break;
            case "a":
                codeName = "CODABAR";
                break;
            case "b":
                codeName = "CODE39";
                break;
            case "c":
                codeName = "UPCA";
                break;
            case "d":
                codeName = "EAN13";
                break;
            case "e":
                codeName = "I25";
                break;
            case "f":
                codeName = "S25 (2BAR and 3BAR)";
                break;
            case "g":
                codeName = "MSI";
                break;
            case "h":
                codeName = "CODE11";
                break;
            case "i":
                codeName = "CODE93";
                break;
            case "j":
                codeName = "CODE128";
                break;
            case "k":
                codeName = "UNUSED";
                break;
            case "l":
                codeName = "CODE49";
                break;
            case "m":
                codeName = "M25";
                break;
            case "n":
                codeName = "PLESSEY";
                break;
            case "o":
                codeName = "CODE16K";
                break;
            case "p":
                codeName = "CHANNELCODE";
                break;
            case "q":
                codeName = "CODABLOCK_F";
                break;
            case "r":
                codeName = "PDF417";
                break;
            case "s":
                codeName = "QRCODE";
                break;
            case "-":
                codeName = "MICROQR_ALT";
                break;
            case "t":
                codeName = "TELEPEN";
                break;
            case "u":
                codeName = "CODEZ";
                break;
            case "v":
                codeName = "VERICODE";
                break;
            case "w":
                codeName = "DATAMATRIX";
                break;
            case "x":
                codeName = "MAXICODE";
                break;
            case "y":
                codeName = "RSS";
                break;
            case "{":
                codeName = "GS1_DATABAR";
                break;
            case "}":
                codeName = "GS1_DATABAR_EXP";
                break;
            case "z":
                codeName = "AZTEC_CODE";
                break;
            default:
                codeName = "";
        }
        return codeName;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_barcode_comparison);


        setDefaultFocus();

        hideKey();

        inputMasterTypeBarcode();

        inputReferenceBarcode();

        inputMasterTypeBarcodeForScanner();

        inputReferenceBarcodeForScanner();


    }

    private void hideKey() {
        getWindow().setSoftInputMode(
                WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN
        );
    }

    private void setDefaultFocus() {
        binding.masterbarCodeFromSCET.requestFocus();
        disableFocus();
        hideKeyboard(this);
    }

    private void inputReferenceBarcode() {
        binding.barCodeET.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                boolean handled = false;
                if (actionId == EditorInfo.IME_ACTION_DONE) {
                    binding.masterBarCodeET.setSelectAllOnFocus(true);
                    binding.masterBarCodeET.requestFocus();
                    InputMethodManager imm = (InputMethodManager) getSystemService(Activity.INPUT_METHOD_SERVICE);
                    imm.showSoftInput(binding.edittextLL, InputMethodManager.SHOW_IMPLICIT);

                    binding.masterBarCodeET.setSelectAllOnFocus(true);
                    binding.scanDigitCount.setText(binding.barCodeET.getText().toString().length() + " Digits");
                    compareBarcode();

                    handled = true;
                }
                return handled;
            }
        });


    }

    private void inputMasterTypeBarcode() {

        binding.masterBarCodeET.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                boolean handled = false;
                if (actionId == EditorInfo.IME_ACTION_DONE) {
                    binding.barcodeLL.setVisibility(View.VISIBLE);
                    binding.barCodeET.setText("");
                    binding.barCodeET.requestFocus();
                    InputMethodManager imm = (InputMethodManager) getSystemService(Activity.INPUT_METHOD_SERVICE);
                    imm.showSoftInput(binding.edittextLL, InputMethodManager.SHOW_IMPLICIT);
                    binding.resultRL.setVisibility(View.GONE);
                    binding.masterScanDigitCount.setText(binding.masterBarCodeET.getText().toString().length() + " Digits");


                    handled = true;
                }
                return handled;
            }
        });

    }

    private void inputReferenceBarcodeForScanner() {
        binding.barCodeFromSCET.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                binding.masterbarCodeFromSCET.setSelectAllOnFocus(true);
                binding.masterbarCodeFromSCET.requestFocus();
                binding.scanDigitCount.setText(binding.masterbarCodeFromSCET.getText().toString().length() + " Digits");
                compareBarcodeForScanner();

            }
        });


    }

    private void inputMasterTypeBarcodeForScanner() {

        binding.masterbarCodeFromSCET.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                binding.barcodeLL.setVisibility(View.VISIBLE);
                binding.barCodeFromSCET.setText("");
                binding.barCodeFromSCET.requestFocus();
                binding.resultRL.setVisibility(View.GONE);
                binding.masterScanDigitCount.setText(binding.masterbarCodeFromSCET.getText().toString().length() + " Digits");

            }
        });

    }

    @Override
    protected void onResume() {
        super.onResume();
        registerReceiver(barcodeDataReceiver, new IntentFilter(ACTION_BARCODE_DATA));
        claimScanner();
    }

    @Override
    protected void onPause() {
        super.onPause();

        if (masterScannerOpen || referScannerOpen) {
            unregisterReceiver(barcodeDataReceiver);
            releaseScanner();
        }
    }

    private void claimScanner() {
        Bundle properties = new Bundle();
        properties.putBoolean("DPR_DATA_INTENT", true);
        properties.putString("DPR_DATA_INTENT_ACTION", ACTION_BARCODE_DATA);
        sendBroadcast(new Intent(ACTION_CLAIM_SCANNER).setPackage("com.intermec.datacollectionservice")
                .putExtra(EXTRA_SCANNER, "dcs.scanner.imager").putExtra(EXTRA_PROFILE, "MyProfile1").putExtra(EXTRA_PROPERTIES, properties)
        );
    }

    private void releaseScanner() {
        sendBroadcast(new Intent(ACTION_RELEASE_SCANNER)
        );
    }


    private void focusEditText() {
        binding.edittextLL.requestFocus();
        InputMethodManager imm = (InputMethodManager) getSystemService(Activity.INPUT_METHOD_SERVICE);
        imm.showSoftInput(binding.edittextLL, InputMethodManager.SHOW_IMPLICIT);
    }


    private void compareBarcode() {
        String code = binding.masterBarCodeET.getText().toString();
        binding.resultRL.setVisibility(View.VISIBLE);
        if (binding.barCodeET.getText().toString().equals(code)) {
            binding.resultRL.setBackgroundColor(getResources().getColor(R.color.green));
            binding.statusTV.setText("OK");
            binding.resultRL.setVisibility(View.VISIBLE);
        } else {
            binding.resultRL.setBackgroundColor(getResources().getColor(R.color.red));
            binding.statusTV.setText("Fail");
            binding.resultRL.setVisibility(View.VISIBLE);
        }

    }

    private void compareBarcodeForScanner() {
        String code = binding.masterbarCodeFromSCET.getText().toString();
        binding.resultRL.setVisibility(View.VISIBLE);
        if (binding.barCodeFromSCET.getText().toString().equals(code)) {
            binding.resultRL.setBackgroundColor(getResources().getColor(R.color.green));
            binding.statusTV.setText("OK");
            binding.resultRL.setVisibility(View.VISIBLE);
        } else {
            binding.resultRL.setBackgroundColor(getResources().getColor(R.color.red));
            binding.statusTV.setText("Fail");
            binding.resultRL.setVisibility(View.VISIBLE);
        }

    }


    public void openKeyboard(View view) {

        binding.edittextLL.setVisibility(View.VISIBLE);
        binding.sannnerLL.setVisibility(View.GONE);
        binding.barCodeET.requestFocus();
        referScannerOpen = false;
        InputMethodManager imm = (InputMethodManager) view.getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.toggleSoftInput(InputMethodManager.SHOW_FORCED, InputMethodManager.HIDE_IMPLICIT_ONLY);
        checkBrodcastReceiver();
    }

    public void openScaaner(View view) {

        binding.edittextLL.setVisibility(View.GONE);
        binding.sannnerLL.setVisibility(View.VISIBLE);
        binding.masterbarCodeFromSCET.requestFocus();
        referScannerOpen = true;
        disableFocus();
        hideKeyboard(this);
        checkBrodcastReceiver();
    }

    private void disableFocus() {
        InputMethodManager imm = (InputMethodManager) getSystemService(Activity.INPUT_METHOD_SERVICE);
        imm.toggleSoftInput(InputMethodManager.HIDE_IMPLICIT_ONLY, 0);
    }

    public void onSettings(View view) {
        startActivity(new Intent(BarcodeComparisonActivity.this, ApplicationSettingsActivity.class));
    }

    public static void hideKeyboard(Activity activity) {
        InputMethodManager imm = (InputMethodManager) activity.getSystemService(Activity.INPUT_METHOD_SERVICE);
        View view = activity.getCurrentFocus();
        if (view == null) {
            view = new View(activity);
        }
        imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }

    public void openScaanerMaster(View view) {
        binding.masterEdittextLL.setVisibility(View.GONE);
        binding.masterSannnerLL.setVisibility(View.VISIBLE);
        binding.masterbarCodeFromSCET.requestFocus();
        disableFocus();
        masterScannerOpen = true;
        hideKeyboard(this);
        checkBrodcastReceiver();
    }

    public void openKeyboardMaster(View view) {
        binding.masterEdittextLL.setVisibility(View.VISIBLE);
        binding.masterSannnerLL.setVisibility(View.GONE);
        binding.masterBarCodeET.requestFocus();
        masterScannerOpen = false;
        InputMethodManager imm = (InputMethodManager) view.getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.toggleSoftInput(InputMethodManager.SHOW_FORCED, InputMethodManager.HIDE_IMPLICIT_ONLY);
        checkBrodcastReceiver();
    }

    public void backComparison(View view) {
        onBackPressed();
    }

    public void registeredScanner() {
        registerReceiver(barcodeDataReceiver, new IntentFilter(ACTION_BARCODE_DATA));
        claimScanner();
    }

    public void unRegisteredScanner() {
        unregisterReceiver(barcodeDataReceiver);
        releaseScanner();

    }

    public void checkBrodcastReceiver() {
        if (masterScannerOpen || referScannerOpen) {
            registeredScanner();
        } else {
            unRegisteredScanner();
        }
    }
}