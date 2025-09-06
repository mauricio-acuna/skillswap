package com.skillswap.backend.controller;

import com.skillswap.backend.model.CreditTransaction;
import com.skillswap.backend.service.CreditService;
import com.skillswap.backend.security.JwtTokenProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.Map;

@RestController
@RequestMapping("/api/credits")
@CrossOrigin(origins = "*")
public class CreditController {

    @Autowired
    private CreditService creditService;

    @Autowired
    private JwtTokenProvider jwtTokenProvider;

    // Obtener balance actual del usuario
    @GetMapping("/balance")
    public ResponseEntity<?> getUserBalance(@RequestHeader("Authorization") String token) {
        try {
            Long userId = jwtTokenProvider.getUserIdFromToken(token.substring(7));
            // Obtener usuario desde el repository
            com.skillswap.backend.model.User user = getUserById(userId);
            
            BigDecimal balance = creditService.getUserBalance(user);
            return ResponseEntity.ok(Map.of("balance", balance));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    // Obtener historial de transacciones
    @GetMapping("/history")
    public ResponseEntity<?> getTransactionHistory(
            @RequestHeader("Authorization") String token,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(defaultValue = "createdAt") String sortBy,
            @RequestParam(defaultValue = "desc") String sortDir) {
        try {
            Long userId = jwtTokenProvider.getUserIdFromToken(token.substring(7));
            com.skillswap.backend.model.User user = getUserById(userId);

            Sort sort = sortDir.equalsIgnoreCase("desc") ? 
                Sort.by(sortBy).descending() : Sort.by(sortBy).ascending();
            Pageable pageable = PageRequest.of(page, size, sort);

            Page<CreditTransaction> transactions = creditService.getUserTransactionHistory(user, pageable);
            return ResponseEntity.ok(transactions);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    // Obtener estadísticas del usuario
    @GetMapping("/stats")
    public ResponseEntity<?> getUserCreditStatistics(@RequestHeader("Authorization") String token) {
        try {
            Long userId = jwtTokenProvider.getUserIdFromToken(token.substring(7));
            com.skillswap.backend.model.User user = getUserById(userId);

            CreditService.CreditStatistics stats = creditService.getUserCreditStatistics(user);
            return ResponseEntity.ok(stats);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    // Transferir créditos a otro usuario
    @PostMapping("/transfer")
    public ResponseEntity<?> transferCredits(
            @RequestHeader("Authorization") String token,
            @RequestBody TransferCreditsRequest request) {
        try {
            Long fromUserId = jwtTokenProvider.getUserIdFromToken(token.substring(7));
            com.skillswap.backend.model.User fromUser = getUserById(fromUserId);
            com.skillswap.backend.model.User toUser = getUserById(request.getToUserId());

            creditService.transferCredits(fromUser, toUser, request.getAmount(), request.getDescription());
            
            return ResponseEntity.ok(Map.of(
                "message", "Transferencia completada exitosamente",
                "amount", request.getAmount(),
                "recipient", toUser.getFirstName() + " " + toUser.getLastName()
            ));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    // Comprar créditos (integración con sistema de pagos)
    @PostMapping("/purchase")
    public ResponseEntity<?> purchaseCredits(
            @RequestHeader("Authorization") String token,
            @RequestBody PurchaseCreditsRequest request) {
        try {
            Long userId = jwtTokenProvider.getUserIdFromToken(token.substring(7));
            com.skillswap.backend.model.User user = getUserById(userId);

            // Validar el pago antes de procesar (esto se integraría con Stripe, PayPal, etc.)
            if (!isValidPayment(request.getPaymentReference())) {
                return ResponseEntity.badRequest().body(Map.of("error", "Pago no válido"));
            }

            CreditTransaction transaction = creditService.purchaseCredits(
                user, request.getAmount(), request.getPaymentReference());
            
            return ResponseEntity.ok(Map.of(
                "message", "Créditos comprados exitosamente",
                "transaction", transaction,
                "newBalance", creditService.getUserBalance(user)
            ));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    // Verificar si el usuario tiene suficientes créditos
    @GetMapping("/check-balance")
    public ResponseEntity<?> checkSufficientCredits(
            @RequestHeader("Authorization") String token,
            @RequestParam BigDecimal amount) {
        try {
            Long userId = jwtTokenProvider.getUserIdFromToken(token.substring(7));
            com.skillswap.backend.model.User user = getUserById(userId);

            boolean hasSufficientCredits = creditService.hasSufficientCredits(user, amount);
            BigDecimal currentBalance = creditService.getUserBalance(user);
            
            return ResponseEntity.ok(Map.of(
                "hasSufficientCredits", hasSufficientCredits,
                "currentBalance", currentBalance,
                "requiredAmount", amount
            ));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    // Método auxiliar para obtener usuario por ID (debería inyectarse UserRepository)
    private com.skillswap.backend.model.User getUserById(Long userId) {
        // Esta sería la implementación real con UserRepository
        // Por ahora, lanzamos excepción para que se implemente correctamente
        throw new RuntimeException("UserRepository injection needed");
    }

    // Método auxiliar para validar pagos (se integraría con el proveedor de pagos)
    private boolean isValidPayment(String paymentReference) {
        // Esta sería la implementación real con el proveedor de pagos
        // Por ahora, simulamos validación básica
        return paymentReference != null && !paymentReference.trim().isEmpty();
    }

    // DTOs para requests
    public static class TransferCreditsRequest {
        private Long toUserId;
        private BigDecimal amount;
        private String description;

        // Constructors
        public TransferCreditsRequest() {}

        // Getters y Setters
        public Long getToUserId() {
            return toUserId;
        }

        public void setToUserId(Long toUserId) {
            this.toUserId = toUserId;
        }

        public BigDecimal getAmount() {
            return amount;
        }

        public void setAmount(BigDecimal amount) {
            this.amount = amount;
        }

        public String getDescription() {
            return description;
        }

        public void setDescription(String description) {
            this.description = description;
        }
    }

    public static class PurchaseCreditsRequest {
        private BigDecimal amount;
        private String paymentReference;
        private String paymentMethod; // "CARD", "PAYPAL", "GOOGLE_PAY", etc.

        // Constructors
        public PurchaseCreditsRequest() {}

        // Getters y Setters
        public BigDecimal getAmount() {
            return amount;
        }

        public void setAmount(BigDecimal amount) {
            this.amount = amount;
        }

        public String getPaymentReference() {
            return paymentReference;
        }

        public void setPaymentReference(String paymentReference) {
            this.paymentReference = paymentReference;
        }

        public String getPaymentMethod() {
            return paymentMethod;
        }

        public void setPaymentMethod(String paymentMethod) {
            this.paymentMethod = paymentMethod;
        }
    }
}
