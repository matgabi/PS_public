package  model;
import java.io.File;
import java.io.FileOutputStream;
import java.math.BigDecimal;
import java.util.ArrayList;

import com.itextpdf.text.Chunk;
import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Element;
import com.itextpdf.text.Font;
import com.itextpdf.text.FontFactory;
import com.itextpdf.text.PageSize;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.Phrase;
import com.itextpdf.text.Rectangle;
import com.itextpdf.text.pdf.PdfContentByte;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;

import javafx.collections.ObservableList;
import model.interfaces.ClientInterface;
import model.interfaces.SellerInterface;
import model.interfaces.TableItem;


public class PdfCreator {

    private static String TVA= "19";


    private Document document;
    private PdfWriter writer;
    private PdfPTable table;
    private Rectangle rect;

    private float[] columnWidths = { 1f, 3.5f, 1f, 2f, 2f, 2.5f, 2.5f };

    public PdfCreator() {
    }

    public void createPdf(String format,Factura factura) {
        table = new PdfPTable(7); // 3 columns.
        table.setWidthPercentage(100); // Width 100%
        table.setSpacingBefore(10f); // Space before table
        table.setSpacingAfter(10f); // Space after table
        File file = new File("facturiPdf");
        if (!file.exists()) {
            if (file.mkdir()) {
                System.out.println("Directory is created!");
            } else {
                System.out.println("Failed to create directory!");
            }
        }

        if(format == "a4"){
            document = new Document(PageSize.A4,30,30,30,30);
            rect = new Rectangle(30, 30, 565, 818);
        }
        else{
            document = new Document(PageSize.A5.rotate(),10,10,10,10);
            rect = new Rectangle(10, 10, 585, 410);
        }
        writer = null;
        try {
            System.out.println(factura.getPdfPath().replace("\\","/"));
            writer = PdfWriter.getInstance(document, new FileOutputStream(factura.getPdfPath().replace("\\","/") + "/" + factura.getName() + ".pdf"));
            document.open();

            PdfContentByte canvas = writer.getDirectContent();
            rect.setBorder(Rectangle.BOX);
            rect.setBorderWidth(0.5f);
            canvas.rectangle(rect);


            createInfoData(factura.getSeller(),factura.getClient());
            createCotaTva(factura.getTva());
            createFirstRow();
            createTableContent(factura.getProducts());
            document.add(table);
            //createObservatii(format);
            createSignature(factura.getEmitor(),factura.getTotalValue(),factura.getTotalTvaValue(),factura.getCar());

            document.close();
            writer.close();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            document.close();
            writer.close();
            ProcessBuilder pb = new ProcessBuilder("C:\\Program Files (x86)\\Google\\Chrome\\Application\\chrome.exe",
                    factura.getPdfPath().replace("\\","/") + "/" + factura.getName() + ".pdf");
            try {
                pb.start();
            } catch (Exception e) {
                System.out.println(e.getMessage());
            }
        }
    }

