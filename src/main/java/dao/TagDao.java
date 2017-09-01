package dao;

import generated.tables.records.TagsRecord;
import org.jooq.Configuration;
import org.jooq.DSLContext;
import org.jooq.Name;
import org.jooq.impl.DSL;

import java.math.BigDecimal;
import java.util.List;

import static com.google.common.base.Preconditions.checkState;
import static generated.Tables.TAGS;

public class TagDao {
    DSLContext dsl;

    public TagDao(Configuration jooqConfig) {
        this.dsl = DSL.using(jooqConfig);
    }

    public int insert(String tagName, int receiptID) {
        TagsRecord tagsRecord = dsl
                .insertInto(TAGS, TAGS.NAME, TAGS.RECEIPT_ID)
                .values(tagName, receiptID)
                .returning(TAGS.ID)
                .fetchOne();

        checkState(tagsRecord != null && tagsRecord.getId() != null, "Insert failed");

        return tagsRecord.getId();
    }

    public int delete(String tagName, int receiptID) {
        return dsl
                .delete(TAGS)
                .where(TAGS.NAME.eq(tagName).and(TAGS.RECEIPT_ID.eq(receiptID)))
                .execute();
    }

    public List<Integer> getAllReceiptIDsForTagName(String tagName) {
        return dsl.selectFrom(TAGS).where(TAGS.NAME.eq(tagName)).fetch(TAGS.RECEIPT_ID, Integer.class);
    }
}
