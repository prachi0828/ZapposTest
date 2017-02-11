package com.search.material.materialsearch;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.speech.RecognizerIntent;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.TextView;
import android.support.design.widget.FloatingActionButton;
import android.widget.Toast;


import com.search.material.library.MaterialSearchView;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private MaterialSearchView searchView;
    FloatingActionButton fab;
    Animation show_fab_1;
    Animation hide_fab_1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        fab = (FloatingActionButton)findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {


                show_fab_1 = AnimationUtils.loadAnimation(getApplication(), R.anim.fab1_show);
                hide_fab_1 = AnimationUtils.loadAnimation(getApplication(), R.anim.fab1_hide);
                fab.startAnimation(show_fab_1);
                // Snackbar.make(view, "Action", Snackbar.LENGTH_LONG).setAction("Action", null).show();
            }
        });

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        searchView = (MaterialSearchView) findViewById(R.id.search_view);
        searchView.setOnQueryTextListener(new MaterialSearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                return false;
            }
        });

        searchView.setOnSearchViewListener(new MaterialSearchView.SearchViewListener() {
            @Override
            public void onSearchViewShown() {

            }

            @Override
            public void onSearchViewClosed() {

            }
        });

        searchView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Toast.makeText(MainActivity.this, (String)parent.getItemAtPosition(position), Toast.LENGTH_SHORT).show();
            }
        });

        SearchAdapter adapter = new SearchAdapter();
        searchView.setAdapter(adapter);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);

        MenuItem item = menu.findItem(R.id.action_search);
        searchView.setMenuItem(item);

        return true;
    }

    @Override
    public void onBackPressed() {
        if (searchView.isSearchOpen()) {
            searchView.closeSearch();
        } else {
            super.onBackPressed();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == MaterialSearchView.REQUEST_VOICE && resultCode == RESULT_OK) {
            ArrayList<String> matches = data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
            if (matches != null && matches.size() > 0) {
                String searchWrd = matches.get(0);
                if (!TextUtils.isEmpty(searchWrd)) {
                    searchView.setQuery(searchWrd, false);
                }
            }

            return;
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    public void parsingJSONResponse(String jsonString) {
        final Dialog dialog = new Dialog(this);
        dialog.setContentView(R.layout.shoes);
        dialog.setTitle(getString(R.string.notice));
        dialog.setCancelable(true);

        TextView brandName=(TextView) dialog.findViewById(R.id.brandName);
        TextView thumbnailImageUrl=(TextView) dialog.findViewById(R.id.thumbnailImageUrl);
        TextView productId=(TextView) dialog.findViewById(R.id.productId);
        TextView originalPrice=(TextView) dialog.findViewById(R.id.originalPrice);
        TextView styleId=(TextView) dialog.findViewById(R.id.styleId);
        TextView colorId=(TextView) dialog.findViewById(R.id.colorId);
        TextView price=(TextView) dialog.findViewById(R.id.price);
        TextView percentOff=(TextView) dialog.findViewById(R.id.percentOff);
        TextView productUrl=(TextView) dialog.findViewById(R.id.productUrl);
        TextView productName=(TextView) dialog.findViewById(R.id.productName    );

        // parsing json and set the custom dialog components - text, image and button
        try {
            JSONObject jsonObject = new JSONObject(jsonString);

            //get temperature, humidity and pressure
            JSONObject tempObject = jsonObject.getJSONObject("main");
            brandName.setText(tempObject.getString("brandName: "));
            thumbnailImageUrl.setText(tempObject.getString("thumbnailImageUrl"));
            productId.setText(tempObject.getString("productId: "));
            originalPrice.setText(tempObject.getString("originalPrice: "));
            styleId.setText(tempObject.getString("styleId: "));
            colorId.setText(tempObject.getString("colorId: "));
            price.setText(tempObject.getString("price: "));
            percentOff.setText(tempObject.getString("percentOff: "));
            productUrl.setText(tempObject.getString("productUrl: "));
            productName.setText(tempObject.getString("productName: "));

            //get city name
        } catch (JSONException e) {
            e.printStackTrace();
        }

        //showing dialog
        dialog.show();
    }


    private class SearchAdapter extends BaseAdapter implements Filterable {

        private ArrayList<String> data;

        private String[] typeAheadData;

        LayoutInflater inflater;

        public SearchAdapter() {
            inflater = LayoutInflater.from(MainActivity.this);
            data = new ArrayList<String>();
            typeAheadData = getResources().getStringArray(R.array.state_array_full);
        }


        @Override
        public Filter getFilter() {
            Filter filter = new Filter() {
                @Override
                protected FilterResults performFiltering(CharSequence constraint) {
                    FilterResults filterResults = new FilterResults();
                    if (!TextUtils.isEmpty(constraint)) {
                        // Retrieve the autocomplete results.
                        List<String> searchData = new ArrayList<>();

                        for (String str : typeAheadData) {
                            if (str.toLowerCase().startsWith(constraint.toString().toLowerCase())) {
                                searchData.add(str);
                            }
                        }

                        // Assign the data to the FilterResults
                        filterResults.values = searchData;
                        filterResults.count = searchData.size();
                    }
                    return filterResults;
                }

                @Override
                protected void publishResults(CharSequence constraint, FilterResults results) {
                    if (results.values != null) {
                        data = (ArrayList<String>) results.values;
                        notifyDataSetChanged();
                    }
                }
            };
            return filter;
        }

        @Override
        public int getCount() {
            return data.size();
        }

        @Override
        public Object getItem(int position) {
            return data.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            MyViewHolder mViewHolder;

            if (convertView == null) {
                convertView = inflater.inflate(android.R.layout.simple_list_item_1, parent, false);
                mViewHolder = new MyViewHolder(convertView);
                convertView.setTag(mViewHolder);
            } else {
                mViewHolder = (MyViewHolder) convertView.getTag();
            }

            String currentListData = (String) getItem(position);

            mViewHolder.textView.setText(currentListData);

            return convertView;
        }


        private class MyViewHolder {
            TextView textView;

            public MyViewHolder(View convertView) {
                textView = (TextView) convertView.findViewById(android.R.id.text1);
            }
        }
    }
}
