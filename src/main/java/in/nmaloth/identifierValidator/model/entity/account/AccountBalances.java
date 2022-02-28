package in.nmaloth.identifierValidator.model.entity.account;

import org.bson.codecs.pojo.annotations.BsonProperty;

public class AccountBalances {
    @BsonProperty("posted_balance")
    private long postedBalance;
    @BsonProperty("memo_db")
    private long memoDb;
    @BsonProperty("memo_cr")
    private long memoCr;


    public static AccountBalances.AccountBalancesBuilder builder() {
        return new AccountBalances.AccountBalancesBuilder();
    }

    public long getPostedBalance() {
        return this.postedBalance;
    }

    public long getMemoDb() {
        return this.memoDb;
    }

    public long getMemoCr() {
        return this.memoCr;
    }

    public void setPostedBalance(final long postedBalance) {
        this.postedBalance = postedBalance;
    }

    public void setMemoDb(final long memoDb) {
        this.memoDb = memoDb;
    }

    public void setMemoCr(final long memoCr) {
        this.memoCr = memoCr;
    }

    public AccountBalances() {
    }

    public AccountBalances(final long postedBalance, final long memoDb, final long memoCr) {
        this.postedBalance = postedBalance;
        this.memoDb = memoDb;
        this.memoCr = memoCr;
    }

    public static class AccountBalancesBuilder {
        private long postedBalance;
        private long memoDb;
        private long memoCr;

        AccountBalancesBuilder() {
        }

        public AccountBalances.AccountBalancesBuilder postedBalance(final long postedBalance) {
            this.postedBalance = postedBalance;
            return this;
        }

        public AccountBalances.AccountBalancesBuilder memoDb(final long memoDb) {
            this.memoDb = memoDb;
            return this;
        }

        public AccountBalances.AccountBalancesBuilder memoCr(final long memoCr) {
            this.memoCr = memoCr;
            return this;
        }

        public AccountBalances build() {
            return new AccountBalances(this.postedBalance, this.memoDb, this.memoCr);
        }

        public String toString() {
            return "AccountBalances.AccountBalancesBuilder(postedBalance=" + this.postedBalance + ", memoDb=" + this.memoDb + ", memoCr=" + this.memoCr + ")";
        }
    }
}