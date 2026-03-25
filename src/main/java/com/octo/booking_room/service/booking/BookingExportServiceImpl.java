package com.octo.booking_room.service.booking;

import com.lowagie.text.Document;
import com.lowagie.text.DocumentException;
import com.lowagie.text.Element;
import com.lowagie.text.FontFactory;
import com.lowagie.text.PageSize;
import com.lowagie.text.Paragraph;
import com.lowagie.text.Phrase;
import com.lowagie.text.pdf.PdfPCell;
import com.lowagie.text.pdf.PdfPTable;
import com.lowagie.text.pdf.PdfWriter;
import com.octo.booking_room.dto.booking.BookingExportRow;
import com.octo.booking_room.dto.booking.BookingFilter;
import com.octo.booking_room.entity.booking.Booking;
import com.octo.booking_room.entity.booking.BookingSlot;
import com.octo.booking_room.entity.customer.Customer;
import com.octo.booking_room.exception.ResourceNotFoundException;
import com.octo.booking_room.repository.booking.BookingRepository;
import com.octo.booking_room.repository.customer.CustomerRepository;
import lombok.RequiredArgsConstructor;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.FillPatternType;
import org.apache.poi.ss.usermodel.HorizontalAlignment;
import org.apache.poi.ss.usermodel.IndexedColors;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Comparator;
import java.util.List;

@Service
@RequiredArgsConstructor
public class BookingExportServiceImpl implements BookingExportService {

  private static final String[] HEADERS = {
      "Booking ID", "Date", "Status", "Customer ID", "Customer Name", "Customer Email",
      "Room ID", "Room Name", "Room Type ID", "Room Type", "Floor", "Slots"
  };

  private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ISO_LOCAL_DATE;
  private static final DateTimeFormatter TIME_FORMATTER = DateTimeFormatter.ofPattern("HH:mm");

  private final BookingRepository bookingRepository;
  private final CustomerRepository customerRepository;

  @Override
  @Transactional(readOnly = true)
  public byte[] exportBookingsToExcel(String requesterEmail, BookingFilter filter) {
    List<BookingExportRow> rows = getFilteredRows(requesterEmail, filter);

    try (XSSFWorkbook workbook = new XSSFWorkbook(); ByteArrayOutputStream outputStream = new ByteArrayOutputStream()) {
      Sheet sheet = workbook.createSheet("Bookings");

      CellStyle headerStyle = workbook.createCellStyle();
      headerStyle.setFillForegroundColor(IndexedColors.GREY_25_PERCENT.getIndex());
      headerStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
      headerStyle.setAlignment(HorizontalAlignment.CENTER);

      Row headerRow = sheet.createRow(0);
      for (int i = 0; i < HEADERS.length; i++) {
        Cell cell = headerRow.createCell(i);
        cell.setCellValue(HEADERS[i]);
        cell.setCellStyle(headerStyle);
      }

      for (int i = 0; i < rows.size(); i++) {
        BookingExportRow exportRow = rows.get(i);
        Row row = sheet.createRow(i + 1);
        row.createCell(0).setCellValue(exportRow.bookingId());
        row.createCell(1).setCellValue(exportRow.date());
        row.createCell(2).setCellValue(exportRow.status());
        row.createCell(3).setCellValue(exportRow.customerId());
        row.createCell(4).setCellValue(exportRow.customerName());
        row.createCell(5).setCellValue(exportRow.customerEmail());
        row.createCell(6).setCellValue(exportRow.roomId());
        row.createCell(7).setCellValue(exportRow.roomName());
        row.createCell(8).setCellValue(exportRow.roomTypeId());
        row.createCell(9).setCellValue(exportRow.roomTypeName());
        row.createCell(10).setCellValue(exportRow.floor());
        row.createCell(11).setCellValue(exportRow.slots());
      }

      for (int i = 0; i < HEADERS.length; i++) {
        sheet.autoSizeColumn(i);
      }

      workbook.write(outputStream);
      return outputStream.toByteArray();
    } catch (IOException exception) {
      throw new IllegalStateException("Failed to generate Excel export", exception);
    }
  }

