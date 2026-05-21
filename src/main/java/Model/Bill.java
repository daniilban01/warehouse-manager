package Model;

import java.time.LocalDateTime;

/**
 * Immutable record representing a generated bill log for an processed order.
 *
 * @param billId      the unique identifier of the bill
 * @param clientName  the name of the client who placed the order
 * @param productName the name of the purchased product
 * @param quantity     the number of product units ordered
 * @param orderTime    the timestamp when the order was created
 */
public record Bill(
        int billId,
        String clientName,
        String productName,
        int quantity,
        LocalDateTime orderTime
) {
}