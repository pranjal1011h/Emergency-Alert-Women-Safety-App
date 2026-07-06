package com.example.safetyapp;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.telephony.SmsManager;
import android.widget.Toast;

import java.util.List;

public class SosSender {

    // ── Build the SOS message ────────────────────────────────────────
    public static String buildMessage(double lat, double lng) {
        String mapsLink =
                "https://www.google.com/maps/dir/?api=1&destination="
                        +  + lat + "," + lng
                        + "&travelmode=driving";
        return "EMERGENCY SOS ALERT!\n"
                + "I am in DANGER and need immediate help!\n"
                + "Please contact emergency services or reach me NOW.\n\n"
                + "My location:\n"
                + mapsLink
                + "\n\n Sent via Sakhi Rakshak Safety App";
    }

    // ── Send SMS to all contacts ─────────────────────────────────────
    public static void sendSmsToAll(Context ctx, double lat, double lng) {
        List<SosContact> contacts = ContactManager.loadContacts(ctx);

        if (contacts.isEmpty()) {
            AppLogger.sos("SMS_SKIPPED", "no_contacts_saved");
            Toast.makeText(ctx,
                    "No emergency contacts saved. Add contacts first.",
                    Toast.LENGTH_LONG).show();
            return;
        }

        String message = buildMessage(lat, lng);

        SmsManager smsManager =
                SmsManager.getDefault();

        int successCount = 0;

        /* -------- PRIMARY CONTACT FIRST -------- */

        SosContact primary =
                ContactManager.getPrimaryContact(ctx);

        if (primary != null) {

            try {

                java.util.ArrayList<String> parts =
                        smsManager.divideMessage(message);

                smsManager.sendMultipartTextMessage(
                        primary.getPhone(),
                        null,
                        parts,
                        null,
                        null);

                successCount++;

            } catch (Exception e) {

                AppLogger.error(
                        "PRIMARY_SMS_FAILED",
                        e);
            }
        }

        /* -------- REMAINING CONTACTS -------- */

        for (SosContact contact : contacts) {

            if (contact.isPrimary()) {
                continue;
            }

            try {

                java.util.ArrayList<String> parts =
                        smsManager.divideMessage(message);

                smsManager.sendMultipartTextMessage(
                        contact.getPhone(),
                        null,
                        parts,
                        null,
                        null);

                successCount++;

            } catch (Exception e) {

                AppLogger.error(
                        "SMS_FAILED_" + contact.getPhone(),
                        e);
            }
        }
        Toast.makeText(ctx,
                "SOS SMS sent to " + successCount + " contact(s)",
                Toast.LENGTH_LONG).show();
    }

    // ── Send WhatsApp to all contacts ────────────────────────────────
    // Opens WhatsApp for each contact one by one (no WhatsApp Business API needed)
    public static void sendWhatsAppToAll(Context ctx, double lat, double lng) {
        List<SosContact> contacts = ContactManager.loadContacts(ctx);

        if (contacts.isEmpty()) {
            AppLogger.sos("WHATSAPP_SKIPPED", "no_contacts_saved");
            return;
        }

        String message = buildMessage(lat, lng);

        for (SosContact contact : contacts) {
            try {
                // Format phone: remove spaces, dashes, brackets; ensure + prefix
                String phone = contact.getPhone()
                        .replaceAll("[\\s\\-().]", "");
                if (!phone.startsWith("+")) {
                    phone = "+91" + phone; // default to India — change as needed
                }

                Intent intent = new Intent(Intent.ACTION_VIEW);
                intent.setData(Uri.parse(
                        "https://api.whatsapp.com/send?phone="
                                + phone
                                + "&text="
                                + Uri.encode(message)));
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                ctx.startActivity(intent);

                AppLogger.sos("WHATSAPP_OPENED",
                        "to=" + contact.getName() + " number=" + phone);

                // Small delay between each WhatsApp open so they don't stack instantly
                try { Thread.sleep(1500); } catch (InterruptedException ignored) {}

            } catch (Exception e) {
                AppLogger.error("WHATSAPP_FAILED_" + contact.getPhone(), e);
                Toast.makeText(ctx,
                        "WhatsApp not installed or failed for " + contact.getName(),
                        Toast.LENGTH_SHORT).show();
            }
        }
    }
}