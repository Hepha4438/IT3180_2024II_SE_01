package com.prototype.arpartment_managing.service;

import com.prototype.arpartment_managing.dto.UserDTO;
import com.prototype.arpartment_managing.exception.ApartmentNotFoundException;
import com.prototype.arpartment_managing.exception.FeeNotFoundException;
import com.prototype.arpartment_managing.exception.UserNotFoundException;
import com.prototype.arpartment_managing.exception.UserNotFoundExceptionUsername;
import com.prototype.arpartment_managing.model.Fee;
import com.prototype.arpartment_managing.model.Revenue;
import com.prototype.arpartment_managing.model.User;
import com.prototype.arpartment_managing.model.Apartment;
import com.prototype.arpartment_managing.repository.ApartmentRepository;
import com.prototype.arpartment_managing.repository.FeeRepository;
import com.prototype.arpartment_managing.repository.RevenueRepository;
import com.prototype.arpartment_managing.repository.UserRepository;
import com.itextpdf.text.*;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfWriter;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Primary;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Primary
@Service
public class ApartmentService {
    @Autowired
    private ApartmentRepository apartmentRepository;
    @Autowired
    private FeeRepository feeRepository;
    @Autowired
    private RevenueRepository revenueRepository;
    @Autowired
    private UserRepository userRepository;

    private final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    public List<Apartment> getAllApartments() {
        return apartmentRepository.findAll();
    }

    public ResponseEntity<?> getApartmentById(String apartmentId){
        if (apartmentId != null) {
            Apartment apartment = apartmentRepository.findByApartmentId(apartmentId)
                    .orElseThrow(() -> new ApartmentNotFoundException(apartmentId));
            return ResponseEntity.ok(apartment);
        } else {
            return ResponseEntity.badRequest().body("Must provide apartment room's number");
        }
    }

    public Apartment createApartment(Apartment apartment) {
        if (apartment.getArea() <= 0) {
            throw new IllegalArgumentException("Area must be greater than 0");
        }
        if (apartment.getFloor() <= 0) {
            throw new IllegalArgumentException("Floor must be greater than 0");
        }        
        return apartmentRepository.save(apartment);
    }

    @Transactional
    public void deleteApartment(String apartmentId) {
        Apartment apartment = apartmentRepository.findByApartmentId(apartmentId)
                .orElseThrow(() -> new ApartmentNotFoundException(apartmentId));

        for (User resident : apartment.getResidents()) {
            resident.setApartment(null);
        }
        userRepository.saveAll(apartment.getResidents()); // Save changes to users

        for (Revenue revenue : new ArrayList<>(apartment.getRevenues())) {
            revenueRepository.delete(revenue);
        }
        apartmentRepository.delete(apartment);
    }

    @Transactional
    public Apartment updateApartment(Apartment newApartment, String apartmentId) {
        return apartmentRepository.findByApartmentId(apartmentId)
                .map(apartment -> {
                    apartment.setFloor(newApartment.getFloor());
                    apartment.setOccupants(newApartment.getOccupants());
                    apartment.setIsOccupied(newApartment.getOccupants() > 0);
                    apartment.setOwner(newApartment.getOwner());
                    apartment.setArea(newApartment.getArea());
                    apartment.setApartmentType(newApartment.getApartmentType());
                    apartment.setTotal(newApartment.getTotal());
                    return apartmentRepository.save(apartment);
                }).orElseThrow(() -> new ApartmentNotFoundException(apartmentId));
    }

//    public Double calculateFee(String apartmentId, String feeType) {
//        List<Revenue> revenues = findAllRevenueByApartmentId(apartmentId);
//        if (revenues.isEmpty()) {
//            return 0.0;
//        }
//        Fee fee = feeRepository.findByType(feeType)
//                .orElseThrow(() -> new IllegalArgumentException("Fee type not found: " + feeType));
//        return revenues.stream()
//                .filter(revenue -> feeType.equals(revenue.getType())) // Selects only revenues matching the requested feeType
//                .mapToDouble(revenue -> revenue.getUsed() * fee.getPricePerUnit()) // Calculates individual fees
//                .sum(); // Sums up all fees
//    }
    public List<Revenue> findAllRevenueByApartmentId(String apartmentId) {
        Apartment apartment = apartmentRepository.findByApartmentId(apartmentId)
                .orElseThrow(() -> new ApartmentNotFoundException(apartmentId));
        return apartment.getRevenues();
    }

    public Double calculateTotalPayment(String apartmentId) {
        List<Revenue> revenues = findAllRevenueByApartmentId(apartmentId);
        if (revenues.isEmpty()) {
            return 0.0;
        }

        // Update status for all revenues first
        revenues.forEach(Revenue::updateStatus);

        // Sum up the total of all unpaid and overdue revenues
        return revenues.stream()
                .filter(revenue -> "Unpaid".equals(revenue.getStatus()) || "Overdue".equals(revenue.getStatus()))
                .mapToDouble(Revenue::getTotal)
                .sum();
    }

