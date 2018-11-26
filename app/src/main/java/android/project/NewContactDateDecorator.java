package android.project;

import android.content.Context;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.text.style.ForegroundColorSpan;
import android.text.style.RelativeSizeSpan;
import android.text.style.StyleSpan;
import android.util.Log;
import android.widget.CalendarView;

import com.prolificinteractive.materialcalendarview.CalendarDay;
import com.prolificinteractive.materialcalendarview.DayViewDecorator;
import com.prolificinteractive.materialcalendarview.DayViewFacade;

import java.util.HashSet;
import java.util.List;

public class NewContactDateDecorator implements DayViewDecorator {
    private Drawable highlightDrawable;
    private Context context;
    private Handler handler;
    private List<ContactInformation> contacts;
    private final HashSet<CalendarDay> dates;

    public NewContactDateDecorator( Context context) {
        ///////when dealing with calendar days the months go from 0 - 11///////
        this.context = context;
        this.dates = new HashSet<>();
        handler = new Handler(context);
        contacts = handler.read();
        for(ContactInformation c : contacts){
            //getting original date from stored contact in format "CalendarDay{YYYY-MM-DD}"
            String tempDate = c.getDate().substring(12,c.getDate().length()-1);
            String tempYear = tempDate.substring(0,4);
            int year = Integer.parseInt(tempYear);
            String tempMonth = tempDate.substring(5,7);
            int month = Integer.parseInt(tempMonth);
            String tempDay = tempDate.substring(8,tempDate.length());
            int day = Integer.parseInt(tempDay);
            CalendarDay date = CalendarDay.from(year,month,day);
            dates.add(date);
            //Log.i("DATE",date.toString());
        }
    }

    //returns true if the user added a contact on the date passed
    public boolean isDateImportant(CalendarDay day){
        if(dates.contains(day))
            return true;
        else
            return false;
    }

    //returns number of contacts met in month of passed day
    public int contactsAdded(CalendarDay day){
        int count = 0;
        int month = day.getMonth();
        for(ContactInformation c : contacts){
            if(month == day.getMonth()){
                count++;
            }
        }
        return count;
    }

    //scrolls through contacts and returns the name of contacts met on passed date
    public String getNames(CalendarDay day){
        int count = 0;
        String displayText = "";
        for(ContactInformation c : contacts){
            if(c.getDate().equals(day.toString())){
                if(count == 0){
                    displayText = c.getName();
                    count++;
                }
                else{
                    displayText = displayText + ", " + c.getName();
                }
            }
        }
        return displayText;
    }

    @Override
    public boolean shouldDecorate(CalendarDay day) {
        return dates.contains(day);
    }

    @Override
    public void decorate(DayViewFacade view) {
        view.addSpan(new ForegroundColorSpan(Color.MAGENTA));
        view.addSpan(new RelativeSizeSpan(1.2f));
        view.addSpan(new StyleSpan(Typeface.BOLD));
    }
}