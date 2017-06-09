package test;

import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.List;

import javax.swing.table.DefaultTableModel;

import org.junit.Test;

public class TestTable {

    @Test
    public void testAddRowsToTable() throws Exception {
        DefaultTableModel model = new javax.swing.table.DefaultTableModel();    

        model.addColumn("Col1");
        model.addColumn("Col2");

        model.addRow(new Object[]{"1", "v2"});
        model.addRow(new Object[]{"2", "v2"});

        List<String> numdata = new ArrayList<String>();
        for (int count = 0; count < model.getRowCount(); count++){
              numdata.add(model.getValueAt(count, 0).toString());
        }

        System.out.println(numdata);
        assertEquals("[1, 2]", numdata.toString());
    }
}
