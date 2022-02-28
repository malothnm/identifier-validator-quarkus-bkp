package in.nmaloth.identifierValidator.model;

import java.time.LocalDate;
import java.util.Objects;

public class CurrencyConversionKeyDaily {

    private String currencyCode;
    private String destinationCurrencyCode;
    private LocalDate conversionDate;


    public CurrencyConversionKeyDaily(String currencyCode, String destinationCurrencyCode, LocalDate conversionDate) {
        this.currencyCode = currencyCode;
        this.destinationCurrencyCode = destinationCurrencyCode;
        this.conversionDate = conversionDate;
    }

    public CurrencyConversionKeyDaily() {
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof CurrencyConversionKeyDaily)) return false;
        CurrencyConversionKeyDaily that = (CurrencyConversionKeyDaily) o;
        return getCurrencyCode().equals(that.getCurrencyCode()) &&
                getDestinationCurrencyCode().equals(that.getDestinationCurrencyCode()) &&
                getConversionDate().equals(that.getConversionDate());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getCurrencyCode(), getDestinationCurrencyCode(), getConversionDate());
    }

    public String getCurrencyCode() {
        return currencyCode;
    }

    public void setCurrencyCode(String currencyCode) {
        this.currencyCode = currencyCode;
    }

    public String getDestinationCurrencyCode() {
        return destinationCurrencyCode;
    }

    public void setDestinationCurrencyCode(String destinationCurrencyCode) {
        this.destinationCurrencyCode = destinationCurrencyCode;
    }

    public LocalDate getConversionDate() {
        return conversionDate;
    }

    public void setConversionDate(LocalDate conversionDate) {
        this.conversionDate = conversionDate;
    }

    public static CurrencyConversionKeyDailyBuilder builder(){
        return new CurrencyConversionKeyDailyBuilder();
    }

    public static class CurrencyConversionKeyDailyBuilder {


        private String currencyCode;
        private String destinationCurrencyCode;
        private LocalDate conversionDate;

        public CurrencyConversionKeyDailyBuilder currencyCode(String currencyCode){
            this.currencyCode = currencyCode;
            return this;
        }

        public CurrencyConversionKeyDailyBuilder destinationCurrencyCode(String destinationCurrencyCode){
            this.destinationCurrencyCode = destinationCurrencyCode;
            return this;
        }

        public CurrencyConversionKeyDailyBuilder conversionDate(LocalDate conversionDate){
            this.conversionDate = conversionDate;
            return this;
        }

        public CurrencyConversionKeyDaily build(){
            return new CurrencyConversionKeyDaily(currencyCode,destinationCurrencyCode,conversionDate);
        }


    }
}
