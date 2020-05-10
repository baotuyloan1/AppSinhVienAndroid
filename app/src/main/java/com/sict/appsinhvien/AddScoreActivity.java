package com.sict.appsinhvien;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class AddScoreActivity extends AppCompatActivity {

    private Button btnNhapDiem;
    private EditText edtDiem;
    private Spinner spinnerMonHoc;
    private TextView txtTenSinhVien, txtMaSinhVien;
    private DatabaseReference databaseMonHoc;

    private ArrayList<Scores> scoresArrayList;
    private ListView listViewMonHoc;
    private ScoresAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_score);


        khoiTao();
        btnNhapDiem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                nhapDiem();
            }
        });
    }

    private void nhapDiem() {
        Double scores = Double.parseDouble(edtDiem.getText().toString());
        String monHoc = spinnerMonHoc.getSelectedItem().toString();
        if (scores >= 0) {

            String idCodeMonHoc = databaseMonHoc.push().getKey();

            Scores diem = new Scores(idCodeMonHoc, scores, monHoc);
            databaseMonHoc.child(idCodeMonHoc).setValue(diem);

            Toast.makeText(this, "Nhập điểm thành công", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(this, "Chưa nhập điểm môn học", Toast.LENGTH_SHORT).show();
        }

    }

    // nhận dữ liệu sinh viên từ mainAcitivity
    private void khoiTao() {

        spinnerMonHoc = (Spinner) findViewById(R.id.spinnerMonHoc);
        btnNhapDiem = (Button) findViewById(R.id.buttonNhapDiem);
        edtDiem = (EditText) findViewById(R.id.editTextScores);
        txtTenSinhVien = (TextView) findViewById(R.id.textViewTenSinhvien);
        listViewMonHoc = (ListView) findViewById(R.id.listViewMonHoc);
        txtMaSinhVien = (TextView)findViewById(R.id.textViewMaSinhvien);
        Intent intent = getIntent();

        String idCodeSinhVien = intent.getStringExtra(MainActivity.ID_CODE_SINHVIEN);
        String tenSinhVien = intent.getStringExtra(MainActivity.TEN_SINHVIEN);
        String idSinhVien = intent.getStringExtra(MainActivity.ID_SINHVIEN);
        txtTenSinhVien.setText(tenSinhVien);
        txtMaSinhVien.setText(idSinhVien);
        databaseMonHoc = FirebaseDatabase.getInstance().getReference("MonHoc").child(idCodeSinhVien);

        scoresArrayList = new ArrayList<Scores>();
        adapter = new ScoresAdapter(AddScoreActivity.this, R.layout.dong_scores, scoresArrayList);
        listViewMonHoc.setAdapter(adapter);

    }


    @Override
    protected void onStart() {
        super.onStart();
        databaseMonHoc.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                scoresArrayList.clear();
                for (DataSnapshot scoresSnapshot : dataSnapshot.getChildren()){
                    Scores scores = scoresSnapshot.getValue(Scores.class);
                    scoresArrayList.add(scores);
                }
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }
}
