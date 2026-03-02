package com.swiftcart.notification_service.Controller;


import com.swiftcart.notification_service.Dto.NotificationResponse;
import com.swiftcart.notification_service.Dto.OfferNotificationRequest;
import com.swiftcart.notification_service.Dto.ProductNotificationRequest;
import com.swiftcart.notification_service.Service.NotificationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/notifications")
public class NotificationController {

    @Autowired
    private NotificationService notificationService;

    // POST /notifications/product
    @PostMapping("/product")
    public ResponseEntity<NotificationResponse> sendProductNotification(
            @RequestBody ProductNotificationRequest request) {
        NotificationResponse response = notificationService.sendProductNotification(request);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    // POST /notifications/offer
    @PostMapping("/offer")
    public ResponseEntity<NotificationResponse> sendOfferNotification(
            @RequestBody OfferNotificationRequest request) {
        NotificationResponse response = notificationService.sendOfferNotification(request);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    // GET /notifications
    @GetMapping("/get-All/notifications")
    public ResponseEntity<List<NotificationResponse>> getAllNotifications() {
        return ResponseEntity.ok(notificationService.getAllNotifications());
    }

    // GET /notifications/email/{email}
    @GetMapping("/email/{email}")
    public ResponseEntity<List<NotificationResponse>> getByEmail(@PathVariable String email) {
        return ResponseEntity.ok(notificationService.getNotificationsByEmail(email));
    }
}