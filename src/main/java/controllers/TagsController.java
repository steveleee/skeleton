package controllers;

import api.ReceiptResponse;
import dao.ReceiptDao;
import dao.TagDao;
import generated.tables.records.ReceiptsRecord;

import javax.validation.constraints.NotNull;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import java.util.List;

import static java.util.stream.Collectors.toList;

@Path("/tags")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public class TagsController {

    final TagDao tags;
    final ReceiptDao receipts;

    public TagsController(TagDao tags, ReceiptDao receipts) {
        this.tags = tags;
        this.receipts = receipts;
    }

    @PUT
    @Path("/{tag}")
    public void toggleTag(@PathParam("tag") String tagName, @NotNull String receiptID) {
        receiptID = receiptID.replaceAll("^\"|\"$", "");
        int delete = tags.delete(tagName, Integer.parseInt(receiptID));
        if (delete == 0) {
            tags.insert(tagName, Integer.parseInt(receiptID));
        }
        else {
            System.out.println("deleted");
        }
    }

    @GET
    @Path("/{tag}")
    public List<ReceiptResponse> getReceiptsForTag(@PathParam("tag") String tagName) {
        List<Integer> receiptIDs = tags.getAllReceiptIDsForTagName(tagName);
        List<ReceiptsRecord> receiptRecords = receipts.getAllReceipts();

        return receiptRecords
                .stream()
                .filter(receiptsRecord -> receiptIDs.contains(receiptsRecord.getId()))
                .map(ReceiptResponse::new).collect(toList());
    }
}
