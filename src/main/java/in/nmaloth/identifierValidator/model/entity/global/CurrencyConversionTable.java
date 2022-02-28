package in.nmaloth.identifierValidator.model.entity.global;

import in.nmaloth.payments.constants.network.NetworkType;
import io.quarkus.mongodb.panache.common.MongoEntity;
import io.quarkus.mongodb.panache.reactive.ReactivePanacheMongoEntity;
import io.smallrye.mutiny.Uni;
import org.bson.codecs.pojo.annotations.BsonProperty;
import org.bson.types.ObjectId;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@MongoEntity(collection = "cc_table")
public class CurrencyConversionTable extends ReactivePanacheMongoEntity {

    public ObjectId id;

    @BsonProperty("network")
    private String network;

    @BsonProperty("curr_code")
    private String currencyCode;
    @BsonProperty("dest_curr_code")
    private String destCurrencyCode;
    @BsonProperty("conversion_date")
    private LocalDate conversionDate;
    @BsonProperty("conversion_date_time")
    private LocalDateTime conversionDateTime;

    @BsonProperty("src_currency_node")
    private Integer sourceCurrencyNode;

    @BsonProperty("dest_currency_node")
    private Integer destinationCurrencyNode;

    @BsonProperty("buy_rate")
    private Long buyRate;
    @BsonProperty("sell_rate")
    private Long sellRate;
    @BsonProperty("mid_rate")
    private Long midRate;


    public static Uni<List<CurrencyConversionTable>> findAllCurrencyConversionTables(String network, LocalDate conversionDate) {

        return find("network = ?1 && conversion_date = ?2", network, conversionDate).list();

    }


    public String getNetwork() {
        return network;
    }

    public void setNetwork(String network) {
        this.network = network;
    }

    public String getCurrencyCode() {
        return currencyCode;
    }

    public void setCurrencyCode(String currencyCode) {
        this.currencyCode = currencyCode;
    }

    public String getDestCurrencyCode() {
        return destCurrencyCode;
    }

    public void setDestCurrencyCode(String destCurrencyCode) {
        this.destCurrencyCode = destCurrencyCode;
    }

    public LocalDate getConversionDate() {
        return conversionDate;
    }

    public void setConversionDate(LocalDate conversionDate) {
        this.conversionDate = conversionDate;
    }

    public LocalDateTime getConversionDateTime() {
        return conversionDateTime;
    }

    public void setConversionDateTime(LocalDateTime conversionDateTime) {
        this.conversionDateTime = conversionDateTime;
    }

    public Integer getSourceCurrencyNode() {
        return sourceCurrencyNode;
    }

    public void setSourceCurrencyNode(Integer sourceCurrencyNode) {
        this.sourceCurrencyNode = sourceCurrencyNode;
    }

    public Integer getDestinationCurrencyNode() {
        return destinationCurrencyNode;
    }

    public void setDestinationCurrencyNode(Integer destinationCurrencyNode) {
        this.destinationCurrencyNode = destinationCurrencyNode;
    }

    public Long getBuyRate() {
        return buyRate;
    }

    public void setBuyRate(Long buyRate) {
        this.buyRate = buyRate;
    }

    public Long getSellRate() {
        return sellRate;
    }

    public void setSellRate(Long sellRate) {
        this.sellRate = sellRate;
    }

    public Long getMidRate() {
        return midRate;
    }

    public void setMidRate(Long midRate) {
        this.midRate = midRate;
    }

    public static CurrencyConversionTableBuilder builder(){
        return new CurrencyConversionTableBuilder();
    }

    public CurrencyConversionTable(String network, String currencyCode, String destCurrencyCode, LocalDate conversionDate,
                                   LocalDateTime conversionDateTime, Integer sourceCurrencyNode, Integer destinationCurrencyNode,
                                   Long buyRate, Long sellRate, Long midRate) {
        this.network = network;
        this.currencyCode = currencyCode;
        this.destCurrencyCode = destCurrencyCode;
        this.sourceCurrencyNode = sourceCurrencyNode;

        this.destinationCurrencyNode = destinationCurrencyNode;
        if(conversionDate != null){
            this.conversionDate = conversionDate;
        }

        if(conversionDateTime != null){
            this.conversionDateTime = conversionDateTime;
        }
        this.buyRate = buyRate;
        this.sellRate = sellRate;
        this.midRate = midRate;
    }

    public CurrencyConversionTable() {
    }


    public static class CurrencyConversionTableBuilder {

        private NetworkType network;
        private String currencyCode;
        private String destCurrencyCode;
        private LocalDate conversionDate;
        private LocalDateTime conversionDateTime;
        private Integer sourceCurrencyNode;
        private Integer destinationCurrencyNode;
        private long buyRate;
        private long sellRate;
        private long midRate;

        CurrencyConversionTableBuilder() {
        }


        public CurrencyConversionTableBuilder network(final NetworkType network) {
            this.network = network;
            return this;
        }

        public CurrencyConversionTableBuilder currencyCode(final String currencyCode) {
            this.currencyCode = currencyCode;
            return this;
        }


        public CurrencyConversionTableBuilder destCurrencyCode(final String destCurrencyCode) {
            this.destCurrencyCode = destCurrencyCode;
            return this;
        }


        public CurrencyConversionTableBuilder conversionDate(final LocalDate conversionDate) {
            this.conversionDate = conversionDate;
            return this;
        }


        public CurrencyConversionTableBuilder conversionDateTime(final LocalDateTime conversionDateTime) {
            this.conversionDateTime = conversionDateTime;
            return this;
        }

        public CurrencyConversionTableBuilder sourceCurrencyNode(final Integer sourceCurrencyNode){
            this.sourceCurrencyNode = sourceCurrencyNode;
            return this;
        }

        public CurrencyConversionTableBuilder destinationCurrencyNode(final Integer destinationCurrencyNode){
            this.destinationCurrencyNode = destinationCurrencyNode;
            return this;
        }

        public CurrencyConversionTableBuilder buyRate(final long buyRate) {
            this.buyRate = buyRate;
            return this;
        }

        public CurrencyConversionTableBuilder sellRate(final long sellRate) {
            this.sellRate = sellRate;
            return this;
        }

        public CurrencyConversionTableBuilder midRate(final long midRate) {
            this.midRate = midRate;
            return this;
        }

        public CurrencyConversionTable build() {
            return new CurrencyConversionTable(this.network.getNetworkType(),this.currencyCode,this.destCurrencyCode,this.conversionDate,
                    this.conversionDateTime, this.sourceCurrencyNode,this.destinationCurrencyNode,this.buyRate, this.sellRate, this.midRate);
        }

    }

}
