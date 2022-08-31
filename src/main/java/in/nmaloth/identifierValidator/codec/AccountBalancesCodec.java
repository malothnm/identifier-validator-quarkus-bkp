package in.nmaloth.identifierValidator.codec;

import com.mongodb.MongoClientSettings;
import in.nmaloth.identifierValidator.model.entity.account.AccountBalances;
import in.nmaloth.payments.constants.account.BalanceTypes;
import org.bson.*;
import org.bson.codecs.Codec;
import org.bson.codecs.CollectibleCodec;
import org.bson.codecs.DecoderContext;
import org.bson.codecs.EncoderContext;

public class AccountBalancesCodec implements CollectibleCodec<AccountBalances> {

    private final Codec<Document> documentCodec;

    public AccountBalancesCodec() {
        this.documentCodec = MongoClientSettings.getDefaultCodecRegistry().get(Document.class);
    }

    @Override
    public AccountBalances generateIdIfAbsentFromDocument(AccountBalances accountBalances) {

        if (!documentHasId(accountBalances)) {
            accountBalances.setPostedBalance(0L);
            accountBalances.setMemoCr(0L);
            accountBalances.setMemoDb(0L);
            accountBalances.setBalanceType(BalanceTypes.CURRENT_BALANCE.getBalanceTypes());
        }
        return accountBalances;      }

    @Override
    public boolean documentHasId(AccountBalances accountBalances) {
        return accountBalances.getBalanceType() != null;
    }

    @Override
    public BsonValue getDocumentId(AccountBalances accountBalances) {
        return  new BsonString(accountBalances.getBalanceType());
    }

    @Override
    public AccountBalances decode(BsonReader bsonReader, DecoderContext decoderContext) {



        Document document = documentCodec.decode(bsonReader, decoderContext);
        AccountBalances accountBalances = new AccountBalances();
        String balance_type = document.getString("balance_type");
        if (balance_type != null) {
            accountBalances.setBalanceType(balance_type);
        }
        Long postedBalance = document.getLong("posted_balance");
        if(postedBalance != null){
            accountBalances.setPostedBalance(postedBalance);
        }
        Long memoDb = document.getLong("memo_db");
        if(memoDb != null){
            accountBalances.setMemoDb(memoDb);
        }
        Long memoCr = document.getLong("memo_cr");
        if(memoCr != null){
            accountBalances.setMemoCr(memoCr);
        }
        return accountBalances;

    }

    @Override
    public void encode(BsonWriter bsonWriter, AccountBalances accountBalances, EncoderContext encoderContext) {

        Document doc = new Document();
        doc.put("balance_type", accountBalances.getBalanceType());
        doc.put("posted_balance", accountBalances.getPostedBalance());
        doc.put("memo_db", accountBalances.getMemoDb());
        doc.put("memo_cr", accountBalances.getMemoCr());

        documentCodec.encode(bsonWriter, doc, encoderContext);

    }

    @Override
    public Class<AccountBalances> getEncoderClass() {
        return AccountBalances.class;
    }
}
