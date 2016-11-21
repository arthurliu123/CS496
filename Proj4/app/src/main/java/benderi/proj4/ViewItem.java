package benderi.proj4;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

public class ViewItem extends AppCompatActivity {
    private TextView mName;
    private TextView mDesc;
    private TextView mPrice;
    private TextView mSize;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_item);

        Bundle b = getIntent().getExtras();
        mName = (TextView) findViewById(R.id.txt_items);
        mDesc = (TextView) findViewById(R.id.txt_item_cost);
        mPrice = (TextView) findViewById(R.id.txt_quantity);
        mSize = (TextView) findViewById(R.id.txt_total_cost);

        mName.setText("Memo Title: " + b.getString("name"));
        mDesc.setText("Description: " + b.getString("description"));
        mPrice.setText("Importance: " + b.getString("importance"));
        mSize.setText("Finish Status: " + b.getString("finish"));

    }

    public void goBack(View view) {
        finish();

    }
}