    private void createSignature(Emitor emitor,BigDecimal totalValue,BigDecimal totalTvaValue,String car) throws DocumentException {

        Font fontbold = FontFactory.getFont("defaultEncoding", 10, Font.NORMAL);
        PdfPTable tableInfo = new PdfPTable(5); // 3 columns.
        tableInfo.setWidthPercentage(100); // Width 100%
        tableInfo.setSpacingBefore(10f); // Space before table
        tableInfo.setSpacingAfter(10f); // Space after table
        float[] widths={1.4f,3f,1.3f,1f,1f};
        tableInfo.setWidths(widths);

        PdfPCell furnizorCell = new PdfPCell(new Paragraph("Semnatura si stampila furnizorului",fontbold));
        furnizorCell.setRowspan(7);
        furnizorCell.setHorizontalAlignment(Element.ALIGN_LEFT);
        furnizorCell.setVerticalAlignment(Element.ALIGN_TOP);

        PdfPCell c2r1 = new PdfPCell(new Paragraph("Date privind expeditia:",fontbold));
        c2r1.setHorizontalAlignment(Element.ALIGN_LEFT);
        c2r1.setVerticalAlignment(Element.ALIGN_MIDDLE);
        c2r1.setBorder(Rectangle.TOP);

        PdfPCell c2r2 = new PdfPCell(new Paragraph("Numele delegatului: " + emitor.getFirstName() + " " + emitor.getLastName(),fontbold));
        c2r2.setHorizontalAlignment(Element.ALIGN_LEFT);
        c2r2.setVerticalAlignment(Element.ALIGN_MIDDLE);
        c2r2.setBorder(Rectangle.NO_BORDER);

        PdfPCell c2r3 = new PdfPCell(new Paragraph("Buletinul de identitate:",fontbold));
        c2r3.setHorizontalAlignment(Element.ALIGN_LEFT);
        c2r3.setVerticalAlignment(Element.ALIGN_MIDDLE);
        c2r3.setBorder(Rectangle.NO_BORDER);

        PdfPCell c2r4 = new PdfPCell(new Paragraph("seria " + emitor.getSeria() +", nr " + emitor.getNr() + " eliberat(a)" + emitor.getCiEmitor(),fontbold));
        c2r4.setHorizontalAlignment(Element.ALIGN_LEFT);
        c2r4.setVerticalAlignment(Element.ALIGN_MIDDLE);
        c2r4.setBorder(Rectangle.NO_BORDER);

        PdfPCell c2r5 = new PdfPCell(new Paragraph("Mijlocul de transport: " + car,fontbold));
        c2r5.setHorizontalAlignment(Element.ALIGN_LEFT);
        c2r5.setVerticalAlignment(Element.ALIGN_MIDDLE);
        c2r5.setBorder(Rectangle.NO_BORDER);

        PdfPCell c2r6 = new PdfPCell(new Paragraph("Expedierea s-a facut in prezenta noastra la:",fontbold));
        c2r6.setHorizontalAlignment(Element.ALIGN_LEFT);
        c2r6.setVerticalAlignment(Element.ALIGN_MIDDLE);
        c2r6.setBorder(Rectangle.NO_BORDER);

        PdfPCell c2r7 = new PdfPCell(new Paragraph("Data de............... ora .......",fontbold));
        c2r7.setHorizontalAlignment(Element.ALIGN_LEFT);
        c2r7.setVerticalAlignment(Element.ALIGN_MIDDLE);
        c2r7.setBorder(Rectangle.BOTTOM);

        PdfPCell c3r1 = new PdfPCell(new Paragraph("Total: ",fontbold));
        c3r1.setHorizontalAlignment(Element.ALIGN_LEFT);
        c3r1.setVerticalAlignment(Element.ALIGN_MIDDLE);
        c3r1.setBorder(Rectangle.TOP | Rectangle.LEFT);


        PdfPCell c3r2 = new PdfPCell(new Paragraph("din care accize",fontbold));
        c3r2.setHorizontalAlignment(Element.ALIGN_LEFT);
        c3r2.setVerticalAlignment(Element.ALIGN_MIDDLE);
        c3r2.setBorder(Rectangle.BOTTOM);
        c3r2.setBorder(Rectangle.LEFT);

        PdfPCell c3r3 = new PdfPCell(new Paragraph("Semnatura de primire",fontbold));
        c3r3.setHorizontalAlignment(Element.ALIGN_LEFT);
        c3r3.setVerticalAlignment(Element.ALIGN_TOP);
        c3r3.setRowspan(5);

        PdfPCell c4r1 = new PdfPCell(new Paragraph((totalValue.toString()),fontbold));
        c4r1.setHorizontalAlignment(Element.ALIGN_MIDDLE);
        c4r1.setVerticalAlignment(Element.ALIGN_MIDDLE);

        PdfPCell c4r2 = new PdfPCell(new Paragraph("0.000",fontbold));
        c4r2.setHorizontalAlignment(Element.ALIGN_MIDDLE);
        c4r2.setVerticalAlignment(Element.ALIGN_MIDDLE);

        PdfPCell c4r3 = new PdfPCell(new Paragraph("Total de plata\n" +(totalValue.add(totalTvaValue).toString()),FontFactory.getFont("defaultEncoding", 10, Font.BOLD)));
        c4r3.setHorizontalAlignment(Element.ALIGN_MIDDLE);
        c4r3.setVerticalAlignment(Element.ALIGN_MIDDLE);
        c4r3.setColspan(2);
        c4r3.setRowspan(5);
        c4r3.setPaddingLeft(30);

        PdfPCell c5r1 = new PdfPCell(new Paragraph((totalTvaValue.toString()),fontbold));
        c5r1.setHorizontalAlignment(Element.ALIGN_MIDDLE);
        c5r1.setVerticalAlignment(Element.ALIGN_MIDDLE);
        c5r1.setPaddingLeft(5);

        PdfPCell c5r2 = new PdfPCell(new Paragraph("x"));
        c5r2.setHorizontalAlignment(Element.ALIGN_MIDDLE);
        c5r2.setPaddingLeft(33);
        c5r2.setVerticalAlignment(Element.ALIGN_MIDDLE);



        tableInfo.addCell(furnizorCell);
        tableInfo.addCell(c2r1);
        tableInfo.addCell(c3r1);
        tableInfo.addCell(c4r1);
        tableInfo.addCell(c5r1);
        tableInfo.addCell(c2r2);
        tableInfo.addCell(c3r2);
        tableInfo.addCell(c4r2);
        tableInfo.addCell(c5r2);
        tableInfo.addCell(c2r3);
        tableInfo.addCell(c3r3);
        tableInfo.addCell(c4r3);
        tableInfo.addCell(c2r4);
        tableInfo.addCell(c2r5);
        tableInfo.addCell(c2r6);
        tableInfo.addCell(c2r7);

        writeFooterTable(writer, document, tableInfo);
    }

