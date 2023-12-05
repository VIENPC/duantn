package com.poly.entities;

import java.text.SimpleDateFormat;
import java.util.Date;

public class EmailContentBuilder {

    public static String buildInvoiceContent(String senderPhone, String tenshop,
            String recipientCode, String recipientName, String recipientAddress,
            String paymentAmount, String deliveryNote, Date orderDate,
            String[] productNames, int[] quantities, int[] price, String mahd) {

        SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy HH:mm");
        String orderDateFormatted = dateFormat.format(orderDate);

        StringBuilder content = new StringBuilder();

        // Thêm thông tin người gửi
        content.append(
                "<div style=\"border: 2px solid #000; padding: 10px; border-radius: 5px; align-items: center; margin-bottom: 10px;\">");
        content.append("<h4 style=\"margin: 0; color: #888;\">Bên gửi:</h4>");
        content.append("<h3 style=\"margin: 0;\">").append(senderPhone).append(" - ").append(tenshop).append("</h3>");
        content.append("</div>");

        // Thêm thông tin người nhận và sản phẩm đặt hàng
        content.append(
                "<div style=\"border: 2px solid #000; padding: 10px; border-radius: 5px; display: block; margin-bottom: 10px;\">");
        content.append("<h4 style=\"margin: 0; color: #888;\">Bên nhận:</h4>");
        content.append("<h3 style=\"margin: 0;\">").append(recipientCode).append(" - ").append(recipientName)
                .append("</h3>");
        content.append("<h3 style=\"margin: 0;\">").append(recipientAddress).append("</h3>");
        content.append("<h4 style=\"margin: 0; color: #888;\">Thu tiền người nhận:</h4>");
        content.append("<h2 style=\"margin: 0;\">").append(paymentAmount).append("</h2>");
        content.append("<div style=\"display: block; margin-top: 10px;\">"); // Chuyển xuống hàng mới
        content.append("<p style=\"margin: 0; font-weight: bold;\">Cho xem hàng, không cho thử</p>");
        content.append("<p style=\"margin: 0; font-weight: bold; text-align: right; margin-left: auto;\">Ngày đặt: ")
                .append(orderDateFormatted).append("</p>");
        content.append("</div>");
        content.append("</div>");

        // Thêm bảng chi tiết đơn hàng
        content.append(
                "<table style=\"width: 100%; border-collapse: collapse; border: 2px solid #000; margin-bottom: 10px;\">");
        content.append("<tr>");
        content.append("<td style=\"width: 65%; border: 1px solid #000; padding: 8px;\">");
        content.append("<p style=\"margin: 0; font-weight: bold;\">Nội dung đơn hàng: #").append(mahd).append("</p>");;
        content.append("</td>");
        content.append("<td style=\"width: 15%; border: 1px solid #000; padding: 8px;\">");
        content.append("<p style=\"margin: 0; font-weight: bold;\">Số lượng sản phẩm: ").append(productNames.length)
                .append("</p>");
        content.append("</td>");
        content.append("<td style=\"width: 20%; border: 1px solid #000; padding: 8px;\">");
        content.append("<p style=\"margin: 0; font-weight: bold;\">Giá sản phẩm</p>");
        content.append("</td>");
        content.append("</tr>");

        for (int i = 0; i < productNames.length; i++) {
            content.append("<tr>");
            content.append("<td style=\"width: 65%; border: 1px solid #000; padding: 8px;\">");
            content.append("<ul style=\"margin: 0; padding: 0; list-style-type: none;\">");
            content.append("<li style=\"margin: 0; font-weight: bold;\">[").append(i + 1).append("] ")
                    .append(productNames[i]).append("</li>");
            content.append("</ul>");
            content.append("</td>");
            content.append("<td style=\"width: 15%; border: 1px solid #000; padding: 8px;\">");
            content.append("<ul style=\"margin: 0; padding: 0; list-style-type: none;\">");
            content.append("<li style=\"margin: 0; font-weight: bold;\">").append(quantities[i]).append("</li>");
            content.append("</ul>");
            content.append("</td>");
            content.append("<td style=\"width: 20%; border: 1px solid #000; padding: 8px;\">");
            content.append("<ul style=\"margin: 0; padding: 0; list-style-type: none;\">");
            content.append("<li style=\"margin: 0; font-weight: bold;\">").append(price[i] ).append("</li>");
            content.append("</ul>");
            content.append("</td>");
            content.append("</tr>");
        }

        content.append("</table>");

        return content.toString();
    }

    
}
