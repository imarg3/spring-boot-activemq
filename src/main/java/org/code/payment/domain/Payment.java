package org.code.payment.domain;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.math.BigDecimal;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Payment implements Serializable {

    private static final long serialVersionUID = 1L; // You can specify a serial version UID for consistency

    private String paymentId;
    private BigDecimal amount;
    private String currency;
    private String userId;
}