    private void writeFooterTable(PdfWriter writer, Document document, PdfPTable table) {
        final int FIRST_ROW = 0;
        final int LAST_ROW = -1;
        // Table must have absolute width set.
        if (table.getTotalWidth() == 0)
            table.setTotalWidth((document.right() - document.left()) * table.getWidthPercentage() / 100f);
        table.writeSelectedRows(FIRST_ROW, LAST_ROW, document.left(), document.bottom() + table.getTotalHeight(),
                writer.getDirectContent());
    }

    private void createObservatii(String format) throws DocumentException {
        // TODO Auto-generated method stub
        Font fontbold = FontFactory.getFont("defaultEncoding", 14, Font.NORMAL);

        Paragraph p = new Paragraph("Observatii - ", fontbold);
        if(format == "a4"){
            p.setSpacingBefore(15);
            p.setSpacingAfter(15);
        }
        p.setIndentationLeft(20);

        document.add(p);

    }

    private void createCotaTva(TVA tva) throws DocumentException {
        Font fontbold = FontFactory.getFont("defaultEncoding", 10, Font.NORMAL);
        Paragraph p = new Paragraph("Cota TVA: "+ tva.getValue().toString() + "%",fontbold);
        //p.setSpacingBefore(10f);
        //p.setSpacingAfter(15f);
        p.setIndentationLeft(10f);

        document.add(p);

    }

