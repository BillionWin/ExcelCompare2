/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ExcelCompare2;

/**
 *
 * @author james.macadie
 */
public interface ISpreadSheet {
    
    boolean hasNext();
    void next();
    String getSheetName();
    CondensedFormulae getCondensedFormulae();
    
}