  @Override
  @Transactional(readOnly = true)
  public byte[] exportBookingsToPdf(String requesterEmail, BookingFilter filter) {
    List<BookingExportRow> rows = getFilteredRows(requesterEmail, filter);

    try (ByteArrayOutputStream outputStream = new ByteArrayOutputStream()) {
      Document document = new Document(PageSize.A4.rotate(), 24, 24, 24, 24);
      PdfWriter.getInstance(document, outputStream);
      document.open();

      Paragraph title = new Paragraph("Booking Report", FontFactory.getFont(FontFactory.HELVETICA_BOLD, 16));
      title.setAlignment(Element.ALIGN_CENTER);
      document.add(title);
      document.add(new Paragraph("Generated at: " + LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"))));
      document.add(new Paragraph(" "));

      PdfPTable table = new PdfPTable(HEADERS.length);
      table.setWidthPercentage(100);
      table.setWidths(new float[]{2.2f, 1.6f, 1.3f, 2.2f, 2.5f, 3.2f, 2.0f, 2.8f, 2.2f, 2.8f, 1.0f, 4.2f});

      for (String header : HEADERS) {
        PdfPCell cell = new PdfPCell(new Phrase(header, FontFactory.getFont(FontFactory.HELVETICA_BOLD, 9)));
        cell.setHorizontalAlignment(Element.ALIGN_CENTER);
        table.addCell(cell);
      }

      for (BookingExportRow row : rows) {
        table.addCell(row.bookingId());
        table.addCell(row.date());
        table.addCell(row.status());
        table.addCell(row.customerId());
        table.addCell(row.customerName());
        table.addCell(row.customerEmail());
        table.addCell(row.roomId());
        table.addCell(row.roomName());
        table.addCell(row.roomTypeId());
        table.addCell(row.roomTypeName());
        table.addCell(String.valueOf(row.floor()));
        table.addCell(row.slots());
      }

      document.add(table);
      document.close();
      return outputStream.toByteArray();
    } catch (DocumentException | IOException exception) {
      throw new IllegalStateException("Failed to generate PDF export", exception);
    }
  }

  private List<BookingExportRow> getFilteredRows(String requesterEmail, BookingFilter filter) {
    requireAdmin(requesterEmail);

    return bookingRepository.findAllForExport().stream()
        .filter(booking -> matchesFilter(booking, filter))
        .sorted(Comparator.comparing(Booking::getDate)
            .thenComparing(booking -> booking.getRoom().getName())
            .thenComparing(Booking::getBookingId))
        .map(this::toExportRow)
        .toList();
  }

  private void requireAdmin(String email) {
    Customer requester = customerRepository.findByEmail(email)
        .orElseThrow(() -> new ResourceNotFoundException("Customer with email " + email + " not found"));

    if (!Boolean.TRUE.equals(requester.getIsAdmin())) {
      throw new AccessDeniedException("Only admins can perform this action");
    }
  }

  private boolean matchesFilter(Booking booking, BookingFilter filter) {
    if (filter == null) {
      return true;
    }
    if (filter.getRoomId() != null && !filter.getRoomId().isBlank() &&
        !booking.getRoom().getRoomId().equals(filter.getRoomId())) {
      return false;
    }
    if (filter.getRoomTypeId() != null && !filter.getRoomTypeId().isBlank() &&
        !booking.getRoom().getRoomType().getTypeId().equals(filter.getRoomTypeId())) {
      return false;
    }
    if (filter.getYear() != null && booking.getDate().getYear() != filter.getYear()) {
      return false;
    }
    if (filter.getMonth() != null && booking.getDate().getMonthValue() != filter.getMonth()) {
      return false;
    }
    return true;
  }

  private BookingExportRow toExportRow(Booking booking) {
    String slots = booking.getBookingSlots().stream()
        .sorted(Comparator.comparing(BookingSlot::getStartHour))
        .map(slot -> TIME_FORMATTER.format(slot.getStartHour()) + " - " + TIME_FORMATTER.format(slot.getEndHour()))
        .reduce((left, right) -> left + ", " + right)
        .orElse("-");

    return new BookingExportRow(
        booking.getBookingId(),
        DATE_FORMATTER.format(booking.getDate()),
        booking.getStatus().name(),
        booking.getCustomer().getCustomerId(),
        booking.getCustomer().getName(),
        booking.getCustomer().getEmail(),
        booking.getRoom().getRoomId(),
        booking.getRoom().getName(),
        booking.getRoom().getRoomType().getTypeId(),
        booking.getRoom().getRoomType().getName(),
        booking.getRoom().getFloor(),
        slots
    );
  }
}
