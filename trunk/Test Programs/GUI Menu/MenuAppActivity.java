package dac.pra;

import android.app.Activity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.Toast;

public class MenuAppActivity extends Activity {
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
    }
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu1, menu);
        return true;
    }
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.item1:    
            	Toast.makeText(this, "You pressed the Start!", Toast.LENGTH_LONG).show();
                                break;
            case R.id.item2:
            	Toast.makeText(this, "You pressed the Stop!", Toast.LENGTH_LONG).show();
            					break;
            	
            case R.id.item3:
            	Toast.makeText(this, "You pressed the Resolution!", Toast.LENGTH_LONG).show();
            					break;
        }
        return true;
    }
}