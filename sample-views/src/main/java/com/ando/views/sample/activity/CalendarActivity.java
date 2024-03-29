package com.ando.views.sample.activity;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;

import com.ando.views.calendar.CustomCalendar;
import com.ando.views.sample.R;
import com.ando.views.sample.common.MainActivity;
import com.haibin.calendarview.Calendar;
import com.haibin.calendarview.CalendarLayout;
import com.haibin.calendarview.CalendarView;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

/**
 * Title: CalendarActivity
 * <p>
 * Description:CalendarView使用案例 https://github.com/huanghaibin-dev/CalendarView
 * </p>
 *
 * @author Changbao
 * @date 2019/1/7  16:32
 */
public class CalendarActivity extends BaseActivity implements
        CalendarView.OnCalendarSelectListener,
        CalendarView.OnYearChangeListener {
    //1.
    private CustomCalendar mCalendar;
    //2.
    private TextView mTextMonthDay;
    private ImageView mIvPrevMonth;
    private ImageView mIvNextMonth;
    private int mYear;
    private CalendarLayout mCalendarLayout;
    private CalendarView mCalendarView;

    public static void show(Context context) {
        context.startActivity(new Intent(context, CalendarActivity.class));
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_calendar);
        initView();
        initData();
    }

    protected void initView() {
        mTextMonthDay = (TextView) findViewById(R.id.tv_current_year_month);

        mIvPrevMonth = findViewById(R.id.iv_month_sub);
        mIvNextMonth = findViewById(R.id.iv_month_add);
        mCalendarView = (CalendarView) findViewById(R.id.calendarView);
        mTextMonthDay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!mCalendarLayout.isExpand()) {
                    mCalendarView.showYearSelectLayout(mYear);
                    return;
                }
                mCalendarView.showYearSelectLayout(mYear);
                mTextMonthDay.setText(String.valueOf(mYear));
            }
        });

        mIvPrevMonth.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mCalendarView.scrollToPre(true);
            }
        });
        mIvNextMonth.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mCalendarView.scrollToNext(true);
            }
        });
        mCalendarLayout = (CalendarLayout) findViewById(R.id.calendarLayout);
        mCalendarView.setOnCalendarSelectListener(this);
        mCalendarView.setOnYearChangeListener(this);
        mYear = mCalendarView.getCurYear();
        mTextMonthDay.setText(String.format(Locale.getDefault(),"%d月%d日", mCalendarView.getCurMonth(), mCalendarView.getCurDay()));


        //自定义日历
        mCalendar = findViewById(R.id.custom_calendar);
        //日历事件列表
        mCalendar.setCallBack(new CustomCalendar.CallBack() {
            @Override
            public void onEventListClick() {
                Toast.makeText(CalendarActivity.this, "查看事件", Toast.LENGTH_SHORT).show();
            }
        });
        //日历月份切换
        mCalendar.setOnMonthChangeListener(new CalendarView.OnMonthChangeListener() {
            @Override
            public void onMonthChange(int year, int month) {
//                mCalendar.setMonthViewScrollable(isNettingBack);//防止cancel
                Toast.makeText(CalendarActivity.this, String.format(Locale.getDefault(),"日历月份切换: %d  %d", year, month), Toast.LENGTH_SHORT).show();
            }
        });

    }

    protected void initData() {
        int year = mCalendarView.getCurYear();
        int month = mCalendarView.getCurMonth();

        Map<String, Calendar> map = new HashMap<>();
        map.put(getSchemeCalendar(year, month, 3, 0xFF40db25, "假").toString(),
                getSchemeCalendar(year, month, 3, 0xFF40db25, "假"));
        map.put(getSchemeCalendar(year, month, 6, 0xFFe69138, "事").toString(),
                getSchemeCalendar(year, month, 6, 0xFFe69138, "事"));
        map.put(getSchemeCalendar(year, month, 9, 0xFFdf1356, "议").toString(),
                getSchemeCalendar(year, month, 9, 0xFFdf1356, "议"));
        map.put(getSchemeCalendar(year, month, 13, 0xFFedc56d, "记").toString(),
                getSchemeCalendar(year, month, 13, 0xFFedc56d, "记"));
        map.put(getSchemeCalendar(year, month, 14, 0xFFedc56d, "记").toString(),
                getSchemeCalendar(year, month, 14, 0xFFedc56d, "记"));
        map.put(getSchemeCalendar(year, month, 15, 0xFFaacc44, "假").toString(),
                getSchemeCalendar(year, month, 15, 0xFFaacc44, "假"));
        map.put(getSchemeCalendar(year, month, 18, 0xFFbc13f0, "记").toString(),
                getSchemeCalendar(year, month, 18, 0xFFbc13f0, "记"));
        map.put(getSchemeCalendar(year, month, 25, 0xFF13acf0, "假").toString(),
                getSchemeCalendar(year, month, 25, 0xFF13acf0, "假"));
        map.put(getSchemeCalendar(year, month, 27, 0xFF13acf0, "多").toString(),
                getSchemeCalendar(year, month, 27, 0xFF13acf0, "多"));
        //此方法在巨大的数据量上不影响遍历性能，推荐使用
        mCalendarView.setSchemeDate(map);
    }

    private Calendar getSchemeCalendar(int year, int month, int day, int color, String text) {
        Calendar calendar = new Calendar();
        calendar.setYear(year);
        calendar.setMonth(month);
        calendar.setDay(day);
        calendar.setSchemeColor(color);//如果单独标记颜色、则会使用这个颜色
        calendar.setScheme(text);
        calendar.addScheme(new Calendar.Scheme());
        calendar.addScheme(0xFF008800, "假");
        calendar.addScheme(0xFF008800, "节");
        return calendar;
    }

    @Override
    public void onCalendarOutOfRange(Calendar calendar) {

    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onCalendarSelect(Calendar calendar, boolean isClick) {
        mTextMonthDay.setText(calendar.getMonth() + "月" + calendar.getDay() + "日");
        mYear = calendar.getYear();
    }

    @Override
    public void onYearChange(int year) {
        mTextMonthDay.setText(String.valueOf(year));
    }

}