    private void createInfoData(SellerInterface seller, ClientInterface client) throws DocumentException {
        // TODO Auto-generated method stub

        Font fontbold = FontFactory.getFont("defaultEncoding", 10, Font.BOLD);
        Font fontnormal = FontFactory.getFont("defaultEncoding", 10, Font.NORMAL);
        PdfPTable tableInfo = new PdfPTable(3); // 3 columns.
        tableInfo.setWidthPercentage(100); // Width 100%
        tableInfo.setSpacingBefore(10f); // Space before table
        tableInfo.setSpacingAfter(10f); // Space after table
        float[] widths = { 2f, 1.2f, 2f };
        tableInfo.setWidths(widths);


        PdfPCell furnizorCell = new PdfPCell(createInfoPhrase("Furnizor: ",seller.getName()));
        furnizorCell.setPaddingLeft(10);
        furnizorCell.setHorizontalAlignment(Element.ALIGN_LEFT);
        furnizorCell.setVerticalAlignment(Element.ALIGN_MIDDLE);
        furnizorCell.setBorder(Rectangle.NO_BORDER);

        PdfPCell cuiCell = new PdfPCell(createInfoPhrase("C.I.F/CUI : " , seller.getCui()));
        cuiCell.setPaddingLeft(10);
        cuiCell.setHorizontalAlignment(Element.ALIGN_LEFT);
        cuiCell.setVerticalAlignment(Element.ALIGN_MIDDLE);
        cuiCell.setBorder(Rectangle.NO_BORDER);

        PdfPCell nrOrdCell = new PdfPCell(createInfoPhrase("Nr. Ord. Reg. Com./an : " , seller.getNr()));
        nrOrdCell.setPaddingLeft(10);
        nrOrdCell.setHorizontalAlignment(Element.ALIGN_LEFT);
        nrOrdCell.setVerticalAlignment(Element.ALIGN_MIDDLE);
        nrOrdCell.setBorder(Rectangle.NO_BORDER);

        PdfPCell sediuCell = new PdfPCell(createInfoPhrase("Sediu : " , seller.getSediu()));
        sediuCell.setPaddingLeft(10);
        sediuCell.setHorizontalAlignment(Element.ALIGN_LEFT);
        sediuCell.setVerticalAlignment(Element.ALIGN_MIDDLE);
        sediuCell.setBorder(Rectangle.NO_BORDER);

        PdfPCell capitalCell = new PdfPCell(createInfoPhrase("Capital social : " , seller.getCapital()));
        capitalCell.setPaddingLeft(10);
        capitalCell.setHorizontalAlignment(Element.ALIGN_LEFT);
        capitalCell.setVerticalAlignment(Element.ALIGN_MIDDLE);
        capitalCell.setBorder(Rectangle.NO_BORDER);

        PdfPCell judetCell = new PdfPCell(createInfoPhrase("Judet : " , seller.getJudet()));
        judetCell.setPaddingLeft(10);
        judetCell.setHorizontalAlignment(Element.ALIGN_LEFT);
        judetCell.setVerticalAlignment(Element.ALIGN_MIDDLE);
        judetCell.setBorder(Rectangle.NO_BORDER);

        PdfPCell contCell = new PdfPCell(createInfoPhrase("Contul : " , seller.getCont()));
        contCell.setPaddingLeft(10);
        contCell.setHorizontalAlignment(Element.ALIGN_LEFT);
        contCell.setVerticalAlignment(Element.ALIGN_MIDDLE);
        contCell.setBorder(Rectangle.NO_BORDER);

        PdfPCell bancaCell = new PdfPCell(createInfoPhrase("Banca : " , seller.getBanca()));
        bancaCell.setPaddingLeft(10);
        bancaCell.setHorizontalAlignment(Element.ALIGN_LEFT);
        bancaCell.setVerticalAlignment(Element.ALIGN_MIDDLE);
        bancaCell.setBorder(Rectangle.NO_BORDER);

        // cumparator

        PdfPCell furnizorCellC = new PdfPCell(createInfoPhrase("Cumparator : ", client.getName()));
        furnizorCellC.setPaddingLeft(10);
        furnizorCellC.setHorizontalAlignment(Element.ALIGN_LEFT);
        furnizorCellC.setVerticalAlignment(Element.ALIGN_MIDDLE);
        furnizorCellC.setBorder(Rectangle.NO_BORDER);

        PdfPCell cuiCellC = new PdfPCell(createInfoPhrase("C.I.F/CUI : ", client.getCui()));
        cuiCellC.setPaddingLeft(10);
        cuiCellC.setHorizontalAlignment(Element.ALIGN_LEFT);
        cuiCellC.setVerticalAlignment(Element.ALIGN_MIDDLE);
        cuiCellC.setBorder(Rectangle.NO_BORDER);

        PdfPCell nrOrdCellC = new PdfPCell(createInfoPhrase("Nr. Ord. Reg. Com./an : ", client.getNr()));
        nrOrdCellC.setPaddingLeft(10);
        nrOrdCellC.setHorizontalAlignment(Element.ALIGN_LEFT);
        nrOrdCellC.setVerticalAlignment(Element.ALIGN_MIDDLE);
        nrOrdCellC.setBorder(Rectangle.NO_BORDER);

        PdfPCell sediuCellC = new PdfPCell(createInfoPhrase("Sediu : ", client.getSediu()));
        sediuCellC.setPaddingLeft(10);
        sediuCellC.setHorizontalAlignment(Element.ALIGN_LEFT);
        sediuCellC.setVerticalAlignment(Element.ALIGN_MIDDLE);
        sediuCellC.setBorder(Rectangle.NO_BORDER);

        PdfPCell capitalCellC = new PdfPCell(createInfoPhrase("Capital social : ", "200"));
        capitalCellC.setPaddingLeft(10);
        capitalCellC.setHorizontalAlignment(Element.ALIGN_LEFT);
        capitalCellC.setVerticalAlignment(Element.ALIGN_MIDDLE);
        capitalCellC.setBorder(Rectangle.NO_BORDER);

        PdfPCell judetCellC = new PdfPCell(createInfoPhrase("Judet : ", client.getJudet()));
        judetCellC.setPaddingLeft(10);
        judetCellC.setHorizontalAlignment(Element.ALIGN_LEFT);
        judetCellC.setVerticalAlignment(Element.ALIGN_MIDDLE);
        judetCellC.setBorder(Rectangle.NO_BORDER);

        PdfPCell contCellC = new PdfPCell(createInfoPhrase("Contul : ", client.getCont()));
        contCellC.setPaddingLeft(10);
        contCellC.setHorizontalAlignment(Element.ALIGN_LEFT);
        contCellC.setVerticalAlignment(Element.ALIGN_MIDDLE);
        contCellC.setBorder(Rectangle.NO_BORDER);

        PdfPCell bancaCellC = new PdfPCell(createInfoPhrase("Banca : ", client.getBanca()));
        bancaCellC.setPaddingLeft(10);
        bancaCellC.setHorizontalAlignment(Element.ALIGN_LEFT);
        bancaCellC.setVerticalAlignment(Element.ALIGN_MIDDLE);
        bancaCellC.setBorder(Rectangle.NO_BORDER);

        // titlu
        PdfPCell empty = new PdfPCell(new Paragraph(""));
        empty.setBorder(0);
        Paragraph title = new Paragraph(50);
        title.add(new Chunk("FACTURA FISCALA" + Chunk.NEWLINE,fontbold));

        Paragraph serie = new Paragraph();
        serie.add(new Chunk("Serie/Numar: .................." + Chunk.NEWLINE,fontnormal));
        PdfPCell seriecell = new PdfPCell(serie);
        seriecell.setBorder(0);;

        Paragraph data = new Paragraph();
        data.add(new Chunk("Data: ............................." + Chunk.NEWLINE,fontnormal));
        PdfPCell datacell = new PdfPCell(data);
        datacell.setBorder(0);

        Paragraph nraviz = new Paragraph();
        nraviz.add(new Chunk("Nr. aviz insot. marfa: " + Chunk.NEWLINE,fontnormal));
        PdfPCell nravizcell = new PdfPCell(nraviz);
        nravizcell.setBorder(0);
        //title.setLeading(150,0);
        PdfPCell titlu = new PdfPCell(title);



        titlu.setHorizontalAlignment(Element.ALIGN_LEFT);
        titlu.setVerticalAlignment(Element.ALIGN_MIDDLE);
        titlu.setBorder(Rectangle.NO_BORDER);
        titlu.setRowspan(1);

        tableInfo.addCell(furnizorCell);
        tableInfo.addCell(empty);
        tableInfo.addCell(furnizorCellC);
        tableInfo.addCell(cuiCell);
        tableInfo.addCell(empty);
        tableInfo.addCell(cuiCellC);
        tableInfo.addCell(nrOrdCell);
        tableInfo.addCell(titlu);
        tableInfo.addCell(nrOrdCellC);
        tableInfo.addCell(sediuCell);
        tableInfo.addCell(seriecell);
        tableInfo.addCell(sediuCellC);
        tableInfo.addCell(capitalCell);
        tableInfo.addCell(datacell);
        tableInfo.addCell(capitalCellC);
        tableInfo.addCell(judetCell);
        tableInfo.addCell(nravizcell);
        tableInfo.addCell(judetCellC);
        tableInfo.addCell(contCell);
        tableInfo.addCell(empty);
        tableInfo.addCell(contCellC);
        tableInfo.addCell(bancaCell);
        tableInfo.addCell(empty);
        tableInfo.addCell(bancaCellC);

        document.add(tableInfo);

    }


