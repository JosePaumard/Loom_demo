package org.paumard.loom.travelpage.model;

public record Quotation(String agency, int quotation) {

    public static Quotation readQuotation() {

        return new Quotation("Agency A", 100);
    }
}
