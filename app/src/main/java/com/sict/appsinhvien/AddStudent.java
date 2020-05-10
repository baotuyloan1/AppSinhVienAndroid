package com.sict.appsinhvien;

import android.app.DatePickerDialog;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.AbsListView;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import java.text.SimpleDateFormat;
import java.util.Calendar;

public class AddStudent extends AppCompatActivity {

    private EditText edtBirthDate, edtFullName, edtAddress, edtDescription, edtPhone;
    private RadioButton ra_Nam, ra_Nu;
    public DatabaseReference mData;
    public  int maxId = 1;
    private boolean gioiTinh;
    private Button btnSave, btnDelete;
    private int PICK_IMAGE_REQUEST = 111;
    private Uri imageUri;
    private ImageView imageView;
    private StorageReference storageRef;
    private ProgressBar progressBar;
    private String linkAnh;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_student);
        khoiTao();
        edtBirthDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                chooseDay();
            }
        });
        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                uploadFile();
            }
        });

        btnDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               deleteDataInput();


            }
        });

        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openFileChooser();
            }
        });

        assert getSupportActionBar() != null;   //null check
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);   //show back button
    }

    private void deleteDataInput() {
        edtBirthDate.setText("");
        edtPhone.setText("");
        edtAddress.setText("");
        edtDescription.setText("");
        ra_Nam.setChecked(true);
        ra_Nu.setChecked(false);
        edtFullName.setText("");

    }


    private void khoiTao() {
        edtBirthDate = (EditText) findViewById(R.id.editTextBirthDate);
        edtFullName = (EditText) findViewById(R.id.editTextFullName);
        edtAddress = (EditText) findViewById(R.id.editTextAddress);
        edtDescription = (EditText) findViewById(R.id.editTextMoTa);
        ra_Nam = (RadioButton) findViewById(R.id.radio_nam);
        ra_Nu = (RadioButton) findViewById(R.id.radio_nu);
        btnDelete = (Button) findViewById(R.id.buttonDeleteAll);
        mData = FirebaseDatabase.getInstance().getReference("SinhVien");
        storageRef = FirebaseStorage.getInstance().getReference("images/");
        edtPhone = (EditText) findViewById(R.id.editTextPhone);
        btnSave = (Button) findViewById(R.id.buttonSave);
        imageView = (ImageView) findViewById(R.id.imageView);
        progressBar = (ProgressBar) findViewById(R.id.progressBar);

        autoIncrement();

    }

    //chọn ngày sinh
    private void chooseDay() {
        final Calendar calendar = Calendar.getInstance();
        DatePickerDialog datePickerDialog = new DatePickerDialog(this, new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                calendar.set(year, month, dayOfMonth);
                SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd/MM/yyyy");
                edtBirthDate.setText(simpleDateFormat.format(calendar.getTime()));
            }
        }, 2000, 02, 01);
        datePickerDialog.show();
    }

    //thêm sinh viên
    private void addStudent() {
            if (ra_Nam.isChecked()) gioiTinh = true;
            else gioiTinh = false;
            String idCode = mData.push().getKey();
            SinhVien sinhVien = new SinhVien(idCode, "18IT" + maxId, edtFullName.getText().toString(), gioiTinh, edtBirthDate.getText().toString(), edtPhone.getText().toString(), edtAddress.getText().toString(), edtDescription.getText().toString(), linkAnh);
            mData.child(idCode).setValue(sinhVien);
        Intent intent = new Intent(AddStudent.this, MainActivity.class);
        startActivity(intent);
        finish();
    }

    //lấy mã id sinh viên tự tăng
    private void autoIncrement() {
        mData.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists())
                for (DataSnapshot sinhVienSnapShot : dataSnapshot.getChildren()) {
                    SinhVien sinhVien = sinhVienSnapShot.getValue(SinhVien.class);
                    int idSinhVien = Integer.parseInt(sinhVien.getSinhVienID().substring(4));
                    if (maxId <= idSinhVien){
                        maxId = idSinhVien+1;
                    }
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        });
    }

    //mở chọn ảnh upload
    private void openFileChooser() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(intent, PICK_IMAGE_REQUEST);
    }

    //load ảnh lên imageView
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null) {
            imageUri = data.getData();
            Picasso.with(this).load(imageUri).into(imageView);
        }
    }

    //đẩy ảnh lên storage
    private void uploadFile() {
        if (imageUri != null) {
            if (!edtFullName.getText().toString().equals("") && !edtDescription.getText().toString().equals("") && !edtAddress.getText().toString().equals("") && !edtBirthDate.getText().toString().equals("")) {
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
                        Toast.makeText(AddStudent.this, "Up ảnh thành công", Toast.LENGTH_SHORT).show();
                        addStudent();
                    } else {
                        Toast.makeText(AddStudent.this, "Up ảnh thất bại" + task.getException(), Toast.LENGTH_SHORT).show();
                    }
                }
            });

            storageRef.putFile(imageUri).addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {
                    double progress = (100.0 * taskSnapshot.getBytesTransferred()) / taskSnapshot.getTotalByteCount();
                    progressBar.setProgress((int) progress);
                }
            });
            }else{
                Toast.makeText(this, "Vui lòng điền đầy đủ thông tin", Toast.LENGTH_SHORT).show();
            }
        } else {
            Toast.makeText(this, "Không có ảnh được chọn", Toast.LENGTH_SHORT).show();
        }
    }

    //đuôi ảnh
    private String getFileExtension(Uri uri) {
        ContentResolver cR = getContentResolver();
        MimeTypeMap mime = MimeTypeMap.getSingleton();
        return mime.getExtensionFromMimeType(cR.getType(uri));
    }



}
