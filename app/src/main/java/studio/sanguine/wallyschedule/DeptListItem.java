package studio.sanguine.wallyschedule;

/**
 * Created by mafia on 11/21/2017.
 */

public class DeptListItem {
    private String deptNumber;
    private String deptName;
    public DeptListItem(String deptNumber, String deptName){
        setDeptName(deptName);
        setDeptNumber(deptNumber);
    }

    public String getDeptNumber() {
        return deptNumber;
    }

    public void setDeptNumber(String deptNumber) {
        this.deptNumber = deptNumber;
    }

    public String getDeptName() {
        return deptName;
    }

    public void setDeptName(String deptName) {
        this.deptName = deptName;
    }

    @Override
    public String toString(){
        return deptNumber;
    }
}