    private void createTableContent(ArrayList<TableItem> productsList) {
        // TODO Auto-generated method stub
        Font fontbold = FontFactory.getFont("defaultEncoding", 10, Font.NORMAL);
        Integer nrCrt = new Integer(1);
        TableItem discount = productsList.get(productsList.size() - 1);
        productsList.remove(productsList.size()-1);
        for (TableItem product : productsList) {
            PdfPCell cell0 = new PdfPCell(new Paragraph(nrCrt.toString(),fontbold));
            cell0.setPaddingLeft(10);
            cell0.setHorizontalAlignment(Element.ALIGN_CENTER);
            cell0.setVerticalAlignment(Element.ALIGN_MIDDLE);
            nrCrt++;

            PdfPCell cell1 = new PdfPCell(new Paragraph(product.getName(),fontbold));
            cell1.setPaddingLeft(10);
            cell1.setHorizontalAlignment(Element.ALIGN_CENTER);
            cell1.setVerticalAlignment(Element.ALIGN_MIDDLE);

            PdfPCell cell2 = new PdfPCell(new Paragraph(product.getUm(),fontbold));
            cell2.setPaddingLeft(10);
            cell2.setHorizontalAlignment(Element.ALIGN_CENTER);
            cell2.setVerticalAlignment(Element.ALIGN_MIDDLE);

            PdfPCell cell3 = new PdfPCell(new Paragraph(new Integer(product.getQuantity()).toString(),fontbold));
            cell3.setPaddingLeft(10);
            cell3.setHorizontalAlignment(Element.ALIGN_CENTER);
            cell3.setVerticalAlignment(Element.ALIGN_MIDDLE);

            PdfPCell cell4 = new PdfPCell(new Paragraph(product.getPrice().toString(),fontbold));
            cell4.setPaddingLeft(10);
            cell4.setHorizontalAlignment(Element.ALIGN_CENTER);
            cell4.setVerticalAlignment(Element.ALIGN_MIDDLE);

            PdfPCell cell5 = new PdfPCell(new Paragraph(product.getValue().toString(),fontbold));
            cell5.setPaddingLeft(10);
            cell5.setHorizontalAlignment(Element.ALIGN_CENTER);
            cell5.setVerticalAlignment(Element.ALIGN_MIDDLE);

            PdfPCell cell6 = new PdfPCell(new Paragraph(product.getTvaValue().toString(),fontbold));
            cell6.setPaddingLeft(10);
            cell6.setHorizontalAlignment(Element.ALIGN_CENTER);
            cell6.setVerticalAlignment(Element.ALIGN_MIDDLE);

            table.addCell(cell0);
            table.addCell(cell1);
            table.addCell(cell2);
            table.addCell(cell3);
            table.addCell(cell4);
            table.addCell(cell5);
            table.addCell(cell6);
        }
        PdfPCell cell0 = new PdfPCell(new Paragraph(nrCrt.toString(),fontbold));
        cell0.setPaddingLeft(10);
        cell0.setHorizontalAlignment(Element.ALIGN_CENTER);
        cell0.setVerticalAlignment(Element.ALIGN_MIDDLE);
        nrCrt++;

        PdfPCell cell1 = new PdfPCell(new Paragraph(discount.getName(),fontbold));
        cell1.setPaddingLeft(10);
        cell1.setHorizontalAlignment(Element.ALIGN_CENTER);
        cell1.setVerticalAlignment(Element.ALIGN_MIDDLE);

        PdfPCell cell2 = new PdfPCell(new Paragraph(discount.getUm(),fontbold));
        cell2.setPaddingLeft(10);
        cell2.setHorizontalAlignment(Element.ALIGN_CENTER);
        cell2.setVerticalAlignment(Element.ALIGN_MIDDLE);

        PdfPCell cell3 = new PdfPCell(new Paragraph(new Integer(discount.getQuantity()).toString(),fontbold));
        cell3.setPaddingLeft(10);
        cell3.setHorizontalAlignment(Element.ALIGN_CENTER);
        cell3.setVerticalAlignment(Element.ALIGN_MIDDLE);

        PdfPCell cell4 = new PdfPCell(new Paragraph("",fontbold));
        cell4.setPaddingLeft(10);
        cell4.setHorizontalAlignment(Element.ALIGN_CENTER);
        cell4.setVerticalAlignment(Element.ALIGN_MIDDLE);

        PdfPCell cell5 = new PdfPCell(new Paragraph(discount.getValue().toString(),fontbold));
        cell5.setPaddingLeft(10);
        cell5.setHorizontalAlignment(Element.ALIGN_CENTER);
        cell5.setVerticalAlignment(Element.ALIGN_MIDDLE);

        PdfPCell cell6 = new PdfPCell(new Paragraph(discount.getTvaValue().toString(),fontbold));
        cell6.setPaddingLeft(10);
        cell6.setHorizontalAlignment(Element.ALIGN_CENTER);
        cell6.setVerticalAlignment(Element.ALIGN_MIDDLE);

        table.addCell(cell0);
        table.addCell(cell1);
        table.addCell(cell2);
        table.addCell(cell3);
        table.addCell(cell4);
        table.addCell(cell5);
        table.addCell(cell6);
    }

