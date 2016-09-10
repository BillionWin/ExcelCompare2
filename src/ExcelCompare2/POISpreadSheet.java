/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ExcelCompare2;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import org.apache.poi.EncryptedDocumentException;
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;

/**
 *
 * @author james.macadie
 */
public class POISpreadSheet implements ISpreadSheet {
    
    private final Workbook _wb;
    private final Iterator<POISheet> _iter;
    private POISheet _sheet;
    
    public POISpreadSheet (String fName) throws Exception  {
        try {
            _wb = loadSpreadSheet(fName);
            _iter = getSheetIterator();
            _sheet = _iter.next();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
    
    private static Workbook loadSpreadSheet(String file) throws Exception {
        // assume file is excel by default
        Exception readException;
        try {
            Workbook workbook = WorkbookFactory.create(new File(file));
            return workbook;
        } catch (IOException | InvalidFormatException | EncryptedDocumentException e) {
            readException = e;
        }
        throw new RuntimeException("Failed to read as excel file: " + file, readException);
    }
    
    @Override
    public boolean hasNext() {
        return _iter.hasNext();
    }
    
    @Override
    public void next() {
        if (_iter.hasNext())
            _sheet = _iter.next();
    }
    
    @Override
    public String getSheetName() {
        return _sheet.getName();
    }
    
    @Override
    public CondensedFormulae getCondensedFormulae() {
        Iterator<POIRow> iterRow = _sheet.getRowIterator();
        Iterator<POICell> iterCell;
        List<Formula> f = new ArrayList<> ();
        // Loop through all rows
        while (iterRow.hasNext()) {
            // Loop through all cells in the row
            iterCell = iterRow.next().getCellIterator();
            while (iterCell.hasNext()) {
                // Add each formula to the array list
                f.add(iterCell.next().getFormula());
            }
        }
        // Construct the Condensed Formulae object from the array of Formulae
        // on the sheet
        return new CondensedFormulae(f);
    }
    
    private Iterator<POISheet> getSheetIterator() {
        return new Iterator<POISheet>() {

            private int currSheetIdx = 0;

            @Override
            public boolean hasNext() {
                return currSheetIdx < _wb.getNumberOfSheets();
            }

            @Override
            public POISheet next() {
                Sheet sheet = _wb.getSheetAt(currSheetIdx);
                POISheet poiSheet = new POISheet(sheet, currSheetIdx);
                currSheetIdx++;
                return poiSheet;
            }

            @Override
            public void remove() {
                throw new UnsupportedOperationException();
            }
        };
    }
    
    class POISheet {

        private final Sheet sheet;
        private final int sheetIdx;

        public POISheet(Sheet sheet, int sheetIdx) {
            this.sheet = sheet;
            this.sheetIdx = sheetIdx;
        }

        public String getName() {
            return sheet.getSheetName();
        }

        public int getSheetIndex() {
            return sheetIdx;
        }

        public Iterator<POIRow> getRowIterator() {
            final Iterator<Row> rowIterator = sheet.rowIterator();
            return new Iterator<POIRow>() {

                @Override
                public boolean hasNext() {
                    return rowIterator.hasNext();
                }

                @Override
                public POIRow next() {
                    return new POIRow(rowIterator.next());
                }

                @Override
                public void remove() {
                    throw new UnsupportedOperationException();
                }
            };
        }
    }

    class POIRow {

        private final Row _row;

        public POIRow(Row row) {
            this._row = row;
        }

        public int getRowIndex() {
            return _row.getRowNum();
        }

        public Iterator<POICell> getCellIterator() {
            final Iterator<Cell> cellIterator = _row.cellIterator();
            return new Iterator<POICell>() {

                @Override
                public boolean hasNext() {
                    return cellIterator.hasNext();
                }

                @Override
                public POICell next() {
                    return new POICell(cellIterator.next());
                }

                @Override
                public void remove() {
                    throw new UnsupportedOperationException();
                }
            };
        }
    }

    class POICell {

        private final Cell _cell;

        public POICell(Cell cell) {
            this._cell = cell;
        }

        public int getRowIndex() {
            return _cell.getRowIndex();
        }

        public int getColumnIndex() {
            return _cell.getColumnIndex();
        }

        public Formula getFormula() {
            // TODO: store dates as dates not numbers
            // might want to conside for other formatting types too e.g. pecenatges
            boolean hasFormula = false;
            String formula = null;
            String value;
            int cellType = _cell.getCellType();
            if (cellType == Cell.CELL_TYPE_FORMULA) {
                hasFormula = true;
                formula = "=" + _cell.getCellFormula();
                cellType = _cell.getCachedFormulaResultType();
            }
            switch (cellType) {
                case Cell.CELL_TYPE_NUMERIC:
                    value = String.valueOf(_cell.getNumericCellValue());
                    break;
                case Cell.CELL_TYPE_BOOLEAN:
                    value = String.valueOf(_cell.getBooleanCellValue());
                    break;
                case Cell.CELL_TYPE_ERROR:
                    value = String.valueOf(_cell.getErrorCellValue());
                    break;
                default:
                    value = _cell.getStringCellValue();
                    break;
            }
            formula = !hasFormula ? value : formula;
            // TODO: extend Formula to hold value as well
            return new Formula(formula,
                    new CellRef(_cell.getRowIndex() + 1, _cell.getColumnIndex() + 1));
        }
    }
}
