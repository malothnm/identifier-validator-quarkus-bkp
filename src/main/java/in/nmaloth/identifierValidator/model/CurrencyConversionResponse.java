package in.nmaloth.identifierValidator.model;

public class CurrencyConversionResponse {

    private String response;
    private String billCurrencyCode;
    private Long billAmount;
    private long midRate;
    private long buyRate;
    private long sellRate;


    public String getResponse() {
        return response;
    }

    public void setResponse(String response) {
        this.response = response;
    }

    public String getBillCurrencyCode() {
        return billCurrencyCode;
    }

    public void setBillCurrencyCode(String billCurrencyCode) {
        this.billCurrencyCode = billCurrencyCode;
    }

    public Long getBillAmount() {
        return billAmount;
    }

    public void setBillAmount(Long billAmount) {
        this.billAmount = billAmount;
    }

    public long getMidRate() {
        return midRate;
    }

    public void setMidRate(long midRate) {
        this.midRate = midRate;
    }

    public long getBuyRate() {
        return buyRate;
    }

    public void setBuyRate(long buyRate) {
        this.buyRate = buyRate;
    }

    public long getSellRate() {
        return sellRate;
    }

    public void setSellRate(long sellRate) {
        this.sellRate = sellRate;
    }
}
