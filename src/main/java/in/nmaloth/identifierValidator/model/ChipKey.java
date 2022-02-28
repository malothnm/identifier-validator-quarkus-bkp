package in.nmaloth.identifierValidator.model;

import in.nmaloth.payments.constants.Strategy;

import java.util.Objects;

public class ChipKey {
    private String chipVersion;
    private Strategy strategy;

    public ChipKey(String chipVersion, Strategy strategy) {
        this.chipVersion = chipVersion;
        this.strategy = strategy;
    }

    public ChipKey() {
    }

    public static ChipKeyBuilder builder(){
        return new ChipKeyBuilder();
    }

    public String getChipVersion() {
        return chipVersion;
    }


    public void setChipVersion(String chipVersion) {
        this.chipVersion = chipVersion;
    }

    public Strategy getStrategy() {
        return strategy;
    }

    public void setStrategy(Strategy strategy) {
        this.strategy = strategy;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof ChipKey)) return false;
        ChipKey chipKey = (ChipKey) o;
        return getChipVersion().equals(chipKey.getChipVersion()) && getStrategy() == chipKey.getStrategy();
    }

    @Override
    public int hashCode() {
        return Objects.hash(getChipVersion(), getStrategy());
    }

    public static class ChipKeyBuilder{

        private String chipVersion;
        private Strategy strategy;


        public ChipKeyBuilder chipVersion(final String chipVersion){
            this.chipVersion = chipVersion;
            return this;
        }

        public ChipKeyBuilder strategy(Strategy strategy){
            this.strategy = strategy;
            return this;
        }

        public ChipKey build(){
            return new ChipKey(chipVersion,strategy);
        }

    }
}
