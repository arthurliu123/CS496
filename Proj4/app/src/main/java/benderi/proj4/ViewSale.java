package benderi.proj4;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

public class ViewSale extends AppCompatActivity {
    private TextView mItems;
    private TextView mItem_cost;
    private TextView mQuantity;
    private TextView mTotal_cost;
    private TextView mLocation;
    private TextView mDatetime;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_sale);

        Bundle b = getIntent().getExtras();
        mItems= (TextView) findViewById(R.id.txt_items);
        mItem_cost = (TextView) findViewById(R.id.txt_item_cost);
        mQuantity = (TextView) findViewById(R.id.txt_quantity);
        mTotal_cost = (TextView) findViewById(R.id.txt_total_cost);
        mLocation = (TextView) findViewById(R.id.txt_location);
        mDatetime = (TextView) findViewById(R.id.txt_datetime);

        mItems.setText("Item Key: " + b.getString("items"));
        mItem_cost.setText("Price: " + b.getString("item_cost"));
        mQuantity.setText("Quantity: " + b.getString("quantity"));
        mTotal_cost.setText("Total Sale: " + b.getString("total_cost"));
        mLocation.setText("Sale Location: " + b.getString("latitude") + ", " + b.getString("longitude"));
        mDatetime.setText("Sale Date/Time: " + b.getString("datetime"));

    }

    public void goBack(View view) {
        finish();

    }
}
