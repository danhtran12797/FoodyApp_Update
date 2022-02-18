package com.danhtran12797.thd.foodyapp.activity;

import static com.danhtran12797.thd.foodyapp.ultil.Ultil.arrHistory;
import static com.danhtran12797.thd.foodyapp.ultil.Ultil.getHistorySearchPreference;
import static com.danhtran12797.thd.foodyapp.ultil.Ultil.removeHistorySearchPreference;
import static com.danhtran12797.thd.foodyapp.ultil.Ultil.setHistorySearchPreference;

import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.os.Bundle;
import android.speech.RecognizerIntent;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.danhtran12797.thd.foodyapp.R;
import com.danhtran12797.thd.foodyapp.activity.listener.ILoading;
import com.danhtran12797.thd.foodyapp.adapter.DealProductAdapter;
import com.danhtran12797.thd.foodyapp.adapter.HistoryAdapter;
import com.danhtran12797.thd.foodyapp.adapter.IHistory;
import com.danhtran12797.thd.foodyapp.adapter.ISearch;
import com.danhtran12797.thd.foodyapp.adapter.LoveProductAdapter;
import com.danhtran12797.thd.foodyapp.adapter.SearchAdapter;
import com.danhtran12797.thd.foodyapp.model.Product;
import com.danhtran12797.thd.foodyapp.service.APIService;
import com.danhtran12797.thd.foodyapp.service.DataService;
import com.danhtran12797.thd.foodyapp.ultil.Ultil;
import com.google.android.flexbox.FlexboxLayout;
import com.jaredrummler.materialspinner.MaterialSpinner;
import com.victor.loading.rotate.RotateLoading;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import de.hdodenhof.circleimageview.CircleImageView;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SearchActivity extends AppCompatActivity implements ISearch, IHistory, ILoading {

    private static final String TAG = "SearchActivity";
    private static final int REQUEST_CODE_SEARCH_VOICE = 100;

    private RecyclerView recyclerView_product;
    private RecyclerView recyclerView_history;
    private Toolbar toolbar;
    private Button btn_continue;
    private TextView txt_history;
    private ImageView imgClose;
    private AutoCompleteTextView txtSearch;
    private CircleImageView imgVoice;
    private LoveProductAdapter adapter;
    private DealProductAdapter adapter1;
    private ArrayList<Product> arrProduct;
    private RotateLoading rotateLoading;
    private FrameLayout layout_container;
    private FlexboxLayout flexboxLayout;
    private RelativeLayout layout_search_main;
    private LinearLayout layout_search, layout_history, layout_empty_search;
    private ISearch listener;
    private HistoryAdapter historyAdapter;

    private MaterialSpinner spinner;
    private ImageView imageSwitch;
    private int type_search = 1;

    ILoading mListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);

        mListener = this;

        spinner = findViewById(R.id.spinner);
        spinner.setItems("Mới nhất", "Giá từ thấp tới cao", "Giá từ cao xuống thấp");
        spinner.setOnItemSelectedListener(new MaterialSpinner.OnItemSelectedListener<String>() {
            @Override
            public void onItemSelected(MaterialSpinner view, int position, long id, String item) {
//                Snackbar.make(viewBottomSheet, "Clicked " + item, Snackbar.LENGTH_LONG).show();
                String query = txtSearch.getText().toString();
                if (query.length() != 0) {
                    getData(query);
                    Log.d(TAG, "onItemSelected: " + position);
                }
            }
        });

        initView();
        initActionBar();
        initAutocompleteSearch();
        addViewFlexboxLayout();
        loadHistory();

        imgClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                txtSearch.setText("");
            }
        });

        imgVoice.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                promptSpeechInput();
            }
        });

        btn_continue.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        txt_history.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                removeHistorySearchPreference(SearchActivity.this);
                layout_history.setVisibility(View.GONE);
                arrHistory.clear();
                historyAdapter.notifyDataSetChanged();
            }
        });

        imageSwitch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (type_search == 1) {
                    imageSwitch.setImageResource(R.drawable.list_view);
                    type_search = 2;
                } else if (type_search == 2) {
                    imageSwitch.setImageResource(R.drawable.grid_view);
                    type_search = 1;
                }

                setAdapter(arrProduct);
            }
        });
    }

    private void loadHistory() {
        arrHistory = getHistorySearchPreference(this);
        if (arrHistory == null) {
            arrHistory = new ArrayList<>();
            layout_history.setVisibility(View.GONE);
        } else {
            layout_history.setVisibility(View.VISIBLE);
        }
        historyAdapter = new HistoryAdapter(arrHistory, this);
        recyclerView_history.setLayoutManager(new LinearLayoutManager(this));
        recyclerView_history.setAdapter(historyAdapter);
    }

    private void addViewFlexboxLayout() {
        String arr[] = getResources().getStringArray(R.array.name_hot_search);
        for (String s : arr) {
            TextView textView = new TextView(this);
            textView.setText(s);
            FlexboxLayout.LayoutParams params = new FlexboxLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            params.setMargins(8, 12, 8, 8);
            textView.setLayoutParams(params);
            textView.setBackgroundResource(R.drawable.custom_text_key_hot);
            textView.setTextColor(getResources().getColor(R.color.text_color));
            flexboxLayout.addView(textView);

            textView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    listener.itemClick(textView.getText().toString());
                }
            });
        }
    }

    private void initAutocompleteSearch() {
        txtSearch.setThreshold(1); //will start working from first character
        txtSearch.setDropDownWidth(800);
        txtSearch.setAdapter(new SearchAdapter(this, android.R.layout.simple_list_item_1));
        txtSearch.setImeOptions(EditorInfo.IME_ACTION_SEARCH);
        txtSearch.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                    //do here your stuff f
                    if (v.getText().length() != 0) {
                        listener.itemClick(v.getText().toString());
                    }
                    return true;
                }
                return false;
            }
        });

        txtSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (s.length() != 0) {
                    imgClose.setVisibility(View.VISIBLE);
                    imgVoice.setVisibility(View.GONE);
                } else {
                    imgClose.setVisibility(View.GONE);
                    imgVoice.setVisibility(View.VISIBLE);
                    layout_search.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });
    }

    private void setAdapter(ArrayList<Product> arrayList) {
        if (type_search == 1) {
            adapter = new LoveProductAdapter(SearchActivity.this, arrProduct);
            recyclerView_product.setLayoutManager(new LinearLayoutManager(SearchActivity.this));
            recyclerView_product.setAdapter(adapter);

        } else if (type_search == 2) {
            adapter1 = new DealProductAdapter(arrProduct);
            recyclerView_product.setLayoutManager(new GridLayoutManager(SearchActivity.this, 2));
            recyclerView_product.setAdapter(adapter1);
        }
    }

    private void getData(String query) {
//        rotateLoading.start();
        mListener.start_loading();
        DataService dataService = APIService.getService();
        Call<List<Product>> callback = dataService.GetProductFromSearchAll(query, String.valueOf(spinner.getSelectedIndex() + 1));
        callback.enqueue(new Callback<List<Product>>() {
            @Override
            public void onResponse(Call<List<Product>> call, Response<List<Product>> response) {
//                txtSearch.dismissDropDown();
                layout_search.setVisibility(View.GONE);
                mListener.stop_loading(true);
                arrProduct.clear();
                arrProduct = (ArrayList<Product>) response.body();
                if (arrProduct.size() != 0) {
                    setAdapter(arrProduct);
                    layout_empty_search.setVisibility(View.GONE);
                    txtSearch.dismissDropDown();
                } else {
                    layout_empty_search.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onFailure(Call<List<Product>> call, Throwable t) {
                mListener.stop_loading(false);
                Log.d(TAG, "onFailure: " + t.getMessage());
            }
        });
    }

    private void initActionBar() {
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
    }

    private void initView() {
        toolbar = findViewById(R.id.toolbar_search_view);
        recyclerView_product = findViewById(R.id.recycler_view_search);
        recyclerView_history = findViewById(R.id.recycler_view_history);
        rotateLoading = findViewById(R.id.rotateLoading);
        layout_container = findViewById(R.id.layout_container);
        layout_container.setVisibility(View.GONE);
        imgClose = findViewById(R.id.imgClose);
        txtSearch = findViewById(R.id.actv);
        imgVoice = findViewById(R.id.imgVoice);
        flexboxLayout = findViewById(R.id.flexbox_search);
        layout_search = findViewById(R.id.layout_search);
        layout_search_main = findViewById(R.id.layout_search_main);
        layout_empty_search = findViewById(R.id.layout_empty_search);
        layout_history = findViewById(R.id.layout_history);
        btn_continue = findViewById(R.id.btn_continue);
        txt_history = findViewById(R.id.txt_history);
        imageSwitch = findViewById(R.id.imageSwitch);

        arrProduct = new ArrayList<>();
        adapter = new LoveProductAdapter(this, arrProduct);
        recyclerView_product.setAdapter(adapter);
        recyclerView_product.setLayoutManager(new LinearLayoutManager(this));

        listener = this;
    }

    private void promptSpeechInput() {
        Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL,
                RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.getDefault());
        intent.putExtra(RecognizerIntent.EXTRA_PROMPT, "Hãy nói gì đó");
        try {
            startActivityForResult(intent, REQUEST_CODE_SEARCH_VOICE);
        } catch (ActivityNotFoundException a) {
            Toast.makeText(this,
                    "Xin lỗi!! Thiết bị của bạn không hỗ trợ chức năng này",
                    Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_CODE_SEARCH_VOICE && resultCode == RESULT_OK && null != data) {
            ArrayList<String> matches = data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
            if (matches != null && matches.size() > 0) {
                String searchWrd = matches.get(0);
                if (!TextUtils.isEmpty(searchWrd)) {
                    txtSearch.setText(searchWrd);
                }
            }
            return;
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public void itemClick(String name) {
        Log.d(TAG, "itemClick: " + name);
        txtSearch.setText(name);
        if (!arrHistory.contains(name)) {
            if (arrHistory.size() == 0)
                layout_history.setVisibility(View.VISIBLE);
            arrHistory.add(0, name);
            setHistorySearchPreference(this);
            historyAdapter.notifyItemInserted(0);
        }
        getData(name);
    }

    @Override
    public void itemClickHistory(String name) {
        Log.d(TAG, "itemClickHistory: " + name);
        txtSearch.setText(name);
        getData(name);
    }

    @Override
    public void start_loading() {
        rotateLoading.start();
        layout_container.setVisibility(View.VISIBLE);
    }

    @Override
    public void stop_loading(boolean isConnect) {
        rotateLoading.stop();
        layout_container.setVisibility(View.GONE);
        if (!isConnect) {
            Ultil.show_snackbar(layout_search_main, null);
        }
    }
}
