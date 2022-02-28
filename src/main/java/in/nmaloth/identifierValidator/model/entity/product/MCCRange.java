package in.nmaloth.identifierValidator.model.entity.product;

import org.bson.codecs.pojo.annotations.BsonProperty;

import java.util.Objects;

public class MCCRange implements Comparable<MCCRange> {
    @BsonProperty("start")
    private int mccStart;
    @BsonProperty("end")
    private int mccEnd;

    public boolean equals(Object o) {
        int value = ((MCCRange)o).getMccStart();
        if (this.mccStart <= value && this.mccEnd > value) {
            return true;
        } else {
            return this.mccEnd == 0 && this.mccStart == value;
        }
    }

    public int hashCode() {
        return Objects.hash(new Object[]{this.getMccStart()});
    }

    public int compareTo(MCCRange o) {
        if (this.mccStart <= o.getMccStart() && this.mccEnd > o.getMccStart()) {
            return 0;
        } else if (this.mccEnd < o.getMccStart()) {
            return -1;
        } else {
            return this.mccStart > o.getMccEnd() ? 1 : 0;
        }
    }

    public static MCCRange.MCCRangeBuilder builder() {
        return new MCCRange.MCCRangeBuilder();
    }

    public int getMccStart() {
        return this.mccStart;
    }

    public int getMccEnd() {
        return this.mccEnd;
    }

    public void setMccStart(final int mccStart) {
        this.mccStart = mccStart;
    }

    public void setMccEnd(final int mccEnd) {
        this.mccEnd = mccEnd;
    }

    public MCCRange() {
    }

    public MCCRange(final int mccStart, final int mccEnd) {
        this.mccStart = mccStart;
        this.mccEnd = mccEnd;
    }

    public static class MCCRangeBuilder {
        private int mccStart;
        private int mccEnd;

        MCCRangeBuilder() {
        }

        public MCCRange.MCCRangeBuilder mccStart(final int mccStart) {
            this.mccStart = mccStart;
            return this;
        }

        public MCCRange.MCCRangeBuilder mccEnd(final int mccEnd) {
            this.mccEnd = mccEnd;
            return this;
        }

        public MCCRange build() {
            return new MCCRange(this.mccStart, this.mccEnd);
        }

        public String toString() {
            return "MCCRange.MCCRangeBuilder(mccStart=" + this.mccStart + ", mccEnd=" + this.mccEnd + ")";
        }
    }
}
