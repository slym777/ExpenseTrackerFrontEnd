package com.example.expensetracker.ui.statistics;

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
import android.widget.CompoundButton;
import android.widget.PopupMenu;

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
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.formatter.PercentFormatter;
import com.github.mikephil.charting.utils.ColorTemplate;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class StatisticsFragment extends Fragment implements OnSelectTripListener {

    private StatisticsViewModel statisticsViewModel;
    private FragmentStatisticsBinding binding;
    private Date min, max;
    private String typeFilter = "ByType";

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        statisticsViewModel =
                new ViewModelProvider(this).get(StatisticsViewModel.class);
        binding = FragmentStatisticsBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        setHasOptionsMenu(true);

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

        Calendar calendar = Calendar.getInstance();
        Date weekFinish = calendar.getTime();
        calendar.add(Calendar.DAY_OF_YEAR, -7);
        Date weekStart = calendar.getTime();
        min = weekStart;
        max = weekFinish;

        binding.switch1.setChecked(true);

        statisticsViewModel.allExpenseLiveList.observe(getViewLifecycleOwner(), creditList -> {
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

        binding.switch1.setOnCheckedChangeListener((compoundButton, b) -> {
            if (b) {
                statisticsViewModel.loadExpensesPieChart(min, max);
                plotPieChart();
            }
        });

    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    public void onResume() {
        super.onResume();
        statisticsViewModel.loadExpensesPieChart(min, max);
    }

    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        inflater.inflate(R.menu.statistics_menu, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    public void plotPieChart() {

        ArrayList<PieEntry> yValues = new ArrayList<>();

//        if (typeFilter.equals("ByType")) {
        Map<String, Double> pond = new HashMap<>();

        statisticsViewModel.expenseList.forEach(e -> {
            if (!pond.containsKey(e.getType().name()))
                pond.put(e.getType().name(), e.getAmount() / e.getCreditors().size());
            else {
                Double sumAmount = pond.get(e.getType().name());
                pond.replace(e.getType().name(), sumAmount + e.getAmount() / e.getCreditors().size());
            }

        });

        for (Map.Entry<String,Double> entry : pond.entrySet())
        yValues.add(new PieEntry(entry.getValue().floatValue(), entry.getKey()));
//

//        }

//        } else if (typeFilter.equals("Hubs")) {
//
//            Map<String, Double> pond = new HashMap<>();
//
//            graphicViewModel.creditList.forEach(d -> {
//                if (!pond.containsKey(d.getHub().getName()))
//                    pond.put(d.getHub().getName(), d.getAmount());
//                else {
//                    Double sumAmount = pond.get(d.getHub().getName());
//                    pond.replace(d.getHub().getName(), sumAmount + d.getAmount());
//                }
//            });
//
//            graphicViewModel.debitList.forEach(d -> {
//                if (!pond.containsKey(d.getHub().getName()))
//                    pond.put(d.getHub().getName(), d.getAmount());
//                else {
//                    Double sumAmount = pond.get(d.getHub().getName());
//                    pond.replace(d.getHub().getName(), sumAmount + d.getAmount());
//                }
//            });
//
//            for (Map.Entry<String,Double> entry : pond.entrySet())
//                yValues.add(new PieEntry(entry.getValue().floatValue(), entry.getKey()));
//
//        } else {
//
//            Map<String, Double> pond = new HashMap<>();
//
//            graphicViewModel.creditList.forEach(d -> {
//                if (!pond.containsKey(d.getCurrency()))
//                    pond.put(d.getCurrency(), d.getAmount());
//                else {
//                    Double sumAmount = pond.get(d.getCurrency());
//                    pond.replace(d.getCurrency(), sumAmount + d.getAmount());
//                }
//            });
//
//            graphicViewModel.debitList.forEach(d -> {
//                if (!pond.containsKey(d.getCurrency()))
//                    pond.put(d.getCurrency(), d.getAmount());
//                else {
//                    Double sumAmount = pond.get(d.getCurrency());
//                    pond.replace(d.getCurrency(), sumAmount + d.getAmount());
//                }
//            });
//
//            for (Map.Entry<String, Double> entry : pond.entrySet())
//                yValues.add(new PieEntry(entry.getValue().floatValue(), entry.getKey()));
//
//        }

        binding.pieChart.animateY(1000, Easing.EaseInOutCubic);

        PieDataSet dataSet = new PieDataSet(yValues, "");
        dataSet.setValueFormatter(new PercentFormatter(binding.pieChart));
        binding.pieChart.setUsePercentValues(true);
        dataSet.setSliceSpace(3f);
        dataSet.setSelectionShift(25f);
        dataSet.setColors(ColorTemplate.JOYFUL_COLORS);

        PieData data = new PieData(dataSet);
        data.setValueTextSize(20f);
        data.setValueTextColor(Color.BLUE);

        binding.pieChart.setDrawEntryLabels(false);
        binding.pieChart.setData(data);

    }


    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == R.id.calendar_button) {
            View menuItemView = requireActivity().findViewById(R.id.calendar_button);
            filterDate(menuItemView);
        }

        return super.onOptionsItemSelected(item);
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    public void filterDate(View v){
        PopupMenu popupMenu = new PopupMenu(getContext(), v);
        Calendar calendar = Calendar.getInstance();
        Date today = calendar.getTime();
        max = today;
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
                min = weekStart;
                statisticsViewModel.loadExpensesPieChart(min, max);
                popupMenu.getMenu().getItem(0).setChecked(true);
//                Toast.makeText(getContext(), "Week was selected", Toast.LENGTH_SHORT).show();
            } else if (item.getTitle().equals("Month")){
                min = monthStart;
                statisticsViewModel.loadExpensesPieChart(min, max);
                popupMenu.getMenu().getItem(1).setChecked(true);
//                Toast.makeText(getContext(), "Month was selected", Toast.LENGTH_SHORT).show();
            } else {
                min = yearStart;
                statisticsViewModel.loadExpensesPieChart(min, max);
                popupMenu.getMenu().getItem(2).setChecked(true);
//                Toast.makeText(getContext(), "Year was selected", Toast.LENGTH_SHORT).show();
            }
            return true;
        });

        popupMenu.show();
    }