    public ResponseEntity<?> generateBill(String apartmentId) {
        try {
            Apartment apartment = apartmentRepository.findByApartmentId(apartmentId)
                    .orElseThrow(() -> new ApartmentNotFoundException(apartmentId));

            Document document = new Document(PageSize.A4);
            ByteArrayOutputStream out = new ByteArrayOutputStream();

            PdfWriter.getInstance(document, out);
            document.open();

            // Add logo
            try {
                String logoPath = "src/main/resources/static/images/logo.png";
                Image logo = Image.getInstance(logoPath);
                logo.scaleToFit(100, 100); // Adjust size as needed
                logo.setAlignment(Element.ALIGN_CENTER);
                document.add(logo);
            } catch (Exception e) {
                // If logo is not found, continue without it
                System.out.println("Logo not found: " + e.getMessage());
            }

            // Add company name
            Font headerFont = new Font(Font.FontFamily.HELVETICA, 24, Font.BOLD);
            Paragraph header = new Paragraph("BLUEMOON APARTMENT", headerFont);
            header.setAlignment(Element.ALIGN_CENTER);
            header.setSpacingAfter(20);
            document.add(header);

            // Add bill title
            Font titleFont = new Font(Font.FontFamily.HELVETICA, 18, Font.BOLD);
            Paragraph title = new Paragraph("BILL STATEMENT", titleFont);
            title.setAlignment(Element.ALIGN_CENTER);
            title.setSpacingAfter(20);
            document.add(title);

            // Add bill information section
            Font sectionFont = new Font(Font.FontFamily.HELVETICA, 12, Font.BOLD);
            Paragraph billInfo = new Paragraph("Bill Information", sectionFont);
            billInfo.setSpacingBefore(10);
            billInfo.setSpacingAfter(10);
            document.add(billInfo);

            // Add bill details in a table
            PdfPTable infoTable = new PdfPTable(2);
            infoTable.setWidthPercentage(100);
            infoTable.setSpacingBefore(10);
            infoTable.setSpacingAfter(10);

            // Get current month and year for the bill
            Calendar calendar = Calendar.getInstance();
            String currentMonth = new SimpleDateFormat("MMMM yyyy").format(calendar.getTime());
            
            // Add bill details
            addTableRow(infoTable, "Bill Number:", "BILL-" + apartmentId + "-" + System.currentTimeMillis());
            addTableRow(infoTable, "Issue Date:", new SimpleDateFormat("dd/MM/yyyy").format(new Date()));
            addTableRow(infoTable, "Bill Period:", currentMonth);
            document.add(infoTable);

            // Add apartment information section
            Paragraph apartmentInfo = new Paragraph("Apartment Information", sectionFont);
            apartmentInfo.setSpacingBefore(10);
            apartmentInfo.setSpacingAfter(10);
            document.add(apartmentInfo);

            // Add apartment details in a table
            PdfPTable apartmentTable = new PdfPTable(2);
            apartmentTable.setWidthPercentage(100);
            apartmentTable.setSpacingBefore(10);
            apartmentTable.setSpacingAfter(10);

            addTableRow(apartmentTable, "Apartment ID:", apartment.getApartmentId());
            addTableRow(apartmentTable, "Floor:", String.valueOf(apartment.getFloor()));
            addTableRow(apartmentTable, "Area:", apartment.getArea() + " m²");
            addTableRow(apartmentTable, "Type:", apartment.getApartmentType());
            addTableRow(apartmentTable, "Owner:", apartment.getOwner() != null ? apartment.getOwner() : "Not assigned");
            addTableRow(apartmentTable, "Number of Occupants:", String.valueOf(apartment.getOccupants()));
            document.add(apartmentTable);

            // Add revenue details section
            Paragraph revenueInfo = new Paragraph("Revenue Details", sectionFont);
            revenueInfo.setSpacingBefore(10);
            revenueInfo.setSpacingAfter(10);
            document.add(revenueInfo);

            // Add revenue table with better formatting
            PdfPTable revenueTable = new PdfPTable(6);
            revenueTable.setWidthPercentage(100);
            revenueTable.setSpacingBefore(10);
            revenueTable.setSpacingAfter(10);

            // Set column widths
            float[] columnWidths = {2f, 2f, 2f, 2f, 2f, 2f};
            revenueTable.setWidths(columnWidths);

            // Add table headers
            Font tableHeaderFont = new Font(Font.FontFamily.HELVETICA, 10, Font.BOLD);
            addTableHeader(revenueTable, "Type", tableHeaderFont);
            addTableHeader(revenueTable, "Usage", tableHeaderFont);
            addTableHeader(revenueTable, "Unit", tableHeaderFont);
            addTableHeader(revenueTable, "Unit Price", tableHeaderFont);
            addTableHeader(revenueTable, "Status", tableHeaderFont);
            addTableHeader(revenueTable, "Total", tableHeaderFont);

            // Add revenue rows
            List<Revenue> revenues = apartment.getRevenues();
            double totalAmount = 0.0;
            for (Revenue revenue : revenues) {
                Fee fee = feeRepository.findByType(revenue.getType())
                        .orElseThrow(() -> new FeeNotFoundException("Fee not found for type: " + revenue.getType()));
                
                double amount = revenue.getUsed() * fee.getPricePerUnit();
                totalAmount += amount;

                // Get unit based on revenue type
                String unit = getUnitForType(revenue.getType());

                // Create cells with center alignment
                PdfPCell typeCell = new PdfPCell(new Phrase(revenue.getType(), new Font(Font.FontFamily.HELVETICA, 10)));
                typeCell.setHorizontalAlignment(Element.ALIGN_CENTER);
                typeCell.setPadding(5);
                revenueTable.addCell(typeCell);

                PdfPCell usageCell = new PdfPCell(new Phrase(String.format("%.2f", revenue.getUsed()), new Font(Font.FontFamily.HELVETICA, 10)));
                usageCell.setHorizontalAlignment(Element.ALIGN_CENTER);
                usageCell.setPadding(5);
                revenueTable.addCell(usageCell);

                PdfPCell unitCell = new PdfPCell(new Phrase(unit, new Font(Font.FontFamily.HELVETICA, 10)));
                unitCell.setHorizontalAlignment(Element.ALIGN_CENTER);
                unitCell.setPadding(5);
                revenueTable.addCell(unitCell);

                PdfPCell priceCell = new PdfPCell(new Phrase(String.format("%.2f VND", fee.getPricePerUnit()), new Font(Font.FontFamily.HELVETICA, 10)));
                priceCell.setHorizontalAlignment(Element.ALIGN_CENTER);
                priceCell.setPadding(5);
                revenueTable.addCell(priceCell);

                PdfPCell statusCell = new PdfPCell(new Phrase(revenue.getStatus(), new Font(Font.FontFamily.HELVETICA, 10)));
                statusCell.setHorizontalAlignment(Element.ALIGN_CENTER);
                statusCell.setPadding(5);
                revenueTable.addCell(statusCell);

                PdfPCell totalCell = new PdfPCell(new Phrase(String.format("%.2f VND", amount), new Font(Font.FontFamily.HELVETICA, 10)));
                totalCell.setHorizontalAlignment(Element.ALIGN_RIGHT);
                totalCell.setPadding(5);
                revenueTable.addCell(totalCell);
            }
            document.add(revenueTable);

            // Add total amount with right alignment
            Paragraph total = new Paragraph(String.format("Total Amount Due: %.2f VND", totalAmount), 
                new Font(Font.FontFamily.HELVETICA, 14, Font.BOLD));
            total.setAlignment(Element.ALIGN_RIGHT);
            total.setSpacingBefore(20);
            document.add(total);

            // Add payment instructions
            Paragraph paymentInfo = new Paragraph("Payment Instructions", sectionFont);
            paymentInfo.setSpacingBefore(20);
            paymentInfo.setSpacingAfter(10);
            document.add(paymentInfo);

            Paragraph instructions = new Paragraph(
                "Please make your payment before the due date to avoid late payment charges.\n" +
                "Payment can be made through our online portal or at the management office.\n" +
                "For any queries, please contact the management office.",
                new Font(Font.FontFamily.HELVETICA, 10)
            );
            instructions.setSpacingBefore(10);
            document.add(instructions);

            // Add footer
            Paragraph footer = new Paragraph(
                "This is a computer-generated document. No signature is required.",
                new Font(Font.FontFamily.HELVETICA, 8, Font.ITALIC)
            );
            footer.setAlignment(Element.ALIGN_CENTER);
            footer.setSpacingBefore(30);
            document.add(footer);

            document.close();

            // Prepare response
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_PDF);
            headers.setContentDispositionFormData("attachment", "bill_" + apartmentId + ".pdf");

            return new ResponseEntity<>(out.toByteArray(), headers, HttpStatus.OK);

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error generating bill: " + e.getMessage());
        }
    }

    private void addTableRow(PdfPTable table, String label, String value) {
        Font labelFont = new Font(Font.FontFamily.HELVETICA, 10, Font.BOLD);
        Font valueFont = new Font(Font.FontFamily.HELVETICA, 10);
        
        PdfPCell labelCell = new PdfPCell(new Phrase(label, labelFont));
        labelCell.setBorder(Rectangle.NO_BORDER);
        labelCell.setPadding(5);
        
        PdfPCell valueCell = new PdfPCell(new Phrase(value, valueFont));
        valueCell.setBorder(Rectangle.NO_BORDER);
        valueCell.setPadding(5);
        
        table.addCell(labelCell);
        table.addCell(valueCell);
    }

    private void addTableHeader(PdfPTable table, String text, Font font) {
        PdfPCell cell = new PdfPCell(new Phrase(text, font));
        cell.setHorizontalAlignment(Element.ALIGN_CENTER);
        cell.setPadding(5);
        cell.setBackgroundColor(BaseColor.LIGHT_GRAY);
        table.addCell(cell);
    }

    // Helper method to get unit based on revenue type
    private String getUnitForType(String type) {
        switch (type.toLowerCase()) {
            case "water":
                return "m³";
            case "electricity":
                return "kWh";
            case "service":
                return "m²";
            case "parking":
                return "slot";
            default:
                return "peace";
        }
    }
}