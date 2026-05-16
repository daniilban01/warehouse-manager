package Model;

import java.time.LocalDateTime;
import java.util.Locale;

public record Bill(
        int bill_id,
        String client_name,
        String product_name,
        int quantity,
        LocalDateTime orderTime
) {
}
