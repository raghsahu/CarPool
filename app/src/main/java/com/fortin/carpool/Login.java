package com.fortin.carpool;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.text.InputFilter;
import android.text.Spanned;
import android.text.TextUtils;
import android.util.Log;
import android.util.SparseArray;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.fortin.carpool.Model.M;
import com.fortin.carpool.Model.ResponseModel;
import com.fortin.carpool.WebServices.APIService;
import com.fortin.carpool.WebServices.UserAPI;
import com.fortin.carpool.countrylookup.Country;
import com.fortin.carpool.countrylookup.CountryAdapter;
import com.fortin.carpool.countrylookup.CustomPhoneNumberFormattingTextWatcher;
import com.fortin.carpool.countrylookup.OnPhoneChangedListener;
import com.fortin.carpool.countrylookup.PhoneUtils;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseException;
import com.google.firebase.FirebaseTooManyRequestsException;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthProvider;
import com.google.i18n.phonenumbers.NumberParseException;
import com.google.i18n.phonenumbers.PhoneNumberUtil;
import com.google.i18n.phonenumbers.Phonenumber;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.TreeSet;
import java.util.concurrent.TimeUnit;

import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

public class Login extends AppCompatActivity implements View.OnClickListener {
    LinearLayout llalreadyotp;
    Button btnlogin,btnskip;
    String mobileNumber, country="";
    EditText mPhoneEdit, etmobile;
    protected SparseArray<ArrayList<Country>> mCountriesMap = new SparseArray<ArrayList<Country>>();
    protected PhoneNumberUtil mPhoneNumberUtil = PhoneNumberUtil.getInstance();
    protected Spinner mSpinner;
    protected String mLastEnteredPhone;
    protected CountryAdapter mAdapter;
    Context context;
    String TAG="Login";
    public static Boolean isverify=false;
    String mVerificationId;
    //public static PhoneAuthCredential credential;
    PhoneAuthProvider.ForceResendingToken mResendToken;
    String ph;

    @RequiresApi(api = Build.VERSION_CODES.CUPCAKE)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        context=Login.this;
        initCodes(this);
        mPhoneEdit = (EditText)findViewById(R.id.etphone);
        etmobile = (EditText)findViewById(R.id.etmobile);
        getSupportActionBar().setTitle(R.string.verify);
        btnlogin=(Button)findViewById(R.id.btnlogin);
        btnskip=(Button)findViewById(R.id.btnskip);
        llalreadyotp =(LinearLayout)findViewById(R.id.llalreadyotp);
        llalreadyotp.setOnClickListener(this);
        btnlogin.setOnClickListener(this);
        btnskip.setOnClickListener(this);
        initUI();
        isStoragePermissionGranted();
    }
    //********************************************
