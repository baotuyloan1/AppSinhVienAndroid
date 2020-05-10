package com.sict.appsinhvien;

import android.app.DatePickerDialog;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import java.text.SimpleDateFormat;
import java.util.Calendar;

import de.hdodenhof.circleimageview.CircleImageView;

public class UpdateSinhVien extends AppCompatActivity {

    private EditText editTextFullNameEdit, editTextAddress, editTextPhone, editTextDescription, editTextBirthDay;
    private RadioButton ra_Nam, ra_Nu;
    private ImageView imageViewEdit;
    private Button buttonCapNhat, buttonDefault;
    private Uri imageUri;
    private int PICK_IMAGE_REQUEST = 2;
    private StorageReference storageRef;
    public DatabaseReference mData;
    private String linkAnh;
    private ProgressBar progressBarEdit;
    private boolean gioiTinh;
    private SinhVien sinhVien;

    private CircleImageView receiverProfileImage;
    private TextView receiverName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update_sinh_vien);
        khoiTao();
        getDataSinhVien();
        buttonDefault.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
              getDataSinhVien();
            }
        });
        imageViewEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openFileChooser();
            }
        });
        buttonCapNhat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                updateSinhVien();
            }
        });

    }

    private void khoiTao() {
        editTextFullNameEdit = (EditText) findViewById(R.id.editTextFullNameEdit);
        editTextAddress = (EditText) findViewById(R.id.editTextAddressEdit);
        editTextPhone = (EditText) findViewById(R.id.editTextPhoneEdit);
        editTextDescription = (EditText) findViewById(R.id.editTextMoTaEdit);
        editTextBirthDay = (EditText) findViewById(R.id.editTextBirthDateEdit);
        ra_Nam = (RadioButton) findViewById(R.id.radio_nam_edit);
        ra_Nu = (RadioButton) findViewById(R.id.radio_nu_edit);
        buttonCapNhat = (Button) findViewById(R.id.buttonEdit);
        buttonDefault = (Button) findViewById(R.id.buttonDefault);
        imageViewEdit = (ImageView) findViewById(R.id.imageViewEdit);
        mData = FirebaseDatabase.getInstance().getReference("SinhVien");
        storageRef = FirebaseStorage.getInstance().getReference("images");
        progressBarEdit = (ProgressBar) findViewById(R.id.progressBarEdit);

        ActionBar actionBar =  getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setDisplayShowCustomEnabled(true);
        LayoutInflater layoutInflater = (LayoutInflater) this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View action_bar_view = layoutInflater.inflate(R.layout.edit_custom_bar, null);
        actionBar.setCustomView(action_bar_view);
        receiverName = (TextView) findViewById(R.id.custom_profile_name);
        receiverProfileImage = (CircleImageView) findViewById(R.id.custom_profile_image);


    }

    private void openFileChooser() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(intent, PICK_IMAGE_REQUEST);
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null) {
            imageUri = data.getData();
            Picasso.with(this).load(imageUri).into(imageViewEdit);
        }
    }


    private void updateSinhVien() {
        if (imageUri != null) {
            final StorageReference fileReference = storageRef.child("images/" + System.currentTimeMillis() + "." + getFileExtension(imageUri));
            fileReference.putFile(imageUri).continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
                @Override
                public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception {
                    if (!task.isSuccessful()) {
                        throw task.getException();
                    }
                    return fileReference.getDownloadUrl();
                }
            }).addOnCompleteListener(new OnCompleteListener<Uri>() {
                @Override
                public void onComplete(@NonNull Task<Uri> task) {
                    if (task.isSuccessful()) {
                        Uri downloadUri = task.getResult();
                        linkAnh = downloadUri.toString();
                        Toast.makeText(UpdateSinhVien.this, "Up ảnh thành công", Toast.LENGTH_SHORT).show();
                        updateInfo();
                    } else {
                        Toast.makeText(UpdateSinhVien.this, "Up ảnh thất bại" + task.getException(), Toast.LENGTH_SHORT).show();
                    }
                }
            });
            storageRef.putFile(imageUri).addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {
                    double progress = (100.0 * taskSnapshot.getBytesTransferred()) / taskSnapshot.getTotalByteCount();
                    progressBarEdit.setProgress((int) progress);
                }
            });
        } else {
            linkAnh = null;
            updateInfo();
        }
    }

    private void updateInfo() {
        if (!editTextFullNameEdit.equals("") && !editTextDescription.equals("") && !editTextAddress.equals("") && !editTextBirthDay.equals("")) {
            if (ra_Nam.isChecked()) gioiTinh = true;
            else gioiTinh = false;
            String idCode = sinhVien.getIdCode();
            SinhVien sinhvienOld = sinhVien;
            if (linkAnh != null) {
                SinhVien sinhVienUpdate = new SinhVien(idCode, sinhvienOld.getSinhVienID(), editTextFullNameEdit.getText().toString(), gioiTinh, editTextBirthDay.getText().toString(), editTextPhone.getText().toString(), editTextAddress.getText().toString(), editTextDescription.getText().toString(), linkAnh);
                mData.child(idCode).setValue(sinhVienUpdate);
            } else {
                SinhVien sinhVienUpdate = new SinhVien(idCode, sinhvienOld.getSinhVienID(), editTextFullNameEdit.getText().toString(), gioiTinh, editTextBirthDay.getText().toString(), editTextPhone.getText().toString(), editTextAddress.getText().toString(), editTextDescription.getText().toString(), sinhvienOld.getImageUrl());
                mData.child(idCode).setValue(sinhVienUpdate);

            }
            Toast.makeText(UpdateSinhVien.this, "Lưu sinh viên thành công ", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(this, "Vui lòng điền đầy đủ thông tin", Toast.LENGTH_SHORT).show();
        }
        Intent intent = new Intent(UpdateSinhVien.this, MainActivity.class);
        startActivity(intent);
        finish();
    }

    private void getDataSinhVien() {
        sinhVien = (SinhVien) getIntent().getSerializableExtra("sinhVienEdit");
        editTextFullNameEdit.setText(sinhVien.getFullName());
        editTextBirthDay.setText(sinhVien.getBirthDay());
        editTextDescription.setText(sinhVien.getDescription());
        editTextAddress.setText(sinhVien.getAddress());
        editTextPhone.setText(sinhVien.getPhone());
        if (sinhVien.isSex()) {
            ra_Nam.setChecked(true);
        } else {
            ra_Nu.setChecked(true);
        }
        final AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(this);
        editTextBirthDay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                chooseDay();
            }
        });
        Picasso.with(this).load(sinhVien.getImageUrl()).into(imageViewEdit);
        receiverName.setText(sinhVien.getFullName());
        Picasso.with(this).load(sinhVien.getImageUrl()).into(receiverProfileImage);

    }

    //chọn ngày sinh
    private void chooseDay() {
        final Calendar calendar = Calendar.getInstance();
        DatePickerDialog datePickerDialog = new DatePickerDialog(this, new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                calendar.set(year, month, dayOfMonth);
                SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd/MM/yyyy");
                editTextBirthDay.setText(simpleDateFormat.format(calendar.getTime()));
            }
        }, 2000, 02, 01);
        datePickerDialog.show();
    }


    //đuôi ảnh
    private String getFileExtension(Uri uri) {
        ContentResolver cR = getContentResolver();
        MimeTypeMap mime = MimeTypeMap.getSingleton();
        return mime.getExtensionFromMimeType(cR.getType(uri));
    }



}

