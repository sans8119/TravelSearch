package com.uiSearch.presentation.fragment;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.Fragment;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Point;
import android.graphics.Rect;
import android.location.Location;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.HorizontalScrollView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;

import com.google.gson.Gson;
import com.uiSearch.customViews.CustomImageView;
import com.uiSearch.customViews.CustomLinearLayout;
import com.uiSearch.customViews.dataPicker.DatePickerDailog;
import com.uiSearch.data.entity.BaseEntity;
import com.uiSearch.data.entity.SearchSuggestionsEntity;
import com.uiSearch.data.network.Api;
import com.uiSearch.data.network.NetworkConnection;
import com.uiSearch.data.serializer.JsonDataManager;
import com.uiSearch.presentation.activity.MainActivity;
import com.uiSearch.presentation.adapter.CountryCodesAdapter;
import com.uiSearch.utils.Constants;
import com.uiSearch.utils.CustomListener;
import com.uiSearch.utils.SearchSuggestionsEntityComparator;
import com.uiSearch.utils.bindings.DaggerMainScreenComponent;
import com.uiSearch.utils.bindings.FragmentModule;
import com.uiSearch.utils.bindings.MainScreenComponent;
import com.uisearch.presentation.R;

import java.lang.reflect.Constructor;
import java.net.MalformedURLException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.FutureTask;

import javax.inject.Inject;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.OnItemSelected;
import butterknife.OnTouch;


public class MainScreenFragment extends Fragment implements Animation.AnimationListener {
    private String TAG;
    private Animation leftToRight;
    private List<SearchSuggestionsEntity> mResults;

    private View view;
    private EditText otherEditText;
    private Button searchButton;
    private Button dateOfJourneyButton;

    @Inject
    NetworkConnection networkConnection;
    @Inject
    JsonDataManager jsonDataManager;
    @Bind(R.id.wait_1)
    View wait1;
    @Bind(R.id.wait_2)
    View wait2;
    @Bind(R.id.to_edit_text)
    EditText toEditText;
    @Bind(R.id.from_edit_text)
    EditText fromEditText;
    @Bind(R.id.to_msg_on_spinner)
    TextView toMsgOnSpinner;
    @Bind(R.id.from_msg_on_spinner)
    TextView fromMsgOnSpinner;
    @Bind(R.id.from_country_code_spinner)
    Spinner fromCountryCodeSpinner;
    @Bind(R.id.to_country_code_spinner)
    Spinner toCountryCodeSpinner;
    @Bind(R.id.parent_layout)
    CustomLinearLayout parentLayout;
    @Bind(R.id.leaf_image_view)
    CustomImageView customImageView;
    @Bind(R.id.lin_lay)
    LinearLayout linearLayout;
    @Bind(R.id.lin_lay_scroll)
    LinearLayout linearLayoutInScrollView;
    @Bind(R.id.horizontal_scroll)
    HorizontalScrollView scrollView;

    private boolean toEditTextFlag;
    private boolean fromEditTextFlag;

    private int mMediumAnimationDuration;
    private CustomTextWatcher customTextWatcher;
    private int rainDropRadius;

