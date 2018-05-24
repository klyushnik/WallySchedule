package studio.sanguine.wallyschedule;

import android.content.Context;

/**
 * Created by mafia on 7/25/2017.
 */

public final class ScheduleCellSingle {

    //variables
    private String DayOfMonth = "0";
    private String DayOfWeek;
    private String ShiftTime;
    private String MealTime;
    private String JobDescription;
    private String DateCode;
    private Context context;
    public boolean isNotScheduledCell;
    public boolean isOpenShift;

    //constructor
    public ScheduleCellSingle(Context _context, String _dataDate, String _dayOfWeek, String _shiftTime, String _mealTime, String _jobDescription, boolean _openShift){
        context = _context;
        setDayOfMonth(_dataDate);
        setDayOfWeek(_dayOfWeek);
        setJobDescription(_jobDescription);
        setMealTime(_mealTime);
        setShiftTime(_shiftTime);
        setDateCode(_dataDate);
        isNotScheduledCell = false;
        isOpenShift = _openShift;
    }

    public ScheduleCellSingle(Context _context, String _dateCode, String _dayOfWeek, boolean _openShift){
        context = _context;
        setDayOfWeek(_dayOfWeek);
        setDayOfMonth(_dateCode);
        setDateCode(_dateCode);
        isNotScheduledCell = true;
        isOpenShift = _openShift;
    }

    //get-set for every variable
    //return a John Doe persona if everything is empty - I'll use it for prototyping
    public String getDayOfMonth() {
        if (DayOfMonth.equals("0")) return "00";
        return DayOfMonth;
    }

    public void setDayOfMonth(String dateCode) {
        if (dateCode == null || dateCode == ""){
            DayOfMonth = "0";
            return;
        }
        DayOfMonth = dateCode.substring(6);
    }

    public String getDayOfWeek() {
        if (DayOfWeek.equals("")){
            String string = context.getString(R.string.main_wednesday);
            return string;
        }
        return DayOfWeek;
    }

    public void setDayOfWeek(String dayOfWeek) {
        switch (dayOfWeek){
            case "MON":
                DayOfWeek = context.getString(R.string.main_monday);
                break;
            case "TUE":
                DayOfWeek = context.getString(R.string.main_tuesday);
                break;
            case "WED":
                DayOfWeek = context.getString(R.string.main_wednesday);
                break;
            case "THU":
                DayOfWeek = context.getString(R.string.main_thursday);
                break;
            case "FRI":
                DayOfWeek = context.getString(R.string.main_friday);
                break;
            case "SAT":
                DayOfWeek = context.getString(R.string.main_saturday);
                break;
            case "SUN":
                DayOfWeek = context.getString(R.string.main_sunday);
                break;
            default:
                DayOfWeek = "";
                break;
        }
    }


    public String getShiftTime() {
        if (ShiftTime.equals("")) return "6:30am - 10:30pm";
        return ShiftTime;
    }

    public void setShiftTime(String shiftTime) {
        ShiftTime = shiftTime;
    }

    public String getMealTime() {
        if (MealTime.equals("")) return "-none-";
        return MealTime;
    }

    public void setMealTime(String mealTime) {
        MealTime = mealTime;
    }

    public String getJobDescription() {
        if (JobDescription.equals("")) return "Mafia Associate";
        return JobDescription;
    }

    public void setJobDescription(String jobDescription) {
        JobDescription = jobDescription;
    }

    public String getDateCode() {
        return (DateCode == null || DateCode.equals("")) ? "00000000" : DateCode;
    }

    public void setDateCode(String dateCode) {
        DateCode = dateCode;
    }

    public String getOpenScheduleTimeCode(){
        return DateCode.substring(0,4) + "-" + DateCode.substring(4,6) + "-" + DateCode.substring(6,8);
    }
}
