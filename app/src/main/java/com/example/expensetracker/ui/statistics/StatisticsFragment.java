package com.example.expensetracker.ui.statistics;

import android.annotation.SuppressLint;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.PopupMenu;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.bumptech.glide.Glide;
import com.example.expensetracker.R;
import com.example.expensetracker.databinding.FragmentStatisticsBinding;
import com.example.expensetracker.model.Trip;
import com.example.expensetracker.utils.BaseApp;
import com.github.mikephil.charting.animation.Easing;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.formatter.PercentFormatter;
import com.github.mikephil.charting.highlight.Highlight;
import com.github.mikephil.charting.listener.OnChartValueSelectedListener;
import com.github.mikephil.charting.utils.ColorTemplate;
import com.google.android.material.snackbar.Snackbar;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class StatisticsFragment extends Fragment implements OnSelectTripListener, OnChartValueSelectedListener {

    private StatisticsViewModel statisticsViewModel;
    private FragmentStatisticsBinding binding;
    private HashMap<Integer, String> labelMap = new HashMap<>();

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        statisticsViewModel =
                new ViewModelProvider(this).get(StatisticsViewModel.class);
        binding = FragmentStatisticsBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        setHasOptionsMenu(true);

        Calendar calendar = Calendar.getInstance();
        Date weekFinish = calendar.getTime();
        calendar.add(Calendar.DAY_OF_YEAR, -7);
        Date weekStart = calendar.getTime();
        statisticsViewModel.min = weekStart;
        statisticsViewModel.max = weekFinish;
        statisticsViewModel.isGroup = true;
        setDate();

        binding.alltripsSw.setChecked(true);
        binding.groupSw.setChecked(true);

        statisticsViewModel.allExpenseLiveList.observe(getViewLifecycleOwner(), creditList -> {
            plotPieChart();
        });

        statisticsViewModel.tripLive.observe(getViewLifecycleOwner(), trip -> {
            plotPieChart();
        });

        binding.selectTrip.setOnClickListener(v -> {
            ChooseTripDialog dialog = new ChooseTripDialog(this);
            dialog.show(getChildFragmentManager(), "Choose Trip");
        });

        binding.tripInfo.setOnClickListener(v -> {
            ChooseTripDialog dialog = new ChooseTripDialog(this);
            dialog.show(getChildFragmentManager(), "Choose Trip");
        });

        binding.alltripsSw.setOnCheckedChangeListener((compoundButton, b) -> {
            if (b) {
                binding.tripInfo.setVisibility(View.GONE);
                binding.selectTrip.setVisibility(View.VISIBLE);
                statisticsViewModel.loadExpensesPieChart();
            }
        });

        binding.personalSw.setOnCheckedChangeListener((compoundButton, b) -> {
            if (b) {
                binding.groupSw.setChecked(false);
                statisticsViewModel.isGroup = false;
                statisticsViewModel.filterByIsGroup();
                plotPieChart();
            } else {
                binding.personalSw.setChecked(false);
                binding.groupSw.setChecked(true);
                statisticsViewModel.isGroup = true;
                statisticsViewModel.filterByIsGroup();
                plotPieChart();
            }
        });

        binding.groupSw.setOnCheckedChangeListener((compoundButton, b) -> {
            if (b) {
                binding.personalSw.setChecked(false);
                statisticsViewModel.isGroup = true;
                statisticsViewModel.filterByIsGroup();
                plotPieChart();
            } else {
                binding.groupSw.setChecked(false);
                binding.personalSw.setChecked(true);
                statisticsViewModel.isGroup = false;
                statisticsViewModel.filterByIsGroup();
                plotPieChart();
            }
        });

        statisticsViewModel.loadExpensesPieChart();

    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        inflater.inflate(R.menu.statistics_menu, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    public void plotPieChart() {

        binding.pieChart.destroyDrawingCache();
        binding.pieChart.clear();

        binding.pieChart.setUsePercentValues(true);
        binding.pieChart.getDescription().setEnabled(false);
        binding.pieChart.setExtraOffsets(-10, 5, -10 ,-40);

        binding.pieChart.setDragDecelerationFrictionCoef(0.95f);
        binding.pieChart.setDrawHoleEnabled(true);
        binding.pieChart.setHoleColor(Color.WHITE);
        binding.pieChart.setTransparentCircleRadius(61f);
        binding.pieChart.setUsePercentValues(true);

        Legend l = binding.pieChart.getLegend();
        l.setVerticalAlignment(Legend.LegendVerticalAlignment.TOP);
        l.setHorizontalAlignment(Legend.LegendHorizontalAlignment.RIGHT);
        l.setFormSize(20F);
        l.setFormToTextSpace(5f);
        l.setForm(Legend.LegendForm.CIRCLE);
        l.setTextSize(12f);
        l.setOrientation(Legend.LegendOrientation.VERTICAL);
        l.setWordWrapEnabled(true);
        l.setDrawInside(false);
        l.setStackSpace(5f);
        l.setXEntrySpace(7f);
        l.setYEntrySpace(4f);
        l.setYOffset(0f);
        l.getCalculatedLineSizes();

        binding.pieChart.setCenterText("");

        ArrayList<PieEntry> yValues = new ArrayList<>();

//        if (typeFilter.equals("ByType")) {
        Map<String, Double> pond = new HashMap<>();

        statisticsViewModel.filteredList.forEach(e -> {
            if (!pond.containsKey(e.getType().name()))
                pond.put(e.getType().name(), e.getAmount() / (e.getCreditors().size() == 0 ? 1 : e.getCreditors().size()));
            else {
                Double sumAmount = pond.get(e.getType().name());
                pond.replace(e.getType().name(), sumAmount + e.getAmount() / (e.getCreditors().size() == 0 ? 1 : e.getCreditors().size()));
            }

        });

        double finalSum = 0;
        int cnt = 0;
        labelMap.clear();
         for (Map.Entry<String,Double> entry : pond.entrySet()) {
             labelMap.put(cnt, entry.getKey());
             cnt++;
            yValues.add(new PieEntry(entry.getValue().floatValue(), entry.getKey()));
            finalSum += entry.getValue().floatValue();
        }

        binding.pieChart.setCenterText(String.format("Spent amount \n %.2f $", finalSum));
        binding.pieChart.setCenterTextSize(20f);

        binding.pieChart.animateY(1000, Easing.EaseInOutCubic);

        PieDataSet dataSet = new PieDataSet(yValues, "");
        dataSet.setValueFormatter(new PercentFormatter(binding.pieChart));
        binding.pieChart.setUsePercentValues(true);
        dataSet.setSliceSpace(3f);
        dataSet.setSelectionShift(25f);
        dataSet.setColors(ColorTemplate.JOYFUL_COLORS);

        PieData data = new PieData(dataSet);
        data.setValueTextSize(20f);
        data.setValueTextColor(R.color.purple_700);
        data.setDrawValues(true);

        binding.pieChart.setDrawEntryLabels(false);
        binding.pieChart.setData(data);

        binding.pieChart.invalidate();

        binding.pieChart.notifyDataSetChanged();

        binding.pieChart.setOnChartValueSelectedListener(this);

    }


    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == R.id.calendar_button) {
            View menuItemView = requireActivity().findViewById(R.id.calendar_button);
            filterDate(menuItemView);
        }

        return super.onOptionsItemSelected(item);
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    public void filterDate(View v){
        PopupMenu popupMenu = new PopupMenu(getContext(), v);
        Calendar calendar = Calendar.getInstance();
        Date today = calendar.getTime();
        statisticsViewModel.max = today;
        calendar.add(Calendar.DAY_OF_YEAR, -7);
        Date weekStart = calendar.getTime();
        calendar.add(Calendar.DAY_OF_YEAR, 7);
        popupMenu.getMenu().add("Week");
        popupMenu.getMenu().add("Month");
        popupMenu.getMenu().add("Year");

        calendar.add(Calendar.MONTH, -1);
        Date monthStart = calendar.getTime();
        calendar.add(Calendar.MONTH, 1);

        calendar.add(Calendar.YEAR, -1);
        Date yearStart = calendar.getTime();
        calendar.add(Calendar.YEAR, 1);

        popupMenu.setOnMenuItemClickListener(item -> {
            if (item.getTitle().equals("Week")){
                statisticsViewModel.min = weekStart;
                statisticsViewModel.loadExpensesPieChart();
                popupMenu.getMenu().getItem(0).setChecked(true);
                setDate();
            } else if (item.getTitle().equals("Month")){
                statisticsViewModel.min = monthStart;
                statisticsViewModel.loadExpensesPieChart();
                popupMenu.getMenu().getItem(1).setChecked(true);
                setDate();
            } else {
                statisticsViewModel.min = yearStart;
                statisticsViewModel.loadExpensesPieChart();
                popupMenu.getMenu().getItem(2).setChecked(true);
                setDate();
            }
            return true;
        });

        popupMenu.show();
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private void setDate() {
        SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy");
        String minDate = formatter.format(statisticsViewModel.min);
        String maxDAte = formatter.format(statisticsViewModel.max);

        binding.dateFrom.setText(minDate);
        binding.dateTo.setText(maxDAte);
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    public void onSelectTrip(Trip trip) {
        binding.selectTrip.setVisibility(View.GONE);
        binding.tripInfo.setVisibility(View.VISIBLE);
        binding.alltripsSw.setChecked(false);
        binding.tripName.setText(trip.getName());

        if (!TextUtils.isEmpty(trip.getAvatarUri())) {
            Glide.with(BaseApp.context)
                    .load(trip.getAvatarUri())
                    .centerCrop()
                    .placeholder(R.drawable.progress_animation)
                    .into(binding.tripAvatar);
        } else {
            Glide.with(BaseApp.context).clear(binding.tripAvatar);
            binding.tripAvatar.setImageResource(R.drawable.default_trip_back);
        }


//        statisticsViewModel.loadExpenseFromTrip(trip.getId());
        statisticsViewModel.loadExpenseFromTrip(trip.getId());
    }

    @Override
    public void onValueSelected(Entry e, Highlight h) {
        @SuppressLint("WrongConstant") Snackbar snack = Snackbar.make(getView(), String.format("Spent on %s - %.2f $", labelMap.get((int)h.getX()), e.getY()), Snackbar.ANIMATION_MODE_SLIDE);
        View view = snack.getView();
        TextView tv = (TextView) view.findViewById(com.google.android.material.R.id.snackbar_text);
        tv.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
        snack.show();
    }

    @Override
    public void onNothingSelected() {

    }
}

