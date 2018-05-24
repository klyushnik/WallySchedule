package studio.sanguine.wallyschedule;

/**
 * Created by mafia on 11/15/2017.
 */

public class TaxOption {
    private String name;
    private double taxRate;
    private boolean isPercent;
    private boolean replaceMainTax;
    public TaxOption(String _name, double _taxRate, boolean _isPercent, boolean _replaceMainTax){
        setName(_name);
        setPercent(_isPercent);
        setReplaceMainTax(_replaceMainTax);
        setTaxRate(_taxRate);
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public double getTaxRate() {
        return taxRate;
    }

    public void setTaxRate(double taxRate) {
        this.taxRate = taxRate;
    }

    public boolean isPercent() {
        return isPercent;
    }

    public void setPercent(boolean percent) {
        isPercent = percent;
    }

    public boolean isReplaceMainTax() {
        return replaceMainTax;
    }

    public void setReplaceMainTax(boolean replaceMainTax) {
        this.replaceMainTax = replaceMainTax;
    }

    @Override
    public String toString(){
        return name + " (" + String.valueOf(taxRate) + (isPercent ? "%)" : "$)");
    }
}
