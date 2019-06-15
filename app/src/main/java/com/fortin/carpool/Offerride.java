package com.fortin.carpool;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.fortin.carpool.Model.M;
import com.fortin.carpool.Model.VehiclePojo;
import com.fortin.carpool.WebServices.APIService;
import com.fortin.carpool.WebServices.RideAPI;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.ui.PlaceAutocompleteFragment;
import com.google.android.gms.location.places.ui.PlaceSelectionListener;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.JsonHttpResponseHandler;

import org.apache.http.Header;
import org.apache.http.entity.ByteArrayEntity;
import org.apache.http.message.BasicHeader;
import org.apache.http.protocol.HTTP;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

public class Offerride extends AppCompatActivity implements View.OnClickListener {

    PlaceAutocompleteFragment source,destination,route;
    LinearLayout continer1,continerroute,continerroute1,linear,retlinear;
    Button btnnext;
    Spinner spnvehicle;
    TextView txt,tvsmoke,tvac;
    EditText etdeptime,etreturntime,etseats,ettotseats,etrate,etextra,etdeptdate,etretdate;
    RadioButton rbtype,rbp,rbpm,rbpf,rbpb,rbt1,rbt2;
    RadioGroup rgtype,rgpassenger;

    String TAG="offerride";

    int flagac=0,flagsmoke=1;
    String userid="",vehicleid="";
    String sourcename,sourcelat,sourcelong,destinationname,destlat,destlong;
    String deptdate="",returndate="",deptime="",returntime="",rate="0",extra="";
    String triptype,passengertype,seats,totseats,srt="";
    String pnm,plat,plong,routetype;
    JSONObject obj=new JSONObject(),routeobj;

    List<VehiclePojo> list=new ArrayList<VehiclePojo>();
    ArrayList<String> namelist=new ArrayList<>();
    ArrayList<TextView> nmlist=new ArrayList<TextView>();
    ArrayList<String> latlist=new ArrayList<>();
    ArrayList<String> longlist=new ArrayList<>();
    ArrayList<String> typelist=new ArrayList<>();
    //DatePicker
    final Calendar dateAndTime=Calendar.getInstance();
    SimpleDateFormat fmtDateAndTime=new SimpleDateFormat("dd MMM yyyy");
    SimpleDateFormat dtfmt=new SimpleDateFormat("yyyy-MM-dd");
    SimpleDateFormat datetimefmt=new SimpleDateFormat("yyyy-MM-dd hh:mm");
    DatePickerDialog.OnDateSetListener d,d1;
    Date datefmt;

    //TimePicker
    SimpleDateFormat timefmt24=new SimpleDateFormat("hh:mm");
    SimpleDateFormat timefmt12=new SimpleDateFormat("hh:mm a");
    int hour = dateAndTime.get(Calendar.HOUR_OF_DAY);
    int minute = dateAndTime.get(Calendar.MINUTE);
    TimePickerDialog rtimepicker,dtimepicker;

    boolean flag=true;