//    String phone=mPhoneEdit.getText().toString();
    //***************************************************

    public  boolean isStoragePermissionGranted() {
        if (Build.VERSION.SDK_INT >= 23) {
            if (checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE)
                    == PackageManager.PERMISSION_GRANTED) {
                Log.v("here","Permission is granted");
                return true;
            } else {
                ActivityCompat.requestPermissions(this,new String[]{android.Manifest.permission.WRITE_EXTERNAL_STORAGE},2);

                Log.v("Here","Permission is revoked");
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, 1);
                return false;
            }
        }
        else {
            Log.v("Here","Permission is granted");
            return true;
        }


    }
    @RequiresApi(api = Build.VERSION_CODES.ECLAIR)
    @Override
    public void onClick(View v) {
        if(v.getId()==R.id.btnskip){
            Intent it=new Intent(Login.this,MainActivity.class);
            finish();
            startActivity(it);
        }
        if(v.getId()==R.id.btnlogin){
           send();
        }else if(v.getId()==R.id.llalreadyotp){
            String phone = etmobile.getText().toString().trim();

            mLastEnteredPhone = mPhoneEdit.getText().toString().trim();
            if(phone.length()< 5)
            {
                etmobile.requestFocus();
                etmobile.setError(getString(R.string.label_error_incorrect_phone));
                return;
            }

            StringBuilder sb = new StringBuilder(16);
            sb.append(mLastEnteredPhone).append(phone);
            mobileNumber = sb.toString().trim();



            hideKeyboard(etmobile);
            etmobile.setError(null);

            if (phone == null) {
                etmobile.requestFocus();
                etmobile.setError(getString(R.string.label_error_incorrect_phone));
                return;
            }

            Intent intent = new Intent(Login.this, VarifyOTP.class);
            Log.d("LOGIN"," already otp country:"+country);
            intent.putExtra("phone", mobileNumber);
            intent.putExtra("countrycode", mLastEnteredPhone);
            finish();
            startActivity(intent);
            overridePendingTransition(0, 0);
        }
    }

    @Override
    public void onBackPressed() {
        finish();
    }

    @RequiresApi(api = Build.VERSION_CODES.CUPCAKE)
    public void send()
    {
        String phone = etmobile.getText().toString().trim();
        mLastEnteredPhone = mPhoneEdit.getText().toString().trim();
        if(phone.length()< 5)
        {
            etmobile.requestFocus();
            etmobile.setError(getString(R.string.label_error_incorrect_phone));
            return;
        }

        StringBuilder sb = new StringBuilder(16);
        sb.append(mLastEnteredPhone).append(phone);
        mobileNumber = sb.toString().trim();



        hideKeyboard(etmobile);
        etmobile.setError(null);

        if (phone == null) {
            etmobile.requestFocus();
            etmobile.setError(getString(R.string.label_error_incorrect_phone));
            return;
        }
        PhoneAuthProvider.getInstance().verifyPhoneNumber(
                mobileNumber,        // Phone number to verify
                60,                 // Timeout duration
                TimeUnit.SECONDS,   // Unit of timeout
                Login.this,               // Activity (for callback binding)
                mCallbacks);
       // generateOTP();
    }

    public void generateOTP() {
        M.showLoadingDialog(Login.this);
        System.out.println("phone---"+mobileNumber+"");
        UserAPI mUsersAPI = APIService.createService(UserAPI.class, M.getToken(Login.this));
        mUsersAPI.generateOTP(mobileNumber, new Callback<ResponseModel>() {
            @Override
            public void success(ResponseModel success, Response response) {
                M.L(response.getBody() + "");

                if(success.getSuccess().equals("1")) {
                    PhoneAuthProvider.getInstance().verifyPhoneNumber(
                            mobileNumber,        // Phone number to verify
                            60,                 // Timeout duration
                            TimeUnit.SECONDS,   // Unit of timeout
                            Login.this,               // Activity (for callback binding)
                            mCallbacks);
                }
                else
                {
                    M.hideLoadingDialog();
                    M.showToast(Login.this, "Server Error");
                }
            }

            @Override
            public void failure(RetrofitError error) {
                M.hideLoadingDialog();
                M.L(getString(R.string.ServerError));
                M.L("" + error.getUrl());
            }
        });
    }

    PhoneAuthProvider.OnVerificationStateChangedCallbacks mCallbacks = new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {

        @RequiresApi(api = Build.VERSION_CODES.ECLAIR)
        @Override
        public void onVerificationCompleted(PhoneAuthCredential credential1) {
            // This callback will be invoked in two situations:
            // 1 - Instant verification. In some cases the phone number can be instantly
            //     verified without needing to send or enter a verification code.
            // 2 - Auto-retrieval. On some devices Google Play services can automatically
            //     detect the incoming verification SMS and perform verification without
            //     user action.
            Log.d(TAG, "onVerificationCompleted:" + credential1);
            isverify=true;
            M.hideLoadingDialog();
            Intent intent = new Intent(Login.this, VarifyOTP.class);
            intent.putExtra(getString(R.string.phone), mobileNumber);
            intent.putExtra(getString(R.string.country), country);
            intent.putExtra("verificationid",mVerificationId);
            finish();
            startActivity(intent);
            overridePendingTransition(0, 0);
        }

        @Override
        public void onVerificationFailed(FirebaseException e) {
            // This callback is invoked in an invalid request for verification is made,
            // for instance if the the phone number format is not valid.
            Log.w(TAG, "onVerificationFailed", e);
            isverify=false;
            if (e instanceof FirebaseAuthInvalidCredentialsException) {
                Toast.makeText(context,"invalid Phone Number",Toast.LENGTH_SHORT).show();
                // Invalid request
                // ...
            } else if (e instanceof FirebaseTooManyRequestsException) {
                // The SMS quota for the project has been exceeded
                // ...
            }

            // Show a message and update the UI
            // ...
        }

        @RequiresApi(api = Build.VERSION_CODES.ECLAIR)
        @Override
        public void onCodeSent(String verificationId,
                PhoneAuthProvider.ForceResendingToken token) {
            // The SMS verification code has been sent to the provided phone number, we
            // now need to ask the user to enter the code and then construct a credential
            // by combining the code with a verification ID.
            Log.d(TAG, "onCodeSent:" + verificationId);

            // Save verification ID and resending token so we can use them later
            mVerificationId = verificationId;
            mResendToken = token;
            M.hideLoadingDialog();
            Intent intent = new Intent(Login.this, VarifyOTP.class);
            intent.putExtra(getString(R.string.phone), mobileNumber);
            intent.putExtra(getString(R.string.country), country);
            intent.putExtra("verificationid",mVerificationId);
            finish();
            startActivity(intent);
            overridePendingTransition(0, 0);
        }
    };

    @RequiresApi(api = Build.VERSION_CODES.CUPCAKE)
    protected void initUI() {
        mSpinner = (Spinner) findViewById(R.id.spinner);
        mSpinner.setOnItemSelectedListener(mOnItemSelectedListener);

        mPhoneEdit.addTextChangedListener(new CustomPhoneNumberFormattingTextWatcher(mOnPhoneChangedListener));
        InputFilter filter = new InputFilter() {
            public CharSequence filter(CharSequence source, int start, int end,
                                       Spanned dest, int dstart, int dend) {
                for (int i = start; i < end; i++) {
                    char c = source.charAt(i);
                    if (dstart > 0 && !Character.isDigit(c)) {
                        return "";
                    }
                }
                return null;
            }
        };

        mPhoneEdit.setFilters(new InputFilter[]{filter});


        mPhoneEdit.setImeOptions(EditorInfo.IME_ACTION_SEND);
        mPhoneEdit.setImeActionLabel("Send", EditorInfo.IME_ACTION_SEND);
        mPhoneEdit.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_SEND) {

                    send();
                    return true;
                }
                return false;
            }
        });

    }

    // Country code
    protected static final TreeSet<String> CANADA_CODES = new TreeSet<String>();

    static {
        CANADA_CODES.add("204");
        CANADA_CODES.add("236");
        CANADA_CODES.add("249");
        CANADA_CODES.add("250");
        CANADA_CODES.add("289");
        CANADA_CODES.add("306");
        CANADA_CODES.add("343");
        CANADA_CODES.add("365");
        CANADA_CODES.add("387");
        CANADA_CODES.add("403");
        CANADA_CODES.add("416");
        CANADA_CODES.add("418");
        CANADA_CODES.add("431");
        CANADA_CODES.add("437");
        CANADA_CODES.add("438");
        CANADA_CODES.add("450");
        CANADA_CODES.add("506");
        CANADA_CODES.add("514");
        CANADA_CODES.add("519");
        CANADA_CODES.add("548");
        CANADA_CODES.add("579");
        CANADA_CODES.add("581");
        CANADA_CODES.add("587");
        CANADA_CODES.add("604");
        CANADA_CODES.add("613");
        CANADA_CODES.add("639");
        CANADA_CODES.add("647");
        CANADA_CODES.add("672");
        CANADA_CODES.add("705");
        CANADA_CODES.add("709");
        CANADA_CODES.add("742");
        CANADA_CODES.add("778");
        CANADA_CODES.add("780");
        CANADA_CODES.add("782");
        CANADA_CODES.add("807");
        CANADA_CODES.add("819");
        CANADA_CODES.add("825");
        CANADA_CODES.add("867");
        CANADA_CODES.add("873");
        CANADA_CODES.add("902");
        CANADA_CODES.add("905");
    }

    protected AdapterView.OnItemSelectedListener mOnItemSelectedListener = new AdapterView.OnItemSelectedListener() {
        @Override
        public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
            Country c = (Country) mSpinner.getItemAtPosition(position);
            if (mLastEnteredPhone != null && mLastEnteredPhone.startsWith(c.getCountryCodeStr())) {
                return;
            }
            country = c.getName().trim();
            System.out.println("countryname--"+ country);
            mPhoneEdit.getText().clear();
            mPhoneEdit.getText().insert(mPhoneEdit.getText().length() > 0 ? 1 : 0, String.valueOf(c.getCountryCode()));
            mPhoneEdit.setSelection(mPhoneEdit.length());
            mLastEnteredPhone = null;

             ph=mPhoneEdit.getText().toString();
        }

        @Override
        public void onNothingSelected(AdapterView<?> parent) {
        }
    };

    protected OnPhoneChangedListener mOnPhoneChangedListener = new OnPhoneChangedListener() {
        @Override
        public void onPhoneChanged(String phone) {
            try {
                mLastEnteredPhone = phone;
                Phonenumber.PhoneNumber p = mPhoneNumberUtil.parse(phone, null);
                ArrayList<Country> list = mCountriesMap.get(p.getCountryCode());
                Country country = null;
                if (list != null) {
                    if (p.getCountryCode() == 1) {
                        String num = String.valueOf(p.getNationalNumber());
                        if (num.length() >= 3) {
                            String code = num.substring(0, 3);
                            if (CANADA_CODES.contains(code)) {
                                for (Country c : list) {
                                    // Canada has priority 1, US has priority 0
                                    if (c.getPriority() == 1) {
                                        country = c;
                                        break;
                                    }
                                }
                            }
                        }
                    }
                    if (country == null) {
                        for (Country c : list) {
                            if (c.getPriority() == 0) {
                                country = c;
                                break;
                            }
                        }
                    }
                }
                if (country != null) {
                    final int position = country.getNum();
                    mSpinner.post(new Runnable() {
                        @Override
                        public void run() {
                            mSpinner.setSelection(position);
                        }
                    });
                }
            } catch (NumberParseException ignore) {
            }

        }
    };

    @RequiresApi(api = Build.VERSION_CODES.CUPCAKE)
    protected void initCodes(Context context) {

        new AsyncPhoneInitTask(context).execute();
    }

    @RequiresApi(api = Build.VERSION_CODES.CUPCAKE)
    protected class AsyncPhoneInitTask extends AsyncTask<Void, Void, ArrayList<Country>> {

        private int mSpinnerPosition = -1;
        private Context mContext;


        public AsyncPhoneInitTask(Context context) {

            mContext = context;
        }

        @Override
        protected ArrayList<Country> doInBackground(Void... params) {
            ArrayList<Country> data = new ArrayList<Country>(233);
            BufferedReader reader = null;
            try {
                reader = new BufferedReader(new InputStreamReader(mContext.getApplicationContext().getAssets().open("countries.dat"), "UTF-8"));

                // do reading, usually loop until end of file reading
                String line;
                int i = 0;
                while ((line = reader.readLine()) != null) {
                    //process line
                    Country c = new Country(mContext, line, i);
                    data.add(c);
                    ArrayList<Country> list = mCountriesMap.get(c.getCountryCode());
                    if (list == null) {
                        list = new ArrayList<Country>();
                        mCountriesMap.put(c.getCountryCode(), list);
                    }
                    list.add(c);
                    i++;
                }
            } catch (IOException e) {
                //log the exception
            } finally {
                if (reader != null) {
                    try {
                        reader.close();
                    } catch (IOException e) {
                        //log the exception
                    }
                }
            }



            if (!TextUtils.isEmpty(ph)) {

                return data;
            }
            String countryRegion = PhoneUtils.getCountryRegionFromPhone(mContext);
            int code = mPhoneNumberUtil.getCountryCodeForRegion(countryRegion);

            ArrayList<Country> list = mCountriesMap.get(code);
            if (list != null) {
                for (Country c : list) {
                    if (c.getPriority() == 0) {
                        mSpinnerPosition = c.getNum();
                        break;
                    }
                }
            }
            return data;
        }

        @Override
        protected void onPostExecute(ArrayList<Country> data) {
            mAdapter=new CountryAdapter(Login.this,data);
            mSpinner.setAdapter(mAdapter);
            if (mSpinnerPosition > 0) {
                mSpinner.setSelection(mSpinnerPosition);
            }
        }
    }



    protected String validate() {
        String region = null;
        String phone = null;
        System.out.println("mLastEnteredPhone--1-"+mLastEnteredPhone);
        if (mLastEnteredPhone != null) {
            try {
                Phonenumber.PhoneNumber p = mPhoneNumberUtil.parse(mLastEnteredPhone, null);
                StringBuilder sb = new StringBuilder(16);
                sb.append('+').append(p.getCountryCode()).append(p.getNationalNumber());
                phone = sb.toString();
                System.out.println("here--1-"+phone.toString());
                region = mPhoneNumberUtil.getRegionCodeForNumber(p);
            } catch (NumberParseException ignore) {

                System.out.println("here---"+ignore.getMessage());
            }
        }
        if (region != null) {
            return phone;
        } else {
            StringBuilder sb = new StringBuilder(16);
            sb.append('+').append(mLastEnteredPhone);
            phone = sb.toString();
            return phone;
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.CUPCAKE)
    protected void hideKeyboard(View v) {
        InputMethodManager imm = (InputMethodManager) v.getContext().getApplicationContext().getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
    }

    @RequiresApi(api = Build.VERSION_CODES.CUPCAKE)
    protected void showKeyboard(View v) {
        InputMethodManager imm = (InputMethodManager) v.getContext().getApplicationContext().getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.toggleSoftInput(InputMethodManager.SHOW_FORCED, 0);
    }

}
