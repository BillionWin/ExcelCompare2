/* MIT License
 *
 * Copyright (c) 2016 James MacAdie
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 * 
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 * 
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */
package ExcelCompare2;

/**
 *
 * @author james.macadie
 */
public class AnalysedFormula extends UniqueFormula {
    
    private boolean _analysed;
    private CompoundRange _unanalysedRange;
    
    public AnalysedFormula(Formula f) {
        // Construct the superclass
        super(f);
        _analysed = false;
        _unanalysedRange = super.getRange().getCopy();
    }
    
    @Override
    public void addCell(CellRef newCell) {
        if (!super.getRange().contains(newCell)) {
            super.addCell(newCell);
            _unanalysedRange.addCell(newCell);
            _analysed = false;
        }
    }
    
    public void setAnalysed(CellRef analysed) {
        _unanalysedRange.removeCell(analysed);
        if (_unanalysedRange.isEmpty())
            _analysed = true;
    }
    
    public void setAnalysed(CompoundRange analysed) {
        _unanalysedRange = _unanalysedRange.missingFrom(analysed);
        if (_unanalysedRange.isEmpty())
            _analysed = true;
    }
    
    public boolean isAnalysed() {
        return _analysed;
    }
    
    public boolean isAnalysed(CellRef cell) {
        return !_unanalysedRange.contains(cell);
    }
    
    public CompoundRange getUnanalysed() {
        return _unanalysedRange;
    }
    
    public UniqueFormula getUniqueFormula() {
        // TODO: clones but might be a more effienct way to do this
        return new UniqueFormula(super.getFormula(), super.getRange());
    }
    
}
