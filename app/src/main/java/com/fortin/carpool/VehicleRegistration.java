package com.fortin.carpool;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.Toast;

import com.fortin.carpool.Model.CategoryPojo;
import com.fortin.carpool.Model.M;
import com.fortin.carpool.Model.VehiclePojo;
import com.fortin.carpool.Model.userRegPojo;
import com.fortin.carpool.WebServices.APIService;
import com.fortin.carpool.WebServices.CategoryAPI;
import com.fortin.carpool.WebServices.RequestedRideAPI;
import com.fortin.carpool.WebServices.vehicleRegAPI;
import com.fortin.carpool.helper.FilePath;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import cn.pedant.SweetAlert.SweetAlertDialog;
import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;
import retrofit.mime.TypedFile;

public class VehicleRegistration extends AppCompatActivity implements View.OnClickListener {

    String TAG="Vehicle Registration";
    LinearLayout insertlinear,updatelinear;
    Spinner etcompany;
    Button btnsave,btnupdate,btndelete;
    ImageView ivvehicle;
    RadioGroup rgtype;
    RadioButton rbtype,rbpetrol,rbdiesel,rbcng;
    EditText etcarnum,etseats,etmodel;

    List<CategoryPojo> catlist=new ArrayList<CategoryPojo>();
    ArrayList<String> catidlist=new ArrayList<>();
    ArrayList<String> list = new ArrayList<String>();
    private Intent mIntent;
    String company,vehicleimg=null,type,number,seats,userid="",model,vehicleid,companyid="0";
    private static int RESULT_LOAD_IMAGE1 = 1;
    VehiclePojo vehicledata;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_vehicle_registration);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        toolbar.setNavigationIcon(R.drawable.backicon);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();

            }
        });
        userid=M.getID(VehicleRegistration.this);

        if(userid.length()==0 || userid.equals("0")){
            finish();
        }
        insertlinear=(LinearLayout)findViewById(R.id.savelinear);
        updatelinear=(LinearLayout)findViewById(R.id.updatelinear);
        etcompany=(Spinner) findViewById(R.id.etcompany);
        ivvehicle=(ImageView)findViewById(R.id.ivcar);
        etmodel=(EditText)findViewById(R.id.etmodel);
        etcarnum=(EditText)findViewById(R.id.etnum);
        etseats=(EditText)findViewById(R.id.etseat);
        rgtype=(RadioGroup)findViewById(R.id.rgtype);
        rbpetrol=(RadioButton)findViewById(R.id.rb1);
        rbpetrol.setBackgroundColor(getResources().getColor(R.color.radioselected));
        rbdiesel=(RadioButton)findViewById(R.id.rb2);
        rbcng=(RadioButton)findViewById(R.id.rb3);
        btnsave=(Button)findViewById(R.id.btnsubmit);
        btnupdate=(Button)findViewById(R.id.btnupdate);
        btndelete=(Button)findViewById(R.id.btndelete);
        btnsave.setOnClickListener(this);
        btnupdate.setOnClickListener(this);
        btndelete.setOnClickListener(this);
        ivvehicle.setOnClickListener(this);

        vehicledata=AppConst.selvehicle;
        if(vehicledata!=null){
            updatelinear.setVisibility(View.VISIBLE);
            insertlinear.setVisibility(View.GONE);
            vehicleid=vehicledata.getVehicle_id();


            etmodel.setText(vehicledata.getModel());
            etcarnum.setText(vehicledata.getNumber());
            etseats.setText(vehicledata.getSeats());
            String vtype=vehicledata.getType();
            rbpetrol.setBackgroundColor(getResources().getColor(android.R.color.transparent));
            if(vtype.equalsIgnoreCase(getString(R.string.petrol))) {
                rbpetrol.setChecked(true);
                rbpetrol.setBackgroundColor(getResources().getColor(R.color.radioselected));
            }
            else if(vtype.equalsIgnoreCase(getString(R.string.diesel))) {
                rbdiesel.setChecked(true);
                rbdiesel.setBackgroundColor(getResources().getColor(R.color.radioselected));
            }
            else if(vtype.equalsIgnoreCase(getString(R.string.cng))) {
                rbcng.setChecked(true);
                rbcng.setBackgroundColor(getResources().getColor(R.color.radioselected));
            }
            String img=AppConst.imgurl+"vehicle/"+vehicledata.getImage();

            Picasso.with(VehicleRegistration.this)
                    .load(img)
                    .placeholder(R.drawable.offerride)
                    .error(R.drawable.offerride)
                    .into(ivvehicle);
        }else {
            updatelinear.setVisibility(View.GONE);
            insertlinear.setVisibility(View.VISIBLE);
        }
        rgtype.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                RadioButton radioButton = (RadioButton) group.findViewById(checkedId);
                radioButton.setBackgroundColor(getResources().getColor(R.color.radioselected));
                if(!rbpetrol.isChecked())
                    rbpetrol.setBackgroundColor(getResources().getColor(android.R.color.transparent));
                if(!rbdiesel.isChecked())
                    rbdiesel.setBackgroundColor(getResources().getColor(android.R.color.transparent));
                if(!rbcng.isChecked())
                    rbcng.setBackgroundColor(getResources().getColor(android.R.color.transparent));
            }
        });
        getCategories();

        etcompany.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                companyid = catidlist.get(position).toString();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                companyid = "0";
            }
        });

    }

    public void getCategories(){
        M.showLoadingDialog(VehicleRegistration.this);
        CategoryAPI mCommentsAPI = APIService.createService(CategoryAPI.class);
        mCommentsAPI.getCategory(new Callback<List<CategoryPojo>>() {

            @Override
            public void success(List<CategoryPojo> categoryPojos, Response response) {

                list.add("Select Car Company");
                catidlist.add("0");

                if (categoryPojos != null) {
                    M.L("car----"+categoryPojos.size());
                    catlist = categoryPojos;

                    for (CategoryPojo data : catlist) {
                        catidlist.add(data.getCategoryid());
                        String catnm=data.getCategorynm();
                        list.add(catnm);
                    }


                }

                ArrayAdapter<String> adapter = new ArrayAdapter<String>(VehicleRegistration.this,android.R.layout.simple_list_item_1, list);
                etcompany.setAdapter(adapter);

             //   etcompany.setSelection(adapter.getPosition(vehicledata.getCompany()));
                M.hideLoadingDialog();
            }

            @Override
            public void failure(RetrofitError error) {
                M.hideLoadingDialog();
                Log.e(TAG, error.getMessage());
            }

        });
    }



    @Override
    public void onClick(View v) {
        if(v.getId()==R.id.ivcar){

            mIntent = new Intent();
            mIntent.setType("image/*");
            mIntent.setAction(Intent.ACTION_GET_CONTENT);
            startActivityForResult(
                    Intent.createChooser(mIntent, getString(R.string.selpicture)),
                    RESULT_LOAD_IMAGE1);
        }
        else if(v.getId()==R.id.btnsubmit){


            if(companyid.equals("0"))
            {
                M.T(VehicleRegistration.this, "Select Car Company Please");

            }else {
                M.showLoadingDialog(this);
                getdata();
                TypedFile img = null;
                if (vehicleimg != null) {
                    img = new TypedFile("image/jpg", new File(vehicleimg));
                }
                Log.d(TAG, "insert:" + userid + " " + companyid + " " + model + " " + number + " " + seats + " " + type + " " + img);
                vehicleRegAPI mCommentsAPI = APIService.createService(vehicleRegAPI.class);
                mCommentsAPI.vehicleReg(userid, companyid, model, number, seats, type, img, new Callback<userRegPojo>() {
                            @Override
                            public void success(userRegPojo user, Response response) {
                                M.hideLoadingDialog();
                                if (user.getStatus().equals("success")) {
                                    Log.e(TAG, response.toString());
                                    Log.e(TAG, user.getStatus());
                                    Toast.makeText(VehicleRegistration.this, user.getMessage(), Toast.LENGTH_SHORT).show();
                                    Intent intent = new Intent(VehicleRegistration.this, MainActivity.class);
                                    startActivity(intent);
                                    finish();

                                }
                            }

                            @Override
                            public void failure(RetrofitError error) {
                                M.hideLoadingDialog();
                                System.out.println("error" + error.getMessage());
                            }
                        }

                );
            }

        }else if (v.getId()==R.id.btnupdate){


            if(companyid.equals("0"))
            {
                M.T(VehicleRegistration.this, "Select Car Company Please");

            }else {
                M.showLoadingDialog(this);
                getdata();

                TypedFile vehicleimage = null;
                Log.d(TAG, "img:" + vehicleimg);
                if (vehicleimg != null) {
                    vehicleimage = new TypedFile("image/jpg", new File(vehicleimg));
                }
                Log.d(TAG, "update:" + vehicleid + " " + companyid + " " + model + " " + number + " " + seats + " " + type + " " + vehicleimage);
                vehicleRegAPI mCommentsAPI = APIService.createService(vehicleRegAPI.class);
                mCommentsAPI.updateVehicle(vehicleid, companyid, model, number, seats, type, vehicleimage, new Callback<userRegPojo>() {
                            @Override
                            public void success(userRegPojo user, Response response) {
                                M.hideLoadingDialog();
                                if (user.getStatus().equals("success")) {
                                    Log.e(TAG, response.toString());
                                    Log.e(TAG, user.getStatus());
                                    Toast.makeText(VehicleRegistration.this, user.getMessage(), Toast.LENGTH_SHORT).show();
                                    Intent intent = new Intent(VehicleRegistration.this, MainActivity.class);
                                    startActivity(intent);
                                    finish();

                                }
                            }

                            @Override
                            public void failure(RetrofitError error) {
                                M.hideLoadingDialog();
                                System.out.println("error" + error.getMessage());
                            }
                        }

                );
            }
        }else if(v.getId()==R.id.btndelete){

            new SweetAlertDialog(VehicleRegistration.this, SweetAlertDialog.WARNING_TYPE)
                    .setTitleText("Delete?")
                    .setContentText("Do you want to delete this vehicle?")
                    .setCancelText("No")
                    .setConfirmText("Yes,delete it!")
                    .showCancelButton(true)
                    .setCancelClickListener(new SweetAlertDialog.OnSweetClickListener() {
                        @Override
                        public void onClick(SweetAlertDialog sDialog) {
                            sDialog.cancel();
                        }
                    })
                    .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                        @Override
                        public void onClick(final SweetAlertDialog sDialog) {

                           M.showLoadingDialog(VehicleRegistration.this);
                           vehicleRegAPI mCommentsAPI = APIService.createService(vehicleRegAPI.class);
                           mCommentsAPI.deletevehicle(vehicleid, new Callback<userRegPojo>() {
                           @Override
                           public void success(userRegPojo user, Response response) {

                                if (user.getStatus().equals("success")) {
                                  Toast.makeText(VehicleRegistration.this, user.getMessage(), Toast.LENGTH_SHORT).show();
                                    Intent it= new Intent(VehicleRegistration.this,MainActivity.class);
                                    finish();
                                    startActivity(it);
                                }
                                 M.hideLoadingDialog();
                                }
                                @Override
                                 public void failure(RetrofitError error) {
                                         M.hideLoadingDialog();
                                         System.out.println("error" + error.getMessage());
                                 }
                           }
                             );
                        }
                    })
                    .show();
       }
    }

    public  void getdata(){


        model=etmodel.getText().toString();
        number=etcarnum.getText().toString();
        seats=etseats.getText().toString();
        int selId=rgtype.getCheckedRadioButtonId();
        rbtype=(RadioButton)findViewById(selId);
        type=rbtype.getText().toString();
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == VehicleRegistration.this.RESULT_OK && requestCode == RESULT_LOAD_IMAGE1) {
            vehicleimg = FilePath.getPath(VehicleRegistration.this, data.getData());
            Picasso.with(VehicleRegistration.this)
                    .load(data.getData())
                    .placeholder(R.drawable.offerride1)
                    .error(R.drawable.offerride1)
                    .into(ivvehicle);
            if (ivvehicle.getVisibility() != View.VISIBLE) {
                ivvehicle.setVisibility(View.VISIBLE);
            }
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();

    }
}
