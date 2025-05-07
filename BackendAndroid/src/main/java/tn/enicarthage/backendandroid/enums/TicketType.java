package tn.enicarthage.backendandroid.enums;

import java.math.BigDecimal;

public enum TicketType {
    VIP("VIP", new BigDecimal("100.00")),
    PREMIUM("Premium", new BigDecimal("50.00")),
    STANDARD("Standard", new BigDecimal("20.00"));

    private final String name;
    private final BigDecimal price;

    TicketType(String name, BigDecimal price) {
        this.name = name;
        this.price = price;
    }

    public String getName() {
        return name;
    }

    public BigDecimal getPrice() {
        return price;
    }
}