    private void createFirstRow() throws DocumentException {
        // TODO Auto-generated method stub
        table.setWidths(columnWidths);

        PdfPCell cell0 = new PdfPCell(new Paragraph("Nr. crt.",FontFactory.getFont("defaultEncoding", 12, Font.NORMAL)));
        cell0.setPaddingLeft(10);
        cell0.setHorizontalAlignment(Element.ALIGN_CENTER);
        cell0.setVerticalAlignment(Element.ALIGN_MIDDLE);

        PdfPCell cell1 = new PdfPCell(new Paragraph("Produs"));
        cell1.setPaddingLeft(10);
        cell1.setHorizontalAlignment(Element.ALIGN_CENTER);
        cell1.setVerticalAlignment(Element.ALIGN_MIDDLE);

        PdfPCell cell2 = new PdfPCell(new Paragraph("UM"));
        cell2.setPaddingLeft(10);
        cell2.setHorizontalAlignment(Element.ALIGN_CENTER);
        cell2.setVerticalAlignment(Element.ALIGN_MIDDLE);

        PdfPCell cell3 = new PdfPCell(new Paragraph("Cantitate"));
        cell3.setPaddingLeft(10);
        cell3.setHorizontalAlignment(Element.ALIGN_CENTER);
        cell3.setVerticalAlignment(Element.ALIGN_MIDDLE);

        PdfPCell cell4 = new PdfPCell(new Paragraph("Pret unitar"));
        cell4.setPaddingLeft(10);
        cell4.setHorizontalAlignment(Element.ALIGN_CENTER);
        cell4.setVerticalAlignment(Element.ALIGN_MIDDLE);

        PdfPCell cell5 = new PdfPCell(new Paragraph("Valoare"));
        cell5.setPaddingLeft(10);
        cell5.setHorizontalAlignment(Element.ALIGN_CENTER);
        cell5.setVerticalAlignment(Element.ALIGN_MIDDLE);

        PdfPCell cell6 = new PdfPCell(new Paragraph("TVA"));
        cell6.setPaddingLeft(10);
        cell6.setHorizontalAlignment(Element.ALIGN_CENTER);
        cell6.setVerticalAlignment(Element.ALIGN_MIDDLE);

        table.addCell(cell0);
        table.addCell(cell1);
        table.addCell(cell2);
        table.addCell(cell3);
        table.addCell(cell4);
        table.addCell(cell5);
        table.addCell(cell6);

    }

    private Phrase createInfoPhrase(String camp,String valoare){
        Font fontbold = FontFactory.getFont("defaultEncoding", 10, Font.BOLD);
        Font fontnormal = FontFactory.getFont("defaultEncoding", 10, Font.NORMAL);
        Phrase phrase = new Phrase();
        phrase.add(
                new Chunk(camp,  fontbold)
        );
        phrase.add(new Chunk(valoare, fontnormal));
        return phrase;
    }

}
