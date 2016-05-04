package pro.documentum.jdo.query;

public class DQLQueryCompilation {

    private String _dqlText;

    private boolean _filterComplete = true;

    private boolean _orderComplete = true;

    private boolean _resultComplete = true;

    private boolean _rangeComplete = true;

    private boolean _precompilable = true;

    public DQLQueryCompilation() {
        super();
    }

    public boolean isPrecompilable() {
        return _precompilable;
    }

    public void setPrecompilable(final boolean flag) {
        _precompilable = flag;
    }

    public void setDqlText(final String text) {
        _dqlText = text;
    }

    public String getDqlText() {
        return _dqlText;
    }

    public boolean isFilterComplete() {
        return _filterComplete;
    }

    public boolean isResultComplete() {
        return _resultComplete;
    }

    public boolean isOrderComplete() {
        return _orderComplete;
    }

    public boolean isRangeComplete() {
        return _rangeComplete;
    }

    public void setFilterComplete(final boolean complete) {
        _filterComplete = complete;
    }

    public void setOrderComplete(final boolean complete) {
        _orderComplete = complete;
    }

    public void setResultComplete(final boolean complete) {
        _resultComplete = complete;
    }

    public void setRangeComplete(final boolean complete) {
        _rangeComplete = complete;
    }

}
