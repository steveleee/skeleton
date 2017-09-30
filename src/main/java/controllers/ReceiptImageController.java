package controllers;

import api.ReceiptSuggestionResponse;
import com.google.cloud.vision.v1.*;
import com.google.protobuf.ByteString;
import java.math.BigDecimal;
import java.util.*;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import org.hibernate.validator.constraints.NotEmpty;
import java.util.regex.*;

import static java.lang.System.out;

@Path("/images")
@Consumes(MediaType.TEXT_PLAIN)
@Produces(MediaType.APPLICATION_JSON)
public class ReceiptImageController {
    private final AnnotateImageRequest.Builder requestBuilder;

    public ReceiptImageController() {
        // DOCUMENT_TEXT_DETECTION is not the best or only OCR method available
        Feature ocrFeature = Feature.newBuilder().setType(Feature.Type.DOCUMENT_TEXT_DETECTION).build();
        this.requestBuilder = AnnotateImageRequest.newBuilder().addFeatures(ocrFeature);
    }

    /**
     * This borrows heavily from the Google Vision API Docs.  See:
     * https://cloud.google.com/vision/docs/detecting-fulltext
     *
     * YOU SHOULD MODIFY THIS METHOD TO RETURN A ReceiptSuggestionResponse:
     *
     * public class ReceiptSuggestionResponse {
     *     String merchantName;
     *     String amount;
     * }
     */
    @POST
    public ReceiptSuggestionResponse parseReceipt(@NotEmpty String base64EncodedImage) throws Exception {
        Image img = Image.newBuilder().setContent(ByteString.copyFrom(Base64.getDecoder().decode(base64EncodedImage))).build();
        AnnotateImageRequest request = this.requestBuilder.setImage(img).build();

        try (ImageAnnotatorClient client = ImageAnnotatorClient.create()) {
            BatchAnnotateImagesResponse responses = client.batchAnnotateImages(Collections.singletonList(request));
            AnnotateImageResponse res = responses.getResponses(0);

//            List<String> rows = new ArrayList<>();

            // Your Algo Here!!
            // Sort text annotations by bounding polygon.  Top-most non-decimal text is the merchant
            // bottom-most decimal text is the total amount

//            StringBuilder newRowBuilder = new StringBuilder();
//            int lastY = -1;
//
//            for (EntityAnnotation annotation : res.getTextAnnotationsList()) {
//                Vertex topLeftVertex = annotation.getBoundingPoly().getVertices(0);
//                Vertex topRightVertex = annotation.getBoundingPoly().getVertices(1);
//                Vertex bottomRightVertex = annotation.getBoundingPoly().getVertices(2);
//
//                int height = Math.abs(topRightVertex.getY() - bottomRightVertex.getY());
//
//                if (lastY != -1) {
//                    if (Math.abs(topLeftVertex.getY() - lastY) < height / 5) {
//                        newRowBuilder.append(annotation.getDescription());
//                    }
//                    else {
//                        rows.add(newRowBuilder.toString());
//                        out.println("New String: " + newRowBuilder.toString());
//                        newRowBuilder = new StringBuilder();
//                        newRowBuilder.append(annotation.getDescription());
//                    }
//
//                    lastY = topRightVertex.getY();
//                }
//                else {
//                    newRowBuilder.append(annotation.getDescription());
//                    lastY = topRightVertex.getY();
//                }
//            }
//

            EntityAnnotation annotation = res.getTextAnnotations(0);
            String[] rows = annotation.getDescription().split("\n");
            String merchantName = rows[0];

            for (String row: rows) {
                if (findDecimalOrDollarAmount(row) == null) {
                    merchantName = row;
                    break;
                }
            }

            BigDecimal amount = null;

            /// Find amount, reverse iterate
            for (int i = rows.length - 1; i >= 0; i--) {
                if(findDecimalOrDollarAmount(rows[i]) != null) {
                    amount = findDecimalOrDollarAmount(rows[i]);
                    break;
                }
            }

            return new ReceiptSuggestionResponse(merchantName, amount);
        }
    }

    private BigDecimal findDecimalOrDollarAmount(String str) {
        Pattern dollarPattern = Pattern.compile("\\$?((([1-9]\\d{0,2}(,\\d{3})*)|(([1-9]\\d*)?\\d))(\\.\\d\\d)?)");
        Matcher m = dollarPattern.matcher(str);

        if (m.matches()) {
            return new BigDecimal(m.group(1));
        }
        else {
            return null;
        }
    }
}