    private void initUIComponents(View rootView) {
        CountryCodesAdapter countryCodesAdapter = new CountryCodesAdapter(this.getActivity());
        toCountryCodeSpinner.setAdapter(countryCodesAdapter);
        fromCountryCodeSpinner.setAdapter(countryCodesAdapter);
        toCountryCodeSpinner.setSelection(67);
        fromCountryCodeSpinner.setSelection(67);

        searchButton = ((Button) rootView.findViewById(R.id.search));
        dateOfJourneyButton = ((Button) rootView.findViewById(R.id.date_of_journey));

        toEditText.addTextChangedListener(customTextWatcher);
        fromEditText.addTextChangedListener(customTextWatcher);
        customImageView.setListener(new CustomListener() {
            public void enableEditText() {
                otherEditText.setEnabled(true);
            }

            public void setCanvasSize(int canvasSize) {
                if(canvasSize>mCanvasSize) {
                    resizeView((View) customImageView, canvasSize, ViewGroup.LayoutParams.MATCH_PARENT);
                    mCanvasSize = canvasSize;
                }
            }
        });
        mMediumAnimationDuration = getResources().getInteger(
                android.R.integer.config_mediumAnimTime);
    }
private  int mCanvasSize;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        TAG = getClass().getCanonicalName();
        View rootView = inflater.inflate(R.layout.fragment_main, container, false);
        rainDropRadius = Integer.parseInt(Constants.RainDropRadius.getValue());
        MainScreenComponent component = DaggerMainScreenComponent.builder().fragmentModule(new FragmentModule()).build();
        networkConnection = component.provideNetworkConnection();
        jsonDataManager = component.provideJsonDataManager();
        ButterKnife.bind(this, rootView);
        leftToRight = AnimationUtils.loadAnimation(this.getActivity(),
                R.anim.left_to_right);
        leftToRight.setAnimationListener(this);
        customTextWatcher = new CustomTextWatcher();
        initUIComponents(rootView);
        resizeView((View) customImageView, 7000, ViewGroup.LayoutParams.MATCH_PARENT);
        setWidth();
        return rootView;
    }

    @OnTouch(R.id.horizontal_scroll)
    public boolean onTouch(View view, MotionEvent event) {
        int action = event.getActionMasked();
        switch (action) {
            case MotionEvent.ACTION_UP:
                toEditText.removeTextChangedListener(customTextWatcher);
                fromEditText.removeTextChangedListener(customTextWatcher);
                float x = event.getX() + (int) (scrollView.getScrollX());
                float y = event.getY();
                String text = "--";
                if (mResults != null) {
                    Iterator<SearchSuggestionsEntity> itr = mResults.iterator();
                    while (itr.hasNext()) {
                        SearchSuggestionsEntity SearchSuggestionsEntity = itr.next();
                        Point point = SearchSuggestionsEntity.getPoinOnCanvas();
                        if (point != null) {
                            Rect rect = new Rect(point.x - rainDropRadius, point.y - rainDropRadius, point.x - rainDropRadius + 2 * rainDropRadius, point.y - rainDropRadius + 2 * rainDropRadius);
                            if (rect.contains((int) x, (int) y)) {
                                text = SearchSuggestionsEntity.getFullName();
                                EditText selectedEditText = toEditText;
                                if (otherEditText.getId() == fromEditText.getId()) {
                                    selectedEditText = toEditText;
                                } else if (otherEditText.getId() == toEditText.getId()) {
                                    selectedEditText = fromEditText;
                                }
                                selectedEditText.setText(text);
                                if (toEditText.getText().length() > 0 && fromEditText.getText().length() > 0)
                                    toggleButton(true);
                                break;
                            }
                            Log.i(TAG, otherEditText.getId() + " Text selected: " + text);
                        }
                    }
                    toEditText.addTextChangedListener(customTextWatcher);
                    fromEditText.addTextChangedListener(customTextWatcher);


                }
                break;
        }
        return false;
    }

    @OnClick(R.id.date_of_journey)
    public void onClick(final View view) {
        final Calendar dateandtime = Calendar.getInstance(Locale.US);
        DatePickerDailog dp = new DatePickerDailog(getActivity(),
                dateandtime, new DatePickerDailog.DatePickerListner() {

            @Override
            public void OnDoneButton(Dialog datedialog, Calendar c) {
                datedialog.dismiss();
                dateandtime.set(Calendar.YEAR, c.get(Calendar.YEAR));
                dateandtime.set(Calendar.MONTH,
                        c.get(Calendar.MONTH));
                dateandtime.set(Calendar.DAY_OF_MONTH,
                        c.get(Calendar.DAY_OF_MONTH));
                ((Button) view).setText(new SimpleDateFormat("MMMM dd yyyy")
                        .format(c.getTime()));
            }

            @Override
            public void OnCancelButton(Dialog datedialog) {
                datedialog.dismiss();
            }
        });
        dp.show();
    }

    @OnItemSelected({R.id.from_country_code_spinner, R.id.to_country_code_spinner})
    public void onItemSelected(AdapterView<?> parent, View view, int pos, long id) {
        String selectedStr = parent.getItemAtPosition(pos).toString();
        String[] strArr = selectedStr.split(",");
        if (toCountryCodeSpinner.getId() == parent.getId()) {
            toMsgOnSpinner.setText(strArr[0]);
        } else {
            fromMsgOnSpinner.setText(strArr[0]);
        }
    }

    @Override
    public void onAnimationEnd(Animation animation) {
    }

    @Override
    public void onAnimationRepeat(Animation animation) {
    }

    @Override
    public void onAnimationStart(Animation animation) {
    }

    private void toggleButton(boolean showSearchButton) {
        Button visibleButton = searchButton;
        Button goneButton = dateOfJourneyButton;
        int to = toEditText.getText().length();
        int from = fromEditText.getText().length();
        boolean flag = true;
        if (showSearchButton) {
            visibleButton = searchButton;
            goneButton = dateOfJourneyButton;
        } else if (!showSearchButton) {
            visibleButton = dateOfJourneyButton;
            goneButton = searchButton;
        }
        final Button goneButtonFinal = goneButton;
        visibleButton.setVisibility(View.VISIBLE);
        visibleButton.animate()
                .alpha(1f)
                .setDuration(mMediumAnimationDuration)
                .setListener(null);
        goneButton.animate()
                .alpha(0f)
                .setDuration(mMediumAnimationDuration)
                .setListener(new AnimatorListenerAdapter() {
                    @Override
                    public void onAnimationEnd(Animator animation) {
                        goneButtonFinal.setVisibility(View.GONE);
                    }
                });
    }

    private void resizeView(View view, int newWidth, int newHeight) {
        try {
            Constructor<? extends ViewGroup.LayoutParams> ctor = view.getLayoutParams().getClass().getDeclaredConstructor(int.class, int.class);
            view.setLayoutParams(ctor.newInstance(newWidth, newHeight));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void setWidth() {
        DisplayMetrics displaymetrics = new DisplayMetrics();
        getActivity().getWindowManager().getDefaultDisplay().getMetrics(displaymetrics);
        int height = displaymetrics.heightPixels;
        int width = displaymetrics.widthPixels;
        FrameLayout.LayoutParams parms = new FrameLayout.LayoutParams(2 * width, LinearLayout.LayoutParams.MATCH_PARENT);
        linearLayoutInScrollView.setLayoutParams(parms);
        linearLayoutInScrollView.requestLayout();
    }

    private void setAndStartIndeterminateAnimation() {
        wait1.setAnimation(leftToRight);
        wait2.setAnimation(leftToRight);
        leftToRight.start();
    }

    class CustomTextWatcher implements TextWatcher {
        private View view;
        private long time;

        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            boolean showSearch = true;
            if (fromEditText.isFocused()) {
                MainScreenFragment.this.view = linearLayout;
            } else if (toEditText.isFocused()) {
                MainScreenFragment.this.view = wait2;
            }
            if (!(dateOfJourneyButton.getVisibility() == View.VISIBLE)) {
                toggleButton(false);
            }

            MainScreenFragment.this.view.setVisibility(View.VISIBLE);
        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
        }

        @Override
        public void afterTextChanged(Editable s) {
            TextView textView = toMsgOnSpinner;
            view = null;
            if (fromEditText.isFocused()) {
                view = wait1;
                textView = fromMsgOnSpinner;
                otherEditText = toEditText;
            } else if (toEditText.isFocused()) {
                view = wait2;
                otherEditText = fromEditText;
            }
            otherEditText.setEnabled(false);
            if (view != null) {
                view.setAnimation(leftToRight);
            }
            view.startAnimation(leftToRight);
            /*toEditText.removeTextChangedListener(this);
            fromEditText.removeTextChangedListener(this);*/
            String query = new StringBuffer().append(textView.getText().toString()).append("/").append(s.toString().trim()).toString();
            Log.i(TAG, "Server search query: " + query);
            try {
                // if((System.currentTimeMillis()-time) > 300) {   Log.i(TAG, "1111  Server search query: " + query);
                search(query, view);
                   /* time=System.currentTimeMillis();
                }*/

            } catch (Exception e) {
                e.printStackTrace();
            }

        }
    }

    private void runOnUi(Thread thread) {
        getActivity().runOnUiThread(thread);
    }

    private List<SearchSuggestionsEntity> mapJsonToModel(String json, Thread thread) {
        final ArrayList<SearchSuggestionsEntity> results = new ArrayList<SearchSuggestionsEntity>();
        BaseEntity[] searchSuggestions = jsonDataManager.deserialize(json);
        if(searchSuggestions==null)
            return results;
        for (int i = 0; i < searchSuggestions.length; i++) {
            results.add((SearchSuggestionsEntity) searchSuggestions[i]);
        }
        MainActivity mainActivity = (MainActivity) getActivity();
        Location location = new Location("");
        location.setLatitude(mainActivity.getLatitude());
        location.setLongitude(mainActivity.getLongitude());
        Collections.sort(results, new SearchSuggestionsEntityComparator(location));
        mResults = results;
        Log.i(TAG, "Results from server: \n " + mResults);
        return mResults;
    }

    private void search(String query, final View view) throws MalformedURLException {
       /* toEditText.addTextChangedListener(customTextWatcher);
        fromEditText.addTextChangedListener(customTextWatcher);*/
        view.clearAnimation();
        MainScreenFragment.this.view.setVisibility(View.INVISIBLE);
        final Context context = MainScreenFragment.this.getActivity();
        if (networkConnection.isNetworkAvailable(context)) {
            String url = new StringBuffer(100).append(Api.API_URL.getValue()).append(Api.API_URL_GET_SEARCH_LIST.getValue()).
                    append(query).toString().trim();
            networkConnection.setUrl(url);
            final FutureTask<String> futureTask = new FutureTask<String>(networkConnection);
            new Thread() {
                public void run() {
                    try {
                        futureTask.run();
                        final String result = futureTask.get();
                        if(MainScreenFragment.this.getActivity()==null) return;//if the activity is closed when running this thread.
                        final List<SearchSuggestionsEntity> searchSuggestions = mapJsonToModel(result, this);
                        runOnUi(new Thread() {
                            public void run() {
                                if (result != null && !result.isEmpty()) {
                                    //resizeView((View) customImageView, 7000, ViewGroup.LayoutParams.MATCH_PARENT);
                                    parentLayout.createRainDrops(searchSuggestions);
                                    view.clearAnimation();
                                    MainScreenFragment.this.view.setVisibility(View.INVISIBLE);
                                } else {
                                    new AlertDialog.Builder(context)
                                            .setMessage("The device is not internet connected")
                                            .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                                                public void onClick(DialogInterface dialog, int which) {
                                                    dialog.dismiss();
                                                }
                                            })
                                            .setIcon(android.R.drawable.ic_dialog_alert)
                                            .show();
                                }
                            }
                        });
                    } catch (InterruptedException i) {
                        i.printStackTrace();
                    } catch (ExecutionException e) {
                        e.printStackTrace();
                    }
                }


            }.start();
        }
    }


}