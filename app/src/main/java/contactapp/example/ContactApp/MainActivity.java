package contactapp.example.ContactApp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.SearchView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

public class MainActivity extends AppCompatActivity {

    //view
    private FloatingActionButton fab;
    private RecyclerView contactRv;

    //db
    private DbHelper dbHelper;

    //adapter
    private AdapterContact adapterContact;

    //action bar
    private ActionBar actionBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //init
        actionBar = getSupportActionBar();

        //init db
        dbHelper = new DbHelper(this);

        // initialization
        fab = findViewById(R.id.fab);
        contactRv = findViewById(R.id.contactRv);

        contactRv.setHasFixedSize(true);

        // add listener
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // pindah ke aktivitas baru untuk menambahkan kontak
                Intent intent = new Intent(MainActivity.this,AddEditContact.class);
                intent.putExtra("isEditMode",false);
                startActivity(intent);

            }
        });
        
        loadData();
    }

    private void loadData() {
        adapterContact = new AdapterContact(this,dbHelper.getAllData());
        contactRv.setAdapter(adapterContact);

    }

    @Override
    protected void onResume() {
        super.onResume();
        loadData(); // refresh data
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        getMenuInflater().inflate(R.menu.main_top,menu);

        //dapatkan item pencarian dari menu
        MenuItem item = menu.findItem(R.id.searchContact);
        //daerah pencarian
        SearchView searchView = (SearchView) item.getActionView();
        //tetapkan nilai maks untuk lebar
        searchView.setMaxWidth(Integer.MAX_VALUE);
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                searchContact(query);
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                searchContact( newText);
                return true;
            }
        });

        return true;
    }

    private void searchContact(String query){

        adapterContact = new AdapterContact(this,dbHelper.getSearchContact(query));
        contactRv.setAdapter(adapterContact);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {

        switch (item.getItemId()){
            case R.id.deleteAllContact:
                dbHelper.deleteAllContact();
                onResume();
                break;
        }

        return true;
    }
}