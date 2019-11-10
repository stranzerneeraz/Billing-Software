package sample;

import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.controlsfx.control.textfield.TextFields;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.DecimalFormat;
import java.time.LocalDate;
import java.util.ArrayList;

public class BillController {
    @FXML
    private TextField nameField;
    @FXML
    private Label dateLabel;
    @FXML
    private TextField addressField;
    @FXML
    private TextField contactNumberField;
    @FXML
    private TextField particularField;
    @FXML
    private TextField quantityField;
    @FXML
    private TableView<ProductModel> billItemsTableView;
    @FXML
    private TableColumn<ProductModel, Integer> serialNumber;
    @FXML
    private Label grossTotalLabel;
    @FXML
    private TextField discountField;
    @FXML
    private CheckBox vatCheckBox;
    @FXML
    private Label vatLabel;
    @FXML
    private Label totalLabel;
    @FXML
    private TextField cashField;
    @FXML
    private Label refundLabel;

    private ArrayList<String> particularString = new ArrayList<>();
    private ArrayList<String> productList = new ArrayList<>();

    private ObservableList<ProductModel> productModelObservableList = FXCollections.observableArrayList();
    private static DecimalFormat df2 = new DecimalFormat("#.##");
    private ProductModel productModel;
    private double grossTotal = 0.0;
    private int vat;
    private int discount;
    private double priceAfterDiscount;
    private double cash;

    public void initialize() throws IOException {
        String FILE_NAME = "./items.xlsx";
        File file = new File(FILE_NAME);
        FileInputStream excelInputFile = new FileInputStream(file);
        Workbook workbook = WorkbookFactory.create(excelInputFile);
        Sheet dataTypeSheet = workbook.getSheet("sheet1");
        int numberOfRows = dataTypeSheet.getLastRowNum();
        if (file.isFile() && file.exists()) {
            dateLabel.setText(LocalDate.now().toString());
            System.out.println("file open successfully.");
            for (int iteration = 1; iteration <= numberOfRows; iteration++) {
                productModel = new ProductModel();
                productModel.setProductID(iteration);
                productModel.setUnitPrice(dataTypeSheet.getRow(iteration).getCell(2).toString());
                productModel.setParticular(dataTypeSheet.getRow(iteration).getCell(1).toString());
                particularString.add(productModel.getParticular() + "," + productModel.getUnitPrice());
            }
        } else {
            System.out.println("Error to open file.");
        }
        TextFields.bindAutoCompletion(particularField, particularString);
        serialNumber.setCellValueFactory(cellData -> new ReadOnlyObjectWrapper<>(billItemsTableView.getItems().indexOf(cellData.getValue()) + 1));
        billItemsTableView.setItems(productModelObservableList);
    }

    public void addNewItem() {
        String quantity = quantityField.getText();
        double total;
        if ((particularField.getText().length() > 0) && (quantity.matches("^[0-9]+(\\.[0-9]+)?$"))) {
            if (particularString.contains(particularField.getText())) {
                String[] splitData = particularField.getText().split(",");
                productList.add(splitData[0] + " - " + quantity);
                total = Double.parseDouble(quantity) * Double.parseDouble(splitData[1]);
                productModel = new ProductModel(0, splitData[0], splitData[1], quantity, total + "");
                productModelObservableList.add(productModel);
                billCalculation(total);
            } else {
                System.out.println("item is not in list");
            }
        } else {
            System.out.println("condition not matched");
        }
        particularField.setText("");
        quantityField.setText("");
        particularField.requestFocus();
    }

    private void billCalculation(double total) {
        grossTotal += total;
        grossTotalLabel.setText(String.valueOf(df2.format(grossTotal)));
        discountField.textProperty().addListener(((observable, oldValue, newValue) -> {
            if (newValue.equals("")) {
                discount = 0;
            } else {
                discount = Integer.parseInt(newValue);
                grandTotalCalculation();
            }
        }));
        vatCheckBox.selectedProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue) {
                vat = 13;
            } else {
                vat = 0;
            }
            grandTotalCalculation();
        });
        cashField.textProperty().addListener((((observable, oldValue, newValue) -> {
            if (newValue.equals("")) {
                cash = 0;
            } else {
                cash = Double.parseDouble(newValue);
                grandTotalCalculation();
            }
        })));
    }

    private void grandTotalCalculation() {
        double vatAmount;
        double grandTotal;
        if (discount != 0) {
            priceAfterDiscount = (100 - discount) * 0.01 * grossTotal;
            if (vat == 0) {
                totalLabel.setText(String.valueOf(df2.format(priceAfterDiscount)));
            } else {
                vatAmount = 13 * 0.01 * priceAfterDiscount;
                vatLabel.setText(String.valueOf(df2.format(vatAmount)));
                grandTotal = vatAmount + priceAfterDiscount;
                totalLabel.setText(String.valueOf(df2.format(grandTotal)));
            }
        }
        if (cash != 0) {
            grandTotal = Double.parseDouble(totalLabel.getText());
            refundLabel.setText(String.valueOf(df2.format(cash - grandTotal)));
        }
    }

    public void printBill() {
        String name = nameField.getText();
        LocalDate date = LocalDate.now();
        String address = addressField.getText();
        String contactNumber = contactNumberField.getText();
        String grossTotal = grossTotalLabel.getText();
        double discount = (Double.parseDouble(grossTotal) - priceAfterDiscount);
        String vatAmount = vatLabel.getText();
        String total = totalLabel.getText();
        try {
            FileInputStream fileInput = new FileInputStream(new File("./bills.xlsx"));
            XSSFWorkbook workbook = new XSSFWorkbook(fileInput);
            XSSFSheet sheet = workbook.getSheetAt(0);
            XSSFRow newRow = sheet.createRow(sheet.getLastRowNum() + 1);
            if ((name.equals("")) && (address.equals("")) && (contactNumber.equals(""))) {
                newRow.createCell(0).setCellValue("" + date);
                newRow.createCell(4).setCellValue(productList.toString());
                newRow.createCell(5).setCellValue(grossTotal);
                newRow.createCell(6).setCellValue(discount);
                newRow.createCell(7).setCellValue(vatAmount);
                newRow.createCell(8).setCellValue(total);
            } else {
                newRow.createCell(0).setCellValue("" + date);
                newRow.createCell(1).setCellValue(name);
                newRow.createCell(2).setCellValue(address);
                newRow.createCell(3).setCellValue(contactNumber);
                newRow.createCell(4).setCellValue(productList.toString());
                newRow.createCell(5).setCellValue(grossTotal);
                newRow.createCell(6).setCellValue(discount);
                newRow.createCell(7).setCellValue(vatAmount);
                newRow.createCell(8).setCellValue(total);
            }
            fileInput.close();
            FileOutputStream outFile = new FileOutputStream(new File("./bills.xlsx"));
            workbook.write(outFile);
            outFile.close();
            workbook.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        flushData();
    }

    private void flushData() {
        nameField.setText("");
        addressField.setText("");
        contactNumberField.setText("");
        billItemsTableView.getItems().clear();
        grossTotalLabel.setText("");
        discountField.setText("");
        vatLabel.setText("");
        cashField.setText("");
        refundLabel.setText("");
    }
}
