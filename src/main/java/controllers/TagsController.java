package controllers;

import api.CreateReceiptRequest;
import api.ReceiptResponse;
import dao.ReceiptDao;
import generated.tables.records.ReceiptsRecord;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import java.util.List;

import static java.util.stream.Collectors.toList;

@Path("/tags")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public class TagsController {

    final ReceiptDao receipts;

    public TagsController(ReceiptDao receipts) {
        this.receipts = receipts;
    }

    @PUT
    @Path("/:tag")
    public void toggleTag(@PathParam("tag") String tagName) {
        // <your code here
    }

//    @GET
//    @Path("/:tag")
//    public void getReceiptsForTag(@PathParam("tag") String tagName) {
//        List<ReceiptsRecord> receiptRecords = receipts.getAllReceipts();
//        return receiptRecords.stream()
//                .filter(record -> record.tags.contains(tagName))
//                .map(ReceiptResponse::new)
//                .collect(toList());
//    }
}
