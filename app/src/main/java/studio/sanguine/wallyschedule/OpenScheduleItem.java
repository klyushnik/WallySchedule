package studio.sanguine.wallyschedule;

/**
 * Created by mafia on 8/25/2017.
 */

public class OpenScheduleItem {

    private String _jobDescription = "";
    private String _shiftTime = "";

    public OpenScheduleItem(String JobDesc, String ShiftTime){
        set_jobDescription(JobDesc);
        set_shiftTime(ShiftTime);
    }

    public String get_jobDescription() {
        if (_jobDescription.equals("")) return "DM - SALESFLOOR";
        return _jobDescription;
    }

    public void set_jobDescription(String _jobDescription) {
        this._jobDescription = _jobDescription;
    }

    public String get_shiftTime() {
        if (_shiftTime.equals("")) return "07:00am - 04:00pm";
        return _shiftTime;
    }

    public void set_shiftTime(String _shiftTime) {
        this._shiftTime = _shiftTime;
    }
}
