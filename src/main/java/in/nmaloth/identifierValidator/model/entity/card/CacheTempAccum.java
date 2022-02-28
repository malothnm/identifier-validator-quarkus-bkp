package in.nmaloth.identifierValidator.model.entity.card;


import java.util.Objects;

public class CacheTempAccum {


    private String accumType;
    private long accumAmount;
    private int accumCount;

    public CacheTempAccum(String accumType, long accumAmount, int accumCount) {
        this.accumType = accumType;
        this.accumAmount = accumAmount;
        this.accumCount = accumCount;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof CacheTempAccum)) return false;
        CacheTempAccum that = (CacheTempAccum) o;
        return getAccumType().equals(that.getAccumType());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getAccumType());
    }

    public CacheTempAccum() {
    }

    public String getAccumType() {
        return accumType;
    }

    public void setAccumType(String accumType) {
        this.accumType = accumType;
    }

    public long getAccumAmount() {
        return accumAmount;
    }

    public void setAccumAmount(long accumAmount) {
        this.accumAmount = accumAmount;
    }

    public int getAccumCount() {
        return accumCount;
    }

    public void setAccumCount(int accumCount) {
        this.accumCount = accumCount;
    }


    public static CacheTempAccumBuilder builder(){
        return new CacheTempAccumBuilder();
    }


    public static class CacheTempAccumBuilder {
        private String accumType;
        private long accumAmount;
        private int accumCount;

        CacheTempAccumBuilder() {
        }

        public CacheTempAccumBuilder accumType(final String accumType) {
            this.accumType = accumType;
            return this;
        }

        public CacheTempAccumBuilder accumAmount(final long accumAmount) {
            this.accumAmount = accumAmount;
            return this;
        }

        public CacheTempAccumBuilder accumCount(final int accumCount) {
            this.accumCount = accumCount;
            return this;
        }

        public CacheTempAccum build() {
            return new CacheTempAccum(this.accumType, this.accumAmount, this.accumCount);
        }


    }


}
