package api;

import generated.tables.records.ReceiptsRecord;
import io.dropwizard.jersey.validation.Validators;
import org.junit.Test;

import javax.validation.Validator;

import java.math.BigDecimal;
import java.sql.Time;

import static org.hamcrest.MatcherAssert.assertThat;

public class ReceiptsRecordTest {

    private final Validator validator = Validators.newValidator();

    @Test
    public void testMerchant() {
        ReceiptsRecord receipt = new ReceiptsRecord();
        receipt.setMerchant("starbucks");
        assertThat("Merchant should be `Starbucks`", receipt.getMerchant() == "starbucks");
    }
}