//    @RequiresApi(api = Build.VERSION_CODES.N)
//    public void filterType(View v){
//        PopupMenu popupMenu = new PopupMenu(getContext(), v);
//        popupMenu.getMenu().add("ByType");
//        popupMenu.getMenu().add("ByTrips");
//
//        popupMenu.setOnMenuItemClickListener(item -> {
//            if (item.getTitle().equals("ByType")){
//                typeFilter = item.getTitle().toString();
//                plotPieChart();
//                popupMenu.getMenu().getItem(0).setChecked(true);
//                popupMenu.getMenu().getItem(0).setIcon(R.drawable.circleyellowfil);
//            } else if (item.getTitle().equals("ByTrips")){
//                typeFilter = item.getTitle().toString();
//                plotPieChart();
//                popupMenu.getMenu().getItem(1).setChecked(true);
//            } else {
//                typeFilter = item.getTitle().toString();
//                plotPieChart();
//            }
//            return true;
//        });
//
//        popupMenu.show();
//    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    public void onSelectTrip(Trip trip) {
        binding.selectTrip.setVisibility(View.GONE);
        binding.tripInfo.setVisibility(View.VISIBLE);
        binding.switch1.setChecked(false);
        binding.tripName.setText(trip.getName());

        if (!TextUtils.isEmpty(trip.getAvatarUri())) {
            Glide.with(BaseApp.context)
                    .load(trip.getAvatarUri())
                    .centerCrop()
                    .placeholder(R.drawable.progress_animation)
                    .into(binding.tripAvatar);
        }

        statisticsViewModel.setExpenseList(trip.getExpenses());
        plotPieChart();
    }
}

