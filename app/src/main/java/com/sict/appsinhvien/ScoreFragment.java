package com.sict.appsinhvien;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;


import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.google.android.material.navigation.NavigationView;

public class ScoreFragment extends Fragment implements NavigationView.OnNavigationItemSelectedListener {


    private DrawerLayout drawerLayout;
    private ActionBarDrawerToggle actionBarDrawerToggle;
    private Toolbar toolbar;
    private NavigationView navigationView;
    private FragmentManager fragmentManager;
    private FragmentTransaction fragmentTransaction;



    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        drawerLayout = getActivity().findViewById(R.id.drawer_layout);
        toolbar = getActivity().findViewById(R.id.toolbar);
        navigationView = getActivity().findViewById(R.id.navigationView);

        navigationView.setNavigationItemSelectedListener(this);

        actionBarDrawerToggle = new ActionBarDrawerToggle(getActivity(), drawerLayout, toolbar, R.string.open, R.string.close);
        drawerLayout.addDrawerListener(actionBarDrawerToggle);
        actionBarDrawerToggle.setDrawerIndicatorEnabled(true);
        actionBarDrawerToggle.syncState();

        return super.onCreateView(inflater, container, savedInstanceState);
    }

    private onFragmentBtnSelected listener;

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        if (context instanceof onFragmentBtnSelected) {
            listener = (onFragmentBtnSelected) context;
        } else {
            throw new ClassCastException(context.toString());
        }
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == R.id.nav_addStudent) {
            Intent intent = new Intent(getActivity(), AddStudent.class);
            startActivity(intent);
        }

        if (item.getItemId() == R.id.nav_logOut) {
            Toast.makeText(getActivity(), "Đăng xuất", Toast.LENGTH_SHORT).show();
        }

        if (item.getItemId() == R.id.nav_editProfile) {
            Toast.makeText(getActivity(), "Cập nhật hồ sơ", Toast.LENGTH_SHORT).show();
        }

        if (item.getItemId() == R.id.nav_profile) {
            Toast.makeText(getActivity(), "Send", Toast.LENGTH_SHORT).show();
        }
        return true;
    }

    public interface onFragmentBtnSelected {
        public void onButtonSelected(int id);
    }
}
