package com.example.android.androidskeletonapp.ui.programs;

import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

import com.example.android.androidskeletonapp.R;
import com.example.android.androidskeletonapp.data.D2Factory;

import org.hisp.dhis.android.core.program.Program;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.lifecycle.LiveData;
import androidx.paging.PagedList;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import static com.example.android.androidskeletonapp.data.service.LogOutService.logOut;

public class ProgramsActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_programs);
        Toolbar toolbar = findViewById(R.id.programs_toolbar);
        setSupportActionBar(toolbar);


        RecyclerView programsRecyclerView = findViewById(R.id.programs_recycler_view);
        programsRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        programsRecyclerView.setHasFixedSize(true);

        ProgramsAdapter adapter = new ProgramsAdapter();
        programsRecyclerView.setAdapter(adapter);

        LiveData<PagedList<Program>> programs =
                D2Factory.getD2(this).programModule().programs
                        .withStyle()
                        .withProgramStages()
                        .getPaged(20);

        programs.observe(this, adapter::setPrograms);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.logout_item) {
            logOut(this);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
