package org.sdfw.biometric.util;

import android.databinding.BindingAdapter;
import android.databinding.adapters.AdapterViewBindingAdapter;
import android.support.v7.widget.RecyclerView;
import android.util.Base64;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.Spinner;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;

import org.sdfw.biometric.R;
import org.sdfw.biometric.delegate.BindableAdapter;
import org.sdfw.biometric.model.Center;

import java.util.ArrayList;
import java.util.List;

import fr.ganfra.materialspinner.MaterialSpinner;

import static org.sdfw.biometric.util.Constant.TAG;

public class BindingUtils {

    @BindingAdapter("app:data")
    public static void setSpinnerData(Spinner spinner, List<Center> data) {
        Log.d(TAG, "setSpinnerData: setting spinner data " + data.size());
        if (data.size() > 0) {
            List<String> entries = new ArrayList<>();
            for (Center c : data) {
                entries.add(c.getCenterName());
            }
            ArrayAdapter<String> adapter = (ArrayAdapter<String>) spinner.getAdapter();
            adapter.clear();
            adapter.addAll(entries);
            spinner.setOnItemSelectedListener(new AdapterViewBindingAdapter.OnItemSelectedComponentListener((parent, view, position, id) -> {
                Log.d(TAG, "onItemSelected: " + position);
            }, parent -> Log.d(TAG, "onNothingSelected: "), () -> {
                Log.d(TAG, "onChange: ");
            }));
        }
    }

    @BindingAdapter("app:data")
    public static <T> void setRecyclerViewData(RecyclerView recyclerView, T data) {
        if (recyclerView.getAdapter() instanceof BindableAdapter) {
            ((BindableAdapter<T>) recyclerView.getAdapter()).setData(data);
        }
    }

    @BindingAdapter("app:imgData")
    public static void setImageViewData(ImageView imageView, String data) {
//        if (true) return;
        if (data != null && !data.isEmpty()) {
            Glide.with(imageView.getContext())
                    .asBitmap()
                    .load(Base64.decode(data, Base64.NO_WRAP))
                    .apply(new RequestOptions()
                            .placeholder(R.drawable.avatar)
                            .error(R.drawable.avatar)
                            .centerCrop())
                    .into(imageView);
        } else {
            imageView.setImageResource(R.drawable.avatar);
        }
    }

    @BindingAdapter("app:error")
    public static void setSpinnerError(MaterialSpinner spinner, String data) {
        Log.d(TAG, "setSpinnerError: set");
        spinner.setError(data);
    }

}