    Dialog dialog;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_offerride);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        toolbar.setNavigationIcon(R.drawable.backicon);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent it=new Intent(Offerride.this,MainActivity.class);
                finish();
                startActivity(it);
            }
        });

        source = (PlaceAutocompleteFragment)getFragmentManager().findFragmentById(R.id.sourceautocomplete1);
        destination = (PlaceAutocompleteFragment)getFragmentManager().findFragmentById(R.id.destinationautocomplete1);
        route= (PlaceAutocompleteFragment)getFragmentManager().findFragmentById(R.id.routeautocomplete1);
        ettotseats=(EditText)findViewById(R.id.ettotseats);
        ettotseats.setText("0");
        etdeptdate=(EditText)findViewById(R.id.etdeptdate);
        etdeptime=(EditText)findViewById(R.id.etdeptime);
        retlinear=(LinearLayout)findViewById(R.id.returnlinear);
        retlinear.setVisibility(View.GONE);
        etretdate=(EditText)findViewById(R.id.etreturndate);
        etreturntime=(EditText)findViewById(R.id.etreturntime);
        etseats=(EditText)findViewById(R.id.etseats);
        etseats.setText("1");
        etrate=(EditText)findViewById(R.id.etrate);
        etextra=(EditText)findViewById(R.id.etextra);
        rgtype=(RadioGroup)findViewById(R.id.rgtriptype);
        rbt1=(RadioButton)findViewById(R.id.rb1);
        rbt2=(RadioButton)findViewById(R.id.rb2);
        rbpm=(RadioButton)findViewById(R.id.rbp1);
        rbpf=(RadioButton)findViewById(R.id.rbp2);
        rbpb=(RadioButton)findViewById(R.id.rbp3);
        rbpb.setBackgroundColor(getResources().getColor(R.color.radioselected));
        rgpassenger=(RadioGroup)findViewById(R.id.rgpassengertype);
        continer1=(LinearLayout)findViewById(R.id.addcontiner);
        btnnext=(Button)findViewById(R.id.btnnext);

        userid= M.getID(Offerride.this);

        datefmt = dateAndTime.getTime();
        spnvehicle=(Spinner)findViewById(R.id.spnvehicle);
        txt=(TextView)findViewById(R.id.txt);
        linear=(LinearLayout) findViewById(R.id.linear);
        linear.setVisibility(View.GONE);
        namelist.clear();
        if(userid==null || userid.equals("0")){
            Intent it=new Intent(Offerride.this,Login.class);
            finish();
            startActivity(it);
        }else
            fillvehicle();

        spnvehicle.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String selitem=parent.getSelectedItem().toString();
                if(selitem!=getString(R.string.selvehicle)) {
                    vehicleid = list.get(position-1).getVehicle_id();
                    String model = list.get(position-1).getModel();
                    String company = list.get(position-1).getCompany();
                    String type = list.get(position-1).getType();
                    totseats = list.get(position-1).getSeats();
                    if (vehicleid.length()!=0 && vehicleid!="0") {
                        linear.setVisibility(View.VISIBLE);
                        txt.setText(getString(R.string.model) + model + "-" + company + " (" + type + ")");
                        ettotseats.setText(totseats);
                    }
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        tvsmoke=(TextView)findViewById(R.id.smoke);
        tvsmoke.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(flagsmoke==0) {
                    tvsmoke.setCompoundDrawablesWithIntrinsicBounds(0,  R.drawable.smoking,0, 0);
                    tvsmoke.setText(getString(R.string.smoking));
                    flagsmoke=1;
                }
                else {
                    tvsmoke.setCompoundDrawablesWithIntrinsicBounds(0,R.drawable.nosmoking, 0, 0);
                    tvsmoke.setText(getString(R.string.nosmoking));
                    flagsmoke=0;
                }
            }
        });
        tvac= (TextView) findViewById(R.id.ac);
        tvac.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (flagac == 0) {
                    tvac.setCompoundDrawablesWithIntrinsicBounds(0, R.drawable.ac,0, 0);
                    tvac.setText(getString(R.string.ac));
                    flagac = 1;
                } else {
                    tvac.setCompoundDrawablesWithIntrinsicBounds(0,R.drawable.noac, 0,  0);
                    tvac.setText(getString(R.string.noac));
                    flagac = 0;
                }
            }
        });
        source.setOnPlaceSelectedListener(new PlaceSelectionListener() {
            @Override
            public void onPlaceSelected(Place place) {
                sourcename = place.getName().toString();
                sourcelat = place.getLatLng().latitude + "";
                sourcelong = place.getLatLng().longitude + "";
            }

            @Override
            public void onError(Status status) {
                Log.e(TAG, "source onError: Status = " + status.toString());
                Toast.makeText(Offerride.this, "Source Place selection failed: " + status.getStatusMessage(), Toast.LENGTH_SHORT).show();
            }
        });
        destination.setOnPlaceSelectedListener(new PlaceSelectionListener() {
            @Override
            public void onPlaceSelected(Place place) {
                destinationname = place.getName().toString();
                destlat = place.getLatLng().latitude + "";
                destlong = place.getLatLng().longitude + "";
            }

            @Override
            public void onError(Status status) {
                Log.e(TAG, "destination onError: Status = " + status.toString());
                Toast.makeText(Offerride.this, "Destination Place selection failed: " + status.getStatusMessage(), Toast.LENGTH_SHORT).show();
            }
        });


        route.setOnPlaceSelectedListener(new PlaceSelectionListener() {
            @Override
            public void onPlaceSelected(Place place) {
                if (nmlist.size() < 3) {
                    addItemRow(place.getName().toString(), place.getLatLng().latitude + "", place.getLatLng().longitude + "");
                    route.setHint(getString(R.string.selroute));
                    route.setText("");
                } else {
                    Toast.makeText(Offerride.this, R.string.addrouterror, Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onError(Status status) {

            }
        });

        etdeptdate.setOnClickListener(this);
        etretdate.setOnClickListener(this);
        etreturntime.setOnClickListener(this);
        etdeptime.setOnClickListener(this);

        deptdate=dtfmt.format(datefmt);
        etdeptdate.setText(fmtDateAndTime.format(datefmt));
        etdeptdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DatePickerDialog d1 = new DatePickerDialog(Offerride.this, d, dateAndTime.get(Calendar.YEAR), dateAndTime.get(Calendar.MONTH), dateAndTime.get(Calendar.DAY_OF_MONTH));
                d1.show();
            }
        });

        rgtype.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {

                if (checkedId == R.id.rb1) {
                    retlinear.setVisibility(View.GONE);

                } else {
                   retlinear.setVisibility(View.VISIBLE);
                    returndate = dtfmt.format(datefmt);
                    etretdate.setText(fmtDateAndTime.format(datefmt));
                    etretdate.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {

                            DatePickerDialog d2 = new DatePickerDialog(Offerride.this, d1, dateAndTime.get(Calendar.YEAR), dateAndTime.get(Calendar.MONTH), dateAndTime.get(Calendar.DAY_OF_MONTH));
                            d2.show();
                        }
                    });
                }
            }
        });


        d=new DatePickerDialog.OnDateSetListener() {
            public void onDateSet(DatePicker arg0, int arg1, int arg2,int arg3)
            {
              
                dateAndTime.set(Calendar.YEAR, arg1);
                dateAndTime.set(Calendar.MONTH,arg2);
                dateAndTime.set(Calendar.DAY_OF_MONTH, arg3);

                etdeptdate.setText(fmtDateAndTime.format(dateAndTime.getTime()));
                deptdate=dtfmt.format(dateAndTime.getTime());

            }
        };

        d1=new DatePickerDialog.OnDateSetListener() {
            public void onDateSet(DatePicker arg0, int arg1, int arg2,int arg3)
            {
                dateAndTime.set(Calendar.YEAR, arg1);
                dateAndTime.set(Calendar.MONTH, arg2);
                dateAndTime.set(Calendar.DAY_OF_MONTH, arg3);
                etretdate.setText(fmtDateAndTime.format(dateAndTime.getTime()));
                returndate=dtfmt.format(dateAndTime.getTime());
            }
        };

        rgpassenger.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                RadioButton radioButton = (RadioButton) group.findViewById(checkedId);
                radioButton.setBackgroundColor(getResources().getColor(R.color.radioselected));
                if (!rbpf.isChecked())
                    rbpf.setBackgroundColor(getResources().getColor(android.R.color.transparent));
                if (!rbpm.isChecked())
                    rbpm.setBackgroundColor(getResources().getColor(android.R.color.transparent));
                if (!rbpb.isChecked())
                    rbpb.setBackgroundColor(getResources().getColor(android.R.color.transparent));
            }
        });

        btnnext.setOnClickListener(this);

    }

    public void fillvehicle(){
       Log.d(TAG,userid);
        M.showLoadingDialog(Offerride.this);
        RideAPI mCommentsAPI = APIService.createService(RideAPI.class);
        mCommentsAPI.getVehicles(userid, new Callback<List<VehiclePojo>>() {
            @Override
            public void success(List<VehiclePojo> pojo, Response response) {
                Log.d(TAG,"fill vehicle success");
                list = pojo;
                if(list.size()==0) {
                    Toast.makeText(Offerride.this, R.string.vehiclealert, Toast.LENGTH_SHORT).show();
                    btnnext.setVisibility(View.GONE);
                    finish();
                }
                else {
                    namelist.add(getString(R.string.selvehicle));
                    btnnext.setVisibility(View.VISIBLE);
                    for (VehiclePojo data : list) {
                        Log.d(TAG,"vnm:"+data.getNumber());
                        namelist.add(data.getNumber());
                    }
                    ArrayAdapter<String> adapter = new ArrayAdapter<String>(Offerride.this, android.R.layout.simple_list_item_1, namelist);
                    spnvehicle.setAdapter(adapter);
                }
                M.hideLoadingDialog();
            }

            @Override
            public void failure(RetrofitError error) {
                M.hideLoadingDialog();
                System.out.print("error:fill vehicle:" + error.getMessage());
            }
        });
    }

    private void addItemRow(String placenm, final String placelat, final String placelong) {
        int itemsCount = 1;
        for (int i = 0; i < itemsCount; i++) {
            View v = getLayoutInflater().inflate(R.layout.add_row,null);
            final TextView nm=(TextView)v.findViewById(R.id.row);
            ImageView img=(ImageView)v.findViewById(R.id.img);
            nm.setText(placenm);
            nm.setTextColor(getResources().getColor(R.color.txtcolordark));
            img.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    ((LinearLayout) nm.getParent()).removeView(nm);
                    ((LinearLayout) v.getParent()).removeView(v);
                    nmlist.remove(nm);
                    latlist.remove(placelat);
                    longlist.remove(placelong);
                }
            });
            nmlist.add(nm);
            latlist.add(placelat);
            longlist.add(placelong);
            typelist.add("0");
            continer1.addView(v);
        }
    }

    private void addRouteRow(String placenm,final String placelat, final String placelong,final String direction) {
        int itemsCount = 1;

        for (int i = 0; i < itemsCount; i++) {
            View v = getLayoutInflater().inflate(R.layout.add_row,null);
            final TextView nm=(TextView)v.findViewById(R.id.row);
            ImageView img=(ImageView)v.findViewById(R.id.img);
            nm.setText(placenm);
            nm.setTextColor(getResources().getColor(R.color.txtcolor));
            nm.setTextSize(18);
            img.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    ((LinearLayout) nm.getParent()).removeView(nm);
                    ((LinearLayout) v.getParent()).removeView(v);
                    nmlist.remove(nm);
                    latlist.remove(placelat);
                    longlist.remove(placelong);
                }
            });
            nmlist.add(nm);
            latlist.add(placelat);
            longlist.add(placelong);

            if(direction.equalsIgnoreCase("one")) {
                typelist.add("0");
                continerroute.addView(v);
            }
            else if(direction.equalsIgnoreCase("two")) {
                typelist.add("1");
                continerroute1.addView(v);
            }
        }
    }

    public JSONObject getdata()
    {
        ArrayList<Integer> skipped = new ArrayList<Integer>();
        StringBuilder stringBuilder = new StringBuilder();
        StringBuilder stringBuilder2 = new StringBuilder();
        StringBuilder stringBuilder3 = new StringBuilder();
        StringBuilder stringBuilder4 = new StringBuilder();

        for (int i=0;i<nmlist.size();i++) {
            String name = nmlist.get(i).getText().toString();
            String lat=latlist.get(i);
            String plong1=longlist.get(i);
            String ptype=typelist.get(i);

            if(name.length() > 0)
            {
                stringBuilder.append(name).append(",");
                stringBuilder2.append(lat).append(",");
                stringBuilder3.append(plong1).append(",");
                stringBuilder4.append(ptype).append(",");
            }
            else
            {
                skipped.add(i);
            }
        }

        pnm = stringBuilder.toString();
        plat=stringBuilder2.toString();
        plong=stringBuilder3.toString();
        routetype=stringBuilder4.toString();
        if(pnm.length() > 0)
        {
            pnm = pnm.substring(0, pnm.length()-1);
            plat=plat.substring(0,plat.length()-1);
            plong=plong.substring(0,plong.length()-1);
            routetype=routetype.substring(0,routetype.length()-1);
        }

        if(pnm.length() > 0)
        {
            JSONArray json=new JSONArray();
            String[] itemnm = pnm.split(",");
            String[] itemlat=plat.split(",");
            String[] itemlong=plong.split(",");
            String[] itemtype=routetype.split(",");

            for(int p=0;p<itemnm.length;p++){
                JSONObject obj1=new JSONObject();
                String pnm=itemnm[p];
                String plat=itemlat[p];
                String plong=itemlong[p];
                String rt=itemtype[p];
                try {
                    obj1.put("route_name",pnm);
                    obj1.put("route_lat",plat);
                    obj1.put("route_long",plong);
                    obj1.put("route_type",rt);
                    json.put(obj1);
                    obj.put("route",json);

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }
        return obj;
    }

    @Override
    public void onClick(View v) {
        if(v.getId()==R.id.etdeptime){
            dtimepicker = new TimePickerDialog(Offerride.this, new TimePickerDialog.OnTimeSetListener() {
                @Override
                public void onTimeSet(TimePicker timePicker, int selectedHour, int selectedMinute) {
                    deptime=selectedHour + ":" + selectedMinute;
                    try {
                        Date dtime=timefmt24.parse(deptime);
                        etdeptime.setText(timefmt12.format(dtime));
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }

                }
            }, hour, minute, false);//Yes 24 hour time
            dtimepicker.setTitle(getString(R.string.seldepture));
            dtimepicker.show();

        }
        else if(v.getId()==R.id.etreturntime){
            rtimepicker = new TimePickerDialog(Offerride.this, new TimePickerDialog.OnTimeSetListener() {
                @Override
                public void onTimeSet(TimePicker timePicker, int selectedHour, int selectedMinute) {
                    returntime=selectedHour + ":" + selectedMinute;
                    try {
                        Date rtime=timefmt24.parse(returntime);
                        etreturntime.setText(timefmt12.format(rtime));
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }

                }
            }, hour, minute, false);//Yes 24 hour time
            rtimepicker.setTitle(getString(R.string.selreturn));
            rtimepicker.show();
        }

        else if(v.getId()==R.id.btnnext){
            if(userid.length()==0 || userid.equals('0')){
                Toast.makeText(Offerride.this,"userid empty",Toast.LENGTH_SHORT).show();
            }
            else if(vehicleid.length()==0 || vehicleid.equals('0')){
                Toast.makeText(Offerride.this, R.string.selcarerror,Toast.LENGTH_SHORT).show();
            }
            else if (deptime.length()==0 || deptdate.length()==0){
                Toast.makeText(Offerride.this, R.string.depaturerror,Toast.LENGTH_SHORT).show();
            }
            else if(sourcename.length()!=0 && destinationname.length()!=0) {

                int selId=rgtype.getCheckedRadioButtonId();
                rbtype=(RadioButton)findViewById(selId);
                triptype=rbtype.getText().toString();

                seats=etseats.getText().toString();
                if(Integer.parseInt(seats)==0){
                    Toast.makeText(Offerride.this, R.string.minseatmsg,Toast.LENGTH_SHORT).show();
                    flag=false;
                }
                else if(Integer.parseInt(seats)>Integer.parseInt(totseats)){
                    flag=false;
                    nmlist.clear();
                    latlist.clear();
                    longlist.clear();
                    typelist.clear();
                    Toast.makeText(Offerride.this,"Only "+totseats+" Avaliable...",Toast.LENGTH_SHORT).show();
                }

                if(flag==true) {
                    ArrayList<String> listnm = new ArrayList<>();
                    ArrayList<String> listlat = new ArrayList<>();
                    ArrayList<String> listlong = new ArrayList<>();
                    routeobj = getdata();
                    listnm.add(sourcename);
                    listlat.add(sourcelat);
                    listlong.add(sourcelong);
                    if (routeobj.length() != 0) {
                        try {
                            JSONArray json = routeobj.getJSONArray("route");
                            for (int i = 0; i < json.length(); i++) {
                                String r = json.getJSONObject(i).getString("route_name");
                                String rlat = json.getJSONObject(i).getString("route_lat");
                                String rlong = json.getJSONObject(i).getString("route_long");

                                if (r.length()!=0) {
                                    listnm.add(r);
                                    listlat.add(rlat);
                                    listlong.add(rlong);
                                }
                            }

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }

                    listnm.add(destinationname);
                    listlat.add(destlat);
                    listlong.add(destlong);
                    dialog = new Dialog(Offerride.this);
                    dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                    dialog.getWindow().setLayout(LinearLayout.LayoutParams.MATCH_PARENT,
                            LinearLayout.LayoutParams.MATCH_PARENT);
                    dialog.setContentView(R.layout.route_dialog);
                    dialog.getWindow().setBackgroundDrawableResource(R.color.colorPrimary);
                    continerroute = (LinearLayout) dialog.findViewById(R.id.continerroute);
                    continerroute1 = (LinearLayout) dialog.findViewById(R.id.continerroute1);
                    TextView title=(TextView)dialog.findViewById(R.id.tvtitle);
                    TextView title1=(TextView)dialog.findViewById(R.id.tvtitle1);
                    title1.setVisibility(View.GONE);
                    continerroute1.setVisibility(View.GONE);
                    title.setText(R.string.tripdirection);
                    Button btnconfirm = (Button) dialog.findViewById(R.id.btnconfirm);
                    nmlist.clear();
                    latlist.clear();
                    longlist.clear();
                    typelist.clear();
                    for (int j = 0; j < listnm.size(); j++) {
                        for (int k = j; k < listnm.size(); k++) {
                            if (j != k) {
                                String p = listnm.get(j) + "-" + listnm.get(k);
                                String plat = listlat.get(j) + "-" + listlat.get(k);
                                String plong = listlong.get(j) + "-" + listlong.get(k);
                                addRouteRow(p, plat, plong,"one");
                            }
                        }
                    }

                    if(triptype.equalsIgnoreCase(getString(R.string.roundtrip)))
                    {
                        title1.setVisibility(View.VISIBLE);
                        continerroute1.setVisibility(View.VISIBLE);
                        title1.setText(R.string.returntripdirection);

                        for (int j = listnm.size()-1; j >= 0; j--) {
                            for (int k = j; k >=0; k--) {
                                if (j != k) {
                                    Log.d(TAG, " return route leg:" + listnm.get(j) + "-" + listnm.get(k));
                                   String p = listnm.get(j) + "-" + listnm.get(k);
                                   String plat = listlat.get(j) + "-" + listlat.get(k);
                                   String plong = listlong.get(j) + "-" + listlong.get(k);
                                   addRouteRow(p, plat, plong,"two");
                                }
                            }
                        }
                    }
                    btnconfirm.setOnClickListener(this);
                    dialog.show();
                }
            }
            else
                Toast.makeText(Offerride.this, R.string.emptyridemsg,Toast.LENGTH_SHORT).show();
        }
        else if(v.getId()==R.id.btnconfirm){

            dialog.cancel();
            int selId1=rgpassenger.getCheckedRadioButtonId();
            rbp=(RadioButton)findViewById(selId1);
            passengertype=rbp.getText().toString();

            if(triptype.equalsIgnoreCase(getString(R.string.oneway))) {
                returntime = "00:00";
                returndate="0000-00-00";
                flag=true;
            }
            else {
                try {
                    Date deptdt=datetimefmt.parse(deptdate+" "+deptime);
                    Date retdt=datetimefmt.parse(returndate+" "+returntime);
                    if(deptdt.after(retdt)) {
                        nmlist.clear();
                        latlist.clear();
                        longlist.clear();
                        typelist.clear();
                        Toast.makeText(Offerride.this, R.string.dateerrormsg,Toast.LENGTH_LONG).show();
                        flag=false;
                    }else
                        flag=true;
                } catch (ParseException e) {
                    e.printStackTrace();
                    Log.d(TAG,"error:"+e.getMessage());
                }
            }

            if(flag==true){
                rate=etrate.getText().toString();
                routeobj=getdata();
                extra=etextra.getText().toString();
                JSONObject ride=new JSONObject();
                try {
                    ride.put("user_id",userid);
                    ride.put("vehicle_id",vehicleid);
                    ride.put("source",sourcename);
                    ride.put("source_lat",sourcelat);
                    ride.put("source_long",sourcelong);
                    ride.put("destination",destinationname);
                    ride.put("dest_lat",destlat);
                    ride.put("dest_long",destlong);
                    ride.put("depature_date",deptdate);
                    ride.put("depature_time",deptime);
                    ride.put("return_date",returndate);
                    ride.put("return_time",returntime);
                    ride.put("trip_type",triptype);
                    ride.put("frequency",srt);
                    ride.put("seats",seats);
                    ride.put("passenger_type",passengertype);
                    ride.put("rate",rate);
                    ride.put("ac",flagac+"");
                    ride.put("smoking",flagsmoke+"");
                    ride.put("extra",extra);
                    ride.put("route",routeobj);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                Log.d(TAG, ride.toString());
                postride(ride);
            }

        }
    }

    public void postride(JSONObject rideobj){
        M.showLoadingDialog(this);
        try {
            ByteArrayEntity entity = new ByteArrayEntity(rideobj.toString().getBytes("UTF-8"));
            entity.setContentType(new BasicHeader(HTTP.CONTENT_TYPE, "application/json"));
            AsyncHttpClient client = new AsyncHttpClient();
            client.post(Offerride.this,AppConst.MAIN+"offerRide.php",entity,"application/json",new JsonHttpResponseHandler(){
                @Override
                public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                    //super.onSuccess(statusCode, headers, response);
                    Toast.makeText(Offerride.this, R.string.postridesuccess,Toast.LENGTH_SHORT).show();
                    Intent it=new Intent(Offerride.this,MainActivity.class);
                    finish();
                    startActivity(it);
                    M.hideLoadingDialog();
                }

                @Override
                public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                    //super.onFailure(statusCode, headers, throwable, errorResponse);
                    if (statusCode == 404) {
                        Toast.makeText(getApplicationContext(), "404 - Nie odnaleziono serwera!", Toast.LENGTH_LONG).show();
                    } else if (statusCode == 500) {
                        Toast.makeText(getApplicationContext(), "500 - Coś poszło nie tak po stronie serwera!", Toast.LENGTH_LONG).show();
                    } else if (statusCode == 403) {
                        Toast.makeText(getApplicationContext(), "Podano niepoprawne dane!", Toast.LENGTH_LONG).show();
                    } else {
                        Toast.makeText(Offerride.this, errorResponse.toString(), Toast.LENGTH_LONG).show();
                    }
                }
            });
            M.hideLoadingDialog();
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
            Toast.makeText(getApplicationContext(), e.getMessage(), Toast.LENGTH_LONG).show();
            M.hideLoadingDialog();
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent it=new Intent(Offerride.this,MainActivity.class);
        finish();
        startActivity(it);
    }
}
