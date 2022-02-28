package in.nmaloth.identifierValidator.model.entity.global;


import in.nmaloth.payments.constants.Strategy;
import io.quarkus.mongodb.panache.common.MongoEntity;
import io.quarkus.mongodb.panache.reactive.ReactivePanacheMongoEntity;
import io.smallrye.mutiny.Uni;
import org.bson.codecs.pojo.annotations.BsonProperty;

import java.util.Optional;

@MongoEntity(collection = "chip_params")
public class ChipParameters extends ReactivePanacheMongoEntity {

    @BsonProperty("chip_version")
    private String chipVersion;
    @BsonProperty("strategy")
    private String strategy;
    @BsonProperty("allow_tvr")
    private boolean decideOnTvr;
    @BsonProperty("tvr_length")
    private Integer tvrLength;
    @BsonProperty("tvr_approve_decline")
    private byte[] tvrApproveDecline;
    @BsonProperty("allow_cvr")
    private boolean decideOnCvr;
    @BsonProperty("cvr_length")
    private int cvrLength;
    @BsonProperty("cvr_approve_decline")
    private byte[] cvrApproveDecline;


    public ChipParameters(String chipVersion, String strategy, boolean decideOnTvr, Integer tvrLength, byte[] tvrApproveDecline, boolean decideOnCvr, int cvrLength, byte[] cvrApproveDecline) {
        this.chipVersion = chipVersion;
        this.strategy = strategy;
        this.decideOnTvr = decideOnTvr;
        this.tvrLength = tvrLength;
        this.tvrApproveDecline = tvrApproveDecline;
        this.decideOnCvr = decideOnCvr;
        this.cvrLength = cvrLength;
        this.cvrApproveDecline = cvrApproveDecline;
    }

    public ChipParameters() {
    }


    public static Uni<Optional<ChipParameters>> findBYChipVersionAndStrategy(String chipVersion, Strategy strategy) {
        return find("chip_version =?1 && strategy = ?2", chipVersion, strategy.getStrategy()).firstResultOptional();
    }

    public String getChipVersion() {
        return chipVersion;
    }

    public void setChipVersion(String chipVersion) {
        this.chipVersion = chipVersion;
    }

    public String getStrategy() {
        return strategy;
    }

    public void setStrategy(String strategy) {
        this.strategy = strategy;
    }

    public boolean isDecideOnTvr() {
        return decideOnTvr;
    }

    public void setDecideOnTvr(boolean decideOnTvr) {
        this.decideOnTvr = decideOnTvr;
    }

    public Integer getTvrLength() {
        return tvrLength;
    }

    public void setTvrLength(Integer tvrLength) {
        this.tvrLength = tvrLength;
    }

    public byte[] getTvrApproveDecline() {
        return tvrApproveDecline;
    }

    public void setTvrApproveDecline(byte[] tvrApproveDecline) {
        this.tvrApproveDecline = tvrApproveDecline;
    }

    public boolean isDecideOnCvr() {
        return decideOnCvr;
    }

    public void setDecideOnCvr(boolean decideOnCvr) {
        this.decideOnCvr = decideOnCvr;
    }

    public int getCvrLength() {
        return cvrLength;
    }

    public void setCvrLength(int cvrLength) {
        this.cvrLength = cvrLength;
    }

    public byte[] getCvrApproveDecline() {
        return cvrApproveDecline;
    }

    public void setCvrApproveDecline(byte[] cvrApproveDecline) {
        this.cvrApproveDecline = cvrApproveDecline;
    }

    public static ChipParametersBuilder builder() {
        return new ChipParametersBuilder();
    }

    public static class ChipParametersBuilder {
        private String chipVersion;
        private Strategy strategy;
        private boolean decideOnTvr;
        private int tvrLength;
        private byte[] tvrApproveDecline;
        private boolean decideOnCvr;
        private int cvrLength;
        private byte[] cvrApproveDecline;

        ChipParametersBuilder() {
        }

        public ChipParametersBuilder chipVersion(final String chipVersion) {
            this.chipVersion = chipVersion;
            return this;
        }

        public ChipParametersBuilder strategy(final Strategy strategy) {
            this.strategy = strategy;
            return this;
        }

        public ChipParametersBuilder decideOnTvr(final boolean decideOnTvr) {
            this.decideOnTvr = decideOnTvr;
            return this;
        }

        public ChipParametersBuilder tvrLength(final int tvrLength) {
            this.tvrLength = tvrLength;
            return this;
        }

        public ChipParametersBuilder tvrApproveDecline(final byte[] tvrApproveDecline) {
            this.tvrApproveDecline = tvrApproveDecline;
            return this;
        }

        public ChipParametersBuilder decideOnCvr(final boolean decideOnCvr) {
            this.decideOnCvr = decideOnCvr;
            return this;
        }

        public ChipParametersBuilder cvrLength(final int cvrLength) {
            this.cvrLength = cvrLength;
            return this;
        }

        public ChipParametersBuilder cvrApproveDecline(final byte[] cvrApproveDecline) {
            this.cvrApproveDecline = cvrApproveDecline;
            return this;
        }

        public ChipParameters build() {
            return new ChipParameters(this.chipVersion, this.strategy.getStrategy(), this.decideOnTvr, this.tvrLength, this.tvrApproveDecline, this.decideOnCvr, this.cvrLength, this.cvrApproveDecline);
        }

    }
}
