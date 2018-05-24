package studio.sanguine.wallyschedule;

/**
 * Created by mafia on 8/25/2017.
 */

public class TaxItem {
    private double _price = 0;
    private double _taxRate = 0;
    private boolean _isPercent = true;
    private boolean _replaceMainTax = false;
    private int _quantity = 0;
    private double _discount = 0;
    private boolean _discountPercent = true;
    private String _taxOption = "";

    //the old FinalPrice had only 3 custom taxes hardcoded, so we needed fewer vars and switch statement.
    //TaxCalc has a list of taxes, so we're passing the tax options here instead of using switch.
    public TaxItem(double _price, double _taxRate, boolean _isPercent, boolean _replaceMainTax,
                   int _quantity, double _discount, boolean _discountPercent, String _taxOption){
        set_discount(_discount);
        set_discountPercent(_discountPercent);
        set_isPercent(_isPercent);
        set_price(_price);
        set_quantity(_quantity);
        set_replaceMainTax(_replaceMainTax);
        set_taxRate(_taxRate);
        set_taxOption(_taxOption);
    }

    public double get_price() {
        return _price;
    }

    public void set_price(double _price) {
        this._price = _price;
    }

    public double get_taxRate() {
        return _taxRate;
    }

    public void set_taxRate(double _taxRate) {
        this._taxRate = _taxRate;
    }

    public boolean is_isPercent() {
        return _isPercent;
    }

    public void set_isPercent(boolean _isPercent) {
        this._isPercent = _isPercent;
    }

    public boolean is_replaceMainTax() {
        return _replaceMainTax;
    }

    public void set_replaceMainTax(boolean _replaceMainTax) {
        this._replaceMainTax = _replaceMainTax;
    }

    public int get_quantity() {
        return _quantity;
    }

    public void set_quantity(int _quantity) {
        this._quantity = _quantity;
    }

    public double get_discount() {
        return _discount;
    }

    public void set_discount(double _discount) {
        this._discount = _discount;
    }

    public boolean is_discountPercent() {
        return _discountPercent;
    }

    public void set_discountPercent(boolean _discountPercent) {
        this._discountPercent = _discountPercent;
    }


    public String get_taxOption() {
        return _taxOption;
    }

    public void set_taxOption(String _taxOption) {
        this._taxOption = _taxOption;
    }
}
