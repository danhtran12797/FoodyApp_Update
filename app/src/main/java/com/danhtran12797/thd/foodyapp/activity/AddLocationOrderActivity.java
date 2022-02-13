package com.danhtran12797.thd.foodyapp.activity;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.appcompat.widget.Toolbar;

import com.danhtran12797.thd.foodyapp.R;
import com.danhtran12797.thd.foodyapp.adapter.AddressDetailAdapter;
import com.danhtran12797.thd.foodyapp.model.AddressDetail;
import com.danhtran12797.thd.foodyapp.model.AddressShipping;
import com.danhtran12797.thd.foodyapp.model.ListProvin;
import com.danhtran12797.thd.foodyapp.service.APIAddressService;
import com.danhtran12797.thd.foodyapp.service.DataAddressService;
import com.danhtran12797.thd.foodyapp.ultil.Ultil;

import java.util.ArrayList;
import java.util.List;

import maes.tech.intentanim.CustomIntent;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AddLocationOrderActivity extends AppCompatActivity implements View.OnClickListener {

    private static final String TAG = "AddLocationOrderActivit";

    private Toolbar toolbar;
    private Button btn_add_new_location;
    private EditText edt_name;
    private EditText edt_phone;
    private EditText edt_provin;
    private EditText edt_distric;
    private EditText edt_ward;
    private EditText edt_address;
    private CheckBox checkBox;

    private int id_provin = -1;
    private int id_distric = -1;
    private AddressDetailAdapter adapter;
    private ArrayList<AddressDetail> arrAddress;

    private ListView listView;
    private ImageView imgClos;
    private TextView txtTitle;
    private SearchView search_address_detail;
    private LinearLayout layout_lisview_select_address;
    private ProgressBar progress_dialog_select_address;

    private Dialog dialog;
    private AddressShipping addressShipping = null;
    private int position_location_order = -1;

    String name = "";
    String address = "";
    String phone = "";
    String provin = "";
    String distric = "";
    String ward = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_location_order);

        initView();
        get_intent();
        initActionBar();
        initDialog();
    }

    private void get_intent() {
        Intent intent = getIntent();
        if (intent.hasExtra("edit_location_order")) {
            addressShipping = (AddressShipping) intent.getSerializableExtra("edit_location_order");
            position_location_order = intent.getIntExtra("position_location_order", -1);
            Log.d(TAG, "position_location_order frist: " + position_location_order);

            name = addressShipping.getName();
            phone = addressShipping.getPhone();

            edt_name.setText(name);
            edt_phone.setText(phone);
            checkBox.setChecked(addressShipping.isCheck());
            extra_full_address(addressShipping.getAddress());
            edt_provin.setText(provin);
            edt_distric.setText(distric);
            edt_ward.setText(ward);
            edt_address.setText(address);

            toolbar.setTitle("Cập nhật địa chỉ");
            btn_add_new_location.setText("lưu địa chỉ");
        }
    }

    private void extra_full_address(String address_full) {
        String arr[] = address_full.split(", ");
        provin = arr[3];
        distric = arr[2];
        ward = arr[1];
        address = arr[0];

    }

    private void initDialog() {
        dialog = new Dialog(this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.dialog_select_address);

        listView = dialog.findViewById(R.id.listview_select_address);
        imgClos = dialog.findViewById(R.id.img_close_dialog_select_address);
        txtTitle = dialog.findViewById(R.id.txt_title_select_address);
        search_address_detail = dialog.findViewById(R.id.search_address_detail);
        layout_lisview_select_address = dialog.findViewById(R.id.layout_lisview_select_address);
        search_address_detail = dialog.findViewById(R.id.search_address_detail);
        progress_dialog_select_address = dialog.findViewById(R.id.progress_dialog_select_address);
    }

    private String check_all() {
        name = edt_name.getText().toString().trim();
        address = edt_address.getText().toString().trim();
        phone = edt_phone.getText().toString().trim();
        provin = edt_provin.getText().toString().trim();
        distric = edt_distric.getText().toString().trim();
        ward = edt_ward.getText().toString().trim();

        String result = "success";
        if (name.isEmpty()) {
            return "Vui lòng nhập họ tên";
        } else if (!check_input_name(name)) {
            return "Vui lòng nhập đầy đủ họ tên";
        }

        if (phone.isEmpty()) {
            return "Vui lòng nhập số điện thoại";
        } else if (!(phone.length() == 10 && Ultil.check_phone_valid(phone, this))) {
            return "Số điện thoại không hợp lệ";
        }

        if (provin.isEmpty()) {
            return "Vui lòng nhập tỉnh/thành";
        }

        if (distric.isEmpty()) {
            return "Vui lòng nhập quận/huyện";
        }

        if (ward.isEmpty()) {
            return "Vui lòng nhập phường/xã";
        }
        if (address.isEmpty()) {
            return "Vui lòng nhập địa chỉ";
        }
        return result;
    }

    private boolean check_input_name(String name) {
        boolean check = true;
        if (!name.contains(" ")) {
            check = false;
        }
        return check;
    }

    public void load_all_provin() {
        showProgress();
        arrAddress.clear();
        DataAddressService service = APIAddressService.getService();
        Call<ListProvin> callback = service.GetAllProvin();
        callback.enqueue(new Callback<ListProvin>() {
            @Override
            public void onResponse(Call<ListProvin> call, Response<ListProvin> response) {
                ListProvin listProvin = response.body();
                setAdapterAddress(listProvin.getLtsItem());
                hideProgress();
            }

            @Override
            public void onFailure(Call<ListProvin> call, Throwable t) {
                Log.d(TAG, "onFailure: " + t.getMessage());
            }
        });
    }

    public void load_all_district(int id) {
        showProgress();
        arrAddress.clear();
        DataAddressService service = APIAddressService.getService();
        Call<List<AddressDetail>> callback = service.GetAllDistrict(id);
        callback.enqueue(new Callback<List<AddressDetail>>() {
            @Override
            public void onResponse(Call<List<AddressDetail>> call, Response<List<AddressDetail>> response) {
                ArrayList<AddressDetail> arrayList = (ArrayList<AddressDetail>) response.body();
                setAdapterAddress(arrayList);
                hideProgress();
            }

            @Override
            public void onFailure(Call<List<AddressDetail>> call, Throwable t) {
                Log.d(TAG, "onFailure: " + t.getMessage());
            }
        });
    }

    public void load_all_ward(int id) {
        showProgress();
        arrAddress.clear();
        DataAddressService service = APIAddressService.getService();
        Call<List<AddressDetail>> callback = service.GetAllWard(id);
        callback.enqueue(new Callback<List<AddressDetail>>() {
            @Override
            public void onResponse(Call<List<AddressDetail>> call, Response<List<AddressDetail>> response) {
                ArrayList<AddressDetail> arrayList = (ArrayList<AddressDetail>) response.body();
                setAdapterAddress(arrayList);
                hideProgress();
            }

            @Override
            public void onFailure(Call<List<AddressDetail>> call, Throwable t) {
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
        arrAddress = new ArrayList<>();

        toolbar = findViewById(R.id.toolbar_add_location);
        btn_add_new_location = findViewById(R.id.btn_add_new_loca);
        edt_name = findViewById(R.id.edt_name_loca);
        edt_phone = findViewById(R.id.edt_phone_loca);
        edt_provin = findViewById(R.id.edt_provin_loca);
        edt_distric = findViewById(R.id.edt_distric_loca);
        edt_ward = findViewById(R.id.edt_ward_loca);
        edt_address = findViewById(R.id.edt_address_loca);
        checkBox = findViewById(R.id.checkbox_default_loca);

        btn_add_new_location.setOnClickListener(this);
        edt_provin.setOnClickListener(this);
        edt_distric.setOnClickListener(this);
        edt_ward.setOnClickListener(this);

    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.edt_provin_loca:
                show_dialog_select_address(-1, arrAddress, 1);
                edt_distric.setText("");
                edt_ward.setText("");
                break;
            case R.id.edt_distric_loca:
                if (id_provin != -1) {
                    show_dialog_select_address(id_provin, arrAddress, 2);
                    edt_ward.setText("");
                } else {
                    Toast.makeText(this, "Vui lòng chọn tỉnh/thành", Toast.LENGTH_SHORT).show();
                }
                break;
            case R.id.edt_ward_loca:
                if (id_distric != -1) {
                    show_dialog_select_address(id_distric, arrAddress, 3);
                } else {
                    Toast.makeText(this, "Vui lòng chọn quận/huyện", Toast.LENGTH_SHORT).show();
                }
                break;
            case R.id.btn_add_new_loca:
                // kiểm tra user có nhập đúng thông tin
                String result = check_all();
                if (result.equals("success")) {
                    Ultil.arrAddressShipping = Ultil.getAddressShipping(this);
                    String full_address = getFullAddress(
                            edt_provin.getText().toString()
                            , edt_distric.getText().toString()
                            , edt_ward.getText().toString()
                            , edt_address.getText().toString());

                    AddressShipping addressShipping = new AddressShipping(name, full_address, phone, checkBox.isChecked());
                    if (Ultil.arrAddressShipping != null) {
                        // trường hợp user update địa chỉ giao hàng
                        if (position_location_order != -1) {
                            Ultil.arrAddressShipping.get(position_location_order).setCheck(addressShipping.isCheck());
                            Ultil.arrAddressShipping.get(position_location_order).setName(addressShipping.getName());
                            Ultil.arrAddressShipping.get(position_location_order).setPhone(addressShipping.getPhone());
                            Ultil.arrAddressShipping.get(position_location_order).setAddress(addressShipping.getAddress());
                            // set lại tất cả mảng AddressShipping, để set default checked
                            if (addressShipping.isCheck()) {
                                setCheckDefault(position_location_order);
                            }
                        } else {
                            Ultil.arrAddressShipping.add(0, addressShipping);
                            // set lại tất cả mảng AddressShipping, để set default checked
                            if (addressShipping.isCheck()) {
                                setCheckDefault(0);
                            }
                        }
                        // trường hợp user thêm mới địa chỉ giao hàng
                    } else {
                        Ultil.arrAddressShipping = new ArrayList<>();
                        Ultil.arrAddressShipping.add(addressShipping);
                        Ultil.arrAddressShipping.get(0).setCheck(true);
                    }
                    // lưu xuống shared_preference
                    Ultil.setAddressShipping(this);
                    Intent resultIntent = new Intent();
                    setResult(RESULT_OK, resultIntent);
                    finish();
                } else {
                    Toast.makeText(this, result, Toast.LENGTH_LONG).show();
                }
                break;
        }
    }

    private void setCheckDefault(int position) {
        for (AddressShipping shipping : Ultil.arrAddressShipping) {
            shipping.setCheck(false);
        }
        Ultil.arrAddressShipping.get(position).setCheck(true);
    }

    private String getFullAddress(String provin, String distric, String ward, String address) {
        String full_address = (new StringBuilder())
                .append(address)
                .append(", " + ward)
                .append(", " + distric)
                .append(", " + provin)
                .toString();
        return full_address;
    }

    private void show_dialog_select_address(final int id, final ArrayList<AddressDetail> arrayList, final int code) {
        switch (code) {
            case 1:
                load_all_provin();
                break;
            case 2:
                load_all_district(id);
                break;
            case 3:
                load_all_ward(id);
                break;
        }
        setDataDialog(code);

        search_address_detail.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                adapter.getFilter().filter(newText);
                return false;
            }
        });

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                // gán vào editText khi user chọn thành phố,... từ dialog
                if (code == 1) {
                    edt_provin.setText(adapter.getTitle(i));
                    id_provin = adapter.getId(i);
                } else if (code == 2) {
                    edt_distric.setText(adapter.getTitle(i));
                    id_distric = adapter.getId(i);
                } else {
                    edt_ward.setText(adapter.getTitle(i));
                }
                dialog.dismiss();
            }
        });

        imgClos.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
            }
        });

        dialog.show();
    }

    public void setDataDialog(int code) {
        if (code == 1) {
            txtTitle.setText("Chọn Tỉnh/Thành");
            search_address_detail.setQueryHint("Tìm kiếm Tỉnh/Thành");
        } else if (code == 2) {
            txtTitle.setText("Chọn Quận/Huyện");
            search_address_detail.setQueryHint("Tìm kiếm Quận/Huyện");
        } else {
            txtTitle.setText("Chọn Phường/Xã");
            search_address_detail.setQueryHint("Tìm kiếm Phường/Xã");
        }
    }

    public void setAdapterAddress(ArrayList<AddressDetail> arrayList) {
        adapter = new AddressDetailAdapter(this, arrayList);
        listView.setAdapter(adapter);
    }

    private void showProgress() {
        progress_dialog_select_address.setVisibility(View.VISIBLE);
        layout_lisview_select_address.setVisibility(View.GONE);
    }

    private void hideProgress() {
        progress_dialog_select_address.setVisibility(View.GONE);
        layout_lisview_select_address.setVisibility(View.VISIBLE);
    }

    @Override
    public void finish() {
        super.finish();
        CustomIntent.customType(this, "fadein-to-fadeout");
    }
}
