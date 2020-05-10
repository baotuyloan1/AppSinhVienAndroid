package com.sict.appsinhvien;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.ContextMenu;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.view.ActionMode;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;


public class MainFragment extends Fragment {
    private ArrayList<SinhVien> arraySinhVien;
    private ListView lwSinhVien;
    private TextView txtID, txtFullName, txtSex, txtBirthDay, txtPhone, txtAddress, txtdDescription;
    private ArrayList<View> arrayView;
    private onFragmentBtnSelected listener;
    private View view;
    public SinhVienAdapter adapter;
    public ArrayList<SinhVien> sinhVienDelete;
    private boolean multiSelect = false;
    private int countSelecter = 0;
    private ActionMode actionMode;
    private int positionListView;
    public DatabaseReference mData;
    private static final String TAG = "Fragment Main";

    private DrawerLayout drawerLayout;
    private ActionBarDrawerToggle actionBarDrawerToggle;
    private Toolbar toolbar;
    private NavigationView navigationView;
    private FragmentManager fragmentManager;
    private FragmentTransaction fragmentTransaction;


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_main, container, false);
        khoiTao();
        final MainActivity activity = (MainActivity) getActivity();
        registerForContextMenu(lwSinhVien);
        lwSinhVien.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (multiSelect) {
                    SinhVien sinhVien = arraySinhVien.get(position);
                    selectItem(sinhVien, view);
                } else {
                    positionListView = position;
                    activity.openContextMenu(lwSinhVien);
                }
            }
        });
        lwSinhVien.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                multiSelect = true;
                actionMode = activity.startSupportActionMode(callback);
                SinhVien sinhVien = arraySinhVien.get(position);
                sinhVien.setChecked(true);
                selectItem(sinhVien, view);
                return true;
            }
        });
        return view;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    private void khoiTao() {
        mData = FirebaseDatabase.getInstance().getReference("SinhVien");
        arraySinhVien = new ArrayList<SinhVien>();
        adapter = new SinhVienAdapter(getActivity(), R.layout.dong_sinh_vien, arraySinhVien);
        mData.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                arraySinhVien.clear();
                for (DataSnapshot sinhVienSnapShot : dataSnapshot.getChildren()) {
                    SinhVien sinhVien = sinhVienSnapShot.getValue(SinhVien.class);
                    sinhVien.setChecked(false);
                    arraySinhVien.add(sinhVien);
                }
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        });
        txtID = (TextView) view.findViewById(R.id.textViewID);
        txtFullName = (TextView) view.findViewById(R.id.textViewTen);
        txtSex = (TextView) view.findViewById(R.id.textViewSex);
        txtBirthDay = (TextView) view.findViewById(R.id.textViewBirthday);
        txtPhone = (TextView) view.findViewById(R.id.textViewSDT);
        txtAddress = (TextView) view.findViewById(R.id.textViewAddress);
        txtdDescription = (TextView) view.findViewById(R.id.textViewDescription);
        lwSinhVien = (ListView) view.findViewById(R.id.listViewSinhVien);
        arrayView = new ArrayList<View>();
        sinhVienDelete = new ArrayList<SinhVien>();
        lwSinhVien.setAdapter(adapter);
    }


    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        if (context instanceof onFragmentBtnSelected) {
            listener = (onFragmentBtnSelected) context;
        } else {
            throw new ClassCastException(context.toString());
        }
    }


    public interface onFragmentBtnSelected {
        public void onButtonSelected(int id);
    }

    void selectItem(SinhVien sinhVien, View view) {
        if (multiSelect) {
            if (sinhVienDelete.contains(sinhVien)) {
                sinhVien.setChecked(false);
                sinhVienDelete.remove(sinhVien);
                view.setBackgroundColor(Color.WHITE);
                countSelecter--;
                arrayView.remove(view);
            } else {
                sinhVienDelete.add(sinhVien);
                sinhVien.setChecked(true);
                view.setBackgroundColor(Color.LTGRAY);
                arrayView.add(view);
                countSelecter++;
            }
        }
    }

    private ActionMode.Callback callback = new ActionMode.Callback() {
        @Override
        public boolean onCreateActionMode(ActionMode mode, Menu menu) {
            mode.getMenuInflater().inflate(R.menu.contextual_action_mode, menu);
            multiSelect = true;
            return true;
        }

        @Override
        public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
            return false;
        }

        @Override
        public boolean onActionItemClicked(ActionMode mode, MenuItem item) {
            switch (item.getItemId()) {
                case R.id.deleteSinhVien:
                    if (countSelecter > 0) {
                        askDelete(sinhVienDelete);
                    }
                    return true;
            }
            sinhVienDelete.clear();
            return true;
        }

        @Override
        public void onDestroyActionMode(ActionMode mode) {
            multiSelect = false;
            if (arrayView.size() > 0) {
                for (int i = 0; i < arrayView.size(); i++) {
                    arrayView.get(i).setBackgroundColor(Color.WHITE);
                }
                arrayView.clear();
            }
            for (int i = 0; i < arraySinhVien.size(); i++) {
                SinhVien sinhVien = arraySinhVien.get(i);
                sinhVien.setChecked(false);
            }
            sinhVienDelete.clear();
        }
    };


    private void askDelete(final ArrayList<SinhVien> sinhViens) {
        if (sinhViens.size() > 0) {
            DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    switch (which) {
                        case DialogInterface.BUTTON_POSITIVE:
                            deleteSinhVien(sinhViens);
                            countSelecter = 0;
                            sinhVienDelete.clear();
                            actionMode.finish();
                            break;
                        case DialogInterface.BUTTON_NEGATIVE:
                            actionMode.finish();
                            break;
                    }
                }

            };
            AlertDialog.Builder ab = new AlertDialog.Builder(getActivity());
            String deleteSinhVienListString = "\n";
            int y = 0;
            for (int i = 0; i < sinhViens.size(); i++) {
                SinhVien sinhVien = sinhViens.get(i);
                y++;
                deleteSinhVienListString = deleteSinhVienListString + y + " - " + sinhVien.getFullName() + "\n";
            }
            ab.setMessage("Bạn có muốn xóa sinh viên đã chọn " + deleteSinhVienListString).setPositiveButton("Đồng ý", dialogClickListener)
                    .setNegativeButton("Quay lại", dialogClickListener).show();
        }
    }

    private void deleteSinhVien(ArrayList<SinhVien> sinhVien) {
        if (sinhVien.size() > 0) {
            for (int i = 0; i < sinhVien.size(); i++) {
                SinhVien sinhVienDel = sinhVien.get(i);
                mData.child(sinhVienDel.getIdCode()).removeValue();
            }
            Toast.makeText(getActivity(), "Xóa thành công", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(getActivity(), "Danh sách rỗng", Toast.LENGTH_SHORT).show();
        }

    }


    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);
        getActivity().getMenuInflater().inflate(R.menu.floating_context_menu, menu);
        SinhVien sinhVien = arraySinhVien.get(positionListView);
        menu.setHeaderTitle(sinhVien.getFullName());
    }


    @Override
    public boolean onContextItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.floating_edit_sinhvien:
                SinhVien sinhVienEdit = arraySinhVien.get(positionListView);
                Intent intent = new Intent(getActivity(), UpdateSinhVien.class);
                intent.putExtra("sinhVienEdit", sinhVienEdit);
                startActivity(intent);
                return true;
            case R.id.floating_capnhat_diem_sinhvien:
                return true;
            default:
                return super.onContextItemSelected(item);
        }
    }
}
