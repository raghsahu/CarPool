package com.fortin.carpool;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.fortin.carpool.Model.FindHistoryPojo;
import com.fortin.carpool.Model.M;
import com.fortin.carpool.Model.RidePojo;
import com.fortin.carpool.WebServices.APIService;
import com.fortin.carpool.WebServices.RideAPI;
import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.common.GooglePlayServicesRepairableException;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.ui.PlaceAutocomplete;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

public class FindRide extends AppCompatActivity implements View.OnClickListener {
    String TAG="Findride";
    EditText etsource,etdest;
    ListView lvgetride,lvhistory;
    ImageView btnfindride;
    TextView tvmsg;
    CheckBox chkfemale;
    RadioGroup rgtype;
    RadioButton rball,rbeven,rbodd;
    EditText etdate;
    Button btnfilter;

    String sourcename="",sourcelat,sourcelong,destinationname="",destlat,destlong;
    String filterfemaleride="",filternumtype="",filterdate="";
    int Source_AUTOCOMPLETE_REQUEST_CODE = 1;
    int Destination_AUTOCOMPLETE_REQUEST_CODE =2;

    List<RidePojo> list=new ArrayList<>();
    FindRideAdapter adapter;
    ArrayList<FindHistoryPojo> findlist=new ArrayList<>();
    Dialog dialog;
    DatePickerDialog.OnDateSetListener d;
    final Calendar dateAndTime=Calendar.getInstance();
    SimpleDateFormat fmtDateAndTime=new SimpleDateFormat("dd MMM yyyy");
    SimpleDateFormat dtfmt=new SimpleDateFormat("yyyy-MM-dd");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_find_ride);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        toolbar.setNavigationIcon(R.drawable.backicon);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               onBackPressed();
            }
        });
        AppConst.droppoint=null;
        AppConst.pickupoint=null;

        etsource=(EditText)findViewById(R.id.etsource);
        etsource.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent =null;
                try {
                    intent = new PlaceAutocomplete.IntentBuilder(PlaceAutocomplete.MODE_FULLSCREEN)
                            .build(FindRide.this);
                } catch (GooglePlayServicesRepairableException e) {
                    e.printStackTrace();
                } catch (GooglePlayServicesNotAvailableException e) {
                    e.printStackTrace();
                }
                startActivityForResult(intent, Source_AUTOCOMPLETE_REQUEST_CODE);
            }
        });

        etdest=(EditText)findViewById(R.id.etdestination);
        etdest.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent =null;
                try {
                    intent = new PlaceAutocomplete.IntentBuilder(PlaceAutocomplete.MODE_FULLSCREEN)
                            .build(FindRide.this);
                } catch (GooglePlayServicesRepairableException e) {
                    e.printStackTrace();
                } catch (GooglePlayServicesNotAvailableException e) {
                    e.printStackTrace();
                }
                startActivityForResult(intent, Destination_AUTOCOMPLETE_REQUEST_CODE);
            }
        });

        lvgetride=(ListView)findViewById(R.id.lvgetride);
        lvhistory=(ListView)findViewById(R.id.lvhistory);
        lvhistory.setVisibility(View.GONE);
        tvmsg=(TextView)findViewById(R.id.tvmsg);
        tvmsg.setVisibility(View.GONE);
        lvgetride.setVisibility(View.GONE);
        btnfindride=(ImageView)findViewById(R.id.btnfind);
        btnfindride.setOnClickListener(this);
        lvgetride.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                AppConst.selride = list.get(position);
                AppConst.pickupoint = sourcename;
                AppConst.droppoint = destinationname;
                Intent intent = new Intent(FindRide.this, RideDetail.class);
                startActivity(intent);
            }
        });

        getFindHistory();

        lvhistory.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                sourcename=findlist.get(position).getSource();
                destinationname=findlist.get(position).getDestination();
                sourcelat=findlist.get(position).getSource_lat();
                sourcelong=findlist.get(position).getSource_long();
                destlat=findlist.get(position).getDest_lat();
                destlong=findlist.get(position).getDest_long();
                findRide(filterfemaleride,filternumtype,filterdate);
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == Source_AUTOCOMPLETE_REQUEST_CODE) {
            if (resultCode == RESULT_OK) {
                Place place = PlaceAutocomplete.getPlace(this, data);
                sourcename=place.getName().toString();
                sourcelat=place.getLatLng().latitude+"";
                sourcelong=place.getLatLng().longitude+"";
                etsource.setText(place.getName());
            } else if (resultCode == PlaceAutocomplete.RESULT_ERROR) {
                Status status = PlaceAutocomplete.getStatus(this, data);
                Log.i(TAG, status.getStatusMessage());
            } else if (resultCode == RESULT_CANCELED) {
            }
        }
        if (requestCode == Destination_AUTOCOMPLETE_REQUEST_CODE) {
            if (resultCode == RESULT_OK) {
                Place place = PlaceAutocomplete.getPlace(this, data);
                destinationname=place.getName().toString();
                destlat=place.getLatLng().latitude+"";
                destlong=place.getLatLng().longitude+"";
                etdest.setText(place.getName());
            } else if (resultCode == PlaceAutocomplete.RESULT_ERROR) {
                Status status = PlaceAutocomplete.getStatus(this, data);
                Log.i(TAG, status.getStatusMessage());
            } else if (resultCode == RESULT_CANCELED) {

            }
        }
    }

    private void getFindHistory() {

        FindHistoryIS c=new FindHistoryIS();
        findlist=c.getHistory(FindRide.this);

        if(findlist.size()==0){
            Log.d(TAG,"History not avaliable...");
        }else {
            lvhistory.setVisibility(View.VISIBLE);
            lvgetride.setVisibility(View.GONE);
            tvmsg.setVisibility(View.GONE);
            ArrayList<String> namelist=new ArrayList<>();
            for(int i=0;i<findlist.size();i++){
                namelist.add(findlist.get(i).getSource() + " >> " + findlist.get(i).getDestination());
            }
            ArrayAdapter<String> adapter = new ArrayAdapter<String>(FindRide.this, android.R.layout.simple_list_item_1, namelist);
            lvhistory.setAdapter(adapter);
        }

    }

    public  void insertHistory(){
        FindHistoryPojo hpojo=new FindHistoryPojo();
        FindHistoryIS f=new FindHistoryIS();
        Boolean chk=f.check(sourcename,destinationname,FindRide.this);
        if(chk==true) {
            hpojo.setSource(sourcename);
            hpojo.setSource_lat(sourcelat);
            hpojo.setSource_long(sourcelong);
            hpojo.setDestination(destinationname);
            hpojo.setDest_lat(destlat);
            hpojo.setDest_long(destlong);
            f.insert(hpojo, FindRide.this);
        }
    }

    @Override
    public void onClick(View v) {
        if(v.getId()==R.id.btnfind){
            if(sourcename.length()==0)
                Toast.makeText(FindRide.this,"Select Pickup Point...",Toast.LENGTH_SHORT).show();
            else if (destinationname.length()==0)
                Toast.makeText(FindRide.this,"Select Drop Point...",Toast.LENGTH_SHORT).show();
            else {
                insertHistory();
                findRide(filterfemaleride,filternumtype,filterdate);
            }
        }
    }

    public  void findRide(String femaleride,String numtype,String selectdt){
        M.showLoadingDialog(FindRide.this);
        RideAPI mCommentsAPI = APIService.createService(RideAPI.class);
        mCommentsAPI.getRides(sourcelat, sourcelong, destlat, destlong,femaleride,numtype,selectdt, new Callback<List<RidePojo>>() {
            @Override
            public void success(List<RidePojo> pojo, Response response) {
                list.clear();
                if(pojo.size()!=0) {
                    for (int j=0;j<pojo.size();j++) {
                        String deptdt=pojo.get(j).getDep_time();
                        SimpleDateFormat dtfmt=new SimpleDateFormat("dd MMM yyyy hh:mm a");
                        try {
                            Date dt=dtfmt.parse(deptdt);
                            Date todt=new Date();
                            if(dt.after(todt)){
                                list.add(pojo.get(j));
                            }
                        } catch (ParseException e) {
                            e.printStackTrace();
                        }
                    }

                    if(list.size()!=0) {
                        lvgetride.setVisibility(View.VISIBLE);
                        tvmsg.setVisibility(View.GONE);
                        lvhistory.setVisibility(View.GONE);
                        adapter = new FindRideAdapter(FindRide.this, (ArrayList<RidePojo>) list);
                        lvgetride.setAdapter(adapter);
                        M.hideLoadingDialog();
                    }
                    else {
                        tvmsg.setVisibility(View.VISIBLE);
                        lvgetride.setVisibility(View.GONE);
                        lvhistory.setVisibility(View.GONE);
                        tvmsg.setText("Sorry! Rides not avaliable... Please Try Later... ");
                        M.hideLoadingDialog();
                    }
                }else {
                    tvmsg.setVisibility(View.VISIBLE);
                    lvgetride.setVisibility(View.GONE);
                    lvhistory.setVisibility(View.GONE);
                    tvmsg.setText("Sorry! Rides not avaliable... Please Try Later... ");
                    M.hideLoadingDialog();
                }
                M.hideLoadingDialog();
            }

            @Override
            public void failure(RetrofitError error) {
                M.hideLoadingDialog();
                System.out.print("error:" + error.getMessage());
            }

        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_findride, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.filter) {
            filteroption();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    public void filteroption(){
        dialog = new Dialog(FindRide.this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.getWindow().setLayout(LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.MATCH_PARENT);
        dialog.setContentView(R.layout.filter_dialog);
        dialog.getWindow().setBackgroundDrawableResource(R.color.white);
        chkfemale=(CheckBox)dialog.findViewById(R.id.checkBox);
        rgtype=(RadioGroup)dialog.findViewById(R.id.numbertype);
        rball=(RadioButton)dialog.findViewById(R.id.all);
        rbeven=(RadioButton)dialog.findViewById(R.id.even);
        rbodd=(RadioButton)dialog.findViewById(R.id.odd);
        etdate=(EditText)dialog.findViewById(R.id.seldt);
        etdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DatePickerDialog d1 = new DatePickerDialog(FindRide.this, d, dateAndTime.get(Calendar.YEAR), dateAndTime.get(Calendar.MONTH), dateAndTime.get(Calendar.DAY_OF_MONTH));
                d1.show();
            }
        });
        btnfilter=(Button)dialog.findViewById(R.id.btnfilter);
        ImageView clear=(ImageView)dialog.findViewById(R.id.ivclear);

        if(M.loadSavedPreferences("filterdate",FindRide.this)!=null) {
            filterdate=M.loadSavedPreferences("filterdate", FindRide.this);
            try {
                Date dt=dtfmt.parse(filterdate);
                etdate.setText(fmtDateAndTime.format(dt));
            } catch (ParseException e) {
                e.printStackTrace();
            }
        }
        if(M.loadSavedPreferences("filterfemale",FindRide.this)!=null){
            filterfemaleride=M.loadSavedPreferences("filterfemale",FindRide.this);
            chkfemale.setChecked(Boolean.parseBoolean(filterfemaleride));
        }
        if(M.loadSavedPreferences("filternum",FindRide.this)!=null){
            filternumtype=M.loadSavedPreferences("filternum",FindRide.this);
            if(filternumtype.equalsIgnoreCase(getString(R.string.odd)))
                rbodd.setChecked(true);
            else if(filternumtype.equalsIgnoreCase(getString(R.string.even)))
                rbeven.setChecked(true);
            else
                rball.setChecked(true);
        }
        btnfilter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                int selid=rgtype.getCheckedRadioButtonId();
                RadioButton rb=(RadioButton)dialog.findViewById(selid);
                String type=rb.getText().toString();

                filterfemaleride=chkfemale.isChecked()+"";
                filternumtype=type;
                M.savePreferences("filterdate",filterdate.toString(),FindRide.this);
                M.savePreferences("filterfemale",filterfemaleride.toString(),FindRide.this);
                M.savePreferences("filternum", filternumtype.toString(), FindRide.this);
                //Log.d(TAG,"filter:"+filterfemaleride+" "+filternumtype+" "+filterdate);
                findRide(filterfemaleride, filternumtype, filterdate);
                dialog.cancel();
            }
        });

        clear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                etdate.setText("");
                filterdate="";
                M.savePreferences("filterdate", null, FindRide.this);
            }
        });

        d=new DatePickerDialog.OnDateSetListener() {
            public void onDateSet(DatePicker arg0, int arg1, int arg2,int arg3)
            {

                dateAndTime.set(Calendar.YEAR, arg1);
                dateAndTime.set(Calendar.MONTH,arg2);
                dateAndTime.set(Calendar.DAY_OF_MONTH, arg3);

                etdate.setText(fmtDateAndTime.format(dateAndTime.getTime()));
                filterdate=dtfmt.format(dateAndTime.getTime());

            }
        };
        dialog.show();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent it=new Intent(FindRide.this,MainActivity.class);
        finish();
        startActivity(it);
    }
}
