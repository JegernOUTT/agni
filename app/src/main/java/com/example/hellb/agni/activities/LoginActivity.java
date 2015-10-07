package com.example.hellb.agni.activities;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;

import com.example.hellb.agni.R;
import com.example.hellb.agni.serializible.SerializableScheduleData;
import com.example.hellb.agni.serializible.scheduleData.Course;
import com.example.hellb.agni.serializible.scheduleData.Faculty;

public class LoginActivity extends AppCompatActivity implements AdapterView.OnItemSelectedListener, Runnable,
        NavigationView.OnNavigationItemSelectedListener {

    private volatile boolean isGroupsReady;
    private Spinner spFaculty, spGroup, spCourse;
    private ArrayAdapter<String> arrayAdapterFac, arrayAdapterGr;
    private EditText editText;
    private SerializableScheduleData serializableScheduleData;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        NavigationCreate();

        spFaculty = (Spinner) findViewById(R.id.spFaculty);
        spGroup = (Spinner) findViewById(R.id.spGroup);
        spCourse = (Spinner) findViewById(R.id.spCourse);

        spFaculty.setEnabled(false);
        spGroup.setEnabled(false);
        spCourse.setEnabled(false);

        spFaculty.setOnItemSelectedListener(this);
        spCourse.setOnItemSelectedListener(this);
        spGroup.setOnItemSelectedListener(this);

        serializableScheduleData = SerializableScheduleData.getInstance(getApplicationContext());

        new Thread(this).start();
    }

    private void NavigationCreate() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar_login);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
    }

    private void loadSpinnerFaculty() {
        String[] arr = serializableScheduleData.getStringRepresentationFaculties();

        arrayAdapterFac = new ArrayAdapter<String>(getApplicationContext(),
                android.R.layout.simple_spinner_item, arr);
        spFaculty.setAdapter(arrayAdapterFac);
        spFaculty.setPrompt("Выберите факультет...");

        spFaculty.setEnabled(true);
    }

    private void loadSpinnerCourse(Faculty faculty) {
        String[] arr =  faculty.getStringRepresentationCourses();

        arrayAdapterFac = new ArrayAdapter<String>(getApplicationContext(),
                android.R.layout.simple_spinner_dropdown_item, arr);
        spCourse.setAdapter(arrayAdapterFac);
        spCourse.setPrompt("Выберите курс...");

        spCourse.setEnabled(true);
    }

    private void loadSpinnerGroup(Course course) {
        String[] arr = course.getStringRepresentationGroups();
        arrayAdapterFac = new ArrayAdapter<String>(getApplicationContext(),
                android.R.layout.simple_spinner_item, arr);
        spGroup.setAdapter(arrayAdapterFac);
        spGroup.setPrompt("Выберите группу...");

        spGroup.setEnabled(true);
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        switch (parent.getId())
        {
            case R.id.spFaculty:
                for (Faculty faculty: serializableScheduleData.getFaculties())
                {
                    if (faculty.toString().equals(spFaculty.getSelectedItem()))
                    {
                        loadSpinnerCourse(faculty);
                    }
                }

                break;
            case R.id.spCourse:
                for (Faculty faculty: serializableScheduleData.getFaculties())
                {
                    if (faculty.toString().equals(spFaculty.getSelectedItem()))
                    {
                        for (Course course: faculty.getCourses())
                        {
                            if (course.toString().equals(spCourse.getSelectedItem()))
                            {
                                loadSpinnerGroup(course);
                            }
                        }
                    }
                }

                break;
        }
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {
        switch (parent.getId()) {
            case R.id.spFaculty:
                spCourse.setAdapter(new ArrayAdapter<String>(getApplicationContext(),
                        android.R.layout.simple_spinner_dropdown_item, new String[]{}));
                break;

            case R.id.spCourse:
                spGroup.setAdapter(new ArrayAdapter<String>(getApplicationContext(),
                        android.R.layout.simple_spinner_dropdown_item, new String[]{}));
                break;
        }
    }

    @Override
    public void run() {
        boolean isLoadedFaculty = false;
        while (true)
        {
            if (serializableScheduleData.isRegisterParamReady && !isLoadedFaculty)
            {
                loadSpinnerFaculty();
                isLoadedFaculty = true;
            }

            boolean isGroupReady = true;
            for(Faculty faculty: serializableScheduleData.getFaculties())
            {
                if (! faculty.isLoaded)
                    isGroupReady = false;
            }

            if (isGroupReady)
            {
                isGroupsReady = true;
                break;
            }

            try {
                Thread.sleep(500);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onNavigationItemSelected(MenuItem menuItem) {
        int id = menuItem.getItemId();

        if (id == R.id.nav_enter) {

        }
        else if (id == R.id.nav_schedule) {

        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }
}
